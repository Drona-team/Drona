package com.facebook.imagepipeline.request;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.nativecode.Bitmaps;

public abstract class BasePostprocessor
  implements Postprocessor
{
  public static final Bitmap.Config FALLBACK_BITMAP_CONFIGURATION = Bitmap.Config.ARGB_8888;
  
  public BasePostprocessor() {}
  
  private static void internalCopyBitmap(Bitmap paramBitmap1, Bitmap paramBitmap2)
  {
    if (paramBitmap1.getConfig() == paramBitmap2.getConfig())
    {
      Bitmaps.copyBitmap(paramBitmap1, paramBitmap2);
      return;
    }
    new Canvas(paramBitmap1).drawBitmap(paramBitmap2, 0.0F, 0.0F, null);
  }
  
  public String getName()
  {
    return "Unknown postprocessor";
  }
  
  public CacheKey getPostprocessorCacheKey()
  {
    return null;
  }
  
  public CloseableReference process(Bitmap paramBitmap, PlatformBitmapFactory paramPlatformBitmapFactory)
  {
    Bitmap.Config localConfig2 = paramBitmap.getConfig();
    Bitmap.Config localConfig1 = localConfig2;
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    if (localConfig2 == null) {
      localConfig1 = FALLBACK_BITMAP_CONFIGURATION;
    }
    paramPlatformBitmapFactory = paramPlatformBitmapFactory.createBitmapInternal(i, j, localConfig1);
    try
    {
      process((Bitmap)paramPlatformBitmapFactory.get(), paramBitmap);
      paramBitmap = CloseableReference.cloneOrNull(paramPlatformBitmapFactory);
      CloseableReference.closeSafely(paramPlatformBitmapFactory);
      return paramBitmap;
    }
    catch (Throwable paramBitmap)
    {
      CloseableReference.closeSafely(paramPlatformBitmapFactory);
      throw paramBitmap;
    }
  }
  
  public void process(Bitmap paramBitmap) {}
  
  public void process(Bitmap paramBitmap1, Bitmap paramBitmap2)
  {
    internalCopyBitmap(paramBitmap1, paramBitmap2);
    process(paramBitmap1);
  }
}
