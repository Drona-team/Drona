package com.google.android.gms.package_8.mediation;

import android.os.Bundle;
import android.view.View;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.package_8.VideoController;
import java.util.Map;

@zzard
@Deprecated
public class NativeAdMapper
{
  protected View mAdChoicesContent;
  protected Bundle mExtras = new Bundle();
  protected boolean mOverrideClickHandling;
  protected boolean mOverrideImpressionRecording;
  private VideoController zzcje;
  private View zzena;
  private boolean zzenb;
  
  public NativeAdMapper() {}
  
  public View getAdChoicesContent()
  {
    return mAdChoicesContent;
  }
  
  public final Bundle getExtras()
  {
    return mExtras;
  }
  
  public final boolean getOverrideClickHandling()
  {
    return mOverrideClickHandling;
  }
  
  public final boolean getOverrideImpressionRecording()
  {
    return mOverrideImpressionRecording;
  }
  
  public final VideoController getVideoController()
  {
    return zzcje;
  }
  
  public void handleClick(View paramView) {}
  
  public boolean hasVideoContent()
  {
    return zzenb;
  }
  
  public void recordImpression() {}
  
  public void setAdChoicesContent(View paramView)
  {
    mAdChoicesContent = paramView;
  }
  
  public final void setExpandableListAdapter(VideoController paramVideoController)
  {
    zzcje = paramVideoController;
  }
  
  public final void setExtras(Bundle paramBundle)
  {
    mExtras = paramBundle;
  }
  
  public void setHasVideoContent(boolean paramBoolean)
  {
    zzenb = paramBoolean;
  }
  
  public void setMediaView(View paramView)
  {
    zzena = paramView;
  }
  
  public final void setOverrideClickHandling(boolean paramBoolean)
  {
    mOverrideClickHandling = paramBoolean;
  }
  
  public final void setOverrideImpressionRecording(boolean paramBoolean)
  {
    mOverrideImpressionRecording = paramBoolean;
  }
  
  public void trackView(View paramView) {}
  
  public void trackViews(View paramView, Map paramMap1, Map paramMap2) {}
  
  public void untrackView(View paramView) {}
  
  public final View zzacd()
  {
    return zzena;
  }
}
