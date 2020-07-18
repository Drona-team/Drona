package com.google.android.gms.common.server.converter;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;
import com.google.android.gms.common.server.response.FastJsonResponse.FieldConverter;

@SafeParcelable.Class(creator="ConverterWrapperCreator")
public final class MapPack
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zaa> CREATOR = new VerticalProgressBar.SavedState.1();
  @SafeParcelable.VersionField(id=1)
  private final int zalf;
  @SafeParcelable.Field(getter="getStringToIntConverter", id=2)
  private final StringToIntConverter zapl;
  
  MapPack(int paramInt, StringToIntConverter paramStringToIntConverter)
  {
    zalf = paramInt;
    zapl = paramStringToIntConverter;
  }
  
  private MapPack(StringToIntConverter paramStringToIntConverter)
  {
    zalf = 1;
    zapl = paramStringToIntConverter;
  }
  
  public static MapPack getSize(FastJsonResponse.FieldConverter paramFieldConverter)
  {
    if ((paramFieldConverter instanceof StringToIntConverter)) {
      return new MapPack((StringToIntConverter)paramFieldConverter);
    }
    throw new IllegalArgumentException("Unsupported safe parcelable field converter class.");
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, zalf);
    SafeParcelWriter.writeParcelable(paramParcel, 2, zapl, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public final FastJsonResponse.FieldConverter zaci()
  {
    if (zapl != null) {
      return zapl;
    }
    throw new IllegalStateException("There was no converter wrapped in this ConverterWrapper.");
  }
}
