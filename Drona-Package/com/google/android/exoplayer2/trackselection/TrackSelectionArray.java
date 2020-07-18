package com.google.android.exoplayer2.trackselection;

import java.util.Arrays;

public final class TrackSelectionArray
{
  private int hashCode;
  public final int length;
  private final TrackSelection[] trackSelections;
  
  public TrackSelectionArray(TrackSelection... paramVarArgs)
  {
    trackSelections = paramVarArgs;
    length = paramVarArgs.length;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject != null) && (getClass() == paramObject.getClass()))
    {
      paramObject = (TrackSelectionArray)paramObject;
      return Arrays.equals(trackSelections, trackSelections);
    }
    return false;
  }
  
  public TrackSelection[] getAll()
  {
    return (TrackSelection[])trackSelections.clone();
  }
  
  public TrackSelection getChapters(int paramInt)
  {
    return trackSelections[paramInt];
  }
  
  public int hashCode()
  {
    if (hashCode == 0) {
      hashCode = (527 + Arrays.hashCode(trackSelections));
    }
    return hashCode;
  }
}
