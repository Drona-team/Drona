package com.google.android.gms.common.package_6.internal;

import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.internal.base.zap;

final class zabg
  extends zap
{
  zabg(zabe paramZabe, Looper paramLooper)
  {
    super(paramLooper);
  }
  
  public final void handleMessage(Message paramMessage)
  {
    switch (what)
    {
    default: 
      int i = what;
      paramMessage = new StringBuilder(31);
      paramMessage.append("Unknown message id: ");
      paramMessage.append(i);
      Log.w("GACStateManager", paramMessage.toString());
      return;
    case 2: 
      throw ((RuntimeException)obj);
    }
    ((zabf)obj).removeFile(zahv);
  }
}
