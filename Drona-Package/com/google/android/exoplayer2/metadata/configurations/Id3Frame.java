package com.google.android.exoplayer2.metadata.configurations;

import com.google.android.exoplayer2.metadata.Metadata.Entry;

public abstract class Id3Frame
  implements Metadata.Entry
{
  public final String id;
  
  public Id3Frame(String paramString)
  {
    id = paramString;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return id;
  }
}
