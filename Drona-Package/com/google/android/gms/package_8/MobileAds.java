package com.google.android.gms.package_8;

import android.content.Context;
import com.google.android.gms.internal.ads.zzabe;
import com.google.android.gms.internal.ads.zzabi;
import com.google.android.gms.package_8.reward.RewardedVideoAd;

public class MobileAds
{
  private MobileAds() {}
  
  public static RewardedVideoAd getRewardedVideoAdInstance(Context paramContext)
  {
    return zzabe.zzqf().getRewardedVideoAdInstance(paramContext);
  }
  
  public static String getVersionString()
  {
    return zzabe.zzqf().getVersionString();
  }
  
  public static void initialize(Context paramContext)
  {
    initialize(paramContext, null, null);
  }
  
  public static void initialize(Context paramContext, String paramString)
  {
    initialize(paramContext, paramString, null);
  }
  
  public static void initialize(Context paramContext, String paramString, Settings paramSettings)
  {
    zzabe localZzabe = zzabe.zzqf();
    if (paramSettings == null) {
      paramSettings = null;
    } else {
      paramSettings = paramSettings.zzdg();
    }
    localZzabe.zza(paramContext, paramString, paramSettings, null);
  }
  
  public static void openDebugMenu(Context paramContext, String paramString)
  {
    zzabe.zzqf().openDebugMenu(paramContext, paramString);
  }
  
  public static void registerRtbAdapter(Class paramClass)
  {
    zzabe.zzqf().registerRtbAdapter(paramClass);
  }
  
  public static void setAppMuted(boolean paramBoolean)
  {
    zzabe.zzqf().setAppMuted(paramBoolean);
  }
  
  public static void setAppVolume(float paramFloat)
  {
    zzabe.zzqf().setAppVolume(paramFloat);
  }
  
  public final class Settings
  {
    private final zzabi zzaat = new zzabi();
    
    public Settings() {}
    
    public final String getTrackingId()
    {
      return null;
    }
    
    public final boolean isGoogleAnalyticsEnabled()
    {
      return false;
    }
    
    public final Settings setGoogleAnalyticsEnabled(boolean paramBoolean)
    {
      return this;
    }
    
    public final Settings setTrackingId(String paramString)
    {
      return this;
    }
    
    final zzabi zzdg()
    {
      return zzaat;
    }
  }
}
