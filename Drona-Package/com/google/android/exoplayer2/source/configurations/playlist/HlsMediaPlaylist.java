package com.google.android.exoplayer2.source.configurations.playlist;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.upgrade.DrmInitData;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;

public final class HlsMediaPlaylist
  extends HlsPlaylist
{
  public static final int PLAYLIST_TYPE_EVENT = 2;
  public static final int PLAYLIST_TYPE_UNKNOWN = 0;
  public static final int PLAYLIST_TYPE_VOD = 1;
  public final int discontinuitySequence;
  public final long durationUs;
  public final boolean hasDiscontinuitySequence;
  public final boolean hasEndTag;
  public final boolean hasProgramDateTime;
  public final long mediaSequence;
  public final int playlistType;
  @Nullable
  public final DrmInitData protectionSchemes;
  public final List<com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment> segments;
  public final long startOffsetUs;
  public final long startTimeUs;
  public final long targetDurationUs;
  public final int version;
  
  public HlsMediaPlaylist(int paramInt1, String paramString, List paramList1, long paramLong1, long paramLong2, boolean paramBoolean1, int paramInt2, long paramLong3, int paramInt3, long paramLong4, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, DrmInitData paramDrmInitData, List paramList2)
  {
    super(paramString, paramList1, paramBoolean2);
    playlistType = paramInt1;
    startTimeUs = paramLong2;
    hasDiscontinuitySequence = paramBoolean1;
    discontinuitySequence = paramInt2;
    mediaSequence = paramLong3;
    version = paramInt3;
    targetDurationUs = paramLong4;
    hasEndTag = paramBoolean3;
    hasProgramDateTime = paramBoolean4;
    protectionSchemes = paramDrmInitData;
    segments = Collections.unmodifiableList(paramList2);
    if (!paramList2.isEmpty())
    {
      paramString = (Segment)paramList2.get(paramList2.size() - 1);
      durationUs = (relativeStartTimeUs + durationUs);
    }
    else
    {
      durationUs = 0L;
    }
    if (paramLong1 == -9223372036854775807L) {
      paramLong1 = -9223372036854775807L;
    } else if (paramLong1 < 0L) {
      paramLong1 = durationUs + paramLong1;
    }
    startOffsetUs = paramLong1;
  }
  
  public HlsMediaPlaylist copy(List paramList)
  {
    return this;
  }
  
  public HlsMediaPlaylist copyWith(long paramLong, int paramInt)
  {
    return new HlsMediaPlaylist(playlistType, baseUri, tags, startOffsetUs, paramLong, true, paramInt, mediaSequence, version, targetDurationUs, hasIndependentSegments, hasEndTag, hasProgramDateTime, protectionSchemes, segments);
  }
  
  public HlsMediaPlaylist copyWithEndTag()
  {
    if (hasEndTag) {
      return this;
    }
    return new HlsMediaPlaylist(playlistType, baseUri, tags, startOffsetUs, startTimeUs, hasDiscontinuitySequence, discontinuitySequence, mediaSequence, version, targetDurationUs, hasIndependentSegments, true, hasProgramDateTime, protectionSchemes, segments);
  }
  
  public long getEndTimeUs()
  {
    return startTimeUs + durationUs;
  }
  
  public boolean isNewerThan(HlsMediaPlaylist paramHlsMediaPlaylist)
  {
    if (paramHlsMediaPlaylist != null)
    {
      if (mediaSequence > mediaSequence) {
        return true;
      }
      if (mediaSequence < mediaSequence) {
        return false;
      }
      int i = segments.size();
      int j = segments.size();
      if (i <= j) {
        return (i == j) && (hasEndTag) && (!hasEndTag);
      }
    }
    return true;
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface PlaylistType {}
  
  public final class Segment
    implements Comparable<Long>
  {
    public final long byterangeLength;
    public final long byterangeOffset;
    @Nullable
    public final DrmInitData drmInitData;
    public final long durationUs;
    @Nullable
    public final String encryptionIV;
    @Nullable
    public final String fullSegmentEncryptionKeyUri;
    public final boolean hasGapTag;
    @Nullable
    public final Segment initializationSegment;
    public final int relativeDiscontinuitySequence;
    public final long relativeStartTimeUs;
    public final String title;
    
    public Segment(long paramLong1, long paramLong2)
    {
      this(null, "", 0L, -1, -9223372036854775807L, null, null, null, paramLong1, paramLong2, false);
    }
    
    public Segment(Segment paramSegment, String paramString1, long paramLong1, int paramInt, long paramLong2, DrmInitData paramDrmInitData, String paramString2, String paramString3, long paramLong3, long paramLong4, boolean paramBoolean)
    {
      initializationSegment = paramSegment;
      title = paramString1;
      durationUs = paramLong1;
      relativeDiscontinuitySequence = paramInt;
      relativeStartTimeUs = paramLong2;
      drmInitData = paramDrmInitData;
      fullSegmentEncryptionKeyUri = paramString2;
      encryptionIV = paramString3;
      byterangeOffset = paramLong3;
      byterangeLength = paramLong4;
      hasGapTag = paramBoolean;
    }
    
    public int compareTo(Long paramLong)
    {
      if (relativeStartTimeUs > paramLong.longValue()) {
        return 1;
      }
      if (relativeStartTimeUs < paramLong.longValue()) {
        return -1;
      }
      return 0;
    }
  }
}
