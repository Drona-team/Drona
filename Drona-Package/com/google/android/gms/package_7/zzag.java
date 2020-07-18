package com.google.android.gms.package_7;

import android.os.Looper;
import android.os.Message;
import com.google.android.gms.internal.gcm.zzj;

final class zzag
  extends zzj
{
  zzag(zzaf paramZzaf, Looper paramLooper)
  {
    super(paramLooper);
  }
  
  public final void handleMessage(Message paramMessage)
  {
    zzdc.processMessage(paramMessage);
  }
}
