package com.google.android.gms.package_8.mediation.admob;

import android.os.Bundle;
import com.google.ads.mediation.NetworkExtras;

@Deprecated
public final class AdMobExtras
  implements NetworkExtras
{
  private final Bundle extras;
  
  public AdMobExtras(Bundle paramBundle)
  {
    if (paramBundle != null) {
      paramBundle = new Bundle(paramBundle);
    } else {
      paramBundle = null;
    }
    extras = paramBundle;
  }
  
  public final Bundle getExtras()
  {
    return extras;
  }
}
