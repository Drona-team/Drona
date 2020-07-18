package com.facebook.imagepipeline.postprocessors;

import android.graphics.Bitmap;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.internal.Preconditions;
import com.facebook.imagepipeline.nativecode.NativeBlurFilter;
import com.facebook.imagepipeline.request.BasePostprocessor;

public class IterativeBoxBlurPostProcessor
  extends BasePostprocessor
{
  private static final int DEFAULT_ITERATIONS = 3;
  private final int mBlurRadius;
  private CacheKey mCacheKey;
  private final int mIterations;
  
  public IterativeBoxBlurPostProcessor(int paramInt)
  {
    this(3, paramInt);
  }
  
  public IterativeBoxBlurPostProcessor(int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    if (paramInt1 > 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    boolean bool1 = bool2;
    if (paramInt2 > 0) {
      bool1 = true;
    }
    Preconditions.checkArgument(bool1);
    mIterations = paramInt1;
    mBlurRadius = paramInt2;
  }
  
  public CacheKey getPostprocessorCacheKey()
  {
    if (mCacheKey == null) {
      mCacheKey = new SimpleCacheKey(String.format(null, "i%dr%d", new Object[] { Integer.valueOf(mIterations), Integer.valueOf(mBlurRadius) }));
    }
    return mCacheKey;
  }
  
  public void process(Bitmap paramBitmap)
  {
    NativeBlurFilter.iterativeBoxBlur(paramBitmap, mIterations, mBlurRadius);
  }
}
