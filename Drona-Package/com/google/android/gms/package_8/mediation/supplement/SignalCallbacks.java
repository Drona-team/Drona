package com.google.android.gms.package_8.mediation.supplement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract interface SignalCallbacks
{
  public abstract void onFailure(String paramString);
  
  public abstract void onSuccess(String paramString);
}
