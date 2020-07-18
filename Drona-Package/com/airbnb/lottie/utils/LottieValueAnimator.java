package com.airbnb.lottie.utils;

import android.animation.ValueAnimator;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.airbnb.lottie.LottieComposition;

public class LottieValueAnimator
  extends BaseLottieAnimator
  implements Choreographer.FrameCallback
{
  @Nullable
  private LottieComposition composition;
  private float frame = 0.0F;
  private long lastFrameTimeNs = 0L;
  private float maxFrame = 2.14748365E9F;
  private float minFrame = -2.14748365E9F;
  private int repeatCount = 0;
  @VisibleForTesting
  protected boolean running = false;
  private float speed = 1.0F;
  private boolean speedReversedForRepeatMode = false;
  
  public LottieValueAnimator() {}
  
  private float getFrameDurationNs()
  {
    if (composition == null) {
      return Float.MAX_VALUE;
    }
    return 1.0E9F / composition.getFrameRate() / Math.abs(speed);
  }
  
  private boolean isReversed()
  {
    return getSpeed() < 0.0F;
  }
  
  private void verifyFrame()
  {
    if (composition == null) {
      return;
    }
    if ((frame >= minFrame) && (frame <= maxFrame)) {
      return;
    }
    throw new IllegalStateException(String.format("Frame must be [%f,%f]. It is %f", new Object[] { Float.valueOf(minFrame), Float.valueOf(maxFrame), Float.valueOf(frame) }));
  }
  
  public void cancel()
  {
    notifyCancel();
    removeFrameCallback();
  }
  
  public void clearComposition()
  {
    composition = null;
    minFrame = -2.14748365E9F;
    maxFrame = 2.14748365E9F;
  }
  
  public void doFrame(long paramLong)
  {
    postFrameCallback();
    if (composition != null)
    {
      if (!isRunning()) {
        return;
      }
      long l2 = lastFrameTimeNs;
      long l1 = 0L;
      if (l2 != 0L) {
        l1 = paramLong - lastFrameTimeNs;
      }
      float f1 = getFrameDurationNs();
      float f2 = (float)l1 / f1;
      float f3 = frame;
      f1 = f2;
      if (isReversed()) {
        f1 = -f2;
      }
      frame = (f3 + f1);
      boolean bool = MiscUtils.contains(frame, getMinFrame(), getMaxFrame());
      frame = MiscUtils.clamp(frame, getMinFrame(), getMaxFrame());
      lastFrameTimeNs = paramLong;
      notifyUpdate();
      if ((bool ^ true)) {
        if ((getRepeatCount() != -1) && (repeatCount >= getRepeatCount()))
        {
          if (speed < 0.0F) {
            f1 = getMinFrame();
          } else {
            f1 = getMaxFrame();
          }
          frame = f1;
          removeFrameCallback();
          notifyEnd(isReversed());
        }
        else
        {
          notifyRepeat();
          repeatCount += 1;
          if (getRepeatMode() == 2)
          {
            speedReversedForRepeatMode ^= true;
            reverseAnimationSpeed();
          }
          else
          {
            if (isReversed()) {
              f1 = getMaxFrame();
            } else {
              f1 = getMinFrame();
            }
            frame = f1;
          }
          lastFrameTimeNs = paramLong;
        }
      }
      verifyFrame();
    }
  }
  
  public void endAnimation()
  {
    removeFrameCallback();
    notifyEnd(isReversed());
  }
  
  public float getAnimatedFraction()
  {
    if (composition == null) {
      return 0.0F;
    }
    if (isReversed()) {
      return (getMaxFrame() - frame) / (getMaxFrame() - getMinFrame());
    }
    return (frame - getMinFrame()) / (getMaxFrame() - getMinFrame());
  }
  
  public Object getAnimatedValue()
  {
    return Float.valueOf(getAnimatedValueAbsolute());
  }
  
  public float getAnimatedValueAbsolute()
  {
    if (composition == null) {
      return 0.0F;
    }
    return (frame - composition.getStartFrame()) / (composition.getEndFrame() - composition.getStartFrame());
  }
  
  public long getDuration()
  {
    if (composition == null) {
      return 0L;
    }
    return composition.getDuration();
  }
  
  public float getFrame()
  {
    return frame;
  }
  
  public float getMaxFrame()
  {
    if (composition == null) {
      return 0.0F;
    }
    if (maxFrame == 2.14748365E9F) {
      return composition.getEndFrame();
    }
    return maxFrame;
  }
  
  public float getMinFrame()
  {
    if (composition == null) {
      return 0.0F;
    }
    if (minFrame == -2.14748365E9F) {
      return composition.getStartFrame();
    }
    return minFrame;
  }
  
  public float getSpeed()
  {
    return speed;
  }
  
  public boolean isRunning()
  {
    return running;
  }
  
  public void pauseAnimation()
  {
    removeFrameCallback();
  }
  
  public void playAnimation()
  {
    running = true;
    notifyStart(isReversed());
    float f;
    if (isReversed()) {
      f = getMaxFrame();
    } else {
      f = getMinFrame();
    }
    setFrame((int)f);
    lastFrameTimeNs = 0L;
    repeatCount = 0;
    postFrameCallback();
  }
  
  protected void postFrameCallback()
  {
    if (isRunning())
    {
      removeFrameCallback(false);
      Choreographer.getInstance().postFrameCallback(this);
    }
  }
  
  protected void removeFrameCallback()
  {
    removeFrameCallback(true);
  }
  
  protected void removeFrameCallback(boolean paramBoolean)
  {
    Choreographer.getInstance().removeFrameCallback(this);
    if (paramBoolean) {
      running = false;
    }
  }
  
  public void resumeAnimation()
  {
    running = true;
    postFrameCallback();
    lastFrameTimeNs = 0L;
    if ((isReversed()) && (getFrame() == getMinFrame()))
    {
      frame = getMaxFrame();
      return;
    }
    if ((!isReversed()) && (getFrame() == getMaxFrame())) {
      frame = getMinFrame();
    }
  }
  
  public void reverseAnimationSpeed()
  {
    setSpeed(-getSpeed());
  }
  
  public void setComposition(LottieComposition paramLottieComposition)
  {
    int i;
    if (composition == null) {
      i = 1;
    } else {
      i = 0;
    }
    composition = paramLottieComposition;
    if (i != 0) {
      setMinAndMaxFrames((int)Math.max(minFrame, paramLottieComposition.getStartFrame()), (int)Math.min(maxFrame, paramLottieComposition.getEndFrame()));
    } else {
      setMinAndMaxFrames((int)paramLottieComposition.getStartFrame(), (int)paramLottieComposition.getEndFrame());
    }
    float f = frame;
    frame = 0.0F;
    setFrame((int)f);
  }
  
  public void setFrame(float paramFloat)
  {
    if (frame == paramFloat) {
      return;
    }
    frame = MiscUtils.clamp(paramFloat, getMinFrame(), getMaxFrame());
    lastFrameTimeNs = 0L;
    notifyUpdate();
  }
  
  public void setMaxFrame(float paramFloat)
  {
    setMinAndMaxFrames(minFrame, paramFloat);
  }
  
  public void setMinAndMaxFrames(float paramFloat1, float paramFloat2)
  {
    if (paramFloat1 <= paramFloat2)
    {
      float f1;
      if (composition == null) {
        f1 = -3.4028235E38F;
      } else {
        f1 = composition.getStartFrame();
      }
      float f2;
      if (composition == null) {
        f2 = Float.MAX_VALUE;
      } else {
        f2 = composition.getEndFrame();
      }
      minFrame = MiscUtils.clamp(paramFloat1, f1, f2);
      maxFrame = MiscUtils.clamp(paramFloat2, f1, f2);
      setFrame((int)MiscUtils.clamp(frame, paramFloat1, paramFloat2));
      return;
    }
    throw new IllegalArgumentException(String.format("minFrame (%s) must be <= maxFrame (%s)", new Object[] { Float.valueOf(paramFloat1), Float.valueOf(paramFloat2) }));
  }
  
  public void setMinFrame(int paramInt)
  {
    setMinAndMaxFrames(paramInt, (int)maxFrame);
  }
  
  public void setRepeatMode(int paramInt)
  {
    super.setRepeatMode(paramInt);
    if ((paramInt != 2) && (speedReversedForRepeatMode))
    {
      speedReversedForRepeatMode = false;
      reverseAnimationSpeed();
    }
  }
  
  public void setSpeed(float paramFloat)
  {
    speed = paramFloat;
  }
}
