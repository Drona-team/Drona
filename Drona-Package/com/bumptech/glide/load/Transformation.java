package com.bumptech.glide.load;

import android.content.Context;
import com.bumptech.glide.load.engine.Resource;

public abstract interface Transformation<T>
  extends Key
{
  public abstract Resource transform(Context paramContext, Resource paramResource, int paramInt1, int paramInt2);
}
