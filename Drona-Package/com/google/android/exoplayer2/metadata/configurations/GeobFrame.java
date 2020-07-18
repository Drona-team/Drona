package com.google.android.exoplayer2.metadata.configurations;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class GeobFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<com.google.android.exoplayer2.metadata.id3.GeobFrame> CREATOR = new Parcelable.Creator()
  {
    public GeobFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new GeobFrame(paramAnonymousParcel);
    }
    
    public GeobFrame[] newArray(int paramAnonymousInt)
    {
      return new GeobFrame[paramAnonymousInt];
    }
  };
  public static final String TYPE = "GEOB";
  public final byte[] data;
  public final String description;
  public final String filename;
  public final String mimeType;
  
  GeobFrame(Parcel paramParcel)
  {
    super("GEOB");
    mimeType = ((String)Util.castNonNull(paramParcel.readString()));
    filename = ((String)Util.castNonNull(paramParcel.readString()));
    description = ((String)Util.castNonNull(paramParcel.readString()));
    data = ((byte[])Util.castNonNull(paramParcel.createByteArray()));
  }
  
  public GeobFrame(String paramString1, String paramString2, String paramString3, byte[] paramArrayOfByte)
  {
    super("GEOB");
    mimeType = paramString1;
    filename = paramString2;
    description = paramString3;
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
      paramObject = (GeobFrame)paramObject;
      return (Util.areEqual(mimeType, mimeType)) && (Util.areEqual(filename, filename)) && (Util.areEqual(description, description)) && (Arrays.equals(data, data));
    }
    return false;
  }
  
  public int hashCode()
  {
    String str = mimeType;
    int k = 0;
    int i;
    if (str != null) {
      i = mimeType.hashCode();
    } else {
      i = 0;
    }
    int j;
    if (filename != null) {
      j = filename.hashCode();
    } else {
      j = 0;
    }
    if (description != null) {
      k = description.hashCode();
    }
    return (((527 + i) * 31 + j) * 31 + k) * 31 + Arrays.hashCode(data);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(id);
    localStringBuilder.append(": mimeType=");
    localStringBuilder.append(mimeType);
    localStringBuilder.append(", filename=");
    localStringBuilder.append(filename);
    localStringBuilder.append(", description=");
    localStringBuilder.append(description);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(mimeType);
    paramParcel.writeString(filename);
    paramParcel.writeString(description);
    paramParcel.writeByteArray(data);
  }
}
