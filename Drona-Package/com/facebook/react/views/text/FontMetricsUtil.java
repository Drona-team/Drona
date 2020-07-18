package com.facebook.react.views.text;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

public class FontMetricsUtil
{
  private static final float AMPLIFICATION_FACTOR = 100.0F;
  private static final String CAP_HEIGHT_MEASUREMENT_TEXT = "T";
  private static final String X_HEIGHT_MEASUREMENT_TEXT = "x";
  
  public FontMetricsUtil() {}
  
  public static WritableArray getFontMetrics(CharSequence paramCharSequence, Layout paramLayout, TextPaint paramTextPaint, Context paramContext)
  {
    paramContext = paramContext.getResources().getDisplayMetrics();
    WritableArray localWritableArray = Arguments.createArray();
    paramTextPaint = new TextPaint(paramTextPaint);
    paramTextPaint.setTextSize(paramTextPaint.getTextSize() * 100.0F);
    Object localObject = new Rect();
    int j = "T".length();
    int i = 0;
    paramTextPaint.getTextBounds("T", 0, j, (Rect)localObject);
    double d1 = ((Rect)localObject).height() / 100.0F / density;
    localObject = new Rect();
    paramTextPaint.getTextBounds("x", 0, "x".length(), (Rect)localObject);
    double d2 = ((Rect)localObject).height() / 100.0F / density;
    while (i < paramLayout.getLineCount())
    {
      paramTextPaint = new Rect();
      paramLayout.getLineBounds(i, paramTextPaint);
      localObject = Arguments.createMap();
      ((WritableMap)localObject).putDouble("x", paramLayout.getLineLeft(i) / density);
      ((WritableMap)localObject).putDouble("y", top / density);
      ((WritableMap)localObject).putDouble("width", paramLayout.getLineWidth(i) / density);
      ((WritableMap)localObject).putDouble("height", paramTextPaint.height() / density);
      ((WritableMap)localObject).putDouble("descender", paramLayout.getLineDescent(i) / density);
      ((WritableMap)localObject).putDouble("ascender", -paramLayout.getLineAscent(i) / density);
      ((WritableMap)localObject).putDouble("baseline", paramLayout.getLineBaseline(i) / density);
      ((WritableMap)localObject).putDouble("capHeight", d1);
      ((WritableMap)localObject).putDouble("xHeight", d2);
      ((WritableMap)localObject).putString("text", paramCharSequence.subSequence(paramLayout.getLineStart(i), paramLayout.getLineEnd(i)).toString());
      localWritableArray.pushMap((ReadableMap)localObject);
      i += 1;
    }
    return localWritableArray;
  }
}