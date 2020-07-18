package com.facebook.drawee.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import com.facebook.common.internal.Preconditions;

public class ArrayDrawable
  extends Drawable
  implements Drawable.Callback, TransformCallback, TransformAwareDrawable
{
  private final DrawableParent[] mDrawableParents;
  private final DrawableProperties mDrawableProperties = new DrawableProperties();
  private boolean mIsMutated;
  private boolean mIsStateful;
  private boolean mIsStatefulCalculated;
  private final Drawable[] mLayers;
  private final Rect mTmpRect = new Rect();
  private TransformCallback mTransformCallback;
  
  public ArrayDrawable(Drawable[] paramArrayOfDrawable)
  {
    int i = 0;
    mIsStateful = false;
    mIsStatefulCalculated = false;
    mIsMutated = false;
    Preconditions.checkNotNull(paramArrayOfDrawable);
    mLayers = paramArrayOfDrawable;
    while (i < mLayers.length)
    {
      DrawableUtils.setCallbacks(mLayers[i], this, this);
      i += 1;
    }
    mDrawableParents = new DrawableParent[mLayers.length];
  }
  
  private DrawableParent createDrawableParentForIndex(final int paramInt)
  {
    new DrawableParent()
    {
      public Drawable getDrawable()
      {
        return getDrawable(paramInt);
      }
      
      public Drawable setDrawable(Drawable paramAnonymousDrawable)
      {
        return setDrawable(paramInt, paramAnonymousDrawable);
      }
    };
  }
  
  public void draw(Canvas paramCanvas)
  {
    int i = 0;
    while (i < mLayers.length)
    {
      Drawable localDrawable = mLayers[i];
      if (localDrawable != null) {
        localDrawable.draw(paramCanvas);
      }
      i += 1;
    }
  }
  
  public Drawable getDrawable(int paramInt)
  {
    boolean bool2 = false;
    if (paramInt >= 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    boolean bool1 = bool2;
    if (paramInt < mLayers.length) {
      bool1 = true;
    }
    Preconditions.checkArgument(bool1);
    return mLayers[paramInt];
  }
  
  public DrawableParent getDrawableParentForIndex(int paramInt)
  {
    boolean bool2 = false;
    if (paramInt >= 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    boolean bool1 = bool2;
    if (paramInt < mDrawableParents.length) {
      bool1 = true;
    }
    Preconditions.checkArgument(bool1);
    if (mDrawableParents[paramInt] == null) {
      mDrawableParents[paramInt] = createDrawableParentForIndex(paramInt);
    }
    return mDrawableParents[paramInt];
  }
  
  public int getIntrinsicHeight()
  {
    int i = 0;
    int k;
    for (int j = -1; i < mLayers.length; j = k)
    {
      Drawable localDrawable = mLayers[i];
      k = j;
      if (localDrawable != null) {
        k = Math.max(j, localDrawable.getIntrinsicHeight());
      }
      i += 1;
    }
    if (j > 0) {
      return j;
    }
    return -1;
  }
  
  public int getIntrinsicWidth()
  {
    int i = 0;
    int k;
    for (int j = -1; i < mLayers.length; j = k)
    {
      Drawable localDrawable = mLayers[i];
      k = j;
      if (localDrawable != null) {
        k = Math.max(j, localDrawable.getIntrinsicWidth());
      }
      i += 1;
    }
    if (j > 0) {
      return j;
    }
    return -1;
  }
  
  public int getNumberOfLayers()
  {
    return mLayers.length;
  }
  
  public int getOpacity()
  {
    if (mLayers.length == 0) {
      return -2;
    }
    int j = -1;
    int i = 1;
    while (i < mLayers.length)
    {
      Drawable localDrawable = mLayers[i];
      int k = j;
      if (localDrawable != null) {
        k = Drawable.resolveOpacity(j, localDrawable.getOpacity());
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  public boolean getPadding(Rect paramRect)
  {
    int i = 0;
    left = 0;
    top = 0;
    right = 0;
    bottom = 0;
    Rect localRect = mTmpRect;
    while (i < mLayers.length)
    {
      Drawable localDrawable = mLayers[i];
      if (localDrawable != null)
      {
        localDrawable.getPadding(localRect);
        left = Math.max(left, left);
        top = Math.max(top, top);
        right = Math.max(right, right);
        bottom = Math.max(bottom, bottom);
      }
      i += 1;
    }
    return true;
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
    if (mTransformCallback != null)
    {
      mTransformCallback.getTransform(paramMatrix);
      return;
    }
    paramMatrix.reset();
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    invalidateSelf();
  }
  
  public boolean isStateful()
  {
    if (!mIsStatefulCalculated)
    {
      mIsStateful = false;
      int i = 0;
      for (;;)
      {
        int j = mLayers.length;
        boolean bool1 = true;
        if (i >= j) {
          break;
        }
        Drawable localDrawable = mLayers[i];
        boolean bool2 = mIsStateful;
        if ((localDrawable == null) || (!localDrawable.isStateful())) {
          bool1 = false;
        }
        mIsStateful = (bool2 | bool1);
        i += 1;
      }
      mIsStatefulCalculated = true;
    }
    return mIsStateful;
  }
  
  public Drawable mutate()
  {
    int i = 0;
    while (i < mLayers.length)
    {
      Drawable localDrawable = mLayers[i];
      if (localDrawable != null) {
        localDrawable.mutate();
      }
      i += 1;
    }
    mIsMutated = true;
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    int i = 0;
    while (i < mLayers.length)
    {
      Drawable localDrawable = mLayers[i];
      if (localDrawable != null) {
        localDrawable.setBounds(paramRect);
      }
      i += 1;
    }
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    int i = 0;
    boolean bool2;
    for (boolean bool1 = false; i < mLayers.length; bool1 = bool2)
    {
      Drawable localDrawable = mLayers[i];
      bool2 = bool1;
      if (localDrawable != null)
      {
        bool2 = bool1;
        if (localDrawable.setLevel(paramInt)) {
          bool2 = true;
        }
      }
      i += 1;
    }
    return bool1;
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    int i = 0;
    boolean bool2;
    for (boolean bool1 = false; i < mLayers.length; bool1 = bool2)
    {
      Drawable localDrawable = mLayers[i];
      bool2 = bool1;
      if (localDrawable != null)
      {
        bool2 = bool1;
        if (localDrawable.setState(paramArrayOfInt)) {
          bool2 = true;
        }
      }
      i += 1;
    }
    return bool1;
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    scheduleSelf(paramRunnable, paramLong);
  }
  
  public void setAlpha(int paramInt)
  {
    mDrawableProperties.setAlpha(paramInt);
    int i = 0;
    while (i < mLayers.length)
    {
      Drawable localDrawable = mLayers[i];
      if (localDrawable != null) {
        localDrawable.setAlpha(paramInt);
      }
      i += 1;
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    mDrawableProperties.setColorFilter(paramColorFilter);
    int i = 0;
    while (i < mLayers.length)
    {
      Drawable localDrawable = mLayers[i];
      if (localDrawable != null) {
        localDrawable.setColorFilter(paramColorFilter);
      }
      i += 1;
    }
  }
  
  public void setDither(boolean paramBoolean)
  {
    mDrawableProperties.setDither(paramBoolean);
    int i = 0;
    while (i < mLayers.length)
    {
      Drawable localDrawable = mLayers[i];
      if (localDrawable != null) {
        localDrawable.setDither(paramBoolean);
      }
      i += 1;
    }
  }
  
  public Drawable setDrawable(int paramInt, Drawable paramDrawable)
  {
    boolean bool2 = true;
    boolean bool1;
    if (paramInt >= 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    if (paramInt < mLayers.length) {
      bool1 = bool2;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    Drawable localDrawable = mLayers[paramInt];
    if (paramDrawable != localDrawable)
    {
      if ((paramDrawable != null) && (mIsMutated)) {
        paramDrawable.mutate();
      }
      DrawableUtils.setCallbacks(mLayers[paramInt], null, null);
      DrawableUtils.setCallbacks(paramDrawable, null, null);
      DrawableUtils.setDrawableProperties(paramDrawable, mDrawableProperties);
      DrawableUtils.copyProperties(paramDrawable, this);
      DrawableUtils.setCallbacks(paramDrawable, this, this);
      mIsStatefulCalculated = false;
      mLayers[paramInt] = paramDrawable;
      invalidateSelf();
    }
    return localDrawable;
  }
  
  public void setFilterBitmap(boolean paramBoolean)
  {
    mDrawableProperties.setFilterBitmap(paramBoolean);
    int i = 0;
    while (i < mLayers.length)
    {
      Drawable localDrawable = mLayers[i];
      if (localDrawable != null) {
        localDrawable.setFilterBitmap(paramBoolean);
      }
      i += 1;
    }
  }
  
  public void setHotspot(float paramFloat1, float paramFloat2)
  {
    int i = 0;
    while (i < mLayers.length)
    {
      Drawable localDrawable = mLayers[i];
      if (localDrawable != null) {
        localDrawable.setHotspot(paramFloat1, paramFloat2);
      }
      i += 1;
    }
  }
  
  public void setTransformCallback(TransformCallback paramTransformCallback)
  {
    mTransformCallback = paramTransformCallback;
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = super.setVisible(paramBoolean1, paramBoolean2);
    int i = 0;
    while (i < mLayers.length)
    {
      Drawable localDrawable = mLayers[i];
      if (localDrawable != null) {
        localDrawable.setVisible(paramBoolean1, paramBoolean2);
      }
      i += 1;
    }
    return bool;
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    unscheduleSelf(paramRunnable);
  }
}
