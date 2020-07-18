package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import java.io.IOException;

public class BitmapDrawableDecoder<DataType>
  implements ResourceDecoder<DataType, BitmapDrawable>
{
  private final ResourceDecoder<DataType, Bitmap> decoder;
  private final Resources resources;
  
  public BitmapDrawableDecoder(Context paramContext, ResourceDecoder paramResourceDecoder)
  {
    this(paramContext.getResources(), paramResourceDecoder);
  }
  
  public BitmapDrawableDecoder(Resources paramResources, ResourceDecoder paramResourceDecoder)
  {
    resources = ((Resources)Preconditions.checkNotNull(paramResources));
    decoder = ((ResourceDecoder)Preconditions.checkNotNull(paramResourceDecoder));
  }
  
  public BitmapDrawableDecoder(Resources paramResources, BitmapPool paramBitmapPool, ResourceDecoder paramResourceDecoder)
  {
    this(paramResources, paramResourceDecoder);
  }
  
  public Resource decode(Object paramObject, int paramInt1, int paramInt2, Options paramOptions)
    throws IOException
  {
    paramObject = decoder.decode(paramObject, paramInt1, paramInt2, paramOptions);
    return LazyBitmapDrawableResource.obtain(resources, paramObject);
  }
  
  public boolean handles(Object paramObject, Options paramOptions)
    throws IOException
  {
    return decoder.handles(paramObject, paramOptions);
  }
}
