package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class SilenceSkippingAudioProcessor
  implements AudioProcessor
{
  private static final long MINIMUM_SILENCE_DURATION_US = 150000L;
  private static final long PADDING_SILENCE_US = 20000L;
  private static final short SILENCE_THRESHOLD_LEVEL = 1024;
  private static final byte SILENCE_THRESHOLD_LEVEL_MSB = 4;
  private static final int STATE_MAYBE_SILENT = 1;
  private static final int STATE_NOISY = 0;
  private static final int STATE_SILENT = 2;
  private ByteBuffer buffer = AudioProcessor.EMPTY_BUFFER;
  private int bytesPerFrame;
  private int channelCount = -1;
  private boolean enabled;
  private boolean hasOutputNoise;
  private boolean inputEnded;
  private byte[] maybeSilenceBuffer = Util.EMPTY_BYTE_ARRAY;
  private int maybeSilenceBufferSize;
  private ByteBuffer outputBuffer = AudioProcessor.EMPTY_BUFFER;
  private byte[] paddingBuffer = Util.EMPTY_BYTE_ARRAY;
  private int paddingSize;
  private int sampleRateHz = -1;
  private long skippedFrames;
  private int state;
  
  public SilenceSkippingAudioProcessor() {}
  
  private int durationUsToFrames(long paramLong)
  {
    return (int)(paramLong * sampleRateHz / 1000000L);
  }
  
  private int findNoiseLimit(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.limit() - 1;
    while (i >= paramByteBuffer.position())
    {
      if (Math.abs(paramByteBuffer.get(i)) > 4) {
        return bytesPerFrame * (i / bytesPerFrame) + bytesPerFrame;
      }
      i -= 2;
    }
    return paramByteBuffer.position();
  }
  
  private int findNoisePosition(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.position() + 1;
    while (i < paramByteBuffer.limit())
    {
      if (Math.abs(paramByteBuffer.get(i)) > 4) {
        return bytesPerFrame * (i / bytesPerFrame);
      }
      i += 2;
    }
    return paramByteBuffer.limit();
  }
  
  private void output(ByteBuffer paramByteBuffer)
  {
    prepareForOutput(paramByteBuffer.remaining());
    buffer.put(paramByteBuffer);
    buffer.flip();
    outputBuffer = buffer;
  }
  
  private void output(byte[] paramArrayOfByte, int paramInt)
  {
    prepareForOutput(paramInt);
    buffer.put(paramArrayOfByte, 0, paramInt);
    buffer.flip();
    outputBuffer = buffer;
  }
  
  private void prepareForOutput(int paramInt)
  {
    if (buffer.capacity() < paramInt) {
      buffer = ByteBuffer.allocateDirect(paramInt).order(ByteOrder.nativeOrder());
    } else {
      buffer.clear();
    }
    if (paramInt > 0) {
      hasOutputNoise = true;
    }
  }
  
  private void processMaybeSilence(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.limit();
    int j = findNoisePosition(paramByteBuffer);
    int k = j - paramByteBuffer.position();
    int m = maybeSilenceBuffer.length - maybeSilenceBufferSize;
    if ((j < i) && (k < m))
    {
      output(maybeSilenceBuffer, maybeSilenceBufferSize);
      maybeSilenceBufferSize = 0;
      state = 0;
      return;
    }
    j = Math.min(k, m);
    paramByteBuffer.limit(paramByteBuffer.position() + j);
    paramByteBuffer.get(maybeSilenceBuffer, maybeSilenceBufferSize, j);
    maybeSilenceBufferSize += j;
    if (maybeSilenceBufferSize == maybeSilenceBuffer.length)
    {
      if (hasOutputNoise)
      {
        output(maybeSilenceBuffer, paddingSize);
        skippedFrames += (maybeSilenceBufferSize - paddingSize * 2) / bytesPerFrame;
      }
      else
      {
        skippedFrames += (maybeSilenceBufferSize - paddingSize) / bytesPerFrame;
      }
      updatePaddingBuffer(paramByteBuffer, maybeSilenceBuffer, maybeSilenceBufferSize);
      maybeSilenceBufferSize = 0;
      state = 2;
    }
    paramByteBuffer.limit(i);
  }
  
  private void processNoisy(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.limit();
    paramByteBuffer.limit(Math.min(i, paramByteBuffer.position() + maybeSilenceBuffer.length));
    int j = findNoiseLimit(paramByteBuffer);
    if (j == paramByteBuffer.position())
    {
      state = 1;
    }
    else
    {
      paramByteBuffer.limit(j);
      output(paramByteBuffer);
    }
    paramByteBuffer.limit(i);
  }
  
  private void processSilence(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.limit();
    int j = findNoisePosition(paramByteBuffer);
    paramByteBuffer.limit(j);
    skippedFrames += paramByteBuffer.remaining() / bytesPerFrame;
    updatePaddingBuffer(paramByteBuffer, paddingBuffer, paddingSize);
    if (j < i)
    {
      output(paddingBuffer, paddingSize);
      state = 0;
      paramByteBuffer.limit(i);
    }
  }
  
  private void updatePaddingBuffer(ByteBuffer paramByteBuffer, byte[] paramArrayOfByte, int paramInt)
  {
    int i = Math.min(paramByteBuffer.remaining(), paddingSize);
    int j = paddingSize - i;
    System.arraycopy(paramArrayOfByte, paramInt - j, paddingBuffer, 0, j);
    paramByteBuffer.position(paramByteBuffer.limit() - i);
    paramByteBuffer.get(paddingBuffer, j, i);
  }
  
  public boolean configure(int paramInt1, int paramInt2, int paramInt3)
    throws AudioProcessor.UnhandledFormatException
  {
    if (paramInt3 == 2)
    {
      if ((sampleRateHz == paramInt1) && (channelCount == paramInt2)) {
        return false;
      }
      sampleRateHz = paramInt1;
      channelCount = paramInt2;
      bytesPerFrame = (paramInt2 * 2);
      return true;
    }
    throw new AudioProcessor.UnhandledFormatException(paramInt1, paramInt2, paramInt3);
  }
  
  public void flush()
  {
    if (isActive())
    {
      int i = durationUsToFrames(150000L) * bytesPerFrame;
      if (maybeSilenceBuffer.length != i) {
        maybeSilenceBuffer = new byte[i];
      }
      paddingSize = (durationUsToFrames(20000L) * bytesPerFrame);
      if (paddingBuffer.length != paddingSize) {
        paddingBuffer = new byte[paddingSize];
      }
    }
    state = 0;
    outputBuffer = AudioProcessor.EMPTY_BUFFER;
    inputEnded = false;
    skippedFrames = 0L;
    maybeSilenceBufferSize = 0;
    hasOutputNoise = false;
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
  
  public long getSkippedFrames()
  {
    return skippedFrames;
  }
  
  public boolean isActive()
  {
    return (sampleRateHz != -1) && (enabled);
  }
  
  public boolean isEnded()
  {
    return (inputEnded) && (outputBuffer == AudioProcessor.EMPTY_BUFFER);
  }
  
  public void queueEndOfStream()
  {
    inputEnded = true;
    if (maybeSilenceBufferSize > 0) {
      output(maybeSilenceBuffer, maybeSilenceBufferSize);
    }
    if (!hasOutputNoise) {
      skippedFrames += paddingSize / bytesPerFrame;
    }
  }
  
  public void queueInput(ByteBuffer paramByteBuffer)
  {
    while ((paramByteBuffer.hasRemaining()) && (!outputBuffer.hasRemaining())) {
      switch (state)
      {
      default: 
        throw new IllegalStateException();
      case 2: 
        processSilence(paramByteBuffer);
        break;
      case 1: 
        processMaybeSilence(paramByteBuffer);
        break;
      case 0: 
        processNoisy(paramByteBuffer);
      }
    }
  }
  
  public void reset()
  {
    enabled = false;
    flush();
    buffer = AudioProcessor.EMPTY_BUFFER;
    channelCount = -1;
    sampleRateHz = -1;
    paddingSize = 0;
    maybeSilenceBuffer = Util.EMPTY_BYTE_ARRAY;
    paddingBuffer = Util.EMPTY_BYTE_ARRAY;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    enabled = paramBoolean;
    flush();
  }
}
