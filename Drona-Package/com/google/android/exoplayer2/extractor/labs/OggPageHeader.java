package com.google.android.exoplayer2.extractor.labs;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;

final class OggPageHeader
{
  public static final int EMPTY_PAGE_HEADER_SIZE = 27;
  public static final int MAX_PAGE_PAYLOAD = 65025;
  public static final int MAX_PAGE_SIZE = 65307;
  public static final int MAX_SEGMENT_COUNT = 255;
  private static final int TYPE_OGGS = Util.getIntegerCodeForString("OggS");
  public int bodySize;
  public long granulePosition;
  public int headerSize;
  public final int[] laces = new int['?'];
  public long pageChecksum;
  public int pageSegmentCount;
  public long pageSequenceNumber;
  public int revision;
  private final ParsableByteArray scratch = new ParsableByteArray(255);
  public long streamSerialNumber;
  public int type;
  
  OggPageHeader() {}
  
  public boolean populate(ExtractorInput paramExtractorInput, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    scratch.reset();
    reset();
    long l = paramExtractorInput.getLength();
    int j = 0;
    int i;
    if ((l != -1L) && (paramExtractorInput.getLength() - paramExtractorInput.getPeekPosition() < 27L)) {
      i = 0;
    } else {
      i = 1;
    }
    if ((i != 0) && (paramExtractorInput.peekFully(scratch.data, 0, 27, true)))
    {
      if (scratch.readUnsignedInt() != TYPE_OGGS)
      {
        if (paramBoolean) {
          return false;
        }
        throw new ParserException("expected OggS capture pattern at begin of page");
      }
      revision = scratch.readUnsignedByte();
      if (revision != 0)
      {
        if (paramBoolean) {
          return false;
        }
        throw new ParserException("unsupported bit stream revision");
      }
      type = scratch.readUnsignedByte();
      granulePosition = scratch.readLittleEndianLong();
      streamSerialNumber = scratch.readLittleEndianUnsignedInt();
      pageSequenceNumber = scratch.readLittleEndianUnsignedInt();
      pageChecksum = scratch.readLittleEndianUnsignedInt();
      pageSegmentCount = scratch.readUnsignedByte();
      headerSize = (pageSegmentCount + 27);
      scratch.reset();
      paramExtractorInput.peekFully(scratch.data, 0, pageSegmentCount);
      i = j;
      while (i < pageSegmentCount)
      {
        laces[i] = scratch.readUnsignedByte();
        bodySize += laces[i];
        i += 1;
      }
      return true;
    }
    if (paramBoolean) {
      return false;
    }
    throw new EOFException();
  }
  
  public void reset()
  {
    revision = 0;
    type = 0;
    granulePosition = 0L;
    streamSerialNumber = 0L;
    pageSequenceNumber = 0L;
    pageChecksum = 0L;
    pageSegmentCount = 0;
    headerSize = 0;
    bodySize = 0;
  }
}
