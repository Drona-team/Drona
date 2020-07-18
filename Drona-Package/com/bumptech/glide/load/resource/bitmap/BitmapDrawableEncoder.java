package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.io.File;

public class BitmapDrawableEncoder
  implements ResourceEncoder<BitmapDrawable>
{
  private final BitmapPool bitmapPool;
  private final ResourceEncoder<Bitmap> encoder;
  
  public BitmapDrawableEncoder(BitmapPool paramBitmapPool, ResourceEncoder paramResourceEncoder)
  {
    bitmapPool = paramBitmapPool;
    encoder = paramResourceEncoder;
  }
  
  public boolean encode(Resource paramResource, File paramFile, Options paramOptions)
  {
    return encoder.encode(new BitmapResource(((BitmapDrawable)paramResource.get()).getBitmap(), bitmapPool), paramFile, paramOptions);
  }
  
  public EncodeStrategy getEncodeStrategy(Options paramOptions)
  {
    return encoder.getEncodeStrategy(paramOptions);
  }
}
