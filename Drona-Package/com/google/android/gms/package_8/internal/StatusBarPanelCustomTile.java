package com.google.android.gms.package_8.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.ads.internal.zzh;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Reserved;
import com.google.android.gms.internal.ads.zzard;

@zzard
@SafeParcelable.Class(creator="InterstitialAdParameterParcelCreator")
@SafeParcelable.Reserved({1})
public final class StatusBarPanelCustomTile
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzh> CREATOR = new VerticalProgressBar.SavedState.1();
  @SafeParcelable.Field(id=2)
  public final boolean zzbre;
  @SafeParcelable.Field(id=3)
  public final boolean zzbrf;
  @SafeParcelable.Field(id=4)
  private final String zzbrg;
  @SafeParcelable.Field(id=5)
  public final boolean zzbrh;
  @SafeParcelable.Field(id=6)
  public final float zzbri;
  @SafeParcelable.Field(id=7)
  public final int zzbrj;
  @SafeParcelable.Field(id=8)
  public final boolean zzbrk;
  @SafeParcelable.Field(id=9)
  public final boolean zzbrl;
  @SafeParcelable.Field(id=10)
  public final boolean zzbrm;
  
  StatusBarPanelCustomTile(boolean paramBoolean1, boolean paramBoolean2, String paramString, boolean paramBoolean3, float paramFloat, int paramInt, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6)
  {
    zzbre = paramBoolean1;
    zzbrf = paramBoolean2;
    zzbrg = paramString;
    zzbrh = paramBoolean3;
    zzbri = paramFloat;
    zzbrj = paramInt;
    zzbrk = paramBoolean4;
    zzbrl = paramBoolean5;
    zzbrm = paramBoolean6;
  }
  
  public StatusBarPanelCustomTile(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, float paramFloat, int paramInt, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6)
  {
    this(false, paramBoolean2, null, false, 0.0F, -1, paramBoolean4, paramBoolean5, paramBoolean6);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeBoolean(paramParcel, 2, zzbre);
    SafeParcelWriter.writeBoolean(paramParcel, 3, zzbrf);
    SafeParcelWriter.writeString(paramParcel, 4, zzbrg, false);
    SafeParcelWriter.writeBoolean(paramParcel, 5, zzbrh);
    SafeParcelWriter.writeFloat(paramParcel, 6, zzbri);
    SafeParcelWriter.writeInt(paramParcel, 7, zzbrj);
    SafeParcelWriter.writeBoolean(paramParcel, 8, zzbrk);
    SafeParcelWriter.writeBoolean(paramParcel, 9, zzbrl);
    SafeParcelWriter.writeBoolean(paramParcel, 10, zzbrm);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}
