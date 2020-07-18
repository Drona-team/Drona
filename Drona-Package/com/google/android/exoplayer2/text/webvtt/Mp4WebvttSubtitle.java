package com.google.android.exoplayer2.text.webvtt;

import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Collections;
import java.util.List;

final class Mp4WebvttSubtitle
  implements Subtitle
{
  private final List<Cue> cues;
  
  public Mp4WebvttSubtitle(List paramList)
  {
    cues = Collections.unmodifiableList(paramList);
  }
  
  public List getCues(long paramLong)
  {
    if (paramLong >= 0L) {
      return cues;
    }
    return Collections.emptyList();
  }
  
  public long getEventTime(int paramInt)
  {
    boolean bool;
    if (paramInt == 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    return 0L;
  }
  
  public int getEventTimeCount()
  {
    return 1;
  }
  
  public int getNextEventTimeIndex(long paramLong)
  {
    if (paramLong < 0L) {
      return 0;
    }
    return -1;
  }
}
