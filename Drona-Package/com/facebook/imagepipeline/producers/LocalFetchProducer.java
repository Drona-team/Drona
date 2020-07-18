package com.facebook.imagepipeline.producers;

import com.facebook.common.executors.StatefulRunnable;
import com.facebook.common.internal.Closeables;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

public abstract class LocalFetchProducer
  implements Producer<EncodedImage>
{
  private final Executor mExecutor;
  private final PooledByteBufferFactory mPooledByteBufferFactory;
  
  protected LocalFetchProducer(Executor paramExecutor, PooledByteBufferFactory paramPooledByteBufferFactory)
  {
    mExecutor = paramExecutor;
    mPooledByteBufferFactory = paramPooledByteBufferFactory;
  }
  
  protected EncodedImage getByteBufferBackedEncodedImage(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    Object localObject = null;
    if (paramInt <= 0) {}
    CloseableReference localCloseableReference2;
    for (;;)
    {
      try
      {
        CloseableReference localCloseableReference1 = CloseableReference.of(mPooledByteBufferFactory.newByteBuffer(paramInputStream));
        localObject = localCloseableReference1;
      }
      catch (Throwable localThrowable)
      {
        break label70;
      }
      localCloseableReference2 = CloseableReference.of(mPooledByteBufferFactory.newByteBuffer(paramInputStream, paramInt));
    }
    EncodedImage localEncodedImage = new EncodedImage(localCloseableReference2);
    Closeables.closeQuietly(paramInputStream);
    CloseableReference.closeSafely(localCloseableReference2);
    return localEncodedImage;
    label70:
    Closeables.closeQuietly(paramInputStream);
    CloseableReference.closeSafely(localObject);
    throw localCloseableReference2;
  }
  
  protected abstract EncodedImage getEncodedImage(ImageRequest paramImageRequest)
    throws IOException;
  
  protected EncodedImage getEncodedImage(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    return getByteBufferBackedEncodedImage(paramInputStream, paramInt);
  }
  
  protected abstract String getProducerName();
  
  public void produceResults(final Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    final ProducerListener localProducerListener = paramProducerContext.getListener();
    final String str = paramProducerContext.getId();
    final ImageRequest localImageRequest = paramProducerContext.getImageRequest();
    paramConsumer = new StatefulProducerRunnable(paramConsumer, localProducerListener, getProducerName(), str)
    {
      protected void disposeResult(EncodedImage paramAnonymousEncodedImage)
      {
        EncodedImage.closeSafely(paramAnonymousEncodedImage);
      }
      
      protected EncodedImage getResult()
        throws Exception
      {
        EncodedImage localEncodedImage = getEncodedImage(localImageRequest);
        if (localEncodedImage == null)
        {
          localProducerListener.onUltimateProducerReached(str, getProducerName(), false);
          return null;
        }
        localEncodedImage.parseMetaData();
        localProducerListener.onUltimateProducerReached(str, getProducerName(), true);
        return localEncodedImage;
      }
    };
    paramProducerContext.addCallbacks(new BaseProducerContextCallbacks()
    {
      public void onCancellationRequested()
      {
        paramConsumer.cancel();
      }
    });
    mExecutor.execute(paramConsumer);
  }
}
