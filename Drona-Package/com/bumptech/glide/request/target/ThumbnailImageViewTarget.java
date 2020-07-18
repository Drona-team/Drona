package com.bumptech.glide.request.target;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public abstract class ThumbnailImageViewTarget<T>
  extends ImageViewTarget<T>
{
  public ThumbnailImageViewTarget(ImageView paramImageView)
  {
    super(paramImageView);
  }
  
  public ThumbnailImageViewTarget(ImageView paramImageView, boolean paramBoolean)
  {
    super(paramImageView, paramBoolean);
  }
  
  protected abstract Drawable getDrawable(Object paramObject);
  
  protected void setResource(Object paramObject)
  {
    ViewGroup.LayoutParams localLayoutParams = ((ImageView)view).getLayoutParams();
    Drawable localDrawable = getDrawable(paramObject);
    paramObject = localDrawable;
    Object localObject = paramObject;
    if (localLayoutParams != null)
    {
      localObject = paramObject;
      if (width > 0)
      {
        localObject = paramObject;
        if (height > 0) {
          localObject = new FixedSizeDrawable(localDrawable, width, height);
        }
      }
    }
    ((ImageView)view).setImageDrawable((Drawable)localObject);
  }
}
