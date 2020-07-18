package com.facebook.imagepipeline.image;

import android.graphics.ColorSpace;
import android.util.Pair;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferInputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.SharedReference;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imageformat.ImageFormatChecker;
import com.facebook.imagepipeline.common.BytesRange;
import com.facebook.imageutils.HeifExifUtil;
import com.facebook.imageutils.ImageMetaData;
import com.facebook.imageutils.JfifUtil;
import com.facebook.imageutils.WebpUtil;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class EncodedImage
  implements Closeable
{
  public static final int DEFAULT_SAMPLE_SIZE = 1;
  public static final int UNKNOWN_HEIGHT = -1;
  public static final int UNKNOWN_ROTATION_ANGLE = -1;
  public static final int UNKNOWN_STREAM_SIZE = -1;
  public static final int UNKNOWN_WIDTH = -1;
  @Nullable
  private BytesRange mBytesRange;
  @Nullable
  private ColorSpace mColorSpace;
  private int mExifOrientation = 0;
  private int mHeight = -1;
  private ImageFormat mImageFormat = ImageFormat.UNKNOWN;
  @Nullable
  private final Supplier<FileInputStream> mInputStreamSupplier;
  @Nullable
  private final CloseableReference<PooledByteBuffer> mPooledByteBufferRef;
  private int mRotationAngle = -1;
  private int mSampleSize = 1;
  private int mStreamSize = -1;
  private int mWidth = -1;
  
  public EncodedImage(Supplier paramSupplier)
  {
    Preconditions.checkNotNull(paramSupplier);
    mPooledByteBufferRef = null;
    mInputStreamSupplier = paramSupplier;
  }
  
  public EncodedImage(Supplier paramSupplier, int paramInt)
  {
    this(paramSupplier);
    mStreamSize = paramInt;
  }
  
  public EncodedImage(CloseableReference paramCloseableReference)
  {
    Preconditions.checkArgument(CloseableReference.isValid(paramCloseableReference));
    mPooledByteBufferRef = paramCloseableReference.clone();
    mInputStreamSupplier = null;
  }
  
  public static EncodedImage cloneOrNull(EncodedImage paramEncodedImage)
  {
    if (paramEncodedImage != null) {
      return paramEncodedImage.cloneOrNull();
    }
    return null;
  }
  
  public static void closeSafely(EncodedImage paramEncodedImage)
  {
    if (paramEncodedImage != null) {
      paramEncodedImage.close();
    }
  }
  
  public static boolean isMetaDataAvailable(EncodedImage paramEncodedImage)
  {
    return (mRotationAngle >= 0) && (mWidth >= 0) && (mHeight >= 0);
  }
  
  public static boolean isValid(EncodedImage paramEncodedImage)
  {
    return (paramEncodedImage != null) && (paramEncodedImage.isValid());
  }
  
  private void parseMetaDataIfNeeded()
  {
    if ((mWidth < 0) || (mHeight < 0)) {
      parseMetaData();
    }
  }
  
  /* Error */
  private ImageMetaData readImageMetaData()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 116	com/facebook/imagepipeline/image/EncodedImage:getInputStream	()Ljava/io/InputStream;
    //   4: astore_2
    //   5: aload_2
    //   6: astore_1
    //   7: aload_2
    //   8: invokestatic 122	com/facebook/imageutils/BitmapUtil:decodeDimensionsAndColorSpace	(Ljava/io/InputStream;)Lcom/facebook/imageutils/ImageMetaData;
    //   11: astore_3
    //   12: aload_0
    //   13: aload_3
    //   14: invokevirtual 128	com/facebook/imageutils/ImageMetaData:getColorSpace	()Landroid/graphics/ColorSpace;
    //   17: putfield 130	com/facebook/imagepipeline/image/EncodedImage:mColorSpace	Landroid/graphics/ColorSpace;
    //   20: aload_3
    //   21: invokevirtual 134	com/facebook/imageutils/ImageMetaData:getDimensions	()Landroid/util/Pair;
    //   24: astore 4
    //   26: aload 4
    //   28: ifnull +33 -> 61
    //   31: aload_0
    //   32: aload 4
    //   34: getfield 140	android/util/Pair:first	Ljava/lang/Object;
    //   37: checkcast 142	java/lang/Integer
    //   40: invokevirtual 146	java/lang/Integer:intValue	()I
    //   43: putfield 52	com/facebook/imagepipeline/image/EncodedImage:mWidth	I
    //   46: aload_0
    //   47: aload 4
    //   49: getfield 149	android/util/Pair:second	Ljava/lang/Object;
    //   52: checkcast 142	java/lang/Integer
    //   55: invokevirtual 146	java/lang/Integer:intValue	()I
    //   58: putfield 54	com/facebook/imagepipeline/image/EncodedImage:mHeight	I
    //   61: aload_2
    //   62: ifnull +37 -> 99
    //   65: aload_2
    //   66: invokevirtual 152	java/io/InputStream:close	()V
    //   69: aload_3
    //   70: areturn
    //   71: astore_3
    //   72: aload_1
    //   73: astore_2
    //   74: aload_3
    //   75: astore_1
    //   76: goto +6 -> 82
    //   79: astore_1
    //   80: aconst_null
    //   81: astore_2
    //   82: aload_2
    //   83: ifnull +7 -> 90
    //   86: aload_2
    //   87: invokevirtual 152	java/io/InputStream:close	()V
    //   90: aload_1
    //   91: athrow
    //   92: astore_1
    //   93: aload_3
    //   94: areturn
    //   95: astore_2
    //   96: goto -6 -> 90
    //   99: aload_3
    //   100: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	101	0	this	EncodedImage
    //   6	70	1	localObject1	Object
    //   79	12	1	localThrowable1	Throwable
    //   92	1	1	localIOException1	java.io.IOException
    //   4	83	2	localObject2	Object
    //   95	1	2	localIOException2	java.io.IOException
    //   11	59	3	localImageMetaData	ImageMetaData
    //   71	29	3	localThrowable2	Throwable
    //   24	24	4	localPair	Pair
    // Exception table:
    //   from	to	target	type
    //   7	26	71	java/lang/Throwable
    //   31	61	71	java/lang/Throwable
    //   0	5	79	java/lang/Throwable
    //   65	69	92	java/io/IOException
    //   86	90	95	java/io/IOException
  }
  
  private Pair readWebPImageSize()
  {
    Pair localPair = WebpUtil.getSize(getInputStream());
    if (localPair != null)
    {
      mWidth = ((Integer)first).intValue();
      mHeight = ((Integer)second).intValue();
    }
    return localPair;
  }
  
  public EncodedImage cloneOrNull()
  {
    EncodedImage localEncodedImage;
    CloseableReference localCloseableReference;
    if (mInputStreamSupplier != null)
    {
      localEncodedImage = new EncodedImage(mInputStreamSupplier, mStreamSize);
    }
    else
    {
      localCloseableReference = CloseableReference.cloneOrNull(mPooledByteBufferRef);
      if (localCloseableReference == null) {
        localEncodedImage = null;
      }
    }
    try
    {
      localEncodedImage = new EncodedImage(localCloseableReference);
      CloseableReference.closeSafely(localCloseableReference);
      if (localEncodedImage != null)
      {
        localEncodedImage.copyMetaDataFrom(this);
        return localEncodedImage;
      }
    }
    catch (Throwable localThrowable)
    {
      CloseableReference.closeSafely(localCloseableReference);
      throw localThrowable;
    }
    return localThrowable;
  }
  
  public void close()
  {
    CloseableReference.closeSafely(mPooledByteBufferRef);
  }
  
  public void copyMetaDataFrom(EncodedImage paramEncodedImage)
  {
    mImageFormat = paramEncodedImage.getImageFormat();
    mWidth = paramEncodedImage.getWidth();
    mHeight = paramEncodedImage.getHeight();
    mRotationAngle = paramEncodedImage.getRotationAngle();
    mExifOrientation = paramEncodedImage.getExifOrientation();
    mSampleSize = paramEncodedImage.getSampleSize();
    mStreamSize = paramEncodedImage.getSize();
    mBytesRange = paramEncodedImage.getBytesRange();
    mColorSpace = paramEncodedImage.getColorSpace();
  }
  
  public CloseableReference getByteBufferRef()
  {
    return CloseableReference.cloneOrNull(mPooledByteBufferRef);
  }
  
  public BytesRange getBytesRange()
  {
    return mBytesRange;
  }
  
  public ColorSpace getColorSpace()
  {
    parseMetaDataIfNeeded();
    return mColorSpace;
  }
  
  public int getExifOrientation()
  {
    parseMetaDataIfNeeded();
    return mExifOrientation;
  }
  
  public String getFirstBytesAsHexString(int paramInt)
  {
    Object localObject = getByteBufferRef();
    if (localObject == null) {
      return "";
    }
    paramInt = Math.min(getSize(), paramInt);
    byte[] arrayOfByte = new byte[paramInt];
    try
    {
      PooledByteBuffer localPooledByteBuffer = (PooledByteBuffer)((CloseableReference)localObject).get();
      if (localPooledByteBuffer == null)
      {
        ((CloseableReference)localObject).close();
        return "";
      }
      localPooledByteBuffer.read(0, arrayOfByte, 0, paramInt);
      ((CloseableReference)localObject).close();
      localObject = new StringBuilder(arrayOfByte.length * 2);
      int i = arrayOfByte.length;
      paramInt = 0;
      while (paramInt < i)
      {
        ((StringBuilder)localObject).append(String.format("%02X", new Object[] { Byte.valueOf(arrayOfByte[paramInt]) }));
        paramInt += 1;
      }
      return ((StringBuilder)localObject).toString();
    }
    catch (Throwable localThrowable)
    {
      ((CloseableReference)localObject).close();
      throw localThrowable;
    }
  }
  
  public int getHeight()
  {
    parseMetaDataIfNeeded();
    return mHeight;
  }
  
  public ImageFormat getImageFormat()
  {
    parseMetaDataIfNeeded();
    return mImageFormat;
  }
  
  public InputStream getInputStream()
  {
    if (mInputStreamSupplier != null) {
      return (InputStream)mInputStreamSupplier.getFolder();
    }
    CloseableReference localCloseableReference = CloseableReference.cloneOrNull(mPooledByteBufferRef);
    if (localCloseableReference != null) {
      try
      {
        PooledByteBufferInputStream localPooledByteBufferInputStream = new PooledByteBufferInputStream((PooledByteBuffer)localCloseableReference.get());
        CloseableReference.closeSafely(localCloseableReference);
        return localPooledByteBufferInputStream;
      }
      catch (Throwable localThrowable)
      {
        CloseableReference.closeSafely(localCloseableReference);
        throw localThrowable;
      }
    }
    return null;
  }
  
  public int getRotationAngle()
  {
    parseMetaDataIfNeeded();
    return mRotationAngle;
  }
  
  public int getSampleSize()
  {
    return mSampleSize;
  }
  
  public int getSize()
  {
    if ((mPooledByteBufferRef != null) && (mPooledByteBufferRef.get() != null)) {
      return ((PooledByteBuffer)mPooledByteBufferRef.get()).size();
    }
    return mStreamSize;
  }
  
  public SharedReference getUnderlyingReferenceTestOnly()
  {
    try
    {
      SharedReference localSharedReference;
      if (mPooledByteBufferRef != null) {
        localSharedReference = mPooledByteBufferRef.getUnderlyingReferenceTestOnly();
      } else {
        localSharedReference = null;
      }
      return localSharedReference;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int getWidth()
  {
    parseMetaDataIfNeeded();
    return mWidth;
  }
  
  public boolean isCompleteAt(int paramInt)
  {
    if (mImageFormat != DefaultImageFormats.JPEG) {
      return true;
    }
    if (mInputStreamSupplier != null) {
      return true;
    }
    Preconditions.checkNotNull(mPooledByteBufferRef);
    PooledByteBuffer localPooledByteBuffer = (PooledByteBuffer)mPooledByteBufferRef.get();
    return (localPooledByteBuffer.read(paramInt - 2) == -1) && (localPooledByteBuffer.read(paramInt - 1) == -39);
  }
  
  public boolean isValid()
  {
    try
    {
      if (!CloseableReference.isValid(mPooledByteBufferRef))
      {
        Supplier localSupplier = mInputStreamSupplier;
        if (localSupplier == null)
        {
          bool = false;
          break label31;
        }
      }
      boolean bool = true;
      label31:
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void parseMetaData()
  {
    ImageFormat localImageFormat = ImageFormatChecker.getImageFormat_WrapIOException(getInputStream());
    mImageFormat = localImageFormat;
    Pair localPair;
    if (DefaultImageFormats.isWebpFormat(localImageFormat)) {
      localPair = readWebPImageSize();
    } else {
      localPair = readImageMetaData().getDimensions();
    }
    if ((localImageFormat == DefaultImageFormats.JPEG) && (mRotationAngle == -1))
    {
      if (localPair != null)
      {
        mExifOrientation = JfifUtil.getOrientation(getInputStream());
        mRotationAngle = JfifUtil.getAutoRotateAngleFromOrientation(mExifOrientation);
      }
    }
    else
    {
      if ((localImageFormat == DefaultImageFormats.HEIF) && (mRotationAngle == -1))
      {
        mExifOrientation = HeifExifUtil.getOrientation(getInputStream());
        mRotationAngle = JfifUtil.getAutoRotateAngleFromOrientation(mExifOrientation);
        return;
      }
      mRotationAngle = 0;
    }
  }
  
  public void setBytesRange(BytesRange paramBytesRange)
  {
    mBytesRange = paramBytesRange;
  }
  
  public void setExifOrientation(int paramInt)
  {
    mExifOrientation = paramInt;
  }
  
  public void setHeight(int paramInt)
  {
    mHeight = paramInt;
  }
  
  public void setImageFormat(ImageFormat paramImageFormat)
  {
    mImageFormat = paramImageFormat;
  }
  
  public void setRotationAngle(int paramInt)
  {
    mRotationAngle = paramInt;
  }
  
  public void setSampleSize(int paramInt)
  {
    mSampleSize = paramInt;
  }
  
  public void setStreamSize(int paramInt)
  {
    mStreamSize = paramInt;
  }
  
  public void setWidth(int paramInt)
  {
    mWidth = paramInt;
  }
}
