package com.bumptech.glide.request.target;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

public class BitmapThumbnailImageViewTarget
  extends ThumbnailImageViewTarget<Bitmap>
{
  public BitmapThumbnailImageViewTarget(ImageView paramImageView)
  {
    super(paramImageView);
  }
  
  public BitmapThumbnailImageViewTarget(ImageView paramImageView, boolean paramBoolean)
  {
    super(paramImageView, paramBoolean);
  }
  
  protected Drawable getDrawable(Bitmap paramBitmap)
  {
    return new BitmapDrawable(((ImageView)view).getResources(), paramBitmap);
  }
}
