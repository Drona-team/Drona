package com.facebook.imagepipeline.postprocessors;

import android.graphics.Bitmap;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.internal.Preconditions;
import com.facebook.imagepipeline.filter.InPlaceRoundFilter;
import com.facebook.imagepipeline.filter.XferRoundFilter;
import com.facebook.imagepipeline.request.BasePostprocessor;
import javax.annotation.Nullable;

public class RoundPostprocessor
  extends BasePostprocessor
{
  private static final boolean ENABLE_ANTI_ALIASING = true;
  private static final boolean canUseXferRoundFilter = ;
  @Nullable
  private CacheKey mCacheKey;
  private final boolean mEnableAntiAliasing;
  
  public RoundPostprocessor()
  {
    this(true);
  }
  
  public RoundPostprocessor(boolean paramBoolean)
  {
    mEnableAntiAliasing = paramBoolean;
  }
  
  public CacheKey getPostprocessorCacheKey()
  {
    if (mCacheKey == null) {
      if (canUseXferRoundFilter) {
        mCacheKey = new SimpleCacheKey("XferRoundFilter");
      } else {
        mCacheKey = new SimpleCacheKey("InPlaceRoundFilter");
      }
    }
    return mCacheKey;
  }
  
  public void process(Bitmap paramBitmap)
  {
    InPlaceRoundFilter.roundBitmapInPlace(paramBitmap);
  }
  
  public void process(Bitmap paramBitmap1, Bitmap paramBitmap2)
  {
    Preconditions.checkNotNull(paramBitmap1);
    Preconditions.checkNotNull(paramBitmap2);
    if (canUseXferRoundFilter)
    {
      XferRoundFilter.xferRoundBitmap(paramBitmap1, paramBitmap2, mEnableAntiAliasing);
      return;
    }
    super.process(paramBitmap1, paramBitmap2);
  }
}
