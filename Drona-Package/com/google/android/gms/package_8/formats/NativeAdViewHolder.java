package com.google.android.gms.package_8.formats;

import android.os.RemoteException;
import android.view.View;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.internal.ads.zzaer;
import com.google.android.gms.internal.ads.zzbad;
import com.google.android.gms.internal.ads.zzyh;
import com.google.android.gms.internal.ads.zzyt;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class NativeAdViewHolder
{
  public static WeakHashMap<View, com.google.android.gms.ads.formats.NativeAdViewHolder> zzbql = new WeakHashMap();
  private zzaer zzbqk;
  private WeakReference<View> zzbqm;
  
  public NativeAdViewHolder(View paramView, Map paramMap1, Map paramMap2)
  {
    Preconditions.checkNotNull(paramView, "ContainerView must not be null");
    if ((!(paramView instanceof NativeAdView)) && (!(paramView instanceof UnifiedNativeAdView)))
    {
      if (zzbql.get(paramView) != null)
      {
        zzbad.zzen("The provided containerView is already in use with another NativeAdViewHolder.");
        return;
      }
      zzbql.put(paramView, this);
      zzbqm = new WeakReference(paramView);
      paramMap1 = getContactsEmailAddress(paramMap1);
      paramMap2 = getContactsEmailAddress(paramMap2);
      zzbqk = zzyt.zzpb().zza(paramView, paramMap1, paramMap2);
      return;
    }
    zzbad.zzen("The provided containerView is of type of NativeAdView, which cannot be usedwith NativeAdViewHolder.");
  }
  
  private final void afterHookedMethod(IObjectWrapper paramIObjectWrapper)
  {
    Object localObject;
    if (zzbqm != null) {
      localObject = (View)zzbqm.get();
    } else {
      localObject = null;
    }
    if (localObject == null)
    {
      zzbad.zzep("NativeAdViewHolder.setNativeAd containerView doesn't exist, returning");
      return;
    }
    if (!zzbql.containsKey(localObject)) {
      zzbql.put(localObject, this);
    }
    if (zzbqk != null)
    {
      localObject = zzbqk;
      try
      {
        ((zzaer)localObject).zze(paramIObjectWrapper);
        return;
      }
      catch (RemoteException paramIObjectWrapper)
      {
        zzbad.zzc("Unable to call setNativeAd on delegate", paramIObjectWrapper);
      }
    }
  }
  
  private static HashMap getContactsEmailAddress(Map paramMap)
  {
    if (paramMap == null) {
      return new HashMap();
    }
    return new HashMap(paramMap);
  }
  
  public final void setClickConfirmingView(View paramView)
  {
    zzaer localZzaer = zzbqk;
    try
    {
      localZzaer.zzi(ObjectWrapper.wrap(paramView));
      return;
    }
    catch (RemoteException paramView)
    {
      zzbad.zzc("Unable to call setClickConfirmingView on delegate", paramView);
    }
  }
  
  public final void setNativeAd(NativeAd paramNativeAd)
  {
    afterHookedMethod((IObjectWrapper)paramNativeAd.zzkq());
  }
  
  public final void setNativeAd(UnifiedNativeAd paramUnifiedNativeAd)
  {
    afterHookedMethod((IObjectWrapper)paramUnifiedNativeAd.zzkq());
  }
  
  public final void unregisterNativeAd()
  {
    if (zzbqk != null)
    {
      zzaer localZzaer = zzbqk;
      try
      {
        localZzaer.unregisterNativeAd();
      }
      catch (RemoteException localRemoteException)
      {
        zzbad.zzc("Unable to call unregisterNativeAd on delegate", localRemoteException);
      }
    }
    View localView;
    if (zzbqm != null) {
      localView = (View)zzbqm.get();
    } else {
      localView = null;
    }
    if (localView != null) {
      zzbql.remove(localView);
    }
  }
}
