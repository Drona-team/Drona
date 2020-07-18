package com.bumptech.glide.load;

import java.io.File;

public abstract interface Encoder<T>
{
  public abstract boolean encode(Object paramObject, File paramFile, Options paramOptions);
}
