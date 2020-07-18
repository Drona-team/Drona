package com.google.android.exoplayer2.text.ttml;

import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.util.Util;
import java.util.Collections;
import java.util.List;
import java.util.Map;

final class TtmlSubtitle
  implements Subtitle
{
  private final long[] eventTimesUs;
  private final Map<String, TtmlStyle> globalStyles;
  private final Map<String, TtmlRegion> regionMap;
  private final TtmlNode root;
  
  public TtmlSubtitle(TtmlNode paramTtmlNode, Map paramMap1, Map paramMap2)
  {
    root = paramTtmlNode;
    regionMap = paramMap2;
    if (paramMap1 != null) {
      paramMap1 = Collections.unmodifiableMap(paramMap1);
    } else {
      paramMap1 = Collections.emptyMap();
    }
    globalStyles = paramMap1;
    eventTimesUs = paramTtmlNode.getEventTimesUs();
  }
  
  public List getCues(long paramLong)
  {
    return root.getCues(paramLong, globalStyles, regionMap);
  }
  
  public long getEventTime(int paramInt)
  {
    return eventTimesUs[paramInt];
  }
  
  public int getEventTimeCount()
  {
    return eventTimesUs.length;
  }
  
  Map getGlobalStyles()
  {
    return globalStyles;
  }
  
  public int getNextEventTimeIndex(long paramLong)
  {
    int i = Util.binarySearchCeil(eventTimesUs, paramLong, false, false);
    if (i < eventTimesUs.length) {
      return i;
    }
    return -1;
  }
  
  TtmlNode getRoot()
  {
    return root;
  }
}
