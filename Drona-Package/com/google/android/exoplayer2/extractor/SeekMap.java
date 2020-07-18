package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.util.Assertions;

public abstract interface SeekMap
{
  public abstract long getDurationUs();
  
  public abstract SeekPoints getSeekPoints(long paramLong);
  
  public abstract boolean isSeekable();
  
  public static final class SeekPoints
  {
    public final SeekPoint first;
    public final SeekPoint second;
    
    public SeekPoints(SeekPoint paramSeekPoint)
    {
      this(paramSeekPoint, paramSeekPoint);
    }
    
    public SeekPoints(SeekPoint paramSeekPoint1, SeekPoint paramSeekPoint2)
    {
      first = ((SeekPoint)Assertions.checkNotNull(paramSeekPoint1));
      second = ((SeekPoint)Assertions.checkNotNull(paramSeekPoint2));
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
        paramObject = (SeekPoints)paramObject;
        return (first.equals(first)) && (second.equals(second));
      }
      return false;
    }
    
    public int hashCode()
    {
      return first.hashCode() * 31 + second.hashCode();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      localStringBuilder.append(first);
      Object localObject;
      if (first.equals(second))
      {
        localObject = "";
      }
      else
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append(", ");
        ((StringBuilder)localObject).append(second);
        localObject = ((StringBuilder)localObject).toString();
      }
      localStringBuilder.append((String)localObject);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  public static final class Unseekable
    implements SeekMap
  {
    private final long durationUs;
    private final SeekMap.SeekPoints startSeekPoints;
    
    public Unseekable(long paramLong)
    {
      this(paramLong, 0L);
    }
    
    public Unseekable(long paramLong1, long paramLong2)
    {
      durationUs = paramLong1;
      SeekPoint localSeekPoint;
      if (paramLong2 == 0L) {
        localSeekPoint = SeekPoint.START;
      } else {
        localSeekPoint = new SeekPoint(0L, paramLong2);
      }
      startSeekPoints = new SeekMap.SeekPoints(localSeekPoint);
    }
    
    public long getDurationUs()
    {
      return durationUs;
    }
    
    public SeekMap.SeekPoints getSeekPoints(long paramLong)
    {
      return startSeekPoints;
    }
    
    public boolean isSeekable()
    {
      return false;
    }
  }
}
