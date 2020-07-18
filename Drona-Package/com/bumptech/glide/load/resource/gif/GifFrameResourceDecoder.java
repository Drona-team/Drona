package com.bumptech.glide.load.resource.gif;

import android.graphics.Bitmap;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

public final class GifFrameResourceDecoder
  implements ResourceDecoder<GifDecoder, Bitmap>
{
  private final BitmapPool bitmapPool;
  
  public GifFrameResourceDecoder(BitmapPool paramBitmapPool)
  {
    bitmapPool = paramBitmapPool;
  }
  
  public Resource decode(GifDecoder paramGifDecoder, int paramInt1, int paramInt2, Options paramOptions)
  {
    return BitmapResource.obtain(paramGifDecoder.getNextFrame(), bitmapPool);
  }
  
  public boolean handles(GifDecoder paramGifDecoder, Options paramOptions)
  {
    return true;
  }
}
