package com.bumptech.glide.request.target;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class DrawableThumbnailImageViewTarget
  extends ThumbnailImageViewTarget<Drawable>
{
  public DrawableThumbnailImageViewTarget(ImageView paramImageView)
  {
    super(paramImageView);
  }
  
  public DrawableThumbnailImageViewTarget(ImageView paramImageView, boolean paramBoolean)
  {
    super(paramImageView, paramBoolean);
  }
  
  protected Drawable getDrawable(Drawable paramDrawable)
  {
    return paramDrawable;
  }
}
