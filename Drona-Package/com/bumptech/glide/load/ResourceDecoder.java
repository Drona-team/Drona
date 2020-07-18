package com.bumptech.glide.load;

import com.bumptech.glide.load.engine.Resource;
import java.io.IOException;

public abstract interface ResourceDecoder<T, Z>
{
  public abstract Resource decode(Object paramObject, int paramInt1, int paramInt2, Options paramOptions)
    throws IOException;
  
  public abstract boolean handles(Object paramObject, Options paramOptions)
    throws IOException;
}
