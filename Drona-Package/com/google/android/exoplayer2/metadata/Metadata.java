package com.google.android.exoplayer2.metadata;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class Metadata
  implements Parcelable
{
  public static final Parcelable.Creator<Metadata> CREATOR = new Parcelable.Creator()
  {
    public Metadata createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Metadata(paramAnonymousParcel);
    }
    
    public Metadata[] newArray(int paramAnonymousInt)
    {
      return new Metadata[0];
    }
  };
  private final Entry[] entries;
  
  Metadata(Parcel paramParcel)
  {
    entries = new Entry[paramParcel.readInt()];
    int i = 0;
    while (i < entries.length)
    {
      entries[i] = ((Entry)paramParcel.readParcelable(Entry.class.getClassLoader()));
      i += 1;
    }
  }
  
  public Metadata(List paramList)
  {
    if (paramList != null)
    {
      entries = new Entry[paramList.size()];
      paramList.toArray(entries);
      return;
    }
    entries = new Entry[0];
  }
  
  public Metadata(Entry... paramVarArgs)
  {
    Entry[] arrayOfEntry = paramVarArgs;
    if (paramVarArgs == null) {
      arrayOfEntry = new Entry[0];
    }
    entries = arrayOfEntry;
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
    if ((paramObject != null) && (getClass() == paramObject.getClass()))
    {
      paramObject = (Metadata)paramObject;
      return Arrays.equals(entries, entries);
    }
    return false;
  }
  
  public Entry getFormat(int paramInt)
  {
    return entries[paramInt];
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(entries);
  }
  
  public int length()
  {
    return entries.length;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(entries.length);
    Entry[] arrayOfEntry = entries;
    int i = arrayOfEntry.length;
    paramInt = 0;
    while (paramInt < i)
    {
      paramParcel.writeParcelable(arrayOfEntry[paramInt], 0);
      paramInt += 1;
    }
  }
  
  public static abstract interface Entry
    extends Parcelable
  {}
}
