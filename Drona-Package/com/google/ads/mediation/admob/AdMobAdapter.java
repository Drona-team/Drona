package com.google.ads.mediation.admob;

import android.os.BaseBundle;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.Keep;
import com.google.ads.mediation.AbstractAdViewAdapter;

@Keep
public final class AdMobAdapter
  extends AbstractAdViewAdapter
{
  public static final String NEW_BUNDLE = "_newBundle";
  
  public AdMobAdapter() {}
  
  protected final Bundle toBundle(Bundle paramBundle1, Bundle paramBundle2)
  {
    if (paramBundle1 == null) {
      paramBundle1 = new Bundle();
    }
    Bundle localBundle = paramBundle1;
    if (paramBundle1.getBoolean("_newBundle")) {
      localBundle = new Bundle(paramBundle1);
    }
    localBundle.putInt("gw", 1);
    localBundle.putString("mad_hac", paramBundle2.getString("mad_hac"));
    if (!TextUtils.isEmpty(paramBundle2.getString("adJson"))) {
      localBundle.putString("_ad", paramBundle2.getString("adJson"));
    }
    localBundle.putBoolean("_noRefresh", true);
    return localBundle;
  }
}
