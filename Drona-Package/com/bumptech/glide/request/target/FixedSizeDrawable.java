package com.bumptech.glide.request.target;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import com.bumptech.glide.util.Preconditions;

public class FixedSizeDrawable
  extends Drawable
{
  private final RectF bounds;
  private final Matrix matrix;
  private boolean mutated;
  private State state;
  private Drawable wrapped;
  private final RectF wrappedRect;
  
  public FixedSizeDrawable(Drawable paramDrawable, int paramInt1, int paramInt2)
  {
    this(new State(paramDrawable.getConstantState(), paramInt1, paramInt2), paramDrawable);
  }
  
  FixedSizeDrawable(State paramState, Drawable paramDrawable)
  {
    state = ((State)Preconditions.checkNotNull(paramState));
    wrapped = ((Drawable)Preconditions.checkNotNull(paramDrawable));
    paramDrawable.setBounds(0, 0, paramDrawable.getIntrinsicWidth(), paramDrawable.getIntrinsicHeight());
    matrix = new Matrix();
    wrappedRect = new RectF(0.0F, 0.0F, paramDrawable.getIntrinsicWidth(), paramDrawable.getIntrinsicHeight());
    bounds = new RectF();
  }
  
  private void updateMatrix()
  {
    matrix.setRectToRect(wrappedRect, bounds, Matrix.ScaleToFit.CENTER);
  }
  
  public void clearColorFilter()
  {
    wrapped.clearColorFilter();
  }
  
  public void draw(Canvas paramCanvas)
  {
    paramCanvas.save();
    paramCanvas.concat(matrix);
    wrapped.draw(paramCanvas);
    paramCanvas.restore();
  }
  
  public int getAlpha()
  {
    return wrapped.getAlpha();
  }
  
  public Drawable.Callback getCallback()
  {
    return wrapped.getCallback();
  }
  
  public int getChangingConfigurations()
  {
    return wrapped.getChangingConfigurations();
  }
  
  public Drawable.ConstantState getConstantState()
  {
    return state;
  }
  
  public Drawable getCurrent()
  {
    return wrapped.getCurrent();
  }
  
  public int getIntrinsicHeight()
  {
    return state.height;
  }
  
  public int getIntrinsicWidth()
  {
    return state.width;
  }
  
  public int getMinimumHeight()
  {
    return wrapped.getMinimumHeight();
  }
  
  public int getMinimumWidth()
  {
    return wrapped.getMinimumWidth();
  }
  
  public int getOpacity()
  {
    return wrapped.getOpacity();
  }
  
  public boolean getPadding(Rect paramRect)
  {
    return wrapped.getPadding(paramRect);
  }
  
  public void invalidateSelf()
  {
    super.invalidateSelf();
    wrapped.invalidateSelf();
  }
  
  public Drawable mutate()
  {
    if ((!mutated) && (super.mutate() == this))
    {
      wrapped = wrapped.mutate();
      state = new State(state);
      mutated = true;
    }
    return this;
  }
  
  public void scheduleSelf(Runnable paramRunnable, long paramLong)
  {
    super.scheduleSelf(paramRunnable, paramLong);
    wrapped.scheduleSelf(paramRunnable, paramLong);
  }
  
  public void setAlpha(int paramInt)
  {
    wrapped.setAlpha(paramInt);
  }
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    bounds.set(paramInt1, paramInt2, paramInt3, paramInt4);
    updateMatrix();
  }
  
  public void setBounds(Rect paramRect)
  {
    super.setBounds(paramRect);
    bounds.set(paramRect);
    updateMatrix();
  }
  
  public void setChangingConfigurations(int paramInt)
  {
    wrapped.setChangingConfigurations(paramInt);
  }
  
  public void setColorFilter(int paramInt, PorterDuff.Mode paramMode)
  {
    wrapped.setColorFilter(paramInt, paramMode);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    wrapped.setColorFilter(paramColorFilter);
  }
  
  public void setDither(boolean paramBoolean)
  {
    wrapped.setDither(paramBoolean);
  }
  
  public void setFilterBitmap(boolean paramBoolean)
  {
    wrapped.setFilterBitmap(paramBoolean);
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    return wrapped.setVisible(paramBoolean1, paramBoolean2);
  }
  
  public void unscheduleSelf(Runnable paramRunnable)
  {
    super.unscheduleSelf(paramRunnable);
    wrapped.unscheduleSelf(paramRunnable);
  }
  
  static final class State
    extends Drawable.ConstantState
  {
    final int height;
    final int width;
    private final Drawable.ConstantState wrapped;
    
    State(Drawable.ConstantState paramConstantState, int paramInt1, int paramInt2)
    {
      wrapped = paramConstantState;
      width = paramInt1;
      height = paramInt2;
    }
    
    State(State paramState)
    {
      this(wrapped, width, height);
    }
    
    public int getChangingConfigurations()
    {
      return 0;
    }
    
    public Drawable newDrawable()
    {
      return new FixedSizeDrawable(this, wrapped.newDrawable());
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new FixedSizeDrawable(this, wrapped.newDrawable(paramResources));
    }
  }
}
