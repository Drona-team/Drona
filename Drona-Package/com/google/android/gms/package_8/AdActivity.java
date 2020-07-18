package com.google.android.gms.package_8;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.google.android.gms.common.annotation.KeepForSdkWithMembers;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.internal.ads.zzaqg;
import com.google.android.gms.internal.ads.zzbad;
import com.google.android.gms.internal.ads.zzyh;
import com.google.android.gms.internal.ads.zzyt;

@KeepForSdkWithMembers
public class AdActivity
  extends Activity
{
  public static final String CLASS_NAME = "com.google.android.gms.ads.AdActivity";
  public static final String SIMPLE_CLASS_NAME = "AdActivity";
  private zzaqg zzaah;
  
  public AdActivity() {}
  
  private final void zzdd()
  {
    if (zzaah != null)
    {
      zzaqg localZzaqg = zzaah;
      try
      {
        localZzaqg.zzdd();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        zzbad.zze("#007 Could not call remote method.", localRemoteException);
      }
    }
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    zzaqg localZzaqg = zzaah;
    try
    {
      localZzaqg.onActivityResult(paramInt1, paramInt2, paramIntent);
    }
    catch (Exception localException)
    {
      zzbad.zze("#007 Could not call remote method.", localException);
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void onBackPressed()
  {
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (zzaah != null)
    {
      zzaqg localZzaqg = zzaah;
      try
      {
        bool1 = localZzaqg.zztg();
      }
      catch (RemoteException localRemoteException)
      {
        zzbad.zze("#007 Could not call remote method.", localRemoteException);
        bool1 = bool2;
      }
    }
    if (bool1) {
      super.onBackPressed();
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    zzaqg localZzaqg = zzaah;
    try
    {
      localZzaqg.zzac(ObjectWrapper.wrap(paramConfiguration));
      return;
    }
    catch (RemoteException paramConfiguration)
    {
      zzbad.zze("#007 Could not call remote method.", paramConfiguration);
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    zzaah = zzyt.zzpb().zzb(this);
    if (zzaah == null)
    {
      zzbad.zze("#007 Could not call remote method.", null);
      finish();
      return;
    }
    zzaqg localZzaqg = zzaah;
    try
    {
      localZzaqg.onCreate(paramBundle);
      return;
    }
    catch (RemoteException paramBundle)
    {
      zzbad.zze("#007 Could not call remote method.", paramBundle);
      finish();
    }
  }
  
  protected void onDestroy()
  {
    if (zzaah != null)
    {
      zzaqg localZzaqg = zzaah;
      try
      {
        localZzaqg.onDestroy();
      }
      catch (RemoteException localRemoteException)
      {
        zzbad.zze("#007 Could not call remote method.", localRemoteException);
      }
    }
    super.onDestroy();
  }
  
  protected void onPause()
  {
    if (zzaah != null)
    {
      zzaqg localZzaqg = zzaah;
      try
      {
        localZzaqg.onPause();
      }
      catch (RemoteException localRemoteException)
      {
        zzbad.zze("#007 Could not call remote method.", localRemoteException);
        finish();
      }
    }
    super.onPause();
  }
  
  protected void onRestart()
  {
    super.onRestart();
    if (zzaah != null)
    {
      zzaqg localZzaqg = zzaah;
      try
      {
        localZzaqg.onRestart();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        zzbad.zze("#007 Could not call remote method.", localRemoteException);
        finish();
      }
    }
  }
  
  protected void onResume()
  {
    super.onResume();
    if (zzaah != null)
    {
      zzaqg localZzaqg = zzaah;
      try
      {
        localZzaqg.onResume();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        zzbad.zze("#007 Could not call remote method.", localRemoteException);
        finish();
      }
    }
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    if (zzaah != null)
    {
      zzaqg localZzaqg = zzaah;
      try
      {
        localZzaqg.onSaveInstanceState(paramBundle);
      }
      catch (RemoteException localRemoteException)
      {
        zzbad.zze("#007 Could not call remote method.", localRemoteException);
        finish();
      }
    }
    super.onSaveInstanceState(paramBundle);
  }
  
  protected void onStart()
  {
    super.onStart();
    if (zzaah != null)
    {
      zzaqg localZzaqg = zzaah;
      try
      {
        localZzaqg.onStart();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        zzbad.zze("#007 Could not call remote method.", localRemoteException);
        finish();
      }
    }
  }
  
  protected void onStop()
  {
    if (zzaah != null)
    {
      zzaqg localZzaqg = zzaah;
      try
      {
        localZzaqg.onStop();
      }
      catch (RemoteException localRemoteException)
      {
        zzbad.zze("#007 Could not call remote method.", localRemoteException);
        finish();
      }
    }
    super.onStop();
  }
  
  public void setContentView(int paramInt)
  {
    super.setContentView(paramInt);
    zzdd();
  }
  
  public void setContentView(View paramView)
  {
    super.setContentView(paramView);
    zzdd();
  }
  
  public void setContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    super.setContentView(paramView, paramLayoutParams);
    zzdd();
  }
}
