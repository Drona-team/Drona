package com.google.android.exoplayer2.metadata.configurations;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class BinaryFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<com.google.android.exoplayer2.metadata.id3.BinaryFrame> CREATOR = new Parcelable.Creator()
  {
    public BinaryFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BinaryFrame(paramAnonymousParcel);
    }
    
    public BinaryFrame[] newArray(int paramAnonymousInt)
    {
      return new BinaryFrame[paramAnonymousInt];
    }
  };
  public final byte[] data;
  
  BinaryFrame(Parcel paramParcel)
  {
    super((String)Util.castNonNull(paramParcel.readString()));
    data = ((byte[])Util.castNonNull(paramParcel.createByteArray()));
  }
  
  public BinaryFrame(String paramString, byte[] paramArrayOfByte)
  {
    super(paramString);
    data = paramArrayOfByte;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject != null)
    {
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (BinaryFrame)paramObject;
      return (id.equals(id)) && (Arrays.equals(data, data));
    }
    return false;
  }
  
  public int hashCode()
  {
    return (527 + id.hashCode()) * 31 + Arrays.hashCode(data);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(id);
    paramParcel.writeByteArray(data);
  }
}
