package com.google.android.json;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.android.a.c;

public final class JSONObject
{
  static
  {
    c.class.getClassLoader();
  }
  
  private JSONObject() {}
  
  public static Parcelable get(Parcel paramParcel, Parcelable.Creator paramCreator)
  {
    if (paramParcel.readInt() != 0) {
      return (Parcelable)paramCreator.createFromParcel(paramParcel);
    }
    return null;
  }
  
  public static void writeValue(Parcel paramParcel, Parcelable paramParcelable)
  {
    paramParcel.writeInt(1);
    paramParcelable.writeToParcel(paramParcel, 0);
  }
}
