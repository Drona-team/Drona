package com.bumptech.glide.load.resource.gif;

import android.util.Log;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParser.ImageType;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class StreamGifDecoder
  implements ResourceDecoder<InputStream, GifDrawable>
{
  private static final String PAGE_KEY = "StreamGifDecoder";
  private final ArrayPool byteArrayPool;
  private final ResourceDecoder<ByteBuffer, GifDrawable> byteBufferDecoder;
  private final List<ImageHeaderParser> parsers;
  
  public StreamGifDecoder(List paramList, ResourceDecoder paramResourceDecoder, ArrayPool paramArrayPool)
  {
    parsers = paramList;
    byteBufferDecoder = paramResourceDecoder;
    byteArrayPool = paramArrayPool;
  }
  
  private static byte[] inputStreamToBytes(InputStream paramInputStream)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(16384);
    byte[] arrayOfByte = new byte['?'];
    try
    {
      for (;;)
      {
        int i = paramInputStream.read(arrayOfByte);
        if (i == -1) {
          break;
        }
        localByteArrayOutputStream.write(arrayOfByte, 0, i);
      }
      localByteArrayOutputStream.flush();
      return localByteArrayOutputStream.toByteArray();
    }
    catch (IOException paramInputStream)
    {
      if (Log.isLoggable("StreamGifDecoder", 5)) {
        Log.w("StreamGifDecoder", "Error reading data from stream", paramInputStream);
      }
    }
    return null;
  }
  
  public Resource decode(InputStream paramInputStream, int paramInt1, int paramInt2, Options paramOptions)
    throws IOException
  {
    paramInputStream = inputStreamToBytes(paramInputStream);
    if (paramInputStream == null) {
      return null;
    }
    paramInputStream = ByteBuffer.wrap(paramInputStream);
    return byteBufferDecoder.decode(paramInputStream, paramInt1, paramInt2, paramOptions);
  }
  
  public boolean handles(InputStream paramInputStream, Options paramOptions)
    throws IOException
  {
    return (!((Boolean)paramOptions.getOption(GifOptions.DISABLE_ANIMATION)).booleanValue()) && (ImageHeaderParserUtils.getType(parsers, paramInputStream, byteArrayPool) == ImageHeaderParser.ImageType.GIF);
  }
}
