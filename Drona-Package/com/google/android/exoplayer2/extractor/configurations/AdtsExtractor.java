package com.google.android.exoplayer2.extractor.configurations;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ConstantBitrateSeekMap;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class AdtsExtractor
  implements Extractor
{
  public static final ExtractorsFactory FACTORY = -..Lambda.AdtsExtractor.cqGYwjddB4W6E3ogPGiWfjTa23c.INSTANCE;
  public static final int FLAG_ENABLE_CONSTANT_BITRATE_SEEKING = 1;
  private static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
  private static final int MAX_PACKET_SIZE = 2048;
  private static final int MAX_SNIFF_BYTES = 8192;
  private static final int NUM_FRAMES_FOR_AVERAGE_FRAME_SIZE = 1000;
  private int averageFrameSize;
  @Nullable
  private ExtractorOutput extractorOutput;
  private long firstFramePosition;
  private long firstSampleTimestampUs;
  private final long firstStreamSampleTimestampUs;
  private final int flags;
  private boolean hasCalculatedAverageFrameSize;
  private boolean hasOutputSeekMap;
  private final ParsableByteArray packetBuffer;
  private final AdtsReader reader;
  private final ParsableByteArray scratch;
  private final ParsableBitArray scratchBits;
  private boolean startedPacket;
  
  public AdtsExtractor()
  {
    this(0L);
  }
  
  public AdtsExtractor(long paramLong)
  {
    this(paramLong, 0);
  }
  
  public AdtsExtractor(long paramLong, int paramInt)
  {
    firstStreamSampleTimestampUs = paramLong;
    firstSampleTimestampUs = paramLong;
    flags = paramInt;
    reader = new AdtsReader(true);
    packetBuffer = new ParsableByteArray(2048);
    averageFrameSize = -1;
    firstFramePosition = -1L;
    scratch = new ParsableByteArray(10);
    scratchBits = new ParsableBitArray(scratch.data);
  }
  
  private void calculateAverageFrameSize(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (hasCalculatedAverageFrameSize) {
      return;
    }
    averageFrameSize = -1;
    paramExtractorInput.resetPeekPosition();
    long l2 = paramExtractorInput.getPosition();
    long l1 = 0L;
    if (l2 == 0L) {
      peekId3Header(paramExtractorInput);
    }
    int i = 0;
    int j;
    int k;
    do
    {
      l2 = l1;
      j = i;
      if (!paramExtractorInput.peekFully(scratch.data, 0, 2, true)) {
        break label216;
      }
      scratch.setPosition(0);
      if (!AdtsReader.isAdtsSyncWord(scratch.readUnsignedShort()))
      {
        j = 0;
        l2 = l1;
        break label216;
      }
      if (!paramExtractorInput.peekFully(scratch.data, 0, 4, true))
      {
        l2 = l1;
        j = i;
        break label216;
      }
      scratchBits.setPosition(14);
      k = scratchBits.readBits(13);
      if (k <= 6) {
        break;
      }
      l2 = l1 + k;
      j = i + 1;
      if (j == 1000) {
        break label216;
      }
      l1 = l2;
      i = j;
    } while (paramExtractorInput.advancePeekPosition(k - 6, true));
    break label216;
    hasCalculatedAverageFrameSize = true;
    throw new ParserException("Malformed ADTS stream");
    label216:
    paramExtractorInput.resetPeekPosition();
    if (j > 0) {
      averageFrameSize = ((int)(l2 / j));
    } else {
      averageFrameSize = -1;
    }
    hasCalculatedAverageFrameSize = true;
  }
  
  private static int getBitrateFromFrameSize(int paramInt, long paramLong)
  {
    return (int)(paramInt * 8 * 1000000L / paramLong);
  }
  
  private SeekMap getConstantBitrateSeekMap(long paramLong)
  {
    int i = getBitrateFromFrameSize(averageFrameSize, reader.getSampleDurationUs());
    return new ConstantBitrateSeekMap(paramLong, firstFramePosition, i, averageFrameSize);
  }
  
  private void maybeOutputSeekMap(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (hasOutputSeekMap) {
      return;
    }
    int i;
    if ((paramBoolean1) && (averageFrameSize > 0)) {
      i = 1;
    } else {
      i = 0;
    }
    if ((i != 0) && (reader.getSampleDurationUs() == -9223372036854775807L) && (!paramBoolean2)) {
      return;
    }
    ExtractorOutput localExtractorOutput = (ExtractorOutput)Assertions.checkNotNull(extractorOutput);
    if ((i != 0) && (reader.getSampleDurationUs() != -9223372036854775807L)) {
      localExtractorOutput.seekMap(getConstantBitrateSeekMap(paramLong));
    } else {
      localExtractorOutput.seekMap(new SeekMap.Unseekable(-9223372036854775807L));
    }
    hasOutputSeekMap = true;
  }
  
  private int peekId3Header(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    int i = 0;
    for (;;)
    {
      paramExtractorInput.peekFully(scratch.data, 0, 10);
      scratch.setPosition(0);
      if (scratch.readUnsignedInt24() != ID3_TAG)
      {
        paramExtractorInput.resetPeekPosition();
        paramExtractorInput.advancePeekPosition(i);
        if (firstFramePosition != -1L) {
          break;
        }
        firstFramePosition = i;
        return i;
      }
      scratch.skipBytes(3);
      int j = scratch.readSynchSafeInt();
      i += j + 10;
      paramExtractorInput.advancePeekPosition(j);
    }
    return i;
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    extractorOutput = paramExtractorOutput;
    reader.createTracks(paramExtractorOutput, new TsPayloadReader.TrackIdGenerator(0, 1));
    paramExtractorOutput.endTracks();
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    long l = paramExtractorInput.getLength();
    boolean bool1;
    if (((flags & 0x1) != 0) && (l != -1L)) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    if (bool1) {
      calculateAverageFrameSize(paramExtractorInput);
    }
    int i = paramExtractorInput.read(packetBuffer.data, 0, 2048);
    boolean bool2;
    if (i == -1) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    maybeOutputSeekMap(l, bool1, bool2);
    if (bool2) {
      return -1;
    }
    packetBuffer.setPosition(0);
    packetBuffer.setLimit(i);
    if (!startedPacket)
    {
      reader.packetStarted(firstSampleTimestampUs, true);
      startedPacket = true;
    }
    reader.consume(packetBuffer);
    return 0;
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    startedPacket = false;
    reader.seek();
    firstSampleTimestampUs = (firstStreamSampleTimestampUs + paramLong2);
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    int m = peekId3Header(paramExtractorInput);
    int i = m;
    int k = 0;
    int j = 0;
    for (;;)
    {
      paramExtractorInput.peekFully(scratch.data, 0, 2);
      scratch.setPosition(0);
      if (!AdtsReader.isAdtsSyncWord(scratch.readUnsignedShort()))
      {
        paramExtractorInput.resetPeekPosition();
        i += 1;
        if (i - m >= 8192) {
          return false;
        }
        paramExtractorInput.advancePeekPosition(i);
        break;
      }
      k += 1;
      if ((k >= 4) && (j > 188)) {
        return true;
      }
      paramExtractorInput.peekFully(scratch.data, 0, 4);
      scratchBits.setPosition(14);
      int n = scratchBits.readBits(13);
      if (n <= 6) {
        return false;
      }
      paramExtractorInput.advancePeekPosition(n - 6);
      j += n;
    }
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface Flags {}
}
