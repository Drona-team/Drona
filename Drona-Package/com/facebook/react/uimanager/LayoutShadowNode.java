package com.facebook.react.uimanager;

import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.modules.i18nmanager.I18nUtil;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaDisplay;
import com.facebook.yoga.YogaFlexDirection;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaOverflow;
import com.facebook.yoga.YogaPositionType;
import com.facebook.yoga.YogaUnit;
import com.facebook.yoga.YogaWrap;

public class LayoutShadowNode
  extends ReactShadowNodeImpl
{
  boolean mCollapsable;
  private final MutableYogaValue mTempYogaValue = new MutableYogaValue(null);
  
  public LayoutShadowNode() {}
  
  private int maybeTransformLeftRightToStartEnd(int paramInt)
  {
    if (!I18nUtil.getInstance().doLeftAndRightSwapInRTL(getThemedContext())) {
      return paramInt;
    }
    if (paramInt != 0)
    {
      if (paramInt != 2) {
        return paramInt;
      }
      return 5;
    }
    return 4;
  }
  
  public void setAlignContent(String paramString)
  {
    if (isVirtual()) {
      return;
    }
    if (paramString == null)
    {
      setAlignContent(YogaAlign.FLEX_START);
      return;
    }
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      break;
    case 1937124468: 
      if (paramString.equals("space-around")) {
        i = 7;
      }
      break;
    case 1742952711: 
      if (paramString.equals("flex-end")) {
        i = 3;
      }
      break;
    case 441309761: 
      if (paramString.equals("space-between")) {
        i = 6;
      }
      break;
    case 3005871: 
      if (paramString.equals("auto")) {
        i = 0;
      }
      break;
    case -46581362: 
      if (paramString.equals("flex-start")) {
        i = 1;
      }
      break;
    case -1364013995: 
      if (paramString.equals("center")) {
        i = 2;
      }
      break;
    case -1720785339: 
      if (paramString.equals("baseline")) {
        i = 5;
      }
      break;
    case -1881872635: 
      if (paramString.equals("stretch")) {
        i = 4;
      }
      break;
    }
    switch (i)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("invalid value for alignContent: ");
      localStringBuilder.append(paramString);
      throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
    case 7: 
      setAlignContent(YogaAlign.SPACE_AROUND);
      return;
    case 6: 
      setAlignContent(YogaAlign.SPACE_BETWEEN);
      return;
    case 5: 
      setAlignContent(YogaAlign.BASELINE);
      return;
    case 4: 
      setAlignContent(YogaAlign.STRETCH);
      return;
    case 3: 
      setAlignContent(YogaAlign.FLEX_END);
      return;
    case 2: 
      setAlignContent(YogaAlign.CENTER);
      return;
    case 1: 
      setAlignContent(YogaAlign.FLEX_START);
      return;
    }
    setAlignContent(YogaAlign.AUTO);
  }
  
  public void setAlignItems(String paramString)
  {
    if (isVirtual()) {
      return;
    }
    if (paramString == null)
    {
      setAlignItems(YogaAlign.STRETCH);
      return;
    }
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      break;
    case 1937124468: 
      if (paramString.equals("space-around")) {
        i = 7;
      }
      break;
    case 1742952711: 
      if (paramString.equals("flex-end")) {
        i = 3;
      }
      break;
    case 441309761: 
      if (paramString.equals("space-between")) {
        i = 6;
      }
      break;
    case 3005871: 
      if (paramString.equals("auto")) {
        i = 0;
      }
      break;
    case -46581362: 
      if (paramString.equals("flex-start")) {
        i = 1;
      }
      break;
    case -1364013995: 
      if (paramString.equals("center")) {
        i = 2;
      }
      break;
    case -1720785339: 
      if (paramString.equals("baseline")) {
        i = 5;
      }
      break;
    case -1881872635: 
      if (paramString.equals("stretch")) {
        i = 4;
      }
      break;
    }
    switch (i)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("invalid value for alignItems: ");
      localStringBuilder.append(paramString);
      throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
    case 7: 
      setAlignItems(YogaAlign.SPACE_AROUND);
      return;
    case 6: 
      setAlignItems(YogaAlign.SPACE_BETWEEN);
      return;
    case 5: 
      setAlignItems(YogaAlign.BASELINE);
      return;
    case 4: 
      setAlignItems(YogaAlign.STRETCH);
      return;
    case 3: 
      setAlignItems(YogaAlign.FLEX_END);
      return;
    case 2: 
      setAlignItems(YogaAlign.CENTER);
      return;
    case 1: 
      setAlignItems(YogaAlign.FLEX_START);
      return;
    }
    setAlignItems(YogaAlign.AUTO);
  }
  
  public void setAlignSelf(String paramString)
  {
    if (isVirtual()) {
      return;
    }
    if (paramString == null)
    {
      setAlignSelf(YogaAlign.AUTO);
      return;
    }
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      break;
    case 1937124468: 
      if (paramString.equals("space-around")) {
        i = 7;
      }
      break;
    case 1742952711: 
      if (paramString.equals("flex-end")) {
        i = 3;
      }
      break;
    case 441309761: 
      if (paramString.equals("space-between")) {
        i = 6;
      }
      break;
    case 3005871: 
      if (paramString.equals("auto")) {
        i = 0;
      }
      break;
    case -46581362: 
      if (paramString.equals("flex-start")) {
        i = 1;
      }
      break;
    case -1364013995: 
      if (paramString.equals("center")) {
        i = 2;
      }
      break;
    case -1720785339: 
      if (paramString.equals("baseline")) {
        i = 5;
      }
      break;
    case -1881872635: 
      if (paramString.equals("stretch")) {
        i = 4;
      }
      break;
    }
    switch (i)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("invalid value for alignSelf: ");
      localStringBuilder.append(paramString);
      throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
    case 7: 
      setAlignSelf(YogaAlign.SPACE_AROUND);
      return;
    case 6: 
      setAlignSelf(YogaAlign.SPACE_BETWEEN);
      return;
    case 5: 
      setAlignSelf(YogaAlign.BASELINE);
      return;
    case 4: 
      setAlignSelf(YogaAlign.STRETCH);
      return;
    case 3: 
      setAlignSelf(YogaAlign.FLEX_END);
      return;
    case 2: 
      setAlignSelf(YogaAlign.CENTER);
      return;
    case 1: 
      setAlignSelf(YogaAlign.FLEX_START);
      return;
    }
    setAlignSelf(YogaAlign.AUTO);
  }
  
  public void setAspectRatio(float paramFloat)
  {
    setStyleAspectRatio(paramFloat);
  }
  
  public void setBorderWidths(int paramInt, float paramFloat)
  {
    if (isVirtual()) {
      return;
    }
    setBorder(maybeTransformLeftRightToStartEnd(ViewProps.BORDER_SPACING_TYPES[paramInt]), PixelUtil.toPixelFromDIP(paramFloat));
  }
  
  public void setCollapsable(boolean paramBoolean)
  {
    mCollapsable = paramBoolean;
  }
  
  public void setDisplay(String paramString)
  {
    if (isVirtual()) {
      return;
    }
    if (paramString == null)
    {
      setDisplay(YogaDisplay.FLEX);
      return;
    }
    int i = -1;
    int j = paramString.hashCode();
    if (j != 3145721)
    {
      if ((j == 3387192) && (paramString.equals("none"))) {
        i = 1;
      }
    }
    else if (paramString.equals("flex")) {
      i = 0;
    }
    switch (i)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("invalid value for display: ");
      localStringBuilder.append(paramString);
      throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
    case 1: 
      setDisplay(YogaDisplay.NONE);
      return;
    }
    setDisplay(YogaDisplay.FLEX);
  }
  
  public void setFlex(float paramFloat)
  {
    if (isVirtual()) {
      return;
    }
    super.setFlex(paramFloat);
  }
  
  public void setFlexBasis(Dynamic paramDynamic)
  {
    if (isVirtual()) {
      return;
    }
    mTempYogaValue.setFromDynamic(paramDynamic);
    switch (1.$SwitchMap$com$facebook$yoga$YogaUnit[mTempYogaValue.unit.ordinal()])
    {
    default: 
      break;
    case 4: 
      setFlexBasisPercent(mTempYogaValue.value);
      break;
    case 3: 
      setFlexBasisAuto();
      break;
    case 1: 
    case 2: 
      setFlexBasis(mTempYogaValue.value);
    }
    paramDynamic.recycle();
  }
  
  public void setFlexDirection(String paramString)
  {
    if (isVirtual()) {
      return;
    }
    if (paramString == null)
    {
      setFlexDirection(YogaFlexDirection.COLUMN);
      return;
    }
    int i = -1;
    int j = paramString.hashCode();
    if (j != -1448970769)
    {
      if (j != -1354837162)
      {
        if (j != 113114)
        {
          if ((j == 1272730475) && (paramString.equals("column-reverse"))) {
            i = 1;
          }
        }
        else if (paramString.equals("row")) {
          i = 2;
        }
      }
      else if (paramString.equals("column")) {
        i = 0;
      }
    }
    else if (paramString.equals("row-reverse")) {
      i = 3;
    }
    switch (i)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("invalid value for flexDirection: ");
      localStringBuilder.append(paramString);
      throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
    case 3: 
      setFlexDirection(YogaFlexDirection.ROW_REVERSE);
      return;
    case 2: 
      setFlexDirection(YogaFlexDirection.ALPHABETICAL);
      return;
    case 1: 
      setFlexDirection(YogaFlexDirection.COLUMN_REVERSE);
      return;
    }
    setFlexDirection(YogaFlexDirection.COLUMN);
  }
  
  public void setFlexGrow(float paramFloat)
  {
    if (isVirtual()) {
      return;
    }
    super.setFlexGrow(paramFloat);
  }
  
  public void setFlexShrink(float paramFloat)
  {
    if (isVirtual()) {
      return;
    }
    super.setFlexShrink(paramFloat);
  }
  
  public void setFlexWrap(String paramString)
  {
    if (isVirtual()) {
      return;
    }
    if (paramString == null)
    {
      setFlexWrap(YogaWrap.NO_WRAP);
      return;
    }
    int i = -1;
    int j = paramString.hashCode();
    if (j != -1039592053)
    {
      if (j != -749527969)
      {
        if ((j == 3657802) && (paramString.equals("wrap"))) {
          i = 1;
        }
      }
      else if (paramString.equals("wrap-reverse")) {
        i = 2;
      }
    }
    else if (paramString.equals("nowrap")) {
      i = 0;
    }
    switch (i)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("invalid value for flexWrap: ");
      localStringBuilder.append(paramString);
      throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
    case 2: 
      setFlexWrap(YogaWrap.WRAP_REVERSE);
      return;
    case 1: 
      setFlexWrap(YogaWrap.WRAP);
      return;
    }
    setFlexWrap(YogaWrap.NO_WRAP);
  }
  
  public void setHeight(Dynamic paramDynamic)
  {
    if (isVirtual()) {
      return;
    }
    mTempYogaValue.setFromDynamic(paramDynamic);
    switch (1.$SwitchMap$com$facebook$yoga$YogaUnit[mTempYogaValue.unit.ordinal()])
    {
    default: 
      break;
    case 4: 
      setStyleHeightPercent(mTempYogaValue.value);
      break;
    case 3: 
      setStyleHeightAuto();
      break;
    case 1: 
    case 2: 
      setStyleHeight(mTempYogaValue.value);
    }
    paramDynamic.recycle();
  }
  
  public void setJustifyContent(String paramString)
  {
    if (isVirtual()) {
      return;
    }
    if (paramString == null)
    {
      setJustifyContent(YogaJustify.FLEX_START);
      return;
    }
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      break;
    case 2055030478: 
      if (paramString.equals("space-evenly")) {
        i = 5;
      }
      break;
    case 1937124468: 
      if (paramString.equals("space-around")) {
        i = 4;
      }
      break;
    case 1742952711: 
      if (paramString.equals("flex-end")) {
        i = 2;
      }
      break;
    case 441309761: 
      if (paramString.equals("space-between")) {
        i = 3;
      }
      break;
    case -46581362: 
      if (paramString.equals("flex-start")) {
        i = 0;
      }
      break;
    case -1364013995: 
      if (paramString.equals("center")) {
        i = 1;
      }
      break;
    }
    switch (i)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("invalid value for justifyContent: ");
      localStringBuilder.append(paramString);
      throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
    case 5: 
      setJustifyContent(YogaJustify.SPACE_EVENLY);
      return;
    case 4: 
      setJustifyContent(YogaJustify.SPACE_AROUND);
      return;
    case 3: 
      setJustifyContent(YogaJustify.SPACE_BETWEEN);
      return;
    case 2: 
      setJustifyContent(YogaJustify.FLEX_END);
      return;
    case 1: 
      setJustifyContent(YogaJustify.CENTER);
      return;
    }
    setJustifyContent(YogaJustify.FLEX_START);
  }
  
  public void setMargins(int paramInt, Dynamic paramDynamic)
  {
    if (isVirtual()) {
      return;
    }
    paramInt = maybeTransformLeftRightToStartEnd(ViewProps.PADDING_MARGIN_SPACING_TYPES[paramInt]);
    mTempYogaValue.setFromDynamic(paramDynamic);
    switch (1.$SwitchMap$com$facebook$yoga$YogaUnit[mTempYogaValue.unit.ordinal()])
    {
    default: 
      break;
    case 4: 
      setMarginPercent(paramInt, mTempYogaValue.value);
      break;
    case 3: 
      setMarginAuto(paramInt);
      break;
    case 1: 
    case 2: 
      setMargin(paramInt, mTempYogaValue.value);
    }
    paramDynamic.recycle();
  }
  
  public void setMaxHeight(Dynamic paramDynamic)
  {
    if (isVirtual()) {
      return;
    }
    mTempYogaValue.setFromDynamic(paramDynamic);
    int i = 1.$SwitchMap$com$facebook$yoga$YogaUnit[mTempYogaValue.unit.ordinal()];
    if (i != 4) {
      switch (i)
      {
      default: 
        break;
      case 1: 
      case 2: 
        setStyleMaxHeight(mTempYogaValue.value);
        break;
      }
    } else {
      setStyleMaxHeightPercent(mTempYogaValue.value);
    }
    paramDynamic.recycle();
  }
  
  public void setMaxWidth(Dynamic paramDynamic)
  {
    if (isVirtual()) {
      return;
    }
    mTempYogaValue.setFromDynamic(paramDynamic);
    int i = 1.$SwitchMap$com$facebook$yoga$YogaUnit[mTempYogaValue.unit.ordinal()];
    if (i != 4) {
      switch (i)
      {
      default: 
        break;
      case 1: 
      case 2: 
        setStyleMaxWidth(mTempYogaValue.value);
        break;
      }
    } else {
      setStyleMaxWidthPercent(mTempYogaValue.value);
    }
    paramDynamic.recycle();
  }
  
  public void setMinHeight(Dynamic paramDynamic)
  {
    if (isVirtual()) {
      return;
    }
    mTempYogaValue.setFromDynamic(paramDynamic);
    int i = 1.$SwitchMap$com$facebook$yoga$YogaUnit[mTempYogaValue.unit.ordinal()];
    if (i != 4) {
      switch (i)
      {
      default: 
        break;
      case 1: 
      case 2: 
        setStyleMinHeight(mTempYogaValue.value);
        break;
      }
    } else {
      setStyleMinHeightPercent(mTempYogaValue.value);
    }
    paramDynamic.recycle();
  }
  
  public void setMinWidth(Dynamic paramDynamic)
  {
    if (isVirtual()) {
      return;
    }
    mTempYogaValue.setFromDynamic(paramDynamic);
    int i = 1.$SwitchMap$com$facebook$yoga$YogaUnit[mTempYogaValue.unit.ordinal()];
    if (i != 4) {
      switch (i)
      {
      default: 
        break;
      case 1: 
      case 2: 
        setStyleMinWidth(mTempYogaValue.value);
        break;
      }
    } else {
      setStyleMinWidthPercent(mTempYogaValue.value);
    }
    paramDynamic.recycle();
  }
  
  public void setOverflow(String paramString)
  {
    if (isVirtual()) {
      return;
    }
    if (paramString == null)
    {
      setOverflow(YogaOverflow.VISIBLE);
      return;
    }
    int i = -1;
    int j = paramString.hashCode();
    if (j != -1217487446)
    {
      if (j != -907680051)
      {
        if ((j == 466743410) && (paramString.equals("visible"))) {
          i = 0;
        }
      }
      else if (paramString.equals("scroll")) {
        i = 2;
      }
    }
    else if (paramString.equals("hidden")) {
      i = 1;
    }
    switch (i)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("invalid value for overflow: ");
      localStringBuilder.append(paramString);
      throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
    case 2: 
      setOverflow(YogaOverflow.SCROLL);
      return;
    case 1: 
      setOverflow(YogaOverflow.HIDDEN);
      return;
    }
    setOverflow(YogaOverflow.VISIBLE);
  }
  
  public void setPaddings(int paramInt, Dynamic paramDynamic)
  {
    if (isVirtual()) {
      return;
    }
    paramInt = maybeTransformLeftRightToStartEnd(ViewProps.PADDING_MARGIN_SPACING_TYPES[paramInt]);
    mTempYogaValue.setFromDynamic(paramDynamic);
    int i = 1.$SwitchMap$com$facebook$yoga$YogaUnit[mTempYogaValue.unit.ordinal()];
    if (i != 4) {
      switch (i)
      {
      default: 
        break;
      case 1: 
      case 2: 
        setPadding(paramInt, mTempYogaValue.value);
        break;
      }
    } else {
      setPaddingPercent(paramInt, mTempYogaValue.value);
    }
    paramDynamic.recycle();
  }
  
  public void setPosition(String paramString)
  {
    if (isVirtual()) {
      return;
    }
    if (paramString == null)
    {
      setPositionType(YogaPositionType.RELATIVE);
      return;
    }
    int i = -1;
    int j = paramString.hashCode();
    if (j != -554435892)
    {
      if ((j == 1728122231) && (paramString.equals("absolute"))) {
        i = 1;
      }
    }
    else if (paramString.equals("relative")) {
      i = 0;
    }
    switch (i)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("invalid value for position: ");
      localStringBuilder.append(paramString);
      throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
    case 1: 
      setPositionType(YogaPositionType.ABSOLUTE);
      return;
    }
    setPositionType(YogaPositionType.RELATIVE);
  }
  
  public void setPositionValues(int paramInt, Dynamic paramDynamic)
  {
    if (isVirtual()) {
      return;
    }
    paramInt = maybeTransformLeftRightToStartEnd(new int[] { 4, 5, 0, 2, 1, 3 }[paramInt]);
    mTempYogaValue.setFromDynamic(paramDynamic);
    int i = 1.$SwitchMap$com$facebook$yoga$YogaUnit[mTempYogaValue.unit.ordinal()];
    if (i != 4) {
      switch (i)
      {
      default: 
        break;
      case 1: 
      case 2: 
        setPosition(paramInt, mTempYogaValue.value);
        break;
      }
    } else {
      setPositionPercent(paramInt, mTempYogaValue.value);
    }
    paramDynamic.recycle();
  }
  
  public void setShouldNotifyOnLayout(boolean paramBoolean)
  {
    super.setShouldNotifyOnLayout(paramBoolean);
  }
  
  public void setWidth(Dynamic paramDynamic)
  {
    if (isVirtual()) {
      return;
    }
    mTempYogaValue.setFromDynamic(paramDynamic);
    switch (1.$SwitchMap$com$facebook$yoga$YogaUnit[mTempYogaValue.unit.ordinal()])
    {
    default: 
      break;
    case 4: 
      setStyleWidthPercent(mTempYogaValue.value);
      break;
    case 3: 
      setStyleWidthAuto();
      break;
    case 1: 
    case 2: 
      setStyleWidth(mTempYogaValue.value);
    }
    paramDynamic.recycle();
  }
  
  private static class MutableYogaValue
  {
    YogaUnit unit;
    float value;
    
    private MutableYogaValue() {}
    
    private MutableYogaValue(MutableYogaValue paramMutableYogaValue)
    {
      value = value;
      unit = unit;
    }
    
    void setFromDynamic(Dynamic paramDynamic)
    {
      if (paramDynamic.isNull())
      {
        unit = YogaUnit.UNDEFINED;
        value = NaN.0F;
        return;
      }
      if (paramDynamic.getType() == ReadableType.String)
      {
        paramDynamic = paramDynamic.asString();
        if (paramDynamic.equals("auto"))
        {
          unit = YogaUnit.AUTO;
          value = NaN.0F;
          return;
        }
        if (paramDynamic.endsWith("%"))
        {
          unit = YogaUnit.PERCENT;
          value = Float.parseFloat(paramDynamic.substring(0, paramDynamic.length() - 1));
          return;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Unknown value: ");
        localStringBuilder.append(paramDynamic);
        throw new IllegalArgumentException(localStringBuilder.toString());
      }
      unit = YogaUnit.POINT;
      value = PixelUtil.toPixelFromDIP(paramDynamic.asDouble());
    }
  }
}
