package com.google.android.exoplayer2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

final class ResamplingAudioProcessor
  implements AudioProcessor
{
  private ByteBuffer buffer = AudioProcessor.EMPTY_BUFFER;
  private int channelCount = -1;
  private int encoding = 0;
  private boolean inputEnded;
  private ByteBuffer outputBuffer = AudioProcessor.EMPTY_BUFFER;
  private int sampleRateHz = -1;
  
  public ResamplingAudioProcessor() {}
  
  public boolean configure(int paramInt1, int paramInt2, int paramInt3)
    throws AudioProcessor.UnhandledFormatException
  {
    if ((paramInt3 != 3) && (paramInt3 != 2) && (paramInt3 != Integer.MIN_VALUE) && (paramInt3 != 1073741824)) {
      throw new AudioProcessor.UnhandledFormatException(paramInt1, paramInt2, paramInt3);
    }
    if ((sampleRateHz == paramInt1) && (channelCount == paramInt2) && (encoding == paramInt3)) {
      return false;
    }
    sampleRateHz = paramInt1;
    channelCount = paramInt2;
    encoding = paramInt3;
    return true;
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
    return 2;
  }
  
  public int getOutputSampleRateHz()
  {
    return sampleRateHz;
  }
  
  public boolean isActive()
  {
    return (encoding != 0) && (encoding != 2);
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
    int j = paramByteBuffer.position();
    int i = j;
    int k = paramByteBuffer.limit();
    j = k - j;
    int m = encoding;
    if (m != Integer.MIN_VALUE)
    {
      if (m != 3)
      {
        if (m == 1073741824) {
          j /= 2;
        } else {
          throw new IllegalStateException();
        }
      }
      else {
        j *= 2;
      }
    }
    else {
      j = j / 3 * 2;
    }
    if (buffer.capacity() < j) {
      buffer = ByteBuffer.allocateDirect(j).order(ByteOrder.nativeOrder());
    } else {
      buffer.clear();
    }
    m = encoding;
    j = i;
    if (m != Integer.MIN_VALUE)
    {
      j = i;
      if (m != 3)
      {
        if (m == 1073741824) {
          while (i < k)
          {
            buffer.put(paramByteBuffer.get(i + 2));
            buffer.put(paramByteBuffer.get(i + 3));
            i += 4;
          }
        }
        throw new IllegalStateException();
      }
      while (j < k)
      {
        buffer.put((byte)0);
        buffer.put((byte)((paramByteBuffer.get(j) & 0xFF) - 128));
        j += 1;
      }
    }
    while (j < k)
    {
      buffer.put(paramByteBuffer.get(j + 1));
      buffer.put(paramByteBuffer.get(j + 2));
      j += 3;
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
    encoding = 0;
    buffer = AudioProcessor.EMPTY_BUFFER;
  }
}
