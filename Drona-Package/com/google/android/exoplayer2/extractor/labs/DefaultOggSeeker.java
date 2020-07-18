package com.google.android.exoplayer2.extractor.labs;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.util.Assertions;
import java.io.EOFException;
import java.io.IOException;

final class DefaultOggSeeker
  implements OggSeeker
{
  private static final int DEFAULT_OFFSET = 30000;
  public static final int MATCH_BYTE_RANGE = 100000;
  public static final int MATCH_RANGE = 72000;
  private static final int STATE_IDLE = 3;
  private static final int STATE_READ_LAST_PAGE = 1;
  private static final int STATE_SEEK = 2;
  private static final int STATE_SEEK_TO_END = 0;
  private long endGranule;
  private final long endPosition;
  private long len;
  private final OggPageHeader pageHeader = new OggPageHeader();
  private long positionBeforeSeekToEnd;
  private long start;
  private long startGranule;
  private final long startPosition;
  private int state;
  private final StreamReader streamReader;
  private long targetGranule;
  private long totalGranules;
  
  public DefaultOggSeeker(long paramLong1, long paramLong2, StreamReader paramStreamReader, long paramLong3, long paramLong4, boolean paramBoolean)
  {
    boolean bool;
    if ((paramLong1 >= 0L) && (paramLong2 > paramLong1)) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    streamReader = paramStreamReader;
    startPosition = paramLong1;
    endPosition = paramLong2;
    if ((paramLong3 != paramLong2 - paramLong1) && (!paramBoolean))
    {
      state = 0;
      return;
    }
    totalGranules = paramLong4;
    state = 3;
  }
  
  private long getEstimatedPosition(long paramLong1, long paramLong2, long paramLong3)
  {
    paramLong2 = paramLong1 + (paramLong2 * (endPosition - startPosition) / totalGranules - paramLong3);
    paramLong1 = paramLong2;
    if (paramLong2 < startPosition) {
      paramLong1 = startPosition;
    }
    paramLong2 = paramLong1;
    if (paramLong1 >= endPosition) {
      paramLong2 = endPosition - 1L;
    }
    return paramLong2;
  }
  
  public OggSeekMap createSeekMap()
  {
    if (totalGranules != 0L) {
      return new OggSeekMap(null);
    }
    return null;
  }
  
  public long getNextSeekPosition(long paramLong, ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    long l2 = start;
    long l3 = len;
    long l1 = 2L;
    if (l2 == l3) {
      return -(startGranule + 2L);
    }
    l3 = paramExtractorInput.getPosition();
    if (!skipToNextPage(paramExtractorInput, len))
    {
      if (start != l3) {
        return start;
      }
      throw new IOException("No ogg page can be found.");
    }
    pageHeader.populate(paramExtractorInput, false);
    paramExtractorInput.resetPeekPosition();
    l2 = paramLong - pageHeader.granulePosition;
    int i = pageHeader.headerSize + pageHeader.bodySize;
    boolean bool = l2 < 0L;
    if ((!bool) && (l2 <= 72000L))
    {
      paramExtractorInput.skipFully(i);
      return -(pageHeader.granulePosition + 2L);
    }
    if (bool)
    {
      len = l3;
      endGranule = pageHeader.granulePosition;
    }
    else
    {
      paramLong = paramExtractorInput.getPosition();
      l3 = i;
      start = (paramLong + l3);
      startGranule = pageHeader.granulePosition;
      if (len - start + l3 < 100000L)
      {
        paramExtractorInput.skipFully(i);
        return -(startGranule + 2L);
      }
    }
    if (len - start < 100000L)
    {
      len = start;
      return start;
    }
    l3 = i;
    if (!bool) {
      paramLong = l1;
    } else {
      paramLong = 1L;
    }
    return Math.min(Math.max(paramExtractorInput.getPosition() - l3 * paramLong + l2 * (len - start) / (endGranule - startGranule), start), len - 1L);
  }
  
  public long read(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    long l1;
    switch (state)
    {
    default: 
      throw new IllegalStateException();
    case 3: 
      return -1L;
    case 2: 
      long l2 = targetGranule;
      l1 = 0L;
      if (l2 != 0L)
      {
        l1 = getNextSeekPosition(targetGranule, paramExtractorInput);
        if (l1 >= 0L) {
          return l1;
        }
        l1 = skipToPageOfGranule(paramExtractorInput, targetGranule, -(l1 + 2L));
      }
      state = 3;
      return -(l1 + 2L);
    case 0: 
      positionBeforeSeekToEnd = paramExtractorInput.getPosition();
      state = 1;
      l1 = endPosition - 65307L;
      if (l1 > positionBeforeSeekToEnd) {
        return l1;
      }
      break;
    }
    totalGranules = readGranuleOfLastPage(paramExtractorInput);
    state = 3;
    return positionBeforeSeekToEnd;
  }
  
  long readGranuleOfLastPage(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    skipToNextPage(paramExtractorInput);
    pageHeader.reset();
    while (((pageHeader.type & 0x4) != 4) && (paramExtractorInput.getPosition() < endPosition))
    {
      pageHeader.populate(paramExtractorInput, false);
      paramExtractorInput.skipFully(pageHeader.headerSize + pageHeader.bodySize);
    }
    return pageHeader.granulePosition;
  }
  
  public void resetSeeking()
  {
    start = startPosition;
    len = endPosition;
    startGranule = 0L;
    endGranule = totalGranules;
  }
  
  void skipToNextPage(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (skipToNextPage(paramExtractorInput, endPosition)) {
      return;
    }
    throw new EOFException();
  }
  
  boolean skipToNextPage(ExtractorInput paramExtractorInput, long paramLong)
    throws IOException, InterruptedException
  {
    paramLong = Math.min(paramLong + 3L, endPosition);
    byte[] arrayOfByte = new byte['?'];
    int i = arrayOfByte.length;
    for (;;)
    {
      long l1 = paramExtractorInput.getPosition();
      long l2 = i;
      int j = 0;
      int k;
      if (l1 + l2 > paramLong)
      {
        k = (int)(paramLong - paramExtractorInput.getPosition());
        i = k;
        if (k < 4) {
          return false;
        }
      }
      paramExtractorInput.peekFully(arrayOfByte, 0, i, false);
      for (;;)
      {
        k = i - 3;
        if (j >= k) {
          break;
        }
        if ((arrayOfByte[j] == 79) && (arrayOfByte[(j + 1)] == 103) && (arrayOfByte[(j + 2)] == 103) && (arrayOfByte[(j + 3)] == 83))
        {
          paramExtractorInput.skipFully(j);
          return true;
        }
        j += 1;
      }
      paramExtractorInput.skipFully(k);
    }
  }
  
  long skipToPageOfGranule(ExtractorInput paramExtractorInput, long paramLong1, long paramLong2)
    throws IOException, InterruptedException
  {
    pageHeader.populate(paramExtractorInput, false);
    while (pageHeader.granulePosition < paramLong1)
    {
      paramExtractorInput.skipFully(pageHeader.headerSize + pageHeader.bodySize);
      paramLong2 = pageHeader.granulePosition;
      pageHeader.populate(paramExtractorInput, false);
    }
    paramExtractorInput.resetPeekPosition();
    return paramLong2;
  }
  
  public long startSeek(long paramLong)
  {
    boolean bool;
    if ((state != 3) && (state != 2)) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.checkArgument(bool);
    long l = 0L;
    if (paramLong == 0L) {
      paramLong = l;
    } else {
      paramLong = streamReader.convertTimeToGranule(paramLong);
    }
    targetGranule = paramLong;
    state = 2;
    resetSeeking();
    return targetGranule;
  }
  
  class OggSeekMap
    implements SeekMap
  {
    private OggSeekMap() {}
    
    public long getDurationUs()
    {
      return streamReader.convertGranuleToTime(totalGranules);
    }
    
    public SeekMap.SeekPoints getSeekPoints(long paramLong)
    {
      if (paramLong == 0L) {
        return new SeekMap.SeekPoints(new SeekPoint(0L, startPosition));
      }
      long l = streamReader.convertTimeToGranule(paramLong);
      return new SeekMap.SeekPoints(new SeekPoint(paramLong, DefaultOggSeeker.this.getEstimatedPosition(startPosition, l, 30000L)));
    }
    
    public boolean isSeekable()
    {
      return true;
    }
  }
}