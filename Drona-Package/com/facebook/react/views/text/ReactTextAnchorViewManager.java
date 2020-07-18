package com.facebook.react.views.text;

import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.widget.TextView;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.yoga.YogaConstants;

public abstract class ReactTextAnchorViewManager<T extends View, C extends ReactBaseTextShadowNode>
  extends BaseViewManager<T, C>
{
  private static final int[] SPACING_TYPES = { 8, 0, 2, 1, 3 };
  
  public ReactTextAnchorViewManager() {}
  
  public void setBorderColor(ReactTextView paramReactTextView, int paramInt, Integer paramInteger)
  {
    float f2 = NaN.0F;
    float f1;
    if (paramInteger == null) {
      f1 = NaN.0F;
    } else {
      f1 = paramInteger.intValue() & 0xFFFFFF;
    }
    if (paramInteger != null) {
      f2 = paramInteger.intValue() >>> 24;
    }
    paramReactTextView.setBorderColor(SPACING_TYPES[paramInt], f1, f2);
  }
  
  public void setBorderRadius(ReactTextView paramReactTextView, int paramInt, float paramFloat)
  {
    float f = paramFloat;
    if (!YogaConstants.isUndefined(paramFloat)) {
      f = PixelUtil.toPixelFromDIP(paramFloat);
    }
    if (paramInt == 0)
    {
      paramReactTextView.setBorderRadius(f);
      return;
    }
    paramReactTextView.setBorderRadius(f, paramInt - 1);
  }
  
  public void setBorderStyle(ReactTextView paramReactTextView, String paramString)
  {
    paramReactTextView.setBorderStyle(paramString);
  }
  
  public void setBorderWidth(ReactTextView paramReactTextView, int paramInt, float paramFloat)
  {
    float f = paramFloat;
    if (!YogaConstants.isUndefined(paramFloat)) {
      f = PixelUtil.toPixelFromDIP(paramFloat);
    }
    paramReactTextView.setBorderWidth(SPACING_TYPES[paramInt], f);
  }
  
  public void setDataDetectorType(ReactTextView paramReactTextView, String paramString)
  {
    switch (paramString.hashCode())
    {
    default: 
      break;
    case 96619420: 
      if (paramString.equals("email")) {
        i = 2;
      }
      break;
    case 3387192: 
      if (paramString.equals("none")) {
        i = 4;
      }
      break;
    case 3321850: 
      if (paramString.equals("link")) {
        i = 1;
      }
      break;
    case 96673: 
      if (paramString.equals("all")) {
        i = 3;
      }
      break;
    case -1192969641: 
      if (paramString.equals("phoneNumber")) {
        i = 0;
      }
      break;
    }
    int i = -1;
    switch (i)
    {
    default: 
      paramReactTextView.setLinkifyMask(0);
      return;
    case 3: 
      paramReactTextView.setLinkifyMask(15);
      return;
    case 2: 
      paramReactTextView.setLinkifyMask(2);
      return;
    case 1: 
      paramReactTextView.setLinkifyMask(1);
      return;
    }
    paramReactTextView.setLinkifyMask(4);
  }
  
  public void setDisabled(ReactTextView paramReactTextView, boolean paramBoolean)
  {
    paramReactTextView.setEnabled(paramBoolean ^ true);
  }
  
  public void setEllipsizeMode(ReactTextView paramReactTextView, String paramString)
  {
    if ((paramString != null) && (!paramString.equals("tail")))
    {
      if (paramString.equals("head"))
      {
        paramReactTextView.setEllipsizeLocation(TextUtils.TruncateAt.START);
        return;
      }
      if (paramString.equals("middle"))
      {
        paramReactTextView.setEllipsizeLocation(TextUtils.TruncateAt.MIDDLE);
        return;
      }
      if (paramString.equals("clip"))
      {
        paramReactTextView.setEllipsizeLocation(null);
        return;
      }
      paramReactTextView = new StringBuilder();
      paramReactTextView.append("Invalid ellipsizeMode: ");
      paramReactTextView.append(paramString);
      throw new JSApplicationIllegalArgumentException(paramReactTextView.toString());
    }
    paramReactTextView.setEllipsizeLocation(TextUtils.TruncateAt.END);
  }
  
  public void setIncludeFontPadding(ReactTextView paramReactTextView, boolean paramBoolean)
  {
    paramReactTextView.setIncludeFontPadding(paramBoolean);
  }
  
  public void setNotifyOnInlineViewLayout(ReactTextView paramReactTextView, boolean paramBoolean)
  {
    paramReactTextView.setNotifyOnInlineViewLayout(paramBoolean);
  }
  
  public void setNumberOfLines(ReactTextView paramReactTextView, int paramInt)
  {
    paramReactTextView.setNumberOfLines(paramInt);
  }
  
  public void setSelectable(ReactTextView paramReactTextView, boolean paramBoolean)
  {
    paramReactTextView.setTextIsSelectable(paramBoolean);
  }
  
  public void setSelectionColor(ReactTextView paramReactTextView, Integer paramInteger)
  {
    if (paramInteger == null)
    {
      paramReactTextView.setHighlightColor(DefaultStyleValuesUtil.getDefaultTextColorHighlight(paramReactTextView.getContext()));
      return;
    }
    paramReactTextView.setHighlightColor(paramInteger.intValue());
  }
  
  public void setTextAlignVertical(ReactTextView paramReactTextView, String paramString)
  {
    if ((paramString != null) && (!"auto".equals(paramString)))
    {
      if ("top".equals(paramString))
      {
        paramReactTextView.setGravityVertical(48);
        return;
      }
      if ("bottom".equals(paramString))
      {
        paramReactTextView.setGravityVertical(80);
        return;
      }
      if ("center".equals(paramString))
      {
        paramReactTextView.setGravityVertical(16);
        return;
      }
      paramReactTextView = new StringBuilder();
      paramReactTextView.append("Invalid textAlignVertical: ");
      paramReactTextView.append(paramString);
      throw new JSApplicationIllegalArgumentException(paramReactTextView.toString());
    }
    paramReactTextView.setGravityVertical(0);
  }
}
