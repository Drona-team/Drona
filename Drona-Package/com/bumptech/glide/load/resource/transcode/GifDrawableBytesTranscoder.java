package com.bumptech.glide.load.resource.transcode;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bytes.BytesResource;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.util.ByteBufferUtil;

public class GifDrawableBytesTranscoder
  implements ResourceTranscoder<GifDrawable, byte[]>
{
  public GifDrawableBytesTranscoder() {}
  
  public Resource transcode(Resource paramResource, Options paramOptions)
  {
    return new BytesResource(ByteBufferUtil.toBytes(((GifDrawable)paramResource.get()).getBuffer()));
  }
}
