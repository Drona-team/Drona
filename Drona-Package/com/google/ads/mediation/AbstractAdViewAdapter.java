package com.google.ads.mediation;

import android.content.Context;
import android.location.Location;
import android.os.BaseBundle;
import android.os.Bundle;
import android.view.View;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.internal.ads.zzaar;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.internal.ads.zzazt;
import com.google.android.gms.internal.ads.zzbad;
import com.google.android.gms.internal.ads.zzbjl;
import com.google.android.gms.internal.ads.zzxr;
import com.google.android.gms.internal.ads.zzyt;
import com.google.android.gms.package_8.AdListener;
import com.google.android.gms.package_8.AdLoader;
import com.google.android.gms.package_8.AdLoader.Builder;
import com.google.android.gms.package_8.AdRequest.Builder;
import com.google.android.gms.package_8.AdSize;
import com.google.android.gms.package_8.AdView;
import com.google.android.gms.package_8.InterstitialAd;
import com.google.android.gms.package_8.VideoController;
import com.google.android.gms.package_8.doubleclick.AppEventListener;
import com.google.android.gms.package_8.formats.NativeAdView;
import com.google.android.gms.package_8.formats.NativeAdViewHolder;
import com.google.android.gms.package_8.formats.NativeAppInstallAd;
import com.google.android.gms.package_8.formats.NativeAppInstallAd.OnAppInstallAdLoadedListener;
import com.google.android.gms.package_8.formats.NativeContentAd;
import com.google.android.gms.package_8.formats.NativeContentAd.OnContentAdLoadedListener;
import com.google.android.gms.package_8.formats.NativeCustomTemplateAd;
import com.google.android.gms.package_8.formats.NativeCustomTemplateAd.OnCustomClickListener;
import com.google.android.gms.package_8.formats.NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener;
import com.google.android.gms.package_8.formats.UnifiedNativeAd;
import com.google.android.gms.package_8.formats.UnifiedNativeAd.OnUnifiedNativeAdLoadedListener;
import com.google.android.gms.package_8.formats.UnifiedNativeAdView;
import com.google.android.gms.package_8.mediation.MediationAdRequest;
import com.google.android.gms.package_8.mediation.MediationAdapter.zza;
import com.google.android.gms.package_8.mediation.MediationBannerAdapter;
import com.google.android.gms.package_8.mediation.MediationBannerListener;
import com.google.android.gms.package_8.mediation.MediationInterstitialAdapter;
import com.google.android.gms.package_8.mediation.MediationInterstitialListener;
import com.google.android.gms.package_8.mediation.MediationNativeAdapter;
import com.google.android.gms.package_8.mediation.MediationNativeListener;
import com.google.android.gms.package_8.mediation.NativeAdMapper;
import com.google.android.gms.package_8.mediation.NativeAppInstallAdMapper;
import com.google.android.gms.package_8.mediation.NativeContentAdMapper;
import com.google.android.gms.package_8.mediation.NativeMediationAdRequest;
import com.google.android.gms.package_8.mediation.OnImmersiveModeUpdatedListener;
import com.google.android.gms.package_8.mediation.Trackable;
import com.google.android.gms.package_8.mediation.UnifiedNativeAdMapper;
import com.google.android.gms.package_8.reward.RewardedVideoAdListener;
import com.google.android.gms.package_8.reward.mediation.MediationRewardedVideoAdAdapter;
import com.google.android.gms.package_8.reward.mediation.MediationRewardedVideoAdListener;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@zzard
public abstract class AbstractAdViewAdapter
  implements MediationBannerAdapter, MediationNativeAdapter, OnImmersiveModeUpdatedListener, Trackable, MediationRewardedVideoAdAdapter, zzbjl
{
  public static final String AD_UNIT_ID_PARAMETER = "pubid";
  private AdView zzmd;
  private InterstitialAd zzme;
  private AdLoader zzmf;
  private Context zzmg;
  private InterstitialAd zzmh;
  private MediationRewardedVideoAdListener zzmi;
  @VisibleForTesting
  private final RewardedVideoAdListener zzmj = new SettingsFragment.7(this);
  
  public AbstractAdViewAdapter() {}
  
  private final com.google.android.gms.package_8.AdRequest processResult(Context paramContext, MediationAdRequest paramMediationAdRequest, Bundle paramBundle1, Bundle paramBundle2)
  {
    AdRequest.Builder localBuilder = new AdRequest.Builder();
    Object localObject = paramMediationAdRequest.getBirthday();
    if (localObject != null) {
      localBuilder.setBirthday((Date)localObject);
    }
    int i = paramMediationAdRequest.getGender();
    if (i != 0) {
      localBuilder.setGender(i);
    }
    localObject = paramMediationAdRequest.getKeywords();
    if (localObject != null)
    {
      localObject = ((Set)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        localBuilder.addKeyword((String)((Iterator)localObject).next());
      }
    }
    localObject = paramMediationAdRequest.getLocation();
    if (localObject != null) {
      localBuilder.setLocation((Location)localObject);
    }
    if (paramMediationAdRequest.isTesting())
    {
      zzyt.zzpa();
      localBuilder.addTestDevice(zzazt.zzbe(paramContext));
    }
    if (paramMediationAdRequest.taggedForChildDirectedTreatment() != -1)
    {
      i = paramMediationAdRequest.taggedForChildDirectedTreatment();
      boolean bool = true;
      if (i != 1) {
        bool = false;
      }
      localBuilder.tagForChildDirectedTreatment(bool);
    }
    localBuilder.setIsDesignedForFamilies(paramMediationAdRequest.isDesignedForFamilies());
    localBuilder.addNetworkExtrasBundle(AdMobAdapter.class, toBundle(paramBundle1, paramBundle2));
    return localBuilder.build();
  }
  
  public String getAdUnitId(Bundle paramBundle)
  {
    return paramBundle.getString("pubid");
  }
  
  public View getBannerView()
  {
    return zzmd;
  }
  
  public Bundle getInterstitialAdapterInfo()
  {
    return new MediationAdapter.zza().zzdj(1).zzacc();
  }
  
  public zzaar getVideoController()
  {
    if (zzmd != null)
    {
      VideoController localVideoController = zzmd.getVideoController();
      if (localVideoController != null) {
        return localVideoController.zzdh();
      }
    }
    return null;
  }
  
  public void initialize(Context paramContext, MediationAdRequest paramMediationAdRequest, String paramString, MediationRewardedVideoAdListener paramMediationRewardedVideoAdListener, Bundle paramBundle1, Bundle paramBundle2)
  {
    zzmg = paramContext.getApplicationContext();
    zzmi = paramMediationRewardedVideoAdListener;
    zzmi.onInitializationSucceeded(this);
  }
  
  public boolean isInitialized()
  {
    return zzmi != null;
  }
  
  public void loadAd(MediationAdRequest paramMediationAdRequest, Bundle paramBundle1, Bundle paramBundle2)
  {
    if ((zzmg != null) && (zzmi != null))
    {
      zzmh = new InterstitialAd(zzmg);
      zzmh.e(true);
      zzmh.setAdUnitId(getAdUnitId(paramBundle1));
      zzmh.setRewardedVideoAdListener(zzmj);
      zzmh.setAdMetadataListener(new AdRequest(this));
      zzmh.loadAd(processResult(zzmg, paramMediationAdRequest, paramBundle2, paramBundle1));
      return;
    }
    zzbad.zzen("AdMobAdapter.loadAd called before initialize.");
  }
  
  public void onDestroy()
  {
    if (zzmd != null)
    {
      zzmd.destroy();
      zzmd = null;
    }
    if (zzme != null) {
      zzme = null;
    }
    if (zzmf != null) {
      zzmf = null;
    }
    if (zzmh != null) {
      zzmh = null;
    }
  }
  
  public void onImmersiveModeUpdated(boolean paramBoolean)
  {
    if (zzme != null) {
      zzme.setImmersiveMode(paramBoolean);
    }
    if (zzmh != null) {
      zzmh.setImmersiveMode(paramBoolean);
    }
  }
  
  public void onPause()
  {
    if (zzmd != null) {
      zzmd.pause();
    }
  }
  
  public void onResume()
  {
    if (zzmd != null) {
      zzmd.resume();
    }
  }
  
  public void requestBannerAd(Context paramContext, MediationBannerListener paramMediationBannerListener, Bundle paramBundle1, AdSize paramAdSize, MediationAdRequest paramMediationAdRequest, Bundle paramBundle2)
  {
    zzmd = new AdView(paramContext);
    zzmd.setAdSize(new AdSize(paramAdSize.getWidth(), paramAdSize.getHeight()));
    zzmd.setAdUnitId(getAdUnitId(paramBundle1));
    zzmd.setAdListener(new zzd(this, paramMediationBannerListener));
    zzmd.loadAd(processResult(paramContext, paramMediationAdRequest, paramBundle2, paramBundle1));
  }
  
  public void requestInterstitialAd(Context paramContext, MediationInterstitialListener paramMediationInterstitialListener, Bundle paramBundle1, MediationAdRequest paramMediationAdRequest, Bundle paramBundle2)
  {
    zzme = new InterstitialAd(paramContext);
    zzme.setAdUnitId(getAdUnitId(paramBundle1));
    zzme.setAdListener(new zze(this, paramMediationInterstitialListener));
    zzme.loadAd(processResult(paramContext, paramMediationAdRequest, paramBundle2, paramBundle1));
  }
  
  public void requestNativeAd(Context paramContext, MediationNativeListener paramMediationNativeListener, Bundle paramBundle1, NativeMediationAdRequest paramNativeMediationAdRequest, Bundle paramBundle2)
  {
    zzf localZzf = new zzf(this, paramMediationNativeListener);
    AdLoader.Builder localBuilder = new AdLoader.Builder(paramContext, paramBundle1.getString("pubid")).withAdListener(localZzf);
    paramMediationNativeListener = paramNativeMediationAdRequest.getNativeAdOptions();
    if (paramMediationNativeListener != null) {
      localBuilder.withNativeAdOptions(paramMediationNativeListener);
    }
    if (paramNativeMediationAdRequest.isUnifiedNativeAdRequested()) {
      localBuilder.forUnifiedNativeAd(localZzf);
    }
    if (paramNativeMediationAdRequest.isAppInstallAdRequested()) {
      localBuilder.forAppInstallAd(localZzf);
    }
    if (paramNativeMediationAdRequest.isContentAdRequested()) {
      localBuilder.forContentAd(localZzf);
    }
    if (paramNativeMediationAdRequest.zzsu())
    {
      Iterator localIterator = paramNativeMediationAdRequest.zzsv().keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (((Boolean)paramNativeMediationAdRequest.zzsv().get(str)).booleanValue()) {
          paramMediationNativeListener = localZzf;
        } else {
          paramMediationNativeListener = null;
        }
        localBuilder.forCustomTemplateAd(str, localZzf, paramMediationNativeListener);
      }
    }
    zzmf = localBuilder.build();
    zzmf.loadAd(processResult(paramContext, paramNativeMediationAdRequest, paramBundle2, paramBundle1));
  }
  
  public void showInterstitial()
  {
    zzme.show();
  }
  
  public void showVideo()
  {
    zzmh.show();
  }
  
  protected abstract Bundle toBundle(Bundle paramBundle1, Bundle paramBundle2);
  
  static final class zza
    extends NativeAppInstallAdMapper
  {
    private final NativeAppInstallAd zzml;
    
    public zza(NativeAppInstallAd paramNativeAppInstallAd)
    {
      zzml = paramNativeAppInstallAd;
      setHeadline(paramNativeAppInstallAd.getHeadline().toString());
      setImages(paramNativeAppInstallAd.getImages());
      setBody(paramNativeAppInstallAd.getBody().toString());
      setIcon(paramNativeAppInstallAd.getIcon());
      setCallToAction(paramNativeAppInstallAd.getCallToAction().toString());
      if (paramNativeAppInstallAd.getStarRating() != null) {
        setStarRating(paramNativeAppInstallAd.getStarRating().doubleValue());
      }
      if (paramNativeAppInstallAd.getStore() != null) {
        setStore(paramNativeAppInstallAd.getStore().toString());
      }
      if (paramNativeAppInstallAd.getPrice() != null) {
        setPrice(paramNativeAppInstallAd.getPrice().toString());
      }
      setOverrideImpressionRecording(true);
      setOverrideClickHandling(true);
      setExpandableListAdapter(paramNativeAppInstallAd.getVideoController());
    }
    
    public final void trackView(View paramView)
    {
      if ((paramView instanceof NativeAdView)) {
        ((NativeAdView)paramView).setNativeAd(zzml);
      }
      paramView = (NativeAdViewHolder)NativeAdViewHolder.zzbql.get(paramView);
      if (paramView != null) {
        paramView.setNativeAd(zzml);
      }
    }
  }
  
  static final class zzb
    extends NativeContentAdMapper
  {
    private final NativeContentAd zzmm;
    
    public zzb(NativeContentAd paramNativeContentAd)
    {
      zzmm = paramNativeContentAd;
      setHeadline(paramNativeContentAd.getHeadline().toString());
      setImages(paramNativeContentAd.getImages());
      setBody(paramNativeContentAd.getBody().toString());
      if (paramNativeContentAd.getLogo() != null) {
        setLogo(paramNativeContentAd.getLogo());
      }
      setCallToAction(paramNativeContentAd.getCallToAction().toString());
      setAdvertiser(paramNativeContentAd.getAdvertiser().toString());
      setOverrideImpressionRecording(true);
      setOverrideClickHandling(true);
      setExpandableListAdapter(paramNativeContentAd.getVideoController());
    }
    
    public final void trackView(View paramView)
    {
      if ((paramView instanceof NativeAdView)) {
        ((NativeAdView)paramView).setNativeAd(zzmm);
      }
      paramView = (NativeAdViewHolder)NativeAdViewHolder.zzbql.get(paramView);
      if (paramView != null) {
        paramView.setNativeAd(zzmm);
      }
    }
  }
  
  static final class zzc
    extends UnifiedNativeAdMapper
  {
    private final UnifiedNativeAd zzmn;
    
    public zzc(UnifiedNativeAd paramUnifiedNativeAd)
    {
      zzmn = paramUnifiedNativeAd;
      setHeadline(paramUnifiedNativeAd.getHeadline());
      setImages(paramUnifiedNativeAd.getImages());
      setBody(paramUnifiedNativeAd.getBody());
      setIcon(paramUnifiedNativeAd.getIcon());
      setCallToAction(paramUnifiedNativeAd.getCallToAction());
      setAdvertiser(paramUnifiedNativeAd.getAdvertiser());
      setStarRating(paramUnifiedNativeAd.getStarRating());
      setStore(paramUnifiedNativeAd.getStore());
      setPrice(paramUnifiedNativeAd.getPrice());
      writeShort(paramUnifiedNativeAd.zzkv());
      setOverrideImpressionRecording(true);
      setOverrideClickHandling(true);
      setExpandableListAdapter(paramUnifiedNativeAd.getVideoController());
    }
    
    public final void trackViews(View paramView, Map paramMap1, Map paramMap2)
    {
      if ((paramView instanceof UnifiedNativeAdView))
      {
        ((UnifiedNativeAdView)paramView).setNativeAd(zzmn);
        return;
      }
      paramView = (NativeAdViewHolder)NativeAdViewHolder.zzbql.get(paramView);
      if (paramView != null) {
        paramView.setNativeAd(zzmn);
      }
    }
  }
  
  @VisibleForTesting
  static final class zzd
    extends AdListener
    implements AppEventListener, zzxr
  {
    @VisibleForTesting
    private final AbstractAdViewAdapter zzmo;
    @VisibleForTesting
    private final MediationBannerListener zzmp;
    
    public zzd(AbstractAdViewAdapter paramAbstractAdViewAdapter, MediationBannerListener paramMediationBannerListener)
    {
      zzmo = paramAbstractAdViewAdapter;
      zzmp = paramMediationBannerListener;
    }
    
    public final void onAdClicked()
    {
      zzmp.onAdClicked(zzmo);
    }
    
    public final void onAdClosed()
    {
      zzmp.onAdClosed(zzmo);
    }
    
    public final void onAdFailedToLoad(int paramInt)
    {
      zzmp.onAdFailedToLoad(zzmo, paramInt);
    }
    
    public final void onAdLeftApplication()
    {
      zzmp.onAdLeftApplication(zzmo);
    }
    
    public final void onAdLoaded()
    {
      zzmp.onAdLoaded(zzmo);
    }
    
    public final void onAdOpened()
    {
      zzmp.onAdOpened(zzmo);
    }
    
    public final void onAppEvent(String paramString1, String paramString2)
    {
      zzmp.onClick(zzmo, paramString1, paramString2);
    }
  }
  
  @VisibleForTesting
  static final class zze
    extends AdListener
    implements zzxr
  {
    @VisibleForTesting
    private final AbstractAdViewAdapter zzmo;
    @VisibleForTesting
    private final MediationInterstitialListener zzmq;
    
    public zze(AbstractAdViewAdapter paramAbstractAdViewAdapter, MediationInterstitialListener paramMediationInterstitialListener)
    {
      zzmo = paramAbstractAdViewAdapter;
      zzmq = paramMediationInterstitialListener;
    }
    
    public final void onAdClicked()
    {
      zzmq.onAdClicked((MediationInterstitialAdapter)zzmo);
    }
    
    public final void onAdClosed()
    {
      zzmq.onAdClosed((MediationInterstitialAdapter)zzmo);
    }
    
    public final void onAdFailedToLoad(int paramInt)
    {
      zzmq.onAdFailedToLoad((MediationInterstitialAdapter)zzmo, paramInt);
    }
    
    public final void onAdLeftApplication()
    {
      zzmq.onAdLeftApplication((MediationInterstitialAdapter)zzmo);
    }
    
    public final void onAdLoaded()
    {
      zzmq.onAdLoaded((MediationInterstitialAdapter)zzmo);
    }
    
    public final void onAdOpened()
    {
      zzmq.onAdOpened((MediationInterstitialAdapter)zzmo);
    }
  }
  
  @VisibleForTesting
  static final class zzf
    extends AdListener
    implements NativeAppInstallAd.OnAppInstallAdLoadedListener, NativeContentAd.OnContentAdLoadedListener, NativeCustomTemplateAd.OnCustomClickListener, NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener, UnifiedNativeAd.OnUnifiedNativeAdLoadedListener
  {
    @VisibleForTesting
    private final AbstractAdViewAdapter zzmo;
    @VisibleForTesting
    private final MediationNativeListener zzmr;
    
    public zzf(AbstractAdViewAdapter paramAbstractAdViewAdapter, MediationNativeListener paramMediationNativeListener)
    {
      zzmo = paramAbstractAdViewAdapter;
      zzmr = paramMediationNativeListener;
    }
    
    public final void onAdClicked()
    {
      zzmr.onAdClicked(zzmo);
    }
    
    public final void onAdClosed()
    {
      zzmr.onAdClosed(zzmo);
    }
    
    public final void onAdFailedToLoad(int paramInt)
    {
      zzmr.onAdFailedToLoad(zzmo, paramInt);
    }
    
    public final void onAdImpression()
    {
      zzmr.onAdImpression(zzmo);
    }
    
    public final void onAdLeftApplication()
    {
      zzmr.onAdLeftApplication(zzmo);
    }
    
    public final void onAdLoaded() {}
    
    public final void onAdOpened()
    {
      zzmr.onAdOpened(zzmo);
    }
    
    public final void onAppInstallAdLoaded(NativeAppInstallAd paramNativeAppInstallAd)
    {
      zzmr.onAdLoaded(zzmo, new AbstractAdViewAdapter.zza(paramNativeAppInstallAd));
    }
    
    public final void onContentAdLoaded(NativeContentAd paramNativeContentAd)
    {
      zzmr.onAdLoaded(zzmo, new AbstractAdViewAdapter.zzb(paramNativeContentAd));
    }
    
    public final void onCustomClick(NativeCustomTemplateAd paramNativeCustomTemplateAd, String paramString)
    {
      zzmr.setCurrentTheme(zzmo, paramNativeCustomTemplateAd, paramString);
    }
    
    public final void onCustomTemplateAdLoaded(NativeCustomTemplateAd paramNativeCustomTemplateAd)
    {
      zzmr.setCurrentTheme(zzmo, paramNativeCustomTemplateAd);
    }
    
    public final void onUnifiedNativeAdLoaded(UnifiedNativeAd paramUnifiedNativeAd)
    {
      zzmr.onAdLoaded(zzmo, new AbstractAdViewAdapter.zzc(paramUnifiedNativeAd));
    }
  }
}
