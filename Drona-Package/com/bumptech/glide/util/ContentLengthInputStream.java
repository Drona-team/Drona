package com.bumptech.glide.util;

import android.text.TextUtils;
import android.util.Log;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ContentLengthInputStream
  extends FilterInputStream
{
  private static final String TAG = "ContentLengthStream";
  private static final int UNKNOWN = -1;
  private final long contentLength;
  private int readSoFar;
  
  private ContentLengthInputStream(InputStream paramInputStream, long paramLong)
  {
    super(paramInputStream);
    contentLength = paramLong;
  }
  
  private int checkReadSoFarOrThrow(int paramInt)
    throws IOException
  {
    if (paramInt >= 0)
    {
      readSoFar += paramInt;
      return paramInt;
    }
    if (contentLength - readSoFar <= 0L) {
      return paramInt;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Failed to read all expected data, expected: ");
    localStringBuilder.append(contentLength);
    localStringBuilder.append(", but read: ");
    localStringBuilder.append(readSoFar);
    throw new IOException(localStringBuilder.toString());
  }
  
  public static InputStream obtain(InputStream paramInputStream, long paramLong)
  {
    return new ContentLengthInputStream(paramInputStream, paramLong);
  }
  
  public static InputStream obtain(InputStream paramInputStream, String paramString)
  {
    return obtain(paramInputStream, parseContentLength(paramString));
  }
  
  private static int parseContentLength(String paramString)
  {
    if (!TextUtils.isEmpty(paramString)) {
      try
      {
        int i = Integer.parseInt(paramString);
        return i;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        if (Log.isLoggable("ContentLengthStream", 3))
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("failed to parse content length header: ");
          localStringBuilder.append(paramString);
          Log.d("ContentLengthStream", localStringBuilder.toString(), localNumberFormatException);
        }
      }
    }
    return -1;
  }
  
  public int available()
    throws IOException
  {
    try
    {
      long l = Math.max(contentLength - readSoFar, in.available());
      int i = (int)l;
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int read()
    throws IOException
  {
    for (;;)
    {
      try
      {
        int j = super.read();
        if (j >= 0)
        {
          i = 1;
          checkReadSoFarOrThrow(i);
          return j;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      int i = -1;
    }
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    try
    {
      paramInt1 = checkReadSoFarOrThrow(super.read(paramArrayOfByte, paramInt1, paramInt2));
      return paramInt1;
    }
    catch (Throwable paramArrayOfByte)
    {
      throw paramArrayOfByte;
    }
  }
}
