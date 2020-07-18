package com.facebook.imageformat;

import com.facebook.common.internal.ByteStreams;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Throwables;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class ImageFormatChecker
{
  private static ImageFormatChecker sInstance;
  @Nullable
  private List<ImageFormat.FormatChecker> mCustomImageFormatCheckers;
  private final ImageFormat.FormatChecker mDefaultFormatChecker = new DefaultImageFormatChecker();
  private int mMaxHeaderLength;
  
  private ImageFormatChecker()
  {
    updateMaxHeaderLength();
  }
  
  public static ImageFormat getImageFormat(InputStream paramInputStream)
    throws IOException
  {
    return getInstance().determineImageFormat(paramInputStream);
  }
  
  /* Error */
  public static ImageFormat getImageFormat(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_1
    //   4: new 44	java/io/FileInputStream
    //   7: dup
    //   8: aload_0
    //   9: invokespecial 47	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   12: astore_0
    //   13: aload_0
    //   14: invokestatic 49	com/facebook/imageformat/ImageFormatChecker:getImageFormat	(Ljava/io/InputStream;)Lcom/facebook/imageformat/ImageFormat;
    //   17: astore_1
    //   18: aload_0
    //   19: invokestatic 55	com/facebook/common/internal/Closeables:closeQuietly	(Ljava/io/InputStream;)V
    //   22: aload_1
    //   23: areturn
    //   24: astore_2
    //   25: aload_0
    //   26: astore_1
    //   27: aload_2
    //   28: astore_0
    //   29: goto +22 -> 51
    //   32: goto +7 -> 39
    //   35: astore_0
    //   36: goto +15 -> 51
    //   39: aload_0
    //   40: astore_1
    //   41: getstatic 61	com/facebook/imageformat/ImageFormat:UNKNOWN	Lcom/facebook/imageformat/ImageFormat;
    //   44: astore_2
    //   45: aload_0
    //   46: invokestatic 55	com/facebook/common/internal/Closeables:closeQuietly	(Ljava/io/InputStream;)V
    //   49: aload_2
    //   50: areturn
    //   51: aload_1
    //   52: invokestatic 55	com/facebook/common/internal/Closeables:closeQuietly	(Ljava/io/InputStream;)V
    //   55: aload_0
    //   56: athrow
    //   57: astore_0
    //   58: aload_2
    //   59: astore_0
    //   60: goto -21 -> 39
    //   63: astore_1
    //   64: goto -32 -> 32
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	67	0	paramString	String
    //   3	49	1	localObject1	Object
    //   63	1	1	localIOException	IOException
    //   1	1	2	localObject2	Object
    //   24	4	2	localThrowable	Throwable
    //   44	15	2	localImageFormat	ImageFormat
    // Exception table:
    //   from	to	target	type
    //   13	18	24	java/lang/Throwable
    //   4	13	35	java/lang/Throwable
    //   41	45	35	java/lang/Throwable
    //   4	13	57	java/io/IOException
    //   13	18	63	java/io/IOException
  }
  
  public static ImageFormat getImageFormat_WrapIOException(InputStream paramInputStream)
  {
    try
    {
      paramInputStream = getImageFormat(paramInputStream);
      return paramInputStream;
    }
    catch (IOException paramInputStream)
    {
      throw Throwables.propagate(paramInputStream);
    }
  }
  
  public static ImageFormatChecker getInstance()
  {
    try
    {
      if (sInstance == null) {
        sInstance = new ImageFormatChecker();
      }
      ImageFormatChecker localImageFormatChecker = sInstance;
      return localImageFormatChecker;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private static int readHeaderFromStream(int paramInt, InputStream paramInputStream, byte[] paramArrayOfByte)
    throws IOException
  {
    Preconditions.checkNotNull(paramInputStream);
    Preconditions.checkNotNull(paramArrayOfByte);
    boolean bool;
    if (paramArrayOfByte.length >= paramInt) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
    if (paramInputStream.markSupported()) {
      try
      {
        paramInputStream.mark(paramInt);
        paramInt = ByteStreams.read(paramInputStream, paramArrayOfByte, 0, paramInt);
        paramInputStream.reset();
        return paramInt;
      }
      catch (Throwable paramArrayOfByte)
      {
        paramInputStream.reset();
        throw paramArrayOfByte;
      }
    }
    return ByteStreams.read(paramInputStream, paramArrayOfByte, 0, paramInt);
  }
  
  private void updateMaxHeaderLength()
  {
    mMaxHeaderLength = mDefaultFormatChecker.getHeaderSize();
    if (mCustomImageFormatCheckers != null)
    {
      Iterator localIterator = mCustomImageFormatCheckers.iterator();
      while (localIterator.hasNext())
      {
        ImageFormat.FormatChecker localFormatChecker = (ImageFormat.FormatChecker)localIterator.next();
        mMaxHeaderLength = Math.max(mMaxHeaderLength, localFormatChecker.getHeaderSize());
      }
    }
  }
  
  public ImageFormat determineImageFormat(InputStream paramInputStream)
    throws IOException
  {
    Preconditions.checkNotNull(paramInputStream);
    byte[] arrayOfByte = new byte[mMaxHeaderLength];
    int i = readHeaderFromStream(mMaxHeaderLength, paramInputStream, arrayOfByte);
    paramInputStream = mDefaultFormatChecker.determineFormat(arrayOfByte, i);
    if ((paramInputStream != null) && (paramInputStream != ImageFormat.UNKNOWN)) {
      return paramInputStream;
    }
    if (mCustomImageFormatCheckers != null)
    {
      paramInputStream = mCustomImageFormatCheckers.iterator();
      while (paramInputStream.hasNext())
      {
        ImageFormat localImageFormat = ((ImageFormat.FormatChecker)paramInputStream.next()).determineFormat(arrayOfByte, i);
        if ((localImageFormat != null) && (localImageFormat != ImageFormat.UNKNOWN)) {
          return localImageFormat;
        }
      }
    }
    return ImageFormat.UNKNOWN;
  }
  
  public void setCustomImageFormatCheckers(List paramList)
  {
    mCustomImageFormatCheckers = paramList;
    updateMaxHeaderLength();
  }
}