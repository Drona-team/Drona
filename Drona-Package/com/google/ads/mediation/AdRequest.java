package com.google.ads.mediation;

import android.os.Bundle;
import com.google.android.gms.package_8.InterstitialAd;
import com.google.android.gms.package_8.reward.AdMetadataListener;
import com.google.android.gms.package_8.reward.mediation.MediationRewardedVideoAdListener;

final class AdRequest
  extends AdMetadataListener
{
  AdRequest(AbstractAdViewAdapter paramAbstractAdViewAdapter) {}
  
  public final void onAdMetadataChanged()
  {
    if ((AbstractAdViewAdapter.get0(zzmk) != null) && (AbstractAdViewAdapter.setShuffle(zzmk) != null))
    {
      Bundle localBundle = AbstractAdViewAdapter.get0(zzmk).getAdMetadata();
      AbstractAdViewAdapter.setShuffle(zzmk).loadFragment(localBundle);
    }
  }
}
