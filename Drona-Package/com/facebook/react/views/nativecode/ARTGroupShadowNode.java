package com.facebook.react.views.nativecode;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.ReactShadowNodeImpl;

public class ARTGroupShadowNode
  extends ARTVirtualNode
{
  @Nullable
  protected RectF mClipping;
  
  public ARTGroupShadowNode() {}
  
  private static RectF createClipping(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat.length == 4) {
      return new RectF(paramArrayOfFloat[0], paramArrayOfFloat[1], paramArrayOfFloat[0] + paramArrayOfFloat[2], paramArrayOfFloat[1] + paramArrayOfFloat[3]);
    }
    throw new JSApplicationIllegalArgumentException("Clipping should be array of length 4 (e.g. [x, y, width, height])");
  }
  
  public void draw(Canvas paramCanvas, Paint paramPaint, float paramFloat)
  {
    paramFloat *= mOpacity;
    if (paramFloat > 0.01F)
    {
      saveAndSetupCanvas(paramCanvas);
      if (mClipping != null) {
        paramCanvas.clipRect(mClipping.left * mScale, mClipping.top * mScale, mClipping.right * mScale, mClipping.bottom * mScale);
      }
      int i = 0;
      while (i < getChildCount())
      {
        ARTVirtualNode localARTVirtualNode = (ARTVirtualNode)getChildAt(i);
        localARTVirtualNode.draw(paramCanvas, paramPaint, paramFloat);
        localARTVirtualNode.markUpdateSeen();
        i += 1;
      }
      restoreCanvas(paramCanvas);
    }
  }
  
  public boolean isVirtual()
  {
    return true;
  }
  
  public void setClipping(ReadableArray paramReadableArray)
  {
    paramReadableArray = PropHelper.toFloatArray(paramReadableArray);
    if (paramReadableArray != null)
    {
      mClipping = createClipping(paramReadableArray);
      markUpdated();
    }
  }
}
