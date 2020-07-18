package com.google.android.gms.package_8;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.ads.zzaaz;
import com.google.android.gms.internal.ads.zzady;
import com.google.android.gms.internal.ads.zzagm;
import com.google.android.gms.internal.ads.zzagn;
import com.google.android.gms.internal.ads.zzago;
import com.google.android.gms.internal.ads.zzagp;
import com.google.android.gms.internal.ads.zzagq;
import com.google.android.gms.internal.ads.zzags;
import com.google.android.gms.internal.ads.zzamo;
import com.google.android.gms.internal.ads.zzbad;
import com.google.android.gms.internal.ads.zzxv;
import com.google.android.gms.internal.ads.zzyc;
import com.google.android.gms.internal.ads.zzyd;
import com.google.android.gms.internal.ads.zzyh;
import com.google.android.gms.internal.ads.zzyt;
import com.google.android.gms.internal.ads.zzzc;
import com.google.android.gms.internal.ads.zzzf;
import com.google.android.gms.package_8.doubleclick.PublisherAdRequest;
import com.google.android.gms.package_8.formats.NativeAdOptions;
import com.google.android.gms.package_8.formats.NativeAppInstallAd.OnAppInstallAdLoadedListener;
import com.google.android.gms.package_8.formats.NativeContentAd.OnContentAdLoadedListener;
import com.google.android.gms.package_8.formats.NativeCustomTemplateAd.OnCustomClickListener;
import com.google.android.gms.package_8.formats.NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener;
import com.google.android.gms.package_8.formats.OnPublisherAdViewLoadedListener;
import com.google.android.gms.package_8.formats.PublisherAdViewOptions;
import com.google.android.gms.package_8.formats.UnifiedNativeAd.OnUnifiedNativeAdLoadedListener;

public class AdLoader
{
  private final zzyc zzaaj;
  private final zzzc zzaak;
  private final Context zzlj;
  
  AdLoader(Context paramContext, zzzc paramZzzc)
  {
    this(paramContext, paramZzzc, zzyc.zzche);
  }
  
  private AdLoader(Context paramContext, zzzc paramZzzc, zzyc paramZzyc)
  {
    zzlj = paramContext;
    zzaak = paramZzzc;
    zzaaj = paramZzyc;
  }
  
  private final void applyUpdate(zzaaz paramZzaaz)
  {
    zzzc localZzzc = zzaak;
    Context localContext = zzlj;
    try
    {
      localZzzc.zza(zzyc.zza(localContext, paramZzaaz));
      return;
    }
    catch (RemoteException paramZzaaz)
    {
      zzbad.zzc("Failed to load ad.", paramZzaaz);
    }
  }
  
  public String getMediationAdapterClassName()
  {
    Object localObject = zzaak;
    try
    {
      localObject = ((zzzc)localObject).zzpj();
      return localObject;
    }
    catch (RemoteException localRemoteException)
    {
      zzbad.zzd("Failed to get the mediation adapter class name.", localRemoteException);
    }
    return null;
  }
  
  public boolean isLoading()
  {
    zzzc localZzzc = zzaak;
    try
    {
      boolean bool = localZzzc.isLoading();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      zzbad.zzd("Failed to check if ad is loading.", localRemoteException);
    }
    return false;
  }
  
  public void loadAd(AdRequest paramAdRequest)
  {
    applyUpdate(paramAdRequest.zzde());
  }
  
  public void loadAd(PublisherAdRequest paramPublisherAdRequest)
  {
    applyUpdate(paramPublisherAdRequest.zzde());
  }
  
  public void loadAds(AdRequest paramAdRequest, int paramInt)
  {
    paramAdRequest = paramAdRequest.zzde();
    zzzc localZzzc = zzaak;
    Context localContext = zzlj;
    try
    {
      localZzzc.zza(zzyc.zza(localContext, paramAdRequest), paramInt);
      return;
    }
    catch (RemoteException paramAdRequest)
    {
      zzbad.zzc("Failed to load ads.", paramAdRequest);
    }
  }
  
  public class Builder
  {
    private final zzzf zzaal;
    
    private Builder(zzzf paramZzzf)
    {
      zzaal = paramZzzf;
    }
    
    public Builder(String paramString)
    {
      this(zzyt.zzpb().zzb(AdLoader.this, paramString, new zzamo()));
    }
    
    public AdLoader build()
    {
      Object localObject = AdLoader.this;
      zzzf localZzzf = zzaal;
      try
      {
        localObject = new AdLoader((Context)localObject, localZzzf.zzpk());
        return localObject;
      }
      catch (RemoteException localRemoteException)
      {
        zzbad.zzc("Failed to build AdLoader.", localRemoteException);
      }
      return null;
    }
    
    public Builder forAppInstallAd(NativeAppInstallAd.OnAppInstallAdLoadedListener paramOnAppInstallAdLoadedListener)
    {
      zzzf localZzzf = zzaal;
      try
      {
        localZzzf.zza(new zzagm(paramOnAppInstallAdLoadedListener));
        return this;
      }
      catch (RemoteException paramOnAppInstallAdLoadedListener)
      {
        zzbad.zzd("Failed to add app install ad listener", paramOnAppInstallAdLoadedListener);
      }
      return this;
    }
    
    public Builder forContentAd(NativeContentAd.OnContentAdLoadedListener paramOnContentAdLoadedListener)
    {
      zzzf localZzzf = zzaal;
      try
      {
        localZzzf.zza(new zzagn(paramOnContentAdLoadedListener));
        return this;
      }
      catch (RemoteException paramOnContentAdLoadedListener)
      {
        zzbad.zzd("Failed to add content ad listener", paramOnContentAdLoadedListener);
      }
      return this;
    }
    
    public Builder forCustomTemplateAd(String paramString, NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener paramOnCustomTemplateAdLoadedListener, NativeCustomTemplateAd.OnCustomClickListener paramOnCustomClickListener)
    {
      zzzf localZzzf = zzaal;
      try
      {
        zzagp localZzagp = new zzagp(paramOnCustomTemplateAdLoadedListener);
        if (paramOnCustomClickListener == null) {
          paramOnCustomTemplateAdLoadedListener = null;
        } else {
          paramOnCustomTemplateAdLoadedListener = new zzago(paramOnCustomClickListener);
        }
        localZzzf.zza(paramString, localZzagp, paramOnCustomTemplateAdLoadedListener);
        return this;
      }
      catch (RemoteException paramString)
      {
        zzbad.zzd("Failed to add custom template ad listener", paramString);
      }
      return this;
    }
    
    public Builder forPublisherAdView(OnPublisherAdViewLoadedListener paramOnPublisherAdViewLoadedListener, AdSize... paramVarArgs)
    {
      if ((paramVarArgs != null) && (paramVarArgs.length > 0))
      {
        Object localObject = AdLoader.this;
        try
        {
          paramVarArgs = new zzyd((Context)localObject, paramVarArgs);
          localObject = zzaal;
          ((zzzf)localObject).zza(new zzagq(paramOnPublisherAdViewLoadedListener), paramVarArgs);
          return this;
        }
        catch (RemoteException paramOnPublisherAdViewLoadedListener)
        {
          zzbad.zzd("Failed to add publisher banner ad listener", paramOnPublisherAdViewLoadedListener);
          return this;
        }
      }
      throw new IllegalArgumentException("The supported ad sizes must contain at least one valid ad size.");
    }
    
    public Builder forUnifiedNativeAd(UnifiedNativeAd.OnUnifiedNativeAdLoadedListener paramOnUnifiedNativeAdLoadedListener)
    {
      zzzf localZzzf = zzaal;
      try
      {
        localZzzf.zza(new zzags(paramOnUnifiedNativeAdLoadedListener));
        return this;
      }
      catch (RemoteException paramOnUnifiedNativeAdLoadedListener)
      {
        zzbad.zzd("Failed to add google native ad listener", paramOnUnifiedNativeAdLoadedListener);
      }
      return this;
    }
    
    public Builder withAdListener(AdListener paramAdListener)
    {
      zzzf localZzzf = zzaal;
      try
      {
        localZzzf.zza(new zzxv(paramAdListener));
        return this;
      }
      catch (RemoteException paramAdListener)
      {
        zzbad.zzd("Failed to set AdListener.", paramAdListener);
      }
      return this;
    }
    
    public Builder withCorrelator(Correlator paramCorrelator)
    {
      Preconditions.checkNotNull(paramCorrelator);
      zzzf localZzzf = zzaal;
      paramCorrelator = zzaar;
      try
      {
        localZzzf.zza(paramCorrelator);
        return this;
      }
      catch (RemoteException paramCorrelator)
      {
        zzbad.zzd("Failed to set correlator.", paramCorrelator);
      }
      return this;
    }
    
    public Builder withNativeAdOptions(NativeAdOptions paramNativeAdOptions)
    {
      zzzf localZzzf = zzaal;
      try
      {
        localZzzf.zza(new zzady(paramNativeAdOptions));
        return this;
      }
      catch (RemoteException paramNativeAdOptions)
      {
        zzbad.zzd("Failed to specify native ad options", paramNativeAdOptions);
      }
      return this;
    }
    
    public Builder withPublisherAdViewOptions(PublisherAdViewOptions paramPublisherAdViewOptions)
    {
      zzzf localZzzf = zzaal;
      try
      {
        localZzzf.zza(paramPublisherAdViewOptions);
        return this;
      }
      catch (RemoteException paramPublisherAdViewOptions)
      {
        zzbad.zzd("Failed to specify DFP banner ad options", paramPublisherAdViewOptions);
      }
      return this;
    }
  }
}
