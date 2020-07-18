package com.facebook.imagepipeline.producers;

import android.util.Pair;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel;

public class BitmapMemoryCacheKeyMultiplexProducer
  extends MultiplexProducer<Pair<CacheKey, ImageRequest.RequestLevel>, CloseableReference<CloseableImage>>
{
  private final CacheKeyFactory mCacheKeyFactory;
  
  public BitmapMemoryCacheKeyMultiplexProducer(CacheKeyFactory paramCacheKeyFactory, Producer paramProducer)
  {
    super(paramProducer);
    mCacheKeyFactory = paramCacheKeyFactory;
  }
  
  public CloseableReference cloneOrNull(CloseableReference paramCloseableReference)
  {
    return CloseableReference.cloneOrNull(paramCloseableReference);
  }
  
  protected Pair getKey(ProducerContext paramProducerContext)
  {
    return Pair.create(mCacheKeyFactory.getBitmapCacheKey(paramProducerContext.getImageRequest(), paramProducerContext.getCallerContext()), paramProducerContext.getLowestPermittedRequestLevel());
  }
}
