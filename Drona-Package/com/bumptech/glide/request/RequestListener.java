package com.bumptech.glide.request;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;

public abstract interface RequestListener<R>
{
  public abstract boolean onLoadFailed(GlideException paramGlideException, Object paramObject, Target paramTarget, boolean paramBoolean);
  
  public abstract boolean onResourceReady(Object paramObject1, Object paramObject2, Target paramTarget, DataSource paramDataSource, boolean paramBoolean);
}
