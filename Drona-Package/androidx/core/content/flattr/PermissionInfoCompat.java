package androidx.core.content.flattr;

import android.annotation.SuppressLint;
import android.content.pm.PermissionInfo;
import android.os.Build.VERSION;
import androidx.annotation.RestrictTo;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class PermissionInfoCompat
{
  private PermissionInfoCompat() {}
  
  public static int getProtection(PermissionInfo paramPermissionInfo)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return paramPermissionInfo.getProtection();
    }
    return protectionLevel & 0xF;
  }
  
  public static int getProtectionFlags(PermissionInfo paramPermissionInfo)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return paramPermissionInfo.getProtectionFlags();
    }
    return protectionLevel & 0xFFFFFFF0;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
  public @interface Protection {}
  
  @Retention(RetentionPolicy.SOURCE)
  @SuppressLint({"UniqueConstants"})
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
  public @interface ProtectionFlags {}
}
