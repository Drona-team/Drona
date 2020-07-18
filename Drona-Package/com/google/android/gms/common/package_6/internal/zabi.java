package com.google.android.gms.common.package_6.internal;

import android.os.Handler;

final class zabi
  implements BackgroundDetector.BackgroundStateChangeListener
{
  zabi(GoogleApiManager paramGoogleApiManager) {}
  
  public final void onBackgroundStateChanged(boolean paramBoolean)
  {
    GoogleApiManager.access$getHandler(zaim).sendMessage(GoogleApiManager.access$getHandler(zaim).obtainMessage(1, Boolean.valueOf(paramBoolean)));
  }
}
