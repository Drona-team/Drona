package com.google.android.exoplayer2.audio;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

final class ChannelMappingAudioProcessor
  implements AudioProcessor
{
  private boolean active;
  private ByteBuffer buffer = AudioProcessor.EMPTY_BUFFER;
  private int channelCount = -1;
  private boolean inputEnded;
  private ByteBuffer outputBuffer = AudioProcessor.EMPTY_BUFFER;
  @Nullable
  private int[] outputChannels;
  @Nullable
  private int[] pendingOutputChannels;
  private int sampleRateHz = -1;
  
  public ChannelMappingAudioProcessor() {}
  
  public boolean configure(int paramInt1, int paramInt2, int paramInt3)
    throws AudioProcessor.UnhandledFormatException
  {
    int k = Arrays.equals(pendingOutputChannels, outputChannels) ^ true;
    outputChannels = pendingOutputChannels;
    if (outputChannels == null)
    {
      active = false;
      return k;
    }
    if (paramInt3 == 2)
    {
      if ((k == 0) && (sampleRateHz == paramInt1) && (channelCount == paramInt2)) {
        return false;
      }
      sampleRateHz = paramInt1;
      channelCount = paramInt2;
      if (paramInt2 != outputChannels.length) {
        k = 1;
      } else {
        k = 0;
      }
      active = k;
      int i = 0;
      for (;;)
      {
        if (i >= outputChannels.length) {
          break label190;
        }
        int j = outputChannels[i];
        if (j >= paramInt2) {
          break;
        }
        k = active;
        if (j != i) {
          j = 1;
        } else {
          j = 0;
        }
        active = (j | k);
        i += 1;
      }
      throw new AudioProcessor.UnhandledFormatException(paramInt1, paramInt2, paramInt3);
    }
    throw new AudioProcessor.UnhandledFormatException(paramInt1, paramInt2, paramInt3);
    label190:
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
    if (outputChannels == null) {
      return channelCount;
    }
    return outputChannels.length;
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
    return active;
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
    boolean bool;
    if (outputChannels != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    int j = paramByteBuffer.position();
    int i = j;
    int k = paramByteBuffer.limit();
    j = (k - j) / (channelCount * 2) * outputChannels.length * 2;
    if (buffer.capacity() < j) {
      buffer = ByteBuffer.allocateDirect(j).order(ByteOrder.nativeOrder());
    } else {
      buffer.clear();
    }
    while (i < k)
    {
      int[] arrayOfInt = outputChannels;
      int m = arrayOfInt.length;
      j = 0;
      while (j < m)
      {
        int n = arrayOfInt[j];
        buffer.putShort(paramByteBuffer.getShort(n * 2 + i));
        j += 1;
      }
      i += channelCount * 2;
    }
    paramByteBuffer.position(k);
    buffer.flip();
    outputBuffer = buffer;
  }
  
  public void reset()
  {
    flush();
    buffer = AudioProcessor.EMPTY_BUFFER;
    channelCount = -1;
    sampleRateHz = -1;
    outputChannels = null;
    pendingOutputChannels = null;
    active = false;
  }
  
  public void setChannelMap(int[] paramArrayOfInt)
  {
    pendingOutputChannels = paramArrayOfInt;
  }
}
