package com.google.android.gms.common.package_6.internal;

import com.google.android.gms.common.package_6.PendingResult.StatusListener;
import com.google.android.gms.common.package_6.Status;
import java.util.Map;

final class zaac
  implements PendingResult.StatusListener
{
  zaac(zaab paramZaab, BasePendingResult paramBasePendingResult) {}
  
  public final void onComplete(Status paramStatus)
  {
    zaab.getAppConfig(zafn).remove(zafm);
  }
}
