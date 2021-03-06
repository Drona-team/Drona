package com.facebook.react.views.slider;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsSeekBar;
import android.widget.ProgressBar;
import androidx.appcompat.widget.AppCompatSeekBar;

public class ReactSlider
  extends AppCompatSeekBar
{
  private static int DEFAULT_TOTAL_STEPS;
  private double mMaxValue = 0.0D;
  private double mMinValue = 0.0D;
  private double mStep = 0.0D;
  private double mStepCalculated = 0.0D;
  private double mValue = 0.0D;
  
  public ReactSlider(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    disableStateListAnimatorIfNeeded();
  }
  
  private void disableStateListAnimatorIfNeeded()
  {
    if ((Build.VERSION.SDK_INT >= 23) && (Build.VERSION.SDK_INT < 26)) {
      super.setStateListAnimator(null);
    }
  }
  
  private double getStepValue()
  {
    if (mStep > 0.0D) {
      return mStep;
    }
    return mStepCalculated;
  }
  
  private int getTotalSteps()
  {
    return (int)Math.ceil((mMaxValue - mMinValue) / getStepValue());
  }
  
  private void updateAll()
  {
    if (mStep == 0.0D) {
      mStepCalculated = ((mMaxValue - mMinValue) / DEFAULT_TOTAL_STEPS);
    }
    setMax(getTotalSteps());
    updateValue();
  }
  
  private void updateValue()
  {
    setProgress((int)Math.round((mValue - mMinValue) / (mMaxValue - mMinValue) * getTotalSteps()));
  }
  
  void setMaxValue(double paramDouble)
  {
    mMaxValue = paramDouble;
    updateAll();
  }
  
  void setMinValue(double paramDouble)
  {
    mMinValue = paramDouble;
    updateAll();
  }
  
  void setStep(double paramDouble)
  {
    mStep = paramDouble;
    updateAll();
  }
  
  void setValue(double paramDouble)
  {
    mValue = paramDouble;
    updateValue();
  }
  
  public double toRealProgress(int paramInt)
  {
    if (paramInt == getMax()) {
      return mMaxValue;
    }
    return paramInt * getStepValue() + mMinValue;
  }
}
