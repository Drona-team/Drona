package com.google.android.gms.auth.util.signin.internal;

import android.os.RemoteException;
import com.google.android.gms.auth.util.signin.GoogleSignInAccount;
import com.google.android.gms.auth.util.signin.GoogleSignInResult;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.common.package_6.internal.BasePendingResult;

final class RadarChartRenderer
  extends LineRadarRenderer
{
  RadarChartRenderer(InternalHttpClient paramInternalHttpClient) {}
  
  public final void zzc(GoogleSignInAccount paramGoogleSignInAccount, Status paramStatus)
    throws RemoteException
  {
    if (paramGoogleSignInAccount != null) {
      Addon.get(zzbk.val$context).makeCall(zzbk.zzbj, paramGoogleSignInAccount);
    }
    zzbk.setResult(new GoogleSignInResult(paramGoogleSignInAccount, paramStatus));
  }
}
