package com.facebook.imagepipeline.producers;

import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.Postprocessor;
import com.facebook.imagepipeline.request.RepeatedPostprocessor;

public class PostprocessedBitmapMemoryCacheProducer
  implements Producer<CloseableReference<CloseableImage>>
{
  public static final String PRODUCER_NAME = "PostprocessedBitmapMemoryCacheProducer";
  @VisibleForTesting
  static final String VALUE_FOUND = "cached_value_found";
  private final CacheKeyFactory mCacheKeyFactory;
  private final Producer<CloseableReference<CloseableImage>> mInputProducer;
  private final MemoryCache<CacheKey, CloseableImage> mMemoryCache;
  
  public PostprocessedBitmapMemoryCacheProducer(MemoryCache paramMemoryCache, CacheKeyFactory paramCacheKeyFactory, Producer paramProducer)
  {
    mMemoryCache = paramMemoryCache;
    mCacheKeyFactory = paramCacheKeyFactory;
    mInputProducer = paramProducer;
  }
  
  protected String getProducerName()
  {
    return "PostprocessedBitmapMemoryCacheProducer";
  }
  
  public void produceResults(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    ProducerListener localProducerListener = paramProducerContext.getListener();
    String str = paramProducerContext.getId();
    Object localObject1 = paramProducerContext.getImageRequest();
    Object localObject2 = paramProducerContext.getCallerContext();
    Postprocessor localPostprocessor = ((ImageRequest)localObject1).getPostprocessor();
    if ((localPostprocessor != null) && (localPostprocessor.getPostprocessorCacheKey() != null))
    {
      localProducerListener.onProducerStart(str, getProducerName());
      CacheKey localCacheKey = mCacheKeyFactory.getPostprocessedBitmapCacheKey((ImageRequest)localObject1, localObject2);
      Object localObject3 = mMemoryCache.cache(localCacheKey);
      localObject1 = null;
      localObject2 = null;
      if (localObject3 != null)
      {
        localObject1 = getProducerName();
        paramProducerContext = (ProducerContext)localObject2;
        if (localProducerListener.requiresExtraMap(str)) {
          paramProducerContext = ImmutableMap.of("cached_value_found", "true");
        }
        localProducerListener.onProducerFinishWithSuccess(str, (String)localObject1, paramProducerContext);
        localProducerListener.onUltimateProducerReached(str, "PostprocessedBitmapMemoryCacheProducer", true);
        paramConsumer.onProgressUpdate(1.0F);
        paramConsumer.onNewResult(localObject3, 1);
        ((CloseableReference)localObject3).close();
        return;
      }
      boolean bool1 = localPostprocessor instanceof RepeatedPostprocessor;
      boolean bool2 = paramProducerContext.getImageRequest().isMemoryCacheEnabled();
      localObject2 = new CachedPostprocessorConsumer(paramConsumer, localCacheKey, bool1, mMemoryCache, bool2);
      localObject3 = getProducerName();
      paramConsumer = (Consumer)localObject1;
      if (localProducerListener.requiresExtraMap(str)) {
        paramConsumer = ImmutableMap.of("cached_value_found", "false");
      }
      localProducerListener.onProducerFinishWithSuccess(str, (String)localObject3, paramConsumer);
      mInputProducer.produceResults((Consumer)localObject2, paramProducerContext);
      return;
    }
    mInputProducer.produceResults(paramConsumer, paramProducerContext);
  }
  
  public static class CachedPostprocessorConsumer
    extends DelegatingConsumer<CloseableReference<CloseableImage>, CloseableReference<CloseableImage>>
  {
    private final CacheKey mCacheKey;
    private final boolean mIsMemoryCachedEnabled;
    private final boolean mIsRepeatedProcessor;
    private final MemoryCache<CacheKey, CloseableImage> mMemoryCache;
    
    public CachedPostprocessorConsumer(Consumer paramConsumer, CacheKey paramCacheKey, boolean paramBoolean1, MemoryCache paramMemoryCache, boolean paramBoolean2)
    {
      super();
      mCacheKey = paramCacheKey;
      mIsRepeatedProcessor = paramBoolean1;
      mMemoryCache = paramMemoryCache;
      mIsMemoryCachedEnabled = paramBoolean2;
    }
    
    protected void onNewResultImpl(CloseableReference paramCloseableReference, int paramInt)
    {
      CloseableReference localCloseableReference = null;
      if (paramCloseableReference == null)
      {
        if (BaseConsumer.isLast(paramInt)) {
          getConsumer().onNewResult(null, paramInt);
        }
      }
      else
      {
        if ((BaseConsumer.isNotLast(paramInt)) && (!mIsRepeatedProcessor)) {
          return;
        }
        if (mIsMemoryCachedEnabled) {
          localCloseableReference = mMemoryCache.cache(mCacheKey, paramCloseableReference);
        }
        try
        {
          getConsumer().onProgressUpdate(1.0F);
          Consumer localConsumer = getConsumer();
          if (localCloseableReference != null) {
            paramCloseableReference = localCloseableReference;
          }
          localConsumer.onNewResult(paramCloseableReference, paramInt);
          CloseableReference.closeSafely(localCloseableReference);
          return;
        }
        catch (Throwable paramCloseableReference)
        {
          CloseableReference.closeSafely(localCloseableReference);
          throw paramCloseableReference;
        }
      }
    }
  }
}
