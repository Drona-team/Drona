package com.facebook.react.views.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.text.style.ReplacementSpan;

public class TextInlineViewPlaceholderSpan
  extends ReplacementSpan
  implements ReactSpan
{
  private int mHeight;
  private int mReactTag;
  private int mWidth;
  
  public TextInlineViewPlaceholderSpan(int paramInt1, int paramInt2, int paramInt3)
  {
    mReactTag = paramInt1;
    mWidth = paramInt2;
    mHeight = paramInt3;
  }
  
  public void draw(Canvas paramCanvas, CharSequence paramCharSequence, int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, int paramInt5, Paint paramPaint) {}
  
  public int getHeight()
  {
    return mHeight;
  }
  
  public int getReactTag()
  {
    return mReactTag;
  }
  
  public int getSize(Paint paramPaint, CharSequence paramCharSequence, int paramInt1, int paramInt2, Paint.FontMetricsInt paramFontMetricsInt)
  {
    if (paramFontMetricsInt != null)
    {
      ascent = (-mHeight);
      descent = 0;
      top = ascent;
      bottom = 0;
    }
    return mWidth;
  }
  
  public int getWidth()
  {
    return mWidth;
  }
}
