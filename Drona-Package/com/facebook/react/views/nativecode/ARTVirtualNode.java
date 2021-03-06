package com.facebook.react.views.nativecode;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.DisplayMetricsHolder;
import com.facebook.react.uimanager.ReactShadowNodeImpl;

public abstract class ARTVirtualNode
  extends ReactShadowNodeImpl
{
  protected static final float MIN_OPACITY_FOR_DRAW = 0.01F;
  private static final float[] sMatrixData = new float[9];
  private static final float[] sRawMatrix = new float[9];
  @Nullable
  private Matrix mMatrix = new Matrix();
  protected float mOpacity = 1.0F;
  protected final float mScale = getWindowDisplayMetricsdensity;
  
  public ARTVirtualNode() {}
  
  public abstract void draw(Canvas paramCanvas, Paint paramPaint, float paramFloat);
  
  public boolean isVirtual()
  {
    return true;
  }
  
  protected void restoreCanvas(Canvas paramCanvas)
  {
    paramCanvas.restore();
  }
  
  protected final void saveAndSetupCanvas(Canvas paramCanvas)
  {
    paramCanvas.save();
    if (mMatrix != null) {
      paramCanvas.concat(mMatrix);
    }
  }
  
  public void setOpacity(float paramFloat)
  {
    mOpacity = paramFloat;
    markUpdated();
  }
  
  public void setTransform(ReadableArray paramReadableArray)
  {
    if (paramReadableArray != null)
    {
      int i = PropHelper.toFloatArray(paramReadableArray, sMatrixData);
      if (i == 6) {
        setupMatrix();
      } else if (i != -1) {
        throw new JSApplicationIllegalArgumentException("Transform matrices must be of size 6");
      }
    }
    else
    {
      mMatrix = null;
    }
    markUpdated();
  }
  
  protected void setupMatrix()
  {
    sRawMatrix[0] = sMatrixData[0];
    sRawMatrix[1] = sMatrixData[2];
    sRawMatrix[2] = (sMatrixData[4] * mScale);
    sRawMatrix[3] = sMatrixData[1];
    sRawMatrix[4] = sMatrixData[3];
    sMatrixData[5] *= mScale;
    sRawMatrix[6] = 0.0F;
    sRawMatrix[7] = 0.0F;
    sRawMatrix[8] = 1.06535322E9F;
    if (mMatrix == null) {
      mMatrix = new Matrix();
    }
    mMatrix.setValues(sRawMatrix);
  }
}
