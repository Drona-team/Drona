package com.google.android.gms.common.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

@KeepForSdk
public class DataBufferSafeParcelable<T extends SafeParcelable>
  extends AbstractDataBuffer<T>
{
  private static final String[] zalo = { "data" };
  private final Parcelable.Creator<T> zalp;
  
  public DataBufferSafeParcelable(DataHolder paramDataHolder, Parcelable.Creator paramCreator)
  {
    super(paramDataHolder);
    zalp = paramCreator;
  }
  
  public static void addValue(DataHolder.Builder paramBuilder, SafeParcelable paramSafeParcelable)
  {
    Parcel localParcel = Parcel.obtain();
    paramSafeParcelable.writeToParcel(localParcel, 0);
    paramSafeParcelable = new ContentValues();
    paramSafeParcelable.put("data", localParcel.marshall());
    paramBuilder.withRow(paramSafeParcelable);
    localParcel.recycle();
  }
  
  public static DataHolder.Builder buildDataHolder()
  {
    return DataHolder.builder(zalo);
  }
  
  public SafeParcelable get(int paramInt)
  {
    Object localObject = mDataHolder.getByteArray("data", paramInt, mDataHolder.getWindowIndex(paramInt));
    Parcel localParcel = Parcel.obtain();
    localParcel.unmarshall((byte[])localObject, 0, localObject.length);
    localParcel.setDataPosition(0);
    localObject = (SafeParcelable)zalp.createFromParcel(localParcel);
    localParcel.recycle();
    return localObject;
  }
}
