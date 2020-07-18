package com.bumptech.glide.load;

import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

public final class ImageHeaderParserUtils
{
  private static final int MARK_POSITION = 5242880;
  
  private ImageHeaderParserUtils() {}
  
  public static int getOrientation(List paramList, InputStream paramInputStream, ArrayPool paramArrayPool)
    throws IOException
  {
    if (paramInputStream == null) {
      return -1;
    }
    Object localObject = paramInputStream;
    if (!((InputStream)paramInputStream).markSupported()) {
      localObject = new RecyclableBufferedInputStream((InputStream)paramInputStream, paramArrayPool);
    }
    ((InputStream)localObject).mark(5242880);
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      paramInputStream = (ImageHeaderParser)paramList.get(i);
      try
      {
        int k = paramInputStream.getOrientation((InputStream)localObject, paramArrayPool);
        if (k != -1)
        {
          ((InputStream)localObject).reset();
          return k;
        }
        ((InputStream)localObject).reset();
        i += 1;
      }
      catch (Throwable paramList)
      {
        ((InputStream)localObject).reset();
        throw paramList;
      }
    }
    return -1;
  }
  
  public static ImageHeaderParser.ImageType getType(List paramList, InputStream paramInputStream, ArrayPool paramArrayPool)
    throws IOException
  {
    if (paramInputStream == null) {
      return ImageHeaderParser.ImageType.UNKNOWN;
    }
    Object localObject = paramInputStream;
    if (!((InputStream)paramInputStream).markSupported()) {
      localObject = new RecyclableBufferedInputStream((InputStream)paramInputStream, paramArrayPool);
    }
    ((InputStream)localObject).mark(5242880);
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      paramInputStream = (ImageHeaderParser)paramList.get(i);
      try
      {
        paramInputStream = paramInputStream.getType((InputStream)localObject);
        paramArrayPool = ImageHeaderParser.ImageType.UNKNOWN;
        if (paramInputStream != paramArrayPool)
        {
          ((InputStream)localObject).reset();
          return paramInputStream;
        }
        ((InputStream)localObject).reset();
        i += 1;
      }
      catch (Throwable paramList)
      {
        ((InputStream)localObject).reset();
        throw paramList;
      }
    }
    return ImageHeaderParser.ImageType.UNKNOWN;
  }
  
  public static ImageHeaderParser.ImageType getType(List paramList, ByteBuffer paramByteBuffer)
    throws IOException
  {
    if (paramByteBuffer == null) {
      return ImageHeaderParser.ImageType.UNKNOWN;
    }
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      ImageHeaderParser.ImageType localImageType = ((ImageHeaderParser)paramList.get(i)).getType(paramByteBuffer);
      if (localImageType != ImageHeaderParser.ImageType.UNKNOWN) {
        return localImageType;
      }
      i += 1;
    }
    return ImageHeaderParser.ImageType.UNKNOWN;
  }
}
