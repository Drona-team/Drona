package com.google.android.exoplayer2.extractor.configurations;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

final class PsDurationReader
{
  private static final int TIMESTAMP_SEARCH_BYTES = 20000;
  private long durationUs = -9223372036854775807L;
  private long firstScrValue = -9223372036854775807L;
  private boolean isDurationRead;
  private boolean isFirstScrValueRead;
  private boolean isLastScrValueRead;
  private long lastScrValue = -9223372036854775807L;
  private final ParsableByteArray packetBuffer = new ParsableByteArray();
  private final TimestampAdjuster scrTimestampAdjuster = new TimestampAdjuster(0L);
  
  PsDurationReader() {}
  
  private static boolean checkMarkerBits(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte[0] & 0xC4) != 68) {
      return false;
    }
    if ((paramArrayOfByte[2] & 0x4) != 4) {
      return false;
    }
    if ((paramArrayOfByte[4] & 0x4) != 4) {
      return false;
    }
    if ((paramArrayOfByte[5] & 0x1) != 1) {
      return false;
    }
    return (paramArrayOfByte[8] & 0x3) == 3;
  }
  
  private int finishReadDuration(ExtractorInput paramExtractorInput)
  {
    packetBuffer.reset(Util.EMPTY_BYTE_ARRAY);
    isDurationRead = true;
    paramExtractorInput.resetPeekPosition();
    return 0;
  }
  
  private int peekIntAtPosition(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramArrayOfByte[paramInt];
    int j = paramArrayOfByte[(paramInt + 1)];
    int k = paramArrayOfByte[(paramInt + 2)];
    return paramArrayOfByte[(paramInt + 3)] & 0xFF | (i & 0xFF) << 24 | (j & 0xFF) << 16 | (k & 0xFF) << 8;
  }
  
  private int readFirstScrValue(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    int i = (int)Math.min(20000L, paramExtractorInput.getLength());
    long l1 = paramExtractorInput.getPosition();
    long l2 = 0;
    if (l1 != l2)
    {
      position = l2;
      return 1;
    }
    packetBuffer.reset(i);
    paramExtractorInput.resetPeekPosition();
    paramExtractorInput.peekFully(packetBuffer.data, 0, i);
    firstScrValue = readFirstScrValueFromBuffer(packetBuffer);
    isFirstScrValueRead = true;
    return 0;
  }
  
  private long readFirstScrValueFromBuffer(ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.getPosition();
    int j = paramParsableByteArray.limit();
    while (i < j - 3)
    {
      if (peekIntAtPosition(data, i) == 442)
      {
        paramParsableByteArray.setPosition(i + 4);
        long l = readScrValueFromPack(paramParsableByteArray);
        if (l != -9223372036854775807L) {
          return l;
        }
      }
      i += 1;
    }
    return -9223372036854775807L;
  }
  
  private int readLastScrValue(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    long l = paramExtractorInput.getLength();
    int i = (int)Math.min(20000L, l);
    l -= i;
    if (paramExtractorInput.getPosition() != l)
    {
      position = l;
      return 1;
    }
    packetBuffer.reset(i);
    paramExtractorInput.resetPeekPosition();
    paramExtractorInput.peekFully(packetBuffer.data, 0, i);
    lastScrValue = readLastScrValueFromBuffer(packetBuffer);
    isLastScrValueRead = true;
    return 0;
  }
  
  private long readLastScrValueFromBuffer(ParsableByteArray paramParsableByteArray)
  {
    int j = paramParsableByteArray.getPosition();
    int i = paramParsableByteArray.limit() - 4;
    while (i >= j)
    {
      if (peekIntAtPosition(data, i) == 442)
      {
        paramParsableByteArray.setPosition(i + 4);
        long l = readScrValueFromPack(paramParsableByteArray);
        if (l != -9223372036854775807L) {
          return l;
        }
      }
      i -= 1;
    }
    return -9223372036854775807L;
  }
  
  public static long readScrValueFromPack(ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.getPosition();
    if (paramParsableByteArray.bytesLeft() < 9) {
      return -9223372036854775807L;
    }
    byte[] arrayOfByte = new byte[9];
    paramParsableByteArray.readBytes(arrayOfByte, 0, arrayOfByte.length);
    paramParsableByteArray.setPosition(i);
    if (!checkMarkerBits(arrayOfByte)) {
      return -9223372036854775807L;
    }
    return readScrValueFromPackHeader(arrayOfByte);
  }
  
  private static long readScrValueFromPackHeader(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[0] & 0x38) >> 3 << 30 | (paramArrayOfByte[0] & 0x3) << 28 | (paramArrayOfByte[1] & 0xFF) << 20 | (paramArrayOfByte[2] & 0xF8) >> 3 << 15 | (paramArrayOfByte[2] & 0x3) << 13 | (paramArrayOfByte[3] & 0xFF) << 5 | (paramArrayOfByte[4] & 0xF8) >> 3;
  }
  
  public long getDurationUs()
  {
    return durationUs;
  }
  
  public TimestampAdjuster getScrTimestampAdjuster()
  {
    return scrTimestampAdjuster;
  }
  
  public boolean isDurationReadFinished()
  {
    return isDurationRead;
  }
  
  public int readDuration(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    if (!isLastScrValueRead) {
      return readLastScrValue(paramExtractorInput, paramPositionHolder);
    }
    if (lastScrValue == -9223372036854775807L) {
      return finishReadDuration(paramExtractorInput);
    }
    if (!isFirstScrValueRead) {
      return readFirstScrValue(paramExtractorInput, paramPositionHolder);
    }
    if (firstScrValue == -9223372036854775807L) {
      return finishReadDuration(paramExtractorInput);
    }
    long l = scrTimestampAdjuster.adjustTsTimestamp(firstScrValue);
    durationUs = (scrTimestampAdjuster.adjustTsTimestamp(lastScrValue) - l);
    return finishReadDuration(paramExtractorInput);
  }
}
