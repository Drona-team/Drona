package com.bumptech.glide.load.resource.transcode;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.gif.GifDrawable;

public final class DrawableBytesTranscoder
  implements ResourceTranscoder<Drawable, byte[]>
{
  private final ResourceTranscoder<Bitmap, byte[]> bitmapBytesTranscoder;
  private final BitmapPool bitmapPool;
  private final ResourceTranscoder<GifDrawable, byte[]> gifDrawableBytesTranscoder;
  
  public DrawableBytesTranscoder(BitmapPool paramBitmapPool, ResourceTranscoder paramResourceTranscoder1, ResourceTranscoder paramResourceTranscoder2)
  {
    bitmapPool = paramBitmapPool;
    bitmapBytesTranscoder = paramResourceTranscoder1;
    gifDrawableBytesTranscoder = paramResourceTranscoder2;
  }
  
  private static Resource toGifDrawableResource(Resource paramResource)
  {
    return paramResource;
  }
  
  public Resource transcode(Resource paramResource, Options paramOptions)
  {
    Drawable localDrawable = (Drawable)paramResource.get();
    if ((localDrawable instanceof BitmapDrawable)) {
      return bitmapBytesTranscoder.transcode(BitmapResource.obtain(((BitmapDrawable)localDrawable).getBitmap(), bitmapPool), paramOptions);
    }
    if ((localDrawable instanceof GifDrawable)) {
      return gifDrawableBytesTranscoder.transcode(toGifDrawableResource(paramResource), paramOptions);
    }
    return null;
  }
}
