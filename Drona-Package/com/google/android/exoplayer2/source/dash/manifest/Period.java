package com.google.android.exoplayer2.source.dash.manifest;

import androidx.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class Period
{
  public final List<AdaptationSet> adaptationSets;
  public final List<EventStream> eventStreams;
  @Nullable
  public final String size;
  public final long startMs;
  
  public Period(String paramString, long paramLong, List paramList)
  {
    this(paramString, paramLong, paramList, Collections.emptyList());
  }
  
  public Period(String paramString, long paramLong, List paramList1, List paramList2)
  {
    size = paramString;
    startMs = paramLong;
    adaptationSets = Collections.unmodifiableList(paramList1);
    eventStreams = Collections.unmodifiableList(paramList2);
  }
  
  public int getAdaptationSetIndex(int paramInt)
  {
    int j = adaptationSets.size();
    int i = 0;
    while (i < j)
    {
      if (adaptationSets.get(i)).type == paramInt) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
}
