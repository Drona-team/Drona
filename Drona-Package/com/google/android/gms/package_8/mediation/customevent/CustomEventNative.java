package com.google.android.gms.package_8.mediation.customevent;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.package_8.mediation.NativeMediationAdRequest;

public abstract interface CustomEventNative
  extends CustomEvent
{
  public abstract void requestNativeAd(Context paramContext, CustomEventNativeListener paramCustomEventNativeListener, String paramString, NativeMediationAdRequest paramNativeMediationAdRequest, Bundle paramBundle);
}
