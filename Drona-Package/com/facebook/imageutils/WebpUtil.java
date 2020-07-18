package com.facebook.imageutils;

import android.util.Pair;
import java.io.IOException;
import java.io.InputStream;

public class WebpUtil
{
  private static final String VP8L_HEADER = "VP8L";
  private static final String VP8X_HEADER = "VP8X";
  private static final String VP8_HEADER = "VP8 ";
  
  private WebpUtil() {}
  
  private static boolean compare(byte[] paramArrayOfByte, String paramString)
  {
    if (paramArrayOfByte.length != paramString.length()) {
      return false;
    }
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      if (paramString.charAt(i) != paramArrayOfByte[i]) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public static int get2BytesAsInt(InputStream paramInputStream)
    throws IOException
  {
    int i = (byte)paramInputStream.read();
    return (byte)paramInputStream.read() << 8 & 0xFF00 | i & 0xFF;
  }
  
  private static byte getByte(InputStream paramInputStream)
    throws IOException
  {
    return (byte)(paramInputStream.read() & 0xFF);
  }
  
  private static String getHeader(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      localStringBuilder.append((char)paramArrayOfByte[i]);
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  private static int getInt(InputStream paramInputStream)
    throws IOException
  {
    int i = (byte)paramInputStream.read();
    int j = (byte)paramInputStream.read();
    int k = (byte)paramInputStream.read();
    return (byte)paramInputStream.read() << 24 & 0xFF000000 | k << 16 & 0xFF0000 | j << 8 & 0xFF00 | i & 0xFF;
  }
  
  private static short getShort(InputStream paramInputStream)
    throws IOException
  {
    return (short)(paramInputStream.read() & 0xFF);
  }
  
  public static Pair getSize(InputStream paramInputStream)
  {
    Object localObject = new byte[4];
    try
    {
      paramInputStream.read((byte[])localObject);
      boolean bool = compare((byte[])localObject, "RIFF");
      if (!bool)
      {
        if (paramInputStream == null) {
          break label253;
        }
        try
        {
          paramInputStream.close();
          return null;
        }
        catch (IOException paramInputStream)
        {
          paramInputStream.printStackTrace();
          return null;
        }
      }
      getInt(paramInputStream);
      paramInputStream.read((byte[])localObject);
      bool = compare((byte[])localObject, "WEBP");
      if (!bool)
      {
        if (paramInputStream == null) {
          break label253;
        }
        try
        {
          paramInputStream.close();
          return null;
        }
        catch (IOException paramInputStream)
        {
          paramInputStream.printStackTrace();
          return null;
        }
      }
      paramInputStream.read((byte[])localObject);
      localObject = getHeader((byte[])localObject);
      bool = "VP8 ".equals(localObject);
      Pair localPair;
      if (bool)
      {
        localPair = getVP8Dimension(paramInputStream);
        localObject = localPair;
        if (paramInputStream == null) {
          break label255;
        }
        try
        {
          paramInputStream.close();
          return localPair;
        }
        catch (IOException paramInputStream)
        {
          paramInputStream.printStackTrace();
          return localPair;
        }
      }
      bool = "VP8L".equals(localObject);
      if (bool)
      {
        localPair = getVP8LDimension(paramInputStream);
        localObject = localPair;
        if (paramInputStream == null) {
          break label255;
        }
        try
        {
          paramInputStream.close();
          return localPair;
        }
        catch (IOException paramInputStream)
        {
          paramInputStream.printStackTrace();
          return localPair;
        }
      }
      bool = "VP8X".equals(localObject);
      if (bool)
      {
        localPair = getVP8XDimension(paramInputStream);
        localObject = localPair;
        if (paramInputStream == null) {
          break label255;
        }
        try
        {
          paramInputStream.close();
          return localPair;
        }
        catch (IOException paramInputStream)
        {
          paramInputStream.printStackTrace();
          return localPair;
        }
      }
      if (paramInputStream == null) {
        break label257;
      }
      paramInputStream.close();
    }
    catch (Throwable localThrowable)
    {
      try
      {
        paramInputStream.close();
        return null;
      }
      catch (IOException paramInputStream)
      {
        ((IOException)paramInputStream).printStackTrace();
        return null;
      }
      localThrowable = localThrowable;
    }
    catch (IOException localIOException)
    {
      ((IOException)localIOException).printStackTrace();
      if (paramInputStream == null) {
        break label257;
      }
    }
    return null;
    if (paramInputStream != null) {
      try
      {
        paramInputStream.close();
      }
      catch (IOException paramInputStream)
      {
        paramInputStream.printStackTrace();
      }
    }
    throw localIOException;
    label253:
    return null;
    label255:
    return localIOException;
    label257:
    return null;
  }
  
  private static Pair getVP8Dimension(InputStream paramInputStream)
    throws IOException
  {
    paramInputStream.skip(7L);
    int i = getShort(paramInputStream);
    int j = getShort(paramInputStream);
    int k = getShort(paramInputStream);
    if ((i == 157) && (j == 1) && (k == 42)) {
      return new Pair(Integer.valueOf(get2BytesAsInt(paramInputStream)), Integer.valueOf(get2BytesAsInt(paramInputStream)));
    }
    return null;
  }
  
  private static Pair getVP8LDimension(InputStream paramInputStream)
    throws IOException
  {
    getInt(paramInputStream);
    if (getByte(paramInputStream) != 47) {
      return null;
    }
    int i = (byte)paramInputStream.read();
    int j = (byte)paramInputStream.read() & 0xFF;
    int k = (byte)paramInputStream.read();
    return new Pair(Integer.valueOf((i & 0xFF | (j & 0x3F) << 8) + 1), Integer.valueOf((((byte)paramInputStream.read() & 0xFF & 0xF) << 10 | (k & 0xFF) << 2 | (j & 0xC0) >> 6) + 1));
  }
  
  private static Pair getVP8XDimension(InputStream paramInputStream)
    throws IOException
  {
    paramInputStream.skip(8L);
    return new Pair(Integer.valueOf(read3Bytes(paramInputStream) + 1), Integer.valueOf(read3Bytes(paramInputStream) + 1));
  }
  
  private static boolean isBitOne(byte paramByte, int paramInt)
  {
    return (paramByte >> paramInt % 8 & 0x1) == 1;
  }
  
  private static int read3Bytes(InputStream paramInputStream)
    throws IOException
  {
    int i = getByte(paramInputStream);
    int j = getByte(paramInputStream);
    return getByte(paramInputStream) << 16 & 0xFF0000 | j << 8 & 0xFF00 | i & 0xFF;
  }
}
