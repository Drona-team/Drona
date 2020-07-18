package com.bumptech.glide.load.engine.executor;

import android.os.Build.VERSION;
import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class RuntimeCompat
{
  private static final String CPU_LOCATION = "/sys/devices/system/cpu/";
  private static final String CPU_NAME_REGEX = "cpu[0-9]+";
  private static final String PAGE_KEY = "GlideRuntimeCompat";
  
  private RuntimeCompat() {}
  
  static int availableProcessors()
  {
    int j = Runtime.getRuntime().availableProcessors();
    int i = j;
    if (Build.VERSION.SDK_INT < 17) {
      i = Math.max(getCoreCountPre17(), j);
    }
    return i;
  }
  
  /* Error */
  private static int getCoreCountPre17()
  {
    // Byte code:
    //   0: invokestatic 54	android/os/StrictMode:allowThreadDiskReads	()Landroid/os/StrictMode$ThreadPolicy;
    //   3: astore_3
    //   4: new 56	java/io/File
    //   7: dup
    //   8: ldc 10
    //   10: invokespecial 59	java/io/File:<init>	(Ljava/lang/String;)V
    //   13: new 6	com/bumptech/glide/load/engine/executor/RuntimeCompat$1
    //   16: dup
    //   17: ldc 13
    //   19: invokestatic 65	java/util/regex/Pattern:compile	(Ljava/lang/String;)Ljava/util/regex/Pattern;
    //   22: invokespecial 68	com/bumptech/glide/load/engine/executor/RuntimeCompat$1:<init>	(Ljava/util/regex/Pattern;)V
    //   25: invokevirtual 72	java/io/File:listFiles	(Ljava/io/FilenameFilter;)[Ljava/io/File;
    //   28: astore_2
    //   29: aload_3
    //   30: invokestatic 76	android/os/StrictMode:setThreadPolicy	(Landroid/os/StrictMode$ThreadPolicy;)V
    //   33: goto +35 -> 68
    //   36: astore_2
    //   37: goto +49 -> 86
    //   40: astore_2
    //   41: ldc 16
    //   43: bipush 6
    //   45: invokestatic 82	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   48: istore_1
    //   49: iload_1
    //   50: ifeq +12 -> 62
    //   53: ldc 16
    //   55: ldc 84
    //   57: aload_2
    //   58: invokestatic 88	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   61: pop
    //   62: aload_3
    //   63: invokestatic 76	android/os/StrictMode:setThreadPolicy	(Landroid/os/StrictMode$ThreadPolicy;)V
    //   66: aconst_null
    //   67: astore_2
    //   68: aload_2
    //   69: ifnull +9 -> 78
    //   72: aload_2
    //   73: arraylength
    //   74: istore_0
    //   75: goto +5 -> 80
    //   78: iconst_0
    //   79: istore_0
    //   80: iconst_1
    //   81: iload_0
    //   82: invokestatic 46	java/lang/Math:max	(II)I
    //   85: ireturn
    //   86: aload_3
    //   87: invokestatic 76	android/os/StrictMode:setThreadPolicy	(Landroid/os/StrictMode$ThreadPolicy;)V
    //   90: aload_2
    //   91: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   74	8	0	i	int
    //   48	2	1	bool	boolean
    //   28	1	2	arrayOfFile	File[]
    //   36	1	2	localThrowable1	Throwable
    //   40	18	2	localThrowable2	Throwable
    //   67	24	2	localObject	Object
    //   3	84	3	localThreadPolicy	android.os.StrictMode.ThreadPolicy
    // Exception table:
    //   from	to	target	type
    //   41	49	36	java/lang/Throwable
    //   53	62	36	java/lang/Throwable
    //   4	29	40	java/lang/Throwable
  }
}
