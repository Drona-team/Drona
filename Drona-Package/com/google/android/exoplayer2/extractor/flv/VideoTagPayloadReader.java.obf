package com.google.android.exoplayer2.extractor.flv;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.video.AvcConfig;

final class VideoTagPayloadReader
  extends TagPayloadReader
{
  private static final int AVC_PACKET_TYPE_AVC_NALU = 1;
  private static final int AVC_PACKET_TYPE_SEQUENCE_HEADER = 0;
  private static final int VIDEO_CODEC_AVC = 7;
  private static final int VIDEO_FRAME_KEYFRAME = 1;
  private static final int VIDEO_FRAME_VIDEO_INFO = 5;
  private int frameType;
  private boolean hasOutputFormat;
  private final ParsableByteArray nalLength = new ParsableByteArray(4);
  private final ParsableByteArray nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
  private int nalUnitLengthFieldLength;
  
  public VideoTagPayloadReader(TrackOutput paramTrackOutput)
  {
    super(paramTrackOutput);
  }
  
  protected boolean parseHeader(ParsableByteArray paramParsableByteArray)
    throws TagPayloadReader.UnsupportedFormatException
  {
    int j = paramParsableByteArray.readUnsignedByte();
    int i = j >> 4 & 0xF;
    j &= 0xF;
    if (j == 7)
    {
      frameType = i;
      return i != 5;
    }
    paramParsableByteArray = new StringBuilder();
    paramParsableByteArray.append("Video format not supported: ");
    paramParsableByteArray.append(j);
    throw new TagPayloadReader.UnsupportedFormatException(paramParsableByteArray.toString());
  }
  
  protected void parsePayload(ParsableByteArray paramParsableByteArray, long paramLong)
    throws ParserException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    long l = paramParsableByteArray.readInt24();
    Object localObject;
    if ((i == 0) && (!hasOutputFormat))
    {
      localObject = new ParsableByteArray(new byte[paramParsableByteArray.bytesLeft()]);
      paramParsableByteArray.readBytes(data, 0, paramParsableByteArray.bytesLeft());
      paramParsableByteArray = AvcConfig.parse((ParsableByteArray)localObject);
      nalUnitLengthFieldLength = nalUnitLengthFieldLength;
      paramParsableByteArray = Format.createVideoSampleFormat(null, "video/avc", null, -1, -1, width, height, -1.0F, initializationData, -1, pixelWidthAspectRatio, null);
      output.format(paramParsableByteArray);
      hasOutputFormat = true;
      return;
    }
    if ((i == 1) && (hasOutputFormat))
    {
      localObject = nalLength.data;
      localObject[0] = 0;
      localObject[1] = 0;
      localObject[2] = 0;
      int j = nalUnitLengthFieldLength;
      int k;
      for (i = 0; paramParsableByteArray.bytesLeft() > 0; i = i + 4 + k)
      {
        paramParsableByteArray.readBytes(nalLength.data, 4 - j, nalUnitLengthFieldLength);
        nalLength.setPosition(0);
        k = nalLength.readUnsignedIntToInt();
        nalStartCode.setPosition(0);
        output.sampleData(nalStartCode, 4);
        output.sampleData(paramParsableByteArray, k);
      }
      paramParsableByteArray = output;
      if (frameType == 1) {
        j = 1;
      } else {
        j = 0;
      }
      paramParsableByteArray.sampleMetadata(paramLong + l * 1000L, j, i, 0, null);
    }
  }
  
  public void seek() {}
}
