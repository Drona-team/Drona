package com.google.ads.mediation;

import android.app.Activity;
import android.view.View;
import com.google.ads.AdSize;

@Deprecated
public abstract interface MediationBannerAdapter<ADDITIONAL_PARAMETERS extends NetworkExtras, SERVER_PARAMETERS extends MediationServerParameters>
  extends MediationAdapter<ADDITIONAL_PARAMETERS, SERVER_PARAMETERS>
{
  public abstract View getBannerView();
  
  public abstract void requestBannerAd(MediationBannerListener paramMediationBannerListener, Activity paramActivity, MediationServerParameters paramMediationServerParameters, AdSize paramAdSize, MediationAdRequest paramMediationAdRequest, NetworkExtras paramNetworkExtras);
}
