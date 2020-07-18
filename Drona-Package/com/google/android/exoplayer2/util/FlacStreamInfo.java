package com.google.android.exoplayer2.util;

public final class FlacStreamInfo
{
  public final int bitsPerSample;
  public final int channels;
  public final int maxBlockSize;
  public final int maxFrameSize;
  public final int minBlockSize;
  public final int minFrameSize;
  public final int sampleRate;
  public final long totalSamples;
  
  public FlacStreamInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, long paramLong)
  {
    minBlockSize = paramInt1;
    maxBlockSize = paramInt2;
    minFrameSize = paramInt3;
    maxFrameSize = paramInt4;
    sampleRate = paramInt5;
    channels = paramInt6;
    bitsPerSample = paramInt7;
    totalSamples = paramLong;
  }
  
  public FlacStreamInfo(byte[] paramArrayOfByte, int paramInt)
  {
    paramArrayOfByte = new ParsableBitArray(paramArrayOfByte);
    paramArrayOfByte.setPosition(paramInt * 8);
    minBlockSize = paramArrayOfByte.readBits(16);
    maxBlockSize = paramArrayOfByte.readBits(16);
    minFrameSize = paramArrayOfByte.readBits(24);
    maxFrameSize = paramArrayOfByte.readBits(24);
    sampleRate = paramArrayOfByte.readBits(20);
    channels = (paramArrayOfByte.readBits(3) + 1);
    bitsPerSample = (paramArrayOfByte.readBits(5) + 1);
    totalSamples = ((paramArrayOfByte.readBits(4) & 0xF) << 32 | paramArrayOfByte.readBits(32) & 0xFFFFFFFF);
  }
  
  public int bitRate()
  {
    return bitsPerSample * sampleRate;
  }
  
  public long durationUs()
  {
    return totalSamples * 1000000L / sampleRate;
  }
  
  public long getApproxBytesPerFrame()
  {
    if (maxFrameSize > 0) {
      return (maxFrameSize + minFrameSize) / 2L + 1L;
    }
    long l;
    if ((minBlockSize == maxBlockSize) && (minBlockSize > 0)) {
      l = minBlockSize;
    } else {
      l = 4096L;
    }
    return l * channels * bitsPerSample / 8L + 64L;
  }
  
  public long getSampleIndex(long paramLong)
  {
    return Util.constrainValue(paramLong * sampleRate / 1000000L, 0L, totalSamples - 1L);
  }
  
  public int maxDecodedFrameSize()
  {
    return maxBlockSize * channels * (bitsPerSample / 8);
  }
}
