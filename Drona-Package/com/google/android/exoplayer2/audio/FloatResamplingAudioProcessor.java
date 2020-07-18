package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

final class FloatResamplingAudioProcessor
  implements AudioProcessor
{
  private static final int FLOAT_NAN_AS_INT = Float.floatToIntBits(NaN.0F);
  private static final double PCM_32_BIT_INT_TO_PCM_32_BIT_FLOAT_FACTOR = 4.656612875245797E-10D;
  private ByteBuffer buffer = AudioProcessor.EMPTY_BUFFER;
  private int channelCount = -1;
  private boolean inputEnded;
  private ByteBuffer outputBuffer = AudioProcessor.EMPTY_BUFFER;
  private int sampleRateHz = -1;
  private int sourceEncoding = 0;
  
  public FloatResamplingAudioProcessor() {}
  
  private static void writePcm32BitFloat(int paramInt, ByteBuffer paramByteBuffer)
  {
    int i = Float.floatToIntBits((float)(paramInt * 4.656612875245797E-10D));
    paramInt = i;
    if (i == FLOAT_NAN_AS_INT) {
      paramInt = Float.floatToIntBits(0.0F);
    }
    paramByteBuffer.putInt(paramInt);
  }
  
  public boolean configure(int paramInt1, int paramInt2, int paramInt3)
    throws AudioProcessor.UnhandledFormatException
  {
    if (Util.isEncodingHighResolutionIntegerPcm(paramInt3))
    {
      if ((sampleRateHz == paramInt1) && (channelCount == paramInt2) && (sourceEncoding == paramInt3)) {
        return false;
      }
      sampleRateHz = paramInt1;
      channelCount = paramInt2;
      sourceEncoding = paramInt3;
      return true;
    }
    throw new AudioProcessor.UnhandledFormatException(paramInt1, paramInt2, paramInt3);
  }
  
  public void flush()
  {
    outputBuffer = AudioProcessor.EMPTY_BUFFER;
    inputEnded = false;
  }
  
  public ByteBuffer getOutput()
  {
    ByteBuffer localByteBuffer = outputBuffer;
    outputBuffer = AudioProcessor.EMPTY_BUFFER;
    return localByteBuffer;
  }
  
  public int getOutputChannelCount()
  {
    return channelCount;
  }
  
  public int getOutputEncoding()
  {
    return 4;
  }
  
  public int getOutputSampleRateHz()
  {
    return sampleRateHz;
  }
  
  public boolean isActive()
  {
    return Util.isEncodingHighResolutionIntegerPcm(sourceEncoding);
  }
  
  public boolean isEnded()
  {
    return (inputEnded) && (outputBuffer == AudioProcessor.EMPTY_BUFFER);
  }
  
  public void queueEndOfStream()
  {
    inputEnded = true;
  }
  
  public void queueInput(ByteBuffer paramByteBuffer)
  {
    int j;
    if (sourceEncoding == 1073741824) {
      j = 1;
    } else {
      j = 0;
    }
    int k = paramByteBuffer.position();
    int i = k;
    int m = paramByteBuffer.limit();
    k = m - k;
    if (j == 0) {
      k = k / 3 * 4;
    }
    if (buffer.capacity() < k) {
      buffer = ByteBuffer.allocateDirect(k).order(ByteOrder.nativeOrder());
    } else {
      buffer.clear();
    }
    k = i;
    if (j != 0) {
      while (i < m)
      {
        writePcm32BitFloat(paramByteBuffer.get(i) & 0xFF | (paramByteBuffer.get(i + 1) & 0xFF) << 8 | (paramByteBuffer.get(i + 2) & 0xFF) << 16 | (paramByteBuffer.get(i + 3) & 0xFF) << 24, buffer);
        i += 4;
      }
    }
    while (k < m)
    {
      writePcm32BitFloat((paramByteBuffer.get(k) & 0xFF) << 8 | (paramByteBuffer.get(k + 1) & 0xFF) << 16 | (paramByteBuffer.get(k + 2) & 0xFF) << 24, buffer);
      k += 3;
    }
    paramByteBuffer.position(paramByteBuffer.limit());
    buffer.flip();
    outputBuffer = buffer;
  }
  
  public void reset()
  {
    flush();
    sampleRateHz = -1;
    channelCount = -1;
    sourceEncoding = 0;
    buffer = AudioProcessor.EMPTY_BUFFER;
  }
}
