package com.airbnb.lottie.value;

import android.graphics.PointF;
import android.view.animation.Interpolator;
import androidx.annotation.Nullable;
import com.airbnb.lottie.LottieComposition;

public class Keyframe<T>
{
  private static final float UNSET_FLOAT = -3987645.8F;
  private static final int UNSET_INT = 784923401;
  @Nullable
  private final LottieComposition composition;
  @Nullable
  public Float endFrame;
  private float endProgress = Float.MIN_VALUE;
  @Nullable
  public T endValue;
  private float endValueFloat = -3987645.8F;
  private int endValueInt = 784923401;
  @Nullable
  public final Interpolator interpolator;
  public PointF pathCp1 = null;
  public PointF pathCp2 = null;
  public final float startFrame;
  private float startProgress = Float.MIN_VALUE;
  @Nullable
  public final T startValue;
  private float startValueFloat = -3987645.8F;
  private int startValueInt = 784923401;
  
  public Keyframe(LottieComposition paramLottieComposition, Object paramObject1, Object paramObject2, Interpolator paramInterpolator, float paramFloat, Float paramFloat1)
  {
    composition = paramLottieComposition;
    startValue = paramObject1;
    endValue = paramObject2;
    interpolator = paramInterpolator;
    startFrame = paramFloat;
    endFrame = paramFloat1;
  }
  
  public Keyframe(Object paramObject)
  {
    composition = null;
    startValue = paramObject;
    endValue = paramObject;
    interpolator = null;
    startFrame = Float.MIN_VALUE;
    endFrame = Float.valueOf(Float.MAX_VALUE);
  }
  
  public boolean containsProgress(float paramFloat)
  {
    return (paramFloat >= getStartProgress()) && (paramFloat < getEndProgress());
  }
  
  public float getEndProgress()
  {
    if (composition == null) {
      return 1.0F;
    }
    if (endProgress == Float.MIN_VALUE) {
      if (endFrame == null) {
        endProgress = 1.0F;
      } else {
        endProgress = (getStartProgress() + (endFrame.floatValue() - startFrame) / composition.getDurationFrames());
      }
    }
    return endProgress;
  }
  
  public float getEndValueFloat()
  {
    if (endValueFloat == -3987645.8F) {
      endValueFloat = ((Float)endValue).floatValue();
    }
    return endValueFloat;
  }
  
  public int getEndValueInt()
  {
    if (endValueInt == 784923401) {
      endValueInt = ((Integer)endValue).intValue();
    }
    return endValueInt;
  }
  
  public float getStartProgress()
  {
    if (composition == null) {
      return 0.0F;
    }
    if (startProgress == Float.MIN_VALUE) {
      startProgress = ((startFrame - composition.getStartFrame()) / composition.getDurationFrames());
    }
    return startProgress;
  }
  
  public float getStartValueFloat()
  {
    if (startValueFloat == -3987645.8F) {
      startValueFloat = ((Float)startValue).floatValue();
    }
    return startValueFloat;
  }
  
  public int getStartValueInt()
  {
    if (startValueInt == 784923401) {
      startValueInt = ((Integer)startValue).intValue();
    }
    return startValueInt;
  }
  
  public boolean isStatic()
  {
    return interpolator == null;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Keyframe{startValue=");
    localStringBuilder.append(startValue);
    localStringBuilder.append(", endValue=");
    localStringBuilder.append(endValue);
    localStringBuilder.append(", startFrame=");
    localStringBuilder.append(startFrame);
    localStringBuilder.append(", endFrame=");
    localStringBuilder.append(endFrame);
    localStringBuilder.append(", interpolator=");
    localStringBuilder.append(interpolator);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}
