package com.google.android.gms.package_7;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

final class zzah
  extends BroadcastReceiver
{
  zzah(zzaf paramZzaf) {}
  
  public final void onReceive(Context paramContext, Intent paramIntent)
  {
    if (Log.isLoggable("InstanceID", 3)) {
      Log.d("InstanceID", "Received GSF callback via dynamic receiver");
    }
    zzdc.parse(paramIntent);
  }
}
