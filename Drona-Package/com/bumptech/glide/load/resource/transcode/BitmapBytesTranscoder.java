package com.bumptech.glide.load.resource.transcode;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bytes.BytesResource;
import java.io.ByteArrayOutputStream;

public class BitmapBytesTranscoder
  implements ResourceTranscoder<Bitmap, byte[]>
{
  private final Bitmap.CompressFormat compressFormat;
  private final int quality;
  
  public BitmapBytesTranscoder()
  {
    this(Bitmap.CompressFormat.JPEG, 100);
  }
  
  public BitmapBytesTranscoder(Bitmap.CompressFormat paramCompressFormat, int paramInt)
  {
    compressFormat = paramCompressFormat;
    quality = paramInt;
  }
  
  public Resource transcode(Resource paramResource, Options paramOptions)
  {
    paramOptions = new ByteArrayOutputStream();
    ((Bitmap)paramResource.get()).compress(compressFormat, quality, paramOptions);
    paramResource.recycle();
    return new BytesResource(paramOptions.toByteArray());
  }
}
