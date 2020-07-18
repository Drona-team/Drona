package androidx.core.content.flattr;

import android.content.pm.PackageInfo;
import android.os.Build.VERSION;

public final class PackageInfoCompat
{
  private PackageInfoCompat() {}
  
  public static long getLongVersionCode(PackageInfo paramPackageInfo)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return paramPackageInfo.getLongVersionCode();
    }
    return versionCode;
  }
}
