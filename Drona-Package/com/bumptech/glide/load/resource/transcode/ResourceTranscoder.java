package com.bumptech.glide.load.resource.transcode;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;

public abstract interface ResourceTranscoder<Z, R>
{
  public abstract Resource transcode(Resource paramResource, Options paramOptions);
}
