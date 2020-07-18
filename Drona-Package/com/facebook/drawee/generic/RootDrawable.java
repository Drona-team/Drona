package com.facebook.drawee.generic;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.drawee.drawable.ForwardingDrawable;
import com.facebook.drawee.drawable.VisibilityAwareDrawable;
import com.facebook.drawee.drawable.VisibilityCallback;
import javax.annotation.Nullable;

public class RootDrawable
  extends ForwardingDrawable
  implements VisibilityAwareDrawable
{
  @Nullable
  @VisibleForTesting
  Drawable mControllerOverlay = null;
  @Nullable
  private VisibilityCallback mVisibilityCallback;
  
  public RootDrawable(Drawable paramDrawable)
  {
    super(paramDrawable);
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (!isVisible()) {
      return;
    }
    if (mVisibilityCallback != null) {
      mVisibilityCallback.onDraw();
    }
    super.draw(paramCanvas);
    if (mControllerOverlay != null)
    {
      mControllerOverlay.setBounds(getBounds());
      mControllerOverlay.draw(paramCanvas);
    }
  }
  
  public int getIntrinsicHeight()
  {
    return -1;
  }
  
  public int getIntrinsicWidth()
  {
    return -1;
  }
  
  public void setControllerOverlay(Drawable paramDrawable)
  {
    mControllerOverlay = paramDrawable;
    invalidateSelf();
  }
  
  public void setVisibilityCallback(VisibilityCallback paramVisibilityCallback)
  {
    mVisibilityCallback = paramVisibilityCallback;
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (mVisibilityCallback != null) {
      mVisibilityCallback.onVisibilityChange(paramBoolean1);
    }
    return super.setVisible(paramBoolean1, paramBoolean2);
  }
}
