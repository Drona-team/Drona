package com.bumptech.glide.request.transition;

import android.graphics.Bitmap;

public class BitmapTransitionFactory
  extends BitmapContainerTransitionFactory<Bitmap>
{
  public BitmapTransitionFactory(TransitionFactory paramTransitionFactory)
  {
    super(paramTransitionFactory);
  }
  
  protected Bitmap getBitmap(Bitmap paramBitmap)
  {
    return paramBitmap;
  }
}
