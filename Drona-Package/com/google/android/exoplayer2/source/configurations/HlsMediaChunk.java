package com.google.android.exoplayer2.source.configurations;

import android.util.Pair;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.configurations.Id3Decoder;
import com.google.android.exoplayer2.metadata.configurations.PrivFrame;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.upgrade.DrmInitData;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

final class HlsMediaChunk
  extends MediaChunk
{
  public static final String PRIV_TIMESTAMP_FRAME_OWNER = "com.apple.streaming.transportStreamTimestamp";
  private static final AtomicInteger uidSource = new AtomicInteger();
  public final int discontinuitySequenceNumber;
  private final DrmInitData drmInitData;
  private Extractor extractor;
  private final HlsExtractorFactory extractorFactory;
  private final boolean hasGapTag;
  public final HlsMasterPlaylist.HlsUrl hlsUrl;
  private final ParsableByteArray id3Data;
  private final Id3Decoder id3Decoder;
  private final DataSource initDataSource;
  private final DataSpec initDataSpec;
  private boolean initLoadCompleted;
  private int initSegmentBytesLoaded;
  private final boolean isEncrypted;
  private final boolean isMasterTimestampSource;
  private volatile boolean loadCanceled;
  private boolean loadCompleted;
  private final List<Format> muxedCaptionFormats;
  private int nextLoadPosition;
  private HlsSampleStreamWrapper output;
  private final Extractor previousExtractor;
  public final int sampleQueue;
  private final boolean shouldSpliceIn;
  private final TimestampAdjuster timestampAdjuster;
  
  public HlsMediaChunk(HlsExtractorFactory paramHlsExtractorFactory, DataSource paramDataSource, DataSpec paramDataSpec1, DataSpec paramDataSpec2, HlsMasterPlaylist.HlsUrl paramHlsUrl, List paramList, int paramInt1, Object paramObject, long paramLong1, long paramLong2, long paramLong3, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, TimestampAdjuster paramTimestampAdjuster, HlsMediaChunk paramHlsMediaChunk, DrmInitData paramDrmInitData, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    super(buildDataSource(paramDataSource, paramArrayOfByte1, paramArrayOfByte2), paramDataSpec1, format, paramInt1, paramObject, paramLong1, paramLong2, paramLong3);
    discontinuitySequenceNumber = paramInt2;
    initDataSpec = paramDataSpec2;
    hlsUrl = paramHlsUrl;
    isMasterTimestampSource = paramBoolean2;
    timestampAdjuster = paramTimestampAdjuster;
    boolean bool = true;
    if (paramArrayOfByte1 != null) {
      paramBoolean2 = true;
    } else {
      paramBoolean2 = false;
    }
    isEncrypted = paramBoolean2;
    hasGapTag = paramBoolean1;
    extractorFactory = paramHlsExtractorFactory;
    muxedCaptionFormats = paramList;
    drmInitData = paramDrmInitData;
    paramDataSpec1 = null;
    if (paramHlsMediaChunk != null)
    {
      id3Decoder = id3Decoder;
      id3Data = id3Data;
      paramBoolean1 = bool;
      if (hlsUrl == paramHlsUrl) {
        if (!loadCompleted) {
          paramBoolean1 = bool;
        } else {
          paramBoolean1 = false;
        }
      }
      shouldSpliceIn = paramBoolean1;
      paramHlsExtractorFactory = paramDataSpec1;
      if (discontinuitySequenceNumber == paramInt2) {
        if (shouldSpliceIn) {
          paramHlsExtractorFactory = paramDataSpec1;
        } else {
          paramHlsExtractorFactory = extractor;
        }
      }
    }
    else
    {
      id3Decoder = new Id3Decoder();
      id3Data = new ParsableByteArray(10);
      shouldSpliceIn = false;
      paramHlsExtractorFactory = paramDataSpec1;
    }
    previousExtractor = paramHlsExtractorFactory;
    initDataSource = paramDataSource;
    sampleQueue = uidSource.getAndIncrement();
  }
  
  private static DataSource buildDataSource(DataSource paramDataSource, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if (paramArrayOfByte1 != null) {
      return new Aes128DataSource(paramDataSource, paramArrayOfByte1, paramArrayOfByte2);
    }
    return paramDataSource;
  }
  
  private void loadMedia()
    throws IOException, InterruptedException
  {
    boolean bool = isEncrypted;
    int k = 0;
    Object localObject;
    if (bool)
    {
      DataSpec localDataSpec = dataSpec;
      localObject = localDataSpec;
      if (nextLoadPosition != 0)
      {
        i = 1;
        localObject = localDataSpec;
        break label55;
      }
    }
    else
    {
      localObject = dataSpec.subrange(nextLoadPosition);
    }
    int i = 0;
    label55:
    if (!isMasterTimestampSource) {
      timestampAdjuster.waitUntilInitialized();
    } else if (timestampAdjuster.getFirstSampleTimestampUs() == Long.MAX_VALUE) {
      timestampAdjuster.setFirstSampleTimestampUs(startTimeUs);
    }
    try
    {
      localObject = prepareExtraction(dataSource, (DataSpec)localObject);
      int j = k;
      if (i != 0)
      {
        ((ExtractorInput)localObject).skipFully(nextLoadPosition);
        j = k;
      }
      while (j == 0) {
        try
        {
          bool = loadCanceled;
          if (!bool) {
            j = extractor.read((ExtractorInput)localObject, null);
          }
        }
        catch (Throwable localThrowable2)
        {
          l1 = ((ExtractorInput)localObject).getPosition();
          l2 = dataSpec.absoluteStreamPosition;
          nextLoadPosition = ((int)(l1 - l2));
          throw localThrowable2;
        }
      }
      long l1 = ((ExtractorInput)localObject).getPosition();
      long l2 = dataSpec.absoluteStreamPosition;
      nextLoadPosition = ((int)(l1 - l2));
      Util.closeQuietly(dataSource);
      return;
    }
    catch (Throwable localThrowable1)
    {
      Util.closeQuietly(dataSource);
      throw localThrowable1;
    }
  }
  
  private void maybeLoadInitData()
    throws IOException, InterruptedException
  {
    if (!initLoadCompleted)
    {
      if (initDataSpec == null) {
        return;
      }
      Object localObject = initDataSpec.subrange(initSegmentBytesLoaded);
      try
      {
        localObject = prepareExtraction(initDataSource, (DataSpec)localObject);
        int i = 0;
        while (i == 0) {
          try
          {
            boolean bool = loadCanceled;
            if (!bool) {
              i = extractor.read((ExtractorInput)localObject, null);
            }
          }
          catch (Throwable localThrowable2)
          {
            l1 = ((DefaultExtractorInput)localObject).getPosition();
            l2 = initDataSpec.absoluteStreamPosition;
            initSegmentBytesLoaded = ((int)(l1 - l2));
            throw localThrowable2;
          }
        }
        long l1 = ((DefaultExtractorInput)localObject).getPosition();
        long l2 = initDataSpec.absoluteStreamPosition;
        initSegmentBytesLoaded = ((int)(l1 - l2));
        Util.closeQuietly(initDataSource);
        initLoadCompleted = true;
        return;
      }
      catch (Throwable localThrowable1)
      {
        Util.closeQuietly(initDataSource);
        throw localThrowable1;
      }
    }
  }
  
  private long peekId3PrivTimestamp(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    paramExtractorInput.resetPeekPosition();
    Object localObject = id3Data.data;
    try
    {
      paramExtractorInput.peekFully((byte[])localObject, 0, 10);
      id3Data.reset(10);
      if (id3Data.readUnsignedInt24() != Id3Decoder.ID3_TAG) {
        return -9223372036854775807L;
      }
      id3Data.skipBytes(3);
      int i = id3Data.readSynchSafeInt();
      int j = i + 10;
      if (j > id3Data.capacity())
      {
        localObject = id3Data.data;
        id3Data.reset(j);
        System.arraycopy(localObject, 0, id3Data.data, 0, 10);
      }
      paramExtractorInput.peekFully(id3Data.data, 10, i);
      paramExtractorInput = id3Decoder.decode(id3Data.data, i);
      if (paramExtractorInput == null) {
        return -9223372036854775807L;
      }
      j = paramExtractorInput.length();
      i = 0;
      while (i < j)
      {
        localObject = paramExtractorInput.getFormat(i);
        if ((localObject instanceof PrivFrame))
        {
          localObject = (PrivFrame)localObject;
          if ("com.apple.streaming.transportStreamTimestamp".equals(owner))
          {
            System.arraycopy(privateData, 0, id3Data.data, 0, 8);
            id3Data.reset(8);
            return id3Data.readLong() & 0x1FFFFFFFF;
          }
        }
        i += 1;
      }
      return -9223372036854775807L;
    }
    catch (EOFException paramExtractorInput) {}
    return -9223372036854775807L;
  }
  
  private DefaultExtractorInput prepareExtraction(DataSource paramDataSource, DataSpec paramDataSpec)
    throws IOException, InterruptedException
  {
    long l = paramDataSource.open(paramDataSpec);
    DefaultExtractorInput localDefaultExtractorInput = new DefaultExtractorInput(paramDataSource, absoluteStreamPosition, l);
    if (extractor == null)
    {
      l = peekId3PrivTimestamp(localDefaultExtractorInput);
      localDefaultExtractorInput.resetPeekPosition();
      paramDataSource = extractorFactory.createExtractor(previousExtractor, uri, trackFormat, muxedCaptionFormats, drmInitData, timestampAdjuster, paramDataSource.getResponseHeaders(), localDefaultExtractorInput);
      extractor = ((Extractor)first);
      paramDataSpec = extractor;
      Extractor localExtractor = previousExtractor;
      boolean bool3 = false;
      boolean bool1;
      if (paramDataSpec == localExtractor) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      if (((Boolean)second).booleanValue())
      {
        paramDataSource = output;
        if (l != -9223372036854775807L) {
          l = timestampAdjuster.adjustTsTimestamp(l);
        } else {
          l = startTimeUs;
        }
        paramDataSource.setSampleOffsetUs(l);
      }
      boolean bool2 = bool3;
      if (bool1)
      {
        bool2 = bool3;
        if (initDataSpec != null) {
          bool2 = true;
        }
      }
      initLoadCompleted = bool2;
      output.init(sampleQueue, shouldSpliceIn, bool1);
      if (!bool1) {
        extractor.init(output);
      }
    }
    return localDefaultExtractorInput;
  }
  
  public void cancelLoad()
  {
    loadCanceled = true;
  }
  
  public void init(HlsSampleStreamWrapper paramHlsSampleStreamWrapper)
  {
    output = paramHlsSampleStreamWrapper;
  }
  
  public boolean isLoadCompleted()
  {
    return loadCompleted;
  }
  
  public void load()
    throws IOException, InterruptedException
  {
    maybeLoadInitData();
    if (!loadCanceled)
    {
      if (!hasGapTag) {
        loadMedia();
      }
      loadCompleted = true;
    }
  }
}
