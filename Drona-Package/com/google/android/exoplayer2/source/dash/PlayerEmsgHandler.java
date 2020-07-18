package com.google.android.exoplayer2.source.dash;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.TrackOutput.CryptoData;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.metadata.emsg.EventMessage;
import com.google.android.exoplayer2.metadata.emsg.EventMessageDecoder;
import com.google.android.exoplayer2.source.SampleQueue;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public final class PlayerEmsgHandler
  implements Handler.Callback
{
  private static final int EMSG_MANIFEST_EXPIRED = 1;
  private final Allocator allocator;
  private final EventMessageDecoder decoder;
  private long expiredManifestPublishTimeUs;
  private final Handler handler;
  private boolean isWaitingForManifestRefresh;
  private long lastLoadedChunkEndTimeBeforeRefreshUs;
  private long lastLoadedChunkEndTimeUs;
  private DashManifest manifest;
  private final TreeMap<Long, Long> manifestPublishTimeToExpiryTimeUs;
  private final PlayerEmsgCallback playerEmsgCallback;
  private boolean released;
  
  public PlayerEmsgHandler(DashManifest paramDashManifest, PlayerEmsgCallback paramPlayerEmsgCallback, Allocator paramAllocator)
  {
    manifest = paramDashManifest;
    playerEmsgCallback = paramPlayerEmsgCallback;
    allocator = paramAllocator;
    manifestPublishTimeToExpiryTimeUs = new TreeMap();
    handler = Util.createHandler(this);
    decoder = new EventMessageDecoder();
    lastLoadedChunkEndTimeUs = -9223372036854775807L;
    lastLoadedChunkEndTimeBeforeRefreshUs = -9223372036854775807L;
  }
  
  private Map.Entry ceilingExpiryEntryForPublishTime(long paramLong)
  {
    return manifestPublishTimeToExpiryTimeUs.ceilingEntry(Long.valueOf(paramLong));
  }
  
  private static long getManifestPublishTimeMsInEmsg(EventMessage paramEventMessage)
  {
    paramEventMessage = messageData;
    try
    {
      long l = Util.parseXsDateTime(Util.fromUtf8Bytes(paramEventMessage));
      return l;
    }
    catch (ParserException paramEventMessage)
    {
      for (;;) {}
    }
    return -9223372036854775807L;
  }
  
  private void handleManifestExpiredMessage(long paramLong1, long paramLong2)
  {
    Long localLong = (Long)manifestPublishTimeToExpiryTimeUs.get(Long.valueOf(paramLong2));
    if (localLong == null)
    {
      manifestPublishTimeToExpiryTimeUs.put(Long.valueOf(paramLong2), Long.valueOf(paramLong1));
      return;
    }
    if (localLong.longValue() > paramLong1) {
      manifestPublishTimeToExpiryTimeUs.put(Long.valueOf(paramLong2), Long.valueOf(paramLong1));
    }
  }
  
  public static boolean isPlayerEmsgEvent(String paramString1, String paramString2)
  {
    return ("urn:mpeg:dash:event:2012".equals(paramString1)) && (("1".equals(paramString2)) || ("2".equals(paramString2)) || ("3".equals(paramString2)));
  }
  
  private void maybeNotifyDashManifestRefreshNeeded()
  {
    if ((lastLoadedChunkEndTimeBeforeRefreshUs != -9223372036854775807L) && (lastLoadedChunkEndTimeBeforeRefreshUs == lastLoadedChunkEndTimeUs)) {
      return;
    }
    isWaitingForManifestRefresh = true;
    lastLoadedChunkEndTimeBeforeRefreshUs = lastLoadedChunkEndTimeUs;
    playerEmsgCallback.onDashManifestRefreshRequested();
  }
  
  private void notifyManifestPublishTimeExpired()
  {
    playerEmsgCallback.onDashManifestPublishTimeExpired(expiredManifestPublishTimeUs);
  }
  
  private void removePreviouslyExpiredManifestPublishTimeValues()
  {
    Iterator localIterator = manifestPublishTimeToExpiryTimeUs.entrySet().iterator();
    while (localIterator.hasNext()) {
      if (((Long)((Map.Entry)localIterator.next()).getKey()).longValue() < manifest.publishTimeMs) {
        localIterator.remove();
      }
    }
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    if (released) {
      return true;
    }
    if (what != 1) {
      return false;
    }
    paramMessage = (ManifestExpiryEventInfo)obj;
    handleManifestExpiredMessage(eventTimeUs, manifestPublishTimeMsInEmsg);
    return true;
  }
  
  boolean maybeRefreshManifestBeforeLoadingNextChunk(long paramLong)
  {
    boolean bool1 = manifest.dynamic;
    boolean bool2 = false;
    if (!bool1) {
      return false;
    }
    if (isWaitingForManifestRefresh) {
      return true;
    }
    Map.Entry localEntry = ceilingExpiryEntryForPublishTime(manifest.publishTimeMs);
    bool1 = bool2;
    if (localEntry != null)
    {
      bool1 = bool2;
      if (((Long)localEntry.getValue()).longValue() < paramLong)
      {
        expiredManifestPublishTimeUs = ((Long)localEntry.getKey()).longValue();
        notifyManifestPublishTimeExpired();
        bool1 = true;
      }
    }
    if (bool1) {
      maybeNotifyDashManifestRefreshNeeded();
    }
    return bool1;
  }
  
  boolean maybeRefreshManifestOnLoadingError(Chunk paramChunk)
  {
    if (!manifest.dynamic) {
      return false;
    }
    if (isWaitingForManifestRefresh) {
      return true;
    }
    int i;
    if ((lastLoadedChunkEndTimeUs != -9223372036854775807L) && (lastLoadedChunkEndTimeUs < startTimeUs)) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0)
    {
      maybeNotifyDashManifestRefreshNeeded();
      return true;
    }
    return false;
  }
  
  public PlayerTrackEmsgHandler newPlayerTrackEmsgHandler()
  {
    return new PlayerTrackEmsgHandler(new SampleQueue(allocator));
  }
  
  void onChunkLoadCompleted(Chunk paramChunk)
  {
    if ((lastLoadedChunkEndTimeUs != -9223372036854775807L) || (endTimeUs > lastLoadedChunkEndTimeUs)) {
      lastLoadedChunkEndTimeUs = endTimeUs;
    }
  }
  
  public void release()
  {
    released = true;
    handler.removeCallbacksAndMessages(null);
  }
  
  public void updateManifest(DashManifest paramDashManifest)
  {
    isWaitingForManifestRefresh = false;
    expiredManifestPublishTimeUs = -9223372036854775807L;
    manifest = paramDashManifest;
    removePreviouslyExpiredManifestPublishTimeValues();
  }
  
  private static final class ManifestExpiryEventInfo
  {
    public final long eventTimeUs;
    public final long manifestPublishTimeMsInEmsg;
    
    public ManifestExpiryEventInfo(long paramLong1, long paramLong2)
    {
      eventTimeUs = paramLong1;
      manifestPublishTimeMsInEmsg = paramLong2;
    }
  }
  
  public static abstract interface PlayerEmsgCallback
  {
    public abstract void onDashManifestPublishTimeExpired(long paramLong);
    
    public abstract void onDashManifestRefreshRequested();
  }
  
  public final class PlayerTrackEmsgHandler
    implements TrackOutput
  {
    private final MetadataInputBuffer buffer;
    private final FormatHolder formatHolder;
    private final SampleQueue sampleQueue;
    
    PlayerTrackEmsgHandler(SampleQueue paramSampleQueue)
    {
      sampleQueue = paramSampleQueue;
      formatHolder = new FormatHolder();
      buffer = new MetadataInputBuffer();
    }
    
    private MetadataInputBuffer dequeueSample()
    {
      buffer.clear();
      if (sampleQueue.read(formatHolder, buffer, false, false, 0L) == -4)
      {
        buffer.flip();
        return buffer;
      }
      return null;
    }
    
    private void onManifestExpiredMessageEncountered(long paramLong1, long paramLong2)
    {
      PlayerEmsgHandler.ManifestExpiryEventInfo localManifestExpiryEventInfo = new PlayerEmsgHandler.ManifestExpiryEventInfo(paramLong1, paramLong2);
      handler.sendMessage(handler.obtainMessage(1, localManifestExpiryEventInfo));
    }
    
    private void parseAndDiscardSamples()
    {
      while (sampleQueue.hasNextSample())
      {
        Object localObject = dequeueSample();
        if (localObject != null)
        {
          long l = timeUs;
          localObject = (EventMessage)decoder.decode((MetadataInputBuffer)localObject).getFormat(0);
          if (PlayerEmsgHandler.isPlayerEmsgEvent(schemeIdUri, value)) {
            parsePlayerEmsgEvent(l, (EventMessage)localObject);
          }
        }
      }
      sampleQueue.discardToRead();
    }
    
    private void parsePlayerEmsgEvent(long paramLong, EventMessage paramEventMessage)
    {
      long l = PlayerEmsgHandler.getManifestPublishTimeMsInEmsg(paramEventMessage);
      if (l == -9223372036854775807L) {
        return;
      }
      onManifestExpiredMessageEncountered(paramLong, l);
    }
    
    public void format(Format paramFormat)
    {
      sampleQueue.format(paramFormat);
    }
    
    public boolean maybeRefreshManifestBeforeLoadingNextChunk(long paramLong)
    {
      return PlayerEmsgHandler.this.maybeRefreshManifestBeforeLoadingNextChunk(paramLong);
    }
    
    public boolean maybeRefreshManifestOnLoadingError(Chunk paramChunk)
    {
      return PlayerEmsgHandler.this.maybeRefreshManifestOnLoadingError(paramChunk);
    }
    
    public void onChunkLoadCompleted(Chunk paramChunk)
    {
      PlayerEmsgHandler.this.onChunkLoadCompleted(paramChunk);
    }
    
    public void release()
    {
      sampleQueue.reset();
    }
    
    public int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
      throws IOException, InterruptedException
    {
      return sampleQueue.sampleData(paramExtractorInput, paramInt, paramBoolean);
    }
    
    public void sampleData(ParsableByteArray paramParsableByteArray, int paramInt)
    {
      sampleQueue.sampleData(paramParsableByteArray, paramInt);
    }
    
    public void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, TrackOutput.CryptoData paramCryptoData)
    {
      sampleQueue.sampleMetadata(paramLong, paramInt1, paramInt2, paramInt3, paramCryptoData);
      parseAndDiscardSamples();
    }
  }
}
