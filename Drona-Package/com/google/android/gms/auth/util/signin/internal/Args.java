package com.google.android.gms.auth.util.signin.internal;

import android.os.RemoteException;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.common.package_6.internal.BasePendingResult;

final class Args
  extends LineRadarRenderer
{
  Args(AbstractHttpClient paramAbstractHttpClient) {}
  
  public final void zze(Status paramStatus)
    throws RemoteException
  {
    zzbl.setResult(paramStatus);
  }
}
