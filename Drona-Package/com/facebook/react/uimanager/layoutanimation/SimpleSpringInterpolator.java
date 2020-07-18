package com.facebook.react.uimanager.layoutanimation;

import android.view.animation.Interpolator;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;

class SimpleSpringInterpolator
  implements Interpolator
{
  private static final float FACTOR = 0.5F;
  public static final String PARAM_SPRING_DAMPING = "springDamping";
  private final float mSpringDamping;
  
  public SimpleSpringInterpolator()
  {
    mSpringDamping = 0.5F;
  }
  
  public SimpleSpringInterpolator(float paramFloat)
  {
    mSpringDamping = paramFloat;
  }
  
  public static float getSpringDamping(ReadableMap paramReadableMap)
  {
    if (paramReadableMap.getType("springDamping").equals(ReadableType.Number)) {
      return (float)paramReadableMap.getDouble("springDamping");
    }
    return 0.5F;
  }
  
  public float getInterpolation(float paramFloat)
  {
    return (float)(Math.pow(2.0D, -10.0F * paramFloat) * Math.sin((paramFloat - mSpringDamping / 4.0F) * 3.141592653589793D * 2.0D / mSpringDamping) + 1.0D);
  }
}
