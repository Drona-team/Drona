package com.facebook.imagepipeline.producers;

import android.net.Uri;
import android.net.Uri.Builder;
import bolts.Continuation;
import bolts.Task;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.memory.Pool;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.memory.PooledByteBufferOutputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.common.BytesRange;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;

public class PartialDiskCacheProducer
  implements Producer<EncodedImage>
{
  public static final String ENCODED_IMAGE_SIZE = "encodedImageSize";
  public static final String EXTRA_CACHED_VALUE_FOUND = "cached_value_found";
  public static final String PRODUCER_NAME = "PartialDiskCacheProducer";
  private final ByteArrayPool mByteArrayPool;
  private final CacheKeyFactory mCacheKeyFactory;
  private final BufferedDiskCache mDefaultBufferedDiskCache;
  private final Producer<EncodedImage> mInputProducer;
  private final PooledByteBufferFactory mPooledByteBufferFactory;
  
  public PartialDiskCacheProducer(BufferedDiskCache paramBufferedDiskCache, CacheKeyFactory paramCacheKeyFactory, PooledByteBufferFactory paramPooledByteBufferFactory, ByteArrayPool paramByteArrayPool, Producer paramProducer)
  {
    mDefaultBufferedDiskCache = paramBufferedDiskCache;
    mCacheKeyFactory = paramCacheKeyFactory;
    mPooledByteBufferFactory = paramPooledByteBufferFactory;
    mByteArrayPool = paramByteArrayPool;
    mInputProducer = paramProducer;
  }
  
  private static Uri createUriForPartialCacheKey(ImageRequest paramImageRequest)
  {
    return paramImageRequest.getSourceUri().buildUpon().appendQueryParameter("fresco_partial", "true").build();
  }
  
  static Map getExtraMap(ProducerListener paramProducerListener, String paramString, boolean paramBoolean, int paramInt)
  {
    if (!paramProducerListener.requiresExtraMap(paramString)) {
      return null;
    }
    if (paramBoolean) {
      return ImmutableMap.of("cached_value_found", String.valueOf(paramBoolean), "encodedImageSize", String.valueOf(paramInt));
    }
    return ImmutableMap.of("cached_value_found", String.valueOf(paramBoolean));
  }
  
  private static boolean isTaskCancelled(Task paramTask)
  {
    return (paramTask.isCancelled()) || ((paramTask.isFaulted()) && ((paramTask.getError() instanceof CancellationException)));
  }
  
  private Continuation onFinishDiskReads(final Consumer paramConsumer, final ProducerContext paramProducerContext, final CacheKey paramCacheKey)
  {
    final String str = paramProducerContext.getId();
    new Continuation()
    {
      public Void then(Task paramAnonymousTask)
        throws Exception
      {
        if (PartialDiskCacheProducer.isTaskCancelled(paramAnonymousTask))
        {
          val$listener.onProducerFinishWithCancellation(str, "PartialDiskCacheProducer", null);
          paramConsumer.onCancellation();
          return null;
        }
        if (paramAnonymousTask.isFaulted())
        {
          val$listener.onProducerFinishWithFailure(str, "PartialDiskCacheProducer", paramAnonymousTask.getError(), null);
          PartialDiskCacheProducer.this.startInputProducer(paramConsumer, paramProducerContext, paramCacheKey, null);
          return null;
        }
        paramAnonymousTask = (EncodedImage)paramAnonymousTask.getResult();
        if (paramAnonymousTask != null)
        {
          val$listener.onProducerFinishWithSuccess(str, "PartialDiskCacheProducer", PartialDiskCacheProducer.getExtraMap(val$listener, str, true, paramAnonymousTask.getSize()));
          Object localObject = BytesRange.toMax(paramAnonymousTask.getSize() - 1);
          paramAnonymousTask.setBytesRange((BytesRange)localObject);
          int i = paramAnonymousTask.getSize();
          ImageRequest localImageRequest = paramProducerContext.getImageRequest();
          if (((BytesRange)localObject).contains(localImageRequest.getBytesRange()))
          {
            val$listener.onUltimateProducerReached(str, "PartialDiskCacheProducer", true);
            paramConsumer.onNewResult(paramAnonymousTask, 9);
            return null;
          }
          paramConsumer.onNewResult(paramAnonymousTask, 8);
          localObject = new SettableProducerContext(ImageRequestBuilder.fromRequest(localImageRequest).setBytesRange(BytesRange.from(i - 1)).build(), paramProducerContext);
          PartialDiskCacheProducer.this.startInputProducer(paramConsumer, (ProducerContext)localObject, paramCacheKey, paramAnonymousTask);
          return null;
        }
        val$listener.onProducerFinishWithSuccess(str, "PartialDiskCacheProducer", PartialDiskCacheProducer.getExtraMap(val$listener, str, false, 0));
        PartialDiskCacheProducer.this.startInputProducer(paramConsumer, paramProducerContext, paramCacheKey, paramAnonymousTask);
        return null;
      }
    };
  }
  
  private void startInputProducer(Consumer paramConsumer, ProducerContext paramProducerContext, CacheKey paramCacheKey, EncodedImage paramEncodedImage)
  {
    paramConsumer = new PartialDiskCacheConsumer(paramConsumer, mDefaultBufferedDiskCache, paramCacheKey, mPooledByteBufferFactory, mByteArrayPool, paramEncodedImage, null);
    mInputProducer.produceResults(paramConsumer, paramProducerContext);
  }
  
  private void subscribeTaskForRequestCancellation(final AtomicBoolean paramAtomicBoolean, ProducerContext paramProducerContext)
  {
    paramProducerContext.addCallbacks(new BaseProducerContextCallbacks()
    {
      public void onCancellationRequested()
      {
        paramAtomicBoolean.set(true);
      }
    });
  }
  
  public void produceResults(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    Object localObject1 = paramProducerContext.getImageRequest();
    if (!((ImageRequest)localObject1).isDiskCacheEnabled())
    {
      mInputProducer.produceResults(paramConsumer, paramProducerContext);
      return;
    }
    paramProducerContext.getListener().onProducerStart(paramProducerContext.getId(), "PartialDiskCacheProducer");
    Object localObject2 = createUriForPartialCacheKey((ImageRequest)localObject1);
    localObject1 = mCacheKeyFactory.getEncodedCacheKey((ImageRequest)localObject1, (Uri)localObject2, paramProducerContext.getCallerContext());
    localObject2 = new AtomicBoolean(false);
    mDefaultBufferedDiskCache.addTask((CacheKey)localObject1, (AtomicBoolean)localObject2).continueWith(onFinishDiskReads(paramConsumer, paramProducerContext, (CacheKey)localObject1));
    subscribeTaskForRequestCancellation((AtomicBoolean)localObject2, paramProducerContext);
  }
  
  private static class PartialDiskCacheConsumer
    extends DelegatingConsumer<EncodedImage, EncodedImage>
  {
    private static final int READ_SIZE = 16384;
    private final ByteArrayPool mByteArrayPool;
    private final BufferedDiskCache mDefaultBufferedDiskCache;
    @Nullable
    private final EncodedImage mPartialEncodedImageFromCache;
    private final CacheKey mPartialImageCacheKey;
    private final PooledByteBufferFactory mPooledByteBufferFactory;
    
    private PartialDiskCacheConsumer(Consumer paramConsumer, BufferedDiskCache paramBufferedDiskCache, CacheKey paramCacheKey, PooledByteBufferFactory paramPooledByteBufferFactory, ByteArrayPool paramByteArrayPool, EncodedImage paramEncodedImage)
    {
      super();
      mDefaultBufferedDiskCache = paramBufferedDiskCache;
      mPartialImageCacheKey = paramCacheKey;
      mPooledByteBufferFactory = paramPooledByteBufferFactory;
      mByteArrayPool = paramByteArrayPool;
      mPartialEncodedImageFromCache = paramEncodedImage;
    }
    
    private void copy(InputStream paramInputStream, OutputStream paramOutputStream, int paramInt)
      throws IOException
    {
      byte[] arrayOfByte = (byte[])mByteArrayPool.get(16384);
      int i = paramInt;
      while (i > 0) {
        try
        {
          int j = paramInputStream.read(arrayOfByte, 0, Math.min(16384, i));
          if (j >= 0)
          {
            if (j <= 0) {
              continue;
            }
            paramOutputStream.write(arrayOfByte, 0, j);
            i -= j;
          }
        }
        catch (Throwable paramInputStream)
        {
          mByteArrayPool.release(arrayOfByte);
          throw paramInputStream;
        }
      }
      mByteArrayPool.release(arrayOfByte);
      if (i <= 0) {
        return;
      }
      throw new IOException(String.format(null, "Failed to read %d bytes - finished %d short", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i) }));
    }
    
    private PooledByteBufferOutputStream merge(EncodedImage paramEncodedImage1, EncodedImage paramEncodedImage2)
      throws IOException
    {
      int i = paramEncodedImage2.getSize();
      int j = getBytesRangefrom;
      PooledByteBufferOutputStream localPooledByteBufferOutputStream = mPooledByteBufferFactory.newOutputStream(i + j);
      i = getBytesRangefrom;
      copy(paramEncodedImage1.getInputStream(), localPooledByteBufferOutputStream, i);
      copy(paramEncodedImage2.getInputStream(), localPooledByteBufferOutputStream, paramEncodedImage2.getSize());
      return localPooledByteBufferOutputStream;
    }
    
    private void sendFinalResultToConsumer(PooledByteBufferOutputStream paramPooledByteBufferOutputStream)
    {
      CloseableReference localCloseableReference = CloseableReference.of(paramPooledByteBufferOutputStream.toByteBuffer());
      try
      {
        paramPooledByteBufferOutputStream = new EncodedImage(localCloseableReference);
        try
        {
          paramPooledByteBufferOutputStream.parseMetaData();
          getConsumer().onNewResult(paramPooledByteBufferOutputStream, 1);
          EncodedImage.closeSafely(paramPooledByteBufferOutputStream);
          CloseableReference.closeSafely(localCloseableReference);
          return;
        }
        catch (Throwable localThrowable1) {}
        EncodedImage.closeSafely(paramPooledByteBufferOutputStream);
      }
      catch (Throwable localThrowable2)
      {
        paramPooledByteBufferOutputStream = null;
      }
      CloseableReference.closeSafely(localCloseableReference);
      throw localThrowable2;
    }
    
    public void onNewResultImpl(EncodedImage paramEncodedImage, int paramInt)
    {
      if (BaseConsumer.isNotLast(paramInt)) {
        return;
      }
      if ((mPartialEncodedImageFromCache != null) && (paramEncodedImage.getBytesRange() != null))
      {
        EncodedImage localEncodedImage = mPartialEncodedImageFromCache;
        try
        {
          sendFinalResultToConsumer(merge(localEncodedImage, paramEncodedImage));
          paramEncodedImage.close();
          mPartialEncodedImageFromCache.close();
        }
        catch (Throwable localThrowable)
        {
          break label90;
        }
        catch (IOException localIOException)
        {
          for (;;)
          {
            FLog.e("PartialDiskCacheProducer", "Error while merging image data", localIOException);
            getConsumer().onFailure(localIOException);
          }
        }
        mDefaultBufferedDiskCache.remove(mPartialImageCacheKey);
        return;
        label90:
        paramEncodedImage.close();
        mPartialEncodedImageFromCache.close();
        throw localIOException;
      }
      if ((BaseConsumer.statusHasFlag(paramInt, 8)) && (BaseConsumer.isLast(paramInt)) && (paramEncodedImage.getImageFormat() != ImageFormat.UNKNOWN))
      {
        mDefaultBufferedDiskCache.startListening(mPartialImageCacheKey, paramEncodedImage);
        getConsumer().onNewResult(paramEncodedImage, paramInt);
        return;
      }
      getConsumer().onNewResult(paramEncodedImage, paramInt);
    }
  }
}
