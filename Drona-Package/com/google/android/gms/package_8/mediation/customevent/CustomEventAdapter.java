package com.google.android.gms.package_8.mediation.customevent;

import android.content.Context;
import android.os.BaseBundle;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.common.annotation.KeepForSdkWithMembers;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.internal.ads.zzbad;
import com.google.android.gms.package_8.AdSize;
import com.google.android.gms.package_8.mediation.MediationAdRequest;
import com.google.android.gms.package_8.mediation.MediationBannerAdapter;
import com.google.android.gms.package_8.mediation.MediationBannerListener;
import com.google.android.gms.package_8.mediation.MediationInterstitialAdapter;
import com.google.android.gms.package_8.mediation.MediationInterstitialListener;
import com.google.android.gms.package_8.mediation.MediationNativeAdapter;
import com.google.android.gms.package_8.mediation.MediationNativeListener;
import com.google.android.gms.package_8.mediation.NativeAdMapper;
import com.google.android.gms.package_8.mediation.NativeMediationAdRequest;
import com.google.android.gms.package_8.mediation.UnifiedNativeAdMapper;

@KeepForSdkWithMembers
@KeepName
public final class CustomEventAdapter
  implements MediationBannerAdapter, MediationInterstitialAdapter, MediationNativeAdapter
{
  @VisibleForTesting
  private CustomEventBanner zzent;
  @VisibleForTesting
  private CustomEventInterstitial zzenu;
  @VisibleForTesting
  private CustomEventNative zzenv;
  private View zzmx;
  
  public CustomEventAdapter() {}
  
  private final void invoke(View paramView)
  {
    zzmx = paramView;
  }
  
  private static Object zzaj(String paramString)
  {
    try
    {
      Object localObject = Class.forName(paramString).newInstance();
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      String str = localThrowable.getMessage();
      StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramString).length() + 46 + String.valueOf(str).length());
      localStringBuilder.append("Could not instantiate custom event adapter: ");
      localStringBuilder.append(paramString);
      localStringBuilder.append(". ");
      localStringBuilder.append(str);
      zzbad.zzep(localStringBuilder.toString());
    }
    return null;
  }
  
  public final View getBannerView()
  {
    return zzmx;
  }
  
  public final void onDestroy()
  {
    if (zzent != null) {
      zzent.onDestroy();
    }
    if (zzenu != null) {
      zzenu.onDestroy();
    }
    if (zzenv != null) {
      zzenv.onDestroy();
    }
  }
  
  public final void onPause()
  {
    if (zzent != null) {
      zzent.onPause();
    }
    if (zzenu != null) {
      zzenu.onPause();
    }
    if (zzenv != null) {
      zzenv.onPause();
    }
  }
  
  public final void onResume()
  {
    if (zzent != null) {
      zzent.onResume();
    }
    if (zzenu != null) {
      zzenu.onResume();
    }
    if (zzenv != null) {
      zzenv.onResume();
    }
  }
  
  public final void requestBannerAd(Context paramContext, MediationBannerListener paramMediationBannerListener, Bundle paramBundle1, AdSize paramAdSize, MediationAdRequest paramMediationAdRequest, Bundle paramBundle2)
  {
    zzent = ((CustomEventBanner)zzaj(paramBundle1.getString("class_name")));
    if (zzent == null)
    {
      paramMediationBannerListener.onAdFailedToLoad(this, 0);
      return;
    }
    if (paramBundle2 == null) {}
    for (paramBundle2 = null;; paramBundle2 = paramBundle2.getBundle(paramBundle1.getString("class_name"))) {
      break;
    }
    zzent.requestBannerAd(paramContext, new zza(paramMediationBannerListener), paramBundle1.getString("parameter"), paramAdSize, paramMediationAdRequest, paramBundle2);
  }
  
  public final void requestInterstitialAd(Context paramContext, MediationInterstitialListener paramMediationInterstitialListener, Bundle paramBundle1, MediationAdRequest paramMediationAdRequest, Bundle paramBundle2)
  {
    zzenu = ((CustomEventInterstitial)zzaj(paramBundle1.getString("class_name")));
    if (zzenu == null)
    {
      paramMediationInterstitialListener.onAdFailedToLoad(this, 0);
      return;
    }
    if (paramBundle2 == null) {}
    for (paramBundle2 = null;; paramBundle2 = paramBundle2.getBundle(paramBundle1.getString("class_name"))) {
      break;
    }
    zzenu.requestInterstitialAd(paramContext, new zzb(this, paramMediationInterstitialListener), paramBundle1.getString("parameter"), paramMediationAdRequest, paramBundle2);
  }
  
  public final void requestNativeAd(Context paramContext, MediationNativeListener paramMediationNativeListener, Bundle paramBundle1, NativeMediationAdRequest paramNativeMediationAdRequest, Bundle paramBundle2)
  {
    zzenv = ((CustomEventNative)zzaj(paramBundle1.getString("class_name")));
    if (zzenv == null)
    {
      paramMediationNativeListener.onAdFailedToLoad(this, 0);
      return;
    }
    if (paramBundle2 == null) {}
    for (paramBundle2 = null;; paramBundle2 = paramBundle2.getBundle(paramBundle1.getString("class_name"))) {
      break;
    }
    zzenv.requestNativeAd(paramContext, new zzc(paramMediationNativeListener), paramBundle1.getString("parameter"), paramNativeMediationAdRequest, paramBundle2);
  }
  
  public final void showInterstitial()
  {
    zzenu.showInterstitial();
  }
  
  @VisibleForTesting
  final class zza
    implements CustomEventBannerListener
  {
    private final MediationBannerListener zzenx;
    
    public zza(MediationBannerListener paramMediationBannerListener)
    {
      zzenx = paramMediationBannerListener;
    }
    
    public final void onAdClicked()
    {
      zzbad.zzdp("Custom event adapter called onAdClicked.");
      zzenx.onAdClicked(CustomEventAdapter.this);
    }
    
    public final void onAdClosed()
    {
      zzbad.zzdp("Custom event adapter called onAdClosed.");
      zzenx.onAdClosed(CustomEventAdapter.this);
    }
    
    public final void onAdFailedToLoad(int paramInt)
    {
      zzbad.zzdp("Custom event adapter called onAdFailedToLoad.");
      zzenx.onAdFailedToLoad(CustomEventAdapter.this, paramInt);
    }
    
    public final void onAdLeftApplication()
    {
      zzbad.zzdp("Custom event adapter called onAdLeftApplication.");
      zzenx.onAdLeftApplication(CustomEventAdapter.this);
    }
    
    public final void onAdLoaded(View paramView)
    {
      zzbad.zzdp("Custom event adapter called onAdLoaded.");
      CustomEventAdapter.setReadonly(CustomEventAdapter.this, paramView);
      zzenx.onAdLoaded(CustomEventAdapter.this);
    }
    
    public final void onAdOpened()
    {
      zzbad.zzdp("Custom event adapter called onAdOpened.");
      zzenx.onAdOpened(CustomEventAdapter.this);
    }
  }
  
  @VisibleForTesting
  final class zzb
    implements CustomEventInterstitialListener
  {
    private final CustomEventAdapter zzenw;
    private final MediationInterstitialListener zzeny;
    
    public zzb(CustomEventAdapter paramCustomEventAdapter, MediationInterstitialListener paramMediationInterstitialListener)
    {
      zzenw = paramCustomEventAdapter;
      zzeny = paramMediationInterstitialListener;
    }
    
    public final void onAdClicked()
    {
      zzbad.zzdp("Custom event adapter called onAdClicked.");
      zzeny.onAdClicked(zzenw);
    }
    
    public final void onAdClosed()
    {
      zzbad.zzdp("Custom event adapter called onAdClosed.");
      zzeny.onAdClosed(zzenw);
    }
    
    public final void onAdFailedToLoad(int paramInt)
    {
      zzbad.zzdp("Custom event adapter called onFailedToReceiveAd.");
      zzeny.onAdFailedToLoad(zzenw, paramInt);
    }
    
    public final void onAdLeftApplication()
    {
      zzbad.zzdp("Custom event adapter called onAdLeftApplication.");
      zzeny.onAdLeftApplication(zzenw);
    }
    
    public final void onAdLoaded()
    {
      zzbad.zzdp("Custom event adapter called onReceivedAd.");
      zzeny.onAdLoaded(CustomEventAdapter.this);
    }
    
    public final void onAdOpened()
    {
      zzbad.zzdp("Custom event adapter called onAdOpened.");
      zzeny.onAdOpened(zzenw);
    }
  }
  
  @VisibleForTesting
  final class zzc
    implements CustomEventNativeListener
  {
    private final MediationNativeListener zzeoa;
    
    public zzc(MediationNativeListener paramMediationNativeListener)
    {
      zzeoa = paramMediationNativeListener;
    }
    
    public final void onAdClicked()
    {
      zzbad.zzdp("Custom event adapter called onAdClicked.");
      zzeoa.onAdClicked(CustomEventAdapter.this);
    }
    
    public final void onAdClosed()
    {
      zzbad.zzdp("Custom event adapter called onAdClosed.");
      zzeoa.onAdClosed(CustomEventAdapter.this);
    }
    
    public final void onAdFailedToLoad(int paramInt)
    {
      zzbad.zzdp("Custom event adapter called onAdFailedToLoad.");
      zzeoa.onAdFailedToLoad(CustomEventAdapter.this, paramInt);
    }
    
    public final void onAdImpression()
    {
      zzbad.zzdp("Custom event adapter called onAdImpression.");
      zzeoa.onAdImpression(CustomEventAdapter.this);
    }
    
    public final void onAdLeftApplication()
    {
      zzbad.zzdp("Custom event adapter called onAdLeftApplication.");
      zzeoa.onAdLeftApplication(CustomEventAdapter.this);
    }
    
    public final void onAdLoaded(NativeAdMapper paramNativeAdMapper)
    {
      zzbad.zzdp("Custom event adapter called onAdLoaded.");
      zzeoa.onAdLoaded(CustomEventAdapter.this, paramNativeAdMapper);
    }
    
    public final void onAdLoaded(UnifiedNativeAdMapper paramUnifiedNativeAdMapper)
    {
      zzbad.zzdp("Custom event adapter called onAdLoaded.");
      zzeoa.onAdLoaded(CustomEventAdapter.this, paramUnifiedNativeAdMapper);
    }
    
    public final void onAdOpened()
    {
      zzbad.zzdp("Custom event adapter called onAdOpened.");
      zzeoa.onAdOpened(CustomEventAdapter.this);
    }
  }
}
