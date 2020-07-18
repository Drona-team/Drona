package com.google.android.exoplayer2.extractor.labs;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.util.FlacStreamInfo;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

final class FlacReader
  extends StreamReader
{
  private static final byte AUDIO_PACKET_TYPE = -1;
  private static final int FRAME_HEADER_SAMPLE_NUMBER_OFFSET = 4;
  private static final byte SEEKTABLE_PACKET_TYPE = 3;
  private FlacOggSeeker flacOggSeeker;
  private FlacStreamInfo streamInfo;
  
  FlacReader() {}
  
  private int getFlacFrameBlockSize(ParsableByteArray paramParsableByteArray)
  {
    int i = (data[2] & 0xFF) >> 4;
    switch (i)
    {
    default: 
      return -1;
    case 8: 
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
      return 256 << i - 8;
    case 6: 
    case 7: 
      paramParsableByteArray.skipBytes(4);
      paramParsableByteArray.readUtf8EncodedLong();
      if (i == 6) {
        i = paramParsableByteArray.readUnsignedByte();
      } else {
        i = paramParsableByteArray.readUnsignedShort();
      }
      paramParsableByteArray.setPosition(0);
      return i + 1;
    case 2: 
    case 3: 
    case 4: 
    case 5: 
      return 576 << i - 2;
    }
    return 192;
  }
  
  private static boolean isAudioPacket(byte[] paramArrayOfByte)
  {
    return paramArrayOfByte[0] == -1;
  }
  
  public static boolean verifyBitstreamType(ParsableByteArray paramParsableByteArray)
  {
    return (paramParsableByteArray.bytesLeft() >= 5) && (paramParsableByteArray.readUnsignedByte() == 127) && (paramParsableByteArray.readUnsignedInt() == 1179402563L);
  }
  
  protected long preparePayload(ParsableByteArray paramParsableByteArray)
  {
    if (!isAudioPacket(data)) {
      return -1L;
    }
    return getFlacFrameBlockSize(paramParsableByteArray);
  }
  
  protected boolean readHeaders(ParsableByteArray paramParsableByteArray, long paramLong, StreamReader.SetupData paramSetupData)
    throws IOException, InterruptedException
  {
    byte[] arrayOfByte = data;
    if (streamInfo == null)
    {
      streamInfo = new FlacStreamInfo(arrayOfByte, 17);
      paramParsableByteArray = Arrays.copyOfRange(arrayOfByte, 9, paramParsableByteArray.limit());
      paramParsableByteArray[4] = -128;
      paramParsableByteArray = Collections.singletonList(paramParsableByteArray);
      format = Format.createAudioSampleFormat(null, "audio/flac", null, -1, streamInfo.bitRate(), streamInfo.channels, streamInfo.sampleRate, paramParsableByteArray, null, 0, null);
    }
    else if ((arrayOfByte[0] & 0x7F) == 3)
    {
      flacOggSeeker = new FlacOggSeeker();
      flacOggSeeker.parseSeekTable(paramParsableByteArray);
    }
    else if (isAudioPacket(arrayOfByte))
    {
      if (flacOggSeeker == null) {
        break label161;
      }
      flacOggSeeker.setFirstFrameOffset(paramLong);
      oggSeeker = flacOggSeeker;
      return false;
    }
    return true;
    label161:
    return false;
  }
  
  protected void reset(boolean paramBoolean)
  {
    super.reset(paramBoolean);
    if (paramBoolean)
    {
      streamInfo = null;
      flacOggSeeker = null;
    }
  }
  
  class FlacOggSeeker
    implements OggSeeker, SeekMap
  {
    private static final int METADATA_LENGTH_OFFSET = 1;
    private static final int SEEK_POINT_SIZE = 18;
    private long firstFrameOffset = -1L;
    private long pendingSeekGranule = -1L;
    private long[] seekPointGranules;
    private long[] seekPointOffsets;
    
    public FlacOggSeeker() {}
    
    public SeekMap createSeekMap()
    {
      return this;
    }
    
    public long getDurationUs()
    {
      return streamInfo.durationUs();
    }
    
    public SeekMap.SeekPoints getSeekPoints(long paramLong)
    {
      long l = convertTimeToGranule(paramLong);
      int i = Util.binarySearchFloor(seekPointGranules, l, true, true);
      l = convertGranuleToTime(seekPointGranules[i]);
      SeekPoint localSeekPoint = new SeekPoint(l, firstFrameOffset + seekPointOffsets[i]);
      if ((l < paramLong) && (i != seekPointGranules.length - 1))
      {
        FlacReader localFlacReader = FlacReader.this;
        long[] arrayOfLong = seekPointGranules;
        i += 1;
        return new SeekMap.SeekPoints(localSeekPoint, new SeekPoint(localFlacReader.convertGranuleToTime(arrayOfLong[i]), firstFrameOffset + seekPointOffsets[i]));
      }
      return new SeekMap.SeekPoints(localSeekPoint);
    }
    
    public boolean isSeekable()
    {
      return true;
    }
    
    public void parseSeekTable(ParsableByteArray paramParsableByteArray)
    {
      paramParsableByteArray.skipBytes(1);
      int j = paramParsableByteArray.readUnsignedInt24() / 18;
      seekPointGranules = new long[j];
      seekPointOffsets = new long[j];
      int i = 0;
      while (i < j)
      {
        seekPointGranules[i] = paramParsableByteArray.readLong();
        seekPointOffsets[i] = paramParsableByteArray.readLong();
        paramParsableByteArray.skipBytes(2);
        i += 1;
      }
    }
    
    public long read(ExtractorInput paramExtractorInput)
      throws IOException, InterruptedException
    {
      if (pendingSeekGranule >= 0L)
      {
        long l = -(pendingSeekGranule + 2L);
        pendingSeekGranule = -1L;
        return l;
      }
      return -1L;
    }
    
    public void setFirstFrameOffset(long paramLong)
    {
      firstFrameOffset = paramLong;
    }
    
    public long startSeek(long paramLong)
    {
      paramLong = convertTimeToGranule(paramLong);
      int i = Util.binarySearchFloor(seekPointGranules, paramLong, true, true);
      pendingSeekGranule = seekPointGranules[i];
      return paramLong;
    }
  }
}
