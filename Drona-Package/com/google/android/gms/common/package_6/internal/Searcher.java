package com.google.android.gms.common.package_6.internal;

import android.app.Dialog;

final class Searcher
  extends zabr
{
  Searcher(SyncCampaign paramSyncCampaign, Dialog paramDialog) {}
  
  public final void cancel()
  {
    zadm.zadk.onActivityResult();
    if (zadl.isShowing()) {
      zadl.dismiss();
    }
  }
}
