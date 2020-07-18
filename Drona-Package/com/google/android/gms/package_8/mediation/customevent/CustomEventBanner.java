package com.google.android.gms.package_8.mediation.customevent;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.package_8.AdSize;
import com.google.android.gms.package_8.mediation.MediationAdRequest;

public abstract interface CustomEventBanner
  extends CustomEvent
{
  public abstract void requestBannerAd(Context paramContext, CustomEventBannerListener paramCustomEventBannerListener, String paramString, AdSize paramAdSize, MediationAdRequest paramMediationAdRequest, Bundle paramBundle);
}
