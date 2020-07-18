package com.google.android.exoplayer2.metadata.configurations;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;

public final class UrlLinkFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<com.google.android.exoplayer2.metadata.id3.UrlLinkFrame> CREATOR = new Parcelable.Creator()
  {
    public UrlLinkFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new UrlLinkFrame(paramAnonymousParcel);
    }
    
    public UrlLinkFrame[] newArray(int paramAnonymousInt)
    {
      return new UrlLinkFrame[paramAnonymousInt];
    }
  };
  @Nullable
  public final String description;
  public final String mimeType;
  
  UrlLinkFrame(Parcel paramParcel)
  {
    super((String)Util.castNonNull(paramParcel.readString()));
    description = paramParcel.readString();
    mimeType = ((String)Util.castNonNull(paramParcel.readString()));
  }
  
  public UrlLinkFrame(String paramString1, String paramString2, String paramString3)
  {
    super(paramString1);
    description = paramString2;
    mimeType = paramString3;
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
      paramObject = (UrlLinkFrame)paramObject;
      return (id.equals(id)) && (Util.areEqual(description, description)) && (Util.areEqual(mimeType, mimeType));
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
    if (mimeType != null) {
      j = mimeType.hashCode();
    }
    return ((527 + k) * 31 + i) * 31 + j;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(id);
    localStringBuilder.append(": url=");
    localStringBuilder.append(mimeType);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(id);
    paramParcel.writeString(description);
    paramParcel.writeString(mimeType);
  }
}
