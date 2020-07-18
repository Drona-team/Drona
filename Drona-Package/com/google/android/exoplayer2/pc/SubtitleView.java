package com.google.android.exoplayer2.pc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.List;

public final class SubtitleView
  extends View
  implements TextOutput
{
  public static final float DEFAULT_BOTTOM_PADDING_FRACTION = 0.08F;
  public static final float DEFAULT_TEXT_SIZE_FRACTION = 0.0533F;
  private boolean applyEmbeddedFontSizes = true;
  private boolean applyEmbeddedStyles = true;
  private float bottomPaddingFraction = 0.08F;
  private List<Cue> cues;
  private final List<com.google.android.exoplayer2.ui.SubtitlePainter> painters = new ArrayList();
  private CaptionStyleCompat style = CaptionStyleCompat.DEFAULT;
  private float textSize = 0.0533F;
  private int textSizeType = 0;
  
  public SubtitleView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SubtitleView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private float getUserCaptionFontScaleV19()
  {
    return ((CaptioningManager)getContext().getSystemService("captioning")).getFontScale();
  }
  
  private CaptionStyleCompat getUserCaptionStyleV19()
  {
    return CaptionStyleCompat.createFromCaptionStyle(((CaptioningManager)getContext().getSystemService("captioning")).getUserStyle());
  }
  
  private boolean isCaptionManagerEnabled()
  {
    return ((CaptioningManager)getContext().getSystemService("captioning")).isEnabled();
  }
  
  private float resolveCueTextSize(Cue paramCue, int paramInt1, int paramInt2)
  {
    if (textSizeType != Integer.MIN_VALUE)
    {
      if (textSize == Float.MIN_VALUE) {
        return 0.0F;
      }
      return Math.max(resolveTextSize(textSizeType, textSize, paramInt1, paramInt2), 0.0F);
    }
    return 0.0F;
  }
  
  private float resolveTextSize(int paramInt1, float paramFloat, int paramInt2, int paramInt3)
  {
    switch (paramInt1)
    {
    default: 
      return Float.MIN_VALUE;
    case 2: 
      return paramFloat;
    case 1: 
      return paramFloat * paramInt2;
    }
    return paramFloat * paramInt3;
  }
  
  private void setTextSize(int paramInt, float paramFloat)
  {
    if ((textSizeType == paramInt) && (textSize == paramFloat)) {
      return;
    }
    textSizeType = paramInt;
    textSize = paramFloat;
    invalidate();
  }
  
  public void dispatchDraw(Canvas paramCanvas)
  {
    Object localObject = cues;
    int j = 0;
    int i;
    if (localObject == null) {
      i = 0;
    } else {
      i = cues.size();
    }
    int k = getHeight();
    int m = getPaddingLeft();
    int n = getPaddingTop();
    int i1 = getWidth() - getPaddingRight();
    int i2 = k - getPaddingBottom();
    if (i2 > n)
    {
      if (i1 <= m) {
        return;
      }
      int i3 = i2 - n;
      float f1 = resolveTextSize(textSizeType, textSize, k, i3);
      if (f1 <= 0.0F) {
        return;
      }
      while (j < i)
      {
        localObject = (Cue)cues.get(j);
        float f2 = resolveCueTextSize((Cue)localObject, k, i3);
        ((SubtitlePainter)painters.get(j)).draw((Cue)localObject, applyEmbeddedStyles, applyEmbeddedFontSizes, style, f1, f2, bottomPaddingFraction, paramCanvas, m, n, i1, i2);
        j += 1;
      }
    }
  }
  
  public void onCues(List paramList)
  {
    setCues(paramList);
  }
  
  public void setApplyEmbeddedFontSizes(boolean paramBoolean)
  {
    if (applyEmbeddedFontSizes == paramBoolean) {
      return;
    }
    applyEmbeddedFontSizes = paramBoolean;
    invalidate();
  }
  
  public void setApplyEmbeddedStyles(boolean paramBoolean)
  {
    if ((applyEmbeddedStyles == paramBoolean) && (applyEmbeddedFontSizes == paramBoolean)) {
      return;
    }
    applyEmbeddedStyles = paramBoolean;
    applyEmbeddedFontSizes = paramBoolean;
    invalidate();
  }
  
  public void setBottomPaddingFraction(float paramFloat)
  {
    if (bottomPaddingFraction == paramFloat) {
      return;
    }
    bottomPaddingFraction = paramFloat;
    invalidate();
  }
  
  public void setCues(List paramList)
  {
    if (cues == paramList) {
      return;
    }
    cues = paramList;
    int i;
    if (paramList == null) {
      i = 0;
    } else {
      i = paramList.size();
    }
    while (painters.size() < i) {
      painters.add(new SubtitlePainter(getContext()));
    }
    invalidate();
  }
  
  public void setFixedTextSize(int paramInt, float paramFloat)
  {
    Object localObject = getContext();
    if (localObject == null) {
      localObject = Resources.getSystem();
    } else {
      localObject = ((Context)localObject).getResources();
    }
    setTextSize(2, TypedValue.applyDimension(paramInt, paramFloat, ((Resources)localObject).getDisplayMetrics()));
  }
  
  public void setFractionalTextSize(float paramFloat)
  {
    setFractionalTextSize(paramFloat, false);
  }
  
  public void setFractionalTextSize(float paramFloat, boolean paramBoolean)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  public void setStyle(CaptionStyleCompat paramCaptionStyleCompat)
  {
    if (style == paramCaptionStyleCompat) {
      return;
    }
    style = paramCaptionStyleCompat;
    invalidate();
  }
  
  public void setUserDefaultStyle()
  {
    CaptionStyleCompat localCaptionStyleCompat;
    if ((Util.SDK_INT >= 19) && (isCaptionManagerEnabled()) && (!isInEditMode())) {
      localCaptionStyleCompat = getUserCaptionStyleV19();
    } else {
      localCaptionStyleCompat = CaptionStyleCompat.DEFAULT;
    }
    setStyle(localCaptionStyleCompat);
  }
  
  public void setUserDefaultTextSize()
  {
    float f;
    if ((Util.SDK_INT >= 19) && (!isInEditMode())) {
      f = getUserCaptionFontScaleV19();
    } else {
      f = 1.0F;
    }
    setFractionalTextSize(f * 0.0533F);
  }
}
