package com.google.android.gms.package_8.formats;

import android.content.Context;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.internal.ads.zzaem;
import com.google.android.gms.internal.ads.zzbad;
import com.google.android.gms.internal.ads.zzyh;
import com.google.android.gms.internal.ads.zzyt;

@Deprecated
public class NativeAdView
  extends FrameLayout
{
  private final FrameLayout zzbqi = init(paramContext);
  private final zzaem zzbqj = zzks();
  
  public NativeAdView(Context paramContext)
  {
    super(paramContext);
  }
  
  public NativeAdView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public NativeAdView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public NativeAdView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  private final FrameLayout init(Context paramContext)
  {
    paramContext = new FrameLayout(paramContext);
    paramContext.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
    addView(paramContext);
    return paramContext;
  }
  
  private final zzaem zzks()
  {
    Preconditions.checkNotNull(zzbqi, "createDelegate must be called after mOverlayFrame has been created");
    if (isInEditMode()) {
      return null;
    }
    return zzyt.zzpb().zza(zzbqi.getContext(), this, zzbqi);
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    super.addView(paramView, paramInt, paramLayoutParams);
    super.bringChildToFront(zzbqi);
  }
  
  public void bringChildToFront(View paramView)
  {
    super.bringChildToFront(paramView);
    if (zzbqi != paramView) {
      super.bringChildToFront(zzbqi);
    }
  }
  
  public void destroy()
  {
    zzaem localZzaem = zzbqj;
    try
    {
      localZzaem.destroy();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      zzbad.zzc("Unable to destroy native ad view", localRemoteException);
    }
  }
  
  public AdChoicesView getAdChoicesView()
  {
    View localView = zzbj("1098");
    if ((localView instanceof AdChoicesView)) {
      return (AdChoicesView)localView;
    }
    return null;
  }
  
  protected final void moveCursor(String paramString, View paramView)
  {
    zzaem localZzaem = zzbqj;
    try
    {
      localZzaem.zzc(paramString, ObjectWrapper.wrap(paramView));
      return;
    }
    catch (RemoteException paramString)
    {
      zzbad.zzc("Unable to call setAssetView on delegate", paramString);
    }
  }
  
  public void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    if (zzbqj != null)
    {
      zzaem localZzaem = zzbqj;
      try
      {
        localZzaem.zzc(ObjectWrapper.wrap(paramView), paramInt);
        return;
      }
      catch (RemoteException paramView)
      {
        zzbad.zzc("Unable to call onVisibilityChanged on delegate", paramView);
      }
    }
  }
  
  public void removeAllViews()
  {
    super.removeAllViews();
    super.addView(zzbqi);
  }
  
  public void removeView(View paramView)
  {
    if (zzbqi == paramView) {
      return;
    }
    super.removeView(paramView);
  }
  
  public void setAdChoicesView(AdChoicesView paramAdChoicesView)
  {
    moveCursor("1098", paramAdChoicesView);
  }
  
  public void setNativeAd(NativeAd paramNativeAd)
  {
    zzaem localZzaem = zzbqj;
    try
    {
      paramNativeAd = paramNativeAd.zzkq();
      paramNativeAd = (IObjectWrapper)paramNativeAd;
      localZzaem.zze(paramNativeAd);
      return;
    }
    catch (RemoteException paramNativeAd)
    {
      zzbad.zzc("Unable to call setNativeAd on delegate", paramNativeAd);
    }
  }
  
  protected final View zzbj(String paramString)
  {
    zzaem localZzaem = zzbqj;
    try
    {
      paramString = localZzaem.zzcf(paramString);
      if (paramString != null)
      {
        paramString = ObjectWrapper.unwrap(paramString);
        return (View)paramString;
      }
    }
    catch (RemoteException paramString)
    {
      zzbad.zzc("Unable to call getAssetView on delegate", paramString);
    }
    return null;
  }
}
