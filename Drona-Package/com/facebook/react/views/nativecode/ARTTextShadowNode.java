package com.facebook.react.views.nativecode;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ReactShadowNodeImpl;

public class ARTTextShadowNode
  extends ARTShapeShadowNode
{
  private static final int DEFAULT_FONT_SIZE = 12;
  private static final String PROP_FONT = "font";
  private static final String PROP_FONT_FAMILY = "fontFamily";
  private static final String PROP_FONT_SIZE = "fontSize";
  private static final String PROP_FONT_STYLE = "fontStyle";
  private static final String PROP_FONT_WEIGHT = "fontWeight";
  private static final String PROP_LINES = "lines";
  private static final int TEXT_ALIGNMENT_CENTER = 2;
  private static final int TEXT_ALIGNMENT_LEFT = 0;
  private static final int TEXT_ALIGNMENT_RIGHT = 1;
  @Nullable
  private ReadableMap mFrame;
  private int mTextAlignment = 0;
  
  public ARTTextShadowNode() {}
  
  private void applyTextPropertiesToPaint(Paint paramPaint)
  {
    switch (mTextAlignment)
    {
    default: 
      break;
    case 2: 
      paramPaint.setTextAlign(Paint.Align.CENTER);
      break;
    case 1: 
      paramPaint.setTextAlign(Paint.Align.RIGHT);
      break;
    case 0: 
      paramPaint.setTextAlign(Paint.Align.LEFT);
    }
    if ((mFrame != null) && (mFrame.hasKey("font")))
    {
      ReadableMap localReadableMap = mFrame.getMap("font");
      if (localReadableMap != null)
      {
        float f = 12.0F;
        if (localReadableMap.hasKey("fontSize")) {
          f = (float)localReadableMap.getDouble("fontSize");
        }
        paramPaint.setTextSize(f * mScale);
        boolean bool = localReadableMap.hasKey("fontWeight");
        int k = 1;
        int i;
        if ((bool) && ("bold".equals(localReadableMap.getString("fontWeight")))) {
          i = 1;
        } else {
          i = 0;
        }
        int j;
        if ((localReadableMap.hasKey("fontStyle")) && ("italic".equals(localReadableMap.getString("fontStyle")))) {
          j = 1;
        } else {
          j = 0;
        }
        if ((i != 0) && (j != 0)) {
          i = 3;
        } else if (i != 0) {
          i = k;
        } else if (j != 0) {
          i = 2;
        } else {
          i = 0;
        }
        paramPaint.setTypeface(Typeface.create(localReadableMap.getString("fontFamily"), i));
      }
    }
  }
  
  public void draw(Canvas paramCanvas, Paint paramPaint, float paramFloat)
  {
    if (mFrame == null) {
      return;
    }
    paramFloat *= mOpacity;
    if (paramFloat <= 0.01F) {
      return;
    }
    if (!mFrame.hasKey("lines")) {
      return;
    }
    Object localObject = mFrame.getArray("lines");
    if (localObject != null)
    {
      if (((ReadableArray)localObject).size() == 0) {
        return;
      }
      saveAndSetupCanvas(paramCanvas);
      String[] arrayOfString = new String[((ReadableArray)localObject).size()];
      int i = 0;
      while (i < arrayOfString.length)
      {
        arrayOfString[i] = ((ReadableArray)localObject).getString(i);
        i += 1;
      }
      localObject = TextUtils.join("\n", arrayOfString);
      if (setupStrokePaint(paramPaint, paramFloat))
      {
        applyTextPropertiesToPaint(paramPaint);
        if (mPath == null) {
          paramCanvas.drawText((String)localObject, 0.0F, -paramPaint.ascent(), paramPaint);
        } else {
          paramCanvas.drawTextOnPath((String)localObject, mPath, 0.0F, 0.0F, paramPaint);
        }
      }
      if (setupFillPaint(paramPaint, paramFloat))
      {
        applyTextPropertiesToPaint(paramPaint);
        if (mPath == null) {
          paramCanvas.drawText((String)localObject, 0.0F, -paramPaint.ascent(), paramPaint);
        } else {
          paramCanvas.drawTextOnPath((String)localObject, mPath, 0.0F, 0.0F, paramPaint);
        }
      }
      restoreCanvas(paramCanvas);
      markUpdateSeen();
    }
  }
  
  public void setAlignment(int paramInt)
  {
    mTextAlignment = paramInt;
  }
  
  public void setFrame(ReadableMap paramReadableMap)
  {
    mFrame = paramReadableMap;
  }
}
