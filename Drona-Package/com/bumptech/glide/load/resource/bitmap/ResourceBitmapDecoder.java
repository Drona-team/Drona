package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.drawable.ResourceDrawableDecoder;

public class ResourceBitmapDecoder
  implements ResourceDecoder<Uri, Bitmap>
{
  private final BitmapPool bitmapPool;
  private final ResourceDrawableDecoder drawableDecoder;
  
  public ResourceBitmapDecoder(ResourceDrawableDecoder paramResourceDrawableDecoder, BitmapPool paramBitmapPool)
  {
    drawableDecoder = paramResourceDrawableDecoder;
    bitmapPool = paramBitmapPool;
  }
  
  public Resource decode(Uri paramUri, int paramInt1, int paramInt2, Options paramOptions)
  {
    paramUri = drawableDecoder.decode(paramUri, paramInt1, paramInt2, paramOptions);
    if (paramUri == null) {
      return null;
    }
    paramUri = (Drawable)paramUri.get();
    return DrawableToBitmapConverter.convert(bitmapPool, paramUri, paramInt1, paramInt2);
  }
  
  public boolean handles(Uri paramUri, Options paramOptions)
  {
    return "android.resource".equals(paramUri.getScheme());
  }
}
