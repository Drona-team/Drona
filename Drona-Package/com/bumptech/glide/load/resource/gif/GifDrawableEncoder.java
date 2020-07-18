package com.bumptech.glide.load.resource.gif;

import android.util.Log;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.File;
import java.io.IOException;

public class GifDrawableEncoder
  implements ResourceEncoder<GifDrawable>
{
  private static final String TAG = "GifEncoder";
  
  public GifDrawableEncoder() {}
  
  public boolean encode(Resource paramResource, File paramFile, Options paramOptions)
  {
    paramResource = (GifDrawable)paramResource.get();
    try
    {
      ByteBufferUtil.toFile(paramResource.getBuffer(), paramFile);
      return true;
    }
    catch (IOException paramResource)
    {
      if (Log.isLoggable("GifEncoder", 5)) {
        Log.w("GifEncoder", "Failed to encode GIF drawable data", paramResource);
      }
    }
    return false;
  }
  
  public EncodeStrategy getEncodeStrategy(Options paramOptions)
  {
    return EncodeStrategy.SOURCE;
  }
}
