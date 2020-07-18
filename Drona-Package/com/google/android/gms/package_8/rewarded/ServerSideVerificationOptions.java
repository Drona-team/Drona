package com.google.android.gms.package_8.rewarded;

public class ServerSideVerificationOptions
{
  private final String zzdqs;
  private final String zzdqt;
  
  private ServerSideVerificationOptions(Builder paramBuilder)
  {
    zzdqs = Builder.getSoundPath(paramBuilder);
    zzdqt = Builder.getArticleUrl(paramBuilder);
  }
  
  public String getCustomData()
  {
    return zzdqt;
  }
  
  public String getUserId()
  {
    return zzdqs;
  }
  
  public final class Builder
  {
    private String zzdqs = "";
    private String zzdqt = "";
    
    public Builder() {}
    
    public final ServerSideVerificationOptions build()
    {
      return new ServerSideVerificationOptions(this, null);
    }
    
    public final Builder setCustomData(String paramString)
    {
      zzdqt = paramString;
      return this;
    }
    
    public final Builder setUserId(String paramString)
    {
      zzdqs = paramString;
      return this;
    }
  }
}
