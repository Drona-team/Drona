package com.google.android.gms.package_8.internal.overlay;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.internal.ads.zzayb;

@zzard
@VisibleForTesting
final class TouchInterceptor
  extends RelativeLayout
{
  @VisibleForTesting
  private zzayb zzdkf;
  @VisibleForTesting
  boolean zzdkg;
  
  public TouchInterceptor(Context paramContext, String paramString1, String paramString2)
  {
    super(paramContext);
    zzdkf = new zzayb(paramContext, paramString1);
    zzdkf.zzp(paramString2);
  }
  
  public final boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!zzdkg) {
      zzdkf.zzd(paramMotionEvent);
    }
    return false;
  }
}
