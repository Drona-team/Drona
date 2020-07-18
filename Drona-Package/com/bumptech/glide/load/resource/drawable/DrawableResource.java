package com.bumptech.glide.load.resource.drawable;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import com.bumptech.glide.load.engine.Initializable;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.util.Preconditions;

public abstract class DrawableResource<T extends Drawable>
  implements Resource<T>, Initializable
{
  protected final T drawable;
  
  public DrawableResource(Drawable paramDrawable)
  {
    drawable = ((Drawable)Preconditions.checkNotNull(paramDrawable));
  }
  
  public final Drawable get()
  {
    Drawable.ConstantState localConstantState = drawable.getConstantState();
    if (localConstantState == null) {
      return drawable;
    }
    return localConstantState.newDrawable();
  }
  
  public void initialize()
  {
    if ((drawable instanceof BitmapDrawable))
    {
      ((BitmapDrawable)drawable).getBitmap().prepareToDraw();
      return;
    }
    if ((drawable instanceof GifDrawable)) {
      ((GifDrawable)drawable).getFirstFrame().prepareToDraw();
    }
  }
}
