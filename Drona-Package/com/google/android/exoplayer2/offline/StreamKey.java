package com.google.android.exoplayer2.offline;

public final class StreamKey
  implements Comparable<StreamKey>
{
  public final int groupIndex;
  public final int periodIndex;
  public final int trackIndex;
  
  public StreamKey(int paramInt1, int paramInt2)
  {
    this(0, paramInt1, paramInt2);
  }
  
  public StreamKey(int paramInt1, int paramInt2, int paramInt3)
  {
    periodIndex = paramInt1;
    groupIndex = paramInt2;
    trackIndex = paramInt3;
  }
  
  public int compareTo(StreamKey paramStreamKey)
  {
    int j = periodIndex - periodIndex;
    int i = j;
    if (j == 0)
    {
      j = groupIndex - groupIndex;
      i = j;
      if (j == 0) {
        i = trackIndex - trackIndex;
      }
    }
    return i;
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
      paramObject = (StreamKey)paramObject;
      return (periodIndex == periodIndex) && (groupIndex == groupIndex) && (trackIndex == trackIndex);
    }
    return false;
  }
  
  public int hashCode()
  {
    return (periodIndex * 31 + groupIndex) * 31 + trackIndex;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(periodIndex);
    localStringBuilder.append(".");
    localStringBuilder.append(groupIndex);
    localStringBuilder.append(".");
    localStringBuilder.append(trackIndex);
    return localStringBuilder.toString();
  }
}
