package com.google.android.gms.package_8.rewarded;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.ads.zzaug;
import com.google.android.gms.package_8.AdRequest;
import com.google.android.gms.package_8.doubleclick.PublisherAdRequest;

public final class RewardedAd
{
  private final zzaug zzgnu;
  
  public RewardedAd(Context paramContext, String paramString)
  {
    Preconditions.checkNotNull(paramContext, "context cannot be null");
    Preconditions.checkNotNull(paramString, "adUnitID cannot be null");
    zzgnu = new zzaug(paramContext, paramString);
  }
  
  public final Bundle getAdMetadata()
  {
    return zzgnu.getAdMetadata();
  }
  
  public final String getMediationAdapterClassName()
  {
    return zzgnu.getMediationAdapterClassName();
  }
  
  public final RewardItem getRewardItem()
  {
    return zzgnu.getRewardItem();
  }
  
  public final boolean isLoaded()
  {
    return zzgnu.isLoaded();
  }
  
  public final void loadAd(AdRequest paramAdRequest, RewardedAdLoadCallback paramRewardedAdLoadCallback)
  {
    zzgnu.zza(paramAdRequest.zzde(), paramRewardedAdLoadCallback);
  }
  
  public final void loadAd(PublisherAdRequest paramPublisherAdRequest, RewardedAdLoadCallback paramRewardedAdLoadCallback)
  {
    zzgnu.zza(paramPublisherAdRequest.zzde(), paramRewardedAdLoadCallback);
  }
  
  public final void setOnAdMetadataChangedListener(OnAdMetadataChangedListener paramOnAdMetadataChangedListener)
  {
    zzgnu.setOnAdMetadataChangedListener(paramOnAdMetadataChangedListener);
  }
  
  public final void setServerSideVerificationOptions(ServerSideVerificationOptions paramServerSideVerificationOptions)
  {
    zzgnu.setServerSideVerificationOptions(paramServerSideVerificationOptions);
  }
  
  public final void show(Activity paramActivity, RewardedAdCallback paramRewardedAdCallback)
  {
    zzgnu.show(paramActivity, paramRewardedAdCallback);
  }
  
  public final void show(Activity paramActivity, RewardedAdCallback paramRewardedAdCallback, boolean paramBoolean)
  {
    zzgnu.show(paramActivity, paramRewardedAdCallback, paramBoolean);
  }
}
