package com.google.android.gms.package_8.mediation;

import java.util.List;

@Deprecated
public class NativeContentAdMapper
  extends NativeAdMapper
{
  private String zzdnh;
  private String zzenc;
  private List<com.google.android.gms.ads.formats.NativeAd.Image> zzend;
  private String zzenf;
  private com.google.android.gms.package_8.formats.NativeAd.Image zzenj;
  private String zzenk;
  
  public NativeContentAdMapper() {}
  
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
  
  public final String getHeadline()
  {
    return zzenc;
  }
  
  public final List getImages()
  {
    return zzend;
  }
  
  public final com.google.android.gms.package_8.formats.NativeAd.Image getLogo()
  {
    return zzenj;
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
  
  public final void setHeadline(String paramString)
  {
    zzenc = paramString;
  }
  
  public final void setImages(List paramList)
  {
    zzend = paramList;
  }
  
  public final void setLogo(com.google.android.gms.package_8.formats.NativeAd.Image paramImage)
  {
    zzenj = paramImage;
  }
}
