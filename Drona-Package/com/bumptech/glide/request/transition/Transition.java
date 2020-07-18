package com.bumptech.glide.request.transition;

import android.graphics.drawable.Drawable;
import android.view.View;

public abstract interface Transition<R>
{
  public abstract boolean transition(Object paramObject, ViewAdapter paramViewAdapter);
  
  public static abstract interface ViewAdapter
  {
    public abstract Drawable getCurrentDrawable();
    
    public abstract View getView();
    
    public abstract void setDrawable(Drawable paramDrawable);
  }
}
