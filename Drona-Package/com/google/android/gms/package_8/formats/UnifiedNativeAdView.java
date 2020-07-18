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

public final class UnifiedNativeAdView
  extends FrameLayout
{
  private final FrameLayout zzbqi = init(paramContext);
  private final zzaem zzbqj = zzks();
  
  public UnifiedNativeAdView(Context paramContext)
  {
    super(paramContext);
  }
  
  public UnifiedNativeAdView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public UnifiedNativeAdView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public UnifiedNativeAdView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  private final void executeQuery(String paramString, View paramView)
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
  
  private final FrameLayout init(Context paramContext)
  {
    paramContext = new FrameLayout(paramContext);
    paramContext.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
    addView(paramContext);
    return paramContext;
  }
  
  private final View zzbj(String paramString)
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
  
  private final zzaem zzks()
  {
    Preconditions.checkNotNull(zzbqi, "createDelegate must be called after overlayFrame has been created");
    if (isInEditMode()) {
      return null;
    }
    return zzyt.zzpb().zza(zzbqi.getContext(), this, zzbqi);
  }
  
  public final void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    super.addView(paramView, paramInt, paramLayoutParams);
    super.bringChildToFront(zzbqi);
  }
  
  public final void bringChildToFront(View paramView)
  {
    super.bringChildToFront(paramView);
    if (zzbqi != paramView) {
      super.bringChildToFront(zzbqi);
    }
  }
  
  public final void destroy()
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
  
  public final AdChoicesView getAdChoicesView()
  {
    View localView = zzbj("3011");
    if ((localView instanceof AdChoicesView)) {
      return (AdChoicesView)localView;
    }
    return null;
  }
  
  public final View getAdvertiserView()
  {
    return zzbj("3005");
  }
  
  public final View getBodyView()
  {
    return zzbj("3004");
  }
  
  public final View getCallToActionView()
  {
    return zzbj("3002");
  }
  
  public final View getHeadlineView()
  {
    return zzbj("3001");
  }
  
  public final View getIconView()
  {
    return zzbj("3003");
  }
  
  public final View getImageView()
  {
    return zzbj("3008");
  }
  
  public final MediaView getMediaView()
  {
    View localView = zzbj("3010");
    if ((localView instanceof MediaView)) {
      return (MediaView)localView;
    }
    if (localView != null) {
      zzbad.zzdp("View is not an instance of MediaView");
    }
    return null;
  }
  
  public final View getPriceView()
  {
    return zzbj("3007");
  }
  
  public final View getStarRatingView()
  {
    return zzbj("3009");
  }
  
  public final View getStoreView()
  {
    return zzbj("3006");
  }
  
  public final void onVisibilityChanged(View paramView, int paramInt)
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
  
  public final void removeAllViews()
  {
    super.removeAllViews();
    super.addView(zzbqi);
  }
  
  public final void removeView(View paramView)
  {
    if (zzbqi == paramView) {
      return;
    }
    super.removeView(paramView);
  }
  
  public final void setAdChoicesView(AdChoicesView paramAdChoicesView)
  {
    executeQuery("3011", paramAdChoicesView);
  }
  
  public final void setAdvertiserView(View paramView)
  {
    executeQuery("3005", paramView);
  }
  
  public final void setBodyView(View paramView)
  {
    executeQuery("3004", paramView);
  }
  
  public final void setCallToActionView(View paramView)
  {
    executeQuery("3002", paramView);
  }
  
  public final void setClickConfirmingView(View paramView)
  {
    zzaem localZzaem = zzbqj;
    try
    {
      localZzaem.zzi(ObjectWrapper.wrap(paramView));
      return;
    }
    catch (RemoteException paramView)
    {
      zzbad.zzc("Unable to call setClickConfirmingView on delegate", paramView);
    }
  }
  
  public final void setHeadlineView(View paramView)
  {
    executeQuery("3001", paramView);
  }
  
  public final void setIconView(View paramView)
  {
    executeQuery("3003", paramView);
  }
  
  public final void setImageView(View paramView)
  {
    executeQuery("3008", paramView);
  }
  
  public final void setMediaView(MediaView paramMediaView)
  {
    executeQuery("3010", paramMediaView);
  }
  
  public final void setNativeAd(UnifiedNativeAd paramUnifiedNativeAd)
  {
    zzaem localZzaem = zzbqj;
    try
    {
      paramUnifiedNativeAd = paramUnifiedNativeAd.zzkq();
      paramUnifiedNativeAd = (IObjectWrapper)paramUnifiedNativeAd;
      localZzaem.zze(paramUnifiedNativeAd);
      return;
    }
    catch (RemoteException paramUnifiedNativeAd)
    {
      zzbad.zzc("Unable to call setNativeAd on delegate", paramUnifiedNativeAd);
    }
  }
  
  public final void setPriceView(View paramView)
  {
    executeQuery("3007", paramView);
  }
  
  public final void setStarRatingView(View paramView)
  {
    executeQuery("3009", paramView);
  }
  
  public final void setStoreView(View paramView)
  {
    executeQuery("3006", paramView);
  }
}
