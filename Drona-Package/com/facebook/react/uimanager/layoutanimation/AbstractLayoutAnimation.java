package com.facebook.react.uimanager.layoutanimation;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BaseInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.IllegalViewOperationException;
import java.util.Map;

abstract class AbstractLayoutAnimation
{
  private static final Map<InterpolatorType, BaseInterpolator> INTERPOLATOR = MapBuilder.get(InterpolatorType.LINEAR, new LinearInterpolator(), InterpolatorType.EASE_IN, new AccelerateInterpolator(), InterpolatorType.EASE_OUT, new DecelerateInterpolator(), InterpolatorType.EASE_IN_EASE_OUT, new AccelerateDecelerateInterpolator());
  private static final boolean SLOWDOWN_ANIMATION_MODE = false;
  @Nullable
  protected AnimatedPropertyType mAnimatedProperty;
  private int mDelayMs;
  protected int mDurationMs;
  @Nullable
  private Interpolator mInterpolator;
  
  AbstractLayoutAnimation() {}
  
  private static Interpolator getInterpolator(InterpolatorType paramInterpolatorType, ReadableMap paramReadableMap)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a3 = a2\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public final Animation createAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!isValid()) {
      return null;
    }
    paramView = createAnimationImpl(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramView != null)
    {
      paramView.setDuration(mDurationMs * 1);
      paramView.setStartOffset(mDelayMs * 1);
      paramView.setInterpolator(mInterpolator);
    }
    return paramView;
  }
  
  abstract Animation createAnimationImpl(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public void initializeFromConfig(ReadableMap paramReadableMap, int paramInt)
  {
    Object localObject;
    if (paramReadableMap.hasKey("property")) {
      localObject = AnimatedPropertyType.fromString(paramReadableMap.getString("property"));
    } else {
      localObject = null;
    }
    mAnimatedProperty = ((AnimatedPropertyType)localObject);
    if (paramReadableMap.hasKey("duration")) {
      paramInt = paramReadableMap.getInt("duration");
    }
    mDurationMs = paramInt;
    if (paramReadableMap.hasKey("delay")) {
      paramInt = paramReadableMap.getInt("delay");
    } else {
      paramInt = 0;
    }
    mDelayMs = paramInt;
    if (paramReadableMap.hasKey("type"))
    {
      mInterpolator = getInterpolator(InterpolatorType.fromString(paramReadableMap.getString("type")), paramReadableMap);
      if (isValid()) {
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Invalid layout animation : ");
      ((StringBuilder)localObject).append(paramReadableMap);
      throw new IllegalViewOperationException(((StringBuilder)localObject).toString());
    }
    throw new IllegalArgumentException("Missing interpolation type.");
  }
  
  abstract boolean isValid();
  
  public void reset()
  {
    mAnimatedProperty = null;
    mDurationMs = 0;
    mDelayMs = 0;
    mInterpolator = null;
  }
}
