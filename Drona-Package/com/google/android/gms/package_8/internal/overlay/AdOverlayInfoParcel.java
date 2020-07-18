package com.google.android.gms.package_8.internal.overlay;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Reserved;
import com.google.android.gms.dynamic.IObjectWrapper.Stub;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.internal.ads.zzagv;
import com.google.android.gms.internal.ads.zzagx;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.internal.ads.zzbai;
import com.google.android.gms.internal.ads.zzbgz;
import com.google.android.gms.internal.ads.zzxr;
import com.google.android.gms.package_8.internal.StatusBarPanelCustomTile;

@zzard
@SafeParcelable.Class(creator="AdOverlayInfoCreator")
@SafeParcelable.Reserved({1})
public final class AdOverlayInfoParcel
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<com.google.android.gms.ads.internal.overlay.AdOverlayInfoParcel> CREATOR = new VerticalProgressBar.SavedState.1();
  @SafeParcelable.Field(id=11)
  public final int orientation;
  @SafeParcelable.Field(id=13)
  public final String thumbUrl;
  @SafeParcelable.Field(id=14)
  public final zzbai zzbtc;
  @SafeParcelable.Field(getter="getAdClickListenerAsBinder", id=3, type="android.os.IBinder")
  public final zzxr zzcgi;
  @SafeParcelable.Field(getter="getAdMetadataGmsgListenerAsBinder", id=18, type="android.os.IBinder")
  public final zzagv zzczo;
  @SafeParcelable.Field(getter="getAppEventGmsgListenerAsBinder", id=6, type="android.os.IBinder")
  public final zzagx zzczp;
  @SafeParcelable.Field(getter="getAdWebViewAsBinder", id=5, type="android.os.IBinder")
  public final zzbgz zzdbs;
  @SafeParcelable.Field(id=2)
  public final Attachment zzdkl;
  @SafeParcelable.Field(getter="getAdOverlayListenerAsBinder", id=4, type="android.os.IBinder")
  public final Renderer zzdkm;
  @SafeParcelable.Field(id=7)
  public final String zzdkn;
  @SafeParcelable.Field(id=8)
  public final boolean zzdko;
  @SafeParcelable.Field(id=9)
  public final String zzdkp;
  @SafeParcelable.Field(getter="getLeaveApplicationListenerAsBinder", id=10, type="android.os.IBinder")
  public final Monitor zzdkq;
  @SafeParcelable.Field(id=12)
  public final int zzdkr;
  @SafeParcelable.Field(id=16)
  public final String zzdks;
  @SafeParcelable.Field(id=17)
  public final StatusBarPanelCustomTile zzdkt;
  
  public AdOverlayInfoParcel(zzxr paramZzxr, Renderer paramRenderer, zzagv paramZzagv, zzagx paramZzagx, Monitor paramMonitor, zzbgz paramZzbgz, boolean paramBoolean, int paramInt, String paramString, zzbai paramZzbai)
  {
    zzdkl = null;
    zzcgi = paramZzxr;
    zzdkm = paramRenderer;
    zzdbs = paramZzbgz;
    zzczo = paramZzagv;
    zzczp = paramZzagx;
    zzdkn = null;
    zzdko = paramBoolean;
    zzdkp = null;
    zzdkq = paramMonitor;
    orientation = paramInt;
    zzdkr = 3;
    thumbUrl = paramString;
    zzbtc = paramZzbai;
    zzdks = null;
    zzdkt = null;
  }
  
  public AdOverlayInfoParcel(zzxr paramZzxr, Renderer paramRenderer, zzagv paramZzagv, zzagx paramZzagx, Monitor paramMonitor, zzbgz paramZzbgz, boolean paramBoolean, int paramInt, String paramString1, String paramString2, zzbai paramZzbai)
  {
    zzdkl = null;
    zzcgi = paramZzxr;
    zzdkm = paramRenderer;
    zzdbs = paramZzbgz;
    zzczo = paramZzagv;
    zzczp = paramZzagx;
    zzdkn = paramString2;
    zzdko = paramBoolean;
    zzdkp = paramString1;
    zzdkq = paramMonitor;
    orientation = paramInt;
    zzdkr = 3;
    thumbUrl = null;
    zzbtc = paramZzbai;
    zzdks = null;
    zzdkt = null;
  }
  
  public AdOverlayInfoParcel(zzxr paramZzxr, Renderer paramRenderer, Monitor paramMonitor, zzbgz paramZzbgz, int paramInt, zzbai paramZzbai, String paramString, StatusBarPanelCustomTile paramStatusBarPanelCustomTile)
  {
    zzdkl = null;
    zzcgi = null;
    zzdkm = paramRenderer;
    zzdbs = paramZzbgz;
    zzczo = null;
    zzczp = null;
    zzdkn = null;
    zzdko = false;
    zzdkp = null;
    zzdkq = null;
    orientation = paramInt;
    zzdkr = 1;
    thumbUrl = null;
    zzbtc = paramZzbai;
    zzdks = paramString;
    zzdkt = paramStatusBarPanelCustomTile;
  }
  
  public AdOverlayInfoParcel(zzxr paramZzxr, Renderer paramRenderer, Monitor paramMonitor, zzbgz paramZzbgz, boolean paramBoolean, int paramInt, zzbai paramZzbai)
  {
    zzdkl = null;
    zzcgi = paramZzxr;
    zzdkm = paramRenderer;
    zzdbs = paramZzbgz;
    zzczo = null;
    zzczp = null;
    zzdkn = null;
    zzdko = paramBoolean;
    zzdkp = null;
    zzdkq = paramMonitor;
    orientation = paramInt;
    zzdkr = 2;
    thumbUrl = null;
    zzbtc = paramZzbai;
    zzdks = null;
    zzdkt = null;
  }
  
  AdOverlayInfoParcel(Attachment paramAttachment, IBinder paramIBinder1, IBinder paramIBinder2, IBinder paramIBinder3, IBinder paramIBinder4, String paramString1, boolean paramBoolean, String paramString2, IBinder paramIBinder5, int paramInt1, int paramInt2, String paramString3, zzbai paramZzbai, String paramString4, StatusBarPanelCustomTile paramStatusBarPanelCustomTile, IBinder paramIBinder6)
  {
    zzdkl = paramAttachment;
    zzcgi = ((zzxr)ObjectWrapper.unwrap(IObjectWrapper.Stub.asInterface(paramIBinder1)));
    zzdkm = ((Renderer)ObjectWrapper.unwrap(IObjectWrapper.Stub.asInterface(paramIBinder2)));
    zzdbs = ((zzbgz)ObjectWrapper.unwrap(IObjectWrapper.Stub.asInterface(paramIBinder3)));
    zzczo = ((zzagv)ObjectWrapper.unwrap(IObjectWrapper.Stub.asInterface(paramIBinder6)));
    zzczp = ((zzagx)ObjectWrapper.unwrap(IObjectWrapper.Stub.asInterface(paramIBinder4)));
    zzdkn = paramString1;
    zzdko = paramBoolean;
    zzdkp = paramString2;
    zzdkq = ((Monitor)ObjectWrapper.unwrap(IObjectWrapper.Stub.asInterface(paramIBinder5)));
    orientation = paramInt1;
    zzdkr = paramInt2;
    thumbUrl = paramString3;
    zzbtc = paramZzbai;
    zzdks = paramString4;
    zzdkt = paramStatusBarPanelCustomTile;
  }
  
  public AdOverlayInfoParcel(Attachment paramAttachment, zzxr paramZzxr, Renderer paramRenderer, Monitor paramMonitor, zzbai paramZzbai)
  {
    zzdkl = paramAttachment;
    zzcgi = paramZzxr;
    zzdkm = paramRenderer;
    zzdbs = null;
    zzczo = null;
    zzczp = null;
    zzdkn = null;
    zzdko = false;
    zzdkp = null;
    zzdkq = paramMonitor;
    orientation = -1;
    zzdkr = 4;
    thumbUrl = null;
    zzbtc = paramZzbai;
    zzdks = null;
    zzdkt = null;
  }
  
  public static AdOverlayInfoParcel loadData(Intent paramIntent)
  {
    try
    {
      paramIntent = paramIntent.getBundleExtra("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo");
      paramIntent.setClassLoader(com.google.android.gms.ads.internal.overlay.AdOverlayInfoParcel.class.getClassLoader());
      paramIntent = paramIntent.getParcelable("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo");
      return (AdOverlayInfoParcel)paramIntent;
    }
    catch (Exception paramIntent)
    {
      for (;;) {}
    }
    return null;
  }
  
  public static void onPostExecute(Intent paramIntent, AdOverlayInfoParcel paramAdOverlayInfoParcel)
  {
    Bundle localBundle = new Bundle(1);
    localBundle.putParcelable("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo", paramAdOverlayInfoParcel);
    paramIntent.putExtra("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo", localBundle);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeParcelable(paramParcel, 2, zzdkl, paramInt, false);
    SafeParcelWriter.writeIBinder(paramParcel, 3, ObjectWrapper.wrap(zzcgi).asBinder(), false);
    SafeParcelWriter.writeIBinder(paramParcel, 4, ObjectWrapper.wrap(zzdkm).asBinder(), false);
    SafeParcelWriter.writeIBinder(paramParcel, 5, ObjectWrapper.wrap(zzdbs).asBinder(), false);
    SafeParcelWriter.writeIBinder(paramParcel, 6, ObjectWrapper.wrap(zzczp).asBinder(), false);
    SafeParcelWriter.writeString(paramParcel, 7, zzdkn, false);
    SafeParcelWriter.writeBoolean(paramParcel, 8, zzdko);
    SafeParcelWriter.writeString(paramParcel, 9, zzdkp, false);
    SafeParcelWriter.writeIBinder(paramParcel, 10, ObjectWrapper.wrap(zzdkq).asBinder(), false);
    SafeParcelWriter.writeInt(paramParcel, 11, orientation);
    SafeParcelWriter.writeInt(paramParcel, 12, zzdkr);
    SafeParcelWriter.writeString(paramParcel, 13, thumbUrl, false);
    SafeParcelWriter.writeParcelable(paramParcel, 14, (Parcelable)zzbtc, paramInt, false);
    SafeParcelWriter.writeString(paramParcel, 16, zzdks, false);
    SafeParcelWriter.writeParcelable(paramParcel, 17, zzdkt, paramInt, false);
    SafeParcelWriter.writeIBinder(paramParcel, 18, ObjectWrapper.wrap(zzczo).asBinder(), false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}
