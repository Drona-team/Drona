package com.airbnb.lottie.animation.keyframe;

import com.airbnb.lottie.value.Keyframe;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.LottieValueCallback;
import java.util.Collections;

public class ValueCallbackKeyframeAnimation<K, A>
  extends BaseKeyframeAnimation<K, A>
{
  private final LottieFrameInfo<A> frameInfo = new LottieFrameInfo();
  private final A valueCallbackValue;
  
  public ValueCallbackKeyframeAnimation(LottieValueCallback paramLottieValueCallback)
  {
    this(paramLottieValueCallback, null);
  }
  
  public ValueCallbackKeyframeAnimation(LottieValueCallback paramLottieValueCallback, Object paramObject)
  {
    super(Collections.emptyList());
    setValueCallback(paramLottieValueCallback);
    valueCallbackValue = paramObject;
  }
  
  float getEndProgress()
  {
    return 1.0F;
  }
  
  public Object getValue()
  {
    return valueCallback.getValueInternal(0.0F, 0.0F, valueCallbackValue, valueCallbackValue, getProgress(), getProgress(), getProgress());
  }
  
  Object getValue(Keyframe paramKeyframe, float paramFloat)
  {
    return getValue();
  }
  
  public void notifyListeners()
  {
    if (valueCallback != null) {
      super.notifyListeners();
    }
  }
}
