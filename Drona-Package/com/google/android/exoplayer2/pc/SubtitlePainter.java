package com.google.android.exoplayer2.pc;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.util.Util;

final class SubtitlePainter
{
  private static final float INNER_PADDING_RATIO = 0.125F;
  private static final String PAGE_KEY = "SubtitlePainter";
  private boolean applyEmbeddedFontSizes;
  private boolean applyEmbeddedStyles;
  private int backgroundColor;
  private Rect bitmapRect;
  private float bottomPaddingFraction;
  private Bitmap cueBitmap;
  private float cueBitmapHeight;
  private float cueLine;
  private int cueLineAnchor;
  private int cueLineType;
  private float cuePosition;
  private int cuePositionAnchor;
  private float cueSize;
  private CharSequence cueText;
  private Layout.Alignment cueTextAlignment;
  private float cueTextSizePx;
  private float defaultTextSizePx;
  private int edgeColor;
  private int edgeType;
  private int foregroundColor;
  private final float outlineWidth;
  private final Paint paint;
  private int parentBottom;
  private int parentLeft;
  private int parentRight;
  private int parentTop;
  private final float shadowOffset;
  private final float shadowRadius;
  private final float spacingAdd;
  private final float spacingMult;
  private StaticLayout textLayout;
  private int textLeft;
  private int textPaddingX;
  private final TextPaint textPaint;
  private int textTop;
  private int windowColor;
  
  public SubtitlePainter(Context paramContext)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(null, new int[] { 16843287, 16843288 }, 0, 0);
    spacingAdd = localTypedArray.getDimensionPixelSize(0, 0);
    spacingMult = localTypedArray.getFloat(1, 1.0F);
    localTypedArray.recycle();
    float f = Math.round(getResourcesgetDisplayMetricsdensityDpi * 2.0F / 160.0F);
    outlineWidth = f;
    shadowRadius = f;
    shadowOffset = f;
    textPaint = new TextPaint();
    textPaint.setAntiAlias(true);
    textPaint.setSubpixelText(true);
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.FILL);
  }
  
  private static boolean areCharSequencesEqual(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    return (paramCharSequence1 == paramCharSequence2) || ((paramCharSequence1 != null) && (paramCharSequence1.equals(paramCharSequence2)));
  }
  
  private void drawBitmapLayout(Canvas paramCanvas)
  {
    paramCanvas.drawBitmap(cueBitmap, null, bitmapRect, null);
  }
  
  private void drawLayout(Canvas paramCanvas, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      drawTextLayout(paramCanvas);
      return;
    }
    drawBitmapLayout(paramCanvas);
  }
  
  private void drawTextLayout(Canvas paramCanvas)
  {
    StaticLayout localStaticLayout = textLayout;
    if (localStaticLayout == null) {
      return;
    }
    int m = paramCanvas.save();
    paramCanvas.translate(textLeft, textTop);
    if (Color.alpha(windowColor) > 0)
    {
      paint.setColor(windowColor);
      paramCanvas.drawRect(-textPaddingX, 0.0F, localStaticLayout.getWidth() + textPaddingX, localStaticLayout.getHeight(), paint);
    }
    int j = edgeType;
    int i = 1;
    if (j == 1)
    {
      textPaint.setStrokeJoin(Paint.Join.ROUND);
      textPaint.setStrokeWidth(outlineWidth);
      textPaint.setColor(edgeColor);
      textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
      localStaticLayout.draw(paramCanvas);
    }
    else if (edgeType == 2)
    {
      textPaint.setShadowLayer(shadowRadius, shadowOffset, shadowOffset, edgeColor);
    }
    else if ((edgeType == 3) || (edgeType == 4))
    {
      if (edgeType != 3) {
        i = 0;
      }
      int k = -1;
      if (i != 0) {
        j = -1;
      } else {
        j = edgeColor;
      }
      if (i != 0) {
        k = edgeColor;
      }
      float f1 = shadowRadius / 2.0F;
      textPaint.setColor(foregroundColor);
      textPaint.setStyle(Paint.Style.FILL);
      TextPaint localTextPaint = textPaint;
      float f2 = shadowRadius;
      float f3 = -f1;
      localTextPaint.setShadowLayer(f2, f3, f3, j);
      localStaticLayout.draw(paramCanvas);
      textPaint.setShadowLayer(shadowRadius, f1, f1, k);
    }
    textPaint.setColor(foregroundColor);
    textPaint.setStyle(Paint.Style.FILL);
    localStaticLayout.draw(paramCanvas);
    textPaint.setShadowLayer(0.0F, 0.0F, 0.0F, 0);
    paramCanvas.restoreToCount(m);
  }
  
  private void setupBitmapLayout()
  {
    int i = parentRight;
    int j = parentLeft;
    int k = parentBottom;
    int m = parentTop;
    float f2 = parentLeft;
    float f1 = i - j;
    float f3 = f2 + cuePosition * f1;
    f2 = parentTop;
    float f4 = k - m;
    f2 += cueLine * f4;
    j = Math.round(f1 * cueSize);
    if (cueBitmapHeight != Float.MIN_VALUE) {
      i = Math.round(f4 * cueBitmapHeight);
    } else {
      i = Math.round(j * (cueBitmap.getHeight() / cueBitmap.getWidth()));
    }
    if (cueLineAnchor == 2) {}
    for (f1 = j;; f1 = j / 2)
    {
      f1 = f3 - f1;
      break;
      f1 = f3;
      if (cueLineAnchor != 1) {
        break;
      }
    }
    k = Math.round(f1);
    if (cuePositionAnchor == 2) {}
    for (f1 = i;; f1 = i / 2)
    {
      f1 = f2 - f1;
      break;
      f1 = f2;
      if (cuePositionAnchor != 1) {
        break;
      }
    }
    m = Math.round(f1);
    bitmapRect = new Rect(k, m, j + k, i + m);
  }
  
  private void setupTextLayout()
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a11 = a10\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public void draw(Cue paramCue, boolean paramBoolean1, boolean paramBoolean2, CaptionStyleCompat paramCaptionStyleCompat, float paramFloat1, float paramFloat2, float paramFloat3, Canvas paramCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool;
    if (bitmap == null) {
      bool = true;
    } else {
      bool = false;
    }
    int i = -16777216;
    if (bool)
    {
      if (TextUtils.isEmpty(text)) {
        return;
      }
      if ((windowColorSet) && (paramBoolean1)) {
        i = windowColor;
      } else {
        i = windowColor;
      }
    }
    if ((areCharSequencesEqual(cueText, text)) && (Util.areEqual(cueTextAlignment, textAlignment)) && (cueBitmap == bitmap) && (cueLine == line) && (cueLineType == lineType) && (Util.areEqual(Integer.valueOf(cueLineAnchor), Integer.valueOf(lineAnchor))) && (cuePosition == position) && (Util.areEqual(Integer.valueOf(cuePositionAnchor), Integer.valueOf(positionAnchor))) && (cueSize == size) && (cueBitmapHeight == bitmapHeight) && (applyEmbeddedStyles == paramBoolean1) && (applyEmbeddedFontSizes == paramBoolean2) && (foregroundColor == foregroundColor) && (backgroundColor == backgroundColor) && (windowColor == i) && (edgeType == edgeType) && (edgeColor == edgeColor) && (Util.areEqual(textPaint.getTypeface(), typeface)) && (defaultTextSizePx == paramFloat1) && (cueTextSizePx == paramFloat2) && (bottomPaddingFraction == paramFloat3) && (parentLeft == paramInt1) && (parentTop == paramInt2) && (parentRight == paramInt3) && (parentBottom == paramInt4))
    {
      drawLayout(paramCanvas, bool);
      return;
    }
    cueText = text;
    cueTextAlignment = textAlignment;
    cueBitmap = bitmap;
    cueLine = line;
    cueLineType = lineType;
    cueLineAnchor = lineAnchor;
    cuePosition = position;
    cuePositionAnchor = positionAnchor;
    cueSize = size;
    cueBitmapHeight = bitmapHeight;
    applyEmbeddedStyles = paramBoolean1;
    applyEmbeddedFontSizes = paramBoolean2;
    foregroundColor = foregroundColor;
    backgroundColor = backgroundColor;
    windowColor = i;
    edgeType = edgeType;
    edgeColor = edgeColor;
    textPaint.setTypeface(typeface);
    defaultTextSizePx = paramFloat1;
    cueTextSizePx = paramFloat2;
    bottomPaddingFraction = paramFloat3;
    parentLeft = paramInt1;
    parentTop = paramInt2;
    parentRight = paramInt3;
    parentBottom = paramInt4;
    if (bool) {
      setupTextLayout();
    } else {
      setupBitmapLayout();
    }
    drawLayout(paramCanvas, bool);
  }
}
