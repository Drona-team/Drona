package com.google.android.gms.package_8.mediation.supplement;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.package_8.AdSize;
import com.google.android.gms.package_8.mediation.MediationConfiguration;
import javax.annotation.Nullable;

public class RtbSignalData
{
  private final Bundle zzcgv;
  @Nullable
  private final AdSize zzdh;
  private final MediationConfiguration zzeoc;
  private final Context zzlj;
  
  public RtbSignalData(Context paramContext, MediationConfiguration paramMediationConfiguration, Bundle paramBundle, AdSize paramAdSize)
  {
    zzlj = paramContext;
    zzeoc = paramMediationConfiguration;
    zzcgv = paramBundle;
    zzdh = paramAdSize;
  }
  
  public AdSize getAdSize()
  {
    return zzdh;
  }
  
  public MediationConfiguration getConfiguration()
  {
    return zzeoc;
  }
  
  public Context getContext()
  {
    return zzlj;
  }
  
  public Bundle getNetworkExtras()
  {
    return zzcgv;
  }
}
