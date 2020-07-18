package com.google.android.gms.common.package_6;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentSender.SendIntentException;

public class ResolvableApiException
  extends ApiException
{
  public ResolvableApiException(Status paramStatus)
  {
    super(paramStatus);
  }
  
  public PendingIntent getResolution()
  {
    return mStatus.getResolution();
  }
  
  public void startResolutionForResult(Activity paramActivity, int paramInt)
    throws IntentSender.SendIntentException
  {
    mStatus.startResolutionForResult(paramActivity, paramInt);
  }
}
