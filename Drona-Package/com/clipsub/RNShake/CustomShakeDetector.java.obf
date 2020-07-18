package com.clipsub.RNShake;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.facebook.infer.annotation.Assertions;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

public class CustomShakeDetector
  implements SensorEventListener
{
  private static final int MAGNITUDE_THRESHOLD = 25;
  private static final int MAX_SAMPLES = 40;
  private static final long MIN_TIME_BETWEEN_SAMPLES_NS = TimeUnit.NANOSECONDS.convert(20L, TimeUnit.MILLISECONDS);
  private static final int PERCENT_OVER_THRESHOLD_FOR_SHAKE = 60;
  private static final float SHAKING_WINDOW_NS = (float)TimeUnit.NANOSECONDS.convert(3L, TimeUnit.SECONDS);
  private static final long VISIBLE_TIME_RANGE_NS = TimeUnit.NANOSECONDS.convert(250L, TimeUnit.MILLISECONDS);
  private int mCurrentIndex;
  private long mLastShakeTimestamp;
  private long mLastTimestamp;
  @Nullable
  private double[] mMagnitudes;
  private int mMinNumShakes;
  private int mNumShakes;
  @Nullable
  private SensorManager mSensorManager;
  private final ShakeListener mShakeListener;
  @Nullable
  private long[] mTimestamps;
  
  public CustomShakeDetector(ShakeListener paramShakeListener)
  {
    this(paramShakeListener, 1);
  }
  
  public CustomShakeDetector(ShakeListener paramShakeListener, int paramInt)
  {
    mShakeListener = paramShakeListener;
    mMinNumShakes = paramInt;
  }
  
  private void maybeDispatchShake(long paramLong)
  {
    Assertions.assertNotNull(mTimestamps);
    Assertions.assertNotNull(mMagnitudes);
    int j = 0;
    int k = 0;
    int i;
    for (int m = 0; j < 40; m = i)
    {
      int i1 = (mCurrentIndex - j + 40) % 40;
      int n = k;
      i = m;
      if (paramLong - mTimestamps[i1] < VISIBLE_TIME_RANGE_NS)
      {
        m += 1;
        n = k;
        i = m;
        if (mMagnitudes[i1] >= 25.0D)
        {
          n = k + 1;
          i = m;
        }
      }
      j += 1;
      k = n;
    }
    if (k / m > 0.6D)
    {
      if (paramLong - mLastShakeTimestamp >= VISIBLE_TIME_RANGE_NS) {
        mNumShakes += 1;
      }
      mLastShakeTimestamp = paramLong;
      if (mNumShakes >= mMinNumShakes)
      {
        mNumShakes = 0;
        mLastShakeTimestamp = 0L;
        mShakeListener.onShake();
      }
    }
    if ((float)(paramLong - mLastShakeTimestamp) > SHAKING_WINDOW_NS)
    {
      mNumShakes = 0;
      mLastShakeTimestamp = 0L;
    }
  }
  
  public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
  
  public void onSensorChanged(SensorEvent paramSensorEvent)
  {
    if (timestamp - mLastTimestamp < MIN_TIME_BETWEEN_SAMPLES_NS) {
      return;
    }
    Assertions.assertNotNull(mTimestamps);
    Assertions.assertNotNull(mMagnitudes);
    float f1 = values[0];
    float f2 = values[1];
    float f3 = values[2];
    mLastTimestamp = timestamp;
    mTimestamps[mCurrentIndex] = timestamp;
    mMagnitudes[mCurrentIndex] = Math.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
    maybeDispatchShake(timestamp);
    mCurrentIndex = ((mCurrentIndex + 1) % 40);
  }
  
  public void start(SensorManager paramSensorManager)
  {
    Assertions.assertNotNull(paramSensorManager);
    Sensor localSensor = paramSensorManager.getDefaultSensor(1);
    if (localSensor != null)
    {
      mSensorManager = paramSensorManager;
      mLastTimestamp = -1L;
      mCurrentIndex = 0;
      mMagnitudes = new double[40];
      mTimestamps = new long[40];
      mSensorManager.registerListener(this, localSensor, 2);
      mNumShakes = 0;
      mLastShakeTimestamp = 0L;
    }
  }
  
  public void stop()
  {
    if (mSensorManager != null)
    {
      mSensorManager.unregisterListener(this);
      mSensorManager = null;
    }
  }
  
  public static abstract interface ShakeListener
  {
    public abstract void onShake();
  }
}
