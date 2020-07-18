package com.facebook.imagepipeline.bitmaps;

import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.os.Build.VERSION;
import com.facebook.common.webp.BitmapCreator;
import com.facebook.imagepipeline.memory.FlexByteArrayPool;
import com.facebook.imagepipeline.memory.PoolFactory;

public class HoneycombBitmapCreator
  implements BitmapCreator
{
  private final FlexByteArrayPool mFlexByteArrayPool;
  private final EmptyJpegGenerator mJpegGenerator;
  
  public HoneycombBitmapCreator(PoolFactory paramPoolFactory)
  {
    mFlexByteArrayPool = paramPoolFactory.getFlexByteArrayPool();
    mJpegGenerator = new EmptyJpegGenerator(paramPoolFactory.getPooledByteBufferFactory());
  }
  
  private static BitmapFactory.Options getBitmapFactoryOptions(int paramInt, Bitmap.Config paramConfig)
  {
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    inDither = true;
    inPreferredConfig = paramConfig;
    inPurgeable = true;
    inInputShareable = true;
    inSampleSize = paramInt;
    if (Build.VERSION.SDK_INT >= 11) {
      inMutable = true;
    }
    return localOptions;
  }
  
  /* Error */
  public android.graphics.Bitmap createNakedBitmap(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 34	com/facebook/imagepipeline/bitmaps/HoneycombBitmapCreator:mJpegGenerator	Lcom/facebook/imagepipeline/bitmaps/EmptyJpegGenerator;
    //   4: iload_1
    //   5: i2s
    //   6: iload_2
    //   7: i2s
    //   8: invokevirtual 74	com/facebook/imagepipeline/bitmaps/EmptyJpegGenerator:generate	(SS)Lcom/facebook/common/references/CloseableReference;
    //   11: astore 7
    //   13: aconst_null
    //   14: astore 4
    //   16: new 76	com/facebook/imagepipeline/image/EncodedImage
    //   19: dup
    //   20: aload 7
    //   22: invokespecial 79	com/facebook/imagepipeline/image/EncodedImage:<init>	(Lcom/facebook/common/references/CloseableReference;)V
    //   25: astore 5
    //   27: aload 5
    //   29: getstatic 85	com/facebook/imageformat/DefaultImageFormats:JPEG	Lcom/facebook/imageformat/ImageFormat;
    //   32: invokevirtual 89	com/facebook/imagepipeline/image/EncodedImage:setImageFormat	(Lcom/facebook/imageformat/ImageFormat;)V
    //   35: aload 5
    //   37: invokevirtual 93	com/facebook/imagepipeline/image/EncodedImage:getSampleSize	()I
    //   40: aload_3
    //   41: invokestatic 95	com/facebook/imagepipeline/bitmaps/HoneycombBitmapCreator:getBitmapFactoryOptions	(ILandroid/graphics/Bitmap$Config;)Landroid/graphics/BitmapFactory$Options;
    //   44: astore_3
    //   45: aload 7
    //   47: invokevirtual 101	com/facebook/common/references/CloseableReference:get	()Ljava/lang/Object;
    //   50: checkcast 103	com/facebook/common/memory/PooledByteBuffer
    //   53: invokeinterface 106 1 0
    //   58: istore_1
    //   59: aload 7
    //   61: invokevirtual 101	com/facebook/common/references/CloseableReference:get	()Ljava/lang/Object;
    //   64: checkcast 103	com/facebook/common/memory/PooledByteBuffer
    //   67: astore 8
    //   69: aload_0
    //   70: getfield 23	com/facebook/imagepipeline/bitmaps/HoneycombBitmapCreator:mFlexByteArrayPool	Lcom/facebook/imagepipeline/memory/FlexByteArrayPool;
    //   73: astore 6
    //   75: aload 6
    //   77: iload_1
    //   78: iconst_2
    //   79: iadd
    //   80: invokevirtual 112	com/facebook/imagepipeline/memory/FlexByteArrayPool:calculateDimensions	(I)Lcom/facebook/common/references/CloseableReference;
    //   83: astore 6
    //   85: aload 6
    //   87: invokevirtual 101	com/facebook/common/references/CloseableReference:get	()Ljava/lang/Object;
    //   90: checkcast 114	[B
    //   93: astore 4
    //   95: aload 8
    //   97: iconst_0
    //   98: aload 4
    //   100: iconst_0
    //   101: iload_1
    //   102: invokeinterface 118 5 0
    //   107: pop
    //   108: aload 4
    //   110: iconst_0
    //   111: iload_1
    //   112: aload_3
    //   113: invokestatic 124	android/graphics/BitmapFactory:decodeByteArray	([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   116: astore_3
    //   117: aload_3
    //   118: iconst_1
    //   119: invokevirtual 130	android/graphics/Bitmap:setHasAlpha	(Z)V
    //   122: aload_3
    //   123: iconst_0
    //   124: invokevirtual 134	android/graphics/Bitmap:eraseColor	(I)V
    //   127: aload 6
    //   129: invokestatic 137	com/facebook/common/references/CloseableReference:closeSafely	(Lcom/facebook/common/references/CloseableReference;)V
    //   132: aload 5
    //   134: invokestatic 140	com/facebook/imagepipeline/image/EncodedImage:closeSafely	(Lcom/facebook/imagepipeline/image/EncodedImage;)V
    //   137: aload 7
    //   139: invokestatic 137	com/facebook/common/references/CloseableReference:closeSafely	(Lcom/facebook/common/references/CloseableReference;)V
    //   142: aload_3
    //   143: areturn
    //   144: astore_3
    //   145: aload 6
    //   147: astore 4
    //   149: goto +11 -> 160
    //   152: astore_3
    //   153: goto +7 -> 160
    //   156: astore_3
    //   157: aconst_null
    //   158: astore 5
    //   160: aload 4
    //   162: invokestatic 137	com/facebook/common/references/CloseableReference:closeSafely	(Lcom/facebook/common/references/CloseableReference;)V
    //   165: aload 5
    //   167: invokestatic 140	com/facebook/imagepipeline/image/EncodedImage:closeSafely	(Lcom/facebook/imagepipeline/image/EncodedImage;)V
    //   170: aload 7
    //   172: invokestatic 137	com/facebook/common/references/CloseableReference:closeSafely	(Lcom/facebook/common/references/CloseableReference;)V
    //   175: aload_3
    //   176: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	177	0	this	HoneycombBitmapCreator
    //   0	177	1	paramInt1	int
    //   0	177	2	paramInt2	int
    //   0	177	3	paramConfig	Bitmap.Config
    //   14	147	4	localObject1	Object
    //   25	141	5	localEncodedImage	com.facebook.imagepipeline.image.EncodedImage
    //   73	73	6	localObject2	Object
    //   11	160	7	localCloseableReference	com.facebook.common.references.CloseableReference
    //   67	29	8	localPooledByteBuffer	com.facebook.common.memory.PooledByteBuffer
    // Exception table:
    //   from	to	target	type
    //   85	127	144	java/lang/Throwable
    //   27	75	152	java/lang/Throwable
    //   75	85	152	java/lang/Throwable
    //   16	27	156	java/lang/Throwable
  }
}
