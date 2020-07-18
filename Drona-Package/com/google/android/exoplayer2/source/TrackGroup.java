package com.google.android.exoplayer2.source;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Arrays;

public final class TrackGroup
  implements Parcelable
{
  public static final Parcelable.Creator<TrackGroup> CREATOR = new Parcelable.Creator()
  {
    public TrackGroup createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TrackGroup(paramAnonymousParcel);
    }
    
    public TrackGroup[] newArray(int paramAnonymousInt)
    {
      return new TrackGroup[paramAnonymousInt];
    }
  };
  private final Format[] formats;
  private int hashCode;
  public final int length;
  
  TrackGroup(Parcel paramParcel)
  {
    length = paramParcel.readInt();
    formats = new Format[length];
    int i = 0;
    while (i < length)
    {
      formats[i] = ((Format)paramParcel.readParcelable(Format.class.getClassLoader()));
      i += 1;
    }
  }
  
  public TrackGroup(Format... paramVarArgs)
  {
    boolean bool;
    if (paramVarArgs.length > 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    formats = paramVarArgs;
    length = paramVarArgs.length;
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
      paramObject = (TrackGroup)paramObject;
      return (length == length) && (Arrays.equals(formats, formats));
    }
    return false;
  }
  
  public Format getFormat(int paramInt)
  {
    return formats[paramInt];
  }
  
  public int hashCode()
  {
    if (hashCode == 0) {
      hashCode = (527 + Arrays.hashCode(formats));
    }
    return hashCode;
  }
  
  public int indexOf(Format paramFormat)
  {
    int i = 0;
    while (i < formats.length)
    {
      if (paramFormat == formats[i]) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(length);
    paramInt = 0;
    while (paramInt < length)
    {
      paramParcel.writeParcelable(formats[paramInt], 0);
      paramInt += 1;
    }
  }
}
