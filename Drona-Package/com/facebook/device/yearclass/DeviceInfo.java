package com.facebook.device.yearclass;

import android.os.Build.VERSION;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;

public class DeviceInfo
{
  private static final FileFilter CPU_FILTER = new FileFilter()
  {
    public boolean accept(File paramAnonymousFile)
    {
      paramAnonymousFile = paramAnonymousFile.getName();
      if (paramAnonymousFile.startsWith("cpu"))
      {
        int i = 3;
        while (i < paramAnonymousFile.length())
        {
          if (!Character.isDigit(paramAnonymousFile.charAt(i))) {
            return false;
          }
          i += 1;
        }
        return true;
      }
      return false;
    }
  };
  public static final int DEVICEINFO_UNKNOWN = -1;
  
  public DeviceInfo() {}
  
  private static int extractValue(byte[] paramArrayOfByte, int paramInt)
  {
    while ((paramInt < paramArrayOfByte.length) && (paramArrayOfByte[paramInt] != 10))
    {
      if (Character.isDigit(paramArrayOfByte[paramInt]))
      {
        int i = paramInt + 1;
        while ((i < paramArrayOfByte.length) && (Character.isDigit(paramArrayOfByte[i]))) {
          i += 1;
        }
        return Integer.parseInt(new String(paramArrayOfByte, 0, paramInt, i - paramInt));
      }
      paramInt += 1;
    }
    return -1;
  }
  
  /* Error */
  public static int getCPUMaxFreqKHz()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: iconst_m1
    //   3: istore_0
    //   4: invokestatic 50	com/facebook/device/yearclass/DeviceInfo:getNumberOfCPUCores	()I
    //   7: istore_1
    //   8: iload_2
    //   9: iload_1
    //   10: if_icmpge +206 -> 216
    //   13: new 52	java/lang/StringBuilder
    //   16: dup
    //   17: invokespecial 53	java/lang/StringBuilder:<init>	()V
    //   20: astore 5
    //   22: aload 5
    //   24: ldc 55
    //   26: invokevirtual 59	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   29: pop
    //   30: aload 5
    //   32: iload_2
    //   33: invokevirtual 62	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   36: pop
    //   37: aload 5
    //   39: ldc 64
    //   41: invokevirtual 59	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   44: pop
    //   45: aload 5
    //   47: invokevirtual 68	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   50: astore 5
    //   52: new 70	java/io/File
    //   55: dup
    //   56: aload 5
    //   58: invokespecial 73	java/io/File:<init>	(Ljava/lang/String;)V
    //   61: astore 5
    //   63: aload 5
    //   65: invokevirtual 77	java/io/File:exists	()Z
    //   68: istore 4
    //   70: iload_0
    //   71: istore_1
    //   72: iload 4
    //   74: ifeq +133 -> 207
    //   77: aload 5
    //   79: invokevirtual 80	java/io/File:canRead	()Z
    //   82: istore 4
    //   84: iload_0
    //   85: istore_1
    //   86: iload 4
    //   88: ifeq +119 -> 207
    //   91: sipush 128
    //   94: newarray byte
    //   96: astore 6
    //   98: new 82	java/io/FileInputStream
    //   101: dup
    //   102: aload 5
    //   104: invokespecial 85	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   107: astore 5
    //   109: aload 5
    //   111: aload 6
    //   113: invokevirtual 89	java/io/FileInputStream:read	([B)I
    //   116: pop
    //   117: iconst_0
    //   118: istore_1
    //   119: aload 6
    //   121: iload_1
    //   122: baload
    //   123: istore_3
    //   124: iload_3
    //   125: invokestatic 28	java/lang/Character:isDigit	(I)Z
    //   128: istore 4
    //   130: iload 4
    //   132: ifeq +19 -> 151
    //   135: aload 6
    //   137: arraylength
    //   138: istore_3
    //   139: iload_1
    //   140: iload_3
    //   141: if_icmpge +10 -> 151
    //   144: iload_1
    //   145: iconst_1
    //   146: iadd
    //   147: istore_1
    //   148: goto -29 -> 119
    //   151: new 30	java/lang/String
    //   154: dup
    //   155: aload 6
    //   157: iconst_0
    //   158: iload_1
    //   159: invokespecial 92	java/lang/String:<init>	([BII)V
    //   162: invokestatic 39	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   165: invokestatic 96	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   168: astore 6
    //   170: aload 6
    //   172: invokevirtual 99	java/lang/Integer:intValue	()I
    //   175: istore_3
    //   176: iload_0
    //   177: istore_1
    //   178: iload_3
    //   179: iload_0
    //   180: if_icmple +9 -> 189
    //   183: aload 6
    //   185: invokevirtual 99	java/lang/Integer:intValue	()I
    //   188: istore_1
    //   189: aload 5
    //   191: invokevirtual 102	java/io/FileInputStream:close	()V
    //   194: goto +13 -> 207
    //   197: astore 6
    //   199: aload 5
    //   201: invokevirtual 102	java/io/FileInputStream:close	()V
    //   204: aload 6
    //   206: athrow
    //   207: iload_2
    //   208: iconst_1
    //   209: iadd
    //   210: istore_2
    //   211: iload_1
    //   212: istore_0
    //   213: goto -209 -> 4
    //   216: iload_0
    //   217: iconst_m1
    //   218: if_icmpne +55 -> 273
    //   221: new 82	java/io/FileInputStream
    //   224: dup
    //   225: ldc 104
    //   227: invokespecial 105	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   230: astore 5
    //   232: ldc 107
    //   234: aload 5
    //   236: invokestatic 111	com/facebook/device/yearclass/DeviceInfo:parseFileForValue	(Ljava/lang/String;Ljava/io/FileInputStream;)I
    //   239: istore_1
    //   240: iload_1
    //   241: sipush 1000
    //   244: imul
    //   245: istore_1
    //   246: iload_1
    //   247: iload_0
    //   248: if_icmple +8 -> 256
    //   251: iload_1
    //   252: istore_0
    //   253: goto +3 -> 256
    //   256: aload 5
    //   258: invokevirtual 102	java/io/FileInputStream:close	()V
    //   261: iload_0
    //   262: ireturn
    //   263: astore 6
    //   265: aload 5
    //   267: invokevirtual 102	java/io/FileInputStream:close	()V
    //   270: aload 6
    //   272: athrow
    //   273: iload_0
    //   274: ireturn
    //   275: astore 5
    //   277: iconst_m1
    //   278: ireturn
    //   279: astore 6
    //   281: iload_0
    //   282: istore_1
    //   283: goto -94 -> 189
    //   286: astore 5
    //   288: iconst_m1
    //   289: ireturn
    //   290: astore 5
    //   292: iconst_m1
    //   293: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   3	279	0	i	int
    //   7	276	1	j	int
    //   1	210	2	k	int
    //   123	58	3	m	int
    //   68	63	4	bool	boolean
    //   20	246	5	localObject1	Object
    //   275	1	5	localIOException1	IOException
    //   286	1	5	localIOException2	IOException
    //   290	1	5	localIOException3	IOException
    //   96	88	6	localObject2	Object
    //   197	8	6	localThrowable1	Throwable
    //   263	8	6	localThrowable2	Throwable
    //   279	1	6	localNumberFormatException	NumberFormatException
    // Exception table:
    //   from	to	target	type
    //   109	117	197	java/lang/Throwable
    //   124	130	197	java/lang/Throwable
    //   135	139	197	java/lang/Throwable
    //   151	176	197	java/lang/Throwable
    //   183	189	197	java/lang/Throwable
    //   232	240	263	java/lang/Throwable
    //   4	8	275	java/io/IOException
    //   13	52	275	java/io/IOException
    //   52	70	275	java/io/IOException
    //   77	84	275	java/io/IOException
    //   98	109	275	java/io/IOException
    //   109	117	279	java/lang/NumberFormatException
    //   124	130	279	java/lang/NumberFormatException
    //   151	176	279	java/lang/NumberFormatException
    //   183	189	279	java/lang/NumberFormatException
    //   189	194	286	java/io/IOException
    //   199	207	286	java/io/IOException
    //   221	232	286	java/io/IOException
    //   256	261	290	java/io/IOException
    //   265	273	290	java/io/IOException
  }
  
  private static int getCoresFromCPUFileList()
  {
    return new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
  }
  
  /* Error */
  private static int getCoresFromFileInfo(String paramString)
  {
    // Byte code:
    //   0: new 82	java/io/FileInputStream
    //   3: dup
    //   4: aload_0
    //   5: invokespecial 105	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   8: astore_0
    //   9: new 121	java/io/BufferedReader
    //   12: dup
    //   13: new 123	java/io/InputStreamReader
    //   16: dup
    //   17: aload_0
    //   18: invokespecial 126	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   21: invokespecial 129	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   24: astore_2
    //   25: aload_2
    //   26: invokevirtual 132	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   29: astore_3
    //   30: aload_2
    //   31: invokevirtual 133	java/io/BufferedReader:close	()V
    //   34: aload_3
    //   35: invokestatic 136	com/facebook/device/yearclass/DeviceInfo:getCoresFromFileString	(Ljava/lang/String;)I
    //   38: istore_1
    //   39: aload_0
    //   40: invokevirtual 139	java/io/InputStream:close	()V
    //   43: iload_1
    //   44: ireturn
    //   45: astore_3
    //   46: aload_0
    //   47: astore_2
    //   48: aload_3
    //   49: astore_0
    //   50: goto +9 -> 59
    //   53: goto +18 -> 71
    //   56: astore_0
    //   57: aconst_null
    //   58: astore_2
    //   59: aload_2
    //   60: ifnull +7 -> 67
    //   63: aload_2
    //   64: invokevirtual 139	java/io/InputStream:close	()V
    //   67: aload_0
    //   68: athrow
    //   69: aconst_null
    //   70: astore_0
    //   71: aload_0
    //   72: ifnull +25 -> 97
    //   75: aload_0
    //   76: invokevirtual 139	java/io/InputStream:close	()V
    //   79: iconst_m1
    //   80: ireturn
    //   81: astore_0
    //   82: goto -13 -> 69
    //   85: astore_2
    //   86: goto -33 -> 53
    //   89: astore_0
    //   90: iload_1
    //   91: ireturn
    //   92: astore_2
    //   93: goto -26 -> 67
    //   96: astore_0
    //   97: iconst_m1
    //   98: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	99	0	paramString	String
    //   38	53	1	i	int
    //   24	40	2	localObject	Object
    //   85	1	2	localIOException1	IOException
    //   92	1	2	localIOException2	IOException
    //   29	6	3	str	String
    //   45	4	3	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   9	39	45	java/lang/Throwable
    //   0	9	56	java/lang/Throwable
    //   0	9	81	java/io/IOException
    //   9	39	85	java/io/IOException
    //   39	43	89	java/io/IOException
    //   63	67	92	java/io/IOException
    //   75	79	96	java/io/IOException
  }
  
  static int getCoresFromFileString(String paramString)
  {
    if ((paramString != null) && (paramString.matches("0-[\\d]+$"))) {
      return Integer.valueOf(paramString.substring(2)).intValue() + 1;
    }
    return -1;
  }
  
  public static int getNumberOfCPUCores()
  {
    if (Build.VERSION.SDK_INT <= 10) {
      return 1;
    }
    try
    {
      int j = getCoresFromFileInfo("/sys/devices/system/cpu/possible");
      int i = j;
      if (j == -1) {
        i = getCoresFromFileInfo("/sys/devices/system/cpu/present");
      }
      j = i;
      if (i == -1) {
        j = getCoresFromCPUFileList();
      }
      return j;
    }
    catch (SecurityException localSecurityException)
    {
      return -1;
    }
    catch (NullPointerException localNullPointerException) {}
    return -1;
  }
  
  /* Error */
  public static long getTotalMemory(android.content.Context paramContext)
  {
    // Byte code:
    //   0: getstatic 161	android/os/Build$VERSION:SDK_INT	I
    //   3: bipush 16
    //   5: if_icmplt +32 -> 37
    //   8: new 173	android/app/ActivityManager$MemoryInfo
    //   11: dup
    //   12: invokespecial 174	android/app/ActivityManager$MemoryInfo:<init>	()V
    //   15: astore 6
    //   17: aload_0
    //   18: ldc -80
    //   20: invokevirtual 182	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   23: checkcast 184	android/app/ActivityManager
    //   26: aload 6
    //   28: invokevirtual 188	android/app/ActivityManager:getMemoryInfo	(Landroid/app/ActivityManager$MemoryInfo;)V
    //   31: aload 6
    //   33: getfield 192	android/app/ActivityManager$MemoryInfo:totalMem	J
    //   36: lreturn
    //   37: ldc2_w 193
    //   40: lstore 4
    //   42: new 82	java/io/FileInputStream
    //   45: dup
    //   46: ldc -60
    //   48: invokespecial 105	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   51: astore_0
    //   52: ldc -58
    //   54: aload_0
    //   55: invokestatic 111	com/facebook/device/yearclass/DeviceInfo:parseFileForValue	(Ljava/lang/String;Ljava/io/FileInputStream;)I
    //   58: istore_1
    //   59: iload_1
    //   60: i2l
    //   61: ldc2_w 199
    //   64: lmul
    //   65: lstore 4
    //   67: lload 4
    //   69: lstore_2
    //   70: aload_0
    //   71: invokevirtual 102	java/io/FileInputStream:close	()V
    //   74: lload 4
    //   76: lreturn
    //   77: astore 6
    //   79: lload 4
    //   81: lstore_2
    //   82: aload_0
    //   83: invokevirtual 102	java/io/FileInputStream:close	()V
    //   86: lload 4
    //   88: lstore_2
    //   89: aload 6
    //   91: athrow
    //   92: astore_0
    //   93: ldc2_w 193
    //   96: lreturn
    //   97: astore_0
    //   98: lload_2
    //   99: lreturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	100	0	paramContext	android.content.Context
    //   58	2	1	i	int
    //   69	30	2	l1	long
    //   40	47	4	l2	long
    //   15	17	6	localMemoryInfo	android.app.ActivityManager.MemoryInfo
    //   77	13	6	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   52	59	77	java/lang/Throwable
    //   42	52	92	java/io/IOException
    //   70	74	97	java/io/IOException
    //   82	86	97	java/io/IOException
    //   89	92	97	java/io/IOException
  }
  
  private static int parseFileForValue(String paramString, FileInputStream paramFileInputStream)
  {
    byte[] arrayOfByte = new byte['?'];
    try
    {
      int m = paramFileInputStream.read(arrayOfByte);
      int k;
      for (int j = 0; j < m; j = k + 1) {
        if (arrayOfByte[j] != 10)
        {
          k = j;
          if (j != 0) {}
        }
        else
        {
          int i = j;
          if (arrayOfByte[j] == 10) {
            i = j + 1;
          }
          j = i;
          for (;;)
          {
            k = i;
            if (j >= m) {
              break;
            }
            k = j - i;
            int n = arrayOfByte[j];
            int i1 = paramString.charAt(k);
            if (n != i1)
            {
              k = i;
              break;
            }
            n = paramString.length();
            if (k == n - 1)
            {
              i = extractValue(arrayOfByte, j);
              return i;
            }
            j += 1;
          }
        }
      }
    }
    catch (IOException paramString)
    {
      for (;;) {}
    }
    catch (NumberFormatException paramString)
    {
      for (;;) {}
    }
    return -1;
  }
}
