package com.google.android.gms.package_8;

import android.content.Context;
import android.util.AttributeSet;
import com.google.android.gms.internal.ads.zzabb;
import com.google.android.gms.internal.ads.zzard;

@zzard
public final class NativeExpressAdView
  extends BaseAdView
{
  public NativeExpressAdView(Context paramContext)
  {
    super(paramContext, 1);
  }
  
  public NativeExpressAdView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, 1);
  }
  
  public NativeExpressAdView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt, 1);
  }
  
  public final VideoController getVideoController()
  {
    return zzaaq.getVideoController();
  }
  
  public final VideoOptions getVideoOptions()
  {
    return zzaaq.getVideoOptions();
  }
  
  public final void setVideoOptions(VideoOptions paramVideoOptions)
  {
    zzaaq.setVideoOptions(paramVideoOptions);
  }
}
