package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.util.Assertions;
import java.nio.ShortBuffer;
import java.util.Arrays;

final class Sonic
{
  private static final int AMDF_FREQUENCY = 4000;
  private static final int MAXIMUM_PITCH = 400;
  private static final int MINIMUM_PITCH = 65;
  private final int channelCount;
  private final short[] downSampleBuffer;
  private short[] inputBuffer;
  private int inputFrameCount;
  private final int inputSampleRateHz;
  private int maxDiff;
  private final int maxPeriod;
  private final int maxRequiredFrameCount;
  private int minDiff;
  private final int minPeriod;
  private int newRatePosition;
  private int oldRatePosition;
  private short[] outputBuffer;
  private int outputFrameCount;
  private final float pitch;
  private short[] pitchBuffer;
  private int pitchFrameCount;
  private int prevMinDiff;
  private int prevPeriod;
  private final float rate;
  private int remainingInputToCopyFrameCount;
  private final float speed;
  
  public Sonic(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, int paramInt3)
  {
    inputSampleRateHz = paramInt1;
    channelCount = paramInt2;
    speed = paramFloat1;
    pitch = paramFloat2;
    rate = (paramInt1 / paramInt3);
    minPeriod = (paramInt1 / 400);
    maxPeriod = (paramInt1 / 65);
    maxRequiredFrameCount = (maxPeriod * 2);
    downSampleBuffer = new short[maxRequiredFrameCount];
    inputBuffer = new short[maxRequiredFrameCount * paramInt2];
    outputBuffer = new short[maxRequiredFrameCount * paramInt2];
    pitchBuffer = new short[maxRequiredFrameCount * paramInt2];
  }
  
  private void adjustRate(float paramFloat, int paramInt)
  {
    if (outputFrameCount == paramInt) {
      return;
    }
    int j = (int)(inputSampleRateHz / paramFloat);
    int i = inputSampleRateHz;
    for (;;)
    {
      if ((j <= 16384) && (i <= 16384))
      {
        moveNewSamplesToPitchBuffer(paramInt);
        paramInt = 0;
        for (;;)
        {
          int k = pitchFrameCount;
          boolean bool = true;
          if (paramInt >= k - 1) {
            break;
          }
          while ((oldRatePosition + 1) * j > newRatePosition * i)
          {
            outputBuffer = ensureSpaceForAdditionalFrames(outputBuffer, outputFrameCount, 1);
            k = 0;
            while (k < channelCount)
            {
              outputBuffer[(outputFrameCount * channelCount + k)] = interpolate(pitchBuffer, channelCount * paramInt + k, i, j);
              k += 1;
            }
            newRatePosition += 1;
            outputFrameCount += 1;
          }
          oldRatePosition += 1;
          if (oldRatePosition == i)
          {
            oldRatePosition = 0;
            if (newRatePosition != j) {
              bool = false;
            }
            Assertions.checkState(bool);
            newRatePosition = 0;
          }
          paramInt += 1;
        }
        removePitchFrames(pitchFrameCount - 1);
        return;
      }
      j /= 2;
      i /= 2;
    }
  }
  
  private void changeSpeed(float paramFloat)
  {
    if (inputFrameCount < maxRequiredFrameCount) {
      return;
    }
    int k = inputFrameCount;
    int j = 0;
    int i;
    do
    {
      if (remainingInputToCopyFrameCount > 0)
      {
        i = j + copyInputToOutput(j);
      }
      else
      {
        i = findPitchPeriod(inputBuffer, j);
        if (paramFloat > 1.0D) {
          i = j + (i + skipPitchPeriod(inputBuffer, j, paramFloat, i));
        } else {
          i = j + insertPitchPeriod(inputBuffer, j, paramFloat, i);
        }
      }
      j = i;
    } while (maxRequiredFrameCount + i <= k);
    removeProcessedInputFrames(i);
  }
  
  private int copyInputToOutput(int paramInt)
  {
    int i = Math.min(maxRequiredFrameCount, remainingInputToCopyFrameCount);
    copyToOutput(inputBuffer, paramInt, i);
    remainingInputToCopyFrameCount -= i;
    return i;
  }
  
  private void copyToOutput(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    outputBuffer = ensureSpaceForAdditionalFrames(outputBuffer, outputFrameCount, paramInt2);
    System.arraycopy(paramArrayOfShort, paramInt1 * channelCount, outputBuffer, outputFrameCount * channelCount, channelCount * paramInt2);
    outputFrameCount += paramInt2;
  }
  
  private void downSampleInput(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    int k = maxRequiredFrameCount / paramInt2;
    int m = channelCount * paramInt2;
    int n = channelCount;
    paramInt2 = 0;
    while (paramInt2 < k)
    {
      int i = 0;
      int j = 0;
      while (i < m)
      {
        j += paramArrayOfShort[(paramInt2 * m + paramInt1 * n + i)];
        i += 1;
      }
      i = j / m;
      downSampleBuffer[paramInt2] = ((short)i);
      paramInt2 += 1;
    }
  }
  
  private short[] ensureSpaceForAdditionalFrames(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    int i = paramArrayOfShort.length / channelCount;
    if (paramInt1 + paramInt2 <= i) {
      return paramArrayOfShort;
    }
    return Arrays.copyOf(paramArrayOfShort, (i * 3 / 2 + paramInt2) * channelCount);
  }
  
  private int findPitchPeriod(short[] paramArrayOfShort, int paramInt)
  {
    int i;
    if (inputSampleRateHz > 4000) {
      i = inputSampleRateHz / 4000;
    } else {
      i = 1;
    }
    if ((channelCount == 1) && (i == 1))
    {
      paramInt = findPitchPeriodInRange(paramArrayOfShort, paramInt, minPeriod, maxPeriod);
    }
    else
    {
      downSampleInput(paramArrayOfShort, paramInt, i);
      int j = findPitchPeriodInRange(downSampleBuffer, 0, minPeriod / i, maxPeriod / i);
      if (i != 1)
      {
        int k = j * i;
        i *= 4;
        j = k - i;
        k += i;
        i = j;
        if (j < minPeriod) {
          i = minPeriod;
        }
        j = k;
        if (k > maxPeriod) {
          j = maxPeriod;
        }
        if (channelCount == 1)
        {
          paramInt = findPitchPeriodInRange(paramArrayOfShort, paramInt, i, j);
        }
        else
        {
          downSampleInput(paramArrayOfShort, paramInt, 1);
          paramInt = findPitchPeriodInRange(downSampleBuffer, 0, i, j);
        }
      }
      else
      {
        paramInt = j;
      }
    }
    if (previousPeriodBetter(minDiff, maxDiff)) {
      i = prevPeriod;
    } else {
      i = paramInt;
    }
    prevMinDiff = minDiff;
    prevPeriod = paramInt;
    return i;
  }
  
  private int findPitchPeriodInRange(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3)
  {
    int i2 = paramInt1 * channelCount;
    int m = 1;
    int k = 0;
    int j = 0;
    int i = 255;
    while (paramInt2 <= paramInt3)
    {
      int n = 0;
      paramInt1 = 0;
      while (n < paramInt2)
      {
        paramInt1 += Math.abs(paramArrayOfShort[(i2 + n)] - paramArrayOfShort[(i2 + paramInt2 + n)]);
        n += 1;
      }
      int i1 = m;
      n = k;
      if (paramInt1 * k < m * paramInt2)
      {
        n = paramInt2;
        i1 = paramInt1;
      }
      m = j;
      k = i;
      if (paramInt1 * i > j * paramInt2)
      {
        k = paramInt2;
        m = paramInt1;
      }
      paramInt2 += 1;
      j = m;
      m = i1;
      i = k;
      k = n;
    }
    minDiff = (m / k);
    maxDiff = (j / i);
    return k;
  }
  
  private int insertPitchPeriod(short[] paramArrayOfShort, int paramInt1, float paramFloat, int paramInt2)
  {
    int i;
    if (paramFloat < 0.5F)
    {
      i = (int)(paramInt2 * paramFloat / (1.0F - paramFloat));
    }
    else
    {
      remainingInputToCopyFrameCount = ((int)(paramInt2 * (2.0F * paramFloat - 1.0F) / (1.0F - paramFloat)));
      i = paramInt2;
    }
    short[] arrayOfShort = outputBuffer;
    int j = outputFrameCount;
    int k = paramInt2 + i;
    outputBuffer = ensureSpaceForAdditionalFrames(arrayOfShort, j, k);
    System.arraycopy(paramArrayOfShort, channelCount * paramInt1, outputBuffer, outputFrameCount * channelCount, channelCount * paramInt2);
    overlapAdd(i, channelCount, outputBuffer, outputFrameCount + paramInt2, paramArrayOfShort, paramInt1 + paramInt2, paramArrayOfShort, paramInt1);
    outputFrameCount += k;
    return i;
  }
  
  private short interpolate(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramArrayOfShort[paramInt1];
    paramInt1 = paramArrayOfShort[(paramInt1 + channelCount)];
    int m = newRatePosition;
    int j = oldRatePosition;
    int k = (oldRatePosition + 1) * paramInt3;
    paramInt2 = k - m * paramInt2;
    paramInt3 = k - j * paramInt3;
    return (short)((i * paramInt2 + (paramInt3 - paramInt2) * paramInt1) / paramInt3);
  }
  
  private void moveNewSamplesToPitchBuffer(int paramInt)
  {
    int i = outputFrameCount - paramInt;
    pitchBuffer = ensureSpaceForAdditionalFrames(pitchBuffer, pitchFrameCount, i);
    System.arraycopy(outputBuffer, channelCount * paramInt, pitchBuffer, pitchFrameCount * channelCount, channelCount * i);
    outputFrameCount = paramInt;
    pitchFrameCount += i;
  }
  
  private static void overlapAdd(int paramInt1, int paramInt2, short[] paramArrayOfShort1, int paramInt3, short[] paramArrayOfShort2, int paramInt4, short[] paramArrayOfShort3, int paramInt5)
  {
    int i = 0;
    while (i < paramInt2)
    {
      int n = paramInt3 * paramInt2 + i;
      int k = paramInt5 * paramInt2 + i;
      int m = paramInt4 * paramInt2 + i;
      int j = 0;
      while (j < paramInt1)
      {
        paramArrayOfShort1[n] = ((short)((paramArrayOfShort2[m] * (paramInt1 - j) + paramArrayOfShort3[k] * j) / paramInt1));
        n += paramInt2;
        m += paramInt2;
        k += paramInt2;
        j += 1;
      }
      i += 1;
    }
  }
  
  private boolean previousPeriodBetter(int paramInt1, int paramInt2)
  {
    if (paramInt1 != 0)
    {
      if (prevPeriod == 0) {
        return false;
      }
      if (paramInt2 > paramInt1 * 3) {
        return false;
      }
      return paramInt1 * 2 > prevMinDiff * 3;
    }
    return false;
  }
  
  private void processStreamInput()
  {
    int i = outputFrameCount;
    float f1 = speed / pitch;
    float f2 = rate * pitch;
    double d = f1;
    if ((d <= 1.00001D) && (d >= 0.99999D))
    {
      copyToOutput(inputBuffer, 0, inputFrameCount);
      inputFrameCount = 0;
    }
    else
    {
      changeSpeed(f1);
    }
    if (f2 != 1.0F) {
      adjustRate(f2, i);
    }
  }
  
  private void removePitchFrames(int paramInt)
  {
    if (paramInt == 0) {
      return;
    }
    System.arraycopy(pitchBuffer, channelCount * paramInt, pitchBuffer, 0, (pitchFrameCount - paramInt) * channelCount);
    pitchFrameCount -= paramInt;
  }
  
  private void removeProcessedInputFrames(int paramInt)
  {
    int i = inputFrameCount - paramInt;
    System.arraycopy(inputBuffer, paramInt * channelCount, inputBuffer, 0, channelCount * i);
    inputFrameCount = i;
  }
  
  private int skipPitchPeriod(short[] paramArrayOfShort, int paramInt1, float paramFloat, int paramInt2)
  {
    int i;
    if (paramFloat >= 2.0F)
    {
      i = (int)(paramInt2 / (paramFloat - 1.0F));
    }
    else
    {
      remainingInputToCopyFrameCount = ((int)(paramInt2 * (2.0F - paramFloat) / (paramFloat - 1.0F)));
      i = paramInt2;
    }
    outputBuffer = ensureSpaceForAdditionalFrames(outputBuffer, outputFrameCount, i);
    overlapAdd(i, channelCount, outputBuffer, outputFrameCount, paramArrayOfShort, paramInt1, paramArrayOfShort, paramInt1 + paramInt2);
    outputFrameCount += i;
    return i;
  }
  
  public void flush()
  {
    inputFrameCount = 0;
    outputFrameCount = 0;
    pitchFrameCount = 0;
    oldRatePosition = 0;
    newRatePosition = 0;
    remainingInputToCopyFrameCount = 0;
    prevPeriod = 0;
    prevMinDiff = 0;
    minDiff = 0;
    maxDiff = 0;
  }
  
  public int getFramesAvailable()
  {
    return outputFrameCount;
  }
  
  public void getOutput(ShortBuffer paramShortBuffer)
  {
    int i = Math.min(paramShortBuffer.remaining() / channelCount, outputFrameCount);
    paramShortBuffer.put(outputBuffer, 0, channelCount * i);
    outputFrameCount -= i;
    System.arraycopy(outputBuffer, i * channelCount, outputBuffer, 0, outputFrameCount * channelCount);
  }
  
  public void queueEndOfStream()
  {
    int j = inputFrameCount;
    float f1 = speed / pitch;
    float f2 = rate;
    float f3 = pitch;
    int k = outputFrameCount + (int)((j / f1 + pitchFrameCount) / (f2 * f3) + 0.5F);
    inputBuffer = ensureSpaceForAdditionalFrames(inputBuffer, inputFrameCount, maxRequiredFrameCount * 2 + j);
    int i = 0;
    while (i < maxRequiredFrameCount * 2 * channelCount)
    {
      inputBuffer[(channelCount * j + i)] = 0;
      i += 1;
    }
    inputFrameCount += maxRequiredFrameCount * 2;
    processStreamInput();
    if (outputFrameCount > k) {
      outputFrameCount = k;
    }
    inputFrameCount = 0;
    remainingInputToCopyFrameCount = 0;
    pitchFrameCount = 0;
  }
  
  public void queueInput(ShortBuffer paramShortBuffer)
  {
    int i = paramShortBuffer.remaining() / channelCount;
    int j = channelCount;
    inputBuffer = ensureSpaceForAdditionalFrames(inputBuffer, inputFrameCount, i);
    paramShortBuffer.get(inputBuffer, inputFrameCount * channelCount, j * i * 2 / 2);
    inputFrameCount += i;
    processStreamInput();
  }
}
