package com.google.ads.mediation.customevent;

import android.app.Activity;
import android.view.View;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.MediationBannerAdapter;
import com.google.ads.mediation.MediationBannerListener;
import com.google.ads.mediation.MediationInterstitialAdapter;
import com.google.ads.mediation.MediationInterstitialListener;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.internal.ads.zzbad;

@KeepName
public final class CustomEventAdapter
  implements MediationBannerAdapter<com.google.android.gms.ads.mediation.customevent.CustomEventExtras, CustomEventServerParameters>, MediationInterstitialAdapter<com.google.android.gms.ads.mediation.customevent.CustomEventExtras, CustomEventServerParameters>
{
  private View zzmx;
  @VisibleForTesting
  private CustomEventBanner zzmy;
  @VisibleForTesting
  private CustomEventInterstitial zzmz;
  
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
  
  public final void destroy()
  {
    if (zzmy != null) {
      zzmy.destroy();
    }
    if (zzmz != null) {
      zzmz.destroy();
    }
  }
  
  public final Class getAdditionalParametersType()
  {
    return com.google.android.gms.ads.mediation.customevent.CustomEventExtras.class;
  }
  
  public final View getBannerView()
  {
    return zzmx;
  }
  
  public final Class getServerParametersType()
  {
    return CustomEventServerParameters.class;
  }
  
  public final void requestBannerAd(MediationBannerListener paramMediationBannerListener, Activity paramActivity, CustomEventServerParameters paramCustomEventServerParameters, AdSize paramAdSize, MediationAdRequest paramMediationAdRequest, com.google.android.gms.package_8.mediation.customevent.CustomEventExtras paramCustomEventExtras)
  {
    zzmy = ((CustomEventBanner)zzaj(className));
    if (zzmy == null)
    {
      paramMediationBannerListener.onFailedToReceiveAd(this, AdRequest.ErrorCode.INTERNAL_ERROR);
      return;
    }
    if (paramCustomEventExtras == null) {}
    for (paramCustomEventExtras = null;; paramCustomEventExtras = paramCustomEventExtras.getExtra(label)) {
      break;
    }
    zzmy.requestBannerAd(new zza(this, paramMediationBannerListener), paramActivity, label, parameter, paramAdSize, paramMediationAdRequest, paramCustomEventExtras);
  }
  
  public final void requestInterstitialAd(MediationInterstitialListener paramMediationInterstitialListener, Activity paramActivity, CustomEventServerParameters paramCustomEventServerParameters, MediationAdRequest paramMediationAdRequest, com.google.android.gms.package_8.mediation.customevent.CustomEventExtras paramCustomEventExtras)
  {
    zzmz = ((CustomEventInterstitial)zzaj(className));
    if (zzmz == null)
    {
      paramMediationInterstitialListener.onFailedToReceiveAd(this, AdRequest.ErrorCode.INTERNAL_ERROR);
      return;
    }
    if (paramCustomEventExtras == null) {}
    for (paramCustomEventExtras = null;; paramCustomEventExtras = paramCustomEventExtras.getExtra(label)) {
      break;
    }
    zzmz.requestInterstitialAd(new zzb(this, paramMediationInterstitialListener), paramActivity, label, parameter, paramMediationAdRequest, paramCustomEventExtras);
  }
  
  public final void showInterstitial()
  {
    zzmz.showInterstitial();
  }
  
  @VisibleForTesting
  static final class zza
    implements CustomEventBannerListener
  {
    private final CustomEventAdapter zzna;
    private final MediationBannerListener zznb;
    
    public zza(CustomEventAdapter paramCustomEventAdapter, MediationBannerListener paramMediationBannerListener)
    {
      zzna = paramCustomEventAdapter;
      zznb = paramMediationBannerListener;
    }
    
    public final void onClick()
    {
      zzbad.zzdp("Custom event adapter called onFailedToReceiveAd.");
      zznb.onClick(zzna);
    }
    
    public final void onDismissScreen()
    {
      zzbad.zzdp("Custom event adapter called onFailedToReceiveAd.");
      zznb.onDismissScreen(zzna);
    }
    
    public final void onFailedToReceiveAd()
    {
      zzbad.zzdp("Custom event adapter called onFailedToReceiveAd.");
      zznb.onFailedToReceiveAd(zzna, AdRequest.ErrorCode.NO_FILL);
    }
    
    public final void onLeaveApplication()
    {
      zzbad.zzdp("Custom event adapter called onFailedToReceiveAd.");
      zznb.onLeaveApplication(zzna);
    }
    
    public final void onPresentScreen()
    {
      zzbad.zzdp("Custom event adapter called onFailedToReceiveAd.");
      zznb.onPresentScreen(zzna);
    }
    
    public final void onReceivedAd(View paramView)
    {
      zzbad.zzdp("Custom event adapter called onReceivedAd.");
      CustomEventAdapter.setReadonly(zzna, paramView);
      zznb.onReceivedAd(zzna);
    }
  }
  
  @VisibleForTesting
  final class zzb
    implements CustomEventInterstitialListener
  {
    private final CustomEventAdapter zzna;
    private final MediationInterstitialListener zznc;
    
    public zzb(CustomEventAdapter paramCustomEventAdapter, MediationInterstitialListener paramMediationInterstitialListener)
    {
      zzna = paramCustomEventAdapter;
      zznc = paramMediationInterstitialListener;
    }
    
    public final void onDismissScreen()
    {
      zzbad.zzdp("Custom event adapter called onDismissScreen.");
      zznc.onDismissScreen(zzna);
    }
    
    public final void onFailedToReceiveAd()
    {
      zzbad.zzdp("Custom event adapter called onFailedToReceiveAd.");
      zznc.onFailedToReceiveAd(zzna, AdRequest.ErrorCode.NO_FILL);
    }
    
    public final void onLeaveApplication()
    {
      zzbad.zzdp("Custom event adapter called onLeaveApplication.");
      zznc.onLeaveApplication(zzna);
    }
    
    public final void onPresentScreen()
    {
      zzbad.zzdp("Custom event adapter called onPresentScreen.");
      zznc.onPresentScreen(zzna);
    }
    
    public final void onReceivedAd()
    {
      zzbad.zzdp("Custom event adapter called onReceivedAd.");
      zznc.onReceivedAd(CustomEventAdapter.this);
    }
  }
}
