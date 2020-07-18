package androidx.core.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;

class WrappedDrawableApi14
  extends Drawable
  implements Drawable.Callback, WrappedDrawable, TintAwareDrawable
{
  static final PorterDuff.Mode DEFAULT_TINT_MODE = PorterDuff.Mode.SRC_IN;
  private boolean mColorFilterSet;
  private int mCurrentColor;
  private PorterDuff.Mode mCurrentMode;
  Drawable mDrawable;
  private boolean mMutated;
  WrappedDrawableState mState;
  
  WrappedDrawableApi14(Drawable paramDrawable)
  {
    mState = mutateConstantState();
    setWrappedDrawable(paramDrawable);
  }
  
  WrappedDrawableApi14(WrappedDrawableState paramWrappedDrawableState, Resources paramResources)
  {
    mState = paramWrappedDrawableState;
    updateLocalState(paramResources);
  }
  
  private WrappedDrawableState mutateConstantState()
  {
    return new WrappedDrawableState(mState);
  }
  
  private void updateLocalState(Resources paramResources)
  {
    if ((mState != null) && (mState.mDrawableState != null)) {
      setWrappedDrawable(mState.mDrawableState.newDrawable(paramResources));
    }
  }
  
  private boolean updateTint(int[] paramArrayOfInt)
  {
    if (!isCompatTintEnabled()) {
      return false;
    }
    ColorStateList localColorStateList = mState.mTint;
    PorterDuff.Mode localMode = mState.mTintMode;
    if ((localColorStateList != null) && (localMode != null))
    {
      int i = localColorStateList.getColorForState(paramArrayOfInt, localColorStateList.getDefaultColor());
      if ((!mColorFilterSet) || (i != mCurrentColor) || (localMode != mCurrentMode))
      {
        setColorFilter(i, localMode);
        mCurrentColor = i;
        mCurrentMode = localMode;
        mColorFilterSet = true;
        return true;
      }
    }
    else
    {
      mColorFilterSet = false;
      clearColorFilter();
    }
    return false;
  }
  
  public void draw(Canvas paramCanvas)
  {
    mDrawable.draw(paramCanvas);
  }
  
  public int getChangingConfigurations()
  {
    int j = super.getChangingConfigurations();
    int i;
    if (mState != null) {
      i = mState.getChangingConfigurations();
    } else {
      i = 0;
    }
    return j | i | mDrawable.getChangingConfigurations();
  }
  
  public Drawable.ConstantState getConstantState()
  {
    if ((mState != null) && (mState.canConstantState()))
    {
      mState.mChangingConfigurations = getChangingConfigurations();
      return mState;
    }
    return null;
  }
  
  public Drawable getCurrent()
  {
    return mDrawable.getCurrent();
  }
  
  public int getIntrinsicHeight()
  {
    return mDrawable.getIntrinsicHeight();
  }
  
  public int getIntrinsicWidth()
  {
    return mDrawable.getIntrinsicWidth();
  }
  
  public int getMinimumHeight()
  {
    return mDrawable.getMinimumHeight();
  }
  
  public int getMinimumWidth()
  {
    return mDrawable.getMinimumWidth();
  }
  
  public int getOpacity()
  {
    return mDrawable.getOpacity();
  }
  
  public boolean getPadding(Rect paramRect)
  {
    return mDrawable.getPadding(paramRect);
  }
  
  public int[] getState()
  {
    return mDrawable.getState();
  }
  
  public Region getTransparentRegion()
  {
    return mDrawable.getTransparentRegion();
  }
  
  public final Drawable getWrappedDrawable()
  {
    return mDrawable;
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    invalidateSelf();
  }
  
  public boolean isAutoMirrored()
  {
    return mDrawable.isAutoMirrored();
  }
  
  protected boolean isCompatTintEnabled()
  {
    return true;
  }
  
  public boolean isStateful()
  {
    ColorStateList localColorStateList;
    if ((isCompatTintEnabled()) && (mState != null)) {
      localColorStateList = mState.mTint;
    } else {
      localColorStateList = null;
    }
    return ((localColorStateList != null) && (localColorStateList.isStateful())) || (mDrawable.isStateful());
  }
  
  public void jumpToCurrentState()
  {
    mDrawable.jumpToCurrentState();
  }
  
  public Drawable mutate()
  {
    if ((!mMutated) && (super.mutate() == this))
    {
      mState = mutateConstantState();
      if (mDrawable != null) {
        mDrawable.mutate();
      }
      if (mState != null)
      {
        WrappedDrawableState localWrappedDrawableState = mState;
        Drawable.ConstantState localConstantState;
        if (mDrawable != null) {
          localConstantState = mDrawable.getConstantState();
        } else {
          localConstantState = null;
        }
        mDrawableState = localConstantState;
      }
      mMutated = true;
    }
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    if (mDrawable != null) {
      mDrawable.setBounds(paramRect);
    }
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    return mDrawable.setLevel(paramInt);
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    scheduleSelf(paramRunnable, paramLong);
  }
  
  public void setAlpha(int paramInt)
  {
    mDrawable.setAlpha(paramInt);
  }
  
  public void setAutoMirrored(boolean paramBoolean)
  {
    mDrawable.setAutoMirrored(paramBoolean);
  }
  
  public void setChangingConfigurations(int paramInt)
  {
    mDrawable.setChangingConfigurations(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    mDrawable.setColorFilter(paramColorFilter);
  }
  
  public void setDither(boolean paramBoolean)
  {
    mDrawable.setDither(paramBoolean);
  }
  
  public void setFilterBitmap(boolean paramBoolean)
  {
    mDrawable.setFilterBitmap(paramBoolean);
  }
  
  public boolean setState(int[] paramArrayOfInt)
  {
    boolean bool = mDrawable.setState(paramArrayOfInt);
    return (updateTint(paramArrayOfInt)) || (bool);
  }
  
  public void setTint(int paramInt)
  {
    setTintList(ColorStateList.valueOf(paramInt));
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    mState.mTint = paramColorStateList;
    updateTint(getState());
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    mState.mTintMode = paramMode;
    updateTint(getState());
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    return (super.setVisible(paramBoolean1, paramBoolean2)) || (mDrawable.setVisible(paramBoolean1, paramBoolean2));
  }
  
  public final void setWrappedDrawable(Drawable paramDrawable)
  {
    if (mDrawable != null) {
      mDrawable.setCallback(null);
    }
    mDrawable = paramDrawable;
    if (paramDrawable != null)
    {
      paramDrawable.setCallback(this);
      setVisible(paramDrawable.isVisible(), true);
      setState(paramDrawable.getState());
      setLevel(paramDrawable.getLevel());
      setBounds(paramDrawable.getBounds());
      if (mState != null) {
        mState.mDrawableState = paramDrawable.getConstantState();
      }
    }
    invalidateSelf();
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    unscheduleSelf(paramRunnable);
  }
}
