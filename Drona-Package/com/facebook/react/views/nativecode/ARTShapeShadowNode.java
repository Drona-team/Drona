package com.facebook.react.views.nativecode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.ReactShadowNodeImpl;

public class ARTShapeShadowNode
  extends ARTVirtualNode
{
  private static final int CAP_BUTT = 0;
  private static final int CAP_ROUND = 1;
  private static final int CAP_SQUARE = 2;
  private static final int COLOR_TYPE_LINEAR_GRADIENT = 1;
  private static final int COLOR_TYPE_PATTERN = 3;
  private static final int COLOR_TYPE_RADIAL_GRADIENT = 2;
  private static final int COLOR_TYPE_SOLID_COLOR = 0;
  private static final int JOIN_BEVEL = 2;
  private static final int JOIN_MITER = 0;
  private static final int JOIN_ROUND = 1;
  private static final int PATH_TYPE_ARC = 4;
  private static final int PATH_TYPE_CLOSE = 1;
  private static final int PATH_TYPE_CURVETO = 3;
  private static final int PATH_TYPE_LINETO = 2;
  private static final int PATH_TYPE_MOVETO = 0;
  @Nullable
  private float[] mBrushData;
  @Nullable
  protected Path mPath;
  private int mStrokeCap = 1;
  @Nullable
  private float[] mStrokeColor;
  @Nullable
  private float[] mStrokeDash;
  private int mStrokeJoin = 1;
  private float mStrokeWidth = 1.0F;
  
  public ARTShapeShadowNode() {}
  
  private Path createPath(float[] paramArrayOfFloat)
  {
    Path localPath = new Path();
    localPath.moveTo(0.0F, 0.0F);
    int i = 0;
    while (i < paramArrayOfFloat.length)
    {
      int j = i + 1;
      i = (int)paramArrayOfFloat[i];
      float f4;
      float f5;
      float f6;
      int k;
      float f7;
      float f1;
      float f3;
      float f2;
      switch (i)
      {
      default: 
        paramArrayOfFloat = new StringBuilder();
        paramArrayOfFloat.append("Unrecognized drawing instruction ");
        paramArrayOfFloat.append(i);
        throw new JSApplicationIllegalArgumentException(paramArrayOfFloat.toString());
      case 4: 
        i = j + 1;
        f4 = paramArrayOfFloat[j] * mScale;
        j = i + 1;
        f5 = paramArrayOfFloat[i] * mScale;
        i = j + 1;
        f6 = paramArrayOfFloat[j] * mScale;
        k = i + 1;
        f7 = (float)Math.toDegrees(paramArrayOfFloat[i]);
        j = k + 1;
        f1 = (float)Math.toDegrees(paramArrayOfFloat[k]);
        if (paramArrayOfFloat[j] != 1.0F) {
          i = 1;
        } else {
          i = 0;
        }
        f1 -= f7;
        if (Math.abs(f1) >= 360.0F)
        {
          Path.Direction localDirection;
          if (i != 0) {
            localDirection = Path.Direction.CCW;
          } else {
            localDirection = Path.Direction.CW;
          }
          localPath.addCircle(f4, f5, f6, localDirection);
        }
        else
        {
          f3 = modulus(f1, 360.0F);
          f1 = f3;
          f2 = f1;
          if (i != 0)
          {
            f2 = f1;
            if (f3 < 360.0F) {
              f2 = (360.0F - f3) * -1.0F;
            }
          }
          localPath.arcTo(new RectF(f4 - f6, f5 - f6, f4 + f6, f5 + f6), f7, f2);
        }
        i = j + 1;
        break;
      case 3: 
        i = j + 1;
        f1 = paramArrayOfFloat[j];
        f2 = mScale;
        j = i + 1;
        f3 = paramArrayOfFloat[i];
        f4 = mScale;
        i = j + 1;
        f5 = paramArrayOfFloat[j];
        f6 = mScale;
        j = i + 1;
        f7 = paramArrayOfFloat[i];
        float f8 = mScale;
        i = j + 1;
        float f9 = paramArrayOfFloat[j];
        localPath.cubicTo(f1 * f2, f4 * f3, f6 * f5, f8 * f7, mScale * f9, paramArrayOfFloat[i] * mScale);
        i += 1;
        break;
      case 2: 
        k = j + 1;
        f1 = paramArrayOfFloat[j];
        f2 = mScale;
        i = k + 1;
        localPath.lineTo(f1 * f2, paramArrayOfFloat[k] * mScale);
        break;
      case 1: 
        localPath.close();
        i = j;
        break;
      case 0: 
        k = j + 1;
        f1 = paramArrayOfFloat[j];
        f2 = mScale;
        i = k + 1;
        localPath.moveTo(f1 * f2, paramArrayOfFloat[k] * mScale);
      }
    }
    return localPath;
  }
  
  private float modulus(float paramFloat1, float paramFloat2)
  {
    float f = paramFloat1 % paramFloat2;
    paramFloat1 = f;
    if (f < 0.0F) {
      paramFloat1 = f + paramFloat2;
    }
    return paramFloat1;
  }
  
  public void draw(Canvas paramCanvas, Paint paramPaint, float paramFloat)
  {
    paramFloat *= mOpacity;
    if (paramFloat > 0.01F)
    {
      saveAndSetupCanvas(paramCanvas);
      if (mPath != null)
      {
        if (setupFillPaint(paramPaint, paramFloat)) {
          paramCanvas.drawPath(mPath, paramPaint);
        }
        if (setupStrokePaint(paramPaint, paramFloat)) {
          paramCanvas.drawPath(mPath, paramPaint);
        }
        restoreCanvas(paramCanvas);
      }
      else
      {
        throw new JSApplicationIllegalArgumentException("Shapes should have a valid path (d) prop");
      }
    }
    markUpdateSeen();
  }
  
  public void setFill(ReadableArray paramReadableArray)
  {
    mBrushData = PropHelper.toFloatArray(paramReadableArray);
    markUpdated();
  }
  
  public void setShapePath(ReadableArray paramReadableArray)
  {
    mPath = createPath(PropHelper.toFloatArray(paramReadableArray));
    markUpdated();
  }
  
  public void setStroke(ReadableArray paramReadableArray)
  {
    mStrokeColor = PropHelper.toFloatArray(paramReadableArray);
    markUpdated();
  }
  
  public void setStrokeCap(int paramInt)
  {
    mStrokeCap = paramInt;
    markUpdated();
  }
  
  public void setStrokeDash(ReadableArray paramReadableArray)
  {
    mStrokeDash = PropHelper.toFloatArray(paramReadableArray);
    markUpdated();
  }
  
  public void setStrokeJoin(int paramInt)
  {
    mStrokeJoin = paramInt;
    markUpdated();
  }
  
  public void setStrokeWidth(float paramFloat)
  {
    mStrokeWidth = paramFloat;
    markUpdated();
  }
  
  protected boolean setupFillPaint(Paint paramPaint, float paramFloat)
  {
    float[] arrayOfFloat1 = mBrushData;
    int i = 0;
    if ((arrayOfFloat1 != null) && (mBrushData.length > 0))
    {
      paramPaint.reset();
      paramPaint.setFlags(1);
      paramPaint.setStyle(Paint.Style.FILL);
      int j = (int)mBrushData[0];
      switch (j)
      {
      default: 
        paramPaint = new StringBuilder();
        paramPaint.append("ART: Color type ");
        paramPaint.append(j);
        paramPaint.append(" not supported!");
        FLog.warn("ReactNative", paramPaint.toString());
      }
      for (;;)
      {
        return true;
        if (mBrushData.length < 5)
        {
          paramPaint = new StringBuilder();
          paramPaint.append("[ARTShapeShadowNode setupFillPaint] expects 5 elements, received ");
          paramPaint.append(mBrushData.length);
          FLog.warn("ReactNative", paramPaint.toString());
          return false;
        }
        paramFloat = mBrushData[1];
        float f1 = mScale;
        float f2 = mBrushData[2];
        float f3 = mScale;
        float f4 = mBrushData[3];
        float f5 = mScale;
        float f6 = mBrushData[4];
        float f7 = mScale;
        j = (mBrushData.length - 5) / 5;
        int[] arrayOfInt;
        if (j > 0)
        {
          arrayOfInt = new int[j];
          arrayOfFloat1 = new float[j];
          while (i < j)
          {
            arrayOfFloat1[i] = mBrushData[(j * 4 + 5 + i)];
            float[] arrayOfFloat2 = mBrushData;
            int k = i * 4 + 5;
            int m = (int)(arrayOfFloat2[(k + 0)] * 255.0F);
            int n = (int)(mBrushData[(k + 1)] * 255.0F);
            int i1 = (int)(mBrushData[(k + 2)] * 255.0F);
            arrayOfInt[i] = Color.argb((int)(mBrushData[(k + 3)] * 255.0F), m, n, i1);
            i += 1;
          }
        }
        else
        {
          arrayOfInt = null;
          arrayOfFloat1 = null;
        }
        paramPaint.setShader(new LinearGradient(paramFloat * f1, f2 * f3, f4 * f5, f6 * f7, arrayOfInt, arrayOfFloat1, Shader.TileMode.CLAMP));
        continue;
        if (mBrushData.length > 4) {
          paramFloat = mBrushData[4] * paramFloat * 255.0F;
        } else {
          paramFloat *= 255.0F;
        }
        paramPaint.setARGB((int)paramFloat, (int)(mBrushData[1] * 255.0F), (int)(mBrushData[2] * 255.0F), (int)(mBrushData[3] * 255.0F));
      }
    }
    return false;
  }
  
  protected boolean setupStrokePaint(Paint paramPaint, float paramFloat)
  {
    if ((mStrokeWidth != 0.0F) && (mStrokeColor != null))
    {
      if (mStrokeColor.length == 0) {
        return false;
      }
      paramPaint.reset();
      paramPaint.setFlags(1);
      paramPaint.setStyle(Paint.Style.STROKE);
      switch (mStrokeCap)
      {
      default: 
        paramPaint = new StringBuilder();
        paramPaint.append("strokeCap ");
        paramPaint.append(mStrokeCap);
        paramPaint.append(" unrecognized");
        throw new JSApplicationIllegalArgumentException(paramPaint.toString());
      case 2: 
        paramPaint.setStrokeCap(Paint.Cap.SQUARE);
        break;
      case 1: 
        paramPaint.setStrokeCap(Paint.Cap.ROUND);
        break;
      case 0: 
        paramPaint.setStrokeCap(Paint.Cap.BUTT);
      }
      switch (mStrokeJoin)
      {
      default: 
        paramPaint = new StringBuilder();
        paramPaint.append("strokeJoin ");
        paramPaint.append(mStrokeJoin);
        paramPaint.append(" unrecognized");
        throw new JSApplicationIllegalArgumentException(paramPaint.toString());
      case 2: 
        paramPaint.setStrokeJoin(Paint.Join.BEVEL);
        break;
      case 1: 
        paramPaint.setStrokeJoin(Paint.Join.ROUND);
        break;
      case 0: 
        paramPaint.setStrokeJoin(Paint.Join.MITER);
      }
      paramPaint.setStrokeWidth(mStrokeWidth * mScale);
      if (mStrokeColor.length > 3) {
        paramFloat = mStrokeColor[3] * paramFloat * 255.0F;
      } else {
        paramFloat *= 255.0F;
      }
      paramPaint.setARGB((int)paramFloat, (int)(mStrokeColor[0] * 255.0F), (int)(mStrokeColor[1] * 255.0F), (int)(mStrokeColor[2] * 255.0F));
      if ((mStrokeDash != null) && (mStrokeDash.length > 0))
      {
        paramPaint.setPathEffect(new DashPathEffect(mStrokeDash, 0.0F));
        return true;
      }
    }
    else
    {
      return false;
    }
    return true;
  }
}
