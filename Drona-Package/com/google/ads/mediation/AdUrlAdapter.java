package com.google.ads.mediation;

import android.os.Bundle;
import androidx.annotation.Keep;
import com.google.android.gms.package_8.mediation.MediationBannerAdapter;
import com.google.android.gms.package_8.mediation.MediationInterstitialAdapter;
import com.google.android.gms.package_8.mediation.MediationNativeAdapter;

@Keep
public final class AdUrlAdapter
  extends AbstractAdViewAdapter
  implements MediationBannerAdapter, MediationInterstitialAdapter, MediationNativeAdapter
{
  public AdUrlAdapter() {}
  
  public final String getAdUnitId(Bundle paramBundle)
  {
    return "adurl";
  }
  
  protected final Bundle toBundle(Bundle paramBundle1, Bundle paramBundle2)
  {
    if (paramBundle1 == null) {
      paramBundle1 = new Bundle();
    }
    paramBundle1.putBundle("sdk_less_server_data", paramBundle2);
    paramBundle1.putBoolean("_noRefresh", true);
    return paramBundle1;
  }
}
