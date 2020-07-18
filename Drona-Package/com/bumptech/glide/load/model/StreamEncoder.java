package com.bumptech.glide.load.model;

import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.InputStream;

public class StreamEncoder
  implements Encoder<InputStream>
{
  private static final String TAG = "StreamEncoder";
  private final ArrayPool byteArrayPool;
  
  public StreamEncoder(ArrayPool paramArrayPool)
  {
    byteArrayPool = paramArrayPool;
  }
  
  /* Error */
  public boolean encode(InputStream paramInputStream, java.io.File paramFile, com.bumptech.glide.load.Options paramOptions)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 20	com/bumptech/glide/load/model/StreamEncoder:byteArrayPool	Lcom/bumptech/glide/load/engine/bitmap_recycle/ArrayPool;
    //   4: ldc 28
    //   6: ldc 30
    //   8: invokeinterface 36 3 0
    //   13: checkcast 30	[B
    //   16: astore 8
    //   18: iconst_0
    //   19: istore 6
    //   21: aconst_null
    //   22: astore 7
    //   24: aconst_null
    //   25: astore_3
    //   26: new 38	java/io/FileOutputStream
    //   29: dup
    //   30: aload_2
    //   31: invokespecial 41	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   34: astore_2
    //   35: aload_1
    //   36: aload 8
    //   38: invokevirtual 47	java/io/InputStream:read	([B)I
    //   41: istore 4
    //   43: iload 4
    //   45: iconst_m1
    //   46: if_icmpeq +15 -> 61
    //   49: aload_2
    //   50: aload 8
    //   52: iconst_0
    //   53: iload 4
    //   55: invokevirtual 53	java/io/OutputStream:write	([BII)V
    //   58: goto -23 -> 35
    //   61: aload_2
    //   62: invokevirtual 56	java/io/OutputStream:close	()V
    //   65: iconst_1
    //   66: istore 5
    //   68: aload_2
    //   69: invokevirtual 56	java/io/OutputStream:close	()V
    //   72: goto +67 -> 139
    //   75: astore_1
    //   76: aload_2
    //   77: astore_3
    //   78: goto +75 -> 153
    //   81: astore_3
    //   82: aload_2
    //   83: astore_1
    //   84: aload_3
    //   85: astore_2
    //   86: goto +11 -> 97
    //   89: astore_1
    //   90: goto +63 -> 153
    //   93: astore_2
    //   94: aload 7
    //   96: astore_1
    //   97: aload_1
    //   98: astore_3
    //   99: ldc 11
    //   101: iconst_3
    //   102: invokestatic 62	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   105: istore 5
    //   107: iload 5
    //   109: ifeq +14 -> 123
    //   112: aload_1
    //   113: astore_3
    //   114: ldc 11
    //   116: ldc 64
    //   118: aload_2
    //   119: invokestatic 68	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   122: pop
    //   123: iload 6
    //   125: istore 5
    //   127: aload_1
    //   128: ifnull +11 -> 139
    //   131: aload_1
    //   132: invokevirtual 56	java/io/OutputStream:close	()V
    //   135: iload 6
    //   137: istore 5
    //   139: aload_0
    //   140: getfield 20	com/bumptech/glide/load/model/StreamEncoder:byteArrayPool	Lcom/bumptech/glide/load/engine/bitmap_recycle/ArrayPool;
    //   143: aload 8
    //   145: invokeinterface 72 2 0
    //   150: iload 5
    //   152: ireturn
    //   153: aload_3
    //   154: ifnull +7 -> 161
    //   157: aload_3
    //   158: invokevirtual 56	java/io/OutputStream:close	()V
    //   161: aload_0
    //   162: getfield 20	com/bumptech/glide/load/model/StreamEncoder:byteArrayPool	Lcom/bumptech/glide/load/engine/bitmap_recycle/ArrayPool;
    //   165: aload 8
    //   167: invokeinterface 72 2 0
    //   172: aload_1
    //   173: athrow
    //   174: astore_1
    //   175: goto -36 -> 139
    //   178: astore_1
    //   179: iload 6
    //   181: istore 5
    //   183: goto -44 -> 139
    //   186: astore_2
    //   187: goto -26 -> 161
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	190	0	this	StreamEncoder
    //   0	190	1	paramInputStream	InputStream
    //   0	190	2	paramFile	java.io.File
    //   0	190	3	paramOptions	com.bumptech.glide.load.Options
    //   41	13	4	i	int
    //   66	116	5	bool1	boolean
    //   19	161	6	bool2	boolean
    //   22	73	7	localObject	Object
    //   16	150	8	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   35	43	75	java/lang/Throwable
    //   49	58	75	java/lang/Throwable
    //   61	65	75	java/lang/Throwable
    //   35	43	81	java/io/IOException
    //   49	58	81	java/io/IOException
    //   61	65	81	java/io/IOException
    //   26	35	89	java/lang/Throwable
    //   99	107	89	java/lang/Throwable
    //   114	123	89	java/lang/Throwable
    //   26	35	93	java/io/IOException
    //   68	72	174	java/io/IOException
    //   131	135	178	java/io/IOException
    //   157	161	186	java/io/IOException
  }
}
