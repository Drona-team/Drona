package com.google.android.exoplayer2;

import com.google.android.exoplayer2.util.Assertions;

public final class SeekParameters
{
  public static final SeekParameters CLOSEST_SYNC;
  public static final SeekParameters DEFAULT = EXACT;
  public static final SeekParameters EXACT = new SeekParameters(0L, 0L);
  public static final SeekParameters NEXT_SYNC;
  public static final SeekParameters PREVIOUS_SYNC;
  public final long toleranceAfterUs;
  public final long toleranceBeforeUs;
  
  static
  {
    CLOSEST_SYNC = new SeekParameters(Long.MAX_VALUE, Long.MAX_VALUE);
    PREVIOUS_SYNC = new SeekParameters(Long.MAX_VALUE, 0L);
    NEXT_SYNC = new SeekParameters(0L, Long.MAX_VALUE);
  }
  
  public SeekParameters(long paramLong1, long paramLong2)
  {
    boolean bool2 = false;
    if (paramLong1 >= 0L) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.checkArgument(bool1);
    boolean bool1 = bool2;
    if (paramLong2 >= 0L) {
      bool1 = true;
    }
    Assertions.checkArgument(bool1);
    toleranceBeforeUs = paramLong1;
    toleranceAfterUs = paramLong2;
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
      paramObject = (SeekParameters)paramObject;
      return (toleranceBeforeUs == toleranceBeforeUs) && (toleranceAfterUs == toleranceAfterUs);
    }
    return false;
  }
  
  public int hashCode()
  {
    return (int)toleranceBeforeUs * 31 + (int)toleranceAfterUs;
  }
}
