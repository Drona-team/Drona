package com.bumptech.glide.request.target;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class DrawableImageViewTarget
  extends ImageViewTarget<Drawable>
{
  public DrawableImageViewTarget(ImageView paramImageView)
  {
    super(paramImageView);
  }
  
  public DrawableImageViewTarget(ImageView paramImageView, boolean paramBoolean)
  {
    super(paramImageView, paramBoolean);
  }
  
  protected void setResource(Drawable paramDrawable)
  {
    ((ImageView)view).setImageDrawable(paramDrawable);
  }
}
