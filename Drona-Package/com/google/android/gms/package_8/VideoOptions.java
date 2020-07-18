package com.google.android.gms.package_8;

import com.google.android.gms.internal.ads.zzacd;
import com.google.android.gms.internal.ads.zzard;

@zzard
public final class VideoOptions
{
  private final boolean zzaax;
  private final boolean zzaay;
  private final boolean zzaaz;
  
  public VideoOptions(zzacd paramZzacd)
  {
    zzaax = zzaax;
    zzaay = zzaay;
    zzaaz = zzaaz;
  }
  
  private VideoOptions(Builder paramBuilder)
  {
    zzaax = Builder.s(paramBuilder);
    zzaay = Builder.newFromParcel(paramBuilder);
    zzaaz = Builder.getSoundPath(paramBuilder);
  }
  
  public final boolean getClickToExpandRequested()
  {
    return zzaaz;
  }
  
  public final boolean getCustomControlsRequested()
  {
    return zzaay;
  }
  
  public final boolean getStartMuted()
  {
    return zzaax;
  }
  
  public final class Builder
  {
    private boolean zzaax = true;
    private boolean zzaay = false;
    private boolean zzaaz = false;
    
    public Builder() {}
    
    public final VideoOptions build()
    {
      return new VideoOptions(this, null);
    }
    
    public final Builder setClickToExpandRequested(boolean paramBoolean)
    {
      zzaaz = paramBoolean;
      return this;
    }
    
    public final Builder setCustomControlsRequested(boolean paramBoolean)
    {
      zzaay = paramBoolean;
      return this;
    }
    
    public final Builder setStartMuted(boolean paramBoolean)
    {
      zzaax = paramBoolean;
      return this;
    }
  }
}
