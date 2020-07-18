package com.bumptech.glide.load.resource.bitmap;

import android.media.ExifInterface;
import androidx.annotation.RequiresApi;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParser.ImageType;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@RequiresApi(27)
public final class ExifInterfaceImageHeaderParser
  implements ImageHeaderParser
{
  public ExifInterfaceImageHeaderParser() {}
  
  public int getOrientation(InputStream paramInputStream, ArrayPool paramArrayPool)
    throws IOException
  {
    int i = new ExifInterface(paramInputStream).getAttributeInt("Orientation", 1);
    if (i == 0) {
      return -1;
    }
    return i;
  }
  
  public int getOrientation(ByteBuffer paramByteBuffer, ArrayPool paramArrayPool)
    throws IOException
  {
    return getOrientation(ByteBufferUtil.toStream(paramByteBuffer), paramArrayPool);
  }
  
  public ImageHeaderParser.ImageType getType(InputStream paramInputStream)
    throws IOException
  {
    return ImageHeaderParser.ImageType.UNKNOWN;
  }
  
  public ImageHeaderParser.ImageType getType(ByteBuffer paramByteBuffer)
    throws IOException
  {
    return ImageHeaderParser.ImageType.UNKNOWN;
  }
}
