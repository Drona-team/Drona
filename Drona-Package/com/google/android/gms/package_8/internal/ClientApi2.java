package com.google.android.gms.package_8.internal;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.internal.ads.zzaab;
import com.google.android.gms.internal.ads.zzabp;
import com.google.android.gms.internal.ads.zzaem;
import com.google.android.gms.internal.ads.zzaer;
import com.google.android.gms.internal.ads.zzamp;
import com.google.android.gms.internal.ads.zzaqg;
import com.google.android.gms.internal.ads.zzaqq;
import com.google.android.gms.internal.ads.zzasw;
import com.google.android.gms.internal.ads.zzatt;
import com.google.android.gms.internal.ads.zzbjm;
import com.google.android.gms.internal.ads.zzbzi;
import com.google.android.gms.internal.ads.zzbzj;
import com.google.android.gms.internal.ads.zzcpo;
import com.google.android.gms.internal.ads.zzcpt;
import com.google.android.gms.internal.ads.zzcqd;
import com.google.android.gms.internal.ads.zzcqf;
import com.google.android.gms.internal.ads.zzcqj;
import com.google.android.gms.internal.ads.zzyd;
import com.google.android.gms.internal.ads.zzzf;
import com.google.android.gms.internal.ads.zzzk;
import com.google.android.gms.internal.ads.zzzw;
import com.google.android.gms.package_8.internal.overlay.ActivityBase;
import com.google.android.gms.package_8.internal.overlay.ActivityMain;
import com.google.android.gms.package_8.internal.overlay.AdOverlayInfoParcel;
import com.google.android.gms.package_8.internal.overlay.CropImage;
import com.google.android.gms.package_8.internal.overlay.Gallery;
import com.google.android.gms.package_8.internal.overlay.PickerActivity;
import java.util.HashMap;

public class ClientApi2
  extends zzzw
{
  public ClientApi2() {}
  
  public final zzasw createResponse(IObjectWrapper paramIObjectWrapper, zzamp paramZzamp, int paramInt)
  {
    paramIObjectWrapper = (Context)ObjectWrapper.unwrap(paramIObjectWrapper);
    return new zzcqj(zzbjm.zza(paramIObjectWrapper, paramZzamp, paramInt), paramIObjectWrapper);
  }
  
  public final zzzf createResponse(IObjectWrapper paramIObjectWrapper, String paramString, zzamp paramZzamp, int paramInt)
  {
    paramIObjectWrapper = (Context)ObjectWrapper.unwrap(paramIObjectWrapper);
    return new zzcpo(zzbjm.zza(paramIObjectWrapper, paramZzamp, paramInt), paramIObjectWrapper, paramString);
  }
  
  public final zzzk createResponse(IObjectWrapper paramIObjectWrapper, zzyd paramZzyd, String paramString, zzamp paramZzamp, int paramInt)
  {
    paramIObjectWrapper = (Context)ObjectWrapper.unwrap(paramIObjectWrapper);
    return new zzcpt(zzbjm.zza(paramIObjectWrapper, paramZzamp, paramInt), paramIObjectWrapper, paramZzyd, paramString);
  }
  
  public final zzaer get(IObjectWrapper paramIObjectWrapper1, IObjectWrapper paramIObjectWrapper2, IObjectWrapper paramIObjectWrapper3)
  {
    return new zzbzi((View)ObjectWrapper.unwrap(paramIObjectWrapper1), (HashMap)ObjectWrapper.unwrap(paramIObjectWrapper2), (HashMap)ObjectWrapper.unwrap(paramIObjectWrapper3));
  }
  
  public final zzaab getCurrentSession(IObjectWrapper paramIObjectWrapper)
  {
    return null;
  }
  
  public final zzaqq implementMethod(IObjectWrapper paramIObjectWrapper)
  {
    return null;
  }
  
  public final zzaqg onCreate(IObjectWrapper paramIObjectWrapper)
  {
    paramIObjectWrapper = (Activity)ObjectWrapper.unwrap(paramIObjectWrapper);
    AdOverlayInfoParcel localAdOverlayInfoParcel = AdOverlayInfoParcel.loadData(paramIObjectWrapper.getIntent());
    if (localAdOverlayInfoParcel == null) {
      return (zzaqg)new PickerActivity(paramIObjectWrapper);
    }
    switch (zzdkr)
    {
    default: 
      return (zzaqg)new PickerActivity(paramIObjectWrapper);
    case 4: 
      return (zzaqg)new ActivityMain(paramIObjectWrapper, localAdOverlayInfoParcel);
    case 3: 
      return (zzaqg)new Gallery(paramIObjectWrapper);
    case 2: 
      return (zzaqg)new ActivityBase(paramIObjectWrapper);
    }
    return (zzaqg)new CropImage(paramIObjectWrapper);
  }
  
  public final zzzk setUrls(IObjectWrapper paramIObjectWrapper, zzyd paramZzyd, String paramString, int paramInt)
  {
    return new zzabp();
  }
  
  public final zzatt showDialog(IObjectWrapper paramIObjectWrapper, String paramString, zzamp paramZzamp, int paramInt)
  {
    paramIObjectWrapper = (Context)ObjectWrapper.unwrap(paramIObjectWrapper);
    return new zzcqf(zzbjm.zza(paramIObjectWrapper, paramZzamp, paramInt), paramIObjectWrapper, paramString);
  }
  
  public final zzzk showDialog(IObjectWrapper paramIObjectWrapper, zzyd paramZzyd, String paramString, zzamp paramZzamp, int paramInt)
  {
    paramIObjectWrapper = (Context)ObjectWrapper.unwrap(paramIObjectWrapper);
    return new zzcqd(zzbjm.zza(paramIObjectWrapper, paramZzamp, paramInt), paramIObjectWrapper, paramZzyd, paramString);
  }
  
  public final zzaab unwrap(IObjectWrapper paramIObjectWrapper, int paramInt)
  {
    return zzbjm.zzd((Context)ObjectWrapper.unwrap(paramIObjectWrapper), paramInt).zzaci();
  }
  
  public final zzaem unwrap(IObjectWrapper paramIObjectWrapper1, IObjectWrapper paramIObjectWrapper2)
  {
    return new zzbzj((FrameLayout)ObjectWrapper.unwrap(paramIObjectWrapper1), (FrameLayout)ObjectWrapper.unwrap(paramIObjectWrapper2));
  }
}
