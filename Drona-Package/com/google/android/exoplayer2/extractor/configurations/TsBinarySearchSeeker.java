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

final class TsBinarySearchSeeker
  extends BinarySearchSeeker
{
  private static final int MINIMUM_SEARCH_RANGE_BYTES = 940;
  private static final long SEEK_TOLERANCE_US = 100000L;
  private static final int TIMESTAMP_SEARCH_BYTES = 112800;
  
  public TsBinarySearchSeeker(TimestampAdjuster paramTimestampAdjuster, long paramLong1, long paramLong2, int paramInt)
  {
    super(new BinarySearchSeeker.DefaultSeekTimestampConverter(), new TsPcrSeeker(paramInt, paramTimestampAdjuster), paramLong1, 0L, paramLong1 + 1L, 0L, paramLong2, 188L, 940);
  }
  
  final class TsPcrSeeker
    implements BinarySearchSeeker.TimestampSeeker
  {
    private final ParsableByteArray packetBuffer;
    private final int pcrPid;
    private final TimestampAdjuster pcrTimestampAdjuster;
    
    public TsPcrSeeker(TimestampAdjuster paramTimestampAdjuster)
    {
      pcrPid = this$1;
      pcrTimestampAdjuster = paramTimestampAdjuster;
      packetBuffer = new ParsableByteArray();
    }
    
    private BinarySearchSeeker.TimestampSearchResult searchForPcrValueInBuffer(ParsableByteArray paramParsableByteArray, long paramLong1, long paramLong2)
    {
      int i = paramParsableByteArray.limit();
      long l5 = -1L;
      long l2 = -1L;
      long l3;
      for (long l1 = -9223372036854775807L; paramParsableByteArray.bytesLeft() >= 188; l1 = l3)
      {
        int j = TsUtil.findSyncBytePosition(data, paramParsableByteArray.getPosition(), i);
        int k = j + 188;
        if (k > i) {
          break;
        }
        l5 = TsUtil.readPcrFromPacket(paramParsableByteArray, j, pcrPid);
        long l4 = l2;
        l3 = l1;
        if (l5 != -9223372036854775807L)
        {
          l3 = pcrTimestampAdjuster.adjustTsTimestamp(l5);
          if (l3 > paramLong1)
          {
            if (l1 == -9223372036854775807L) {
              return BinarySearchSeeker.TimestampSearchResult.overestimatedResult(l3, paramLong2);
            }
            return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(paramLong2 + l2);
          }
          if (100000L + l3 > paramLong1) {
            return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(j + paramLong2);
          }
          l4 = j;
        }
        paramParsableByteArray.setPosition(k);
        l5 = k;
        l2 = l4;
      }
      if (l1 != -9223372036854775807L) {
        return BinarySearchSeeker.TimestampSearchResult.underestimatedResult(l1, paramLong2 + l5);
      }
      return BinarySearchSeeker.TimestampSearchResult.NO_TIMESTAMP_IN_RANGE_RESULT;
    }
    
    public void onSeekFinished()
    {
      packetBuffer.reset(Util.EMPTY_BYTE_ARRAY);
    }
    
    public BinarySearchSeeker.TimestampSearchResult searchForTimestamp(ExtractorInput paramExtractorInput, long paramLong, BinarySearchSeeker.OutputFrameHolder paramOutputFrameHolder)
      throws IOException, InterruptedException
    {
      long l = paramExtractorInput.getPosition();
      int i = (int)Math.min(112800L, paramExtractorInput.getLength() - l);
      packetBuffer.reset(i);
      paramExtractorInput.peekFully(packetBuffer.data, 0, i);
      return searchForPcrValueInBuffer(packetBuffer, paramLong, l);
    }
  }
}
