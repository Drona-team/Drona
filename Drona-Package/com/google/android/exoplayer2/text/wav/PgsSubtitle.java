package com.google.android.exoplayer2.text.wav;

import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.Subtitle;
import java.util.List;

final class PgsSubtitle
  implements Subtitle
{
  private final List<Cue> cues;
  
  public PgsSubtitle(List paramList)
  {
    cues = paramList;
  }
  
  public List getCues(long paramLong)
  {
    return cues;
  }
  
  public long getEventTime(int paramInt)
  {
    return 0L;
  }
  
  public int getEventTimeCount()
  {
    return 1;
  }
  
  public int getNextEventTimeIndex(long paramLong)
  {
    return -1;
  }
}
