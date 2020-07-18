package com.google.android.exoplayer2.text.ttml;

import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan.Standard;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import java.util.Map;

final class TtmlRenderUtil
{
  private TtmlRenderUtil() {}
  
  public static void applyStylesToSpan(SpannableStringBuilder paramSpannableStringBuilder, int paramInt1, int paramInt2, TtmlStyle paramTtmlStyle)
  {
    if (paramTtmlStyle.getStyle() != -1) {
      paramSpannableStringBuilder.setSpan(new StyleSpan(paramTtmlStyle.getStyle()), paramInt1, paramInt2, 33);
    }
    if (paramTtmlStyle.isLinethrough()) {
      paramSpannableStringBuilder.setSpan(new StrikethroughSpan(), paramInt1, paramInt2, 33);
    }
    if (paramTtmlStyle.isUnderline()) {
      paramSpannableStringBuilder.setSpan(new UnderlineSpan(), paramInt1, paramInt2, 33);
    }
    if (paramTtmlStyle.hasFontColor()) {
      paramSpannableStringBuilder.setSpan(new ForegroundColorSpan(paramTtmlStyle.getFontColor()), paramInt1, paramInt2, 33);
    }
    if (paramTtmlStyle.hasBackgroundColor()) {
      paramSpannableStringBuilder.setSpan(new BackgroundColorSpan(paramTtmlStyle.getBackgroundColor()), paramInt1, paramInt2, 33);
    }
    if (paramTtmlStyle.getFontFamily() != null) {
      paramSpannableStringBuilder.setSpan(new TypefaceSpan(paramTtmlStyle.getFontFamily()), paramInt1, paramInt2, 33);
    }
    if (paramTtmlStyle.getTextAlign() != null) {
      paramSpannableStringBuilder.setSpan(new AlignmentSpan.Standard(paramTtmlStyle.getTextAlign()), paramInt1, paramInt2, 33);
    }
    switch (paramTtmlStyle.getFontSizeUnit())
    {
    default: 
      return;
    case 3: 
      paramSpannableStringBuilder.setSpan(new RelativeSizeSpan(paramTtmlStyle.getFontSize() / 100.0F), paramInt1, paramInt2, 33);
      return;
    case 2: 
      paramSpannableStringBuilder.setSpan(new RelativeSizeSpan(paramTtmlStyle.getFontSize()), paramInt1, paramInt2, 33);
      return;
    }
    paramSpannableStringBuilder.setSpan(new AbsoluteSizeSpan((int)paramTtmlStyle.getFontSize(), true), paramInt1, paramInt2, 33);
  }
  
  static String applyTextElementSpacePolicy(String paramString)
  {
    return paramString.replaceAll("\r\n", "\n").replaceAll(" *\n *", "\n").replaceAll("\n", " ").replaceAll("[ \t\\x0B\f\r]+", " ");
  }
  
  static void endParagraph(SpannableStringBuilder paramSpannableStringBuilder)
  {
    int i = paramSpannableStringBuilder.length() - 1;
    while ((i >= 0) && (paramSpannableStringBuilder.charAt(i) == ' ')) {
      i -= 1;
    }
    if ((i >= 0) && (paramSpannableStringBuilder.charAt(i) != '\n')) {
      paramSpannableStringBuilder.append('\n');
    }
  }
  
  public static TtmlStyle resolveStyle(TtmlStyle paramTtmlStyle, String[] paramArrayOfString, Map paramMap)
  {
    if ((paramTtmlStyle == null) && (paramArrayOfString == null)) {
      return null;
    }
    int j = 0;
    int i = 0;
    if ((paramTtmlStyle == null) && (paramArrayOfString.length == 1)) {
      return (TtmlStyle)paramMap.get(paramArrayOfString[0]);
    }
    if ((paramTtmlStyle == null) && (paramArrayOfString.length > 1))
    {
      paramTtmlStyle = new TtmlStyle();
      j = paramArrayOfString.length;
      while (i < j)
      {
        paramTtmlStyle.chain((TtmlStyle)paramMap.get(paramArrayOfString[i]));
        i += 1;
      }
      return paramTtmlStyle;
    }
    if ((paramTtmlStyle != null) && (paramArrayOfString != null) && (paramArrayOfString.length == 1)) {
      return paramTtmlStyle.chain((TtmlStyle)paramMap.get(paramArrayOfString[0]));
    }
    if ((paramTtmlStyle != null) && (paramArrayOfString != null) && (paramArrayOfString.length > 1))
    {
      int k = paramArrayOfString.length;
      i = j;
      while (i < k)
      {
        paramTtmlStyle.chain((TtmlStyle)paramMap.get(paramArrayOfString[i]));
        i += 1;
      }
    }
    return paramTtmlStyle;
  }
}
