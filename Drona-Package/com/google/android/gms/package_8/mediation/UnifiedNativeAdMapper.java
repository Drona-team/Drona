package com.google.android.gms.package_8.mediation;

import android.os.Bundle;
import android.view.View;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.package_8.VideoController;
import java.util.List;
import java.util.Map;

@zzard
public class UnifiedNativeAdMapper
{
  private Bundle extras = new Bundle();
  private VideoController zzcje;
  private String zzdnh;
  private View zzena;
  private boolean zzenb;
  private String zzenc;
  private List<com.google.android.gms.ads.formats.NativeAd.Image> zzend;
  private com.google.android.gms.package_8.formats.NativeAd.Image zzene;
  private String zzenf;
  private String zzenh;
  private String zzeni;
  private String zzenk;
  private Double zzenl;
  private View zzenm;
  private Object zzenn;
  private boolean zzeno;
  private boolean zzenp;
  
  public UnifiedNativeAdMapper() {}
  
  public View getAdChoicesContent()
  {
    return zzenm;
  }
  
  public final String getAdvertiser()
  {
    return zzenk;
  }
  
  public final String getBody()
  {
    return zzdnh;
  }
  
  public final String getCallToAction()
  {
    return zzenf;
  }
  
  public final Bundle getExtras()
  {
    return extras;
  }
  
  public final String getHeadline()
  {
    return zzenc;
  }
  
  public final com.google.android.gms.package_8.formats.NativeAd.Image getIcon()
  {
    return zzene;
  }
  
  public final List getImages()
  {
    return zzend;
  }
  
  public final boolean getOverrideClickHandling()
  {
    return zzenp;
  }
  
  public final boolean getOverrideImpressionRecording()
  {
    return zzeno;
  }
  
  public final String getPrice()
  {
    return zzeni;
  }
  
  public final Double getStarRating()
  {
    return zzenl;
  }
  
  public final String getStore()
  {
    return zzenh;
  }
  
  public final VideoController getVideoController()
  {
    return zzcje;
  }
  
  public void handleClick(View paramView) {}
  
  public boolean hasVideoContent()
  {
    return zzenb;
  }
  
  public void recordImpression() {}
  
  public void setAdChoicesContent(View paramView)
  {
    zzenm = paramView;
  }
  
  public final void setAdvertiser(String paramString)
  {
    zzenk = paramString;
  }
  
  public final void setBody(String paramString)
  {
    zzdnh = paramString;
  }
  
  public final void setCallToAction(String paramString)
  {
    zzenf = paramString;
  }
  
  public final void setExpandableListAdapter(VideoController paramVideoController)
  {
    zzcje = paramVideoController;
  }
  
  public final void setExtras(Bundle paramBundle)
  {
    extras = paramBundle;
  }
  
  public void setHasVideoContent(boolean paramBoolean)
  {
    zzenb = paramBoolean;
  }
  
  public final void setHeadline(String paramString)
  {
    zzenc = paramString;
  }
  
  public final void setIcon(com.google.android.gms.package_8.formats.NativeAd.Image paramImage)
  {
    zzene = paramImage;
  }
  
  public final void setImages(List paramList)
  {
    zzend = paramList;
  }
  
  public void setMediaView(View paramView)
  {
    zzena = paramView;
  }
  
  public final void setOverrideClickHandling(boolean paramBoolean)
  {
    zzenp = paramBoolean;
  }
  
  public final void setOverrideImpressionRecording(boolean paramBoolean)
  {
    zzeno = paramBoolean;
  }
  
  public final void setPrice(String paramString)
  {
    zzeni = paramString;
  }
  
  public final void setStarRating(Double paramDouble)
  {
    zzenl = paramDouble;
  }
  
  public final void setStore(String paramString)
  {
    zzenh = paramString;
  }
  
  public void trackViews(View paramView, Map paramMap1, Map paramMap2) {}
  
  public void untrackView(View paramView) {}
  
  public final void writeShort(Object paramObject)
  {
    zzenn = paramObject;
  }
  
  public final View zzacd()
  {
    return zzena;
  }
  
  public final Object zzkv()
  {
    return zzenn;
  }
}
