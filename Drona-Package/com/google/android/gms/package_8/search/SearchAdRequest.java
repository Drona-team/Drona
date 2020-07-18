package com.google.android.gms.package_8.search;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.internal.ads.zzaaz;
import com.google.android.gms.internal.ads.zzaba;
import com.google.android.gms.package_8.mediation.NetworkExtras;

@Deprecated
public final class SearchAdRequest
{
  public static final int BORDER_TYPE_DASHED = 1;
  public static final int BORDER_TYPE_DOTTED = 2;
  public static final int BORDER_TYPE_NONE = 0;
  public static final int BORDER_TYPE_SOLID = 3;
  public static final int CALL_BUTTON_COLOR_DARK = 2;
  public static final int CALL_BUTTON_COLOR_LIGHT = 0;
  public static final int CALL_BUTTON_COLOR_MEDIUM = 1;
  public static final String DEVICE_ID_EMULATOR = "B3EEABB8EE11C2BE770B684D95219ECB";
  public static final int ERROR_CODE_INTERNAL_ERROR = 0;
  public static final int ERROR_CODE_INVALID_REQUEST = 1;
  public static final int ERROR_CODE_NETWORK_ERROR = 2;
  public static final int ERROR_CODE_NO_FILL = 3;
  private final zzaaz zzaam;
  private final String zzckf;
  
  private SearchAdRequest(Builder paramBuilder)
  {
    zzckf = Builder.getArticleUrl(paramBuilder);
    zzaam = new zzaaz(Builder.getSoundPath(paramBuilder), this);
  }
  
  public final int getAnchorTextColor()
  {
    return 0;
  }
  
  public final int getBackgroundColor()
  {
    return 0;
  }
  
  public final int getBackgroundGradientBottom()
  {
    return 0;
  }
  
  public final int getBackgroundGradientTop()
  {
    return 0;
  }
  
  public final int getBorderColor()
  {
    return 0;
  }
  
  public final int getBorderThickness()
  {
    return 0;
  }
  
  public final int getBorderType()
  {
    return 0;
  }
  
  public final int getCallButtonColor()
  {
    return 0;
  }
  
  public final String getCustomChannels()
  {
    return null;
  }
  
  public final Bundle getCustomEventExtrasBundle(Class paramClass)
  {
    return zzaam.getCustomEventExtrasBundle(paramClass);
  }
  
  public final int getDescriptionTextColor()
  {
    return 0;
  }
  
  public final String getFontFace()
  {
    return null;
  }
  
  public final int getHeaderTextColor()
  {
    return 0;
  }
  
  public final int getHeaderTextSize()
  {
    return 0;
  }
  
  public final Location getLocation()
  {
    return zzaam.getLocation();
  }
  
  public final NetworkExtras getNetworkExtras(Class paramClass)
  {
    return zzaam.getNetworkExtras(paramClass);
  }
  
  public final Bundle getNetworkExtrasBundle(Class paramClass)
  {
    return zzaam.getNetworkExtrasBundle(paramClass);
  }
  
  public final String getQuery()
  {
    return zzckf;
  }
  
  public final boolean isTestDevice(Context paramContext)
  {
    return zzaam.isTestDevice(paramContext);
  }
  
  final zzaaz zzde()
  {
    return zzaam;
  }
  
  public final class Builder
  {
    private final zzaba zzaan = new zzaba();
    private String zzckf;
    
    public Builder() {}
    
    public final Builder addCustomEventExtrasBundle(Class paramClass, Bundle paramBundle)
    {
      zzaan.zzb(paramClass, paramBundle);
      return this;
    }
    
    public final Builder addNetworkExtras(NetworkExtras paramNetworkExtras)
    {
      zzaan.zza(paramNetworkExtras);
      return this;
    }
    
    public final Builder addNetworkExtrasBundle(Class paramClass, Bundle paramBundle)
    {
      zzaan.zza(paramClass, paramBundle);
      return this;
    }
    
    public final Builder addTestDevice(String paramString)
    {
      zzaan.zzbx(paramString);
      return this;
    }
    
    public final SearchAdRequest build()
    {
      return new SearchAdRequest(this, null);
    }
    
    public final Builder setAnchorTextColor(int paramInt)
    {
      return this;
    }
    
    public final Builder setBackgroundColor(int paramInt)
    {
      return this;
    }
    
    public final Builder setBackgroundGradient(int paramInt1, int paramInt2)
    {
      return this;
    }
    
    public final Builder setBorderColor(int paramInt)
    {
      return this;
    }
    
    public final Builder setBorderThickness(int paramInt)
    {
      return this;
    }
    
    public final Builder setBorderType(int paramInt)
    {
      return this;
    }
    
    public final Builder setCallButtonColor(int paramInt)
    {
      return this;
    }
    
    public final Builder setCustomChannels(String paramString)
    {
      return this;
    }
    
    public final Builder setDescriptionTextColor(int paramInt)
    {
      return this;
    }
    
    public final Builder setFontFace(String paramString)
    {
      return this;
    }
    
    public final Builder setHeaderTextColor(int paramInt)
    {
      return this;
    }
    
    public final Builder setHeaderTextSize(int paramInt)
    {
      return this;
    }
    
    public final Builder setLocation(Location paramLocation)
    {
      zzaan.zza(paramLocation);
      return this;
    }
    
    public final Builder setQuery(String paramString)
    {
      zzckf = paramString;
      return this;
    }
    
    public final Builder setRequestAgent(String paramString)
    {
      zzaan.zzcb(paramString);
      return this;
    }
    
    public final Builder tagForChildDirectedTreatment(boolean paramBoolean)
    {
      zzaan.zzt(paramBoolean);
      return this;
    }
  }
}
