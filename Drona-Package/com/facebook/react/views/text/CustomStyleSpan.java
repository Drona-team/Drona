package com.facebook.react.views.text;

import android.content.res.AssetManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import androidx.annotation.Nullable;

public class CustomStyleSpan
  extends MetricAffectingSpan
  implements ReactSpan
{
  private final AssetManager mAssetManager;
  @Nullable
  private final String mFontFamily;
  private final int mStyle;
  private final int mWeight;
  
  public CustomStyleSpan(int paramInt1, int paramInt2, String paramString, AssetManager paramAssetManager)
  {
    mStyle = paramInt1;
    mWeight = paramInt2;
    mFontFamily = paramString;
    mAssetManager = paramAssetManager;
  }
  
  private static void apply(Paint paramPaint, int paramInt1, int paramInt2, String paramString, AssetManager paramAssetManager)
  {
    Typeface localTypeface2 = paramPaint.getTypeface();
    Typeface localTypeface1 = localTypeface2;
    int k = 0;
    int j;
    if (localTypeface2 == null) {
      j = 0;
    } else {
      j = localTypeface2.getStyle();
    }
    int i;
    if (paramInt2 != 1)
    {
      i = k;
      if ((j & 0x1) != 0)
      {
        i = k;
        if (paramInt2 != -1) {}
      }
    }
    else
    {
      i = 1;
    }
    if (paramInt1 != 2)
    {
      k = i;
      if ((j & 0x2) != 0)
      {
        k = i;
        if (paramInt1 != -1) {}
      }
    }
    else
    {
      k = i | 0x2;
    }
    if (paramString != null)
    {
      paramString = ReactFontManager.getInstance().getTypeface(paramString, k, paramInt2, paramAssetManager);
    }
    else
    {
      paramString = localTypeface1;
      if (localTypeface2 != null) {
        paramString = Typeface.create(localTypeface2, k);
      }
    }
    if (paramString != null) {
      paramPaint.setTypeface(paramString);
    } else {
      paramPaint.setTypeface(Typeface.defaultFromStyle(k));
    }
    paramPaint.setSubpixelText(true);
  }
  
  public String getFontFamily()
  {
    return mFontFamily;
  }
  
  public int getStyle()
  {
    if (mStyle == -1) {
      return 0;
    }
    return mStyle;
  }
  
  public int getWeight()
  {
    if (mWeight == -1) {
      return 0;
    }
    return mWeight;
  }
  
  public void updateDrawState(TextPaint paramTextPaint)
  {
    apply(paramTextPaint, mStyle, mWeight, mFontFamily, mAssetManager);
  }
  
  public void updateMeasureState(TextPaint paramTextPaint)
  {
    apply(paramTextPaint, mStyle, mWeight, mFontFamily, mAssetManager);
  }
}
