package com.bumptech.glide.load.resource.drawable;

import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;

public class UnitDrawableDecoder
  implements ResourceDecoder<Drawable, Drawable>
{
  public UnitDrawableDecoder() {}
  
  public Resource decode(Drawable paramDrawable, int paramInt1, int paramInt2, Options paramOptions)
  {
    return NonOwnedDrawableResource.newInstance(paramDrawable);
  }
  
  public boolean handles(Drawable paramDrawable, Options paramOptions)
  {
    return true;
  }
}
