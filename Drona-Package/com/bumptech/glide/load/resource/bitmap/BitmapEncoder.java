package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import androidx.annotation.Nullable;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;

public class BitmapEncoder
  implements ResourceEncoder<Bitmap>
{
  public static final Option<Bitmap.CompressFormat> COMPRESSION_FORMAT = Option.memory("com.bumptech.glide.load.resource.bitmap.BitmapEncoder.CompressionFormat");
  public static final Option<Integer> COMPRESSION_QUALITY = Option.memory("com.bumptech.glide.load.resource.bitmap.BitmapEncoder.CompressionQuality", Integer.valueOf(90));
  private static final String TAG = "BitmapEncoder";
  @Nullable
  private final ArrayPool arrayPool;
  
  public BitmapEncoder()
  {
    arrayPool = null;
  }
  
  public BitmapEncoder(ArrayPool paramArrayPool)
  {
    arrayPool = paramArrayPool;
  }
  
  private Bitmap.CompressFormat getFormat(Bitmap paramBitmap, Options paramOptions)
  {
    paramOptions = (Bitmap.CompressFormat)paramOptions.getOption(COMPRESSION_FORMAT);
    if (paramOptions != null) {
      return paramOptions;
    }
    if (paramBitmap.hasAlpha()) {
      return Bitmap.CompressFormat.PNG;
    }
    return Bitmap.CompressFormat.JPEG;
  }
  
  /* Error */
  public boolean encode(com.bumptech.glide.load.engine.Resource paramResource, java.io.File paramFile, Options paramOptions)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface 86 1 0
    //   6: checkcast 63	android/graphics/Bitmap
    //   9: astore 12
    //   11: aload_0
    //   12: aload 12
    //   14: aload_3
    //   15: invokespecial 88	com/bumptech/glide/load/resource/bitmap/BitmapEncoder:getFormat	(Landroid/graphics/Bitmap;Lcom/bumptech/glide/load/Options;)Landroid/graphics/Bitmap$CompressFormat;
    //   18: astore 13
    //   20: ldc 90
    //   22: aload 12
    //   24: invokevirtual 94	android/graphics/Bitmap:getWidth	()I
    //   27: invokestatic 29	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   30: aload 12
    //   32: invokevirtual 97	android/graphics/Bitmap:getHeight	()I
    //   35: invokestatic 29	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   38: aload 13
    //   40: invokestatic 103	com/bumptech/glide/util/pool/GlideTrace:beginSectionFormat	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   43: invokestatic 109	com/bumptech/glide/util/LogTime:getLogTime	()J
    //   46: lstore 5
    //   48: aload_3
    //   49: getstatic 37	com/bumptech/glide/load/resource/bitmap/BitmapEncoder:COMPRESSION_QUALITY	Lcom/bumptech/glide/load/Option;
    //   52: invokevirtual 59	com/bumptech/glide/load/Options:getOption	(Lcom/bumptech/glide/load/Option;)Ljava/lang/Object;
    //   55: checkcast 25	java/lang/Integer
    //   58: invokevirtual 112	java/lang/Integer:intValue	()I
    //   61: istore 4
    //   63: iconst_0
    //   64: istore 7
    //   66: iconst_0
    //   67: istore 8
    //   69: aconst_null
    //   70: astore 11
    //   72: aconst_null
    //   73: astore 10
    //   75: new 114	java/io/FileOutputStream
    //   78: dup
    //   79: aload_2
    //   80: invokespecial 117	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   83: astore_1
    //   84: aload_0
    //   85: getfield 50	com/bumptech/glide/load/resource/bitmap/BitmapEncoder:arrayPool	Lcom/bumptech/glide/load/engine/bitmap_recycle/ArrayPool;
    //   88: astore_2
    //   89: aload_2
    //   90: ifnull +23 -> 113
    //   93: aload_0
    //   94: getfield 50	com/bumptech/glide/load/resource/bitmap/BitmapEncoder:arrayPool	Lcom/bumptech/glide/load/engine/bitmap_recycle/ArrayPool;
    //   97: astore_2
    //   98: new 119	com/bumptech/glide/load/data/BufferedOutputStream
    //   101: dup
    //   102: aload_1
    //   103: aload_2
    //   104: invokespecial 122	com/bumptech/glide/load/data/BufferedOutputStream:<init>	(Ljava/io/OutputStream;Lcom/bumptech/glide/load/engine/bitmap_recycle/ArrayPool;)V
    //   107: astore_2
    //   108: aload_2
    //   109: astore_1
    //   110: goto +3 -> 113
    //   113: aload_1
    //   114: astore 10
    //   116: aload_1
    //   117: checkcast 124	java/io/OutputStream
    //   120: astore_2
    //   121: aload_1
    //   122: astore 10
    //   124: aload_1
    //   125: astore 11
    //   127: aload 12
    //   129: aload 13
    //   131: iload 4
    //   133: aload_2
    //   134: invokevirtual 128	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   137: pop
    //   138: aload_1
    //   139: checkcast 124	java/io/OutputStream
    //   142: astore_2
    //   143: aload_1
    //   144: astore 10
    //   146: aload_1
    //   147: astore 11
    //   149: aload_2
    //   150: invokevirtual 131	java/io/OutputStream:close	()V
    //   153: iconst_1
    //   154: istore 7
    //   156: aload_1
    //   157: checkcast 124	java/io/OutputStream
    //   160: astore_1
    //   161: aload_1
    //   162: invokevirtual 131	java/io/OutputStream:close	()V
    //   165: goto +63 -> 228
    //   168: astore_2
    //   169: aload_1
    //   170: astore 10
    //   172: aload_2
    //   173: astore_1
    //   174: goto +175 -> 349
    //   177: astore_2
    //   178: goto +11 -> 189
    //   181: astore_1
    //   182: goto +167 -> 349
    //   185: astore_2
    //   186: aload 11
    //   188: astore_1
    //   189: aload_1
    //   190: astore 10
    //   192: ldc 16
    //   194: iconst_3
    //   195: invokestatic 137	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   198: istore 9
    //   200: iload 9
    //   202: ifeq +15 -> 217
    //   205: aload_1
    //   206: astore 10
    //   208: ldc 16
    //   210: ldc -117
    //   212: aload_2
    //   213: invokestatic 143	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   216: pop
    //   217: aload_1
    //   218: ifnull +10 -> 228
    //   221: iload 8
    //   223: istore 7
    //   225: goto -69 -> 156
    //   228: ldc 16
    //   230: iconst_2
    //   231: invokestatic 137	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   234: istore 8
    //   236: iload 8
    //   238: ifeq +105 -> 343
    //   241: new 145	java/lang/StringBuilder
    //   244: dup
    //   245: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   248: astore_1
    //   249: aload_1
    //   250: ldc -108
    //   252: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   255: pop
    //   256: aload_1
    //   257: aload 13
    //   259: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   262: pop
    //   263: aload_1
    //   264: ldc -99
    //   266: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   269: pop
    //   270: aload_1
    //   271: aload 12
    //   273: invokestatic 163	com/bumptech/glide/util/Util:getBitmapByteSize	(Landroid/graphics/Bitmap;)I
    //   276: invokevirtual 166	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   279: pop
    //   280: aload_1
    //   281: ldc -88
    //   283: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   286: pop
    //   287: aload_1
    //   288: lload 5
    //   290: invokestatic 172	com/bumptech/glide/util/LogTime:getElapsedMillis	(J)D
    //   293: invokevirtual 175	java/lang/StringBuilder:append	(D)Ljava/lang/StringBuilder;
    //   296: pop
    //   297: aload_1
    //   298: ldc -79
    //   300: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   303: pop
    //   304: aload_1
    //   305: aload_3
    //   306: getstatic 44	com/bumptech/glide/load/resource/bitmap/BitmapEncoder:COMPRESSION_FORMAT	Lcom/bumptech/glide/load/Option;
    //   309: invokevirtual 59	com/bumptech/glide/load/Options:getOption	(Lcom/bumptech/glide/load/Option;)Ljava/lang/Object;
    //   312: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   315: pop
    //   316: aload_1
    //   317: ldc -77
    //   319: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   322: pop
    //   323: aload_1
    //   324: aload 12
    //   326: invokevirtual 67	android/graphics/Bitmap:hasAlpha	()Z
    //   329: invokevirtual 182	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   332: pop
    //   333: ldc 16
    //   335: aload_1
    //   336: invokevirtual 186	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   339: invokestatic 190	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   342: pop
    //   343: invokestatic 193	com/bumptech/glide/util/pool/GlideTrace:endSection	()V
    //   346: iload 7
    //   348: ireturn
    //   349: aload 10
    //   351: ifnull +13 -> 364
    //   354: aload 10
    //   356: checkcast 124	java/io/OutputStream
    //   359: astore_2
    //   360: aload_2
    //   361: invokevirtual 131	java/io/OutputStream:close	()V
    //   364: aload_1
    //   365: athrow
    //   366: astore_1
    //   367: invokestatic 193	com/bumptech/glide/util/pool/GlideTrace:endSection	()V
    //   370: aload_1
    //   371: athrow
    //   372: astore_1
    //   373: goto -145 -> 228
    //   376: astore_2
    //   377: goto -13 -> 364
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	380	0	this	BitmapEncoder
    //   0	380	1	paramResource	com.bumptech.glide.load.engine.Resource
    //   0	380	2	paramFile	java.io.File
    //   0	380	3	paramOptions	Options
    //   61	71	4	i	int
    //   46	243	5	l	long
    //   64	283	7	bool1	boolean
    //   67	170	8	bool2	boolean
    //   198	3	9	bool3	boolean
    //   73	282	10	localResource1	com.bumptech.glide.load.engine.Resource
    //   70	117	11	localResource2	com.bumptech.glide.load.engine.Resource
    //   9	316	12	localBitmap	Bitmap
    //   18	240	13	localCompressFormat	Bitmap.CompressFormat
    // Exception table:
    //   from	to	target	type
    //   84	89	168	java/lang/Throwable
    //   98	108	168	java/lang/Throwable
    //   98	108	177	java/io/IOException
    //   75	84	181	java/lang/Throwable
    //   116	121	181	java/lang/Throwable
    //   127	138	181	java/lang/Throwable
    //   149	153	181	java/lang/Throwable
    //   192	200	181	java/lang/Throwable
    //   208	217	181	java/lang/Throwable
    //   75	84	185	java/io/IOException
    //   127	138	185	java/io/IOException
    //   149	153	185	java/io/IOException
    //   43	63	366	java/lang/Throwable
    //   161	165	366	java/lang/Throwable
    //   228	236	366	java/lang/Throwable
    //   241	343	366	java/lang/Throwable
    //   360	364	366	java/lang/Throwable
    //   364	366	366	java/lang/Throwable
    //   161	165	372	java/io/IOException
    //   360	364	376	java/io/IOException
  }
  
  public EncodeStrategy getEncodeStrategy(Options paramOptions)
  {
    return EncodeStrategy.TRANSFORMED;
  }
}
