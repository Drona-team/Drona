package com.facebook.soloader;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public final class SysUtil
{
  private static final byte APK_SIGNATURE_VERSION = 1;
  
  public SysUtil() {}
  
  static int copyBytes(RandomAccessFile paramRandomAccessFile, InputStream paramInputStream, int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    int i = 0;
    while (i < paramInt)
    {
      int j = paramInputStream.read(paramArrayOfByte, 0, Math.min(paramArrayOfByte.length, paramInt - i));
      if (j == -1) {
        break;
      }
      paramRandomAccessFile.write(paramArrayOfByte, 0, j);
      i += j;
    }
    return i;
  }
  
  public static void deleteOrThrow(File paramFile)
    throws IOException
  {
    if (paramFile.delete()) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("could not delete file ");
    localStringBuilder.append(paramFile);
    throw new IOException(localStringBuilder.toString());
  }
  
  public static void dumbDeleteRecursive(File paramFile)
    throws IOException
  {
    Object localObject;
    if (paramFile.isDirectory())
    {
      localObject = paramFile.listFiles();
      if (localObject == null) {
        return;
      }
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        dumbDeleteRecursive(localObject[i]);
        i += 1;
      }
    }
    if (!paramFile.delete())
    {
      if (!paramFile.exists()) {
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("could not delete: ");
      ((StringBuilder)localObject).append(paramFile);
      throw new IOException(((StringBuilder)localObject).toString());
    }
  }
  
  public static void fallocateIfSupported(FileDescriptor paramFileDescriptor, long paramLong)
    throws IOException
  {
    if (Build.VERSION.SDK_INT >= 21) {
      LollipopSysdeps.fallocateIfSupported(paramFileDescriptor, paramLong);
    }
  }
  
  public static int findAbiScore(String[] paramArrayOfString, String paramString)
  {
    int i = 0;
    while (i < paramArrayOfString.length)
    {
      if ((paramArrayOfString[i] != null) && (paramString.equals(paramArrayOfString[i]))) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  /* Error */
  static void fsyncRecursive(File paramFile)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 69	java/io/File:isDirectory	()Z
    //   4: ifeq +66 -> 70
    //   7: aload_0
    //   8: invokevirtual 73	java/io/File:listFiles	()[Ljava/io/File;
    //   11: astore_2
    //   12: aload_2
    //   13: ifnull +24 -> 37
    //   16: iconst_0
    //   17: istore_1
    //   18: iload_1
    //   19: aload_2
    //   20: arraylength
    //   21: if_icmpge +118 -> 139
    //   24: aload_2
    //   25: iload_1
    //   26: aaload
    //   27: invokestatic 103	com/facebook/soloader/SysUtil:fsyncRecursive	(Ljava/io/File;)V
    //   30: iload_1
    //   31: iconst_1
    //   32: iadd
    //   33: istore_1
    //   34: goto -16 -> 18
    //   37: new 48	java/lang/StringBuilder
    //   40: dup
    //   41: invokespecial 49	java/lang/StringBuilder:<init>	()V
    //   44: astore_2
    //   45: aload_2
    //   46: ldc 105
    //   48: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: pop
    //   52: aload_2
    //   53: aload_0
    //   54: invokevirtual 58	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   57: pop
    //   58: new 19	java/io/IOException
    //   61: dup
    //   62: aload_2
    //   63: invokevirtual 62	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: invokespecial 65	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   69: athrow
    //   70: aload_0
    //   71: invokevirtual 108	java/io/File:getPath	()Ljava/lang/String;
    //   74: ldc 110
    //   76: invokevirtual 114	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   79: ifeq +4 -> 83
    //   82: return
    //   83: new 33	java/io/RandomAccessFile
    //   86: dup
    //   87: aload_0
    //   88: ldc 116
    //   90: invokespecial 119	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   93: astore_3
    //   94: aload_3
    //   95: invokevirtual 123	java/io/RandomAccessFile:getFD	()Ljava/io/FileDescriptor;
    //   98: invokevirtual 128	java/io/FileDescriptor:sync	()V
    //   101: aload_3
    //   102: invokevirtual 131	java/io/RandomAccessFile:close	()V
    //   105: return
    //   106: astore_0
    //   107: goto +6 -> 113
    //   110: astore_2
    //   111: aload_2
    //   112: athrow
    //   113: aload_2
    //   114: ifnull +19 -> 133
    //   117: aload_3
    //   118: invokevirtual 131	java/io/RandomAccessFile:close	()V
    //   121: goto +16 -> 137
    //   124: astore_3
    //   125: aload_2
    //   126: aload_3
    //   127: invokevirtual 135	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   130: goto +7 -> 137
    //   133: aload_3
    //   134: invokevirtual 131	java/io/RandomAccessFile:close	()V
    //   137: aload_0
    //   138: athrow
    //   139: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	140	0	paramFile	File
    //   17	17	1	i	int
    //   11	52	2	localObject	Object
    //   110	16	2	localThrowable1	Throwable
    //   93	25	3	localRandomAccessFile	RandomAccessFile
    //   124	10	3	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   111	113	106	java/lang/Throwable
    //   94	101	110	java/lang/Throwable
    //   117	121	124	java/lang/Throwable
  }
  
  public static int getAppVersionCode(Context paramContext)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    if (localPackageManager != null) {}
    try
    {
      paramContext = localPackageManager.getPackageInfo(paramContext.getPackageName(), 0);
      int i = versionCode;
      return i;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      return 0;
    }
    catch (RuntimeException paramContext) {}
    return 0;
    return 0;
  }
  
  public static String[] getSupportedAbis()
  {
    if (Build.VERSION.SDK_INT < 21) {
      return new String[] { Build.CPU_ABI, Build.CPU_ABI2 };
    }
    return LollipopSysdeps.getSupportedAbis();
  }
  
  public static byte[] makeApkDepBlock(File paramFile, Context paramContext)
    throws IOException
  {
    File localFile = paramFile.getCanonicalFile();
    paramFile = Parcel.obtain();
    try
    {
      paramFile.writeByte((byte)1);
      paramFile.writeString(localFile.getPath());
      paramFile.writeLong(localFile.lastModified());
      paramFile.writeInt(getAppVersionCode(paramContext));
      paramContext = paramFile.marshall();
      paramFile.recycle();
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      paramFile.recycle();
      throw paramContext;
    }
  }
  
  static void mkdirOrThrow(File paramFile)
    throws IOException
  {
    if (!paramFile.mkdirs())
    {
      if (paramFile.isDirectory()) {
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("cannot mkdir: ");
      localStringBuilder.append(paramFile);
      throw new IOException(localStringBuilder.toString());
    }
  }
  
  @TargetApi(21)
  @DoNotOptimize
  private static final class LollipopSysdeps
  {
    private LollipopSysdeps() {}
    
    public static void fallocateIfSupported(FileDescriptor paramFileDescriptor, long paramLong)
      throws IOException
    {
      try
      {
        Os.posix_fallocate(paramFileDescriptor, 0L, paramLong);
        return;
      }
      catch (ErrnoException paramFileDescriptor)
      {
        if ((errno != OsConstants.EOPNOTSUPP) && (errno != OsConstants.ENOSYS))
        {
          if (errno == OsConstants.EINVAL) {
            return;
          }
          throw new IOException(paramFileDescriptor.toString(), paramFileDescriptor);
        }
      }
    }
    
    public static String[] getSupportedAbis()
    {
      return Build.SUPPORTED_ABIS;
    }
  }
}
