package com.bumptech.glide.request.target;

import android.graphics.drawable.Drawable;
import com.bumptech.glide.manager.LifecycleListener;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.transition.Transition;

public abstract interface Target<R>
  extends LifecycleListener
{
  public static final int SIZE_ORIGINAL = Integer.MIN_VALUE;
  
  public abstract Request getRequest();
  
  public abstract void getSize(SizeReadyCallback paramSizeReadyCallback);
  
  public abstract void onLoadCleared(Drawable paramDrawable);
  
  public abstract void onLoadFailed(Drawable paramDrawable);
  
  public abstract void onLoadStarted(Drawable paramDrawable);
  
  public abstract void onResourceReady(Object paramObject, Transition paramTransition);
  
  public abstract void removeCallback(SizeReadyCallback paramSizeReadyCallback);
  
  public abstract void setRequest(Request paramRequest);
}
