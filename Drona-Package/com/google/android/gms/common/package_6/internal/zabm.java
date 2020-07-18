package com.google.android.gms.common.package_6.internal;

import android.os.Handler;
import com.google.android.gms.common.internal.BaseGmsClient.SignOutCallbacks;

final class zabm
  implements BaseGmsClient.SignOutCallbacks
{
  zabm(GoogleApiManager.zaa paramZaa) {}
  
  public final void onSignOutComplete()
  {
    GoogleApiManager.access$getHandler(zaiy.zaim).post(new zabn(this));
  }
}
