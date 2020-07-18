package com.facebook.react.views.text;

import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.uimanager.PixelUtil;

public class TextAttributes
{
  public static final float DEFAULT_MAX_FONT_SIZE_MULTIPLIER = 0.0F;
  private boolean mAllowFontScaling = true;
  private float mFontSize = NaN.0F;
  private float mHeightOfTallestInlineViewOrImage = NaN.0F;
  private float mLetterSpacing = NaN.0F;
  private float mLineHeight = NaN.0F;
  private float mMaxFontSizeMultiplier = NaN.0F;
  private TextTransform mTextTransform = TextTransform.UNSET;
  
  public TextAttributes() {}
  
  public TextAttributes applyChild(TextAttributes paramTextAttributes)
  {
    TextAttributes localTextAttributes = new TextAttributes();
    mAllowFontScaling = mAllowFontScaling;
    float f;
    if (!Float.isNaN(mFontSize)) {
      f = mFontSize;
    } else {
      f = mFontSize;
    }
    mFontSize = f;
    if (!Float.isNaN(mLineHeight)) {
      f = mLineHeight;
    } else {
      f = mLineHeight;
    }
    mLineHeight = f;
    if (!Float.isNaN(mLetterSpacing)) {
      f = mLetterSpacing;
    } else {
      f = mLetterSpacing;
    }
    mLetterSpacing = f;
    if (!Float.isNaN(mMaxFontSizeMultiplier)) {
      f = mMaxFontSizeMultiplier;
    } else {
      f = mMaxFontSizeMultiplier;
    }
    mMaxFontSizeMultiplier = f;
    if (!Float.isNaN(mHeightOfTallestInlineViewOrImage)) {
      f = mHeightOfTallestInlineViewOrImage;
    } else {
      f = mHeightOfTallestInlineViewOrImage;
    }
    mHeightOfTallestInlineViewOrImage = f;
    if (mTextTransform != TextTransform.UNSET) {
      paramTextAttributes = mTextTransform;
    } else {
      paramTextAttributes = mTextTransform;
    }
    mTextTransform = paramTextAttributes;
    return localTextAttributes;
  }
  
  public boolean getAllowFontScaling()
  {
    return mAllowFontScaling;
  }
  
  public int getEffectiveFontSize()
  {
    float f;
    if (!Float.isNaN(mFontSize)) {
      f = mFontSize;
    } else {
      f = 14.0F;
    }
    if (mAllowFontScaling) {
      return (int)Math.ceil(PixelUtil.toPixelFromSP(f, getEffectiveMaxFontSizeMultiplier()));
    }
    return (int)Math.ceil(PixelUtil.toPixelFromDIP(f));
  }
  
  public float getEffectiveLetterSpacing()
  {
    if (Float.isNaN(mLetterSpacing)) {
      return NaN.0F;
    }
    float f;
    if (mAllowFontScaling) {
      f = PixelUtil.toPixelFromSP(mLetterSpacing, getEffectiveMaxFontSizeMultiplier());
    } else {
      f = PixelUtil.toPixelFromDIP(mLetterSpacing);
    }
    return f / getEffectiveFontSize();
  }
  
  public float getEffectiveLineHeight()
  {
    if (Float.isNaN(mLineHeight)) {
      return NaN.0F;
    }
    float f;
    if (mAllowFontScaling) {
      f = PixelUtil.toPixelFromSP(mLineHeight, getEffectiveMaxFontSizeMultiplier());
    } else {
      f = PixelUtil.toPixelFromDIP(mLineHeight);
    }
    int i;
    if ((!Float.isNaN(mHeightOfTallestInlineViewOrImage)) && (mHeightOfTallestInlineViewOrImage > f)) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0) {
      f = mHeightOfTallestInlineViewOrImage;
    }
    return f;
  }
  
  public float getEffectiveMaxFontSizeMultiplier()
  {
    if (!Float.isNaN(mMaxFontSizeMultiplier)) {
      return mMaxFontSizeMultiplier;
    }
    return 0.0F;
  }
  
  public float getFontSize()
  {
    return mFontSize;
  }
  
  public float getHeightOfTallestInlineViewOrImage()
  {
    return mHeightOfTallestInlineViewOrImage;
  }
  
  public float getLetterSpacing()
  {
    return mLetterSpacing;
  }
  
  public float getLineHeight()
  {
    return mLineHeight;
  }
  
  public float getMaxFontSizeMultiplier()
  {
    return mMaxFontSizeMultiplier;
  }
  
  public TextTransform getTextTransform()
  {
    return mTextTransform;
  }
  
  public void setAllowFontScaling(boolean paramBoolean)
  {
    mAllowFontScaling = paramBoolean;
  }
  
  public void setFontSize(float paramFloat)
  {
    mFontSize = paramFloat;
  }
  
  public void setHeightOfTallestInlineViewOrImage(float paramFloat)
  {
    mHeightOfTallestInlineViewOrImage = paramFloat;
  }
  
  public void setLetterSpacing(float paramFloat)
  {
    mLetterSpacing = paramFloat;
  }
  
  public void setLineHeight(float paramFloat)
  {
    mLineHeight = paramFloat;
  }
  
  public void setMaxFontSizeMultiplier(float paramFloat)
  {
    if ((paramFloat != 0.0F) && (paramFloat < 1.0F)) {
      throw new JSApplicationIllegalArgumentException("maxFontSizeMultiplier must be NaN, 0, or >= 1");
    }
    mMaxFontSizeMultiplier = paramFloat;
  }
  
  public void setTextTransform(TextTransform paramTextTransform)
  {
    mTextTransform = paramTextTransform;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("TextAttributes {\n  getAllowFontScaling(): ");
    localStringBuilder.append(getAllowFontScaling());
    localStringBuilder.append("\n  getFontSize(): ");
    localStringBuilder.append(getFontSize());
    localStringBuilder.append("\n  getEffectiveFontSize(): ");
    localStringBuilder.append(getEffectiveFontSize());
    localStringBuilder.append("\n  getHeightOfTallestInlineViewOrImage(): ");
    localStringBuilder.append(getHeightOfTallestInlineViewOrImage());
    localStringBuilder.append("\n  getLetterSpacing(): ");
    localStringBuilder.append(getLetterSpacing());
    localStringBuilder.append("\n  getEffectiveLetterSpacing(): ");
    localStringBuilder.append(getEffectiveLetterSpacing());
    localStringBuilder.append("\n  getLineHeight(): ");
    localStringBuilder.append(getLineHeight());
    localStringBuilder.append("\n  getEffectiveLineHeight(): ");
    localStringBuilder.append(getEffectiveLineHeight());
    localStringBuilder.append("\n  getTextTransform(): ");
    localStringBuilder.append(getTextTransform());
    localStringBuilder.append("\n  getMaxFontSizeMultiplier(): ");
    localStringBuilder.append(getMaxFontSizeMultiplier());
    localStringBuilder.append("\n  getEffectiveMaxFontSizeMultiplier(): ");
    localStringBuilder.append(getEffectiveMaxFontSizeMultiplier());
    localStringBuilder.append("\n}");
    return localStringBuilder.toString();
  }
}