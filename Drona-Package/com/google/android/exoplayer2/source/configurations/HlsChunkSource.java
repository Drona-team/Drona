package com.google.android.exoplayer2.source.configurations;

import android.net.Uri;
import android.os.SystemClock;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.BaseMediaChunkIterator;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.DataChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMediaPlaylist.Segment;
import com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylist;
import com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.trackselection.BaseTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

class HlsChunkSource
{
  private final DataSource encryptionDataSource;
  private byte[] encryptionIv;
  private String encryptionIvString;
  private byte[] encryptionKey;
  private Uri encryptionKeyUri;
  private HlsMasterPlaylist.HlsUrl expectedPlaylistUrl;
  private final HlsExtractorFactory extractorFactory;
  private IOException fatalError;
  private boolean independentSegments;
  private boolean isTimestampMaster;
  private long liveEdgeInPeriodTimeUs;
  private final DataSource mediaDataSource;
  private final List<Format> muxedCaptionFormats;
  private final HlsPlaylistTracker playlistTracker;
  private byte[] scratchSpace;
  private boolean seenExpectedPlaylistError;
  private final TimestampAdjusterProvider timestampAdjusterProvider;
  private final TrackGroup trackGroup;
  private TrackSelection trackSelection;
  private final HlsMasterPlaylist.HlsUrl[] variants;
  
  public HlsChunkSource(HlsExtractorFactory paramHlsExtractorFactory, HlsPlaylistTracker paramHlsPlaylistTracker, HlsMasterPlaylist.HlsUrl[] paramArrayOfHlsUrl, HlsDataSourceFactory paramHlsDataSourceFactory, TransferListener paramTransferListener, TimestampAdjusterProvider paramTimestampAdjusterProvider, List paramList)
  {
    extractorFactory = paramHlsExtractorFactory;
    playlistTracker = paramHlsPlaylistTracker;
    variants = paramArrayOfHlsUrl;
    timestampAdjusterProvider = paramTimestampAdjusterProvider;
    muxedCaptionFormats = paramList;
    liveEdgeInPeriodTimeUs = -9223372036854775807L;
    paramHlsExtractorFactory = new Format[paramArrayOfHlsUrl.length];
    paramHlsPlaylistTracker = new int[paramArrayOfHlsUrl.length];
    int i = 0;
    while (i < paramArrayOfHlsUrl.length)
    {
      paramHlsExtractorFactory[i] = format;
      paramHlsPlaylistTracker[i] = i;
      i += 1;
    }
    mediaDataSource = paramHlsDataSourceFactory.createDataSource(1);
    if (paramTransferListener != null) {
      mediaDataSource.addTransferListener(paramTransferListener);
    }
    encryptionDataSource = paramHlsDataSourceFactory.createDataSource(3);
    trackGroup = new TrackGroup(paramHlsExtractorFactory);
    trackSelection = new InitializationTrackSelection(trackGroup, paramHlsPlaylistTracker);
  }
  
  private void clearEncryptionData()
  {
    encryptionKeyUri = null;
    encryptionKey = null;
    encryptionIvString = null;
    encryptionIv = null;
  }
  
  private long getChunkMediaSequence(HlsMediaChunk paramHlsMediaChunk, boolean paramBoolean, HlsMediaPlaylist paramHlsMediaPlaylist, long paramLong1, long paramLong2)
  {
    if ((paramHlsMediaChunk != null) && (!paramBoolean)) {
      return paramHlsMediaChunk.getNextChunkIndex();
    }
    long l2 = durationUs;
    long l1 = paramLong2;
    if (paramHlsMediaChunk != null) {
      if (independentSegments) {
        l1 = paramLong2;
      } else {
        l1 = startTimeUs;
      }
    }
    if ((!hasEndTag) && (l1 >= l2 + paramLong1)) {
      return mediaSequence + segments.size();
    }
    List localList = segments;
    if ((playlistTracker.isLive()) && (paramHlsMediaChunk != null)) {
      paramBoolean = false;
    } else {
      paramBoolean = true;
    }
    return Util.binarySearchFloor(localList, Long.valueOf(l1 - paramLong1), true, paramBoolean) + mediaSequence;
  }
  
  private EncryptionKeyChunk newEncryptionKeyChunk(Uri paramUri, String paramString, int paramInt1, int paramInt2, Object paramObject)
  {
    paramUri = new DataSpec(paramUri, 0L, -1L, null, 1);
    return new EncryptionKeyChunk(encryptionDataSource, paramUri, variants[paramInt1].format, paramInt2, paramObject, scratchSpace, paramString);
  }
  
  private long resolveTimeToLiveEdgeUs(long paramLong)
  {
    int i;
    if (liveEdgeInPeriodTimeUs != -9223372036854775807L) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0) {
      return liveEdgeInPeriodTimeUs - paramLong;
    }
    return -9223372036854775807L;
  }
  
  private void setEncryptionData(Uri paramUri, String paramString, byte[] paramArrayOfByte)
  {
    if (Util.toLowerInvariant(paramString).startsWith("0x")) {
      localObject = paramString.substring(2);
    } else {
      localObject = paramString;
    }
    Object localObject = new BigInteger((String)localObject, 16).toByteArray();
    byte[] arrayOfByte = new byte[16];
    int i;
    if (localObject.length > 16) {
      i = localObject.length - 16;
    } else {
      i = 0;
    }
    System.arraycopy(localObject, i, arrayOfByte, arrayOfByte.length - localObject.length + i, localObject.length - i);
    encryptionKeyUri = paramUri;
    encryptionKey = paramArrayOfByte;
    encryptionIvString = paramString;
    encryptionIv = arrayOfByte;
  }
  
  private void updateLiveEdgeTimeUs(HlsMediaPlaylist paramHlsMediaPlaylist)
  {
    long l;
    if (hasEndTag) {
      l = -9223372036854775807L;
    } else {
      l = paramHlsMediaPlaylist.getEndTimeUs() - playlistTracker.getInitialStartTimeUs();
    }
    liveEdgeInPeriodTimeUs = l;
  }
  
  public MediaChunkIterator[] createMediaChunkIterators(HlsMediaChunk paramHlsMediaChunk, long paramLong)
  {
    int i;
    if (paramHlsMediaChunk == null) {
      i = -1;
    } else {
      i = trackGroup.indexOf(trackFormat);
    }
    MediaChunkIterator[] arrayOfMediaChunkIterator = new MediaChunkIterator[trackSelection.length()];
    int j = 0;
    while (j < arrayOfMediaChunkIterator.length)
    {
      int k = trackSelection.getIndexInTrackGroup(j);
      Object localObject = variants[k];
      if (!playlistTracker.isSnapshotValid((HlsMasterPlaylist.HlsUrl)localObject))
      {
        arrayOfMediaChunkIterator[j] = MediaChunkIterator.EMPTY;
      }
      else
      {
        localObject = playlistTracker.getPlaylistSnapshot((HlsMasterPlaylist.HlsUrl)localObject, false);
        long l1 = startTimeUs - playlistTracker.getInitialStartTimeUs();
        boolean bool;
        if (k != i) {
          bool = true;
        } else {
          bool = false;
        }
        long l2 = getChunkMediaSequence(paramHlsMediaChunk, bool, (HlsMediaPlaylist)localObject, l1, paramLong);
        if (l2 < mediaSequence) {
          arrayOfMediaChunkIterator[j] = MediaChunkIterator.EMPTY;
        } else {
          arrayOfMediaChunkIterator[j] = new HlsMediaPlaylistSegmentIterator((HlsMediaPlaylist)localObject, l1, (int)(l2 - mediaSequence));
        }
      }
      j += 1;
    }
    return arrayOfMediaChunkIterator;
  }
  
  public void getNextChunk(long paramLong1, long paramLong2, List paramList, HlsChunkHolder paramHlsChunkHolder)
  {
    HlsMediaChunk localHlsMediaChunk;
    if (paramList.isEmpty()) {
      localHlsMediaChunk = null;
    } else {
      localHlsMediaChunk = (HlsMediaChunk)paramList.get(paramList.size() - 1);
    }
    if (localHlsMediaChunk == null) {
      i = -1;
    } else {
      i = trackGroup.indexOf(trackFormat);
    }
    long l4 = paramLong2 - paramLong1;
    long l3 = resolveTimeToLiveEdgeUs(paramLong1);
    long l1 = l4;
    long l2 = l3;
    if (localHlsMediaChunk != null)
    {
      l1 = l4;
      l2 = l3;
      if (!independentSegments)
      {
        long l5 = localHlsMediaChunk.getDurationUs();
        l4 = Math.max(0L, l4 - l5);
        l1 = l4;
        l2 = l3;
        if (l3 != -9223372036854775807L)
        {
          l2 = Math.max(0L, l3 - l5);
          l1 = l4;
        }
      }
    }
    Object localObject1 = createMediaChunkIterators(localHlsMediaChunk, paramLong2);
    trackSelection.updateSelectedTrack(paramLong1, l1, l2, paramList, (MediaChunkIterator[])localObject1);
    int k = trackSelection.getSelectedIndexInTrackGroup();
    int j = 0;
    int m = 0;
    int n;
    if (i != k) {
      n = 1;
    } else {
      n = 0;
    }
    paramList = variants[k];
    if (!playlistTracker.isSnapshotValid(paramList))
    {
      playlist = paramList;
      n = seenExpectedPlaylistError;
      i = m;
      if (expectedPlaylistUrl == paramList) {
        i = 1;
      }
      seenExpectedPlaylistError = (n & i);
      expectedPlaylistUrl = paramList;
      return;
    }
    Object localObject2 = playlistTracker.getPlaylistSnapshot(paramList, true);
    localObject1 = localObject2;
    independentSegments = hasIndependentSegments;
    updateLiveEdgeTimeUs((HlsMediaPlaylist)localObject2);
    l1 = startTimeUs - playlistTracker.getInitialStartTimeUs();
    paramLong1 = getChunkMediaSequence(localHlsMediaChunk, n, (HlsMediaPlaylist)localObject2, l1, paramLong2);
    if (paramLong1 < mediaSequence)
    {
      if ((localHlsMediaChunk != null) && (n != 0))
      {
        paramList = variants[i];
        localObject2 = playlistTracker.getPlaylistSnapshot(paramList, true);
        localObject1 = localObject2;
        paramLong2 = startTimeUs - playlistTracker.getInitialStartTimeUs();
        paramLong1 = localHlsMediaChunk.getNextChunkIndex();
      }
      else
      {
        fatalError = new BehindLiveWindowException();
      }
    }
    else
    {
      i = k;
      paramLong2 = l1;
    }
    k = (int)(paramLong1 - mediaSequence);
    if (k >= segments.size())
    {
      if (hasEndTag)
      {
        endOfStream = true;
        return;
      }
      playlist = paramList;
      int i1 = seenExpectedPlaylistError;
      i = j;
      if (expectedPlaylistUrl == paramList) {
        i = 1;
      }
      seenExpectedPlaylistError = (i1 & i);
      expectedPlaylistUrl = paramList;
      return;
    }
    seenExpectedPlaylistError = false;
    localObject2 = null;
    expectedPlaylistUrl = null;
    HlsMediaPlaylist.Segment localSegment = (HlsMediaPlaylist.Segment)segments.get(k);
    if (fullSegmentEncryptionKeyUri != null)
    {
      localObject3 = UriUtil.resolveToUri(baseUri, fullSegmentEncryptionKeyUri);
      if (!((Uri)localObject3).equals(encryptionKeyUri))
      {
        chunk = newEncryptionKeyChunk((Uri)localObject3, encryptionIV, i, trackSelection.getSelectionReason(), trackSelection.getSelectionData());
        return;
      }
      if (!Util.areEqual(encryptionIV, encryptionIvString)) {
        setEncryptionData((Uri)localObject3, encryptionIV, encryptionKey);
      }
    }
    else
    {
      clearEncryptionData();
    }
    Object localObject3 = initializationSegment;
    if (localObject3 != null) {
      localObject2 = new DataSpec(UriUtil.resolveToUri(baseUri, encryptionKeyUri), byterangeOffset, byterangeLength, null);
    }
    paramLong2 = relativeStartTimeUs + paramLong2;
    int i = discontinuitySequence + relativeDiscontinuitySequence;
    localObject3 = timestampAdjusterProvider.getAdjuster(i);
    localObject1 = new DataSpec(UriUtil.resolveToUri(baseUri, encryptionKeyUri), byterangeOffset, byterangeLength, null);
    chunk = new HlsMediaChunk(extractorFactory, mediaDataSource, (DataSpec)localObject1, (DataSpec)localObject2, paramList, muxedCaptionFormats, trackSelection.getSelectionReason(), trackSelection.getSelectionData(), paramLong2, paramLong2 + durationUs, paramLong1, i, hasGapTag, isTimestampMaster, (TimestampAdjuster)localObject3, localHlsMediaChunk, drmInitData, encryptionKey, encryptionIv);
  }
  
  public TrackGroup getTrackGroup()
  {
    return trackGroup;
  }
  
  public TrackSelection getTrackSelection()
  {
    return trackSelection;
  }
  
  public boolean maybeBlacklistTrack(Chunk paramChunk, long paramLong)
  {
    return trackSelection.blacklist(trackSelection.indexOf(trackGroup.indexOf(trackFormat)), paramLong);
  }
  
  public void maybeThrowError()
    throws IOException
  {
    if (fatalError == null)
    {
      if ((expectedPlaylistUrl != null) && (seenExpectedPlaylistError)) {
        playlistTracker.maybeThrowPlaylistRefreshError(expectedPlaylistUrl);
      }
    }
    else {
      throw fatalError;
    }
  }
  
  public void onChunkLoadCompleted(Chunk paramChunk)
  {
    if ((paramChunk instanceof EncryptionKeyChunk))
    {
      paramChunk = (EncryptionKeyChunk)paramChunk;
      scratchSpace = paramChunk.getDataHolder();
      setEncryptionData(dataSpec.uri, iv, paramChunk.getResult());
    }
  }
  
  public boolean onPlaylistError(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong)
  {
    int i = trackGroup.indexOf(format);
    if (i == -1) {
      return true;
    }
    int j = trackSelection.indexOf(i);
    if (j == -1) {
      return true;
    }
    int k = seenExpectedPlaylistError;
    if (expectedPlaylistUrl == paramHlsUrl) {
      i = 1;
    } else {
      i = 0;
    }
    seenExpectedPlaylistError = (i | k);
    if (paramLong != -9223372036854775807L) {
      return trackSelection.blacklist(j, paramLong);
    }
    return true;
  }
  
  public void reset()
  {
    fatalError = null;
  }
  
  public void selectTracks(TrackSelection paramTrackSelection)
  {
    trackSelection = paramTrackSelection;
  }
  
  public void setIsTimestampMaster(boolean paramBoolean)
  {
    isTimestampMaster = paramBoolean;
  }
  
  final class EncryptionKeyChunk
    extends DataChunk
  {
    public final String iv;
    private byte[] result;
    
    public EncryptionKeyChunk(DataSpec paramDataSpec, Format paramFormat, int paramInt, Object paramObject, byte[] paramArrayOfByte, String paramString)
    {
      super(paramDataSpec, 3, paramFormat, paramInt, paramObject, paramArrayOfByte);
      iv = paramString;
    }
    
    protected void consume(byte[] paramArrayOfByte, int paramInt)
      throws IOException
    {
      result = Arrays.copyOf(paramArrayOfByte, paramInt);
    }
    
    public byte[] getResult()
    {
      return result;
    }
  }
  
  public final class HlsChunkHolder
  {
    public Chunk chunk;
    public boolean endOfStream;
    public HlsMasterPlaylist.HlsUrl playlist;
    
    public HlsChunkHolder()
    {
      clear();
    }
    
    public void clear()
    {
      chunk = null;
      endOfStream = false;
      playlist = null;
    }
  }
  
  final class HlsMediaPlaylistSegmentIterator
    extends BaseMediaChunkIterator
  {
    private final long startOfPlaylistInPeriodUs;
    
    public HlsMediaPlaylistSegmentIterator(long paramLong, int paramInt)
    {
      super(segments.size() - 1);
      startOfPlaylistInPeriodUs = paramLong;
    }
    
    public long getChunkEndTimeUs()
    {
      checkInBounds();
      HlsMediaPlaylist.Segment localSegment = (HlsMediaPlaylist.Segment)segments.get((int)getCurrentIndex());
      return startOfPlaylistInPeriodUs + relativeStartTimeUs + durationUs;
    }
    
    public long getChunkStartTimeUs()
    {
      checkInBounds();
      HlsMediaPlaylist.Segment localSegment = (HlsMediaPlaylist.Segment)segments.get((int)getCurrentIndex());
      return startOfPlaylistInPeriodUs + relativeStartTimeUs;
    }
    
    public DataSpec getDataSpec()
    {
      checkInBounds();
      HlsMediaPlaylist.Segment localSegment = (HlsMediaPlaylist.Segment)segments.get((int)getCurrentIndex());
      return new DataSpec(UriUtil.resolveToUri(baseUri, encryptionKeyUri), byterangeOffset, byterangeLength, null);
    }
  }
  
  final class InitializationTrackSelection
    extends BaseTrackSelection
  {
    private int selectedIndex = indexOf(this$1.getFormat(0));
    
    public InitializationTrackSelection(int[] paramArrayOfInt)
    {
      super(paramArrayOfInt);
    }
    
    public int getSelectedIndex()
    {
      return selectedIndex;
    }
    
    public Object getSelectionData()
    {
      return null;
    }
    
    public int getSelectionReason()
    {
      return 0;
    }
    
    public void updateSelectedTrack(long paramLong1, long paramLong2, long paramLong3, List paramList, MediaChunkIterator[] paramArrayOfMediaChunkIterator)
    {
      paramLong1 = SystemClock.elapsedRealtime();
      if (!isBlacklisted(selectedIndex, paramLong1)) {
        return;
      }
      int i = length - 1;
      while (i >= 0)
      {
        if (!isBlacklisted(i, paramLong1))
        {
          selectedIndex = i;
          return;
        }
        i -= 1;
      }
      throw new IllegalStateException();
    }
  }
}
