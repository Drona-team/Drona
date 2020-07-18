package com.google.android.gms.package_8.internal.overlay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.ads.zzaqh;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.internal.ads.zzxr;
import com.google.android.gms.package_8.internal.UserData;

@zzard
public final class ActivityMain
  extends zzaqh
{
  private AdOverlayInfoParcel zzdkw;
  private boolean zzdkx = false;
  private boolean zzdky = false;
  private Activity zzzd;
  
  public ActivityMain(Activity paramActivity, AdOverlayInfoParcel paramAdOverlayInfoParcel)
  {
    zzdkw = paramAdOverlayInfoParcel;
    zzzd = paramActivity;
  }
  
  private final void zztp()
  {
    try
    {
      if (!zzdky)
      {
        if (zzdkw.zzdkm != null) {
          zzdkw.zzdkm.zzsz();
        }
        zzdky = true;
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    throws RemoteException
  {}
  
  public final void onBackPressed()
    throws RemoteException
  {}
  
  public final void onCreate(Bundle paramBundle)
  {
    int j = 0;
    int i = j;
    if (paramBundle != null)
    {
      i = j;
      if (paramBundle.getBoolean("com.google.android.gms.ads.internal.overlay.hasResumed", false)) {
        i = 1;
      }
    }
    if (zzdkw == null)
    {
      zzzd.finish();
      return;
    }
    if (i != 0)
    {
      zzzd.finish();
      return;
    }
    if (paramBundle == null)
    {
      if (zzdkw.zzcgi != null) {
        zzdkw.zzcgi.onAdClicked();
      }
      if ((zzzd.getIntent() != null) && (zzzd.getIntent().getBooleanExtra("shouldCallOnOverlayOpened", true)) && (zzdkw.zzdkm != null)) {
        zzdkw.zzdkm.zzta();
      }
    }
    UserData.zzle();
    if (!RequestQueue.start(zzzd, zzdkw.zzdkl, zzdkw.zzdkq)) {
      zzzd.finish();
    }
  }
  
  public final void onDestroy()
    throws RemoteException
  {
    if (zzzd.isFinishing()) {
      zztp();
    }
  }
  
  public final void onPause()
    throws RemoteException
  {
    if (zzdkw.zzdkm != null) {
      zzdkw.zzdkm.onPause();
    }
    if (zzzd.isFinishing()) {
      zztp();
    }
  }
  
  public final void onRestart()
    throws RemoteException
  {}
  
  public final void onResume()
    throws RemoteException
  {
    if (zzdkx)
    {
      zzzd.finish();
      return;
    }
    zzdkx = true;
    if (zzdkw.zzdkm != null) {
      zzdkw.zzdkm.onResume();
    }
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
    throws RemoteException
  {
    paramBundle.putBoolean("com.google.android.gms.ads.internal.overlay.hasResumed", zzdkx);
  }
  
  public final void onStart()
    throws RemoteException
  {}
  
  public final void onStop()
    throws RemoteException
  {
    if (zzzd.isFinishing()) {
      zztp();
    }
  }
  
  public final void zzac(IObjectWrapper paramIObjectWrapper)
    throws RemoteException
  {}
  
  public final void zzdd()
    throws RemoteException
  {}
  
  public final boolean zztg()
    throws RemoteException
  {
    return false;
  }
}
