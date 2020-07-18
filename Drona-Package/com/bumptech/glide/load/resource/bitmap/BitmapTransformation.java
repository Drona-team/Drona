package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Util;

public abstract class BitmapTransformation
  implements Transformation<Bitmap>
{
  public BitmapTransformation() {}
  
  protected abstract Bitmap transform(BitmapPool paramBitmapPool, Bitmap paramBitmap, int paramInt1, int paramInt2);
  
  public final Resource transform(Context paramContext, Resource paramResource, int paramInt1, int paramInt2)
  {
    if (Util.isValidDimensions(paramInt1, paramInt2))
    {
      paramContext = Glide.get(paramContext).getBitmapPool();
      Bitmap localBitmap1 = (Bitmap)paramResource.get();
      int i = paramInt1;
      if (paramInt1 == Integer.MIN_VALUE) {
        i = localBitmap1.getWidth();
      }
      paramInt1 = paramInt2;
      if (paramInt2 == Integer.MIN_VALUE) {
        paramInt1 = localBitmap1.getHeight();
      }
      Bitmap localBitmap2 = transform(paramContext, localBitmap1, i, paramInt1);
      if (localBitmap1.equals(localBitmap2)) {
        return paramResource;
      }
      return BitmapResource.obtain(localBitmap2, paramContext);
    }
    paramContext = new StringBuilder();
    paramContext.append("Cannot apply transformation on width: ");
    paramContext.append(paramInt1);
    paramContext.append(" or height: ");
    paramContext.append(paramInt2);
    paramContext.append(" less than or equal to zero and not Target.SIZE_ORIGINAL");
    throw new IllegalArgumentException(paramContext.toString());
  }
}
