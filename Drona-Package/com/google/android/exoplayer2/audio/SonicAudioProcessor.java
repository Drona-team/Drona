package com.google.android.exoplayer2.audio;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public final class SonicAudioProcessor
  implements AudioProcessor
{
  private static final float CLOSE_THRESHOLD = 0.01F;
  public static final float MAXIMUM_PITCH = 8.0F;
  public static final float MAXIMUM_SPEED = 8.0F;
  public static final float MINIMUM_PITCH = 0.1F;
  public static final float MINIMUM_SPEED = 0.1F;
  private static final int MIN_BYTES_FOR_SPEEDUP_CALCULATION = 1024;
  public static final int SAMPLE_RATE_NO_CHANGE = -1;
  private ByteBuffer buffer = AudioProcessor.EMPTY_BUFFER;
  private int channelCount = -1;
  private long inputBytes;
  private boolean inputEnded;
  private ByteBuffer outputBuffer = AudioProcessor.EMPTY_BUFFER;
  private long outputBytes;
  private int outputSampleRateHz = -1;
  private int pendingOutputSampleRateHz = -1;
  private float pitch = 1.0F;
  private int sampleRateHz = -1;
  private ShortBuffer shortBuffer = buffer.asShortBuffer();
  @Nullable
  private Sonic sonic;
  private float speed = 1.0F;
  
  public SonicAudioProcessor() {}
  
  public boolean configure(int paramInt1, int paramInt2, int paramInt3)
    throws AudioProcessor.UnhandledFormatException
  {
    if (paramInt3 == 2)
    {
      if (pendingOutputSampleRateHz == -1) {
        paramInt3 = paramInt1;
      } else {
        paramInt3 = pendingOutputSampleRateHz;
      }
      if ((sampleRateHz == paramInt1) && (channelCount == paramInt2) && (outputSampleRateHz == paramInt3)) {
        return false;
      }
      sampleRateHz = paramInt1;
      channelCount = paramInt2;
      outputSampleRateHz = paramInt3;
      sonic = null;
      return true;
    }
    throw new AudioProcessor.UnhandledFormatException(paramInt1, paramInt2, paramInt3);
  }
  
  public void flush()
  {
    if (isActive()) {
      if (sonic == null) {
        sonic = new Sonic(sampleRateHz, channelCount, speed, pitch, outputSampleRateHz);
      } else {
        sonic.flush();
      }
    }
    outputBuffer = AudioProcessor.EMPTY_BUFFER;
    inputBytes = 0L;
    outputBytes = 0L;
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
    return outputSampleRateHz;
  }
  
  public boolean isActive()
  {
    return (sampleRateHz != -1) && ((Math.abs(speed - 1.0F) >= 0.01F) || (Math.abs(pitch - 1.0F) >= 0.01F) || (outputSampleRateHz != sampleRateHz));
  }
  
  public boolean isEnded()
  {
    return (inputEnded) && ((sonic == null) || (sonic.getFramesAvailable() == 0));
  }
  
  public void queueEndOfStream()
  {
    boolean bool;
    if (sonic != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    sonic.queueEndOfStream();
    inputEnded = true;
  }
  
  public void queueInput(ByteBuffer paramByteBuffer)
  {
    boolean bool;
    if (sonic != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    if (paramByteBuffer.hasRemaining())
    {
      ShortBuffer localShortBuffer = paramByteBuffer.asShortBuffer();
      i = paramByteBuffer.remaining();
      inputBytes += i;
      sonic.queueInput(localShortBuffer);
      paramByteBuffer.position(paramByteBuffer.position() + i);
    }
    int i = sonic.getFramesAvailable() * channelCount * 2;
    if (i > 0)
    {
      if (buffer.capacity() < i)
      {
        buffer = ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
        shortBuffer = buffer.asShortBuffer();
      }
      else
      {
        buffer.clear();
        shortBuffer.clear();
      }
      sonic.getOutput(shortBuffer);
      outputBytes += i;
      buffer.limit(i);
      outputBuffer = buffer;
    }
  }
  
  public void reset()
  {
    speed = 1.0F;
    pitch = 1.0F;
    channelCount = -1;
    sampleRateHz = -1;
    outputSampleRateHz = -1;
    buffer = AudioProcessor.EMPTY_BUFFER;
    shortBuffer = buffer.asShortBuffer();
    outputBuffer = AudioProcessor.EMPTY_BUFFER;
    pendingOutputSampleRateHz = -1;
    sonic = null;
    inputBytes = 0L;
    outputBytes = 0L;
    inputEnded = false;
  }
  
  public long scaleDurationForSpeedup(long paramLong)
  {
    if (outputBytes >= 1024L)
    {
      if (outputSampleRateHz == sampleRateHz) {
        return Util.scaleLargeTimestamp(paramLong, inputBytes, outputBytes);
      }
      return Util.scaleLargeTimestamp(paramLong, inputBytes * outputSampleRateHz, outputBytes * sampleRateHz);
    }
    return (speed * paramLong);
  }
  
  public void setOutputSampleRateHz(int paramInt)
  {
    pendingOutputSampleRateHz = paramInt;
  }
  
  public float setPitch(float paramFloat)
  {
    paramFloat = Util.constrainValue(paramFloat, 0.1F, 8.0F);
    if (pitch != paramFloat)
    {
      pitch = paramFloat;
      sonic = null;
    }
    flush();
    return paramFloat;
  }
  
  public float setSpeed(float paramFloat)
  {
    paramFloat = Util.constrainValue(paramFloat, 0.1F, 8.0F);
    if (speed != paramFloat)
    {
      speed = paramFloat;
      sonic = null;
    }
    flush();
    return paramFloat;
  }
}
