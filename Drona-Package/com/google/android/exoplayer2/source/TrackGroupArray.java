package com.google.android.exoplayer2.source;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public final class TrackGroupArray
  implements Parcelable
{
  public static final Parcelable.Creator<TrackGroupArray> CREATOR = new Parcelable.Creator()
  {
    public TrackGroupArray createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TrackGroupArray(paramAnonymousParcel);
    }
    
    public TrackGroupArray[] newArray(int paramAnonymousInt)
    {
      return new TrackGroupArray[paramAnonymousInt];
    }
  };
  public static final TrackGroupArray EMPTY = new TrackGroupArray(new TrackGroup[0]);
  private int hashCode;
  public final int length;
  private final TrackGroup[] trackGroups;
  
  TrackGroupArray(Parcel paramParcel)
  {
    length = paramParcel.readInt();
    trackGroups = new TrackGroup[length];
    int i = 0;
    while (i < length)
    {
      trackGroups[i] = ((TrackGroup)paramParcel.readParcelable(TrackGroup.class.getClassLoader()));
      i += 1;
    }
  }
  
  public TrackGroupArray(TrackGroup... paramVarArgs)
  {
    trackGroups = paramVarArgs;
    length = paramVarArgs.length;
  }
  
  public TrackGroup context(int paramInt)
  {
    return trackGroups[paramInt];
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
      paramObject = (TrackGroupArray)paramObject;
      return (length == length) && (Arrays.equals(trackGroups, trackGroups));
    }
    return false;
  }
  
  public int hashCode()
  {
    if (hashCode == 0) {
      hashCode = Arrays.hashCode(trackGroups);
    }
    return hashCode;
  }
  
  public int indexOf(TrackGroup paramTrackGroup)
  {
    int i = 0;
    while (i < length)
    {
      if (trackGroups[i] == paramTrackGroup) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public boolean isEmpty()
  {
    return length == 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(length);
    paramInt = 0;
    while (paramInt < length)
    {
      paramParcel.writeParcelable(trackGroups[paramInt], 0);
      paramInt += 1;
    }
  }
}
