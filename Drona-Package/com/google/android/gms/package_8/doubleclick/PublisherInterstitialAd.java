package com.google.android.gms.package_8.doubleclick;

import android.content.Context;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.ads.zzabd;
import com.google.android.gms.package_8.AdListener;
import com.google.android.gms.package_8.Correlator;

public final class PublisherInterstitialAd
{
  private final zzabd zzaas = new zzabd(paramContext, this);
  
  public PublisherInterstitialAd(Context paramContext)
  {
    Preconditions.checkNotNull(paramContext, "Context cannot be null");
  }
  
  public final AdListener getAdListener()
  {
    return zzaas.getAdListener();
  }
  
  public final String getAdUnitId()
  {
    return zzaas.getAdUnitId();
  }
  
  public final AppEventListener getAppEventListener()
  {
    return zzaas.getAppEventListener();
  }
  
  public final String getMediationAdapterClassName()
  {
    return zzaas.getMediationAdapterClassName();
  }
  
  public final OnCustomRenderedAdLoadedListener getOnCustomRenderedAdLoadedListener()
  {
    return zzaas.getOnCustomRenderedAdLoadedListener();
  }
  
  public final boolean isLoaded()
  {
    return zzaas.isLoaded();
  }
  
  public final boolean isLoading()
  {
    return zzaas.isLoading();
  }
  
  public final void loadAd(PublisherAdRequest paramPublisherAdRequest)
  {
    zzaas.zza(paramPublisherAdRequest.zzde());
  }
  
  public final void setAdListener(AdListener paramAdListener)
  {
    zzaas.setAdListener(paramAdListener);
  }
  
  public final void setAdUnitId(String paramString)
  {
    zzaas.setAdUnitId(paramString);
  }
  
  public final void setAppEventListener(AppEventListener paramAppEventListener)
  {
    zzaas.setAppEventListener(paramAppEventListener);
  }
  
  public final void setCorrelator(Correlator paramCorrelator)
  {
    zzaas.setCorrelator(paramCorrelator);
  }
  
  public final void setImmersiveMode(boolean paramBoolean)
  {
    zzaas.setImmersiveMode(paramBoolean);
  }
  
  public final void setOnCustomRenderedAdLoadedListener(OnCustomRenderedAdLoadedListener paramOnCustomRenderedAdLoadedListener)
  {
    zzaas.setOnCustomRenderedAdLoadedListener(paramOnCustomRenderedAdLoadedListener);
  }
  
  public final void show()
  {
    zzaas.show();
  }
}
