package com.facebook.react.uimanager;

import com.facebook.yoga.YogaConstants;
import java.util.Arrays;

public class Spacing
{
  public static final int BOTTOM = 3;
  public static final int DELETE_CONTEXT = 8;
  public static final int DIALOG_CANCEL = 5;
  public static final int HORIZONTAL = 6;
  public static final int LEFT = 0;
  public static final int RIGHT = 2;
  public static final int START = 4;
  public static final int TYPE_EXPANDED = 1;
  public static final int VERTICAL = 7;
  private static final int[] sFlagsMap = { 1, 2, 4, 8, 16, 32, 64, 128, 256 };
  private final float mDefaultValue;
  private boolean mHasAliasesSet;
  private final float[] mSpacing;
  private int mValueFlags = 0;
  
  public Spacing()
  {
    this(0.0F);
  }
  
  public Spacing(float paramFloat)
  {
    mDefaultValue = paramFloat;
    mSpacing = newFullSpacingArray();
  }
  
  public Spacing(Spacing paramSpacing)
  {
    mDefaultValue = mDefaultValue;
    mSpacing = Arrays.copyOf(mSpacing, mSpacing.length);
    mValueFlags = mValueFlags;
    mHasAliasesSet = mHasAliasesSet;
  }
  
  private static float[] newFullSpacingArray()
  {
    return new float[] { NaN.0F, NaN.0F, NaN.0F, NaN.0F, NaN.0F, NaN.0F, NaN.0F, NaN.0F, NaN.0F };
  }
  
  public float getRaw(int paramInt)
  {
    return mSpacing[paramInt];
  }
  
  public float getValue(int paramInt)
  {
    float f1;
    if ((paramInt != 4) && (paramInt != 5)) {
      f1 = mDefaultValue;
    } else {
      f1 = NaN.0F;
    }
    if (mValueFlags == 0) {
      return f1;
    }
    if ((mValueFlags & sFlagsMap[paramInt]) != 0) {
      return mSpacing[paramInt];
    }
    float f2 = f1;
    if (mHasAliasesSet)
    {
      if ((paramInt != 1) && (paramInt != 3)) {
        paramInt = 6;
      } else {
        paramInt = 7;
      }
      if ((mValueFlags & sFlagsMap[paramInt]) != 0) {
        return mSpacing[paramInt];
      }
      f2 = f1;
      if ((mValueFlags & sFlagsMap[8]) != 0) {
        f2 = mSpacing[8];
      }
    }
    return f2;
  }
  
  float getWithFallback(int paramInt1, int paramInt2)
  {
    if ((mValueFlags & sFlagsMap[paramInt1]) != 0) {
      return mSpacing[paramInt1];
    }
    return getValue(paramInt2);
  }
  
  public void reset()
  {
    Arrays.fill(mSpacing, NaN.0F);
    mHasAliasesSet = false;
    mValueFlags = 0;
  }
  
  public boolean writeLong(int paramInt, float paramFloat)
  {
    boolean bool2 = FloatUtil.floatsEqual(mSpacing[paramInt], paramFloat);
    boolean bool1 = false;
    if (!bool2)
    {
      mSpacing[paramInt] = paramFloat;
      int i;
      if (YogaConstants.isUndefined(paramFloat))
      {
        i = mValueFlags;
        mValueFlags = (sFlagsMap[paramInt] & i);
      }
      else
      {
        i = mValueFlags;
        mValueFlags = (sFlagsMap[paramInt] | i);
      }
      if (((mValueFlags & sFlagsMap[8]) != 0) || ((mValueFlags & sFlagsMap[7]) != 0) || ((mValueFlags & sFlagsMap[6]) != 0)) {
        bool1 = true;
      }
      mHasAliasesSet = bool1;
      return true;
    }
    return false;
  }
}
