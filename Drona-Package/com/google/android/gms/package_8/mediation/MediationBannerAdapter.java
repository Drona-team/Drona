package com.google.android.gms.package_8.mediation;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.package_8.AdSize;

public abstract interface MediationBannerAdapter
  extends MediationAdapter
{
  public abstract View getBannerView();
  
  public abstract void requestBannerAd(Context paramContext, MediationBannerListener paramMediationBannerListener, Bundle paramBundle1, AdSize paramAdSize, MediationAdRequest paramMediationAdRequest, Bundle paramBundle2);
}
