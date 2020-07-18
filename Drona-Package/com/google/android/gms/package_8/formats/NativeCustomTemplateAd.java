package com.google.android.gms.package_8.formats;

import com.google.android.gms.package_8.VideoController;
import java.util.List;

public abstract interface NativeCustomTemplateAd
{
  public static final String ASSET_NAME_VIDEO = "_videoMediaView";
  
  public abstract void destroy();
  
  public abstract List getAvailableAssetNames();
  
  public abstract String getCustomTemplateId();
  
  public abstract NativeAd.Image getImage(String paramString);
  
  public abstract CharSequence getText(String paramString);
  
  public abstract VideoController getVideoController();
  
  public abstract MediaView getVideoMediaView();
  
  public abstract void performClick(String paramString);
  
  public abstract void recordImpression();
  
  public abstract interface OnCustomClickListener
  {
    public abstract void onCustomClick(NativeCustomTemplateAd paramNativeCustomTemplateAd, String paramString);
  }
  
  public abstract interface OnCustomTemplateAdLoadedListener
  {
    public abstract void onCustomTemplateAdLoaded(NativeCustomTemplateAd paramNativeCustomTemplateAd);
  }
}
