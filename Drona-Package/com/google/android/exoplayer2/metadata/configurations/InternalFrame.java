package com.google.android.exoplayer2.metadata.configurations;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.util.Util;

public final class InternalFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<com.google.android.exoplayer2.metadata.id3.InternalFrame> CREATOR = new Parcelable.Creator()
  {
    public InternalFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new InternalFrame(paramAnonymousParcel);
    }
    
    public InternalFrame[] newArray(int paramAnonymousInt)
    {
      return new InternalFrame[paramAnonymousInt];
    }
  };
  public static final String IDENTIFIER = "----";
  public final String description;
  public final String domain;
  public final String text;
  
  InternalFrame(Parcel paramParcel)
  {
    super("----");
    domain = ((String)Util.castNonNull(paramParcel.readString()));
    description = ((String)Util.castNonNull(paramParcel.readString()));
    text = ((String)Util.castNonNull(paramParcel.readString()));
  }
  
  public InternalFrame(String paramString1, String paramString2, String paramString3)
  {
    super("----");
    domain = paramString1;
    description = paramString2;
    text = paramString3;
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
      paramObject = (InternalFrame)paramObject;
      return (Util.areEqual(description, description)) && (Util.areEqual(domain, domain)) && (Util.areEqual(text, text));
    }
    return false;
  }
  
  public int hashCode()
  {
    String str = domain;
    int k = 0;
    int i;
    if (str != null) {
      i = domain.hashCode();
    } else {
      i = 0;
    }
    int j;
    if (description != null) {
      j = description.hashCode();
    } else {
      j = 0;
    }
    if (text != null) {
      k = text.hashCode();
    }
    return ((527 + i) * 31 + j) * 31 + k;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(id);
    localStringBuilder.append(": domain=");
    localStringBuilder.append(domain);
    localStringBuilder.append(", description=");
    localStringBuilder.append(description);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(id);
    paramParcel.writeString(domain);
    paramParcel.writeString(text);
  }
}
