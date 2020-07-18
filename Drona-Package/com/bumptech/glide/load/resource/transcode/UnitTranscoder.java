package com.bumptech.glide.load.resource.transcode;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;

public class UnitTranscoder<Z>
  implements ResourceTranscoder<Z, Z>
{
  private static final UnitTranscoder<?> UNIT_TRANSCODER = new UnitTranscoder();
  
  public UnitTranscoder() {}
  
  public static ResourceTranscoder get()
  {
    return UNIT_TRANSCODER;
  }
  
  public Resource transcode(Resource paramResource, Options paramOptions)
  {
    return paramResource;
  }
}
