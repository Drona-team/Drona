package com.google.android.exoplayer2.extractor.ogg;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ConstantBitrateSeekMap;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

public final class AmrExtractor
  implements Extractor
{
  public static final ExtractorsFactory FACTORY = -..Lambda.AmrExtractor.lVuGuaAcylUV-_XE4-hSR1hBylI.INSTANCE;
  public static final int FLAG_ENABLE_CONSTANT_BITRATE_SEEKING = 1;
  private static final int MAX_FRAME_SIZE_BYTES = frameSizeBytesByTypeWb[8];
  private static final int NUM_SAME_SIZE_CONSTANT_BIT_RATE_THRESHOLD = 20;
  private static final int SAMPLE_RATE_NB = 8000;
  private static final int SAMPLE_RATE_WB = 16000;
  private static final int SAMPLE_TIME_PER_FRAME_US = 20000;
  private static final byte[] amrSignatureNb;
  private static final byte[] amrSignatureWb;
  private static final int[] frameSizeBytesByTypeNb = { 13, 14, 16, 18, 20, 21, 27, 32, 6, 7, 6, 6, 1, 1, 1, 1 };
  private static final int[] frameSizeBytesByTypeWb = { 18, 24, 33, 37, 41, 47, 51, 59, 61, 6, 1, 1, 1, 1, 1, 1 };
  private int currentSampleBytesRemaining;
  private int currentSampleSize;
  private long currentSampleTimeUs;
  private ExtractorOutput extractorOutput;
  private long firstSamplePosition;
  private int firstSampleSize;
  private final int flags;
  private boolean hasOutputFormat;
  private boolean hasOutputSeekMap;
  private boolean isWideBand;
  private int numSamplesWithSameSize;
  private final byte[] scratch;
  @Nullable
  private SeekMap seekMap;
  private long timeOffsetUs;
  private TrackOutput trackOutput;
  
  static
  {
    amrSignatureNb = Util.getUtf8Bytes("#!AMR\n");
    amrSignatureWb = Util.getUtf8Bytes("#!AMR-WB\n");
  }
  
  public AmrExtractor()
  {
    this(0);
  }
  
  public AmrExtractor(int paramInt)
  {
    flags = paramInt;
    scratch = new byte[1];
    firstSampleSize = -1;
  }
  
  static byte[] amrSignatureNb()
  {
    return Arrays.copyOf(amrSignatureNb, amrSignatureNb.length);
  }
  
  static byte[] amrSignatureWb()
  {
    return Arrays.copyOf(amrSignatureWb, amrSignatureWb.length);
  }
  
  static int frameSizeBytesByTypeNb(int paramInt)
  {
    return frameSizeBytesByTypeNb[paramInt];
  }
  
  static int frameSizeBytesByTypeWb(int paramInt)
  {
    return frameSizeBytesByTypeWb[paramInt];
  }
  
  private static int getBitrateFromFrameSize(int paramInt, long paramLong)
  {
    return (int)(paramInt * 8 * 1000000L / paramLong);
  }
  
  private SeekMap getConstantBitrateSeekMap(long paramLong)
  {
    int i = getBitrateFromFrameSize(firstSampleSize, 20000L);
    return new ConstantBitrateSeekMap(paramLong, firstSamplePosition, i, firstSampleSize);
  }
  
  private int getFrameSizeInBytes(int paramInt)
    throws ParserException
  {
    if (!isValidFrameType(paramInt))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Illegal AMR ");
      String str;
      if (isWideBand) {
        str = "WB";
      } else {
        str = "NB";
      }
      localStringBuilder.append(str);
      localStringBuilder.append(" frame type ");
      localStringBuilder.append(paramInt);
      throw new ParserException(localStringBuilder.toString());
    }
    if (isWideBand) {
      return frameSizeBytesByTypeWb[paramInt];
    }
    return frameSizeBytesByTypeNb[paramInt];
  }
  
  private boolean isNarrowBandValidFrameType(int paramInt)
  {
    return (!isWideBand) && ((paramInt < 12) || (paramInt > 14));
  }
  
  private boolean isValidFrameType(int paramInt)
  {
    return (paramInt >= 0) && (paramInt <= 15) && ((isWideBandValidFrameType(paramInt)) || (isNarrowBandValidFrameType(paramInt)));
  }
  
  private boolean isWideBandValidFrameType(int paramInt)
  {
    return (isWideBand) && ((paramInt < 10) || (paramInt > 13));
  }
  
  private void maybeOutputFormat()
  {
    if (!hasOutputFormat)
    {
      hasOutputFormat = true;
      if (isWideBand) {}
      for (String str = "audio/amr-wb";; str = "audio/3gpp") {
        break;
      }
      int i;
      if (isWideBand) {
        i = 16000;
      } else {
        i = 8000;
      }
      trackOutput.format(Format.createAudioSampleFormat(null, str, null, -1, MAX_FRAME_SIZE_BYTES, 1, i, -1, null, null, 0, null));
    }
  }
  
  private void maybeOutputSeekMap(long paramLong, int paramInt)
  {
    if (hasOutputSeekMap) {
      return;
    }
    if (((flags & 0x1) != 0) && (paramLong != -1L) && ((firstSampleSize == -1) || (firstSampleSize == currentSampleSize)))
    {
      if ((numSamplesWithSameSize >= 20) || (paramInt == -1))
      {
        seekMap = getConstantBitrateSeekMap(paramLong);
        extractorOutput.seekMap(seekMap);
        hasOutputSeekMap = true;
      }
    }
    else
    {
      seekMap = new SeekMap.Unseekable(-9223372036854775807L);
      extractorOutput.seekMap(seekMap);
      hasOutputSeekMap = true;
    }
  }
  
  private boolean peekAmrSignature(ExtractorInput paramExtractorInput, byte[] paramArrayOfByte)
    throws IOException, InterruptedException
  {
    paramExtractorInput.resetPeekPosition();
    byte[] arrayOfByte = new byte[paramArrayOfByte.length];
    paramExtractorInput.peekFully(arrayOfByte, 0, paramArrayOfByte.length);
    return Arrays.equals(arrayOfByte, paramArrayOfByte);
  }
  
  private int peekNextSampleSize(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    paramExtractorInput.resetPeekPosition();
    paramExtractorInput.peekFully(scratch, 0, 1);
    int i = scratch[0];
    if ((i & 0x83) <= 0) {
      return getFrameSizeInBytes(i >> 3 & 0xF);
    }
    paramExtractorInput = new StringBuilder();
    paramExtractorInput.append("Invalid padding bits for frame header ");
    paramExtractorInput.append(i);
    throw new ParserException(paramExtractorInput.toString());
  }
  
  private boolean readAmrHeader(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (peekAmrSignature(paramExtractorInput, amrSignatureNb))
    {
      isWideBand = false;
      paramExtractorInput.skipFully(amrSignatureNb.length);
      return true;
    }
    if (peekAmrSignature(paramExtractorInput, amrSignatureWb))
    {
      isWideBand = true;
      paramExtractorInput.skipFully(amrSignatureWb.length);
      return true;
    }
    return false;
  }
  
  private int readSample(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (currentSampleBytesRemaining == 0) {}
    try
    {
      int i = peekNextSampleSize(paramExtractorInput);
      currentSampleSize = i;
      currentSampleBytesRemaining = currentSampleSize;
      if (firstSampleSize == -1)
      {
        firstSamplePosition = paramExtractorInput.getPosition();
        firstSampleSize = currentSampleSize;
      }
      if (firstSampleSize == currentSampleSize) {
        numSamplesWithSameSize += 1;
      }
      i = trackOutput.sampleData(paramExtractorInput, currentSampleBytesRemaining, true);
      if (i == -1) {
        return -1;
      }
      currentSampleBytesRemaining -= i;
      if (currentSampleBytesRemaining > 0) {
        return 0;
      }
      trackOutput.sampleMetadata(timeOffsetUs + currentSampleTimeUs, 1, currentSampleSize, 0, null);
      currentSampleTimeUs += 20000L;
      return 0;
    }
    catch (EOFException paramExtractorInput) {}
    return -1;
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    extractorOutput = paramExtractorOutput;
    trackOutput = paramExtractorOutput.track(0, 1);
    paramExtractorOutput.endTracks();
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    if ((paramExtractorInput.getPosition() == 0L) && (!readAmrHeader(paramExtractorInput))) {
      throw new ParserException("Could not find AMR header.");
    }
    maybeOutputFormat();
    int i = readSample(paramExtractorInput);
    maybeOutputSeekMap(paramExtractorInput.getLength(), i);
    return i;
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    currentSampleTimeUs = 0L;
    currentSampleSize = 0;
    currentSampleBytesRemaining = 0;
    if ((paramLong1 != 0L) && ((seekMap instanceof ConstantBitrateSeekMap)))
    {
      timeOffsetUs = ((ConstantBitrateSeekMap)seekMap).getTimeUsAtPosition(paramLong1);
      return;
    }
    timeOffsetUs = 0L;
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return readAmrHeader(paramExtractorInput);
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface Flags {}
}
