package com.google.android.exoplayer2.metadata.configurations;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class ApicFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<com.google.android.exoplayer2.metadata.id3.ApicFrame> CREATOR = new Parcelable.Creator()
  {
    public ApicFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ApicFrame(paramAnonymousParcel);
    }
    
    public ApicFrame[] newArray(int paramAnonymousInt)
    {
      return new ApicFrame[paramAnonymousInt];
    }
  };
  public static final String SQL_UPDATE_6_4 = "APIC";
  @Nullable
  public final String description;
  public final String mimeType;
  public final byte[] pictureData;
  public final int pictureType;
  
  ApicFrame(Parcel paramParcel)
  {
    super("APIC");
    mimeType = ((String)Util.castNonNull(paramParcel.readString()));
    description = ((String)Util.castNonNull(paramParcel.readString()));
    pictureType = paramParcel.readInt();
    pictureData = ((byte[])Util.castNonNull(paramParcel.createByteArray()));
  }
  
  public ApicFrame(String paramString1, String paramString2, int paramInt, byte[] paramArrayOfByte)
  {
    super("APIC");
    mimeType = paramString1;
    description = paramString2;
    pictureType = paramInt;
    pictureData = paramArrayOfByte;
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
      paramObject = (ApicFrame)paramObject;
      return (pictureType == pictureType) && (Util.areEqual(mimeType, mimeType)) && (Util.areEqual(description, description)) && (Arrays.equals(pictureData, pictureData));
    }
    return false;
  }
  
  public int hashCode()
  {
    int k = pictureType;
    String str = mimeType;
    int j = 0;
    int i;
    if (str != null) {
      i = mimeType.hashCode();
    } else {
      i = 0;
    }
    if (description != null) {
      j = description.hashCode();
    }
    return (((527 + k) * 31 + i) * 31 + j) * 31 + Arrays.hashCode(pictureData);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(id);
    localStringBuilder.append(": mimeType=");
    localStringBuilder.append(mimeType);
    localStringBuilder.append(", description=");
    localStringBuilder.append(description);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(mimeType);
    paramParcel.writeString(description);
    paramParcel.writeInt(pictureType);
    paramParcel.writeByteArray(pictureData);
  }
}
