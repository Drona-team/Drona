package com.google.android.gms.package_8.mediation.customevent;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.package_8.mediation.MediationAdRequest;

public abstract interface CustomEventInterstitial
  extends CustomEvent
{
  public abstract void requestInterstitialAd(Context paramContext, CustomEventInterstitialListener paramCustomEventInterstitialListener, String paramString, MediationAdRequest paramMediationAdRequest, Bundle paramBundle);
  
  public abstract void showInterstitial();
}
