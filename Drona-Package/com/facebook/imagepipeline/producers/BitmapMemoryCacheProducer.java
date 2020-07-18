package com.facebook.imagepipeline.producers;

import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.util.Map;

public class BitmapMemoryCacheProducer
  implements Producer<CloseableReference<CloseableImage>>
{
  public static final String EXTRA_CACHED_VALUE_FOUND = "cached_value_found";
  public static final String PRODUCER_NAME = "BitmapMemoryCacheProducer";
  private final CacheKeyFactory mCacheKeyFactory;
  private final Producer<CloseableReference<CloseableImage>> mInputProducer;
  private final MemoryCache<CacheKey, CloseableImage> mMemoryCache;
  
  public BitmapMemoryCacheProducer(MemoryCache paramMemoryCache, CacheKeyFactory paramCacheKeyFactory, Producer paramProducer)
  {
    mMemoryCache = paramMemoryCache;
    mCacheKeyFactory = paramCacheKeyFactory;
    mInputProducer = paramProducer;
  }
  
  protected String getProducerName()
  {
    return "BitmapMemoryCacheProducer";
  }
  
  public void produceResults(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    try
    {
      boolean bool1 = FrescoSystrace.isTracing();
      if (bool1) {
        FrescoSystrace.beginSection("BitmapMemoryCacheProducer#produceResults");
      }
      ProducerListener localProducerListener = paramProducerContext.getListener();
      String str1 = paramProducerContext.getId();
      localProducerListener.onProducerStart(str1, getProducerName());
      Object localObject1 = paramProducerContext.getImageRequest();
      Object localObject2 = paramProducerContext.getCallerContext();
      Object localObject3 = mCacheKeyFactory.getBitmapCacheKey((ImageRequest)localObject1, localObject2);
      CloseableReference localCloseableReference = mMemoryCache.cache(localObject3);
      localObject2 = null;
      if (localCloseableReference != null)
      {
        bool1 = ((CloseableImage)localCloseableReference.get()).getQualityInfo().isOfFullQuality();
        if (bool1)
        {
          String str2 = getProducerName();
          boolean bool2 = localProducerListener.requiresExtraMap(str1);
          if (bool2) {
            localObject1 = ImmutableMap.of("cached_value_found", "true");
          } else {
            localObject1 = null;
          }
          localProducerListener.onProducerFinishWithSuccess(str1, str2, (Map)localObject1);
          localProducerListener.onUltimateProducerReached(str1, getProducerName(), true);
          paramConsumer.onProgressUpdate(1.0F);
        }
        paramConsumer.onNewResult(localCloseableReference, BaseConsumer.simpleStatusForIsLast(bool1));
        localCloseableReference.close();
        if (bool1)
        {
          if (!FrescoSystrace.isTracing()) {
            return;
          }
          FrescoSystrace.endSection();
          return;
        }
      }
      int i = paramProducerContext.getLowestPermittedRequestLevel().getValue();
      int j = ImageRequest.RequestLevel.BITMAP_MEMORY_CACHE.getValue();
      if (i >= j)
      {
        localObject1 = getProducerName();
        bool1 = localProducerListener.requiresExtraMap(str1);
        if (bool1) {
          paramProducerContext = ImmutableMap.of("cached_value_found", "false");
        } else {
          paramProducerContext = null;
        }
        localProducerListener.onProducerFinishWithSuccess(str1, (String)localObject1, paramProducerContext);
        localProducerListener.onUltimateProducerReached(str1, getProducerName(), false);
        paramConsumer.onNewResult(null, 1);
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.endSection();
        }
      }
      else
      {
        localObject1 = wrapConsumer(paramConsumer, (CacheKey)localObject3, paramProducerContext.getImageRequest().isMemoryCacheEnabled());
        localObject3 = getProducerName();
        bool1 = localProducerListener.requiresExtraMap(str1);
        paramConsumer = localObject2;
        if (bool1) {
          paramConsumer = ImmutableMap.of("cached_value_found", "false");
        }
        localProducerListener.onProducerFinishWithSuccess(str1, (String)localObject3, paramConsumer);
        bool1 = FrescoSystrace.isTracing();
        if (bool1) {
          FrescoSystrace.beginSection("mInputProducer.produceResult");
        }
        mInputProducer.produceResults((Consumer)localObject1, paramProducerContext);
        bool1 = FrescoSystrace.isTracing();
        if (bool1) {
          FrescoSystrace.endSection();
        }
        if (FrescoSystrace.isTracing())
        {
          FrescoSystrace.endSection();
          return;
        }
      }
    }
    catch (Throwable paramConsumer)
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      throw paramConsumer;
    }
  }
  
  protected Consumer wrapConsumer(Consumer paramConsumer, final CacheKey paramCacheKey, final boolean paramBoolean)
  {
    new DelegatingConsumer(paramConsumer)
    {
      public void onNewResultImpl(CloseableReference paramAnonymousCloseableReference, int paramAnonymousInt)
      {
        try
        {
          boolean bool1 = FrescoSystrace.isTracing();
          if (bool1) {
            FrescoSystrace.beginSection("BitmapMemoryCacheProducer#onNewResultImpl");
          }
          bool1 = BaseConsumer.isLast(paramAnonymousInt);
          CloseableReference localCloseableReference = null;
          if (paramAnonymousCloseableReference == null)
          {
            if (bool1) {
              getConsumer().onNewResult(null, paramAnonymousInt);
            }
            if (FrescoSystrace.isTracing()) {
              FrescoSystrace.endSection();
            }
          }
          else
          {
            boolean bool2 = ((CloseableImage)paramAnonymousCloseableReference.get()).isStateful();
            if (!bool2)
            {
              bool2 = BaseConsumer.statusHasFlag(paramAnonymousInt, 8);
              if (!bool2)
              {
                if (!bool1)
                {
                  localObject = mMemoryCache.cache(paramCacheKey);
                  if (localObject != null) {
                    try
                    {
                      QualityInfo localQualityInfo1 = ((CloseableImage)paramAnonymousCloseableReference.get()).getQualityInfo();
                      QualityInfo localQualityInfo2 = ((CloseableImage)((CloseableReference)localObject).get()).getQualityInfo();
                      bool2 = localQualityInfo2.isOfFullQuality();
                      if (!bool2)
                      {
                        int i = localQualityInfo2.getQuality();
                        int j = localQualityInfo1.getQuality();
                        if (i < j)
                        {
                          CloseableReference.closeSafely((CloseableReference)localObject);
                          break label223;
                        }
                      }
                      getConsumer().onNewResult(localObject, paramAnonymousInt);
                      CloseableReference.closeSafely((CloseableReference)localObject);
                      if (!FrescoSystrace.isTracing()) {
                        return;
                      }
                      FrescoSystrace.endSection();
                      return;
                    }
                    catch (Throwable paramAnonymousCloseableReference)
                    {
                      CloseableReference.closeSafely((CloseableReference)localObject);
                      throw paramAnonymousCloseableReference;
                    }
                  }
                }
                label223:
                bool2 = paramBoolean;
                if (bool2) {
                  localCloseableReference = mMemoryCache.cache(paramCacheKey, paramAnonymousCloseableReference);
                }
                if (bool1) {
                  try
                  {
                    getConsumer().onProgressUpdate(1.0F);
                  }
                  catch (Throwable paramAnonymousCloseableReference)
                  {
                    break label313;
                  }
                }
                Object localObject = getConsumer();
                if (localCloseableReference != null) {
                  paramAnonymousCloseableReference = localCloseableReference;
                }
                ((Consumer)localObject).onNewResult(paramAnonymousCloseableReference, paramAnonymousInt);
                CloseableReference.closeSafely(localCloseableReference);
                if (!FrescoSystrace.isTracing()) {
                  return;
                }
                FrescoSystrace.endSection();
                return;
                label313:
                CloseableReference.closeSafely(localCloseableReference);
                throw paramAnonymousCloseableReference;
              }
            }
            getConsumer().onNewResult(paramAnonymousCloseableReference, paramAnonymousInt);
            if (FrescoSystrace.isTracing())
            {
              FrescoSystrace.endSection();
              return;
            }
          }
        }
        catch (Throwable paramAnonymousCloseableReference)
        {
          if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
          }
          throw paramAnonymousCloseableReference;
        }
      }
    };
  }
}
