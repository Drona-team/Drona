package com.google.android.gms.package_8.mediation;

import android.os.BaseBundle;
import android.os.Bundle;

public abstract interface MediationAdapter
  extends MediationExtrasReceiver
{
  public abstract void onDestroy();
  
  public abstract void onPause();
  
  public abstract void onResume();
  
  public final class zza
  {
    private int zzemy;
    
    public zza() {}
    
    public final Bundle zzacc()
    {
      Bundle localBundle = new Bundle();
      localBundle.putInt("capabilities", zzemy);
      return localBundle;
    }
    
    public final zza zzdj(int paramInt)
    {
      zzemy = 1;
      return this;
    }
  }
}
