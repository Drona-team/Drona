package com.bumptech.glide.module;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;

@Deprecated
abstract interface RegistersComponents
{
  public abstract void registerComponents(Context paramContext, Glide paramGlide, Registry paramRegistry);
}
