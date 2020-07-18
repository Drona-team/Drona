package com.facebook.imagepipeline.platform;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.memory.FlexByteArrayPool;
import com.facebook.imagepipeline.nativecode.DalvikPurgeableDecoder;
import javax.annotation.concurrent.ThreadSafe;

@TargetApi(19)
@ThreadSafe
public class KitKatPurgeableDecoder
  extends DalvikPurgeableDecoder
{
  private final FlexByteArrayPool mFlexByteArrayPool;
  
  public KitKatPurgeableDecoder(FlexByteArrayPool paramFlexByteArrayPool)
  {
    mFlexByteArrayPool = paramFlexByteArrayPool;
  }
  
  private static void putEOI(byte[] paramArrayOfByte, int paramInt)
  {
    paramArrayOfByte[paramInt] = -1;
    paramArrayOfByte[(paramInt + 1)] = -39;
  }
  
  protected Bitmap decodeByteArrayAsPurgeable(CloseableReference paramCloseableReference, BitmapFactory.Options paramOptions)
  {
    PooledByteBuffer localPooledByteBuffer = (PooledByteBuffer)paramCloseableReference.get();
    int i = localPooledByteBuffer.size();
    paramCloseableReference = mFlexByteArrayPool.calculateDimensions(i);
    try
    {
      byte[] arrayOfByte = (byte[])paramCloseableReference.get();
      localPooledByteBuffer.read(0, arrayOfByte, 0, i);
      paramOptions = (Bitmap)Preconditions.checkNotNull(BitmapFactory.decodeByteArray(arrayOfByte, 0, i, paramOptions), "BitmapFactory returned null");
      CloseableReference.closeSafely(paramCloseableReference);
      return paramOptions;
    }
    catch (Throwable paramOptions)
    {
      CloseableReference.closeSafely(paramCloseableReference);
      throw paramOptions;
    }
  }
  
  protected Bitmap decodeJPEGByteArrayAsPurgeable(CloseableReference paramCloseableReference, int paramInt, BitmapFactory.Options paramOptions)
  {
    byte[] arrayOfByte1;
    if (DalvikPurgeableDecoder.endsWithEOI(paramCloseableReference, paramInt)) {
      arrayOfByte1 = null;
    } else {
      arrayOfByte1 = DalvikPurgeableDecoder.EOF_MARKER;
    }
    PooledByteBuffer localPooledByteBuffer = (PooledByteBuffer)paramCloseableReference.get();
    boolean bool;
    if (paramInt <= localPooledByteBuffer.size()) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
    paramCloseableReference = mFlexByteArrayPool;
    int j = paramInt + 2;
    paramCloseableReference = paramCloseableReference.calculateDimensions(j);
    try
    {
      byte[] arrayOfByte2 = (byte[])paramCloseableReference.get();
      localPooledByteBuffer.read(0, arrayOfByte2, 0, paramInt);
      int i = paramInt;
      if (arrayOfByte1 != null)
      {
        putEOI(arrayOfByte2, paramInt);
        i = j;
      }
      paramOptions = (Bitmap)Preconditions.checkNotNull(BitmapFactory.decodeByteArray(arrayOfByte2, 0, i, paramOptions), "BitmapFactory returned null");
      CloseableReference.closeSafely(paramCloseableReference);
      return paramOptions;
    }
    catch (Throwable paramOptions)
    {
      CloseableReference.closeSafely(paramCloseableReference);
      throw paramOptions;
    }
  }
}
