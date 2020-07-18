package com.google.ads.mediation;

import android.app.Activity;

@Deprecated
public abstract interface MediationInterstitialAdapter<ADDITIONAL_PARAMETERS extends NetworkExtras, SERVER_PARAMETERS extends MediationServerParameters>
  extends MediationAdapter<ADDITIONAL_PARAMETERS, SERVER_PARAMETERS>
{
  public abstract void requestInterstitialAd(MediationInterstitialListener paramMediationInterstitialListener, Activity paramActivity, MediationServerParameters paramMediationServerParameters, MediationAdRequest paramMediationAdRequest, NetworkExtras paramNetworkExtras);
  
  public abstract void showInterstitial();
}
