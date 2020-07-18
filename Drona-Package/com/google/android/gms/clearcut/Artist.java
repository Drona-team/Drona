package com.google.android.gms.clearcut;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;

@SafeParcelable.Class(creator="CollectForDebugParcelableCreator")
public final class Artist
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzc> CREATOR = new Server.1();
  @SafeParcelable.Field(defaultValue="false", id=1)
  private final boolean zzad;
  @SafeParcelable.Field(id=3)
  private final long zzae;
  @SafeParcelable.Field(id=2)
  private final long zzaf;
  
  public Artist(boolean paramBoolean, long paramLong1, long paramLong2)
  {
    zzad = paramBoolean;
    zzae = paramLong1;
    zzaf = paramLong2;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof Artist))
    {
      paramObject = (Artist)paramObject;
      if ((zzad == zzad) && (zzae == zzae) && (zzaf == zzaf)) {
        return true;
      }
    }
    return false;
  }
  
  public final int hashCode()
  {
    return Objects.hashCode(new Object[] { Boolean.valueOf(zzad), Long.valueOf(zzae), Long.valueOf(zzaf) });
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("CollectForDebugParcelable[skipPersistentStorage: ");
    localStringBuilder.append(zzad);
    localStringBuilder.append(",collectForDebugStartTimeMillis: ");
    localStringBuilder.append(zzae);
    localStringBuilder.append(",collectForDebugExpiryTimeMillis: ");
    localStringBuilder.append(zzaf);
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeBoolean(paramParcel, 1, zzad);
    SafeParcelWriter.writeLong(paramParcel, 2, zzaf);
    SafeParcelWriter.writeLong(paramParcel, 3, zzae);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}
