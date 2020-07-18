package com.google.android.exoplayer2.extractor.configurations;

import com.google.android.exoplayer2.extractor.BinarySearchSeeker;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker.DefaultSeekTimestampConverter;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker.OutputFrameHolder;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker.TimestampSearchResult;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker.TimestampSeeker;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

final class PsBinarySearchSeeker
  extends BinarySearchSeeker
{
  private static final int MINIMUM_SEARCH_RANGE_BYTES = 1000;
  private static final long SEEK_TOLERANCE_US = 100000L;
  private static final int TIMESTAMP_SEARCH_BYTES = 20000;
  
  public PsBinarySearchSeeker(TimestampAdjuster paramTimestampAdjuster, long paramLong1, long paramLong2)
  {
    super(new BinarySearchSeeker.DefaultSeekTimestampConverter(), new PsScrSeeker(paramTimestampAdjuster, null), paramLong1, 0L, paramLong1 + 1L, 0L, paramLong2, 188L, 1000);
  }
  
  private static int peekIntAtPosition(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramArrayOfByte[paramInt];
    int j = paramArrayOfByte[(paramInt + 1)];
    int k = paramArrayOfByte[(paramInt + 2)];
    return paramArrayOfByte[(paramInt + 3)] & 0xFF | (i & 0xFF) << 24 | (j & 0xFF) << 16 | (k & 0xFF) << 8;
  }
  
  final class PsScrSeeker
    implements BinarySearchSeeker.TimestampSeeker
  {
    private final ParsableByteArray packetBuffer = new ParsableByteArray();
    
    private PsScrSeeker() {}
    
    private BinarySearchSeeker.TimestampSearchResult searchForScrValueInBuffer(ParsableByteArray paramParsableByteArray, long paramLong1, long paramLong2)
    {
      int k = -1;
      long l1 = -9223372036854775807L;
      int i = -1;
      while (paramParsableByteArray.bytesLeft() >= 4) {
        if (PsBinarySearchSeeker.peekIntAtPosition(data, paramParsableByteArray.getPosition()) != 442)
        {
          paramParsableByteArray.skipBytes(1);
        }
        else
        {
          paramParsableByteArray.skipBytes(4);
          long l3 = PsDurationReader.readScrValueFromPack(paramParsableByteArray);
          long l2 = l1;
          int j = i;
          if (l3 != -9223372036854775807L)
          {
            l2 = adjustTsTimestamp(l3);
            if (l2 > paramLong1)
            {
              if (l1 == -9223372036854775807L) {
                return BinarySearchSeeker.TimestampSearchResult.overestimatedResult(l2, paramLong2);
              }
              return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(paramLong2 + i);
            }
            if (100000L + l2 > paramLong1) {
              return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(paramLong2 + paramParsableByteArray.getPosition());
            }
            j = paramParsableByteArray.getPosition();
          }
          skipToEndOfCurrentPack(paramParsableByteArray);
          k = paramParsableByteArray.getPosition();
          l1 = l2;
          i = j;
        }
      }
      if (l1 != -9223372036854775807L) {
        return BinarySearchSeeker.TimestampSearchResult.underestimatedResult(l1, paramLong2 + k);
      }
      return BinarySearchSeeker.TimestampSearchResult.NO_TIMESTAMP_IN_RANGE_RESULT;
    }
    
    private static void skipToEndOfCurrentPack(ParsableByteArray paramParsableByteArray)
    {
      int i = paramParsableByteArray.limit();
      if (paramParsableByteArray.bytesLeft() < 10)
      {
        paramParsableByteArray.setPosition(i);
        return;
      }
      paramParsableByteArray.skipBytes(9);
      int j = paramParsableByteArray.readUnsignedByte() & 0x7;
      if (paramParsableByteArray.bytesLeft() < j)
      {
        paramParsableByteArray.setPosition(i);
        return;
      }
      paramParsableByteArray.skipBytes(j);
      if (paramParsableByteArray.bytesLeft() < 4)
      {
        paramParsableByteArray.setPosition(i);
        return;
      }
      if (PsBinarySearchSeeker.peekIntAtPosition(data, paramParsableByteArray.getPosition()) == 443)
      {
        paramParsableByteArray.skipBytes(4);
        j = paramParsableByteArray.readUnsignedShort();
        if (paramParsableByteArray.bytesLeft() < j)
        {
          paramParsableByteArray.setPosition(i);
          return;
        }
        paramParsableByteArray.skipBytes(j);
      }
      while (paramParsableByteArray.bytesLeft() >= 4)
      {
        j = PsBinarySearchSeeker.peekIntAtPosition(data, paramParsableByteArray.getPosition());
        if (j == 442) {
          break;
        }
        if (j == 441) {
          return;
        }
        if (j >>> 8 != 1) {
          return;
        }
        paramParsableByteArray.skipBytes(4);
        if (paramParsableByteArray.bytesLeft() < 2)
        {
          paramParsableByteArray.setPosition(i);
          return;
        }
        j = paramParsableByteArray.readUnsignedShort();
        paramParsableByteArray.setPosition(Math.min(paramParsableByteArray.limit(), paramParsableByteArray.getPosition() + j));
      }
    }
    
    public void onSeekFinished()
    {
      packetBuffer.reset(Util.EMPTY_BYTE_ARRAY);
    }
    
    public BinarySearchSeeker.TimestampSearchResult searchForTimestamp(ExtractorInput paramExtractorInput, long paramLong, BinarySearchSeeker.OutputFrameHolder paramOutputFrameHolder)
      throws IOException, InterruptedException
    {
      long l = paramExtractorInput.getPosition();
      int i = (int)Math.min(20000L, paramExtractorInput.getLength() - l);
      packetBuffer.reset(i);
      paramExtractorInput.peekFully(packetBuffer.data, 0, i);
      return searchForScrValueInBuffer(packetBuffer, paramLong, l);
    }
  }
}
