package com.google.android.exoplayer2.source.dash;

import android.net.Uri;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.chunk.BaseMediaChunkIterator;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.ChunkExtractorWrapper;
import com.google.android.exoplayer2.source.chunk.ChunkHolder;
import com.google.android.exoplayer2.source.chunk.ContainerMediaChunk;
import com.google.android.exoplayer2.source.chunk.InitializationChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.source.chunk.SingleSampleMediaChunk;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.RangedUri;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultDashChunkSource
  implements DashChunkSource
{
  private final int[] adaptationSetIndices;
  private final DataSource dataSource;
  private final long elapsedRealtimeOffsetMs;
  private IOException fatalError;
  private long liveEdgeTimeUs;
  private DashManifest manifest;
  private final LoaderErrorThrower manifestLoaderErrorThrower;
  private final int maxSegmentsPerLoad;
  private boolean missingLastSegment;
  private int periodIndex;
  @Nullable
  private final PlayerEmsgHandler.PlayerTrackEmsgHandler playerTrackEmsgHandler;
  protected final RepresentationHolder[] representationHolders;
  private final TrackSelection trackSelection;
  private final int trackType;
  
  public DefaultDashChunkSource(LoaderErrorThrower paramLoaderErrorThrower, DashManifest paramDashManifest, int paramInt1, int[] paramArrayOfInt, TrackSelection paramTrackSelection, int paramInt2, DataSource paramDataSource, long paramLong, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, PlayerEmsgHandler.PlayerTrackEmsgHandler paramPlayerTrackEmsgHandler)
  {
    manifestLoaderErrorThrower = paramLoaderErrorThrower;
    manifest = paramDashManifest;
    adaptationSetIndices = paramArrayOfInt;
    trackSelection = paramTrackSelection;
    trackType = paramInt2;
    dataSource = paramDataSource;
    periodIndex = paramInt1;
    elapsedRealtimeOffsetMs = paramLong;
    maxSegmentsPerLoad = paramInt3;
    playerTrackEmsgHandler = paramPlayerTrackEmsgHandler;
    paramLong = paramDashManifest.getPeriodDurationUs(paramInt1);
    liveEdgeTimeUs = -9223372036854775807L;
    paramLoaderErrorThrower = getRepresentations();
    representationHolders = new RepresentationHolder[paramTrackSelection.length()];
    paramInt1 = 0;
    while (paramInt1 < representationHolders.length)
    {
      paramDashManifest = (Representation)paramLoaderErrorThrower.get(paramTrackSelection.getIndexInTrackGroup(paramInt1));
      representationHolders[paramInt1] = new RepresentationHolder(paramLong, paramInt2, paramDashManifest, paramBoolean1, paramBoolean2, paramPlayerTrackEmsgHandler);
      paramInt1 += 1;
    }
  }
  
  private long getNowUnixTimeUs()
  {
    if (elapsedRealtimeOffsetMs != 0L) {
      return (SystemClock.elapsedRealtime() + elapsedRealtimeOffsetMs) * 1000L;
    }
    return System.currentTimeMillis() * 1000L;
  }
  
  private ArrayList getRepresentations()
  {
    List localList = manifest.getPeriod(periodIndex).adaptationSets;
    ArrayList localArrayList = new ArrayList();
    int[] arrayOfInt = adaptationSetIndices;
    int j = arrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      localArrayList.addAll(getrepresentations);
      i += 1;
    }
    return localArrayList;
  }
  
  private long getSegmentNum(RepresentationHolder paramRepresentationHolder, MediaChunk paramMediaChunk, long paramLong1, long paramLong2, long paramLong3)
  {
    if (paramMediaChunk != null) {
      return paramMediaChunk.getNextChunkIndex();
    }
    return Util.constrainValue(paramRepresentationHolder.getSegmentNum(paramLong1), paramLong2, paramLong3);
  }
  
  private long resolveTimeToLiveEdgeUs(long paramLong)
  {
    int i;
    if ((manifest.dynamic) && (liveEdgeTimeUs != -9223372036854775807L)) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0) {
      return liveEdgeTimeUs - paramLong;
    }
    return -9223372036854775807L;
  }
  
  private void updateLiveEdgeTimeUs(RepresentationHolder paramRepresentationHolder, long paramLong)
  {
    if (manifest.dynamic) {
      paramLong = paramRepresentationHolder.getSegmentEndTimeUs(paramLong);
    } else {
      paramLong = -9223372036854775807L;
    }
    liveEdgeTimeUs = paramLong;
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
    RepresentationHolder[] arrayOfRepresentationHolder = representationHolders;
    int j = arrayOfRepresentationHolder.length;
    int i = 0;
    while (i < j)
    {
      RepresentationHolder localRepresentationHolder = arrayOfRepresentationHolder[i];
      if (segmentIndex != null)
      {
        long l1 = localRepresentationHolder.getSegmentNum(paramLong);
        long l2 = localRepresentationHolder.getSegmentStartTimeUs(l1);
        if ((l2 < paramLong) && (l1 < localRepresentationHolder.getSegmentCount() - 1)) {
          l1 = localRepresentationHolder.getSegmentStartTimeUs(l1 + 1L);
        } else {
          l1 = l2;
        }
        return Util.resolveSeekPositionUs(paramLong, paramSeekParameters, l2, l1);
      }
      i += 1;
    }
    return paramLong;
  }
  
  public void getNextChunk(long paramLong1, long paramLong2, List paramList, ChunkHolder paramChunkHolder)
  {
    if (fatalError != null) {
      return;
    }
    long l1 = resolveTimeToLiveEdgeUs(paramLong1);
    long l2 = IpAddress.msToUs(manifest.availabilityStartTimeMs);
    long l3 = IpAddress.msToUs(manifest.getPeriod(periodIndex).startMs);
    if ((playerTrackEmsgHandler != null) && (playerTrackEmsgHandler.maybeRefreshManifestBeforeLoadingNextChunk(l2 + l3 + paramLong2))) {
      return;
    }
    l2 = getNowUnixTimeUs();
    boolean bool3 = paramList.isEmpty();
    RangedUri localRangedUri = null;
    MediaChunk localMediaChunk;
    if (bool3) {
      localMediaChunk = null;
    } else {
      localMediaChunk = (MediaChunk)paramList.get(paramList.size() - 1);
    }
    Object localObject = new MediaChunkIterator[trackSelection.length()];
    int i = 0;
    while (i < localObject.length)
    {
      localRepresentationHolder = representationHolders[i];
      if (segmentIndex == null)
      {
        localObject[i] = MediaChunkIterator.EMPTY;
      }
      else
      {
        l3 = localRepresentationHolder.getFirstAvailableSegmentNum(manifest, periodIndex, l2);
        long l4 = localRepresentationHolder.getLastAvailableSegmentNum(manifest, periodIndex, l2);
        long l5 = getSegmentNum(localRepresentationHolder, localMediaChunk, paramLong2, l3, l4);
        if (l5 < l3) {
          localObject[i] = MediaChunkIterator.EMPTY;
        } else {
          localObject[i] = new RepresentationSegmentIterator(localRepresentationHolder, l5, l4);
        }
      }
      i += 1;
    }
    trackSelection.updateSelectedTrack(paramLong1, paramLong2 - paramLong1, l1, paramList, (MediaChunkIterator[])localObject);
    RepresentationHolder localRepresentationHolder = representationHolders[trackSelection.getSelectedIndex()];
    if (extractorWrapper != null)
    {
      Representation localRepresentation = representation;
      if (extractorWrapper.getSampleFormats() == null) {
        localObject = localRepresentation.getInitializationUri();
      } else {
        localObject = null;
      }
      if (segmentIndex == null) {
        localRangedUri = localRepresentation.getIndexUri();
      }
      if ((localObject != null) || (localRangedUri != null))
      {
        chunk = newInitializationChunk(localRepresentationHolder, dataSource, trackSelection.getSelectedFormat(), trackSelection.getSelectionReason(), trackSelection.getSelectionData(), (RangedUri)localObject, localRangedUri);
        return;
      }
    }
    l1 = periodDurationUs;
    boolean bool2 = l1 < -9223372036854775807L;
    if (bool2) {
      bool3 = true;
    } else {
      bool3 = false;
    }
    if (localRepresentationHolder.getSegmentCount() == 0)
    {
      endOfStream = bool3;
      return;
    }
    l3 = localRepresentationHolder.getFirstAvailableSegmentNum(manifest, periodIndex, l2);
    l2 = localRepresentationHolder.getLastAvailableSegmentNum(manifest, periodIndex, l2);
    updateLiveEdgeTimeUs(localRepresentationHolder, l2);
    paramLong1 = getSegmentNum(localRepresentationHolder, localMediaChunk, paramLong2, l3, l2);
    if (paramLong1 < l3)
    {
      fatalError = new BehindLiveWindowException();
      return;
    }
    boolean bool1 = paramLong1 < l2;
    if ((!bool1) && ((!missingLastSegment) || (bool1)))
    {
      if ((bool3) && (localRepresentationHolder.getSegmentStartTimeUs(paramLong1) >= l1))
      {
        endOfStream = true;
        return;
      }
      int j = (int)Math.min(maxSegmentsPerLoad, l2 - paramLong1 + 1L);
      int k = j;
      if (bool2) {
        for (;;)
        {
          k = j;
          if (j <= 1) {
            break;
          }
          k = j;
          if (localRepresentationHolder.getSegmentStartTimeUs(j + paramLong1 - 1L) < l1) {
            break;
          }
          j -= 1;
        }
      }
      if (!paramList.isEmpty()) {
        paramLong2 = -9223372036854775807L;
      }
      chunk = newMediaChunk(localRepresentationHolder, dataSource, trackType, trackSelection.getSelectedFormat(), trackSelection.getSelectionReason(), trackSelection.getSelectionData(), paramLong1, k, paramLong2);
      return;
    }
    endOfStream = bool3;
  }
  
  public int getPreferredQueueSize(long paramLong, List paramList)
  {
    if ((fatalError == null) && (trackSelection.length() >= 2)) {
      return trackSelection.evaluateQueueSize(paramLong, paramList);
    }
    return paramList.size();
  }
  
  public void maybeThrowError()
    throws IOException
  {
    if (fatalError == null)
    {
      manifestLoaderErrorThrower.maybeThrowError();
      return;
    }
    throw fatalError;
  }
  
  protected Chunk newInitializationChunk(RepresentationHolder paramRepresentationHolder, DataSource paramDataSource, Format paramFormat, int paramInt, Object paramObject, RangedUri paramRangedUri1, RangedUri paramRangedUri2)
  {
    String str = representation.baseUrl;
    RangedUri localRangedUri = paramRangedUri2;
    if (paramRangedUri1 != null)
    {
      paramRangedUri2 = paramRangedUri1.attemptMerge(paramRangedUri2, str);
      localRangedUri = paramRangedUri2;
      if (paramRangedUri2 == null) {
        localRangedUri = paramRangedUri1;
      }
    }
    return new InitializationChunk(paramDataSource, new DataSpec(localRangedUri.resolveUri(str), start, length, representation.getCacheKey()), paramFormat, paramInt, paramObject, extractorWrapper);
  }
  
  protected Chunk newMediaChunk(RepresentationHolder paramRepresentationHolder, DataSource paramDataSource, int paramInt1, Format paramFormat, int paramInt2, Object paramObject, long paramLong1, int paramInt3, long paramLong2)
  {
    Representation localRepresentation = representation;
    long l2 = paramRepresentationHolder.getSegmentStartTimeUs(paramLong1);
    Object localObject = paramRepresentationHolder.getSegmentUrl(paramLong1);
    String str = baseUrl;
    if (extractorWrapper == null)
    {
      paramLong2 = paramRepresentationHolder.getSegmentEndTimeUs(paramLong1);
      return new SingleSampleMediaChunk(paramDataSource, new DataSpec(((RangedUri)localObject).resolveUri(str), start, length, localRepresentation.getCacheKey()), paramFormat, paramInt2, paramObject, l2, paramLong2, paramLong1, paramInt1, paramFormat);
    }
    paramInt1 = 1;
    int i = 1;
    while (paramInt1 < paramInt3)
    {
      RangedUri localRangedUri = ((RangedUri)localObject).attemptMerge(paramRepresentationHolder.getSegmentUrl(paramInt1 + paramLong1), str);
      if (localRangedUri == null) {
        break;
      }
      i += 1;
      paramInt1 += 1;
      localObject = localRangedUri;
    }
    long l3 = paramRepresentationHolder.getSegmentEndTimeUs(i + paramLong1 - 1L);
    long l1 = periodDurationUs;
    if ((l1 == -9223372036854775807L) || (l1 > l3)) {
      l1 = -9223372036854775807L;
    }
    return new ContainerMediaChunk(paramDataSource, new DataSpec(((RangedUri)localObject).resolveUri(str), start, length, localRepresentation.getCacheKey()), paramFormat, paramInt2, paramObject, l2, l3, paramLong2, l1, paramLong1, i, -presentationTimeOffsetUs, extractorWrapper);
  }
  
  public void onChunkLoadCompleted(Chunk paramChunk)
  {
    if ((paramChunk instanceof InitializationChunk))
    {
      Object localObject = (InitializationChunk)paramChunk;
      int i = trackSelection.indexOf(trackFormat);
      localObject = representationHolders[i];
      if (segmentIndex == null)
      {
        SeekMap localSeekMap = extractorWrapper.getSeekMap();
        if (localSeekMap != null) {
          representationHolders[i] = ((RepresentationHolder)localObject).copyWithNewSegmentIndex(new DashWrappingSegmentIndex((ChunkIndex)localSeekMap, representation.presentationTimeOffsetUs));
        }
      }
    }
    if (playerTrackEmsgHandler != null) {
      playerTrackEmsgHandler.onChunkLoadCompleted(paramChunk);
    }
  }
  
  public boolean onChunkLoadError(Chunk paramChunk, boolean paramBoolean, Exception paramException, long paramLong)
  {
    if (!paramBoolean) {
      return false;
    }
    if ((playerTrackEmsgHandler != null) && (playerTrackEmsgHandler.maybeRefreshManifestOnLoadingError(paramChunk))) {
      return true;
    }
    if ((!manifest.dynamic) && ((paramChunk instanceof MediaChunk)) && ((paramException instanceof HttpDataSource.InvalidResponseCodeException)) && (responseCode == 404))
    {
      paramException = representationHolders[trackSelection.indexOf(trackFormat)];
      int i = paramException.getSegmentCount();
      if ((i != -1) && (i != 0))
      {
        long l1 = paramException.getFirstSegmentNum();
        long l2 = i;
        if (((MediaChunk)paramChunk).getNextChunkIndex() > l1 + l2 - 1L)
        {
          missingLastSegment = true;
          return true;
        }
      }
    }
    return (paramLong != -9223372036854775807L) && (trackSelection.blacklist(trackSelection.indexOf(trackFormat), paramLong));
  }
  
  public void updateManifest(DashManifest paramDashManifest, int paramInt)
  {
    manifest = paramDashManifest;
    periodIndex = paramInt;
    paramDashManifest = manifest;
    paramInt = periodIndex;
    try
    {
      long l = paramDashManifest.getPeriodDurationUs(paramInt);
      paramDashManifest = getRepresentations();
      paramInt = 0;
      while (paramInt < representationHolders.length)
      {
        Object localObject1 = trackSelection;
        localObject1 = paramDashManifest.get(((TrackSelection)localObject1).getIndexInTrackGroup(paramInt));
        Object localObject2 = (Representation)localObject1;
        localObject1 = representationHolders;
        RepresentationHolder localRepresentationHolder = representationHolders[paramInt];
        localObject2 = localRepresentationHolder.copyWithNewRepresentation(l, (Representation)localObject2);
        localObject1[paramInt] = localObject2;
        paramInt += 1;
      }
      return;
    }
    catch (BehindLiveWindowException paramDashManifest)
    {
      fatalError = paramDashManifest;
    }
  }
  
  public static final class Factory
    implements DashChunkSource.Factory
  {
    private final DataSource.Factory dataSourceFactory;
    private final int maxSegmentsPerLoad;
    
    public Factory(DataSource.Factory paramFactory)
    {
      this(paramFactory, 1);
    }
    
    public Factory(DataSource.Factory paramFactory, int paramInt)
    {
      dataSourceFactory = paramFactory;
      maxSegmentsPerLoad = paramInt;
    }
    
    public DashChunkSource createDashChunkSource(LoaderErrorThrower paramLoaderErrorThrower, DashManifest paramDashManifest, int paramInt1, int[] paramArrayOfInt, TrackSelection paramTrackSelection, int paramInt2, long paramLong, boolean paramBoolean1, boolean paramBoolean2, PlayerEmsgHandler.PlayerTrackEmsgHandler paramPlayerTrackEmsgHandler, TransferListener paramTransferListener)
    {
      DataSource localDataSource = dataSourceFactory.createDataSource();
      if (paramTransferListener != null) {
        localDataSource.addTransferListener(paramTransferListener);
      }
      return new DefaultDashChunkSource(paramLoaderErrorThrower, paramDashManifest, paramInt1, paramArrayOfInt, paramTrackSelection, paramInt2, localDataSource, paramLong, maxSegmentsPerLoad, paramBoolean1, paramBoolean2, paramPlayerTrackEmsgHandler);
    }
  }
  
  protected static final class RepresentationHolder
  {
    @Nullable
    final ChunkExtractorWrapper extractorWrapper;
    private final long periodDurationUs;
    public final Representation representation;
    @Nullable
    public final DashSegmentIndex segmentIndex;
    private final long segmentNumShift;
    
    RepresentationHolder(long paramLong, int paramInt, Representation paramRepresentation, boolean paramBoolean1, boolean paramBoolean2, TrackOutput paramTrackOutput)
    {
      this(paramLong, paramRepresentation, createExtractorWrapper(paramInt, paramRepresentation, paramBoolean1, paramBoolean2, paramTrackOutput), 0L, paramRepresentation.getIndex());
    }
    
    private RepresentationHolder(long paramLong1, Representation paramRepresentation, ChunkExtractorWrapper paramChunkExtractorWrapper, long paramLong2, DashSegmentIndex paramDashSegmentIndex)
    {
      periodDurationUs = paramLong1;
      representation = paramRepresentation;
      segmentNumShift = paramLong2;
      extractorWrapper = paramChunkExtractorWrapper;
      segmentIndex = paramDashSegmentIndex;
    }
    
    private static ChunkExtractorWrapper createExtractorWrapper(int paramInt, Representation paramRepresentation, boolean paramBoolean1, boolean paramBoolean2, TrackOutput paramTrackOutput)
    {
      throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a7 = a6\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
    }
    
    private static boolean mimeTypeIsRawText(String paramString)
    {
      return (MimeTypes.isText(paramString)) || ("application/ttml+xml".equals(paramString));
    }
    
    private static boolean mimeTypeIsWebm(String paramString)
    {
      return (paramString.startsWith("video/webm")) || (paramString.startsWith("audio/webm")) || (paramString.startsWith("application/webm"));
    }
    
    RepresentationHolder copyWithNewRepresentation(long paramLong, Representation paramRepresentation)
      throws BehindLiveWindowException
    {
      DashSegmentIndex localDashSegmentIndex1 = representation.getIndex();
      DashSegmentIndex localDashSegmentIndex2 = paramRepresentation.getIndex();
      if (localDashSegmentIndex1 == null) {
        return new RepresentationHolder(paramLong, paramRepresentation, extractorWrapper, segmentNumShift, localDashSegmentIndex1);
      }
      if (!localDashSegmentIndex1.isExplicit()) {
        return new RepresentationHolder(paramLong, paramRepresentation, extractorWrapper, segmentNumShift, localDashSegmentIndex2);
      }
      int i = localDashSegmentIndex1.getSegmentCount(paramLong);
      if (i == 0) {
        return new RepresentationHolder(paramLong, paramRepresentation, extractorWrapper, segmentNumShift, localDashSegmentIndex2);
      }
      long l1 = localDashSegmentIndex1.getFirstSegmentNum() + i - 1L;
      long l2 = localDashSegmentIndex1.getTimeUs(l1);
      long l3 = localDashSegmentIndex1.getDurationUs(l1, paramLong);
      long l4 = localDashSegmentIndex2.getFirstSegmentNum();
      long l5 = localDashSegmentIndex2.getTimeUs(l4);
      long l6 = segmentNumShift;
      boolean bool = l2 + l3 < l5;
      if (!bool)
      {
        l1 = l6 + (l1 + 1L - l4);
      }
      else
      {
        if (bool) {
          break label240;
        }
        l1 = l6 + (localDashSegmentIndex1.getSegmentNum(l5, paramLong) - l4);
      }
      return new RepresentationHolder(paramLong, paramRepresentation, extractorWrapper, l1, localDashSegmentIndex2);
      label240:
      throw new BehindLiveWindowException();
    }
    
    RepresentationHolder copyWithNewSegmentIndex(DashSegmentIndex paramDashSegmentIndex)
    {
      return new RepresentationHolder(periodDurationUs, representation, extractorWrapper, segmentNumShift, paramDashSegmentIndex);
    }
    
    public long getFirstAvailableSegmentNum(DashManifest paramDashManifest, int paramInt, long paramLong)
    {
      if ((getSegmentCount() == -1) && (timeShiftBufferDepthMs != -9223372036854775807L))
      {
        long l1 = IpAddress.msToUs(availabilityStartTimeMs);
        long l2 = IpAddress.msToUs(getPeriodstartMs);
        long l3 = IpAddress.msToUs(timeShiftBufferDepthMs);
        return Math.max(getFirstSegmentNum(), getSegmentNum(paramLong - l1 - l2 - l3));
      }
      return getFirstSegmentNum();
    }
    
    public long getFirstSegmentNum()
    {
      return segmentIndex.getFirstSegmentNum() + segmentNumShift;
    }
    
    public long getLastAvailableSegmentNum(DashManifest paramDashManifest, int paramInt, long paramLong)
    {
      int i = getSegmentCount();
      if (i == -1) {
        return getSegmentNum(paramLong - IpAddress.msToUs(availabilityStartTimeMs) - IpAddress.msToUs(getPeriodstartMs)) - 1L;
      }
      return getFirstSegmentNum() + i - 1L;
    }
    
    public int getSegmentCount()
    {
      return segmentIndex.getSegmentCount(periodDurationUs);
    }
    
    public long getSegmentEndTimeUs(long paramLong)
    {
      return getSegmentStartTimeUs(paramLong) + segmentIndex.getDurationUs(paramLong - segmentNumShift, periodDurationUs);
    }
    
    public long getSegmentNum(long paramLong)
    {
      return segmentIndex.getSegmentNum(paramLong, periodDurationUs) + segmentNumShift;
    }
    
    public long getSegmentStartTimeUs(long paramLong)
    {
      return segmentIndex.getTimeUs(paramLong - segmentNumShift);
    }
    
    public RangedUri getSegmentUrl(long paramLong)
    {
      return segmentIndex.getSegmentUrl(paramLong - segmentNumShift);
    }
  }
  
  protected static final class RepresentationSegmentIterator
    extends BaseMediaChunkIterator
  {
    private final DefaultDashChunkSource.RepresentationHolder representationHolder;
    
    public RepresentationSegmentIterator(DefaultDashChunkSource.RepresentationHolder paramRepresentationHolder, long paramLong1, long paramLong2)
    {
      super(paramLong2);
      representationHolder = paramRepresentationHolder;
    }
    
    public long getChunkEndTimeUs()
    {
      checkInBounds();
      return representationHolder.getSegmentEndTimeUs(getCurrentIndex());
    }
    
    public long getChunkStartTimeUs()
    {
      checkInBounds();
      return representationHolder.getSegmentStartTimeUs(getCurrentIndex());
    }
    
    public DataSpec getDataSpec()
    {
      checkInBounds();
      Object localObject = representationHolder.representation;
      RangedUri localRangedUri = representationHolder.getSegmentUrl(getCurrentIndex());
      Uri localUri = localRangedUri.resolveUri(baseUrl);
      localObject = ((Representation)localObject).getCacheKey();
      return new DataSpec(localUri, start, length, (String)localObject);
    }
  }
}
