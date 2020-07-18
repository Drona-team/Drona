package com.facebook.react.views.text;

import android.annotation.TargetApi;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.NativeViewHierarchyOptimizer;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ReactShadowNode;
import com.facebook.react.uimanager.ReactShadowNodeImpl;
import com.facebook.yoga.YogaDirection;
import com.facebook.yoga.YogaUnit;
import com.facebook.yoga.YogaValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@TargetApi(23)
public abstract class ReactBaseTextShadowNode
  extends LayoutShadowNode
{
  public static final int DEFAULT_TEXT_SHADOW_COLOR = 1426063360;
  private static final String INLINE_VIEW_PLACEHOLDER = "0";
  public static final String PROP_SHADOW_COLOR = "textShadowColor";
  public static final String PROP_SHADOW_OFFSET = "textShadowOffset";
  public static final String PROP_SHADOW_OFFSET_HEIGHT = "height";
  public static final String PROP_SHADOW_OFFSET_WIDTH = "width";
  public static final String PROP_SHADOW_RADIUS = "textShadowRadius";
  public static final String PROP_TEXT_TRANSFORM = "textTransform";
  public static final int UNSET = -1;
  protected int mBackgroundColor;
  protected int mColor;
  protected boolean mContainsImages;
  @Nullable
  protected String mFontFamily;
  protected int mFontStyle;
  protected int mFontWeight;
  protected int mHyphenationFrequency;
  protected boolean mIncludeFontPadding;
  protected Map<Integer, ReactShadowNode> mInlineViews;
  protected boolean mIsBackgroundColorSet = false;
  protected boolean mIsColorSet = false;
  protected boolean mIsLineThroughTextDecorationSet;
  protected boolean mIsUnderlineTextDecorationSet;
  protected int mJustificationMode;
  protected int mNumberOfLines = -1;
  protected int mTextAlign = 0;
  protected TextAttributes mTextAttributes;
  protected int mTextBreakStrategy;
  protected int mTextShadowColor;
  protected float mTextShadowOffsetDx;
  protected float mTextShadowOffsetDy;
  protected float mTextShadowRadius;
  protected TextTransform mTextTransform;
  
  public ReactBaseTextShadowNode()
  {
    if (Build.VERSION.SDK_INT < 23) {
      i = 0;
    } else {
      i = 1;
    }
    mTextBreakStrategy = i;
    int i = Build.VERSION.SDK_INT;
    mHyphenationFrequency = 0;
    i = Build.VERSION.SDK_INT;
    mJustificationMode = 0;
    mTextTransform = TextTransform.UNSET;
    mTextShadowOffsetDx = 0.0F;
    mTextShadowOffsetDy = 0.0F;
    mTextShadowRadius = 0.0F;
    mTextShadowColor = 1426063360;
    mIsUnderlineTextDecorationSet = false;
    mIsLineThroughTextDecorationSet = false;
    mIncludeFontPadding = true;
    mFontStyle = -1;
    mFontWeight = -1;
    mFontFamily = null;
    mContainsImages = false;
    mTextAttributes = new TextAttributes();
  }
  
  private static void buildSpannedFromShadowNode(ReactBaseTextShadowNode paramReactBaseTextShadowNode, SpannableStringBuilder paramSpannableStringBuilder, List paramList, TextAttributes paramTextAttributes, boolean paramBoolean, Map paramMap, int paramInt)
  {
    if (paramTextAttributes != null) {}
    for (TextAttributes localTextAttributes = paramTextAttributes.applyChild(mTextAttributes);; localTextAttributes = mTextAttributes) {
      break;
    }
    int j = paramReactBaseTextShadowNode.getChildCount();
    int i = 0;
    float f1;
    while (i < j)
    {
      ReactShadowNodeImpl localReactShadowNodeImpl = paramReactBaseTextShadowNode.getChildAt(i);
      if ((localReactShadowNodeImpl instanceof ReactRawTextShadowNode)) {
        paramSpannableStringBuilder.append(TextTransform.apply(((ReactRawTextShadowNode)localReactShadowNodeImpl).getText(), localTextAttributes.getTextTransform()));
      }
      for (;;)
      {
        break;
        if ((localReactShadowNodeImpl instanceof ReactBaseTextShadowNode))
        {
          buildSpannedFromShadowNode((ReactBaseTextShadowNode)localReactShadowNodeImpl, paramSpannableStringBuilder, paramList, localTextAttributes, paramBoolean, paramMap, paramSpannableStringBuilder.length());
        }
        else if ((localReactShadowNodeImpl instanceof ReactTextInlineImageShadowNode))
        {
          paramSpannableStringBuilder.append("0");
          paramList.add(new SetSpanOperation(paramSpannableStringBuilder.length() - "0".length(), paramSpannableStringBuilder.length(), ((ReactTextInlineImageShadowNode)localReactShadowNodeImpl).buildInlineImageSpan()));
        }
        else
        {
          if (!paramBoolean) {
            break label326;
          }
          int k = localReactShadowNodeImpl.getReactTag();
          YogaValue localYogaValue1 = localReactShadowNodeImpl.getStyleWidth();
          YogaValue localYogaValue2 = localReactShadowNodeImpl.getStyleHeight();
          if ((unit != YogaUnit.POINT) || (unit != YogaUnit.POINT)) {
            break label316;
          }
          f1 = value;
          float f2 = value;
          paramSpannableStringBuilder.append("0");
          paramList.add(new SetSpanOperation(paramSpannableStringBuilder.length() - "0".length(), paramSpannableStringBuilder.length(), new TextInlineViewPlaceholderSpan(k, (int)f1, (int)f2)));
          paramMap.put(Integer.valueOf(k), localReactShadowNodeImpl);
        }
      }
      localReactShadowNodeImpl.markUpdateSeen();
      i += 1;
      continue;
      label316:
      throw new IllegalViewOperationException("Views nested within a <Text> must have a width and height");
      label326:
      paramReactBaseTextShadowNode = new StringBuilder();
      paramReactBaseTextShadowNode.append("Unexpected view type nested under a <Text> or <TextInput> node: ");
      paramReactBaseTextShadowNode.append(localReactShadowNodeImpl.getClass());
      throw new IllegalViewOperationException(paramReactBaseTextShadowNode.toString());
    }
    i = paramSpannableStringBuilder.length();
    if (i >= paramInt)
    {
      if (mIsColorSet) {
        paramList.add(new SetSpanOperation(paramInt, i, new ReactForegroundColorSpan(mColor)));
      }
      if (mIsBackgroundColorSet) {
        paramList.add(new SetSpanOperation(paramInt, i, new ReactBackgroundColorSpan(mBackgroundColor)));
      }
      if (Build.VERSION.SDK_INT >= 21)
      {
        f1 = localTextAttributes.getEffectiveLetterSpacing();
        if ((!Float.isNaN(f1)) && ((paramTextAttributes == null) || (paramTextAttributes.getEffectiveLetterSpacing() != f1))) {
          paramList.add(new SetSpanOperation(paramInt, i, new CustomLetterSpacingSpan(f1)));
        }
      }
      j = localTextAttributes.getEffectiveFontSize();
      if ((paramTextAttributes == null) || (paramTextAttributes.getEffectiveFontSize() != j)) {
        paramList.add(new SetSpanOperation(paramInt, i, new ReactAbsoluteSizeSpan(j)));
      }
      if ((mFontStyle != -1) || (mFontWeight != -1) || (mFontFamily != null)) {
        paramList.add(new SetSpanOperation(paramInt, i, new CustomStyleSpan(mFontStyle, mFontWeight, mFontFamily, paramReactBaseTextShadowNode.getThemedContext().getAssets())));
      }
      if (mIsUnderlineTextDecorationSet) {
        paramList.add(new SetSpanOperation(paramInt, i, new ReactUnderlineSpan()));
      }
      if (mIsLineThroughTextDecorationSet) {
        paramList.add(new SetSpanOperation(paramInt, i, new ReactStrikethroughSpan()));
      }
      if (((mTextShadowOffsetDx != 0.0F) || (mTextShadowOffsetDy != 0.0F) || (mTextShadowRadius != 0.0F)) && (Color.alpha(mTextShadowColor) != 0)) {
        paramList.add(new SetSpanOperation(paramInt, i, new ShadowStyleSpan(mTextShadowOffsetDx, mTextShadowOffsetDy, mTextShadowRadius, mTextShadowColor)));
      }
      f1 = localTextAttributes.getEffectiveLineHeight();
      if ((!Float.isNaN(f1)) && ((paramTextAttributes == null) || (paramTextAttributes.getEffectiveLineHeight() != f1))) {
        paramList.add(new SetSpanOperation(paramInt, i, new CustomLineHeightSpan(f1)));
      }
      paramList.add(new SetSpanOperation(paramInt, i, new ReactTagSpan(paramReactBaseTextShadowNode.getReactTag())));
    }
  }
  
  private int getTextAlign()
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
  
  private static int parseNumericFontWeight(String paramString)
  {
    if ((paramString.length() == 3) && (paramString.endsWith("00")) && (paramString.charAt(0) <= '9') && (paramString.charAt(0) >= '1')) {
      return (paramString.charAt(0) - '0') * 100;
    }
    return -1;
  }
  
  public void setAllowFontScaling(boolean paramBoolean)
  {
    if (paramBoolean != mTextAttributes.getAllowFontScaling())
    {
      mTextAttributes.setAllowFontScaling(paramBoolean);
      markUpdated();
    }
  }
  
  public void setBackgroundColor(Integer paramInteger)
  {
    if (isVirtual())
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
      markUpdated();
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
    markUpdated();
  }
  
  public void setFontFamily(String paramString)
  {
    mFontFamily = paramString;
    markUpdated();
  }
  
  public void setFontSize(float paramFloat)
  {
    mTextAttributes.setFontSize(paramFloat);
    markUpdated();
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
    if (i != mFontStyle)
    {
      mFontStyle = i;
      markUpdated();
    }
  }
  
  public void setFontWeight(String paramString)
  {
    int i;
    if (paramString != null) {
      i = parseNumericFontWeight(paramString);
    } else {
      i = -1;
    }
    int k = 0;
    if (i == -1) {
      i = 0;
    }
    int j;
    if ((i != 700) && (!"bold".equals(paramString)))
    {
      j = k;
      if (i != 400) {
        if ("normal".equals(paramString)) {
          j = k;
        } else {
          j = i;
        }
      }
    }
    else
    {
      j = 1;
    }
    if (j != mFontWeight)
    {
      mFontWeight = j;
      markUpdated();
    }
  }
  
  public void setIncludeFontPadding(boolean paramBoolean)
  {
    mIncludeFontPadding = paramBoolean;
  }
  
  public void setLetterSpacing(float paramFloat)
  {
    mTextAttributes.setLetterSpacing(paramFloat);
    markUpdated();
  }
  
  public void setLineHeight(float paramFloat)
  {
    mTextAttributes.setLineHeight(paramFloat);
    markUpdated();
  }
  
  public void setMaxFontSizeMultiplier(float paramFloat)
  {
    if (paramFloat != mTextAttributes.getMaxFontSizeMultiplier())
    {
      mTextAttributes.setMaxFontSizeMultiplier(paramFloat);
      markUpdated();
    }
  }
  
  public void setNumberOfLines(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = -1;
    }
    mNumberOfLines = i;
    markUpdated();
  }
  
  public void setTextAlign(String paramString)
  {
    if ("justify".equals(paramString))
    {
      if (Build.VERSION.SDK_INT >= 26) {
        mJustificationMode = 1;
      }
      mTextAlign = 3;
    }
    else
    {
      if (Build.VERSION.SDK_INT >= 26) {
        mJustificationMode = 0;
      }
      if ((paramString != null) && (!"auto".equals(paramString)))
      {
        if ("left".equals(paramString))
        {
          mTextAlign = 3;
        }
        else if ("right".equals(paramString))
        {
          mTextAlign = 5;
        }
        else if ("center".equals(paramString))
        {
          mTextAlign = 1;
        }
        else
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Invalid textAlign: ");
          localStringBuilder.append(paramString);
          throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
        }
      }
      else {
        mTextAlign = 0;
      }
    }
    markUpdated();
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
      }
      else if ("balanced".equals(paramString))
      {
        mTextBreakStrategy = 2;
      }
      else
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Invalid textBreakStrategy: ");
        localStringBuilder.append(paramString);
        throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
      }
    }
    else {
      mTextBreakStrategy = 1;
    }
    markUpdated();
  }
  
  public void setTextDecorationLine(String paramString)
  {
    int i = 0;
    mIsUnderlineTextDecorationSet = false;
    mIsLineThroughTextDecorationSet = false;
    if (paramString != null)
    {
      paramString = paramString.split(" ");
      int j = paramString.length;
      while (i < j)
      {
        Object localObject = paramString[i];
        if ("underline".equals(localObject)) {
          mIsUnderlineTextDecorationSet = true;
        } else if ("line-through".equals(localObject)) {
          mIsLineThroughTextDecorationSet = true;
        }
        i += 1;
      }
    }
    markUpdated();
  }
  
  public void setTextShadowColor(int paramInt)
  {
    if (paramInt != mTextShadowColor)
    {
      mTextShadowColor = paramInt;
      markUpdated();
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
    markUpdated();
  }
  
  public void setTextShadowRadius(float paramFloat)
  {
    if (paramFloat != mTextShadowRadius)
    {
      mTextShadowRadius = paramFloat;
      markUpdated();
    }
  }
  
  public void setTextTransform(String paramString)
  {
    if (paramString == null)
    {
      mTextAttributes.setTextTransform(TextTransform.UNSET);
    }
    else if ("none".equals(paramString))
    {
      mTextAttributes.setTextTransform(TextTransform.NONE);
    }
    else if ("uppercase".equals(paramString))
    {
      mTextAttributes.setTextTransform(TextTransform.UPPERCASE);
    }
    else if ("lowercase".equals(paramString))
    {
      mTextAttributes.setTextTransform(TextTransform.LOWERCASE);
    }
    else
    {
      if (!"capitalize".equals(paramString)) {
        break label111;
      }
      mTextAttributes.setTextTransform(TextTransform.CAPITALIZE);
    }
    markUpdated();
    return;
    label111:
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Invalid textTransform: ");
    localStringBuilder.append(paramString);
    throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
  }
  
  protected Spannable spannedFromShadowNode(ReactBaseTextShadowNode paramReactBaseTextShadowNode, String paramString, boolean paramBoolean, NativeViewHierarchyOptimizer paramNativeViewHierarchyOptimizer)
  {
    int i = 0;
    boolean bool;
    if ((paramBoolean) && (paramNativeViewHierarchyOptimizer == null)) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.assertCondition(bool, "nativeViewHierarchyOptimizer is required when inline views are supported");
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
    Object localObject1 = new ArrayList();
    if (paramBoolean) {}
    for (HashMap localHashMap = new HashMap();; localHashMap = null) {
      break;
    }
    if (paramString != null) {
      localSpannableStringBuilder.append(TextTransform.apply(paramString, mTextAttributes.getTextTransform()));
    }
    buildSpannedFromShadowNode(paramReactBaseTextShadowNode, localSpannableStringBuilder, (List)localObject1, null, paramBoolean, localHashMap, 0);
    mContainsImages = false;
    mInlineViews = localHashMap;
    float f1 = NaN.0F;
    paramString = ((List)localObject1).iterator();
    while (paramString.hasNext())
    {
      localObject1 = (SetSpanOperation)paramString.next();
      paramBoolean = what instanceof TextInlineImageSpan;
      float f2;
      if (!paramBoolean)
      {
        f2 = f1;
        if (!(what instanceof TextInlineViewPlaceholderSpan)) {}
      }
      else
      {
        int j;
        if (paramBoolean)
        {
          j = ((TextInlineImageSpan)what).getHeight();
          mContainsImages = true;
        }
        else
        {
          Object localObject2 = (TextInlineViewPlaceholderSpan)what;
          j = ((TextInlineViewPlaceholderSpan)localObject2).getHeight();
          localObject2 = (ReactShadowNode)localHashMap.get(Integer.valueOf(((TextInlineViewPlaceholderSpan)localObject2).getReactTag()));
          paramNativeViewHierarchyOptimizer.handleForceViewToBeNonLayoutOnly((ReactShadowNode)localObject2);
          ((ReactShadowNode)localObject2).setLayoutParent(paramReactBaseTextShadowNode);
        }
        if (!Float.isNaN(f1))
        {
          f2 = f1;
          if (j <= f1) {}
        }
        else
        {
          f2 = j;
        }
      }
      ((SetSpanOperation)localObject1).execute(localSpannableStringBuilder, i);
      i += 1;
      f1 = f2;
    }
    mTextAttributes.setHeightOfTallestInlineViewOrImage(f1);
    return localSpannableStringBuilder;
  }
  
  private static class SetSpanOperation
  {
    protected int end;
    protected int start;
    protected ReactSpan what;
    
    SetSpanOperation(int paramInt1, int paramInt2, ReactSpan paramReactSpan)
    {
      start = paramInt1;
      end = paramInt2;
      what = paramReactSpan;
    }
    
    public void execute(SpannableStringBuilder paramSpannableStringBuilder, int paramInt)
    {
      int i;
      if (start == 0) {
        i = 18;
      } else {
        i = 34;
      }
      paramSpannableStringBuilder.setSpan(what, start, end, paramInt << 16 & 0xFF0000 | i & 0xFF00FFFF);
    }
  }
}
