package com.google.android.gms.package_8.mediation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract interface MediationAdLoadCallback<MediationAdT, MediationAdCallbackT>
{
  public abstract void onFailure(String paramString);
  
  public abstract Object onSuccess(Object paramObject);
}
