package com.google.android.gms.package_8.mediation;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MediationAdConfiguration
{
  public static final int TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE = 0;
  public static final int TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE = 1;
  public static final int TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED = -1;
  private final String zzchd;
  private final int zzdga;
  private final String zzemu;
  private final Bundle zzemv;
  private final Bundle zzemw;
  private final int zzemx;
  private final Context zzlj;
  private final boolean zzmv;
  private final Location zzmw;
  
  public MediationAdConfiguration(Context paramContext, String paramString1, Bundle paramBundle1, Bundle paramBundle2, boolean paramBoolean, Location paramLocation, int paramInt1, int paramInt2, String paramString2)
  {
    zzemu = paramString1;
    zzemv = paramBundle1;
    zzemw = paramBundle2;
    zzlj = paramContext;
    zzmv = paramBoolean;
    zzmw = paramLocation;
    zzdga = paramInt1;
    zzemx = paramInt2;
    zzchd = paramString2;
  }
  
  public String getBidResponse()
  {
    return zzemu;
  }
  
  public Context getContext()
  {
    return zzlj;
  }
  
  public Location getLocation()
  {
    return zzmw;
  }
  
  public String getMaxAdContentRating()
  {
    return zzchd;
  }
  
  public Bundle getMediationExtras()
  {
    return zzemw;
  }
  
  public Bundle getServerParameters()
  {
    return zzemv;
  }
  
  public boolean isTestRequest()
  {
    return zzmv;
  }
  
  public int taggedForChildDirectedTreatment()
  {
    return zzdga;
  }
  
  public int taggedForUnderAgeTreatment()
  {
    return zzemx;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public @interface TagForChildDirectedTreatment {}
}
