package com.facebook.imagepipeline.nativecode;

import android.graphics.Bitmap;
import com.facebook.common.internal.DoNotStrip;
import com.facebook.common.internal.Preconditions;

@DoNotStrip
public class NativeRoundingFilter
{
  static {}
  
  public NativeRoundingFilter() {}
  
  private static native void nativeToCircleFilter(Bitmap paramBitmap, boolean paramBoolean);
  
  private static native void nativeToCircleWithBorderFilter(Bitmap paramBitmap, int paramInt1, int paramInt2, boolean paramBoolean);
  
  public static void toCircle(Bitmap paramBitmap)
  {
    toCircle(paramBitmap, false);
  }
  
  public static void toCircle(Bitmap paramBitmap, boolean paramBoolean)
  {
    Preconditions.checkNotNull(paramBitmap);
    nativeToCircleFilter(paramBitmap, paramBoolean);
  }
  
  public static void toCircleWithBorder(Bitmap paramBitmap, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Preconditions.checkNotNull(paramBitmap);
    nativeToCircleWithBorderFilter(paramBitmap, paramInt1, paramInt2, paramBoolean);
  }
}
