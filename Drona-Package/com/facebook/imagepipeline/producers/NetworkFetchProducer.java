package com.facebook.imagepipeline.producers;

import android.os.SystemClock;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.memory.Pool;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.memory.PooledByteBufferOutputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.common.BytesRange;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class NetworkFetchProducer
  implements Producer<EncodedImage>
{
  public static final String INTERMEDIATE_RESULT_PRODUCER_EVENT = "intermediate_result";
  public static final String PRODUCER_NAME = "NetworkFetchProducer";
  private static final int READ_SIZE = 16384;
  @VisibleForTesting
  static final long TIME_BETWEEN_PARTIAL_RESULTS_MS = 100L;
  private final ByteArrayPool mByteArrayPool;
  private final NetworkFetcher mNetworkFetcher;
  private final PooledByteBufferFactory mPooledByteBufferFactory;
  
  public NetworkFetchProducer(PooledByteBufferFactory paramPooledByteBufferFactory, ByteArrayPool paramByteArrayPool, NetworkFetcher paramNetworkFetcher)
  {
    mPooledByteBufferFactory = paramPooledByteBufferFactory;
    mByteArrayPool = paramByteArrayPool;
    mNetworkFetcher = paramNetworkFetcher;
  }
  
  protected static float calculateProgress(int paramInt1, int paramInt2)
  {
    if (paramInt2 > 0) {
      return paramInt1 / paramInt2;
    }
    return 1.0F - (float)Math.exp(-paramInt1 / 50000.0D);
  }
  
  private Map getExtraMap(FetchState paramFetchState, int paramInt)
  {
    if (!paramFetchState.getListener().requiresExtraMap(paramFetchState.getId())) {
      return null;
    }
    return mNetworkFetcher.getExtraMap(paramFetchState, paramInt);
  }
  
  protected static void notifyConsumer(PooledByteBufferOutputStream paramPooledByteBufferOutputStream, int paramInt, BytesRange paramBytesRange, Consumer paramConsumer)
  {
    CloseableReference localCloseableReference = CloseableReference.of(paramPooledByteBufferOutputStream.toByteBuffer());
    try
    {
      paramPooledByteBufferOutputStream = new EncodedImage(localCloseableReference);
      try
      {
        paramPooledByteBufferOutputStream.setBytesRange(paramBytesRange);
        paramPooledByteBufferOutputStream.parseMetaData();
        paramConsumer.onNewResult(paramPooledByteBufferOutputStream, paramInt);
        EncodedImage.closeSafely(paramPooledByteBufferOutputStream);
        CloseableReference.closeSafely(localCloseableReference);
        return;
      }
      catch (Throwable paramBytesRange) {}
      EncodedImage.closeSafely(paramPooledByteBufferOutputStream);
    }
    catch (Throwable paramBytesRange)
    {
      paramPooledByteBufferOutputStream = null;
    }
    CloseableReference.closeSafely(localCloseableReference);
    throw paramBytesRange;
  }
  
  private void onCancellation(FetchState paramFetchState)
  {
    paramFetchState.getListener().onProducerFinishWithCancellation(paramFetchState.getId(), "NetworkFetchProducer", null);
    paramFetchState.getConsumer().onCancellation();
  }
  
  private void onFailure(FetchState paramFetchState, Throwable paramThrowable)
  {
    paramFetchState.getListener().onProducerFinishWithFailure(paramFetchState.getId(), "NetworkFetchProducer", paramThrowable, null);
    paramFetchState.getListener().onUltimateProducerReached(paramFetchState.getId(), "NetworkFetchProducer", false);
    paramFetchState.getConsumer().onFailure(paramThrowable);
  }
  
  private boolean shouldPropagateIntermediateResults(FetchState paramFetchState)
  {
    if (!paramFetchState.getContext().isIntermediateResultExpected()) {
      return false;
    }
    return mNetworkFetcher.shouldPropagate(paramFetchState);
  }
  
  protected void handleFinalResult(PooledByteBufferOutputStream paramPooledByteBufferOutputStream, FetchState paramFetchState)
  {
    Map localMap = getExtraMap(paramFetchState, paramPooledByteBufferOutputStream.size());
    ProducerListener localProducerListener = paramFetchState.getListener();
    localProducerListener.onProducerFinishWithSuccess(paramFetchState.getId(), "NetworkFetchProducer", localMap);
    localProducerListener.onUltimateProducerReached(paramFetchState.getId(), "NetworkFetchProducer", true);
    notifyConsumer(paramPooledByteBufferOutputStream, paramFetchState.getOnNewResultStatusFlags() | 0x1, paramFetchState.getResponseBytesRange(), paramFetchState.getConsumer());
  }
  
  protected void maybeHandleIntermediateResult(PooledByteBufferOutputStream paramPooledByteBufferOutputStream, FetchState paramFetchState)
  {
    long l = SystemClock.uptimeMillis();
    if ((shouldPropagateIntermediateResults(paramFetchState)) && (l - paramFetchState.getLastIntermediateResultTimeMs() >= 100L))
    {
      paramFetchState.setLastIntermediateResultTimeMs(l);
      paramFetchState.getListener().onProducerEvent(paramFetchState.getId(), "NetworkFetchProducer", "intermediate_result");
      notifyConsumer(paramPooledByteBufferOutputStream, paramFetchState.getOnNewResultStatusFlags(), paramFetchState.getResponseBytesRange(), paramFetchState.getConsumer());
    }
  }
  
  protected void onResponse(FetchState paramFetchState, InputStream paramInputStream, int paramInt)
    throws IOException
  {
    PooledByteBufferOutputStream localPooledByteBufferOutputStream;
    if (paramInt > 0) {
      localPooledByteBufferOutputStream = mPooledByteBufferFactory.newOutputStream(paramInt);
    } else {
      localPooledByteBufferOutputStream = mPooledByteBufferFactory.newOutputStream();
    }
    byte[] arrayOfByte = (byte[])mByteArrayPool.get(16384);
    try
    {
      for (;;)
      {
        int i = paramInputStream.read(arrayOfByte);
        if (i < 0) {
          break;
        }
        if (i > 0)
        {
          localPooledByteBufferOutputStream.write(arrayOfByte, 0, i);
          maybeHandleIntermediateResult(localPooledByteBufferOutputStream, paramFetchState);
          float f = calculateProgress(localPooledByteBufferOutputStream.size(), paramInt);
          paramFetchState.getConsumer().onProgressUpdate(f);
        }
      }
      mNetworkFetcher.onFetchCompletion(paramFetchState, localPooledByteBufferOutputStream.size());
      handleFinalResult(localPooledByteBufferOutputStream, paramFetchState);
      mByteArrayPool.release(arrayOfByte);
      localPooledByteBufferOutputStream.close();
      return;
    }
    catch (Throwable paramFetchState)
    {
      mByteArrayPool.release(arrayOfByte);
      localPooledByteBufferOutputStream.close();
      throw paramFetchState;
    }
  }
  
  public void produceResults(final Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    paramProducerContext.getListener().onProducerStart(paramProducerContext.getId(), "NetworkFetchProducer");
    paramConsumer = mNetworkFetcher.createFetchState(paramConsumer, paramProducerContext);
    mNetworkFetcher.fetch(paramConsumer, new NetworkFetcher.Callback()
    {
      public void onCancellation()
      {
        NetworkFetchProducer.this.onCancellation(paramConsumer);
      }
      
      public void onFailure(Throwable paramAnonymousThrowable)
      {
        NetworkFetchProducer.this.onFailure(paramConsumer, paramAnonymousThrowable);
      }
      
      public void onResponse(InputStream paramAnonymousInputStream, int paramAnonymousInt)
        throws IOException
      {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.beginSection("NetworkFetcher->onResponse");
        }
        onResponse(paramConsumer, paramAnonymousInputStream, paramAnonymousInt);
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.endSection();
        }
      }
    });
  }
}
