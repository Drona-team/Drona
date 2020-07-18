package androidx.browser.customtabs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.package_4.BundleCompat;

public class TrustedWebUtils
{
  public static final String EXTRA_LAUNCH_AS_TRUSTED_WEB_ACTIVITY = "android.support.customtabs.extra.LAUNCH_AS_TRUSTED_WEB_ACTIVITY";
  
  private TrustedWebUtils() {}
  
  public static void launchAsTrustedWebActivity(Context paramContext, CustomTabsIntent paramCustomTabsIntent, Uri paramUri)
  {
    if (BundleCompat.getBinder(intent.getExtras(), "android.support.customtabs.extra.SESSION") != null)
    {
      intent.putExtra("android.support.customtabs.extra.LAUNCH_AS_TRUSTED_WEB_ACTIVITY", true);
      paramCustomTabsIntent.launchUrl(paramContext, paramUri);
      return;
    }
    throw new IllegalArgumentException("Given CustomTabsIntent should be associated with a valid CustomTabsSession");
  }
}
