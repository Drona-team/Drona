package com.google.android.exoplayer2.extractor.labs;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;
import java.util.Arrays;

final class OggPacket
{
  private int currentSegmentIndex = -1;
  private final ParsableByteArray packetArray = new ParsableByteArray(new byte[65025], 0);
  private final OggPageHeader pageHeader = new OggPageHeader();
  private boolean populated;
  private int segmentCount;
  
  OggPacket() {}
  
  private int calculatePacketSize(int paramInt)
  {
    int i = 0;
    segmentCount = 0;
    int j;
    int k;
    do
    {
      j = i;
      if (segmentCount + paramInt >= pageHeader.pageSegmentCount) {
        break;
      }
      int[] arrayOfInt = pageHeader.laces;
      j = segmentCount;
      segmentCount = (j + 1);
      k = arrayOfInt[(j + paramInt)];
      j = i + k;
      i = j;
    } while (k == 255);
    return j;
  }
  
  public OggPageHeader getPageHeader()
  {
    return pageHeader;
  }
  
  public ParsableByteArray getPayload()
  {
    return packetArray;
  }
  
  public boolean populate(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    boolean bool;
    if (paramExtractorInput != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    if (populated)
    {
      populated = false;
      packetArray.reset();
    }
    while (!populated)
    {
      if (currentSegmentIndex < 0)
      {
        if (!pageHeader.populate(paramExtractorInput, true)) {
          return false;
        }
        i = pageHeader.headerSize;
        if (((pageHeader.type & 0x1) == 1) && (packetArray.limit() == 0))
        {
          i += calculatePacketSize(0);
          j = segmentCount + 0;
        }
        else
        {
          j = 0;
        }
        paramExtractorInput.skipFully(i);
        currentSegmentIndex = j;
      }
      int i = calculatePacketSize(currentSegmentIndex);
      int j = currentSegmentIndex + segmentCount;
      if (i > 0)
      {
        if (packetArray.capacity() < packetArray.limit() + i) {
          packetArray.data = Arrays.copyOf(packetArray.data, packetArray.limit() + i);
        }
        paramExtractorInput.readFully(packetArray.data, packetArray.limit(), i);
        packetArray.setLimit(packetArray.limit() + i);
        if (pageHeader.laces[(j - 1)] != 255) {
          bool = true;
        } else {
          bool = false;
        }
        populated = bool;
      }
      i = j;
      if (j == pageHeader.pageSegmentCount) {
        i = -1;
      }
      currentSegmentIndex = i;
    }
    return true;
  }
  
  public void reset()
  {
    pageHeader.reset();
    packetArray.reset();
    currentSegmentIndex = -1;
    populated = false;
  }
  
  public void trimPayload()
  {
    if (packetArray.data.length == 65025) {
      return;
    }
    packetArray.data = Arrays.copyOf(packetArray.data, Math.max(65025, packetArray.limit()));
  }
}
