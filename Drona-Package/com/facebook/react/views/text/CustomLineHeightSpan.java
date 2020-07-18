package com.facebook.react.views.text;

import android.graphics.Paint.FontMetricsInt;
import android.text.style.LineHeightSpan;

public class CustomLineHeightSpan
  implements LineHeightSpan, ReactSpan
{
  private final int mHeight;
  
  CustomLineHeightSpan(float paramFloat)
  {
    mHeight = ((int)Math.ceil(paramFloat));
  }
  
  public void chooseHeight(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Paint.FontMetricsInt paramFontMetricsInt)
  {
    if (descent > mHeight)
    {
      paramInt1 = Math.min(mHeight, descent);
      descent = paramInt1;
      bottom = paramInt1;
      ascent = 0;
      top = 0;
      return;
    }
    if (-ascent + descent > mHeight)
    {
      bottom = descent;
      paramInt1 = -mHeight + descent;
      ascent = paramInt1;
      top = paramInt1;
      return;
    }
    if (-ascent + bottom > mHeight)
    {
      top = ascent;
      bottom = (ascent + mHeight);
      return;
    }
    if (-top + bottom > mHeight)
    {
      top = (bottom - mHeight);
      return;
    }
    paramInt1 = mHeight;
    paramInt2 = -top;
    paramInt3 = bottom;
    double d1 = top;
    double d2 = (paramInt1 - (paramInt2 + paramInt3)) / 2.0F;
    top = ((int)(d1 - Math.ceil(d2)));
    bottom = ((int)(bottom + Math.floor(d2)));
    ascent = top;
    descent = bottom;
  }
}
