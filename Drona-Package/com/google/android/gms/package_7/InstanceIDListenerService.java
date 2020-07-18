package com.google.android.gms.package_7;

import android.content.Context;
import android.content.Intent;
import android.os.BaseBundle;
import android.os.Bundle;
import android.util.Log;

@Deprecated
public class InstanceIDListenerService
  extends IRCService
{
  public InstanceIDListenerService() {}
  
  static void send(Context paramContext, zzak paramZzak)
  {
    paramZzak.clear();
    paramZzak = new Intent("com.google.android.gms.iid.InstanceID");
    paramZzak.putExtra("CMD", "RST");
    paramZzak.setClassName(paramContext, "com.google.android.gms.gcm.GcmReceiver");
    paramContext.sendBroadcast(paramZzak);
  }
  
  public void handleIntent(Intent paramIntent)
  {
    if (!"com.google.android.gms.iid.InstanceID".equals(paramIntent.getAction())) {
      return;
    }
    Object localObject1 = null;
    String str = paramIntent.getStringExtra("subtype");
    if (str != null)
    {
      localObject1 = new Bundle();
      ((BaseBundle)localObject1).putString("subtype", str);
    }
    localObject1 = InstanceID.getInstance(this, (Bundle)localObject1);
    paramIntent = paramIntent.getStringExtra("CMD");
    Object localObject2;
    if (Log.isLoggable("InstanceID", 3))
    {
      localObject2 = new StringBuilder(String.valueOf(str).length() + 34 + String.valueOf(paramIntent).length());
      ((StringBuilder)localObject2).append("Service command. subtype:");
      ((StringBuilder)localObject2).append(str);
      ((StringBuilder)localObject2).append(" command:");
      ((StringBuilder)localObject2).append(paramIntent);
      Log.d("InstanceID", ((StringBuilder)localObject2).toString());
    }
    if ("RST".equals(paramIntent))
    {
      ((InstanceID)localObject1).setList();
      onTokenRefresh();
      return;
    }
    if ("RST_FULL".equals(paramIntent))
    {
      if (!InstanceID.getValues().isEmpty())
      {
        InstanceID.getValues().clear();
        onTokenRefresh();
      }
    }
    else if ("SYNC".equals(paramIntent))
    {
      localObject1 = InstanceID.getValues();
      paramIntent = String.valueOf(str);
      localObject2 = String.valueOf("|T|");
      if (((String)localObject2).length() != 0) {
        paramIntent = paramIntent.concat((String)localObject2);
      } else {
        paramIntent = new String(paramIntent);
      }
      ((zzak)localObject1).delete(paramIntent);
      paramIntent = String.valueOf(str);
      str = String.valueOf("|T-timestamp|");
      if (str.length() != 0) {
        paramIntent = paramIntent.concat(str);
      } else {
        paramIntent = new String(paramIntent);
      }
      ((zzak)localObject1).delete(paramIntent);
      onTokenRefresh();
    }
  }
  
  public void onTokenRefresh() {}
}
