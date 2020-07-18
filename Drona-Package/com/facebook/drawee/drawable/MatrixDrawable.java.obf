package com.facebook.drawee.drawable;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.facebook.common.internal.Preconditions;

public class MatrixDrawable
  extends ForwardingDrawable
{
  private Matrix mDrawMatrix;
  private Matrix mMatrix;
  private int mUnderlyingHeight = 0;
  private int mUnderlyingWidth = 0;
  
  public MatrixDrawable(Drawable paramDrawable, Matrix paramMatrix)
  {
    super((Drawable)Preconditions.checkNotNull(paramDrawable));
    mMatrix = paramMatrix;
  }
  
  private void configureBounds()
  {
    Drawable localDrawable = getCurrent();
    Rect localRect = getBounds();
    int i = localDrawable.getIntrinsicWidth();
    mUnderlyingWidth = i;
    int j = localDrawable.getIntrinsicHeight();
    mUnderlyingHeight = j;
    if ((i > 0) && (j > 0))
    {
      localDrawable.setBounds(0, 0, i, j);
      mDrawMatrix = mMatrix;
      return;
    }
    localDrawable.setBounds(localRect);
    mDrawMatrix = null;
  }
  
  private void configureBoundsIfUnderlyingChanged()
  {
    if ((mUnderlyingWidth != getCurrent().getIntrinsicWidth()) || (mUnderlyingHeight != getCurrent().getIntrinsicHeight())) {
      configureBounds();
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    configureBoundsIfUnderlyingChanged();
    if (mDrawMatrix != null)
    {
      int i = paramCanvas.save();
      paramCanvas.clipRect(getBounds());
      paramCanvas.concat(mDrawMatrix);
      super.draw(paramCanvas);
      paramCanvas.restoreToCount(i);
      return;
    }
    super.draw(paramCanvas);
  }
  
  public Matrix getMatrix()
  {
    return mMatrix;
  }
  
  public void getTransform(Matrix paramMatrix)
  {
    super.getTransform(paramMatrix);
    if (mDrawMatrix != null) {
      paramMatrix.preConcat(mDrawMatrix);
    }
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    super.onBoundsChange(paramRect);
    configureBounds();
  }
  
  public Drawable setCurrent(Drawable paramDrawable)
  {
    paramDrawable = super.setCurrent(paramDrawable);
    configureBounds();
    return paramDrawable;
  }
  
  public void setMatrix(Matrix paramMatrix)
  {
    mMatrix = paramMatrix;
    configureBounds();
    invalidateSelf();
  }
}
