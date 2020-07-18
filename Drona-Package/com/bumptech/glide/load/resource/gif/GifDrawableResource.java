package com.bumptech.glide.load.resource.gif;

import android.graphics.Bitmap;
import com.bumptech.glide.load.engine.Initializable;
import com.bumptech.glide.load.resource.drawable.DrawableResource;

public class GifDrawableResource
  extends DrawableResource<GifDrawable>
  implements Initializable
{
  public GifDrawableResource(GifDrawable paramGifDrawable)
  {
    super(paramGifDrawable);
  }
  
  public Class getResourceClass()
  {
    return GifDrawable.class;
  }
  
  public int getSize()
  {
    return ((GifDrawable)drawable).getSize();
  }
  
  public void initialize()
  {
    ((GifDrawable)drawable).getFirstFrame().prepareToDraw();
  }
  
  public void recycle()
  {
    ((GifDrawable)drawable).stop();
    ((GifDrawable)drawable).recycle();
  }
}
