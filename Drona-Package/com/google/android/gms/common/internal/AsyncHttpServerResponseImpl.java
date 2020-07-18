package com.google.android.gms.common.internal;

import android.content.Intent;
import com.google.android.gms.common.package_6.internal.LifecycleFragment;

final class AsyncHttpServerResponseImpl
  extends DialogRedirect
{
  AsyncHttpServerResponseImpl(Intent paramIntent, LifecycleFragment paramLifecycleFragment, int paramInt) {}
  
  public final void redirect()
  {
    if (zaoh != null) {
      zaoi.startActivityForResult(zaoh, val$requestCode);
    }
  }
}
