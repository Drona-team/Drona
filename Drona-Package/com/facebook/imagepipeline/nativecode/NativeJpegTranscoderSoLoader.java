package com.facebook.imagepipeline.nativecode;

public class NativeJpegTranscoderSoLoader
{
  private static boolean sInitialized;
  
  public NativeJpegTranscoderSoLoader() {}
  
  /* Error */
  public static void ensure()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 18	com/facebook/imagepipeline/nativecode/NativeJpegTranscoderSoLoader:sInitialized	Z
    //   6: ifne +29 -> 35
    //   9: getstatic 24	android/os/Build$VERSION:SDK_INT	I
    //   12: istore_0
    //   13: iload_0
    //   14: bipush 16
    //   16: if_icmpgt +9 -> 25
    //   19: ldc 26
    //   21: invokestatic 32	com/facebook/soloader/SoLoader:loadLibrary	(Ljava/lang/String;)Z
    //   24: pop
    //   25: ldc 34
    //   27: invokestatic 32	com/facebook/soloader/SoLoader:loadLibrary	(Ljava/lang/String;)Z
    //   30: pop
    //   31: iconst_1
    //   32: putstatic 18	com/facebook/imagepipeline/nativecode/NativeJpegTranscoderSoLoader:sInitialized	Z
    //   35: ldc 2
    //   37: monitorexit
    //   38: return
    //   39: astore_1
    //   40: ldc 2
    //   42: monitorexit
    //   43: aload_1
    //   44: athrow
    //   45: astore_1
    //   46: goto -21 -> 25
    // Local variable table:
    //   start	length	slot	name	signature
    //   12	5	0	i	int
    //   39	5	1	localThrowable	Throwable
    //   45	1	1	localUnsatisfiedLinkError	UnsatisfiedLinkError
    // Exception table:
    //   from	to	target	type
    //   3	13	39	java/lang/Throwable
    //   19	25	39	java/lang/Throwable
    //   25	35	39	java/lang/Throwable
    //   19	25	45	java/lang/UnsatisfiedLinkError
  }
}
