package com.google.android.exoplayer2.extractor.webm;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;

final class Sniffer
{
  private static final int ID_EBML = 440786851;
  private static final int SEARCH_LENGTH = 1024;
  private int peekLength;
  private final ParsableByteArray scratch = new ParsableByteArray(8);
  
  public Sniffer() {}
  
  private long readUint(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    Object localObject = scratch.data;
    int k = 0;
    paramExtractorInput.peekFully((byte[])localObject, 0, 1);
    int m = scratch.data[0] & 0xFF;
    if (m == 0) {
      return Long.MIN_VALUE;
    }
    int j = 128;
    int i = 0;
    while ((m & j) == 0)
    {
      j >>= 1;
      i += 1;
    }
    j = m & j;
    ParsableByteArray localParsableByteArray = scratch;
    localObject = this;
    paramExtractorInput.peekFully(data, 1, i);
    while (k < i)
    {
      paramExtractorInput = scratch;
      paramExtractorInput = data;
      k += 1;
      j = (paramExtractorInput[k] & 0xFF) + (j << 8);
    }
    peekLength += i + 1;
    return j;
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    long l2 = paramExtractorInput.getLength();
    long l3 = 1024L;
    boolean bool = l2 < -1L;
    long l1 = l3;
    if (bool) {
      if (l2 > 1024L) {
        l1 = l3;
      } else {
        l1 = l2;
      }
    }
    int j = (int)l1;
    paramExtractorInput.peekFully(scratch.data, 0, 4);
    l1 = scratch.readUnsignedInt();
    peekLength = 4;
    while (l1 != 440786851L)
    {
      int k = peekLength + 1;
      peekLength = k;
      if (k == j) {
        return false;
      }
      paramExtractorInput.peekFully(scratch.data, 0, 1);
      l1 = l1 << 8 & 0xFFFFFFFFFFFFFF00 | scratch.data[0] & 0xFF;
    }
    l1 = readUint(paramExtractorInput);
    l3 = peekLength;
    if (l1 != Long.MIN_VALUE)
    {
      if ((bool) && (l3 + l1 >= l2)) {
        return false;
      }
      long l4;
      for (;;)
      {
        l2 = peekLength;
        l4 = l3 + l1;
        if (l2 >= l4) {
          break label289;
        }
        if (readUint(paramExtractorInput) == Long.MIN_VALUE) {
          return false;
        }
        l2 = readUint(paramExtractorInput);
        bool = l2 < 0L;
        if (bool) {
          break;
        }
        if (l2 > 2147483647L) {
          return false;
        }
        if (bool)
        {
          int i = (int)l2;
          paramExtractorInput.advancePeekPosition(i);
          peekLength += i;
        }
      }
      return false;
      label289:
      return peekLength == l4;
    }
    return false;
  }
}
