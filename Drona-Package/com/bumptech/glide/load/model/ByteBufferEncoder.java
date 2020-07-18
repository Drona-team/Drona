package com.bumptech.glide.load.model;

import android.util.Log;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferEncoder
  implements Encoder<ByteBuffer>
{
  private static final String PAGE_KEY = "ByteBufferEncoder";
  
  public ByteBufferEncoder() {}
  
  public boolean encode(ByteBuffer paramByteBuffer, File paramFile, Options paramOptions)
  {
    try
    {
      ByteBufferUtil.toFile(paramByteBuffer, paramFile);
      return true;
    }
    catch (IOException paramByteBuffer)
    {
      if (Log.isLoggable("ByteBufferEncoder", 3)) {
        Log.d("ByteBufferEncoder", "Failed to write data", paramByteBuffer);
      }
    }
    return false;
  }
}
