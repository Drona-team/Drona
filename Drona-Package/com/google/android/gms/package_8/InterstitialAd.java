package com.google.android.gms.package_8;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.ads.zzabd;
import com.google.android.gms.internal.ads.zzxr;
import com.google.android.gms.package_8.reward.AdMetadataListener;
import com.google.android.gms.package_8.reward.RewardedVideoAdListener;

public final class InterstitialAd
{
  private final zzabd zzaas;
  
  public InterstitialAd(Context paramContext)
  {
    zzaas = new zzabd(paramContext);
    Preconditions.checkNotNull(paramContext, "Context cannot be null");
  }
  
  public final void e(boolean paramBoolean)
  {
    zzaas.zzc(true);
  }
  
  public final AdListener getAdListener()
  {
    return zzaas.getAdListener();
  }
  
  public final Bundle getAdMetadata()
  {
    return zzaas.getAdMetadata();
  }
  
  public final String getAdUnitId()
  {
    return zzaas.getAdUnitId();
  }
  
  public final String getMediationAdapterClassName()
  {
    return zzaas.getMediationAdapterClassName();
  }
  
  public final boolean isLoaded()
  {
    return zzaas.isLoaded();
  }
  
  public final boolean isLoading()
  {
    return zzaas.isLoading();
  }
  
  public final void loadAd(AdRequest paramAdRequest)
  {
    zzaas.zza(paramAdRequest.zzde());
  }
  
  public final void setAdListener(AdListener paramAdListener)
  {
    zzaas.setAdListener(paramAdListener);
    if ((paramAdListener != null) && ((paramAdListener instanceof zzxr)))
    {
      zzaas.zza((zzxr)paramAdListener);
      return;
    }
    if (paramAdListener == null) {
      zzaas.zza(null);
    }
  }
  
  public final void setAdMetadataListener(AdMetadataListener paramAdMetadataListener)
  {
    zzaas.setAdMetadataListener(paramAdMetadataListener);
  }
  
  public final void setAdUnitId(String paramString)
  {
    zzaas.setAdUnitId(paramString);
  }
  
  public final void setImmersiveMode(boolean paramBoolean)
  {
    zzaas.setImmersiveMode(paramBoolean);
  }
  
  public final void setRewardedVideoAdListener(RewardedVideoAdListener paramRewardedVideoAdListener)
  {
    zzaas.setRewardedVideoAdListener(paramRewardedVideoAdListener);
  }
  
  public final void show()
  {
    zzaas.show();
  }
}
