package com.google.android.exoplayer2.extractor.configurations;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TsExtractor
  implements Extractor
{
  private static final long AC3_FORMAT_IDENTIFIER = Util.getIntegerCodeForString("AC-3");
  private static final int BUFFER_SIZE = 9400;
  private static final long E_AC3_FORMAT_IDENTIFIER = Util.getIntegerCodeForString("EAC3");
  public static final ExtractorsFactory FACTORY = -..Lambda.TsExtractor.f-UE6PC86cqq4V-qVoFQnPhfFZ8.INSTANCE;
  private static final long HEVC_FORMAT_IDENTIFIER = Util.getIntegerCodeForString("HEVC");
  private static final int MAX_PID_PLUS_ONE = 8192;
  public static final int MODE_HLS = 2;
  public static final int MODE_MULTI_PMT = 0;
  public static final int MODE_SINGLE_PMT = 1;
  private static final int SNIFF_TS_PACKET_COUNT = 5;
  public static final int TS_PACKET_SIZE = 188;
  private static final int TS_PAT_PID = 0;
  public static final int TS_STREAM_TYPE_AAC_ADTS = 15;
  public static final int TS_STREAM_TYPE_AAC_LATM = 17;
  public static final int TS_STREAM_TYPE_AC3 = 129;
  public static final int TS_STREAM_TYPE_DTS = 138;
  public static final int TS_STREAM_TYPE_DVBSUBS = 89;
  public static final int TS_STREAM_TYPE_E_AC3 = 135;
  public static final int TS_STREAM_TYPE_H262 = 2;
  public static final int TS_STREAM_TYPE_H264 = 27;
  public static final int TS_STREAM_TYPE_H265 = 36;
  public static final int TS_STREAM_TYPE_HDMV_DTS = 130;
  public static final int TS_STREAM_TYPE_ID3 = 21;
  public static final int TS_STREAM_TYPE_MPA = 3;
  public static final int TS_STREAM_TYPE_MPA_LSF = 4;
  public static final int TS_STREAM_TYPE_SPLICE_INFO = 134;
  public static final int TS_SYNC_BYTE = 71;
  private int bytesSinceLastSync;
  private final SparseIntArray continuityCounters;
  private final TsDurationReader durationReader;
  private boolean hasOutputSeekMap;
  private TsPayloadReader id3Reader;
  private final int mode;
  private ExtractorOutput output;
  private final TsPayloadReader.Factory payloadReaderFactory;
  private int pcrPid;
  private boolean pendingSeekToStart;
  private int remainingPmts;
  private final List<TimestampAdjuster> timestampAdjusters;
  private final SparseBooleanArray trackIds;
  private final SparseBooleanArray trackPids;
  private boolean tracksEnded;
  private TsBinarySearchSeeker tsBinarySearchSeeker;
  private final ParsableByteArray tsPacketBuffer;
  private final SparseArray<com.google.android.exoplayer2.extractor.ts.TsPayloadReader> tsPayloadReaders;
  
  public TsExtractor()
  {
    this(0);
  }
  
  public TsExtractor(int paramInt)
  {
    this(1, paramInt);
  }
  
  public TsExtractor(int paramInt1, int paramInt2)
  {
    this(paramInt1, new TimestampAdjuster(0L), new DefaultTsPayloadReaderFactory(paramInt2));
  }
  
  public TsExtractor(int paramInt, TimestampAdjuster paramTimestampAdjuster, TsPayloadReader.Factory paramFactory)
  {
    payloadReaderFactory = ((TsPayloadReader.Factory)Assertions.checkNotNull(paramFactory));
    mode = paramInt;
    if ((paramInt != 1) && (paramInt != 2))
    {
      timestampAdjusters = new ArrayList();
      timestampAdjusters.add(paramTimestampAdjuster);
    }
    else
    {
      timestampAdjusters = Collections.singletonList(paramTimestampAdjuster);
    }
    tsPacketBuffer = new ParsableByteArray(new byte['?'], 0);
    trackIds = new SparseBooleanArray();
    trackPids = new SparseBooleanArray();
    tsPayloadReaders = new SparseArray();
    continuityCounters = new SparseIntArray();
    durationReader = new TsDurationReader();
    pcrPid = -1;
    resetPayloadReaders();
  }
  
  private boolean fillBufferWithAtLeastOnePacket(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    byte[] arrayOfByte = tsPacketBuffer.data;
    int i;
    if (9400 - tsPacketBuffer.getPosition() < 188)
    {
      i = tsPacketBuffer.bytesLeft();
      if (i > 0) {
        System.arraycopy(arrayOfByte, tsPacketBuffer.getPosition(), arrayOfByte, 0, i);
      }
      tsPacketBuffer.reset(arrayOfByte, i);
    }
    while (tsPacketBuffer.bytesLeft() < 188)
    {
      i = tsPacketBuffer.limit();
      int j = paramExtractorInput.read(arrayOfByte, i, 9400 - i);
      if (j == -1) {
        return false;
      }
      tsPacketBuffer.setLimit(i + j);
    }
    return true;
  }
  
  private int findEndOfFirstTsPacketInBuffer()
    throws ParserException
  {
    int i = tsPacketBuffer.getPosition();
    int j = tsPacketBuffer.limit();
    int k = TsUtil.findSyncBytePosition(tsPacketBuffer.data, i, j);
    tsPacketBuffer.setPosition(k);
    int m = k + 188;
    if (m > j)
    {
      bytesSinceLastSync += k - i;
      if (mode == 2)
      {
        if (bytesSinceLastSync <= 376) {
          return m;
        }
        throw new ParserException("Cannot find sync byte. Most likely not a Transport Stream.");
      }
    }
    else
    {
      bytesSinceLastSync = 0;
    }
    return m;
  }
  
  private void maybeOutputSeekMap(long paramLong)
  {
    if (!hasOutputSeekMap)
    {
      hasOutputSeekMap = true;
      if (durationReader.getDurationUs() != -9223372036854775807L)
      {
        tsBinarySearchSeeker = new TsBinarySearchSeeker(durationReader.getPcrTimestampAdjuster(), durationReader.getDurationUs(), paramLong, pcrPid);
        output.seekMap(tsBinarySearchSeeker.getSeekMap());
        return;
      }
      output.seekMap(new SeekMap.Unseekable(durationReader.getDurationUs()));
    }
  }
  
  private void resetPayloadReaders()
  {
    trackIds.clear();
    tsPayloadReaders.clear();
    SparseArray localSparseArray = payloadReaderFactory.createInitialPayloadReaders();
    int j = localSparseArray.size();
    int i = 0;
    while (i < j)
    {
      tsPayloadReaders.put(localSparseArray.keyAt(i), localSparseArray.valueAt(i));
      i += 1;
    }
    tsPayloadReaders.put(0, new SectionReader(new PatReader()));
    id3Reader = null;
  }
  
  private boolean shouldConsumePacketPayload(int paramInt)
  {
    return (mode == 2) || (tracksEnded) || (!trackPids.get(paramInt, false));
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    output = paramExtractorOutput;
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    long l = paramExtractorInput.getLength();
    boolean bool1 = tracksEnded;
    Object localObject = null;
    int i;
    if (bool1)
    {
      if ((l != -1L) && (mode != 2)) {
        i = 1;
      } else {
        i = 0;
      }
      if ((i != 0) && (!durationReader.isDurationReadFinished())) {
        return durationReader.readDuration(paramExtractorInput, paramPositionHolder, pcrPid);
      }
      maybeOutputSeekMap(l);
      if (pendingSeekToStart)
      {
        pendingSeekToStart = false;
        seek(0L, 0L);
        if (paramExtractorInput.getPosition() != 0L)
        {
          position = 0L;
          return 1;
        }
      }
      if ((tsBinarySearchSeeker != null) && (tsBinarySearchSeeker.isSeeking())) {
        return tsBinarySearchSeeker.handlePendingSeek(paramExtractorInput, paramPositionHolder, null);
      }
    }
    if (!fillBufferWithAtLeastOnePacket(paramExtractorInput)) {
      return -1;
    }
    int k = findEndOfFirstTsPacketInBuffer();
    int m = tsPacketBuffer.limit();
    if (k > m) {
      return 0;
    }
    int i1 = tsPacketBuffer.readInt();
    if ((0x800000 & i1) != 0)
    {
      tsPacketBuffer.setPosition(k);
      return 0;
    }
    if ((0x400000 & i1) != 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    int n = (0x1FFF00 & i1) >> 8;
    if ((i1 & 0x20) != 0) {
      i = 1;
    } else {
      i = 0;
    }
    int j;
    if ((i1 & 0x10) != 0) {
      j = 1;
    } else {
      j = 0;
    }
    paramExtractorInput = localObject;
    if (j != 0) {
      paramExtractorInput = (TsPayloadReader)tsPayloadReaders.get(n);
    }
    if (paramExtractorInput == null)
    {
      tsPacketBuffer.setPosition(k);
      return 0;
    }
    if (mode != 2)
    {
      j = i1 & 0xF;
      i1 = continuityCounters.get(n, j - 1);
      continuityCounters.put(n, j);
      if (i1 == j)
      {
        tsPacketBuffer.setPosition(k);
        return 0;
      }
      if (j != (i1 + 1 & 0xF)) {
        paramExtractorInput.seek();
      }
    }
    if (i != 0)
    {
      i = tsPacketBuffer.readUnsignedByte();
      tsPacketBuffer.skipBytes(i);
    }
    boolean bool2 = tracksEnded;
    if (shouldConsumePacketPayload(n))
    {
      tsPacketBuffer.setLimit(k);
      paramExtractorInput.consume(tsPacketBuffer, bool1);
      tsPacketBuffer.setLimit(m);
    }
    if ((mode != 2) && (!bool2) && (tracksEnded) && (l != -1L)) {
      pendingSeekToStart = true;
    }
    tsPacketBuffer.setPosition(k);
    return 0;
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    boolean bool;
    if (mode != 2) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    int k = timestampAdjusters.size();
    int i = 0;
    while (i < k)
    {
      TimestampAdjuster localTimestampAdjuster = (TimestampAdjuster)timestampAdjusters.get(i);
      int j;
      if (localTimestampAdjuster.getTimestampOffsetUs() == -9223372036854775807L) {
        j = 1;
      } else {
        j = 0;
      }
      if ((j != 0) || ((localTimestampAdjuster.getTimestampOffsetUs() != 0L) && (localTimestampAdjuster.getFirstSampleTimestampUs() != paramLong2)))
      {
        localTimestampAdjuster.reset();
        localTimestampAdjuster.setFirstSampleTimestampUs(paramLong2);
      }
      i += 1;
    }
    if ((paramLong2 != 0L) && (tsBinarySearchSeeker != null)) {
      tsBinarySearchSeeker.setSeekTargetUs(paramLong2);
    }
    tsPacketBuffer.reset();
    continuityCounters.clear();
    i = 0;
    while (i < tsPayloadReaders.size())
    {
      ((TsPayloadReader)tsPayloadReaders.valueAt(i)).seek();
      i += 1;
    }
    bytesSinceLastSync = 0;
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    byte[] arrayOfByte = tsPacketBuffer.data;
    paramExtractorInput.peekFully(arrayOfByte, 0, 940);
    int i = 0;
    while (i < 188)
    {
      int j = 0;
      while (j < 5)
      {
        if (arrayOfByte[(j * 188 + i)] != 71)
        {
          j = 0;
          break label66;
        }
        j += 1;
      }
      j = 1;
      label66:
      if (j != 0)
      {
        paramExtractorInput.skipFully(i);
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface Mode {}
  
  class PatReader
    implements SectionPayloadReader
  {
    private final ParsableBitArray patScratch = new ParsableBitArray(new byte[4]);
    
    public PatReader() {}
    
    public void consume(ParsableByteArray paramParsableByteArray)
    {
      if (paramParsableByteArray.readUnsignedByte() != 0) {
        return;
      }
      paramParsableByteArray.skipBytes(7);
      int j = paramParsableByteArray.bytesLeft() / 4;
      int i = 0;
      while (i < j)
      {
        paramParsableByteArray.readBytes(patScratch, 4);
        int k = patScratch.readBits(16);
        patScratch.skipBits(3);
        if (k == 0)
        {
          patScratch.skipBits(13);
        }
        else
        {
          k = patScratch.readBits(13);
          tsPayloadReaders.put(k, new SectionReader(new TsExtractor.PmtReader(TsExtractor.this, k)));
          TsExtractor.access$108(TsExtractor.this);
        }
        i += 1;
      }
      if (mode != 2) {
        tsPayloadReaders.remove(0);
      }
    }
    
    public void init(TimestampAdjuster paramTimestampAdjuster, ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator) {}
  }
  
  class PmtReader
    implements SectionPayloadReader
  {
    private static final int TS_PMT_DESC_AC3 = 106;
    private static final int TS_PMT_DESC_DTS = 123;
    private static final int TS_PMT_DESC_DVBSUBS = 89;
    private static final int TS_PMT_DESC_EAC3 = 122;
    private static final int TS_PMT_DESC_ISO639_LANG = 10;
    private static final int TS_PMT_DESC_REGISTRATION = 5;
    private final ParsableBitArray pmtScratch = new ParsableBitArray(new byte[5]);
    private final SparseIntArray trackIdToPidScratch = new SparseIntArray();
    private final SparseArray<com.google.android.exoplayer2.extractor.ts.TsPayloadReader> trackIdToReaderScratch = new SparseArray();
    private final int val$group;
    
    public PmtReader(int paramInt)
    {
      val$group = paramInt;
    }
    
    private TsPayloadReader.EsInfo readEsInfo(ParsableByteArray paramParsableByteArray, int paramInt)
    {
      int i = paramParsableByteArray.getPosition();
      int j = paramInt + i;
      Object localObject1 = null;
      paramInt = -1;
      Object localObject4;
      for (Object localObject2 = null; paramParsableByteArray.getPosition() < j; localObject2 = localObject4)
      {
        int m = paramParsableByteArray.readUnsignedByte();
        int k = paramParsableByteArray.readUnsignedByte();
        k = paramParsableByteArray.getPosition() + k;
        if (m == 5)
        {
          long l = paramParsableByteArray.readUnsignedInt();
          if (l != TsExtractor.AC3_FORMAT_IDENTIFIER)
          {
            if (l == TsExtractor.E_AC3_FORMAT_IDENTIFIER) {
              break label144;
            }
            localObject3 = localObject1;
            localObject4 = localObject2;
            if (l != TsExtractor.HEVC_FORMAT_IDENTIFIER) {
              break label296;
            }
            paramInt = 36;
            localObject3 = localObject1;
            localObject4 = localObject2;
            break label296;
          }
        }
        else
        {
          if (m != 106) {
            break label137;
          }
        }
        paramInt = 129;
        Object localObject3 = localObject1;
        localObject4 = localObject2;
        break label296;
        label137:
        if (m == 122)
        {
          label144:
          paramInt = 135;
          localObject3 = localObject1;
          localObject4 = localObject2;
        }
        else if (m == 123)
        {
          paramInt = 138;
          localObject3 = localObject1;
          localObject4 = localObject2;
        }
        else if (m == 10)
        {
          localObject3 = paramParsableByteArray.readString(3).trim();
          localObject4 = localObject2;
        }
        else
        {
          localObject3 = localObject1;
          localObject4 = localObject2;
          if (m == 89)
          {
            localObject4 = new ArrayList();
            while (paramParsableByteArray.getPosition() < k)
            {
              localObject2 = paramParsableByteArray.readString(3).trim();
              paramInt = paramParsableByteArray.readUnsignedByte();
              localObject3 = new byte[4];
              paramParsableByteArray.readBytes((byte[])localObject3, 0, 4);
              ((List)localObject4).add(new TsPayloadReader.DvbSubtitleInfo((String)localObject2, paramInt, (byte[])localObject3));
            }
            paramInt = 89;
            localObject3 = localObject1;
          }
        }
        label296:
        paramParsableByteArray.skipBytes(k - paramParsableByteArray.getPosition());
        localObject1 = localObject3;
      }
      paramParsableByteArray.setPosition(j);
      return new TsPayloadReader.EsInfo(paramInt, localObject1, (List)localObject2, Arrays.copyOfRange(data, i, j));
    }
    
    public void consume(ParsableByteArray paramParsableByteArray)
    {
      if (paramParsableByteArray.readUnsignedByte() != 2) {
        return;
      }
      int i = mode;
      int k = 0;
      TimestampAdjuster localTimestampAdjuster;
      if ((i != 1) && (mode != 2) && (remainingPmts != 1))
      {
        localTimestampAdjuster = new TimestampAdjuster(((TimestampAdjuster)timestampAdjusters.get(0)).getFirstSampleTimestampUs());
        timestampAdjusters.add(localTimestampAdjuster);
      }
      else
      {
        localTimestampAdjuster = (TimestampAdjuster)timestampAdjusters.get(0);
      }
      paramParsableByteArray.skipBytes(2);
      int i1 = paramParsableByteArray.readUnsignedShort();
      paramParsableByteArray.skipBytes(3);
      paramParsableByteArray.readBytes(pmtScratch, 2);
      pmtScratch.skipBits(3);
      TsExtractor.access$402(TsExtractor.this, pmtScratch.readBits(13));
      paramParsableByteArray.readBytes(pmtScratch, 2);
      pmtScratch.skipBits(4);
      paramParsableByteArray.skipBytes(pmtScratch.readBits(12));
      Object localObject;
      if ((mode == 2) && (id3Reader == null))
      {
        localObject = new TsPayloadReader.EsInfo(21, null, null, Util.EMPTY_BYTE_ARRAY);
        TsExtractor.access$502(TsExtractor.this, payloadReaderFactory.createPayloadReader(21, (TsPayloadReader.EsInfo)localObject));
        id3Reader.init(localTimestampAdjuster, output, new TsPayloadReader.TrackIdGenerator(i1, 21, 8192));
      }
      trackIdToReaderScratch.clear();
      trackIdToPidScratch.clear();
      int n;
      int m;
      for (int j = paramParsableByteArray.bytesLeft(); j > 0; j = n)
      {
        paramParsableByteArray.readBytes(pmtScratch, 5);
        n = pmtScratch.readBits(8);
        i = n;
        pmtScratch.skipBits(3);
        m = pmtScratch.readBits(13);
        pmtScratch.skipBits(4);
        int i2 = pmtScratch.readBits(12);
        localObject = readEsInfo(paramParsableByteArray, i2);
        if (n == 6) {
          i = streamType;
        }
        n = j - (i2 + 5);
        if (mode == 2) {
          j = i;
        } else {
          j = m;
        }
        if (!trackIds.get(j))
        {
          if ((mode == 2) && (i == 21)) {
            localObject = id3Reader;
          } else {
            localObject = payloadReaderFactory.createPayloadReader(i, (TsPayloadReader.EsInfo)localObject);
          }
          if ((mode != 2) || (m < trackIdToPidScratch.get(j, 8192)))
          {
            trackIdToPidScratch.put(j, m);
            trackIdToReaderScratch.put(j, localObject);
          }
        }
      }
      j = trackIdToPidScratch.size();
      i = 0;
      while (i < j)
      {
        m = trackIdToPidScratch.keyAt(i);
        n = trackIdToPidScratch.valueAt(i);
        trackIds.put(m, true);
        trackPids.put(n, true);
        paramParsableByteArray = (TsPayloadReader)trackIdToReaderScratch.valueAt(i);
        if (paramParsableByteArray != null)
        {
          if (paramParsableByteArray != id3Reader) {
            paramParsableByteArray.init(localTimestampAdjuster, output, new TsPayloadReader.TrackIdGenerator(i1, m, 8192));
          }
          tsPayloadReaders.put(n, paramParsableByteArray);
        }
        i += 1;
      }
      if (mode == 2)
      {
        if (!tracksEnded)
        {
          output.endTracks();
          TsExtractor.access$102(TsExtractor.this, 0);
          TsExtractor.access$1002(TsExtractor.this, true);
        }
      }
      else
      {
        tsPayloadReaders.remove(val$group);
        paramParsableByteArray = TsExtractor.this;
        if (mode == 1) {
          i = k;
        } else {
          i = remainingPmts - 1;
        }
        TsExtractor.access$102(paramParsableByteArray, i);
        if (remainingPmts == 0)
        {
          output.endTracks();
          TsExtractor.access$1002(TsExtractor.this, true);
        }
      }
    }
    
    public void init(TimestampAdjuster paramTimestampAdjuster, ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator) {}
  }
}
