package com.facebook.drawee.drawable;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import javax.annotation.Nullable;

public class ScaleTypeDrawable
  extends ForwardingDrawable
{
  @VisibleForTesting
  Matrix mDrawMatrix;
  @Nullable
  @VisibleForTesting
  PointF mFocusPoint = null;
  @VisibleForTesting
  ScalingUtils.ScaleType mScaleType;
  @VisibleForTesting
  Object mScaleTypeState;
  private Matrix mTempMatrix = new Matrix();
  @VisibleForTesting
  int mUnderlyingHeight = 0;
  @VisibleForTesting
  int mUnderlyingWidth = 0;
  
  public ScaleTypeDrawable(Drawable paramDrawable, ScalingUtils.ScaleType paramScaleType)
  {
    super((Drawable)Preconditions.checkNotNull(paramDrawable));
    mScaleType = paramScaleType;
  }
  
  public ScaleTypeDrawable(Drawable paramDrawable, ScalingUtils.ScaleType paramScaleType, PointF paramPointF)
  {
    super((Drawable)Preconditions.checkNotNull(paramDrawable));
    mScaleType = paramScaleType;
    mFocusPoint = paramPointF;
  }
  
  private void configureBoundsIfUnderlyingChanged()
  {
    boolean bool = mScaleType instanceof ScalingUtils.StatefulScaleType;
    int k = 1;
    int i;
    if (bool)
    {
      Object localObject = ((ScalingUtils.StatefulScaleType)mScaleType).getState();
      if ((localObject != null) && (localObject.equals(mScaleTypeState))) {
        i = 0;
      } else {
        i = 1;
      }
      mScaleTypeState = localObject;
    }
    else
    {
      i = 0;
    }
    int j = k;
    if (mUnderlyingWidth == getCurrent().getIntrinsicWidth()) {
      if (mUnderlyingHeight != getCurrent().getIntrinsicHeight()) {
        j = k;
      } else {
        j = 0;
      }
    }
    if ((j != 0) || (i != 0)) {
      configureBounds();
    }
  }
  
  void configureBounds()
  {
    Object localObject = getCurrent();
    Rect localRect = getBounds();
    int i = localRect.width();
    int j = localRect.height();
    int k = ((Drawable)localObject).getIntrinsicWidth();
    mUnderlyingWidth = k;
    int m = ((Drawable)localObject).getIntrinsicHeight();
    mUnderlyingHeight = m;
    if ((k > 0) && (m > 0))
    {
      if ((k == i) && (m == j))
      {
        ((Drawable)localObject).setBounds(localRect);
        mDrawMatrix = null;
        return;
      }
      if (mScaleType == ScalingUtils.ScaleType.FIT_XY)
      {
        ((Drawable)localObject).setBounds(localRect);
        mDrawMatrix = null;
        return;
      }
      ((Drawable)localObject).setBounds(0, 0, k, m);
      localObject = mScaleType;
      Matrix localMatrix = mTempMatrix;
      float f1;
      if (mFocusPoint != null) {
        f1 = mFocusPoint.x;
      } else {
        f1 = 0.5F;
      }
      float f2;
      if (mFocusPoint != null) {
        f2 = mFocusPoint.y;
      } else {
        f2 = 0.5F;
      }
      ((ScalingUtils.ScaleType)localObject).getTransform(localMatrix, localRect, k, m, f1, f2);
      mDrawMatrix = mTempMatrix;
      return;
    }
    ((Drawable)localObject).setBounds(localRect);
    mDrawMatrix = null;
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
  
  public PointF getFocusPoint()
  {
    return mFocusPoint;
  }
  
  public ScalingUtils.ScaleType getScaleType()
  {
    return mScaleType;
  }
  
  public void getTransform(Matrix paramMatrix)
  {
    getParentTransform(paramMatrix);
    configureBoundsIfUnderlyingChanged();
    if (mDrawMatrix != null) {
      paramMatrix.preConcat(mDrawMatrix);
    }
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    configureBounds();
  }
  
  public Drawable setCurrent(Drawable paramDrawable)
  {
    paramDrawable = super.setCurrent(paramDrawable);
    configureBounds();
    return paramDrawable;
  }
  
  public void setFocusPoint(PointF paramPointF)
  {
    if (Objects.equal(mFocusPoint, paramPointF)) {
      return;
    }
    if (mFocusPoint == null) {
      mFocusPoint = new PointF();
    }
    mFocusPoint.set(paramPointF);
    configureBounds();
    invalidateSelf();
  }
  
  public void setScaleType(ScalingUtils.ScaleType paramScaleType)
  {
    if (Objects.equal(mScaleType, paramScaleType)) {
      return;
    }
    mScaleType = paramScaleType;
    mScaleTypeState = null;
    configureBounds();
    invalidateSelf();
  }
}
