package com.facebook.react.fabric.mounting;

import android.view.View.MeasureSpec;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.yoga.YogaMeasureMode;

public class LayoutMetricsConversions
{
  public LayoutMetricsConversions() {}
  
  public static float getMaxSize(int paramInt)
  {
    int i = View.MeasureSpec.getMode(paramInt);
    paramInt = View.MeasureSpec.getSize(paramInt);
    if (i == 0) {
      return Float.POSITIVE_INFINITY;
    }
    return paramInt;
  }
  
  public static float getMinSize(int paramInt)
  {
    int i = View.MeasureSpec.getMode(paramInt);
    paramInt = View.MeasureSpec.getSize(paramInt);
    if (i == 1073741824) {
      return paramInt;
    }
    return 0.0F;
  }
  
  public static YogaMeasureMode getYogaMeasureMode(float paramFloat1, float paramFloat2)
  {
    if (paramFloat1 == paramFloat2) {
      return YogaMeasureMode.EXACTLY;
    }
    if (Float.isInfinite(paramFloat2)) {
      return YogaMeasureMode.UNDEFINED;
    }
    return YogaMeasureMode.AT_MOST;
  }
  
  public static float getYogaSize(float paramFloat1, float paramFloat2)
  {
    if (paramFloat1 == paramFloat2) {
      return PixelUtil.toPixelFromDIP(paramFloat2);
    }
    if (Float.isInfinite(paramFloat2)) {
      return Float.POSITIVE_INFINITY;
    }
    return PixelUtil.toPixelFromDIP(paramFloat2);
  }
}
