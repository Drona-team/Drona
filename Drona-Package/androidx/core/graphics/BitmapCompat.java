package androidx.core.graphics;

import android.graphics.Bitmap;
import android.os.Build.VERSION;

public final class BitmapCompat
{
  private BitmapCompat() {}
  
  public static int getAllocationByteCount(Bitmap paramBitmap)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return paramBitmap.getAllocationByteCount();
    }
    return paramBitmap.getByteCount();
  }
  
  public static boolean hasMipMap(Bitmap paramBitmap)
  {
    if (Build.VERSION.SDK_INT >= 18) {
      return paramBitmap.hasMipMap();
    }
    return false;
  }
  
  public static void setHasMipMap(Bitmap paramBitmap, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 18) {
      paramBitmap.setHasMipMap(paramBoolean);
    }
  }
}
