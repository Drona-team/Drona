package com.google.android.gms.package_8.formats;

import android.os.Bundle;
import com.google.android.gms.package_8.MuteThisAdListener;
import com.google.android.gms.package_8.MuteThisAdReason;
import com.google.android.gms.package_8.VideoController;
import java.util.List;

public abstract class UnifiedNativeAd
{
  public UnifiedNativeAd() {}
  
  public abstract void cancelUnconfirmedClick();
  
  public abstract void destroy();
  
  public abstract void enableCustomClickGesture();
  
  public abstract NativeAd.AdChoicesInfo getAdChoicesInfo();
  
  public abstract String getAdvertiser();
  
  public abstract String getBody();
  
  public abstract String getCallToAction();
  
  public abstract Bundle getExtras();
  
  public abstract String getHeadline();
  
  public abstract NativeAd.Image getIcon();
  
  public abstract List getImages();
  
  public abstract String getMediationAdapterClassName();
  
  public abstract List getMuteThisAdReasons();
  
  public abstract String getPrice();
  
  public abstract Double getStarRating();
  
  public abstract String getStore();
  
  public abstract VideoController getVideoController();
  
  public abstract boolean isCustomMuteThisAdEnabled();
  
  public abstract void muteThisAd(MuteThisAdReason paramMuteThisAdReason);
  
  public abstract void performClick(Bundle paramBundle);
  
  public abstract void recordCustomClickGesture();
  
  public abstract boolean recordImpression(Bundle paramBundle);
  
  public abstract void reportTouchEvent(Bundle paramBundle);
  
  public abstract void setMuteThisAdListener(MuteThisAdListener paramMuteThisAdListener);
  
  public abstract void setUnconfirmedClickListener(UnconfirmedClickListener paramUnconfirmedClickListener);
  
  protected abstract Object zzkq();
  
  public abstract Object zzkv();
  
  public abstract interface OnUnifiedNativeAdLoadedListener
  {
    public abstract void onUnifiedNativeAdLoaded(UnifiedNativeAd paramUnifiedNativeAd);
  }
  
  public abstract interface UnconfirmedClickListener
  {
    public abstract void onUnconfirmedClickCancelled();
    
    public abstract void onUnconfirmedClickReceived(String paramString);
  }
  
  public class zza
  {
    public zza() {}
  }
}
