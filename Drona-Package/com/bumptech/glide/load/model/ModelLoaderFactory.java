package com.bumptech.glide.load.model;

public abstract interface ModelLoaderFactory<T, Y>
{
  public abstract ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory);
  
  public abstract void teardown();
}
