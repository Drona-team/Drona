package com.bumptech.glide.load;

import com.bumptech.glide.load.engine.Resource;

public abstract interface ResourceEncoder<T>
  extends Encoder<Resource<T>>
{
  public abstract EncodeStrategy getEncodeStrategy(Options paramOptions);
}
