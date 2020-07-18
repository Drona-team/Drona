package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Util;

public final class UnitBitmapDecoder
  implements ResourceDecoder<Bitmap, Bitmap>
{
  public UnitBitmapDecoder() {}
  
  public Resource decode(Bitmap paramBitmap, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new NonOwnedBitmapResource(paramBitmap);
  }
  
  public boolean handles(Bitmap paramBitmap, Options paramOptions)
  {
    return true;
  }
  
  private static final class NonOwnedBitmapResource
    implements Resource<Bitmap>
  {
    private final Bitmap bitmap;
    
    NonOwnedBitmapResource(Bitmap paramBitmap)
    {
      bitmap = paramBitmap;
    }
    
    public Bitmap get()
    {
      return bitmap;
    }
    
    public Class getResourceClass()
    {
      return Bitmap.class;
    }
    
    public int getSize()
    {
      return Util.getBitmapByteSize(bitmap);
    }
    
    public void recycle() {}
  }
}
