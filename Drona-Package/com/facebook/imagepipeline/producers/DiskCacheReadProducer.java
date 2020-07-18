package com.facebook.imagepipeline.producers;

import bolts.Continuation;
import bolts.Task;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.CacheChoice;
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

public class DiskCacheReadProducer
  implements Producer<EncodedImage>
{
  public static final String ENCODED_IMAGE_SIZE = "encodedImageSize";
  public static final String EXTRA_CACHED_VALUE_FOUND = "cached_value_found";
  public static final String PRODUCER_NAME = "DiskCacheProducer";
  private final CacheKeyFactory mCacheKeyFactory;
  private final BufferedDiskCache mDefaultBufferedDiskCache;
  private final Producer<EncodedImage> mInputProducer;
  private final BufferedDiskCache mSmallImageBufferedDiskCache;
  
  public DiskCacheReadProducer(BufferedDiskCache paramBufferedDiskCache1, BufferedDiskCache paramBufferedDiskCache2, CacheKeyFactory paramCacheKeyFactory, Producer paramProducer)
  {
    mDefaultBufferedDiskCache = paramBufferedDiskCache1;
    mSmallImageBufferedDiskCache = paramBufferedDiskCache2;
    mCacheKeyFactory = paramCacheKeyFactory;
    mInputProducer = paramProducer;
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
  
  private void maybeStartInputProducer(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    if (paramProducerContext.getLowestPermittedRequestLevel().getValue() >= ImageRequest.RequestLevel.DISK_CACHE.getValue())
    {
      paramConsumer.onNewResult(null, 1);
      return;
    }
    mInputProducer.produceResults(paramConsumer, paramProducerContext);
  }
  
  private Continuation onFinishDiskReads(final Consumer paramConsumer, final ProducerContext paramProducerContext)
  {
    final String str = paramProducerContext.getId();
    new Continuation()
    {
      public Void then(Task paramAnonymousTask)
        throws Exception
      {
        if (DiskCacheReadProducer.isTaskCancelled(paramAnonymousTask))
        {
          val$listener.onProducerFinishWithCancellation(str, "DiskCacheProducer", null);
          paramConsumer.onCancellation();
          return null;
        }
        if (paramAnonymousTask.isFaulted())
        {
          val$listener.onProducerFinishWithFailure(str, "DiskCacheProducer", paramAnonymousTask.getError(), null);
          mInputProducer.produceResults(paramConsumer, paramProducerContext);
          return null;
        }
        paramAnonymousTask = (EncodedImage)paramAnonymousTask.getResult();
        if (paramAnonymousTask != null)
        {
          val$listener.onProducerFinishWithSuccess(str, "DiskCacheProducer", DiskCacheReadProducer.getExtraMap(val$listener, str, true, paramAnonymousTask.getSize()));
          val$listener.onUltimateProducerReached(str, "DiskCacheProducer", true);
          paramConsumer.onProgressUpdate(1.0F);
          paramConsumer.onNewResult(paramAnonymousTask, 1);
          paramAnonymousTask.close();
          return null;
        }
        val$listener.onProducerFinishWithSuccess(str, "DiskCacheProducer", DiskCacheReadProducer.getExtraMap(val$listener, str, false, 0));
        mInputProducer.produceResults(paramConsumer, paramProducerContext);
        return null;
      }
    };
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
    Object localObject = paramProducerContext.getImageRequest();
    if (!((ImageRequest)localObject).isDiskCacheEnabled())
    {
      maybeStartInputProducer(paramConsumer, paramProducerContext);
      return;
    }
    paramProducerContext.getListener().onProducerStart(paramProducerContext.getId(), "DiskCacheProducer");
    CacheKey localCacheKey = mCacheKeyFactory.getEncodedCacheKey((ImageRequest)localObject, paramProducerContext.getCallerContext());
    int i;
    if (((ImageRequest)localObject).getCacheChoice() == ImageRequest.CacheChoice.SMALL) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0) {
      localObject = mSmallImageBufferedDiskCache;
    } else {
      localObject = mDefaultBufferedDiskCache;
    }
    AtomicBoolean localAtomicBoolean = new AtomicBoolean(false);
    ((BufferedDiskCache)localObject).addTask(localCacheKey, localAtomicBoolean).continueWith(onFinishDiskReads(paramConsumer, paramProducerContext));
    subscribeTaskForRequestCancellation(localAtomicBoolean, paramProducerContext);
  }
}
