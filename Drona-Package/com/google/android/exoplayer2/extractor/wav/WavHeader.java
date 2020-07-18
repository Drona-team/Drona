package com.google.android.exoplayer2.extractor.wav;

import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.util.Util;

final class WavHeader
  implements SeekMap
{
  private final int averageBytesPerSecond;
  private final int bitsPerSample;
  private final int blockAlignment;
  private long dataSize;
  private long dataStartPosition;
  private final int encoding;
  private final int numChannels;
  private final int sampleRateHz;
  
  public WavHeader(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    numChannels = paramInt1;
    sampleRateHz = paramInt2;
    averageBytesPerSecond = paramInt3;
    blockAlignment = paramInt4;
    bitsPerSample = paramInt5;
    encoding = paramInt6;
  }
  
  public int getBitrate()
  {
    return sampleRateHz * bitsPerSample * numChannels;
  }
  
  public int getBytesPerFrame()
  {
    return blockAlignment;
  }
  
  public long getDataLimit()
  {
    if (hasDataBounds()) {
      return dataStartPosition + dataSize;
    }
    return -1L;
  }
  
  public long getDurationUs()
  {
    return dataSize / blockAlignment * 1000000L / sampleRateHz;
  }
  
  public int getEncoding()
  {
    return encoding;
  }
  
  public int getNumChannels()
  {
    return numChannels;
  }
  
  public int getSampleRateHz()
  {
    return sampleRateHz;
  }
  
  public SeekMap.SeekPoints getSeekPoints(long paramLong)
  {
    long l1 = Util.constrainValue(averageBytesPerSecond * paramLong / 1000000L / blockAlignment * blockAlignment, 0L, dataSize - blockAlignment);
    long l2 = dataStartPosition + l1;
    long l3 = getTimeUs(l2);
    SeekPoint localSeekPoint = new SeekPoint(l3, l2);
    if ((l3 < paramLong) && (l1 != dataSize - blockAlignment))
    {
      paramLong = l2 + blockAlignment;
      return new SeekMap.SeekPoints(localSeekPoint, new SeekPoint(getTimeUs(paramLong), paramLong));
    }
    return new SeekMap.SeekPoints(localSeekPoint);
  }
  
  public long getTimeUs(long paramLong)
  {
    return Math.max(0L, paramLong - dataStartPosition) * 1000000L / averageBytesPerSecond;
  }
  
  public boolean hasDataBounds()
  {
    return (dataStartPosition != 0L) && (dataSize != 0L);
  }
  
  public boolean isSeekable()
  {
    return true;
  }
  
  public void setDataBounds(long paramLong1, long paramLong2)
  {
    dataStartPosition = paramLong1;
    dataSize = paramLong2;
  }
}
