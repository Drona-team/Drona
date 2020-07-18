package androidx.core.database;

import android.database.CursorWindow;
import android.os.Build.VERSION;

public final class CursorWindowCompat
{
  private CursorWindowCompat() {}
  
  public static CursorWindow create(String paramString, long paramLong)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return new CursorWindow(paramString, paramLong);
    }
    if (Build.VERSION.SDK_INT >= 15) {
      return new CursorWindow(paramString);
    }
    return new CursorWindow(false);
  }
}
