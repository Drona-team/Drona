package com.google.android.gms.common.package_6;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;

@KeepForSdk
@SafeParcelable.Class(creator="ScopeCreator")
public final class Scope
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<com.google.android.gms.common.api.Scope> CREATOR = new DiscreteSeekBar.CustomState.1();
  @SafeParcelable.VersionField(id=1)
  private final int max;
  @SafeParcelable.Field(getter="getScopeUri", id=2)
  private final String zzaq;
  
  Scope(int paramInt, String paramString)
  {
    Preconditions.checkNotEmpty(paramString, "scopeUri must not be null or empty");
    max = paramInt;
    zzaq = paramString;
  }
  
  public Scope(String paramString)
  {
    this(1, paramString);
  }
  
  public final boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Scope)) {
      return false;
    }
    return zzaq.equals(zzaq);
  }
  
  public final String getScopeUri()
  {
    return zzaq;
  }
  
  public final int hashCode()
  {
    return zzaq.hashCode();
  }
  
  public final String toString()
  {
    return zzaq;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, max);
    SafeParcelWriter.writeString(paramParcel, 2, getScopeUri(), false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}
