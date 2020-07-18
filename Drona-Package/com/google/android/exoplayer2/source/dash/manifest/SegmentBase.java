package com.google.android.exoplayer2.source.dash.manifest;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.Util;
import java.util.List;

public abstract class SegmentBase
{
  final RangedUri initialization;
  final long presentationTimeOffset;
  final long timescale;
  
  public SegmentBase(RangedUri paramRangedUri, long paramLong1, long paramLong2)
  {
    initialization = paramRangedUri;
    timescale = paramLong1;
    presentationTimeOffset = paramLong2;
  }
  
  public RangedUri getInitialization(Representation paramRepresentation)
  {
    return initialization;
  }
  
  public long getPresentationTimeOffsetUs()
  {
    return Util.scaleLargeTimestamp(presentationTimeOffset, 1000000L, timescale);
  }
  
  public static abstract class MultiSegmentBase
    extends SegmentBase
  {
    final long duration;
    final List<SegmentBase.SegmentTimelineElement> segmentTimeline;
    final long startNumber;
    
    public MultiSegmentBase(RangedUri paramRangedUri, long paramLong1, long paramLong2, long paramLong3, long paramLong4, List paramList)
    {
      super(paramLong1, paramLong2);
      startNumber = paramLong3;
      duration = paramLong4;
      segmentTimeline = paramList;
    }
    
    public long getFirstSegmentNum()
    {
      return startNumber;
    }
    
    public abstract int getSegmentCount(long paramLong);
    
    public final long getSegmentDurationUs(long paramLong1, long paramLong2)
    {
      if (segmentTimeline != null) {
        return segmentTimeline.get((int)(paramLong1 - startNumber))).duration * 1000000L / timescale;
      }
      int i = getSegmentCount(paramLong2);
      if ((i != -1) && (paramLong1 == getFirstSegmentNum() + i - 1L)) {
        return paramLong2 - getSegmentTimeUs(paramLong1);
      }
      return duration * 1000000L / timescale;
    }
    
    public long getSegmentNum(long paramLong1, long paramLong2)
    {
      long l2 = getFirstSegmentNum();
      paramLong2 = getSegmentCount(paramLong2);
      if (paramLong2 == 0L) {
        return l2;
      }
      long l3;
      if (segmentTimeline == null)
      {
        l1 = duration * 1000000L / timescale;
        l3 = startNumber;
        paramLong1 = paramLong1 / l1 + l3;
        if (paramLong1 < l2) {
          return l2;
        }
        if (paramLong2 == -1L) {
          return paramLong1;
        }
        return Math.min(paramLong1, l2 + paramLong2 - 1L);
      }
      paramLong2 = paramLong2 + l2 - 1L;
      long l1 = l2;
      while (l1 <= paramLong2)
      {
        l3 = (paramLong2 - l1) / 2L + l1;
        boolean bool = getSegmentTimeUs(l3) < paramLong1;
        if (bool) {
          l1 = l3 + 1L;
        } else if (bool) {
          paramLong2 = l3 - 1L;
        } else {
          return l3;
        }
      }
      if (l1 == l2) {
        return l1;
      }
      return paramLong2;
    }
    
    public final long getSegmentTimeUs(long paramLong)
    {
      if (segmentTimeline != null) {}
      for (paramLong = segmentTimeline.get((int)(paramLong - startNumber))).startTime - presentationTimeOffset;; paramLong = (paramLong - startNumber) * duration) {
        break;
      }
      return Util.scaleLargeTimestamp(paramLong, 1000000L, timescale);
    }
    
    public abstract RangedUri getSegmentUrl(Representation paramRepresentation, long paramLong);
    
    public boolean isExplicit()
    {
      return segmentTimeline != null;
    }
  }
  
  public static class SegmentList
    extends SegmentBase.MultiSegmentBase
  {
    final List<RangedUri> mediaSegments;
    
    public SegmentList(RangedUri paramRangedUri, long paramLong1, long paramLong2, long paramLong3, long paramLong4, List paramList1, List paramList2)
    {
      super(paramLong1, paramLong2, paramLong3, paramLong4, paramList1);
      mediaSegments = paramList2;
    }
    
    public int getSegmentCount(long paramLong)
    {
      return mediaSegments.size();
    }
    
    public RangedUri getSegmentUrl(Representation paramRepresentation, long paramLong)
    {
      return (RangedUri)mediaSegments.get((int)(paramLong - startNumber));
    }
    
    public boolean isExplicit()
    {
      return true;
    }
  }
  
  public static class SegmentTemplate
    extends SegmentBase.MultiSegmentBase
  {
    final UrlTemplate initializationTemplate;
    final UrlTemplate mediaTemplate;
    
    public SegmentTemplate(RangedUri paramRangedUri, long paramLong1, long paramLong2, long paramLong3, long paramLong4, List paramList, UrlTemplate paramUrlTemplate1, UrlTemplate paramUrlTemplate2)
    {
      super(paramLong1, paramLong2, paramLong3, paramLong4, paramList);
      initializationTemplate = paramUrlTemplate1;
      mediaTemplate = paramUrlTemplate2;
    }
    
    public RangedUri getInitialization(Representation paramRepresentation)
    {
      if (initializationTemplate != null) {
        return new RangedUri(initializationTemplate.buildUri(format.mimeType, 0L, format.bitrate, 0L), 0L, -1L);
      }
      return super.getInitialization(paramRepresentation);
    }
    
    public int getSegmentCount(long paramLong)
    {
      if (segmentTimeline != null) {
        return segmentTimeline.size();
      }
      if (paramLong != -9223372036854775807L) {
        return (int)Util.ceilDivide(paramLong, duration * 1000000L / timescale);
      }
      return -1;
    }
    
    public RangedUri getSegmentUrl(Representation paramRepresentation, long paramLong)
    {
      if (segmentTimeline != null) {}
      for (long l = segmentTimeline.get((int)(paramLong - startNumber))).startTime;; l = (paramLong - startNumber) * duration) {
        break;
      }
      return new RangedUri(mediaTemplate.buildUri(format.mimeType, paramLong, format.bitrate, l), 0L, -1L);
    }
  }
  
  public static class SegmentTimelineElement
  {
    final long duration;
    final long startTime;
    
    public SegmentTimelineElement(long paramLong1, long paramLong2)
    {
      startTime = paramLong1;
      duration = paramLong2;
    }
  }
  
  public static class SingleSegmentBase
    extends SegmentBase
  {
    final long indexLength;
    final long indexStart;
    
    public SingleSegmentBase()
    {
      this(null, 1L, 0L, 0L, 0L);
    }
    
    public SingleSegmentBase(RangedUri paramRangedUri, long paramLong1, long paramLong2, long paramLong3, long paramLong4)
    {
      super(paramLong1, paramLong2);
      indexStart = paramLong3;
      indexLength = paramLong4;
    }
    
    public RangedUri getIndex()
    {
      if (indexLength <= 0L) {
        return null;
      }
      return new RangedUri(null, indexStart, indexLength);
    }
  }
}
