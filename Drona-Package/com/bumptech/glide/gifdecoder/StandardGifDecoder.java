package com.bumptech.glide.gifdecoder;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class StandardGifDecoder
  implements GifDecoder
{
  private static final int BYTES_PER_INTEGER = 4;
  @ColorInt
  private static final int COLOR_TRANSPARENT_BLACK = 0;
  private static final int INITIAL_FRAME_POINTER = -1;
  private static final int MASK_INT_LOWEST_BYTE = 255;
  private static final int MAX_STACK_SIZE = 4096;
  private static final int NULL_CODE = -1;
  private static final String TAG = "StandardGifDecoder";
  @ColorInt
  private int[] act;
  @NonNull
  private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
  private final GifDecoder.BitmapProvider bitmapProvider;
  private byte[] block;
  private int downsampledHeight;
  private int downsampledWidth;
  private int framePointer;
  @ColorInt
  private final int[] gct = new int['?'];
  private GifHeader header;
  @Nullable
  private Boolean isFirstFrameTransparent;
  private byte[] mainPixels;
  @ColorInt
  private int[] mainScratch;
  private GifHeaderParser parser;
  private byte[] pixelStack;
  private short[] prefix;
  private Bitmap previousImage;
  private ByteBuffer rawData;
  private int sampleSize;
  private boolean savePrevious;
  private int status;
  private byte[] suffix;
  
  public StandardGifDecoder(GifDecoder.BitmapProvider paramBitmapProvider)
  {
    bitmapProvider = paramBitmapProvider;
    header = new GifHeader();
  }
  
  public StandardGifDecoder(GifDecoder.BitmapProvider paramBitmapProvider, GifHeader paramGifHeader, ByteBuffer paramByteBuffer)
  {
    this(paramBitmapProvider, paramGifHeader, paramByteBuffer, 1);
  }
  
  public StandardGifDecoder(GifDecoder.BitmapProvider paramBitmapProvider, GifHeader paramGifHeader, ByteBuffer paramByteBuffer, int paramInt)
  {
    this(paramBitmapProvider);
    setData(paramGifHeader, paramByteBuffer, paramInt);
  }
  
  private int averageColorsNear(int paramInt1, int paramInt2, int paramInt3)
  {
    int i1 = paramInt1;
    int n = 0;
    int m = 0;
    int k = 0;
    int j = 0;
    int i6;
    int i4;
    int i3;
    for (int i = 0; (i1 < sampleSize + paramInt1) && (i1 < mainPixels.length) && (i1 < paramInt2); i = i2)
    {
      i2 = mainPixels[i1];
      int i7 = act[(i2 & 0xFF)];
      i6 = n;
      i5 = m;
      i4 = k;
      i3 = j;
      i2 = i;
      if (i7 != 0)
      {
        i6 = n + (i7 >> 24 & 0xFF);
        i5 = m + (i7 >> 16 & 0xFF);
        i4 = k + (i7 >> 8 & 0xFF);
        i3 = j + (i7 & 0xFF);
        i2 = i + 1;
      }
      i1 += 1;
      n = i6;
      m = i5;
      k = i4;
      j = i3;
    }
    int i5 = paramInt1 + paramInt3;
    paramInt1 = i5;
    int i2 = n;
    while ((paramInt1 < sampleSize + i5) && (paramInt1 < mainPixels.length) && (paramInt1 < paramInt2))
    {
      paramInt3 = mainPixels[paramInt1];
      i6 = act[(paramInt3 & 0xFF)];
      i4 = i2;
      i3 = m;
      i1 = k;
      n = j;
      paramInt3 = i;
      if (i6 != 0)
      {
        i4 = i2 + (i6 >> 24 & 0xFF);
        i3 = m + (i6 >> 16 & 0xFF);
        i1 = k + (i6 >> 8 & 0xFF);
        n = j + (i6 & 0xFF);
        paramInt3 = i + 1;
      }
      paramInt1 += 1;
      i2 = i4;
      m = i3;
      k = i1;
      j = n;
      i = paramInt3;
    }
    if (i == 0) {
      return 0;
    }
    return i2 / i << 24 | m / i << 16 | k / i << 8 | j / i;
  }
  
  private void copyCopyIntoScratchRobust(GifFrame paramGifFrame)
  {
    int[] arrayOfInt1 = mainScratch;
    int i6 = ih / sampleSize;
    int i7 = iy / sampleSize;
    int i8 = iw / sampleSize;
    int i9 = ix / sampleSize;
    int i3;
    if (framePointer == 0) {
      i3 = 1;
    } else {
      i3 = 0;
    }
    int i10 = sampleSize;
    int i11 = downsampledWidth;
    int i12 = downsampledHeight;
    byte[] arrayOfByte = mainPixels;
    int[] arrayOfInt2 = act;
    Object localObject1 = isFirstFrameTransparent;
    int k = 0;
    int m = 0;
    int i1 = 1;
    int n = 8;
    while (m < i6)
    {
      int i;
      int i2;
      int j;
      int i4;
      if (interlace)
      {
        i = k;
        i2 = i1;
        j = n;
        if (k >= i6)
        {
          i2 = i1 + 1;
          switch (i2)
          {
          default: 
            i = k;
            j = n;
            break;
          case 4: 
            i = 1;
            j = 2;
            break;
          case 3: 
            i = 2;
            j = 4;
            break;
          case 2: 
            i = 4;
            j = n;
          }
        }
        i4 = i + j;
        k = i;
        i1 = i2;
        n = j;
        i = i4;
      }
      else
      {
        i = k;
        j = m;
        k = j;
      }
      k += i7;
      if (i10 == 1) {
        j = 1;
      } else {
        j = 0;
      }
      Object localObject2 = localObject1;
      if (k < i12)
      {
        i2 = k * i11;
        i4 = i2 + i9;
        k = i4 + i8;
        int i5 = i2 + i11;
        i2 = k;
        if (i5 < k) {
          i2 = i5;
        }
        i5 = m * i10 * iw;
        if (j != 0) {
          for (;;)
          {
            localObject2 = localObject1;
            if (i4 >= i2) {
              break;
            }
            j = arrayOfInt2[(arrayOfByte[i5] & 0xFF)];
            if (j != 0)
            {
              arrayOfInt1[i4] = j;
              localObject2 = localObject1;
            }
            else
            {
              localObject2 = localObject1;
              if (i3 != 0)
              {
                localObject2 = localObject1;
                if (localObject1 == null) {
                  localObject2 = Boolean.valueOf(true);
                }
              }
            }
            i5 += i10;
            i4 += 1;
            localObject1 = localObject2;
          }
        }
        k = i4;
        j = i5;
        for (;;)
        {
          localObject2 = localObject1;
          if (k >= i2) {
            break;
          }
          int i13 = averageColorsNear(j, (i2 - i4) * i10 + i5, iw);
          if (i13 != 0) {
            arrayOfInt1[k] = i13;
          }
          for (;;)
          {
            break;
            if ((i3 != 0) && (localObject1 == null)) {
              localObject1 = Boolean.valueOf(true);
            }
          }
          j += i10;
          k += 1;
        }
      }
      m += 1;
      localObject1 = localObject2;
      k = i;
    }
    if (isFirstFrameTransparent == null)
    {
      boolean bool;
      if (localObject1 == null) {
        bool = false;
      } else {
        bool = ((Boolean)localObject1).booleanValue();
      }
      isFirstFrameTransparent = Boolean.valueOf(bool);
    }
  }
  
  private void copyIntoScratchFast(GifFrame paramGifFrame)
  {
    int[] arrayOfInt1 = mainScratch;
    int i4 = ih;
    int i5 = iy;
    int i6 = iw;
    int i7 = ix;
    int i;
    if (framePointer == 0) {
      i = 1;
    } else {
      i = 0;
    }
    int i8 = downsampledWidth;
    byte[] arrayOfByte = mainPixels;
    int[] arrayOfInt2 = act;
    int j = 0;
    int n = -1;
    while (j < i4)
    {
      int k = (j + i5) * i8;
      int m = k + i7;
      int i1 = m + i6;
      int i2 = k + i8;
      k = i1;
      if (i2 < i1) {
        k = i2;
      }
      i1 = iw * j;
      while (m < k)
      {
        int i3 = arrayOfByte[i1];
        int i9 = i3 & 0xFF;
        i2 = n;
        if (i9 != n)
        {
          i2 = arrayOfInt2[i9];
          if (i2 != 0)
          {
            arrayOfInt1[m] = i2;
            i2 = n;
          }
          else
          {
            i2 = i3;
          }
        }
        i1 += 1;
        m += 1;
        n = i2;
      }
      j += 1;
    }
    boolean bool;
    if ((isFirstFrameTransparent == null) && (i != 0) && (n != -1)) {
      bool = true;
    } else {
      bool = false;
    }
    isFirstFrameTransparent = Boolean.valueOf(bool);
  }
  
  private void decodeBitmapData(GifFrame paramGifFrame)
  {
    StandardGifDecoder localStandardGifDecoder = this;
    if (paramGifFrame != null) {
      rawData.position(bufferFrameStart);
    }
    int i7;
    if (paramGifFrame == null)
    {
      i7 = header.width * header.height;
    }
    else
    {
      j = iw;
      i7 = ih * j;
    }
    if ((mainPixels == null) || (mainPixels.length < i7)) {
      mainPixels = bitmapProvider.obtainByteArray(i7);
    }
    byte[] arrayOfByte1 = mainPixels;
    if (prefix == null) {
      prefix = new short['?'];
    }
    short[] arrayOfShort = prefix;
    if (suffix == null) {
      suffix = new byte['?'];
    }
    byte[] arrayOfByte2 = suffix;
    if (pixelStack == null) {
      pixelStack = new byte['?'];
    }
    byte[] arrayOfByte3 = pixelStack;
    int j = readByte();
    int i19 = 1 << j;
    int i8 = i19 + 2;
    int i9 = j + 1;
    int i10 = (1 << i9) - 1;
    int i5 = 0;
    j = 0;
    while (j < i19)
    {
      arrayOfShort[j] = 0;
      arrayOfByte2[j] = ((byte)j);
      j += 1;
    }
    byte[] arrayOfByte4 = block;
    int i15 = i9;
    int i16 = i8;
    int i14 = i10;
    int n = 0;
    int i6 = 0;
    j = 0;
    int k = 0;
    int i1 = 0;
    int i2 = -1;
    int i13 = 0;
    int m = 0;
    paramGifFrame = localStandardGifDecoder;
    while (i5 < i7)
    {
      int i3 = n;
      if (n == 0)
      {
        n = readBlock();
        i3 = n;
        if (n <= 0)
        {
          status = 3;
          break;
        }
        i1 = 0;
      }
      k += ((arrayOfByte4[i1] & 0xFF) << j);
      int i17 = i1 + 1;
      int i18 = i3 - 1;
      int i4 = j + 8;
      i3 = k;
      n = i16;
      k = i15;
      j = i14;
      for (;;)
      {
        if (i4 >= k)
        {
          i1 = i3 & j;
          int i11 = i3 >> k;
          int i12 = i4 - k;
          if (i1 == i19)
          {
            k = i9;
            n = i8;
            j = i10;
            i1 = -1;
            i3 = i6;
            i6 = i3;
            i4 = i12;
            i3 = i11;
            i2 = i1;
          }
          else
          {
            if (i1 == i19 + 1)
            {
              i14 = j;
              i15 = k;
              i16 = n;
              n = i18;
              j = i12;
              k = i11;
              i1 = i17;
              break;
            }
            if (i2 == -1)
            {
              arrayOfByte1[i6] = arrayOfByte2[i1];
              i3 = i6 + 1;
              i6 = i5 + 1;
              i2 = i1;
              i4 = n;
            }
            for (;;)
            {
              paramGifFrame = this;
              i5 = i6;
              n = i4;
              i13 = i2;
              break;
              i14 = n;
              if (i1 >= n)
              {
                arrayOfByte3[m] = ((byte)i13);
                m += 1;
                n = i2;
              }
              for (n = i1; n >= i19; n = arrayOfShort[n])
              {
                arrayOfByte3[m] = arrayOfByte2[n];
                m += 1;
              }
              i15 = arrayOfByte2[n] & 0xFF;
              int i = (byte)i15;
              arrayOfByte1[i6] = i;
              n = i6 + 1;
              i3 = i5 + 1;
              while (m > 0)
              {
                m -= 1;
                arrayOfByte1[n] = arrayOfByte3[m];
                n += 1;
                i3 += 1;
              }
              i5 = j;
              i4 = k;
              i6 = i14;
              if (i14 < 4096)
              {
                arrayOfShort[i14] = ((short)i2);
                arrayOfByte2[i14] = i;
                i2 = i14 + 1;
                i5 = j;
                i4 = k;
                i6 = i2;
                if ((i2 & j) == 0)
                {
                  i5 = j;
                  i4 = k;
                  i6 = i2;
                  if (i2 < 4096)
                  {
                    i4 = k + 1;
                    i5 = j + i2;
                    i6 = i2;
                  }
                }
              }
              i13 = i6;
              i2 = i15;
              i6 = i3;
              j = i5;
              k = i4;
              i4 = i13;
              i3 = n;
            }
          }
        }
      }
      paramGifFrame = this;
      i14 = j;
      i15 = k;
      i16 = n;
      n = i18;
      j = i4;
      k = i3;
      i1 = i17;
    }
    Arrays.fill(arrayOfByte1, i6, i7, (byte)0);
  }
  
  private GifHeaderParser getHeaderParser()
  {
    if (parser == null) {
      parser = new GifHeaderParser();
    }
    return parser;
  }
  
  private Bitmap getNextBitmap()
  {
    if ((isFirstFrameTransparent != null) && (!isFirstFrameTransparent.booleanValue())) {
      localObject = bitmapConfig;
    } else {
      localObject = Bitmap.Config.ARGB_8888;
    }
    Object localObject = bitmapProvider.obtain(downsampledWidth, downsampledHeight, (Bitmap.Config)localObject);
    ((Bitmap)localObject).setHasAlpha(true);
    return localObject;
  }
  
  private int readBlock()
  {
    int i = readByte();
    if (i <= 0) {
      return i;
    }
    rawData.get(block, 0, Math.min(i, rawData.remaining()));
    return i;
  }
  
  private int readByte()
  {
    return rawData.get() & 0xFF;
  }
  
  private Bitmap setPixels(GifFrame paramGifFrame1, GifFrame paramGifFrame2)
  {
    int[] arrayOfInt = mainScratch;
    int i = 0;
    if (paramGifFrame2 == null)
    {
      if (previousImage != null) {
        bitmapProvider.release(previousImage);
      }
      previousImage = null;
      Arrays.fill(arrayOfInt, 0);
    }
    if ((paramGifFrame2 != null) && (dispose == 3) && (previousImage == null)) {
      Arrays.fill(arrayOfInt, 0);
    }
    if ((paramGifFrame2 != null) && (dispose > 0))
    {
      if (dispose == 2)
      {
        int j;
        if (!transparency)
        {
          j = header.bgColor;
          if ((lct != null) && (header.bgIndex == transIndex)) {
            j = i;
          }
        }
        else
        {
          j = i;
          if (framePointer == 0)
          {
            isFirstFrameTransparent = Boolean.valueOf(true);
            j = i;
          }
        }
        int n = ih / sampleSize;
        i = iy / sampleSize;
        int i1 = iw / sampleSize;
        int k = ix / sampleSize;
        int m = i * downsampledWidth + k;
        int i2 = downsampledWidth;
        i = m;
        while (i < n * i2 + m)
        {
          k = i;
          while (k < i + i1)
          {
            arrayOfInt[k] = j;
            k += 1;
          }
          i += downsampledWidth;
        }
      }
      if ((dispose == 3) && (previousImage != null)) {
        previousImage.getPixels(arrayOfInt, 0, downsampledWidth, 0, 0, downsampledWidth, downsampledHeight);
      }
    }
    decodeBitmapData(paramGifFrame1);
    if ((!interlace) && (sampleSize == 1)) {
      copyIntoScratchFast(paramGifFrame1);
    } else {
      copyCopyIntoScratchRobust(paramGifFrame1);
    }
    if ((savePrevious) && ((dispose == 0) || (dispose == 1)))
    {
      if (previousImage == null) {
        previousImage = getNextBitmap();
      }
      previousImage.setPixels(arrayOfInt, 0, downsampledWidth, 0, 0, downsampledWidth, downsampledHeight);
    }
    paramGifFrame1 = getNextBitmap();
    paramGifFrame1.setPixels(arrayOfInt, 0, downsampledWidth, 0, 0, downsampledWidth, downsampledHeight);
    return paramGifFrame1;
  }
  
  public void advance()
  {
    framePointer = ((framePointer + 1) % header.frameCount);
  }
  
  public void clear()
  {
    header = null;
    if (mainPixels != null) {
      bitmapProvider.release(mainPixels);
    }
    if (mainScratch != null) {
      bitmapProvider.release(mainScratch);
    }
    if (previousImage != null) {
      bitmapProvider.release(previousImage);
    }
    previousImage = null;
    rawData = null;
    isFirstFrameTransparent = null;
    if (block != null) {
      bitmapProvider.release(block);
    }
  }
  
  public int getByteSize()
  {
    return rawData.limit() + mainPixels.length + mainScratch.length * 4;
  }
  
  public int getCurrentFrameIndex()
  {
    return framePointer;
  }
  
  public ByteBuffer getData()
  {
    return rawData;
  }
  
  public int getDelay(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < header.frameCount)) {
      return header.frames.get(paramInt)).delay;
    }
    return -1;
  }
  
  public int getFrameCount()
  {
    return header.frameCount;
  }
  
  public int getHeight()
  {
    return header.height;
  }
  
  public int getLoopCount()
  {
    if (header.loopCount == -1) {
      return 1;
    }
    return header.loopCount;
  }
  
  public int getNetscapeLoopCount()
  {
    return header.loopCount;
  }
  
  public int getNextDelay()
  {
    if ((header.frameCount > 0) && (framePointer >= 0)) {
      return getDelay(framePointer);
    }
    return 0;
  }
  
  public Bitmap getNextFrame()
  {
    for (;;)
    {
      try
      {
        Object localObject1;
        Object localObject3;
        if ((header.frameCount <= 0) || (framePointer < 0))
        {
          if (Log.isLoggable(TAG, 3))
          {
            localObject1 = TAG;
            localObject3 = new StringBuilder();
            ((StringBuilder)localObject3).append("Unable to decode frame, frameCount=");
            ((StringBuilder)localObject3).append(header.frameCount);
            ((StringBuilder)localObject3).append(", framePointer=");
            ((StringBuilder)localObject3).append(framePointer);
            Log.d((String)localObject1, ((StringBuilder)localObject3).toString());
          }
          status = 1;
        }
        if ((status != 1) && (status != 2))
        {
          status = 0;
          if (block == null) {
            block = bitmapProvider.obtainByteArray(255);
          }
          GifFrame localGifFrame = (GifFrame)header.frames.get(framePointer);
          int i = framePointer - 1;
          if (i >= 0)
          {
            localObject1 = (GifFrame)header.frames.get(i);
            if (lct != null) {
              localObject3 = lct;
            } else {
              localObject3 = header.gct;
            }
            act = ((int[])localObject3);
            if (act == null)
            {
              if (Log.isLoggable(TAG, 3))
              {
                localObject1 = TAG;
                localObject3 = new StringBuilder();
                ((StringBuilder)localObject3).append("No valid color table found for frame #");
                ((StringBuilder)localObject3).append(framePointer);
                Log.d((String)localObject1, ((StringBuilder)localObject3).toString());
              }
              status = 1;
              return null;
            }
            if (transparency)
            {
              System.arraycopy(act, 0, gct, 0, act.length);
              act = gct;
              act[transIndex] = 0;
            }
            localObject1 = setPixels(localGifFrame, (GifFrame)localObject1);
            return localObject1;
          }
        }
        else
        {
          if (Log.isLoggable(TAG, 3))
          {
            localObject1 = TAG;
            localObject3 = new StringBuilder();
            ((StringBuilder)localObject3).append("Unable to decode frame, status=");
            ((StringBuilder)localObject3).append(status);
            Log.d((String)localObject1, ((StringBuilder)localObject3).toString());
          }
          return null;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      Object localObject2 = null;
    }
  }
  
  public int getStatus()
  {
    return status;
  }
  
  public int getTotalIterationCount()
  {
    if (header.loopCount == -1) {
      return 1;
    }
    if (header.loopCount == 0) {
      return 0;
    }
    return header.loopCount + 1;
  }
  
  public int getWidth()
  {
    return header.width;
  }
  
  public int read(InputStream paramInputStream, int paramInt)
  {
    if (paramInputStream != null)
    {
      if (paramInt > 0) {
        paramInt += 4096;
      } else {
        paramInt = 16384;
      }
      try
      {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(paramInt);
        byte[] arrayOfByte = new byte['?'];
        for (;;)
        {
          paramInt = arrayOfByte.length;
          paramInt = paramInputStream.read(arrayOfByte, 0, paramInt);
          if (paramInt == -1) {
            break;
          }
          localByteArrayOutputStream.write(arrayOfByte, 0, paramInt);
        }
        localByteArrayOutputStream.flush();
        read(localByteArrayOutputStream.toByteArray());
      }
      catch (IOException localIOException)
      {
        Log.w(TAG, "Error reading data from stream", localIOException);
      }
    }
    status = 2;
    if (paramInputStream != null) {
      try
      {
        paramInputStream.close();
      }
      catch (IOException paramInputStream)
      {
        Log.w(TAG, "Error closing stream", paramInputStream);
      }
    }
    return status;
  }
  
  public int read(byte[] paramArrayOfByte)
  {
    try
    {
      header = getHeaderParser().setData(paramArrayOfByte).parseHeader();
      if (paramArrayOfByte != null) {
        setData(header, paramArrayOfByte);
      }
      int i = status;
      return i;
    }
    catch (Throwable paramArrayOfByte)
    {
      throw paramArrayOfByte;
    }
  }
  
  public void resetFrameIndex()
  {
    framePointer = -1;
  }
  
  public void setData(GifHeader paramGifHeader, ByteBuffer paramByteBuffer)
  {
    try
    {
      setData(paramGifHeader, paramByteBuffer, 1);
      return;
    }
    catch (Throwable paramGifHeader)
    {
      throw paramGifHeader;
    }
  }
  
  public void setData(GifHeader paramGifHeader, ByteBuffer paramByteBuffer, int paramInt)
  {
    if (paramInt > 0) {}
    try
    {
      paramInt = Integer.highestOneBit(paramInt);
      status = 0;
      header = paramGifHeader;
      framePointer = -1;
      rawData = paramByteBuffer.asReadOnlyBuffer();
      rawData.position(0);
      rawData.order(ByteOrder.LITTLE_ENDIAN);
      savePrevious = false;
      paramByteBuffer = frames.iterator();
      while (paramByteBuffer.hasNext()) {
        if (nextdispose == 3) {
          savePrevious = true;
        }
      }
      sampleSize = paramInt;
      downsampledWidth = (width / paramInt);
      downsampledHeight = (height / paramInt);
      mainPixels = bitmapProvider.obtainByteArray(width * height);
      mainScratch = bitmapProvider.obtainIntArray(downsampledWidth * downsampledHeight);
      return;
    }
    catch (Throwable paramGifHeader)
    {
      throw paramGifHeader;
    }
    paramGifHeader = new StringBuilder();
    paramGifHeader.append("Sample size must be >=0, not: ");
    paramGifHeader.append(paramInt);
    throw new IllegalArgumentException(paramGifHeader.toString());
  }
  
  public void setData(GifHeader paramGifHeader, byte[] paramArrayOfByte)
  {
    try
    {
      setData(paramGifHeader, ByteBuffer.wrap(paramArrayOfByte));
      return;
    }
    catch (Throwable paramGifHeader)
    {
      throw paramGifHeader;
    }
  }
  
  public void setDefaultBitmapConfig(Bitmap.Config paramConfig)
  {
    if ((paramConfig != Bitmap.Config.ARGB_8888) && (paramConfig != Bitmap.Config.RGB_565))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unsupported format: ");
      localStringBuilder.append(paramConfig);
      localStringBuilder.append(", must be one of ");
      localStringBuilder.append(Bitmap.Config.ARGB_8888);
      localStringBuilder.append(" or ");
      localStringBuilder.append(Bitmap.Config.RGB_565);
      throw new IllegalArgumentException(localStringBuilder.toString());
    }
    bitmapConfig = paramConfig;
  }
}
