package com.google.android.exoplayer2.extractor.mp3;

import com.google.android.exoplayer2.extractor.ConstantBitrateSeekMap;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;

final class ConstantBitrateSeeker
  extends ConstantBitrateSeekMap
  implements Mp3Extractor.Seeker
{
  public ConstantBitrateSeeker(long paramLong1, long paramLong2, MpegAudioHeader paramMpegAudioHeader)
  {
    super(paramLong1, paramLong2, bitrate, frameSize);
  }
  
  public long getDataEndPosition()
  {
    return -1L;
  }
  
  public long getTimeUs(long paramLong)
  {
    return getTimeUsAtPosition(paramLong);
  }
}
