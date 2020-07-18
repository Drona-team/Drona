package com.google.android.gms.package_8.internal.overlay;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.internal.ads.zzawz;

@zzard
public final class PickerActivity
  extends AbstractGalleryActivity
{
  public PickerActivity(Activity paramActivity)
  {
    super(paramActivity);
  }
  
  public final void onCreate(Bundle paramBundle)
  {
    zzawz.zzds("AdOverlayParcel is null or does not contain valid overlay type.");
    zzdjw = 3;
    mActivity.finish();
  }
}
