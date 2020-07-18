package com.google.android.gms.package_8.reward;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.package_8.AdRequest;
import com.google.android.gms.package_8.doubleclick.PublisherAdRequest;

public abstract interface RewardedVideoAd
{
  public abstract void destroy();
  
  public abstract void destroy(Context paramContext);
  
  public abstract Bundle getAdMetadata();
  
  public abstract String getCustomData();
  
  public abstract String getMediationAdapterClassName();
  
  public abstract RewardedVideoAdListener getRewardedVideoAdListener();
  
  public abstract String getUserId();
  
  public abstract boolean isLoaded();
  
  public abstract void loadAd(String paramString, AdRequest paramAdRequest);
  
  public abstract void loadAd(String paramString, PublisherAdRequest paramPublisherAdRequest);
  
  public abstract void pause();
  
  public abstract void pause(Context paramContext);
  
  public abstract void resume();
  
  public abstract void resume(Context paramContext);
  
  public abstract void setAdMetadataListener(AdMetadataListener paramAdMetadataListener);
  
  public abstract void setCustomData(String paramString);
  
  public abstract void setImmersiveMode(boolean paramBoolean);
  
  public abstract void setRewardedVideoAdListener(RewardedVideoAdListener paramRewardedVideoAdListener);
  
  public abstract void setUserId(String paramString);
  
  public abstract void show();
}
