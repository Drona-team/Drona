package com.google.android.exoplayer2.text.webvtt;

import android.text.SpannableStringBuilder;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class WebvttSubtitle
  implements Subtitle
{
  private final long[] cueTimesUs;
  private final List<WebvttCue> cues;
  private final int numCues;
  private final long[] sortedCueTimesUs;
  
  public WebvttSubtitle(List paramList)
  {
    cues = paramList;
    numCues = paramList.size();
    cueTimesUs = new long[numCues * 2];
    int i = 0;
    while (i < numCues)
    {
      WebvttCue localWebvttCue = (WebvttCue)paramList.get(i);
      int j = i * 2;
      cueTimesUs[j] = startTime;
      cueTimesUs[(j + 1)] = endTime;
      i += 1;
    }
    sortedCueTimesUs = Arrays.copyOf(cueTimesUs, cueTimesUs.length);
    Arrays.sort(sortedCueTimesUs);
  }
  
  public List getCues(long paramLong)
  {
    Object localObject4 = null;
    int i = 0;
    Object localObject1 = null;
    Object localObject6;
    for (Object localObject3 = null; i < numCues; localObject3 = localObject6)
    {
      long[] arrayOfLong = cueTimesUs;
      int j = i * 2;
      Object localObject5 = localObject4;
      Object localObject2 = localObject1;
      localObject6 = localObject3;
      if (arrayOfLong[j] <= paramLong)
      {
        localObject5 = localObject4;
        localObject2 = localObject1;
        localObject6 = localObject3;
        if (paramLong < cueTimesUs[(j + 1)])
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new ArrayList();
          }
          localObject6 = (WebvttCue)cues.get(i);
          if (((WebvttCue)localObject6).isNormalCue())
          {
            if (localObject3 == null)
            {
              localObject5 = localObject4;
            }
            else if (localObject4 == null)
            {
              localObject5 = new SpannableStringBuilder();
              ((SpannableStringBuilder)localObject5).append(text).append("\n").append(text);
              localObject6 = localObject3;
            }
            else
            {
              localObject4.append("\n").append(text);
              localObject5 = localObject4;
              localObject6 = localObject3;
            }
          }
          else
          {
            ((ArrayList)localObject2).add(localObject6);
            localObject6 = localObject3;
            localObject5 = localObject4;
          }
        }
      }
      i += 1;
      localObject4 = localObject5;
      localObject1 = localObject2;
    }
    if (localObject4 != null) {
      localObject1.add(new WebvttCue(localObject4));
    } else if (localObject3 != null) {
      localObject1.add(localObject3);
    }
    if (localObject1 != null) {
      return localObject1;
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
    if (paramInt < sortedCueTimesUs.length) {
      bool1 = true;
    }
    Assertions.checkArgument(bool1);
    return sortedCueTimesUs[paramInt];
  }
  
  public int getEventTimeCount()
  {
    return sortedCueTimesUs.length;
  }
  
  public int getNextEventTimeIndex(long paramLong)
  {
    int i = Util.binarySearchCeil(sortedCueTimesUs, paramLong, false, false);
    if (i < sortedCueTimesUs.length) {
      return i;
    }
    return -1;
  }
}
