package androidx.core.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Build.VERSION;

final class WrappedDrawableState
  extends Drawable.ConstantState
{
  int mChangingConfigurations;
  Drawable.ConstantState mDrawableState;
  ColorStateList mTint = null;
  PorterDuff.Mode mTintMode = WrappedDrawableApi14.DEFAULT_TINT_MODE;
  
  WrappedDrawableState(WrappedDrawableState paramWrappedDrawableState)
  {
    if (paramWrappedDrawableState != null)
    {
      mChangingConfigurations = mChangingConfigurations;
      mDrawableState = mDrawableState;
      mTint = mTint;
      mTintMode = mTintMode;
    }
  }
  
  boolean canConstantState()
  {
    return mDrawableState != null;
  }
  
  public int getChangingConfigurations()
  {
    int j = mChangingConfigurations;
    int i;
    if (mDrawableState != null) {
      i = mDrawableState.getChangingConfigurations();
    } else {
      i = 0;
    }
    return j | i;
  }
  
  public Drawable newDrawable()
  {
    return newDrawable(null);
  }
  
  public Drawable newDrawable(Resources paramResources)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return new WrappedDrawableApi21(this, paramResources);
    }
    return new WrappedDrawableApi14(this, paramResources);
  }
}
