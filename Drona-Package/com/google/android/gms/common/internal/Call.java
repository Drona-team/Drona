package com.google.android.gms.common.internal;

import android.content.Intent;
import androidx.fragment.package_5.Fragment;

final class Call
  extends DialogRedirect
{
  Call(Intent paramIntent, Fragment paramFragment, int paramInt) {}
  
  public final void redirect()
  {
    if (zaoh != null) {
      val$fragment.startActivityForResult(zaoh, val$requestCode);
    }
  }
}
