package com.google.android.gms.package_8.mediation;

import java.util.List;

@Deprecated
public class NativeAppInstallAdMapper
  extends NativeAdMapper
{
  private String zzdnh;
  private String zzenc;
  private List<com.google.android.gms.ads.formats.NativeAd.Image> zzend;
  private com.google.android.gms.package_8.formats.NativeAd.Image zzene;
  private String zzenf;
  private double zzeng;
  private String zzenh;
  private String zzeni;
  
  public NativeAppInstallAdMapper() {}
  
  public final String getBody()
  {
    return zzdnh;
  }
  
  public final String getCallToAction()
  {
    return zzenf;
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
  
  public final String getPrice()
  {
    return zzeni;
  }
  
  public final double getStarRating()
  {
    return zzeng;
  }
  
  public final String getStore()
  {
    return zzenh;
  }
  
  public final void setBody(String paramString)
  {
    zzdnh = paramString;
  }
  
  public final void setCallToAction(String paramString)
  {
    zzenf = paramString;
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
  
  public final void setPrice(String paramString)
  {
    zzeni = paramString;
  }
  
  public final void setStarRating(double paramDouble)
  {
    zzeng = paramDouble;
  }
  
  public final void setStore(String paramString)
  {
    zzenh = paramString;
  }
}
