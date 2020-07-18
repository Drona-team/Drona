package com.google.android.gms.package_8.internal.overlay;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.internal.ads.zzbgz;

@zzard
@VisibleForTesting
public final class Actor
{
  public final int index;
  public final ViewGroup parent;
  public final ViewGroup.LayoutParams zzdkh;
  public final Context zzlj;
  
  public Actor(zzbgz paramZzbgz)
    throws InvalidPatternException
  {
    zzdkh = paramZzbgz.getLayoutParams();
    ViewParent localViewParent = paramZzbgz.getParent();
    zzlj = paramZzbgz.zzaad();
    if ((localViewParent != null) && ((localViewParent instanceof ViewGroup)))
    {
      parent = ((ViewGroup)localViewParent);
      index = parent.indexOfChild(paramZzbgz.getView());
      parent.removeView(paramZzbgz.getView());
      paramZzbgz.zzaq(true);
      return;
    }
    throw new InvalidPatternException("Could not get the parent of the WebView for an overlay.");
  }
}
