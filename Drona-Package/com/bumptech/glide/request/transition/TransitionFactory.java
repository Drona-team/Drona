package com.bumptech.glide.request.transition;

import com.bumptech.glide.load.DataSource;

public abstract interface TransitionFactory<R>
{
  public abstract Transition build(DataSource paramDataSource, boolean paramBoolean);
}
