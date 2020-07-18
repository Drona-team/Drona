package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

final class TrimmingAudioProcessor
  implements AudioProcessor
{
  private static final int OUTPUT_ENCODING = 2;
  private ByteBuffer buffer = AudioProcessor.EMPTY_BUFFER;
  private int bytesPerFrame;
  private int channelCount = -1;
  private byte[] endBuffer = Util.EMPTY_BYTE_ARRAY;
  private int endBufferSize;
  private boolean inputEnded;
  private boolean isActive;
  private ByteBuffer outputBuffer = AudioProcessor.EMPTY_BUFFER;
  private int pendingTrimStartBytes;
  private boolean receivedInputSinceConfigure;
  private int sampleRateHz = -1;
  private int trimEndFrames;
  private int trimStartFrames;
  private long trimmedFrameCount;
  
  public TrimmingAudioProcessor() {}
  
  public boolean configure(int paramInt1, int paramInt2, int paramInt3)
    throws AudioProcessor.UnhandledFormatException
  {
    if (paramInt3 == 2)
    {
      if (endBufferSize > 0) {
        trimmedFrameCount += endBufferSize / bytesPerFrame;
      }
      channelCount = paramInt2;
      sampleRateHz = paramInt1;
      bytesPerFrame = Util.getPcmFrameSize(2, paramInt2);
      endBuffer = new byte[trimEndFrames * bytesPerFrame];
      endBufferSize = 0;
      pendingTrimStartBytes = (trimStartFrames * bytesPerFrame);
      boolean bool2 = isActive;
      boolean bool1;
      if ((trimStartFrames == 0) && (trimEndFrames == 0)) {
        bool1 = false;
      } else {
        bool1 = true;
      }
      isActive = bool1;
      receivedInputSinceConfigure = false;
      if (bool2 != isActive) {
        return true;
      }
    }
    else
    {
      throw new AudioProcessor.UnhandledFormatException(paramInt1, paramInt2, paramInt3);
    }
    return false;
  }
  
  public void flush()
  {
    outputBuffer = AudioProcessor.EMPTY_BUFFER;
    inputEnded = false;
    if (receivedInputSinceConfigure) {
      pendingTrimStartBytes = 0;
    }
    endBufferSize = 0;
  }
  
  public ByteBuffer getOutput()
  {
    ByteBuffer localByteBuffer2 = outputBuffer;
    ByteBuffer localByteBuffer1 = localByteBuffer2;
    if (inputEnded)
    {
      localByteBuffer1 = localByteBuffer2;
      if (endBufferSize > 0)
      {
        localByteBuffer1 = localByteBuffer2;
        if (localByteBuffer2 == AudioProcessor.EMPTY_BUFFER)
        {
          if (buffer.capacity() < endBufferSize) {
            buffer = ByteBuffer.allocateDirect(endBufferSize).order(ByteOrder.nativeOrder());
          } else {
            buffer.clear();
          }
          buffer.put(endBuffer, 0, endBufferSize);
          endBufferSize = 0;
          buffer.flip();
          localByteBuffer1 = buffer;
        }
      }
    }
    outputBuffer = AudioProcessor.EMPTY_BUFFER;
    return localByteBuffer1;
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
  
  public long getTrimmedFrameCount()
  {
    return trimmedFrameCount;
  }
  
  public boolean isActive()
  {
    return isActive;
  }
  
  public boolean isEnded()
  {
    return (inputEnded) && (endBufferSize == 0) && (outputBuffer == AudioProcessor.EMPTY_BUFFER);
  }
  
  public void queueEndOfStream()
  {
    inputEnded = true;
  }
  
  public void queueInput(ByteBuffer paramByteBuffer)
  {
    int j = paramByteBuffer.position();
    int i = paramByteBuffer.limit();
    int k = i - j;
    if (k == 0) {
      return;
    }
    receivedInputSinceConfigure = true;
    int m = Math.min(k, pendingTrimStartBytes);
    trimmedFrameCount += m / bytesPerFrame;
    pendingTrimStartBytes -= m;
    paramByteBuffer.position(j + m);
    if (pendingTrimStartBytes > 0) {
      return;
    }
    k -= m;
    m = endBufferSize + k - endBuffer.length;
    if (buffer.capacity() < m) {
      buffer = ByteBuffer.allocateDirect(m).order(ByteOrder.nativeOrder());
    } else {
      buffer.clear();
    }
    j = Util.constrainValue(m, 0, endBufferSize);
    buffer.put(endBuffer, 0, j);
    m = Util.constrainValue(m - j, 0, k);
    paramByteBuffer.limit(paramByteBuffer.position() + m);
    buffer.put(paramByteBuffer);
    paramByteBuffer.limit(i);
    i = k - m;
    endBufferSize -= j;
    System.arraycopy(endBuffer, j, endBuffer, 0, endBufferSize);
    paramByteBuffer.get(endBuffer, endBufferSize, i);
    endBufferSize += i;
    buffer.flip();
    outputBuffer = buffer;
  }
  
  public void reset()
  {
    flush();
    buffer = AudioProcessor.EMPTY_BUFFER;
    channelCount = -1;
    sampleRateHz = -1;
    endBuffer = Util.EMPTY_BYTE_ARRAY;
  }
  
  public void resetTrimmedFrameCount()
  {
    trimmedFrameCount = 0L;
  }
  
  public void setTrimFrameCount(int paramInt1, int paramInt2)
  {
    trimStartFrames = paramInt1;
    trimEndFrames = paramInt2;
  }
}
