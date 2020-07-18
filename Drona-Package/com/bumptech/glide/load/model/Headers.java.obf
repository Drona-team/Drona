package com.bumptech.glide.load.model;

import java.util.Collections;
import java.util.Map;

public abstract interface Headers
{
  public static final Headers DEFAULT = new LazyHeaders.Builder().build();
  @Deprecated
  public static final Headers NONE = new Headers()
  {
    public Map<String, String> getHeaders()
    {
      return Collections.emptyMap();
    }
  };
  
  public abstract Map<String, String> getHeaders();
}
