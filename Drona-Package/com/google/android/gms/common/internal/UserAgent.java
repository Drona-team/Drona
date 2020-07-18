package com.google.android.gms.common.internal;

import android.app.Activity;
import android.content.Intent;

final class UserAgent
  extends DialogRedirect
{
  UserAgent(Intent paramIntent, Activity paramActivity, int paramInt) {}
  
  public final void redirect()
  {
    if (zaoh != null) {
      val$activity.startActivityForResult(zaoh, val$requestCode);
    }
  }
}
