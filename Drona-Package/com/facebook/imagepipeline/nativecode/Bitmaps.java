package com.facebook.imagepipeline.nativecode;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.facebook.common.internal.DoNotStrip;
import com.facebook.common.internal.Preconditions;

@DoNotStrip
public class Bitmaps
{
  static {}
  
  public Bitmaps() {}
  
  public static void copyBitmap(Bitmap paramBitmap1, Bitmap paramBitmap2)
  {
    Bitmap.Config localConfig1 = paramBitmap2.getConfig();
    Bitmap.Config localConfig2 = paramBitmap1.getConfig();
    boolean bool2 = false;
    if (localConfig1 == localConfig2) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    Preconditions.checkArgument(paramBitmap1.isMutable());
    if (paramBitmap1.getWidth() == paramBitmap2.getWidth()) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    boolean bool1 = bool2;
    if (paramBitmap1.getHeight() == paramBitmap2.getHeight()) {
      bool1 = true;
    }
    Preconditions.checkArgument(bool1);
    nativeCopyBitmap(paramBitmap1, paramBitmap1.getRowBytes(), paramBitmap2, paramBitmap2.getRowBytes(), paramBitmap1.getHeight());
  }
  
  private static native void nativeCopyBitmap(Bitmap paramBitmap1, int paramInt1, Bitmap paramBitmap2, int paramInt2, int paramInt3);
}
