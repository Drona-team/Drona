package com.google.android.exoplayer2.text;

import android.graphics.Bitmap;
import android.text.Layout.Alignment;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Cue
{
  public static final int ANCHOR_TYPE_END = 2;
  public static final int ANCHOR_TYPE_MIDDLE = 1;
  public static final int ANCHOR_TYPE_START = 0;
  public static final float DIMEN_UNSET = Float.MIN_VALUE;
  public static final int LINE_TYPE_FRACTION = 0;
  public static final int LINE_TYPE_NUMBER = 1;
  public static final int TEXT_SIZE_TYPE_ABSOLUTE = 2;
  public static final int TEXT_SIZE_TYPE_FRACTIONAL = 0;
  public static final int TEXT_SIZE_TYPE_FRACTIONAL_IGNORE_PADDING = 1;
  public static final int TYPE_UNSET = Integer.MIN_VALUE;
  public final Bitmap bitmap;
  public final float bitmapHeight;
  public final float line;
  public final int lineAnchor;
  public final int lineType;
  public final float position;
  public final int positionAnchor;
  public final float size;
  public final CharSequence text;
  public final Layout.Alignment textAlignment;
  public final float textSize;
  public final int textSizeType;
  public final int windowColor;
  public final boolean windowColorSet;
  
  public Cue(Bitmap paramBitmap, float paramFloat1, int paramInt1, float paramFloat2, int paramInt2, float paramFloat3, float paramFloat4)
  {
    this(null, null, paramBitmap, paramFloat2, 0, paramInt2, paramFloat1, paramInt1, Integer.MIN_VALUE, Float.MIN_VALUE, paramFloat3, paramFloat4, false, -16777216);
  }
  
  public Cue(CharSequence paramCharSequence)
  {
    this(paramCharSequence, null, Float.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Float.MIN_VALUE, Integer.MIN_VALUE, Float.MIN_VALUE);
  }
  
  public Cue(CharSequence paramCharSequence, Layout.Alignment paramAlignment, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, int paramInt3, float paramFloat3)
  {
    this(paramCharSequence, paramAlignment, paramFloat1, paramInt1, paramInt2, paramFloat2, paramInt3, paramFloat3, false, -16777216);
  }
  
  public Cue(CharSequence paramCharSequence, Layout.Alignment paramAlignment, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, int paramInt3, float paramFloat3, int paramInt4, float paramFloat4)
  {
    this(paramCharSequence, paramAlignment, null, paramFloat1, paramInt1, paramInt2, paramFloat2, paramInt3, paramInt4, paramFloat4, paramFloat3, Float.MIN_VALUE, false, -16777216);
  }
  
  public Cue(CharSequence paramCharSequence, Layout.Alignment paramAlignment, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, int paramInt3, float paramFloat3, boolean paramBoolean, int paramInt4)
  {
    this(paramCharSequence, paramAlignment, null, paramFloat1, paramInt1, paramInt2, paramFloat2, paramInt3, Integer.MIN_VALUE, Float.MIN_VALUE, paramFloat3, Float.MIN_VALUE, paramBoolean, paramInt4);
  }
  
  private Cue(CharSequence paramCharSequence, Layout.Alignment paramAlignment, Bitmap paramBitmap, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, int paramInt3, int paramInt4, float paramFloat3, float paramFloat4, float paramFloat5, boolean paramBoolean, int paramInt5)
  {
    text = paramCharSequence;
    textAlignment = paramAlignment;
    bitmap = paramBitmap;
    line = paramFloat1;
    lineType = paramInt1;
    lineAnchor = paramInt2;
    position = paramFloat2;
    positionAnchor = paramInt3;
    size = paramFloat4;
    bitmapHeight = paramFloat5;
    windowColorSet = paramBoolean;
    windowColor = paramInt5;
    textSizeType = paramInt4;
    textSize = paramFloat3;
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface AnchorType {}
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface LineType {}
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface TextSizeType {}
}
