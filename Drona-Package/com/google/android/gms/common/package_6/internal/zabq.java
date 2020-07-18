package com.google.android.gms.common.package_6.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class zabq
  extends BroadcastReceiver
{
  private Context mContext;
  private final zabr zaji;
  
  public zabq(zabr paramZabr)
  {
    zaji = paramZabr;
  }
  
  public final void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext = paramIntent.getData();
    if (paramContext != null) {
      paramContext = paramContext.getSchemeSpecificPart();
    } else {
      paramContext = null;
    }
    if ("com.google.android.gms".equals(paramContext))
    {
      zaji.cancel();
      unregister();
    }
  }
  
  public final void unregister()
  {
    try
    {
      if (mContext != null) {
        mContext.unregisterReceiver(this);
      }
      mContext = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void unregister(Context paramContext)
  {
    mContext = paramContext;
  }
}
