package com.google.android.exoplayer2.text.subrip;

import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.Collections;
import java.util.List;

final class SubripSubtitle
  implements Subtitle
{
  private final long[] cueTimesUs;
  private final Cue[] cues;
  
  public SubripSubtitle(Cue[] paramArrayOfCue, long[] paramArrayOfLong)
  {
    cues = paramArrayOfCue;
    cueTimesUs = paramArrayOfLong;
  }
  
  public List getCues(long paramLong)
  {
    int i = Util.binarySearchFloor(cueTimesUs, paramLong, true, false);
    if ((i != -1) && (cues[i] != null)) {
      return Collections.singletonList(cues[i]);
    }
    return Collections.emptyList();
  }
  
  public long getEventTime(int paramInt)
  {
    boolean bool2 = false;
    if (paramInt >= 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.checkArgument(bool1);
    boolean bool1 = bool2;
    if (paramInt < cueTimesUs.length) {
      bool1 = true;
    }
    Assertions.checkArgument(bool1);
    return cueTimesUs[paramInt];
  }
  
  public int getEventTimeCount()
  {
    return cueTimesUs.length;
  }
  
  public int getNextEventTimeIndex(long paramLong)
  {
    int i = Util.binarySearchCeil(cueTimesUs, paramLong, false, false);
    if (i < cueTimesUs.length) {
      return i;
    }
    return -1;
  }
}
