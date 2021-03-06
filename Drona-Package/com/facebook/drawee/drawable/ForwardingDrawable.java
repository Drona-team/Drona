package com.facebook.drawee.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import javax.annotation.Nullable;

public class ForwardingDrawable
  extends Drawable
  implements Drawable.Callback, TransformCallback, TransformAwareDrawable, DrawableParent
{
  private static final Matrix sTempTransform = new Matrix();
  @Nullable
  private Drawable mCurrentDelegate;
  private final DrawableProperties mDrawableProperties = new DrawableProperties();
  protected TransformCallback mTransformCallback;
  
  public ForwardingDrawable(Drawable paramDrawable)
  {
    mCurrentDelegate = paramDrawable;
    DrawableUtils.setCallbacks(mCurrentDelegate, this, this);
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (mCurrentDelegate != null) {
      mCurrentDelegate.draw(paramCanvas);
    }
  }
  
  public Drawable.ConstantState getConstantState()
  {
    if (mCurrentDelegate == null) {
      return super.getConstantState();
    }
    return mCurrentDelegate.getConstantState();
  }
  
  public Drawable getCurrent()
  {
    return mCurrentDelegate;
  }
  
  public Drawable getDrawable()
  {
    return getCurrent();
  }
  
  public int getIntrinsicHeight()
  {
    if (mCurrentDelegate == null) {
      return super.getIntrinsicHeight();
    }
    return mCurrentDelegate.getIntrinsicHeight();
  }
  
  public int getIntrinsicWidth()
  {
    if (mCurrentDelegate == null) {
      return super.getIntrinsicWidth();
    }
    return mCurrentDelegate.getIntrinsicWidth();
  }
  
  public int getOpacity()
  {
    if (mCurrentDelegate == null) {
      return 0;
    }
    return mCurrentDelegate.getOpacity();
  }
  
  public boolean getPadding(Rect paramRect)
  {
    if (mCurrentDelegate == null) {
      return super.getPadding(paramRect);
    }
    return mCurrentDelegate.getPadding(paramRect);
  }
  
  protected void getParentTransform(Matrix paramMatrix)
  {
    if (mTransformCallback != null)
    {
      mTransformCallback.getTransform(paramMatrix);
      return;
    }
    paramMatrix.reset();
  }
  
  public void getRootBounds(RectF paramRectF)
  {
    if (mTransformCallback != null)
    {
      mTransformCallback.getRootBounds(paramRectF);
      return;
    }
    paramRectF.set(getBounds());
  }
  
  public void getTransform(Matrix paramMatrix)
  {
    getParentTransform(paramMatrix);
  }
  
  public void getTransformedBounds(RectF paramRectF)
  {
    getParentTransform(sTempTransform);
    paramRectF.set(getBounds());
    sTempTransform.mapRect(paramRectF);
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    invalidateSelf();
  }
  
  public boolean isStateful()
  {
    if (mCurrentDelegate == null) {
      return false;
    }
    return mCurrentDelegate.isStateful();
  }
  
  public Drawable mutate()
  {
    if (mCurrentDelegate != null) {
      mCurrentDelegate.mutate();
    }
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    if (mCurrentDelegate != null) {
      mCurrentDelegate.setBounds(paramRect);
    }
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    if (mCurrentDelegate == null) {
      return super.onLevelChange(paramInt);
    }
    return mCurrentDelegate.setLevel(paramInt);
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    if (mCurrentDelegate == null) {
      return super.onStateChange(paramArrayOfInt);
    }
    return mCurrentDelegate.setState(paramArrayOfInt);
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    scheduleSelf(paramRunnable, paramLong);
  }
  
  public void setAlpha(int paramInt)
  {
    mDrawableProperties.setAlpha(paramInt);
    if (mCurrentDelegate != null) {
      mCurrentDelegate.setAlpha(paramInt);
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    mDrawableProperties.setColorFilter(paramColorFilter);
    if (mCurrentDelegate != null) {
      mCurrentDelegate.setColorFilter(paramColorFilter);
    }
  }
  
  public Drawable setCurrent(Drawable paramDrawable)
  {
    paramDrawable = setCurrentWithoutInvalidate(paramDrawable);
    invalidateSelf();
    return paramDrawable;
  }
  
  protected Drawable setCurrentWithoutInvalidate(Drawable paramDrawable)
  {
    Drawable localDrawable = mCurrentDelegate;
    DrawableUtils.setCallbacks(localDrawable, null, null);
    DrawableUtils.setCallbacks(paramDrawable, null, null);
    DrawableUtils.setDrawableProperties(paramDrawable, mDrawableProperties);
    DrawableUtils.copyProperties(paramDrawable, this);
    DrawableUtils.setCallbacks(paramDrawable, this, this);
    mCurrentDelegate = paramDrawable;
    return localDrawable;
  }
  
  public void setDither(boolean paramBoolean)
  {
    mDrawableProperties.setDither(paramBoolean);
    if (mCurrentDelegate != null) {
      mCurrentDelegate.setDither(paramBoolean);
    }
  }
  
  public Drawable setDrawable(Drawable paramDrawable)
  {
    return setCurrent(paramDrawable);
  }
  
  public void setFilterBitmap(boolean paramBoolean)
  {
    mDrawableProperties.setFilterBitmap(paramBoolean);
    if (mCurrentDelegate != null) {
      mCurrentDelegate.setFilterBitmap(paramBoolean);
    }
  }
  
  public void setHotspot(float paramFloat1, float paramFloat2)
  {
    if (mCurrentDelegate != null) {
      mCurrentDelegate.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  public void setTransformCallback(TransformCallback paramTransformCallback)
  {
    mTransformCallback = paramTransformCallback;
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = super.setVisible(paramBoolean1, paramBoolean2);
    if (mCurrentDelegate == null) {
      return bool;
    }
    return mCurrentDelegate.setVisible(paramBoolean1, paramBoolean2);
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    unscheduleSelf(paramRunnable);
  }
}
