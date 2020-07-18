package com.google.android.exoplayer2.extractor;

public final class SeekPoint
{
  public static final SeekPoint START = new SeekPoint(0L, 0L);
  public final long position;
  public final long timeUs;
  
  public SeekPoint(long paramLong1, long paramLong2)
  {
    timeUs = paramLong1;
    position = paramLong2;
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
      paramObject = (SeekPoint)paramObject;
      return (timeUs == timeUs) && (position == position);
    }
    return false;
  }
  
  public int hashCode()
  {
    return (int)timeUs * 31 + (int)position;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[timeUs=");
    localStringBuilder.append(timeUs);
    localStringBuilder.append(", position=");
    localStringBuilder.append(position);
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}
