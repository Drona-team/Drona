package com.facebook.imagepipeline.platform;

import android.annotation.TargetApi;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.ColorSpace;
import android.graphics.Rect;
import android.os.Build.VERSION;
import androidx.core.util.Pools.SynchronizedPool;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.memory.BitmapPool;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@TargetApi(21)
@ThreadSafe
public abstract class DefaultDecoder
  implements PlatformDecoder
{
  private static final int DECODE_BUFFER_SIZE = 16384;
  private static final byte[] EOI_TAIL = { -1, -39 };
  private static final Class<?> SHORT = DefaultDecoder.class;
  private final BitmapPool mBitmapPool;
  @VisibleForTesting
  final Pools.SynchronizedPool<ByteBuffer> mDecodeBuffers;
  @Nullable
  private final PreverificationHelper mPreverificationHelper;
  
  public DefaultDecoder(BitmapPool paramBitmapPool, int paramInt, Pools.SynchronizedPool paramSynchronizedPool)
  {
    PreverificationHelper localPreverificationHelper;
    if (Build.VERSION.SDK_INT >= 26) {
      localPreverificationHelper = new PreverificationHelper();
    } else {
      localPreverificationHelper = null;
    }
    mPreverificationHelper = localPreverificationHelper;
    mBitmapPool = paramBitmapPool;
    mDecodeBuffers = paramSynchronizedPool;
    int i = 0;
    while (i < paramInt)
    {
      mDecodeBuffers.release(ByteBuffer.allocate(16384));
      i += 1;
    }
  }
  
  /* Error */
  private CloseableReference decodeFromStream(InputStream paramInputStream, BitmapFactory.Options paramOptions, Rect paramRect, ColorSpace paramColorSpace)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 82	com/facebook/common/internal/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4: pop
    //   5: aload_2
    //   6: getfield 87	android/graphics/BitmapFactory$Options:outWidth	I
    //   9: istore 5
    //   11: aload_2
    //   12: getfield 90	android/graphics/BitmapFactory$Options:outHeight	I
    //   15: istore 6
    //   17: aload_3
    //   18: ifnull +25 -> 43
    //   21: aload_3
    //   22: invokevirtual 96	android/graphics/Rect:width	()I
    //   25: aload_2
    //   26: getfield 99	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   29: idiv
    //   30: istore 5
    //   32: aload_3
    //   33: invokevirtual 102	android/graphics/Rect:height	()I
    //   36: aload_2
    //   37: getfield 99	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   40: idiv
    //   41: istore 6
    //   43: getstatic 45	android/os/Build$VERSION:SDK_INT	I
    //   46: bipush 26
    //   48: if_icmplt +30 -> 78
    //   51: aload_0
    //   52: getfield 50	com/facebook/imagepipeline/platform/DefaultDecoder:mPreverificationHelper	Lcom/facebook/imagepipeline/platform/PreverificationHelper;
    //   55: ifnull +23 -> 78
    //   58: aload_0
    //   59: getfield 50	com/facebook/imagepipeline/platform/DefaultDecoder:mPreverificationHelper	Lcom/facebook/imagepipeline/platform/PreverificationHelper;
    //   62: aload_2
    //   63: getfield 106	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
    //   66: invokevirtual 110	com/facebook/imagepipeline/platform/PreverificationHelper:shouldUseHardwareBitmapConfig	(Landroid/graphics/Bitmap$Config;)Z
    //   69: ifeq +9 -> 78
    //   72: iconst_1
    //   73: istore 7
    //   75: goto +6 -> 81
    //   78: iconst_0
    //   79: istore 7
    //   81: aload_3
    //   82: ifnonnull +19 -> 101
    //   85: iload 7
    //   87: ifeq +14 -> 101
    //   90: aload_2
    //   91: iconst_0
    //   92: putfield 114	android/graphics/BitmapFactory$Options:inMutable	Z
    //   95: aconst_null
    //   96: astore 10
    //   98: goto +51 -> 149
    //   101: aload_3
    //   102: ifnull +15 -> 117
    //   105: iload 7
    //   107: ifeq +10 -> 117
    //   110: aload_2
    //   111: getstatic 119	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   114: putfield 106	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
    //   117: aload_0
    //   118: iload 5
    //   120: iload 6
    //   122: aload_2
    //   123: invokevirtual 123	com/facebook/imagepipeline/platform/DefaultDecoder:getBitmapSize	(IILandroid/graphics/BitmapFactory$Options;)I
    //   126: istore 7
    //   128: aload_0
    //   129: getfield 52	com/facebook/imagepipeline/platform/DefaultDecoder:mBitmapPool	Lcom/facebook/imagepipeline/memory/BitmapPool;
    //   132: iload 7
    //   134: invokeinterface 129 2 0
    //   139: checkcast 131	android/graphics/Bitmap
    //   142: astore 10
    //   144: aload 10
    //   146: ifnull +372 -> 518
    //   149: aload_2
    //   150: aload 10
    //   152: putfield 135	android/graphics/BitmapFactory$Options:inBitmap	Landroid/graphics/Bitmap;
    //   155: getstatic 45	android/os/Build$VERSION:SDK_INT	I
    //   158: bipush 26
    //   160: if_icmplt +26 -> 186
    //   163: aload 4
    //   165: astore 8
    //   167: aload 4
    //   169: ifnonnull +11 -> 180
    //   172: getstatic 141	android/graphics/ColorSpace$Named:SRGB	Landroid/graphics/ColorSpace$Named;
    //   175: invokestatic 146	android/graphics/ColorSpace:get	(Landroid/graphics/ColorSpace$Named;)Landroid/graphics/ColorSpace;
    //   178: astore 8
    //   180: aload_2
    //   181: aload 8
    //   183: putfield 150	android/graphics/BitmapFactory$Options:inPreferredColorSpace	Landroid/graphics/ColorSpace;
    //   186: aload_0
    //   187: getfield 54	com/facebook/imagepipeline/platform/DefaultDecoder:mDecodeBuffers	Landroidx/core/util/Pools$SynchronizedPool;
    //   190: invokevirtual 154	androidx/core/util/Pools$SynchronizedPool:acquire	()Ljava/lang/Object;
    //   193: checkcast 56	java/nio/ByteBuffer
    //   196: astore 4
    //   198: aload 4
    //   200: astore 9
    //   202: aload 4
    //   204: ifnonnull +11 -> 215
    //   207: sipush 16384
    //   210: invokestatic 60	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
    //   213: astore 9
    //   215: aload 9
    //   217: invokevirtual 158	java/nio/ByteBuffer:array	()[B
    //   220: astore 4
    //   222: aload_2
    //   223: aload 4
    //   225: putfield 161	android/graphics/BitmapFactory$Options:inTempStorage	[B
    //   228: aload_3
    //   229: ifnull +124 -> 353
    //   232: aload 10
    //   234: ifnull +119 -> 353
    //   237: aload_2
    //   238: getfield 106	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
    //   241: astore 4
    //   243: aload 10
    //   245: iload 5
    //   247: iload 6
    //   249: aload 4
    //   251: invokevirtual 165	android/graphics/Bitmap:reconfigure	(IILandroid/graphics/Bitmap$Config;)V
    //   254: aload_1
    //   255: iconst_1
    //   256: invokestatic 171	android/graphics/BitmapRegionDecoder:newInstance	(Ljava/io/InputStream;Z)Landroid/graphics/BitmapRegionDecoder;
    //   259: astore 11
    //   261: aload 11
    //   263: astore 8
    //   265: aload 8
    //   267: astore 4
    //   269: aload 11
    //   271: aload_3
    //   272: aload_2
    //   273: invokevirtual 175	android/graphics/BitmapRegionDecoder:decodeRegion	(Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   276: astore 12
    //   278: aload 12
    //   280: astore_3
    //   281: aload 11
    //   283: ifnull +72 -> 355
    //   286: aload 11
    //   288: invokevirtual 178	android/graphics/BitmapRegionDecoder:recycle	()V
    //   291: aload 12
    //   293: astore_3
    //   294: goto +61 -> 355
    //   297: astore_2
    //   298: aconst_null
    //   299: astore 4
    //   301: goto +40 -> 341
    //   304: aconst_null
    //   305: astore 8
    //   307: aload 8
    //   309: astore 4
    //   311: getstatic 31	com/facebook/imagepipeline/platform/DefaultDecoder:SHORT	Ljava/lang/Class;
    //   314: ldc -76
    //   316: iconst_1
    //   317: anewarray 4	java/lang/Object
    //   320: dup
    //   321: iconst_0
    //   322: aload_3
    //   323: aastore
    //   324: invokestatic 186	com/facebook/common/logging/FLog:e	(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Object;)V
    //   327: aload 8
    //   329: ifnull +24 -> 353
    //   332: aload 8
    //   334: invokevirtual 178	android/graphics/BitmapRegionDecoder:recycle	()V
    //   337: goto +16 -> 353
    //   340: astore_2
    //   341: aload 4
    //   343: ifnull +8 -> 351
    //   346: aload 4
    //   348: invokevirtual 178	android/graphics/BitmapRegionDecoder:recycle	()V
    //   351: aload_2
    //   352: athrow
    //   353: aconst_null
    //   354: astore_3
    //   355: aload_3
    //   356: astore 4
    //   358: aload_3
    //   359: ifnonnull +11 -> 370
    //   362: aload_1
    //   363: aconst_null
    //   364: aload_2
    //   365: invokestatic 192	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   368: astore 4
    //   370: aload_0
    //   371: getfield 54	com/facebook/imagepipeline/platform/DefaultDecoder:mDecodeBuffers	Landroidx/core/util/Pools$SynchronizedPool;
    //   374: aload 9
    //   376: invokevirtual 66	androidx/core/util/Pools$SynchronizedPool:release	(Ljava/lang/Object;)Z
    //   379: pop
    //   380: aload 10
    //   382: ifnull +37 -> 419
    //   385: aload 10
    //   387: aload 4
    //   389: if_acmpne +6 -> 395
    //   392: goto +27 -> 419
    //   395: aload_0
    //   396: getfield 52	com/facebook/imagepipeline/platform/DefaultDecoder:mBitmapPool	Lcom/facebook/imagepipeline/memory/BitmapPool;
    //   399: aload 10
    //   401: invokeinterface 195 2 0
    //   406: aload 4
    //   408: invokevirtual 196	android/graphics/Bitmap:recycle	()V
    //   411: new 198	java/lang/IllegalStateException
    //   414: dup
    //   415: invokespecial 199	java/lang/IllegalStateException:<init>	()V
    //   418: athrow
    //   419: aload 4
    //   421: aload_0
    //   422: getfield 52	com/facebook/imagepipeline/platform/DefaultDecoder:mBitmapPool	Lcom/facebook/imagepipeline/memory/BitmapPool;
    //   425: invokestatic 205	com/facebook/common/references/CloseableReference:of	(Ljava/lang/Object;Lcom/facebook/common/references/ResourceReleaser;)Lcom/facebook/common/references/CloseableReference;
    //   428: areturn
    //   429: astore_1
    //   430: goto +76 -> 506
    //   433: astore_1
    //   434: aload 10
    //   436: ifnull +14 -> 450
    //   439: aload_0
    //   440: getfield 52	com/facebook/imagepipeline/platform/DefaultDecoder:mBitmapPool	Lcom/facebook/imagepipeline/memory/BitmapPool;
    //   443: aload 10
    //   445: invokeinterface 195 2 0
    //   450: aload_1
    //   451: athrow
    //   452: astore_2
    //   453: aload 10
    //   455: ifnull +14 -> 469
    //   458: aload_0
    //   459: getfield 52	com/facebook/imagepipeline/platform/DefaultDecoder:mBitmapPool	Lcom/facebook/imagepipeline/memory/BitmapPool;
    //   462: aload 10
    //   464: invokeinterface 195 2 0
    //   469: aload_1
    //   470: invokevirtual 210	java/io/InputStream:reset	()V
    //   473: aload_1
    //   474: invokestatic 213	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;)Landroid/graphics/Bitmap;
    //   477: astore_1
    //   478: aload_1
    //   479: ifnull +23 -> 502
    //   482: aload_1
    //   483: invokestatic 219	com/facebook/imagepipeline/bitmaps/SimpleBitmapReleaser:getInstance	()Lcom/facebook/imagepipeline/bitmaps/SimpleBitmapReleaser;
    //   486: invokestatic 205	com/facebook/common/references/CloseableReference:of	(Ljava/lang/Object;Lcom/facebook/common/references/ResourceReleaser;)Lcom/facebook/common/references/CloseableReference;
    //   489: astore_1
    //   490: aload_0
    //   491: getfield 54	com/facebook/imagepipeline/platform/DefaultDecoder:mDecodeBuffers	Landroidx/core/util/Pools$SynchronizedPool;
    //   494: aload 9
    //   496: invokevirtual 66	androidx/core/util/Pools$SynchronizedPool:release	(Ljava/lang/Object;)Z
    //   499: pop
    //   500: aload_1
    //   501: areturn
    //   502: aload_2
    //   503: athrow
    //   504: aload_2
    //   505: athrow
    //   506: aload_0
    //   507: getfield 54	com/facebook/imagepipeline/platform/DefaultDecoder:mDecodeBuffers	Landroidx/core/util/Pools$SynchronizedPool;
    //   510: aload 9
    //   512: invokevirtual 66	androidx/core/util/Pools$SynchronizedPool:release	(Ljava/lang/Object;)Z
    //   515: pop
    //   516: aload_1
    //   517: athrow
    //   518: new 221	java/lang/NullPointerException
    //   521: dup
    //   522: ldc -33
    //   524: invokespecial 226	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
    //   527: athrow
    //   528: astore 4
    //   530: goto -226 -> 304
    //   533: astore 4
    //   535: goto -228 -> 307
    //   538: astore_1
    //   539: goto -35 -> 504
    //   542: astore_1
    //   543: goto -39 -> 504
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	546	0	this	DefaultDecoder
    //   0	546	1	paramInputStream	InputStream
    //   0	546	2	paramOptions	BitmapFactory.Options
    //   0	546	3	paramRect	Rect
    //   0	546	4	paramColorSpace	ColorSpace
    //   9	237	5	i	int
    //   15	233	6	j	int
    //   73	60	7	k	int
    //   165	168	8	localObject1	Object
    //   200	311	9	localObject2	Object
    //   96	367	10	localBitmap1	android.graphics.Bitmap
    //   259	28	11	localBitmapRegionDecoder	android.graphics.BitmapRegionDecoder
    //   276	16	12	localBitmap2	android.graphics.Bitmap
    // Exception table:
    //   from	to	target	type
    //   243	261	297	java/lang/Throwable
    //   269	278	340	java/lang/Throwable
    //   311	327	340	java/lang/Throwable
    //   215	222	429	java/lang/Throwable
    //   222	228	429	java/lang/Throwable
    //   286	291	429	java/lang/Throwable
    //   332	337	429	java/lang/Throwable
    //   346	351	429	java/lang/Throwable
    //   351	353	429	java/lang/Throwable
    //   362	370	429	java/lang/Throwable
    //   439	450	429	java/lang/Throwable
    //   450	452	429	java/lang/Throwable
    //   458	469	429	java/lang/Throwable
    //   469	478	429	java/lang/Throwable
    //   482	490	429	java/lang/Throwable
    //   502	504	429	java/lang/Throwable
    //   504	506	429	java/lang/Throwable
    //   215	222	433	java/lang/RuntimeException
    //   222	228	433	java/lang/RuntimeException
    //   286	291	433	java/lang/RuntimeException
    //   332	337	433	java/lang/RuntimeException
    //   346	351	433	java/lang/RuntimeException
    //   351	353	433	java/lang/RuntimeException
    //   362	370	433	java/lang/RuntimeException
    //   215	222	452	java/lang/IllegalArgumentException
    //   286	291	452	java/lang/IllegalArgumentException
    //   332	337	452	java/lang/IllegalArgumentException
    //   346	351	452	java/lang/IllegalArgumentException
    //   351	353	452	java/lang/IllegalArgumentException
    //   362	370	452	java/lang/IllegalArgumentException
    //   243	261	528	java/io/IOException
    //   269	278	533	java/io/IOException
    //   469	478	538	java/io/IOException
    //   482	490	538	java/io/IOException
    //   502	504	542	java/io/IOException
  }
  
  private static BitmapFactory.Options getDecodeOptionsForStream(EncodedImage paramEncodedImage, Bitmap.Config paramConfig)
  {
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    inSampleSize = paramEncodedImage.getSampleSize();
    inJustDecodeBounds = true;
    BitmapFactory.decodeStream(paramEncodedImage.getInputStream(), null, localOptions);
    if ((outWidth != -1) && (outHeight != -1))
    {
      inJustDecodeBounds = false;
      inDither = true;
      inPreferredConfig = paramConfig;
      inMutable = true;
      return localOptions;
    }
    throw new IllegalArgumentException();
  }
  
  public CloseableReference decodeFromEncodedImage(EncodedImage paramEncodedImage, Bitmap.Config paramConfig, Rect paramRect)
  {
    return decodeFromEncodedImageWithColorSpace(paramEncodedImage, paramConfig, paramRect, null);
  }
  
  public CloseableReference decodeFromEncodedImageWithColorSpace(EncodedImage paramEncodedImage, Bitmap.Config paramConfig, Rect paramRect, ColorSpace paramColorSpace)
  {
    paramConfig = getDecodeOptionsForStream(paramEncodedImage, paramConfig);
    int i;
    if (inPreferredConfig != Bitmap.Config.ARGB_8888) {
      i = 1;
    } else {
      i = 0;
    }
    try
    {
      paramConfig = decodeFromStream(paramEncodedImage.getInputStream(), paramConfig, paramRect, paramColorSpace);
      return paramConfig;
    }
    catch (RuntimeException paramConfig)
    {
      if (i != 0) {
        return decodeFromEncodedImageWithColorSpace(paramEncodedImage, Bitmap.Config.ARGB_8888, paramRect, paramColorSpace);
      }
      throw paramConfig;
    }
  }
  
  public CloseableReference decodeJPEGFromEncodedImage(EncodedImage paramEncodedImage, Bitmap.Config paramConfig, Rect paramRect, int paramInt)
  {
    return decodeJPEGFromEncodedImageWithColorSpace(paramEncodedImage, paramConfig, paramRect, paramInt, null);
  }
  
  public CloseableReference decodeJPEGFromEncodedImageWithColorSpace(EncodedImage paramEncodedImage, Bitmap.Config paramConfig, Rect paramRect, int paramInt, ColorSpace paramColorSpace)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a13 = a12\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  protected CloseableReference decodeStaticImageFromStream(InputStream paramInputStream, BitmapFactory.Options paramOptions, Rect paramRect)
  {
    return decodeFromStream(paramInputStream, paramOptions, paramRect, null);
  }
  
  public abstract int getBitmapSize(int paramInt1, int paramInt2, BitmapFactory.Options paramOptions);
}
