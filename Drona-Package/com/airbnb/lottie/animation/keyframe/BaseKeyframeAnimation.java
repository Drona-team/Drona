package com.airbnb.lottie.animation.keyframe;

import android.animation.TimeInterpolator;
import androidx.annotation.Nullable;
import com.airbnb.lottie.value.Keyframe;
import com.airbnb.lottie.value.LottieValueCallback;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseKeyframeAnimation<K, A>
{
  private float cachedEndProgress = -1.0F;
  @Nullable
  private A cachedGetValue = null;
  @Nullable
  private Keyframe<K> cachedGetValueKeyframe;
  private float cachedGetValueProgress = -1.0F;
  @Nullable
  private Keyframe<K> cachedKeyframe;
  private float cachedStartDelayProgress = -1.0F;
  private boolean isDiscrete = false;
  private final List<? extends Keyframe<K>> keyframes;
  final List<AnimationListener> listeners = new ArrayList(1);
  private float progress = 0.0F;
  @Nullable
  protected LottieValueCallback<A> valueCallback;
  
  BaseKeyframeAnimation(List paramList)
  {
    keyframes = paramList;
  }
  
  private float getStartDelayProgress()
  {
    if (cachedStartDelayProgress == -1.0F)
    {
      float f;
      if (keyframes.isEmpty()) {
        f = 0.0F;
      } else {
        f = ((Keyframe)keyframes.get(0)).getStartProgress();
      }
      cachedStartDelayProgress = f;
    }
    return cachedStartDelayProgress;
  }
  
  public void addUpdateListener(AnimationListener paramAnimationListener)
  {
    listeners.add(paramAnimationListener);
  }
  
  protected Keyframe getCurrentKeyframe()
  {
    if ((cachedKeyframe != null) && (cachedKeyframe.containsProgress(progress))) {
      return cachedKeyframe;
    }
    Keyframe localKeyframe2 = (Keyframe)keyframes.get(keyframes.size() - 1);
    Keyframe localKeyframe1 = localKeyframe2;
    if (progress < localKeyframe2.getStartProgress())
    {
      int i = keyframes.size() - 1;
      localKeyframe1 = localKeyframe2;
      while (i >= 0)
      {
        localKeyframe1 = (Keyframe)keyframes.get(i);
        if (localKeyframe1.containsProgress(progress)) {
          break;
        }
        i -= 1;
      }
    }
    cachedKeyframe = localKeyframe1;
    return localKeyframe1;
  }
  
  float getEndProgress()
  {
    if (cachedEndProgress == -1.0F)
    {
      float f;
      if (keyframes.isEmpty()) {
        f = 1.0F;
      } else {
        f = ((Keyframe)keyframes.get(keyframes.size() - 1)).getEndProgress();
      }
      cachedEndProgress = f;
    }
    return cachedEndProgress;
  }
  
  protected float getInterpolatedCurrentKeyframeProgress()
  {
    Keyframe localKeyframe = getCurrentKeyframe();
    if (localKeyframe.isStatic()) {
      return 0.0F;
    }
    return interpolator.getInterpolation(getLinearCurrentKeyframeProgress());
  }
  
  float getLinearCurrentKeyframeProgress()
  {
    if (isDiscrete) {
      return 0.0F;
    }
    Keyframe localKeyframe = getCurrentKeyframe();
    if (localKeyframe.isStatic()) {
      return 0.0F;
    }
    return (progress - localKeyframe.getStartProgress()) / (localKeyframe.getEndProgress() - localKeyframe.getStartProgress());
  }
  
  public float getProgress()
  {
    return progress;
  }
  
  public Object getValue()
  {
    Object localObject = getCurrentKeyframe();
    float f = getInterpolatedCurrentKeyframeProgress();
    if ((valueCallback == null) && (localObject == cachedGetValueKeyframe) && (cachedGetValueProgress == f)) {
      return cachedGetValue;
    }
    cachedGetValueKeyframe = ((Keyframe)localObject);
    cachedGetValueProgress = f;
    localObject = getValue((Keyframe)localObject, f);
    cachedGetValue = localObject;
    return localObject;
  }
  
  abstract Object getValue(Keyframe paramKeyframe, float paramFloat);
  
  public void notifyListeners()
  {
    int i = 0;
    while (i < listeners.size())
    {
      ((AnimationListener)listeners.get(i)).onValueChanged();
      i += 1;
    }
  }
  
  public void setIsDiscrete()
  {
    isDiscrete = true;
  }
  
  public void setProgress(float paramFloat)
  {
    if (keyframes.isEmpty()) {
      return;
    }
    Keyframe localKeyframe1 = getCurrentKeyframe();
    float f;
    if (paramFloat < getStartDelayProgress())
    {
      f = getStartDelayProgress();
    }
    else
    {
      f = paramFloat;
      if (paramFloat > getEndProgress()) {
        f = getEndProgress();
      }
    }
    if (f == progress) {
      return;
    }
    progress = f;
    Keyframe localKeyframe2 = getCurrentKeyframe();
    if ((localKeyframe1 != localKeyframe2) || (!localKeyframe2.isStatic())) {
      notifyListeners();
    }
  }
  
  public void setValueCallback(LottieValueCallback paramLottieValueCallback)
  {
    if (valueCallback != null) {
      valueCallback.setAnimation(null);
    }
    valueCallback = paramLottieValueCallback;
    if (paramLottieValueCallback != null) {
      paramLottieValueCallback.setAnimation(this);
    }
  }
  
  public static abstract interface AnimationListener
  {
    public abstract void onValueChanged();
  }
}
