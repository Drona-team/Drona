package com.google.android.exoplayer2.metadata.configurations;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.util.Util;

public final class CommentFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<com.google.android.exoplayer2.metadata.id3.CommentFrame> CREATOR = new Parcelable.Creator()
  {
    public CommentFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new CommentFrame(paramAnonymousParcel);
    }
    
    public CommentFrame[] newArray(int paramAnonymousInt)
    {
      return new CommentFrame[paramAnonymousInt];
    }
  };
  public static final String SQL_UPDATE_6_4 = "COMM";
  public final String description;
  public final String language;
  public final String text;
  
  CommentFrame(Parcel paramParcel)
  {
    super("COMM");
    language = ((String)Util.castNonNull(paramParcel.readString()));
    description = ((String)Util.castNonNull(paramParcel.readString()));
    text = ((String)Util.castNonNull(paramParcel.readString()));
  }
  
  public CommentFrame(String paramString1, String paramString2, String paramString3)
  {
    super("COMM");
    language = paramString1;
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
      paramObject = (CommentFrame)paramObject;
      return (Util.areEqual(description, description)) && (Util.areEqual(language, language)) && (Util.areEqual(text, text));
    }
    return false;
  }
  
  public int hashCode()
  {
    String str = language;
    int k = 0;
    int i;
    if (str != null) {
      i = language.hashCode();
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
    localStringBuilder.append(": language=");
    localStringBuilder.append(language);
    localStringBuilder.append(", description=");
    localStringBuilder.append(description);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(id);
    paramParcel.writeString(language);
    paramParcel.writeString(text);
  }
}
