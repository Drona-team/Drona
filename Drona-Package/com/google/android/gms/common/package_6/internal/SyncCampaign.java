package com.google.android.gms.common.package_6.internal;

import android.app.Dialog;
import android.content.ContextWrapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.package_6.GoogleApiActivity;

final class SyncCampaign
  implements Runnable
{
  private final Tag zadj;
  
  SyncCampaign(AbstractGalleryActivity paramAbstractGalleryActivity, Tag paramTag)
  {
    zadj = paramTag;
  }
  
  public final void run()
  {
    if (!zadk.mStarted) {
      return;
    }
    Object localObject = zadj.getConnectionResult();
    if (((ConnectionResult)localObject).hasResolution())
    {
      zadk.mLifecycleFragment.startActivityForResult(GoogleApiActivity.createIntent(zadk.getActivity(), ((ConnectionResult)localObject).getResolution(), zadj.getId(), false), 1);
      return;
    }
    if (zadk.zacd.isUserResolvableError(((ConnectionResult)localObject).getErrorCode()))
    {
      zadk.zacd.create(zadk.getActivity(), zadk.mLifecycleFragment, ((ConnectionResult)localObject).getErrorCode(), 2, zadk);
      return;
    }
    if (((ConnectionResult)localObject).getErrorCode() == 18)
    {
      localObject = GoogleApiAvailability.show(zadk.getActivity(), zadk);
      zadk.zacd.register(zadk.getActivity().getApplicationContext(), new Searcher(this, (Dialog)localObject));
      return;
    }
    zadk.add((ConnectionResult)localObject, zadj.getId());
  }
}
