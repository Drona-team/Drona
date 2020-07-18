package com.facebook.imagepipeline.bitmaps;

import com.facebook.common.memory.PooledByteBufferFactory;

public class EmptyJpegGenerator
{
  private static final byte[] EMPTY_JPEG_PREFIX = { -1, -40, -1, -37, 0, 67, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 0, 17, 8 };
  private static final byte[] EMPTY_JPEG_SUFFIX = { 3, 1, 34, 0, 2, 17, 0, 3, 17, 0, -1, -60, 0, 31, 0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 16, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125, 1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, -127, -111, -95, 8, 35, 66, -79, -63, 21, 82, -47, -16, 36, 51, 98, 114, -126, 9, 10, 22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -60, 0, 31, 1, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 17, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119, 0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, -127, 8, 20, 66, -111, -95, -79, -63, 9, 35, 51, 82, -16, 21, 98, 114, -47, 10, 22, 36, 52, -31, 37, -15, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -126, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -30, -29, -28, -27, -26, -25, -24, -23, -22, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -38, 0, 12, 3, 1, 0, 2, 17, 3, 17, 0, 63, 0, -114, -118, 40, -96, 15, -1, -39 };
  private final PooledByteBufferFactory mPooledByteBufferFactory;
  
  public EmptyJpegGenerator(PooledByteBufferFactory paramPooledByteBufferFactory)
  {
    mPooledByteBufferFactory = paramPooledByteBufferFactory;
  }
  
  /* Error */
  public com.facebook.common.references.CloseableReference generate(short paramShort1, short paramShort2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aconst_null
    //   4: astore 6
    //   6: aload 6
    //   8: astore 5
    //   10: aload_0
    //   11: getfield 196	com/facebook/imagepipeline/bitmaps/EmptyJpegGenerator:mPooledByteBufferFactory	Lcom/facebook/common/memory/PooledByteBufferFactory;
    //   14: astore 8
    //   16: aload 6
    //   18: astore 5
    //   20: getstatic 21	com/facebook/imagepipeline/bitmaps/EmptyJpegGenerator:EMPTY_JPEG_PREFIX	[B
    //   23: arraylength
    //   24: istore_3
    //   25: aload 6
    //   27: astore 5
    //   29: getstatic 189	com/facebook/imagepipeline/bitmaps/EmptyJpegGenerator:EMPTY_JPEG_SUFFIX	[B
    //   32: arraylength
    //   33: istore 4
    //   35: aload 6
    //   37: astore 5
    //   39: aload 8
    //   41: iload_3
    //   42: iload 4
    //   44: iadd
    //   45: iconst_4
    //   46: iadd
    //   47: invokeinterface 208 2 0
    //   52: astore 6
    //   54: aload 6
    //   56: astore 5
    //   58: getstatic 21	com/facebook/imagepipeline/bitmaps/EmptyJpegGenerator:EMPTY_JPEG_PREFIX	[B
    //   61: astore 7
    //   63: aload 6
    //   65: aload 7
    //   67: invokevirtual 214	java/io/OutputStream:write	([B)V
    //   70: iload_2
    //   71: bipush 8
    //   73: ishr
    //   74: i2b
    //   75: istore_3
    //   76: aload 6
    //   78: iload_3
    //   79: invokevirtual 217	java/io/OutputStream:write	(I)V
    //   82: iload_2
    //   83: sipush 255
    //   86: iand
    //   87: i2b
    //   88: istore_2
    //   89: aload 6
    //   91: iload_2
    //   92: invokevirtual 217	java/io/OutputStream:write	(I)V
    //   95: iload_1
    //   96: bipush 8
    //   98: ishr
    //   99: i2b
    //   100: istore_2
    //   101: aload 6
    //   103: iload_2
    //   104: invokevirtual 217	java/io/OutputStream:write	(I)V
    //   107: iload_1
    //   108: sipush 255
    //   111: iand
    //   112: i2b
    //   113: istore_1
    //   114: aload 6
    //   116: iload_1
    //   117: invokevirtual 217	java/io/OutputStream:write	(I)V
    //   120: getstatic 189	com/facebook/imagepipeline/bitmaps/EmptyJpegGenerator:EMPTY_JPEG_SUFFIX	[B
    //   123: astore 7
    //   125: aload 6
    //   127: aload 7
    //   129: invokevirtual 214	java/io/OutputStream:write	([B)V
    //   132: aload 6
    //   134: invokevirtual 223	com/facebook/common/memory/PooledByteBufferOutputStream:toByteBuffer	()Lcom/facebook/common/memory/PooledByteBuffer;
    //   137: invokestatic 229	com/facebook/common/references/CloseableReference:of	(Ljava/io/Closeable;)Lcom/facebook/common/references/CloseableReference;
    //   140: astore 7
    //   142: aload 6
    //   144: ifnull +63 -> 207
    //   147: aload 6
    //   149: invokevirtual 232	com/facebook/common/memory/PooledByteBufferOutputStream:close	()V
    //   152: aload 7
    //   154: areturn
    //   155: astore 6
    //   157: goto +37 -> 194
    //   160: astore 7
    //   162: aload 6
    //   164: astore 5
    //   166: aload 7
    //   168: astore 6
    //   170: goto +14 -> 184
    //   173: astore 6
    //   175: goto +19 -> 194
    //   178: astore 6
    //   180: aload 7
    //   182: astore 5
    //   184: new 234	java/lang/RuntimeException
    //   187: dup
    //   188: aload 6
    //   190: invokespecial 237	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   193: athrow
    //   194: aload 5
    //   196: ifnull +8 -> 204
    //   199: aload 5
    //   201: invokevirtual 232	com/facebook/common/memory/PooledByteBufferOutputStream:close	()V
    //   204: aload 6
    //   206: athrow
    //   207: aload 7
    //   209: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	210	0	this	EmptyJpegGenerator
    //   0	210	1	paramShort1	short
    //   0	210	2	paramShort2	short
    //   24	55	3	i	int
    //   33	12	4	j	int
    //   8	192	5	localObject1	Object
    //   4	144	6	localPooledByteBufferOutputStream	com.facebook.common.memory.PooledByteBufferOutputStream
    //   155	8	6	localThrowable1	Throwable
    //   168	1	6	localIOException1	java.io.IOException
    //   173	1	6	localThrowable2	Throwable
    //   178	27	6	localIOException2	java.io.IOException
    //   1	152	7	localObject2	Object
    //   160	48	7	localIOException3	java.io.IOException
    //   14	26	8	localPooledByteBufferFactory	PooledByteBufferFactory
    // Exception table:
    //   from	to	target	type
    //   63	70	155	java/lang/Throwable
    //   76	82	155	java/lang/Throwable
    //   89	95	155	java/lang/Throwable
    //   101	107	155	java/lang/Throwable
    //   114	120	155	java/lang/Throwable
    //   125	142	155	java/lang/Throwable
    //   63	70	160	java/io/IOException
    //   76	82	160	java/io/IOException
    //   89	95	160	java/io/IOException
    //   101	107	160	java/io/IOException
    //   114	120	160	java/io/IOException
    //   125	142	160	java/io/IOException
    //   10	16	173	java/lang/Throwable
    //   20	25	173	java/lang/Throwable
    //   29	35	173	java/lang/Throwable
    //   39	54	173	java/lang/Throwable
    //   184	194	173	java/lang/Throwable
    //   39	54	178	java/io/IOException
  }
}
