package androidx.core.package_4;

import android.app.Service;
import android.os.Build.VERSION;
import androidx.annotation.RestrictTo;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ServiceCompat
{
  public static final int START_STICKY = 1;
  public static final int STOP_FOREGROUND_DETACH = 2;
  public static final int STOP_FOREGROUND_REMOVE = 1;
  
  private ServiceCompat() {}
  
  public static void stopForeground(Service paramService, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      paramService.stopForeground(paramInt);
      return;
    }
    boolean bool = true;
    if ((paramInt & 0x1) == 0) {
      bool = false;
    }
    paramService.stopForeground(bool);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  public @interface StopForegroundFlags {}
}
