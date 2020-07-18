package com.google.android.exoplayer2.video;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class ColorInfo
  implements Parcelable
{
  public static final Parcelable.Creator<ColorInfo> CREATOR = new Parcelable.Creator()
  {
    public ColorInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ColorInfo(paramAnonymousParcel);
    }
    
    public ColorInfo[] newArray(int paramAnonymousInt)
    {
      return new ColorInfo[0];
    }
  };
  public final int colorRange;
  public final int colorSpace;
  public final int colorTransfer;
  private int hashCode;
  @Nullable
  public final byte[] hdrStaticInfo;
  
  public ColorInfo(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    colorSpace = paramInt1;
    colorRange = paramInt2;
    colorTransfer = paramInt3;
    hdrStaticInfo = paramArrayOfByte;
  }
  
  ColorInfo(Parcel paramParcel)
  {
    colorSpace = paramParcel.readInt();
    colorRange = paramParcel.readInt();
    colorTransfer = paramParcel.readInt();
    if (Util.readBoolean(paramParcel)) {
      paramParcel = paramParcel.createByteArray();
    } else {
      paramParcel = null;
    }
    hdrStaticInfo = paramParcel;
  }
  
  public int describeContents()
  {
    return 0;
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
      paramObject = (ColorInfo)paramObject;
      return (colorSpace == colorSpace) && (colorRange == colorRange) && (colorTransfer == colorTransfer) && (Arrays.equals(hdrStaticInfo, hdrStaticInfo));
    }
    return false;
  }
  
  public int hashCode()
  {
    if (hashCode == 0) {
      hashCode = ((((527 + colorSpace) * 31 + colorRange) * 31 + colorTransfer) * 31 + Arrays.hashCode(hdrStaticInfo));
    }
    return hashCode;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ColorInfo(");
    localStringBuilder.append(colorSpace);
    localStringBuilder.append(", ");
    localStringBuilder.append(colorRange);
    localStringBuilder.append(", ");
    localStringBuilder.append(colorTransfer);
    localStringBuilder.append(", ");
    boolean bool;
    if (hdrStaticInfo != null) {
      bool = true;
    } else {
      bool = false;
    }
    localStringBuilder.append(bool);
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(colorSpace);
    paramParcel.writeInt(colorRange);
    paramParcel.writeInt(colorTransfer);
    boolean bool;
    if (hdrStaticInfo != null) {
      bool = true;
    } else {
      bool = false;
    }
    Util.writeBoolean(paramParcel, bool);
    if (hdrStaticInfo != null) {
      paramParcel.writeByteArray(hdrStaticInfo);
    }
  }
}