package com.google.android.exoplayer2.text;

import android.graphics.Typeface;
import android.view.accessibility.CaptioningManager.CaptionStyle;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class CaptionStyleCompat
{
  public static final CaptionStyleCompat DEFAULT = new CaptionStyleCompat(-1, -16777216, 0, 0, -1, null);
  public static final int EDGE_TYPE_DEPRESSED = 4;
  public static final int EDGE_TYPE_DROP_SHADOW = 2;
  public static final int EDGE_TYPE_NONE = 0;
  public static final int EDGE_TYPE_OUTLINE = 1;
  public static final int EDGE_TYPE_RAISED = 3;
  public static final int USE_TRACK_COLOR_SETTINGS = 1;
  public final int backgroundColor;
  public final int edgeColor;
  public final int edgeType;
  public final int foregroundColor;
  public final Typeface typeface;
  public final int windowColor;
  
  public CaptionStyleCompat(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Typeface paramTypeface)
  {
    foregroundColor = paramInt1;
    backgroundColor = paramInt2;
    windowColor = paramInt3;
    edgeType = paramInt4;
    edgeColor = paramInt5;
    typeface = paramTypeface;
  }
  
  public static CaptionStyleCompat createFromCaptionStyle(CaptioningManager.CaptionStyle paramCaptionStyle)
  {
    if (Util.SDK_INT >= 21) {
      return createFromCaptionStyleV21(paramCaptionStyle);
    }
    return createFromCaptionStyleV19(paramCaptionStyle);
  }
  
  private static CaptionStyleCompat createFromCaptionStyleV19(CaptioningManager.CaptionStyle paramCaptionStyle)
  {
    return new CaptionStyleCompat(foregroundColor, backgroundColor, 0, edgeType, edgeColor, paramCaptionStyle.getTypeface());
  }
  
  private static CaptionStyleCompat createFromCaptionStyleV21(CaptioningManager.CaptionStyle paramCaptionStyle)
  {
    if (paramCaptionStyle.hasForegroundColor()) {}
    for (int i = foregroundColor;; i = DEFAULTforegroundColor) {
      break;
    }
    if (paramCaptionStyle.hasBackgroundColor()) {}
    for (int j = backgroundColor;; j = DEFAULTbackgroundColor) {
      break;
    }
    if (paramCaptionStyle.hasWindowColor()) {}
    for (int k = windowColor;; k = DEFAULTwindowColor) {
      break;
    }
    if (paramCaptionStyle.hasEdgeType()) {}
    for (int m = edgeType;; m = DEFAULTedgeType) {
      break;
    }
    if (paramCaptionStyle.hasEdgeColor()) {}
    for (int n = edgeColor;; n = DEFAULTedgeColor) {
      break;
    }
    return new CaptionStyleCompat(i, j, k, m, n, paramCaptionStyle.getTypeface());
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface EdgeType {}
}
