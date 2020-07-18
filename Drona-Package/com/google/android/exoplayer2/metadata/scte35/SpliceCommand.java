package com.google.android.exoplayer2.metadata.scte35;

import com.google.android.exoplayer2.metadata.Metadata.Entry;

public abstract class SpliceCommand
  implements Metadata.Entry
{
  public SpliceCommand() {}
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SCTE-35 splice command: type=");
    localStringBuilder.append(getClass().getSimpleName());
    return localStringBuilder.toString();
  }
}
