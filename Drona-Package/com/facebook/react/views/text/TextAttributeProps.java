package com.facebook.react.views.text;

import android.os.Build.VERSION;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.yoga.YogaDirection;

public class TextAttributeProps
{
  private static final int DEFAULT_TEXT_SHADOW_COLOR = 1426063360;
  private static final String INLINE_IMAGE_PLACEHOLDER = "I";
  private static final String PROP_SHADOW_COLOR = "textShadowColor";
  private static final String PROP_SHADOW_OFFSET = "textShadowOffset";
  private static final String PROP_SHADOW_OFFSET_HEIGHT = "height";
  private static final String PROP_SHADOW_OFFSET_WIDTH = "width";
  private static final String PROP_SHADOW_RADIUS = "textShadowRadius";
  private static final String PROP_TEXT_TRANSFORM = "textTransform";
  public static final int UNSET = -1;
  protected boolean mAllowFontScaling = true;
  protected int mBackgroundColor;
  protected int mColor;
  protected boolean mContainsImages;
  @Nullable
  protected String mFontFamily;
  protected int mFontSize = -1;
  protected float mFontSizeInput = -1.0F;
  protected int mFontStyle;
  protected int mFontWeight;
  protected float mHeightOfTallestInlineImage;
  protected boolean mIncludeFontPadding;
  protected boolean mIsBackgroundColorSet = false;
  protected boolean mIsColorSet = false;
  protected boolean mIsLineThroughTextDecorationSet;
  protected boolean mIsUnderlineTextDecorationSet;
  protected int mJustificationMode;
  protected float mLetterSpacing = NaN.0F;
  protected float mLetterSpacingInput = NaN.0F;
  protected float mLineHeight = NaN.0F;
  protected float mLineHeightInput = -1.0F;
  protected int mNumberOfLines = -1;
  private final ReactStylesDiffMap mProps;
  protected int mTextAlign = 0;
  protected int mTextBreakStrategy;
  protected int mTextShadowColor;
  protected float mTextShadowOffsetDx;
  protected float mTextShadowOffsetDy;
  protected float mTextShadowRadius;
  protected TextTransform mTextTransform;
  
  public TextAttributeProps(ReactStylesDiffMap paramReactStylesDiffMap)
  {
    if (Build.VERSION.SDK_INT < 23) {
      i = 0;
    } else {
      i = 1;
    }
    mTextBreakStrategy = i;
    int i = Build.VERSION.SDK_INT;
    mJustificationMode = 0;
    mTextTransform = TextTransform.UNSET;
    mTextShadowOffsetDx = 0.0F;
    mTextShadowOffsetDy = 0.0F;
    mTextShadowRadius = 1.0F;
    mTextShadowColor = 1426063360;
    mIsUnderlineTextDecorationSet = false;
    mIsLineThroughTextDecorationSet = false;
    mIncludeFontPadding = true;
    mFontStyle = -1;
    mFontWeight = -1;
    Object localObject2 = null;
    mFontFamily = null;
    mContainsImages = false;
    mHeightOfTallestInlineImage = NaN.0F;
    mProps = paramReactStylesDiffMap;
    setNumberOfLines(getIntProp("numberOfLines", -1));
    setLineHeight(getFloatProp("lineHeight", -1.0F));
    setLetterSpacing(getFloatProp("letterSpacing", NaN.0F));
    setAllowFontScaling(getBooleanProp("allowFontScaling", true));
    setTextAlign(getStringProp("textAlign"));
    setFontSize(getFloatProp("fontSize", -1.0F));
    if (paramReactStylesDiffMap.hasKey("color")) {
      localObject1 = Integer.valueOf(paramReactStylesDiffMap.getInt("color", 0));
    } else {
      localObject1 = null;
    }
    setColor((Integer)localObject1);
    if (paramReactStylesDiffMap.hasKey("foregroundColor")) {
      localObject1 = Integer.valueOf(paramReactStylesDiffMap.getInt("foregroundColor", 0));
    } else {
      localObject1 = null;
    }
    setColor((Integer)localObject1);
    if (paramReactStylesDiffMap.hasKey("backgroundColor")) {
      localObject1 = Integer.valueOf(paramReactStylesDiffMap.getInt("backgroundColor", 0));
    } else {
      localObject1 = null;
    }
    setBackgroundColor((Integer)localObject1);
    setFontFamily(getStringProp("fontFamily"));
    setFontWeight(getStringProp("fontWeight"));
    setFontStyle(getStringProp("fontStyle"));
    setIncludeFontPadding(getBooleanProp("includeFontPadding", true));
    setTextDecorationLine(getStringProp("textDecorationLine"));
    setTextBreakStrategy(getStringProp("textBreakStrategy"));
    Object localObject1 = localObject2;
    if (paramReactStylesDiffMap.hasKey("textShadowOffset")) {
      localObject1 = paramReactStylesDiffMap.getMap("textShadowOffset");
    }
    setTextShadowOffset((ReadableMap)localObject1);
    setTextShadowRadius(getIntProp("textShadowRadius", 1));
    setTextShadowColor(getIntProp("textShadowColor", 1426063360));
    setTextTransform(getStringProp("textTransform"));
  }
  
  private boolean getBooleanProp(String paramString, boolean paramBoolean)
  {
    boolean bool = paramBoolean;
    if (mProps.hasKey(paramString)) {
      bool = mProps.getBoolean(paramString, paramBoolean);
    }
    return bool;
  }
  
  private float getFloatProp(String paramString, float paramFloat)
  {
    float f = paramFloat;
    if (mProps.hasKey(paramString)) {
      f = mProps.getFloat(paramString, paramFloat);
    }
    return f;
  }
  
  private int getIntProp(String paramString, int paramInt)
  {
    int i = paramInt;
    if (mProps.hasKey(paramString)) {
      i = mProps.getInt(paramString, paramInt);
    }
    return i;
  }
  
  private YogaDirection getLayoutDirection()
  {
    return YogaDirection.LEFT;
  }
  
  private float getPaddingProp(String paramString)
  {
    if (mProps.hasKey("padding")) {
      return PixelUtil.toPixelFromDIP(getFloatProp("padding", 0.0F));
    }
    return PixelUtil.toPixelFromDIP(getFloatProp(paramString, 0.0F));
  }
  
  private String getStringProp(String paramString)
  {
    if (mProps.hasKey(paramString)) {
      return mProps.getString(paramString);
    }
    return null;
  }
  
  private static int parseNumericFontWeight(String paramString)
  {
    if ((paramString.length() == 3) && (paramString.endsWith("00")) && (paramString.charAt(0) <= '9') && (paramString.charAt(0) >= '1')) {
      return (paramString.charAt(0) - '0') * 100;
    }
    return -1;
  }
  
  public float getBottomPadding()
  {
    return getPaddingProp("paddingBottom");
  }
  
  public float getEffectiveLineHeight()
  {
    int i;
    if ((!Float.isNaN(mLineHeight)) && (!Float.isNaN(mHeightOfTallestInlineImage)) && (mHeightOfTallestInlineImage > mLineHeight)) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0) {
      return mHeightOfTallestInlineImage;
    }
    return mLineHeight;
  }
  
  public float getEndPadding()
  {
    return getPaddingProp("paddingEnd");
  }
  
  public float getLeftPadding()
  {
    return getPaddingProp("paddingLeft");
  }
  
  public float getRightPadding()
  {
    return getPaddingProp("paddingRight");
  }
  
  public float getStartPadding()
  {
    return getPaddingProp("paddingStart");
  }
  
  public int getTextAlign()
  {
    int i = mTextAlign;
    if (getLayoutDirection() == YogaDirection.RIGHT)
    {
      if (i == 5) {
        return 3;
      }
      if (i == 3) {
        return 5;
      }
    }
    return i;
  }
  
  public float getTopPadding()
  {
    return getPaddingProp("paddingTop");
  }
  
  public void setAllowFontScaling(boolean paramBoolean)
  {
    if (paramBoolean != mAllowFontScaling)
    {
      mAllowFontScaling = paramBoolean;
      setFontSize(mFontSizeInput);
      setLineHeight(mLineHeightInput);
      setLetterSpacing(mLetterSpacingInput);
    }
  }
  
  public void setBackgroundColor(Integer paramInteger)
  {
    boolean bool;
    if (paramInteger != null) {
      bool = true;
    } else {
      bool = false;
    }
    mIsBackgroundColorSet = bool;
    if (mIsBackgroundColorSet) {
      mBackgroundColor = paramInteger.intValue();
    }
  }
  
  public void setColor(Integer paramInteger)
  {
    boolean bool;
    if (paramInteger != null) {
      bool = true;
    } else {
      bool = false;
    }
    mIsColorSet = bool;
    if (mIsColorSet) {
      mColor = paramInteger.intValue();
    }
  }
  
  public void setFontFamily(String paramString)
  {
    mFontFamily = paramString;
  }
  
  public void setFontSize(float paramFloat)
  {
    mFontSizeInput = paramFloat;
    float f = paramFloat;
    if (paramFloat != -1.0F) {
      if (mAllowFontScaling) {
        f = (float)Math.ceil(PixelUtil.toPixelFromSP(paramFloat));
      } else {
        f = (float)Math.ceil(PixelUtil.toPixelFromDIP(paramFloat));
      }
    }
    mFontSize = ((int)f);
  }
  
  public void setFontStyle(String paramString)
  {
    int i;
    if ("italic".equals(paramString)) {
      i = 2;
    } else if ("normal".equals(paramString)) {
      i = 0;
    } else {
      i = -1;
    }
    if (i != mFontStyle) {
      mFontStyle = i;
    }
  }
  
  public void setFontWeight(String paramString)
  {
    int k = -1;
    int j;
    if (paramString != null) {
      j = parseNumericFontWeight(paramString);
    } else {
      j = -1;
    }
    int i;
    if ((j < 500) && (!"bold".equals(paramString)))
    {
      if (!"normal".equals(paramString))
      {
        i = k;
        if (j != -1)
        {
          i = k;
          if (j >= 500) {}
        }
      }
      else
      {
        i = 0;
      }
    }
    else {
      i = 1;
    }
    if (i != mFontWeight) {
      mFontWeight = i;
    }
  }
  
  public void setIncludeFontPadding(boolean paramBoolean)
  {
    mIncludeFontPadding = paramBoolean;
  }
  
  public void setLetterSpacing(float paramFloat)
  {
    mLetterSpacingInput = paramFloat;
    if (mAllowFontScaling) {
      paramFloat = PixelUtil.toPixelFromSP(mLetterSpacingInput);
    } else {
      paramFloat = PixelUtil.toPixelFromDIP(mLetterSpacingInput);
    }
    mLetterSpacing = paramFloat;
  }
  
  public void setLineHeight(float paramFloat)
  {
    mLineHeightInput = paramFloat;
    if (paramFloat == -1.0F)
    {
      mLineHeight = NaN.0F;
      return;
    }
    if (mAllowFontScaling) {
      paramFloat = PixelUtil.toPixelFromSP(paramFloat);
    } else {
      paramFloat = PixelUtil.toPixelFromDIP(paramFloat);
    }
    mLineHeight = paramFloat;
  }
  
  public void setNumberOfLines(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = -1;
    }
    mNumberOfLines = i;
  }
  
  public void setTextAlign(String paramString)
  {
    if ("justify".equals(paramString))
    {
      if (Build.VERSION.SDK_INT >= 26) {
        mJustificationMode = 1;
      }
      mTextAlign = 3;
      return;
    }
    if (Build.VERSION.SDK_INT >= 26) {
      mJustificationMode = 0;
    }
    if ((paramString != null) && (!"auto".equals(paramString)))
    {
      if ("left".equals(paramString))
      {
        mTextAlign = 3;
        return;
      }
      if ("right".equals(paramString))
      {
        mTextAlign = 5;
        return;
      }
      if ("center".equals(paramString))
      {
        mTextAlign = 1;
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Invalid textAlign: ");
      localStringBuilder.append(paramString);
      throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
    }
    mTextAlign = 0;
  }
  
  public void setTextBreakStrategy(String paramString)
  {
    if (Build.VERSION.SDK_INT < 23) {
      return;
    }
    if ((paramString != null) && (!"highQuality".equals(paramString)))
    {
      if ("simple".equals(paramString))
      {
        mTextBreakStrategy = 0;
        return;
      }
      if ("balanced".equals(paramString))
      {
        mTextBreakStrategy = 2;
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Invalid textBreakStrategy: ");
      localStringBuilder.append(paramString);
      throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
    }
    mTextBreakStrategy = 1;
  }
  
  public void setTextDecorationLine(String paramString)
  {
    int i = 0;
    mIsUnderlineTextDecorationSet = false;
    mIsLineThroughTextDecorationSet = false;
    if (paramString != null)
    {
      paramString = paramString.split("-");
      int j = paramString.length;
      while (i < j)
      {
        Object localObject = paramString[i];
        if ("underline".equals(localObject)) {
          mIsUnderlineTextDecorationSet = true;
        } else if ("strikethrough".equals(localObject)) {
          mIsLineThroughTextDecorationSet = true;
        }
        i += 1;
      }
    }
  }
  
  public void setTextShadowColor(int paramInt)
  {
    if (paramInt != mTextShadowColor) {
      mTextShadowColor = paramInt;
    }
  }
  
  public void setTextShadowOffset(ReadableMap paramReadableMap)
  {
    mTextShadowOffsetDx = 0.0F;
    mTextShadowOffsetDy = 0.0F;
    if (paramReadableMap != null)
    {
      if ((paramReadableMap.hasKey("width")) && (!paramReadableMap.isNull("width"))) {
        mTextShadowOffsetDx = PixelUtil.toPixelFromDIP(paramReadableMap.getDouble("width"));
      }
      if ((paramReadableMap.hasKey("height")) && (!paramReadableMap.isNull("height"))) {
        mTextShadowOffsetDy = PixelUtil.toPixelFromDIP(paramReadableMap.getDouble("height"));
      }
    }
  }
  
  public void setTextShadowRadius(float paramFloat)
  {
    if (paramFloat != mTextShadowRadius) {
      mTextShadowRadius = paramFloat;
    }
  }
  
  public void setTextTransform(String paramString)
  {
    if ((paramString != null) && (!"none".equals(paramString)))
    {
      if ("uppercase".equals(paramString))
      {
        mTextTransform = TextTransform.UPPERCASE;
        return;
      }
      if ("lowercase".equals(paramString))
      {
        mTextTransform = TextTransform.LOWERCASE;
        return;
      }
      if ("capitalize".equals(paramString))
      {
        mTextTransform = TextTransform.CAPITALIZE;
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Invalid textTransform: ");
      localStringBuilder.append(paramString);
      throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
    }
    mTextTransform = TextTransform.NONE;
  }
}
