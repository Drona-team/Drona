package com.google.android.exoplayer2.metadata.configurations;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;

public final class TextInformationFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<com.google.android.exoplayer2.metadata.id3.TextInformationFrame> CREATOR = new Parcelable.Creator()
  {
    public TextInformationFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TextInformationFrame(paramAnonymousParcel);
    }
    
    public TextInformationFrame[] newArray(int paramAnonymousInt)
    {
      return new TextInformationFrame[paramAnonymousInt];
    }
  };
  @Nullable
  public final String description;
  public final String value;
  
  TextInformationFrame(Parcel paramParcel)
  {
    super((String)Util.castNonNull(paramParcel.readString()));
    description = paramParcel.readString();
    value = ((String)Util.castNonNull(paramParcel.readString()));
  }
  
  public TextInformationFrame(String paramString1, String paramString2, String paramString3)
  {
    super(paramString1);
    description = paramString2;
    value = paramString3;
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
      paramObject = (TextInformationFrame)paramObject;
      return (id.equals(id)) && (Util.areEqual(description, description)) && (Util.areEqual(value, value));
    }
    return false;
  }
  
  public int hashCode()
  {
    int k = id.hashCode();
    String str = description;
    int j = 0;
    int i;
    if (str != null) {
      i = description.hashCode();
    } else {
      i = 0;
    }
    if (value != null) {
      j = value.hashCode();
    }
    return ((527 + k) * 31 + i) * 31 + j;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(id);
    localStringBuilder.append(": value=");
    localStringBuilder.append(value);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(id);
    paramParcel.writeString(description);
    paramParcel.writeString(value);
  }
}
