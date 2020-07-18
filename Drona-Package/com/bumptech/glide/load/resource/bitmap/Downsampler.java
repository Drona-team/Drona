package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.Log;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParser.ImageType;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Lock;

public final class Downsampler
{
  public static final Option<Boolean> ALLOW_HARDWARE_CONFIG;
  public static final Option<DecodeFormat> DECODE_FORMAT = Option.memory("com.bumptech.glide.load.resource.bitmap.Downsampler.DecodeFormat", DecodeFormat.DEFAULT);
  @Deprecated
  public static final Option<DownsampleStrategy> DOWNSAMPLE_STRATEGY = DownsampleStrategy.OPTION;
  private static final DecodeCallbacks EMPTY_CALLBACKS;
  public static final Option<Boolean> FIX_BITMAP_SIZE_TO_REQUESTED_DIMENSIONS = Option.memory("com.bumptech.glide.load.resource.bitmap.Downsampler.FixBitmapSize", Boolean.valueOf(false));
  private static final String ICO_MIME_TYPE = "image/x-ico";
  private static final int MARK_POSITION = 10485760;
  private static final Set<String> NO_DOWNSAMPLE_PRE_N_MIME_TYPES;
  private static final Queue<BitmapFactory.Options> OPTIONS_QUEUE = Util.createQueue(0);
  static final String TAG = "Downsampler";
  private static final Set<ImageHeaderParser.ImageType> TYPES_THAT_USE_POOL_PRE_KITKAT;
  private static final String WBMP_MIME_TYPE = "image/vnd.wap.wbmp";
  private final BitmapPool bitmapPool;
  private final ArrayPool byteArrayPool;
  private final DisplayMetrics displayMetrics;
  private final HardwareConfigState hardwareConfigState = HardwareConfigState.getInstance();
  private final List<ImageHeaderParser> parsers;
  
  static
  {
    ALLOW_HARDWARE_CONFIG = Option.memory("com.bumptech.glide.load.resource.bitmap.Downsampler.AllowHardwareDecode", Boolean.valueOf(false));
    NO_DOWNSAMPLE_PRE_N_MIME_TYPES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] { "image/vnd.wap.wbmp", "image/x-ico" })));
    EMPTY_CALLBACKS = new DecodeCallbacks()
    {
      public void onDecodeComplete(BitmapPool paramAnonymousBitmapPool, Bitmap paramAnonymousBitmap) {}
      
      public void onObtainBounds() {}
    };
    TYPES_THAT_USE_POOL_PRE_KITKAT = Collections.unmodifiableSet(EnumSet.of(ImageHeaderParser.ImageType.JPEG, ImageHeaderParser.ImageType.PNG_A, ImageHeaderParser.ImageType.PNG));
  }
  
  public Downsampler(List paramList, DisplayMetrics paramDisplayMetrics, BitmapPool paramBitmapPool, ArrayPool paramArrayPool)
  {
    parsers = paramList;
    displayMetrics = ((DisplayMetrics)Preconditions.checkNotNull(paramDisplayMetrics));
    bitmapPool = ((BitmapPool)Preconditions.checkNotNull(paramBitmapPool));
    byteArrayPool = ((ArrayPool)Preconditions.checkNotNull(paramArrayPool));
  }
  
  private static int adjustTargetDensityForError(double paramDouble)
  {
    int i = getDensityMultiplier(paramDouble);
    int j = round(i * paramDouble);
    return round(paramDouble / (j / i) * j);
  }
  
  private void calculateConfig(InputStream paramInputStream, DecodeFormat paramDecodeFormat, boolean paramBoolean1, boolean paramBoolean2, BitmapFactory.Options paramOptions, int paramInt1, int paramInt2)
  {
    if (hardwareConfigState.setHardwareConfigIfAllowed(paramInt1, paramInt2, paramOptions, paramDecodeFormat, paramBoolean1, paramBoolean2)) {
      return;
    }
    if ((paramDecodeFormat != DecodeFormat.PREFER_ARGB_8888) && (Build.VERSION.SDK_INT != 16))
    {
      Object localObject = parsers;
      ArrayPool localArrayPool = byteArrayPool;
      try
      {
        paramBoolean1 = ImageHeaderParserUtils.getType((List)localObject, paramInputStream, localArrayPool).hasAlpha();
      }
      catch (IOException paramInputStream)
      {
        if (Log.isLoggable("Downsampler", 3))
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("Cannot determine whether the image has alpha or not from header, format ");
          ((StringBuilder)localObject).append(paramDecodeFormat);
          Log.d("Downsampler", ((StringBuilder)localObject).toString(), paramInputStream);
        }
        paramBoolean1 = false;
      }
      if (paramBoolean1) {
        paramInputStream = Bitmap.Config.ARGB_8888;
      } else {
        paramInputStream = Bitmap.Config.RGB_565;
      }
      inPreferredConfig = paramInputStream;
      if (inPreferredConfig == Bitmap.Config.RGB_565) {
        inDither = true;
      }
    }
    else
    {
      inPreferredConfig = Bitmap.Config.ARGB_8888;
    }
  }
  
  private static void calculateScaling(ImageHeaderParser.ImageType paramImageType, InputStream paramInputStream, DecodeCallbacks paramDecodeCallbacks, BitmapPool paramBitmapPool, DownsampleStrategy paramDownsampleStrategy, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, BitmapFactory.Options paramOptions)
    throws IOException
  {
    if ((paramInt2 > 0) && (paramInt3 > 0))
    {
      float f1;
      if ((paramInt1 != 90) && (paramInt1 != 270)) {
        f1 = paramDownsampleStrategy.getScaleFactor(paramInt2, paramInt3, paramInt4, paramInt5);
      } else {
        f1 = paramDownsampleStrategy.getScaleFactor(paramInt3, paramInt2, paramInt4, paramInt5);
      }
      if (f1 > 0.0F)
      {
        DownsampleStrategy.SampleSizeRounding localSampleSizeRounding = paramDownsampleStrategy.getSampleSizeRounding(paramInt2, paramInt3, paramInt4, paramInt5);
        if (localSampleSizeRounding != null)
        {
          float f2 = paramInt2;
          int i = round(f1 * f2);
          float f3 = paramInt3;
          paramInt1 = round(f1 * f3);
          i = paramInt2 / i;
          paramInt1 = paramInt3 / paramInt1;
          if (localSampleSizeRounding == DownsampleStrategy.SampleSizeRounding.MEMORY) {
            paramInt1 = Math.max(i, paramInt1);
          } else {
            paramInt1 = Math.min(i, paramInt1);
          }
          int j;
          if ((Build.VERSION.SDK_INT <= 23) && (NO_DOWNSAMPLE_PRE_N_MIME_TYPES.contains(outMimeType)))
          {
            j = 1;
          }
          else
          {
            paramInt1 = Math.max(1, Integer.highestOneBit(paramInt1));
            j = paramInt1;
            if (localSampleSizeRounding == DownsampleStrategy.SampleSizeRounding.MEMORY)
            {
              j = paramInt1;
              if (paramInt1 < 1.0F / f1) {
                j = paramInt1 << 1;
              }
            }
          }
          inSampleSize = j;
          float f4;
          if (paramImageType == ImageHeaderParser.ImageType.JPEG)
          {
            f4 = Math.min(j, 8);
            int m = (int)Math.ceil(f2 / f4);
            int k = (int)Math.ceil(f3 / f4);
            int n = j / 8;
            paramInt1 = m;
            i = k;
            if (n > 0)
            {
              paramInt1 = m / n;
              i = k / n;
            }
          }
          else if ((paramImageType != ImageHeaderParser.ImageType.PNG) && (paramImageType != ImageHeaderParser.ImageType.PNG_A))
          {
            if ((paramImageType != ImageHeaderParser.ImageType.WEBP) && (paramImageType != ImageHeaderParser.ImageType.WEBP_A))
            {
              if ((paramInt2 % j == 0) && (paramInt3 % j == 0))
              {
                paramInt1 = paramInt2 / j;
                i = paramInt3 / j;
              }
              else
              {
                paramImageType = getDimensions(paramInputStream, paramOptions, paramDecodeCallbacks, paramBitmapPool);
                paramInt1 = paramImageType[0];
                i = paramImageType[1];
              }
            }
            else if (Build.VERSION.SDK_INT >= 24)
            {
              f4 = j;
              paramInt1 = Math.round(f2 / f4);
              i = Math.round(f3 / f4);
            }
            else
            {
              f4 = j;
              paramInt1 = (int)Math.floor(f2 / f4);
              i = (int)Math.floor(f3 / f4);
            }
          }
          else
          {
            f4 = j;
            paramInt1 = (int)Math.floor(f2 / f4);
            i = (int)Math.floor(f3 / f4);
          }
          double d = paramDownsampleStrategy.getScaleFactor(paramInt1, i, paramInt4, paramInt5);
          if (Build.VERSION.SDK_INT >= 19)
          {
            inTargetDensity = adjustTargetDensityForError(d);
            inDensity = getDensityMultiplier(d);
          }
          if (isScaling(paramOptions))
          {
            inScaled = true;
          }
          else
          {
            inTargetDensity = 0;
            inDensity = 0;
          }
          if (Log.isLoggable("Downsampler", 2))
          {
            paramImageType = new StringBuilder();
            paramImageType.append("Calculate scaling, source: [");
            paramImageType.append(paramInt2);
            paramImageType.append("x");
            paramImageType.append(paramInt3);
            paramImageType.append("], target: [");
            paramImageType.append(paramInt4);
            paramImageType.append("x");
            paramImageType.append(paramInt5);
            paramImageType.append("], power of two scaled: [");
            paramImageType.append(paramInt1);
            paramImageType.append("x");
            paramImageType.append(i);
            paramImageType.append("], exact scale factor: ");
            paramImageType.append(f1);
            paramImageType.append(", power of 2 sample size: ");
            paramImageType.append(j);
            paramImageType.append(", adjusted scale factor: ");
            paramImageType.append(d);
            paramImageType.append(", target density: ");
            paramImageType.append(inTargetDensity);
            paramImageType.append(", density: ");
            paramImageType.append(inDensity);
            Log.v("Downsampler", paramImageType.toString());
          }
        }
        else
        {
          throw new IllegalArgumentException("Cannot round with null rounding");
        }
      }
      else
      {
        paramImageType = new StringBuilder();
        paramImageType.append("Cannot scale with factor: ");
        paramImageType.append(f1);
        paramImageType.append(" from: ");
        paramImageType.append(paramDownsampleStrategy);
        paramImageType.append(", source: [");
        paramImageType.append(paramInt2);
        paramImageType.append("x");
        paramImageType.append(paramInt3);
        paramImageType.append("], target: [");
        paramImageType.append(paramInt4);
        paramImageType.append("x");
        paramImageType.append(paramInt5);
        paramImageType.append("]");
        throw new IllegalArgumentException(paramImageType.toString());
      }
    }
    else if (Log.isLoggable("Downsampler", 3))
    {
      paramInputStream = new StringBuilder();
      paramInputStream.append("Unable to determine dimensions for: ");
      paramInputStream.append(paramImageType);
      paramInputStream.append(" with target [");
      paramInputStream.append(paramInt4);
      paramInputStream.append("x");
      paramInputStream.append(paramInt5);
      paramInputStream.append("]");
      Log.d("Downsampler", paramInputStream.toString());
    }
  }
  
  private Bitmap decodeFromWrappedStreams(InputStream paramInputStream, BitmapFactory.Options paramOptions, DownsampleStrategy paramDownsampleStrategy, DecodeFormat paramDecodeFormat, boolean paramBoolean1, int paramInt1, int paramInt2, boolean paramBoolean2, DecodeCallbacks paramDecodeCallbacks)
    throws IOException
  {
    long l = LogTime.getLogTime();
    Object localObject = getDimensions(paramInputStream, paramOptions, paramDecodeCallbacks, bitmapPool);
    int m = 0;
    int n = localObject[0];
    int i1 = localObject[1];
    localObject = outMimeType;
    if ((n != -1) && (i1 != -1)) {
      break label60;
    }
    paramBoolean1 = false;
    label60:
    int i4 = ImageHeaderParserUtils.getOrientation(parsers, paramInputStream, byteArrayPool);
    int i2 = TransformationUtils.getExifOrientationDegrees(i4);
    boolean bool = TransformationUtils.isExifOrientationRequired(i4);
    int i;
    if (paramInt1 == Integer.MIN_VALUE) {
      i = n;
    } else {
      i = paramInt1;
    }
    int k = paramInt2;
    int j = k;
    if (k == Integer.MIN_VALUE) {
      j = i1;
    }
    ImageHeaderParser.ImageType localImageType = ImageHeaderParserUtils.getType(parsers, paramInputStream, byteArrayPool);
    calculateScaling(localImageType, paramInputStream, paramDecodeCallbacks, bitmapPool, paramDownsampleStrategy, i2, n, i1, i, j, paramOptions);
    calculateConfig(paramInputStream, paramDecodeFormat, paramBoolean1, bool, paramOptions, i, j);
    k = m;
    if (Build.VERSION.SDK_INT >= 19) {
      k = 1;
    }
    if ((inSampleSize != 1) && (k == 0)) {
      break label540;
    }
    if (shouldUsePool(localImageType))
    {
      if ((n >= 0) && (i1 >= 0) && (paramBoolean2) && (k != 0))
      {
        m = i;
      }
      else
      {
        float f1;
        if (isScaling(paramOptions)) {
          f1 = inTargetDensity / inDensity;
        } else {
          f1 = 1.0F;
        }
        int i5 = inSampleSize;
        float f2 = n;
        float f3 = i5;
        i = (int)Math.ceil(f2 / f3);
        j = (int)Math.ceil(i1 / f3);
        i2 = Math.round(i * f1);
        i = i2;
        int i3 = Math.round(j * f1);
        k = i3;
        j = k;
        m = i;
        if (Log.isLoggable("Downsampler", 2))
        {
          paramDownsampleStrategy = new StringBuilder();
          paramDownsampleStrategy.append("Calculated target [");
          paramDownsampleStrategy.append(i2);
          paramDownsampleStrategy.append("x");
          paramDownsampleStrategy.append(i3);
          paramDownsampleStrategy.append("] for source [");
          paramDownsampleStrategy.append(n);
          paramDownsampleStrategy.append("x");
          paramDownsampleStrategy.append(i1);
          paramDownsampleStrategy.append("], sampleSize: ");
          paramDownsampleStrategy.append(i5);
          paramDownsampleStrategy.append(", targetDensity: ");
          paramDownsampleStrategy.append(inTargetDensity);
          paramDownsampleStrategy.append(", density: ");
          paramDownsampleStrategy.append(inDensity);
          paramDownsampleStrategy.append(", density multiplier: ");
          paramDownsampleStrategy.append(f1);
          Log.v("Downsampler", paramDownsampleStrategy.toString());
          m = i;
          j = k;
        }
      }
      if ((m > 0) && (j > 0)) {
        setInBitmap(paramOptions, bitmapPool, m, j);
      }
    }
    label540:
    paramDownsampleStrategy = this;
    paramInputStream = decodeStream(paramInputStream, paramOptions, paramDecodeCallbacks, bitmapPool);
    paramDecodeCallbacks.onDecodeComplete(bitmapPool, paramInputStream);
    if (Log.isLoggable("Downsampler", 2)) {
      logDecode(n, i1, (String)localObject, paramOptions, paramInputStream, paramInt1, paramInt2, l);
    }
    if (paramInputStream != null)
    {
      paramInputStream.setDensity(displayMetrics.densityDpi);
      paramOptions = TransformationUtils.rotateImageExif(bitmapPool, paramInputStream, i4);
      if (!paramInputStream.equals(paramOptions))
      {
        bitmapPool.put(paramInputStream);
        return paramOptions;
      }
    }
    else
    {
      return null;
    }
    return paramOptions;
  }
  
  private static Bitmap decodeStream(InputStream paramInputStream, BitmapFactory.Options paramOptions, DecodeCallbacks paramDecodeCallbacks, BitmapPool paramBitmapPool)
    throws IOException
  {
    if (inJustDecodeBounds) {
      paramInputStream.mark(10485760);
    } else {
      paramDecodeCallbacks.onObtainBounds();
    }
    int i = outWidth;
    int j = outHeight;
    Object localObject = outMimeType;
    TransformationUtils.getBitmapDrawableLock().lock();
    Bitmap localBitmap2;
    try
    {
      Bitmap localBitmap1 = BitmapFactory.decodeStream(paramInputStream, null, paramOptions);
      TransformationUtils.getBitmapDrawableLock().unlock();
      if (!inJustDecodeBounds) {
        break label194;
      }
      paramInputStream.reset();
      return localBitmap1;
    }
    catch (Throwable paramInputStream) {}catch (IllegalArgumentException localIllegalArgumentException)
    {
      localObject = newIoExceptionForInBitmapAssertion(localIllegalArgumentException, i, j, (String)localObject, paramOptions);
      boolean bool = Log.isLoggable("Downsampler", 3);
      if (bool) {
        Log.d("Downsampler", "Failed to decode with inBitmap, trying again without Bitmap re-use", (Throwable)localObject);
      }
      localBitmap2 = inBitmap;
      if (localBitmap2 == null) {}
    }
    try
    {
      paramInputStream.reset();
      localBitmap2 = inBitmap;
      paramBitmapPool.put(localBitmap2);
      inBitmap = null;
      paramInputStream = decodeStream(paramInputStream, paramOptions, paramDecodeCallbacks, paramBitmapPool);
      TransformationUtils.getBitmapDrawableLock().unlock();
      return paramInputStream;
    }
    catch (IOException paramInputStream)
    {
      for (;;) {}
    }
    throw ((Throwable)localObject);
    throw ((Throwable)localObject);
    TransformationUtils.getBitmapDrawableLock().unlock();
    throw paramInputStream;
    label194:
    return localBitmap2;
  }
  
  private static String getBitmapString(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      return null;
    }
    Object localObject;
    if (Build.VERSION.SDK_INT >= 19)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(" (");
      ((StringBuilder)localObject).append(paramBitmap.getAllocationByteCount());
      ((StringBuilder)localObject).append(")");
      localObject = ((StringBuilder)localObject).toString();
    }
    else
    {
      localObject = "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[");
    localStringBuilder.append(paramBitmap.getWidth());
    localStringBuilder.append("x");
    localStringBuilder.append(paramBitmap.getHeight());
    localStringBuilder.append("] ");
    localStringBuilder.append(paramBitmap.getConfig());
    localStringBuilder.append((String)localObject);
    return localStringBuilder.toString();
  }
  
  /* Error */
  private static BitmapFactory.Options getDefaultOptions()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 145	com/bumptech/glide/load/resource/bitmap/Downsampler:OPTIONS_QUEUE	Ljava/util/Queue;
    //   6: astore_0
    //   7: aload_0
    //   8: monitorenter
    //   9: getstatic 145	com/bumptech/glide/load/resource/bitmap/Downsampler:OPTIONS_QUEUE	Ljava/util/Queue;
    //   12: invokeinterface 544 1 0
    //   17: checkcast 247	android/graphics/BitmapFactory$Options
    //   20: astore_1
    //   21: aload_0
    //   22: monitorexit
    //   23: aload_1
    //   24: astore_0
    //   25: aload_1
    //   26: ifnonnull +15 -> 41
    //   29: new 247	android/graphics/BitmapFactory$Options
    //   32: dup
    //   33: invokespecial 545	android/graphics/BitmapFactory$Options:<init>	()V
    //   36: astore_0
    //   37: aload_0
    //   38: invokestatic 549	com/bumptech/glide/load/resource/bitmap/Downsampler:resetOptions	(Landroid/graphics/BitmapFactory$Options;)V
    //   41: ldc 2
    //   43: monitorexit
    //   44: aload_0
    //   45: areturn
    //   46: astore_1
    //   47: aload_0
    //   48: monitorexit
    //   49: aload_1
    //   50: athrow
    //   51: astore_0
    //   52: ldc 2
    //   54: monitorexit
    //   55: aload_0
    //   56: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   6	42	0	localObject	Object
    //   51	5	0	localThrowable1	Throwable
    //   20	6	1	localOptions	BitmapFactory.Options
    //   46	4	1	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   9	23	46	java/lang/Throwable
    //   47	49	46	java/lang/Throwable
    //   3	9	51	java/lang/Throwable
    //   29	41	51	java/lang/Throwable
    //   49	51	51	java/lang/Throwable
  }
  
  private static int getDensityMultiplier(double paramDouble)
  {
    if (paramDouble > 1.0D) {
      paramDouble = 1.0D / paramDouble;
    }
    return (int)Math.round(paramDouble * 2.147483647E9D);
  }
  
  private static int[] getDimensions(InputStream paramInputStream, BitmapFactory.Options paramOptions, DecodeCallbacks paramDecodeCallbacks, BitmapPool paramBitmapPool)
    throws IOException
  {
    inJustDecodeBounds = true;
    decodeStream(paramInputStream, paramOptions, paramDecodeCallbacks, paramBitmapPool);
    inJustDecodeBounds = false;
    return new int[] { outWidth, outHeight };
  }
  
  private static String getInBitmapString(BitmapFactory.Options paramOptions)
  {
    return getBitmapString(inBitmap);
  }
  
  private static boolean isScaling(BitmapFactory.Options paramOptions)
  {
    return (inTargetDensity > 0) && (inDensity > 0) && (inTargetDensity != inDensity);
  }
  
  private static void logDecode(int paramInt1, int paramInt2, String paramString, BitmapFactory.Options paramOptions, Bitmap paramBitmap, int paramInt3, int paramInt4, long paramLong)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Decoded ");
    localStringBuilder.append(getBitmapString(paramBitmap));
    localStringBuilder.append(" from [");
    localStringBuilder.append(paramInt1);
    localStringBuilder.append("x");
    localStringBuilder.append(paramInt2);
    localStringBuilder.append("] ");
    localStringBuilder.append(paramString);
    localStringBuilder.append(" with inBitmap ");
    localStringBuilder.append(getInBitmapString(paramOptions));
    localStringBuilder.append(" for [");
    localStringBuilder.append(paramInt3);
    localStringBuilder.append("x");
    localStringBuilder.append(paramInt4);
    localStringBuilder.append("], sample size: ");
    localStringBuilder.append(inSampleSize);
    localStringBuilder.append(", density: ");
    localStringBuilder.append(inDensity);
    localStringBuilder.append(", target density: ");
    localStringBuilder.append(inTargetDensity);
    localStringBuilder.append(", thread: ");
    localStringBuilder.append(Thread.currentThread().getName());
    localStringBuilder.append(", duration: ");
    localStringBuilder.append(LogTime.getElapsedMillis(paramLong));
    Log.v("Downsampler", localStringBuilder.toString());
  }
  
  private static IOException newIoExceptionForInBitmapAssertion(IllegalArgumentException paramIllegalArgumentException, int paramInt1, int paramInt2, String paramString, BitmapFactory.Options paramOptions)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Exception decoding bitmap, outWidth: ");
    localStringBuilder.append(paramInt1);
    localStringBuilder.append(", outHeight: ");
    localStringBuilder.append(paramInt2);
    localStringBuilder.append(", outMimeType: ");
    localStringBuilder.append(paramString);
    localStringBuilder.append(", inBitmap: ");
    localStringBuilder.append(getInBitmapString(paramOptions));
    return new IOException(localStringBuilder.toString(), paramIllegalArgumentException);
  }
  
  private static void releaseOptions(BitmapFactory.Options paramOptions)
  {
    resetOptions(paramOptions);
    Queue localQueue = OPTIONS_QUEUE;
    try
    {
      OPTIONS_QUEUE.offer(paramOptions);
      return;
    }
    catch (Throwable paramOptions)
    {
      throw paramOptions;
    }
  }
  
  private static void resetOptions(BitmapFactory.Options paramOptions)
  {
    inTempStorage = null;
    inDither = false;
    inScaled = false;
    inSampleSize = 1;
    inPreferredConfig = null;
    inJustDecodeBounds = false;
    inDensity = 0;
    inTargetDensity = 0;
    outWidth = 0;
    outHeight = 0;
    outMimeType = null;
    inBitmap = null;
    inMutable = true;
  }
  
  private static int round(double paramDouble)
  {
    return (int)(paramDouble + 0.5D);
  }
  
  private static void setInBitmap(BitmapFactory.Options paramOptions, BitmapPool paramBitmapPool, int paramInt1, int paramInt2)
  {
    Bitmap.Config localConfig1;
    if (Build.VERSION.SDK_INT >= 26)
    {
      if (inPreferredConfig == Enum.HARDWARE) {
        return;
      }
      localConfig1 = outConfig;
    }
    else
    {
      localConfig1 = null;
    }
    Bitmap.Config localConfig2 = localConfig1;
    if (localConfig1 == null) {
      localConfig2 = inPreferredConfig;
    }
    inBitmap = paramBitmapPool.getDirty(paramInt1, paramInt2, localConfig2);
  }
  
  private boolean shouldUsePool(ImageHeaderParser.ImageType paramImageType)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return true;
    }
    return TYPES_THAT_USE_POOL_PRE_KITKAT.contains(paramImageType);
  }
  
  public Resource decode(InputStream paramInputStream, int paramInt1, int paramInt2, Options paramOptions)
    throws IOException
  {
    return decode(paramInputStream, paramInt1, paramInt2, paramOptions, EMPTY_CALLBACKS);
  }
  
  public Resource decode(InputStream paramInputStream, int paramInt1, int paramInt2, Options paramOptions, DecodeCallbacks paramDecodeCallbacks)
    throws IOException
  {
    Preconditions.checkArgument(paramInputStream.markSupported(), "You must provide an InputStream that supports mark()");
    byte[] arrayOfByte = (byte[])byteArrayPool.w(65536, [B.class);
    BitmapFactory.Options localOptions = getDefaultOptions();
    inTempStorage = arrayOfByte;
    DecodeFormat localDecodeFormat = (DecodeFormat)paramOptions.getOption(DECODE_FORMAT);
    DownsampleStrategy localDownsampleStrategy = (DownsampleStrategy)paramOptions.getOption(DownsampleStrategy.OPTION);
    boolean bool2 = ((Boolean)paramOptions.getOption(FIX_BITMAP_SIZE_TO_REQUESTED_DIMENSIONS)).booleanValue();
    boolean bool1;
    if ((paramOptions.getOption(ALLOW_HARDWARE_CONFIG) != null) && (((Boolean)paramOptions.getOption(ALLOW_HARDWARE_CONFIG)).booleanValue())) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    try
    {
      paramInputStream = BitmapResource.obtain(decodeFromWrappedStreams(paramInputStream, localOptions, localDownsampleStrategy, localDecodeFormat, bool1, paramInt1, paramInt2, bool2, paramDecodeCallbacks), bitmapPool);
      releaseOptions(localOptions);
      byteArrayPool.put(arrayOfByte);
      return paramInputStream;
    }
    catch (Throwable paramInputStream)
    {
      releaseOptions(localOptions);
      byteArrayPool.put(arrayOfByte);
      throw paramInputStream;
    }
  }
  
  public boolean handles(InputStream paramInputStream)
  {
    return true;
  }
  
  public boolean handles(ByteBuffer paramByteBuffer)
  {
    return true;
  }
  
  public static abstract interface DecodeCallbacks
  {
    public abstract void onDecodeComplete(BitmapPool paramBitmapPool, Bitmap paramBitmap)
      throws IOException;
    
    public abstract void onObtainBounds();
  }
}
