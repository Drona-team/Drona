package com.bumptech.glide.request.target;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.request.transition.Transition.ViewAdapter;

public abstract class ImageViewTarget<Z>
  extends ViewTarget<ImageView, Z>
  implements Transition.ViewAdapter
{
  @Nullable
  private Animatable animatable;
  
  public ImageViewTarget(ImageView paramImageView)
  {
    super(paramImageView);
  }
  
  public ImageViewTarget(ImageView paramImageView, boolean paramBoolean)
  {
    super(paramImageView, paramBoolean);
  }
  
  private void maybeUpdateAnimatable(Object paramObject)
  {
    if ((paramObject instanceof Animatable))
    {
      animatable = ((Animatable)paramObject);
      animatable.start();
      return;
    }
    animatable = null;
  }
  
  private void setResourceInternal(Object paramObject)
  {
    setResource(paramObject);
    maybeUpdateAnimatable(paramObject);
  }
  
  public Drawable getCurrentDrawable()
  {
    return ((ImageView)view).getDrawable();
  }
  
  public void onLoadCleared(Drawable paramDrawable)
  {
    super.onLoadCleared(paramDrawable);
    if (animatable != null) {
      animatable.stop();
    }
    setResourceInternal(null);
    setDrawable(paramDrawable);
  }
  
  public void onLoadFailed(Drawable paramDrawable)
  {
    super.onLoadFailed(paramDrawable);
    setResourceInternal(null);
    setDrawable(paramDrawable);
  }
  
  public void onLoadStarted(Drawable paramDrawable)
  {
    super.onLoadStarted(paramDrawable);
    setResourceInternal(null);
    setDrawable(paramDrawable);
  }
  
  public void onResourceReady(Object paramObject, Transition paramTransition)
  {
    if ((paramTransition != null) && (paramTransition.transition(paramObject, this)))
    {
      maybeUpdateAnimatable(paramObject);
      return;
    }
    setResourceInternal(paramObject);
  }
  
  public void onStart()
  {
    if (animatable != null) {
      animatable.start();
    }
  }
  
  public void onStop()
  {
    if (animatable != null) {
      animatable.stop();
    }
  }
  
  public void setDrawable(Drawable paramDrawable)
  {
    ((ImageView)view).setImageDrawable(paramDrawable);
  }
  
  protected abstract void setResource(Object paramObject);
}
