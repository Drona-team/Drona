package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;

@SafeParcelable.Class(creator="ConnectionInfoCreator")
public final class Entry
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzb> CREATOR = new VerticalProgressBar.SavedState.1();
  @SafeParcelable.Field(id=1)
  Bundle zzda;
  @SafeParcelable.Field(id=2)
  Feature[] zzdb;
  
  public Entry() {}
  
  Entry(Bundle paramBundle, Feature[] paramArrayOfFeature)
  {
    zzda = paramBundle;
    zzdb = paramArrayOfFeature;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeBundle(paramParcel, 1, zzda, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 2, zzdb, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}
