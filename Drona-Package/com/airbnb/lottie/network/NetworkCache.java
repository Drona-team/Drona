package com.airbnb.lottie.network;

import android.content.Context;
import androidx.core.util.Pair;
import com.airbnb.lottie.utils.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

class NetworkCache
{
  private final Context appContext;
  private final String name;
  
  NetworkCache(Context paramContext, String paramString)
  {
    appContext = paramContext.getApplicationContext();
    name = paramString;
  }
  
  private static String filenameForUrl(String paramString, FileExtension paramFileExtension, boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("lottie_cache_");
    localStringBuilder.append(paramString.replaceAll("\\W+", ""));
    if (paramBoolean) {
      paramString = paramFileExtension.tempExtension();
    } else {
      paramString = extension;
    }
    localStringBuilder.append(paramString);
    return localStringBuilder.toString();
  }
  
  private File getCachedFile(String paramString)
    throws FileNotFoundException
  {
    File localFile = new File(appContext.getCacheDir(), filenameForUrl(paramString, FileExtension.JSON, false));
    if (localFile.exists()) {
      return localFile;
    }
    paramString = new File(appContext.getCacheDir(), filenameForUrl(paramString, FileExtension.HTML, false));
    if (paramString.exists()) {
      return paramString;
    }
    return null;
  }
  
  Pair fetch()
  {
    Object localObject = name;
    try
    {
      File localFile = getCachedFile((String)localObject);
      if (localFile == null) {
        return null;
      }
      FileInputStream localFileInputStream;
      StringBuilder localStringBuilder;
      return null;
    }
    catch (FileNotFoundException localFileNotFoundException1)
    {
      try
      {
        localFileInputStream = new FileInputStream(localFile);
        if (localFile.getAbsolutePath().endsWith(".zip")) {
          localObject = FileExtension.HTML;
        } else {
          localObject = FileExtension.JSON;
        }
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("Cache hit for ");
        localStringBuilder.append(name);
        localStringBuilder.append(" at ");
        localStringBuilder.append(localFile.getAbsolutePath());
        Logger.debug(localStringBuilder.toString());
        return new Pair(localObject, localFileInputStream);
      }
      catch (FileNotFoundException localFileNotFoundException2) {}
      localFileNotFoundException1 = localFileNotFoundException1;
      return null;
    }
  }
  
  void renameTempFile(FileExtension paramFileExtension)
  {
    paramFileExtension = filenameForUrl(name, paramFileExtension, true);
    paramFileExtension = new File(appContext.getCacheDir(), paramFileExtension);
    File localFile = new File(paramFileExtension.getAbsolutePath().replace(".temp", ""));
    boolean bool = paramFileExtension.renameTo(localFile);
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Copying temp file to real file (");
    localStringBuilder.append(localFile);
    localStringBuilder.append(")");
    Logger.debug(localStringBuilder.toString());
    if (!bool)
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unable to rename cache file ");
      localStringBuilder.append(paramFileExtension.getAbsolutePath());
      localStringBuilder.append(" to ");
      localStringBuilder.append(localFile.getAbsolutePath());
      localStringBuilder.append(".");
      Logger.warning(localStringBuilder.toString());
    }
  }
  
  /* Error */
  File writeTempCacheFile(java.io.InputStream paramInputStream, FileExtension paramFileExtension)
    throws java.io.IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 23	com/airbnb/lottie/network/NetworkCache:name	Ljava/lang/String;
    //   4: aload_2
    //   5: iconst_1
    //   6: invokestatic 73	com/airbnb/lottie/network/NetworkCache:filenameForUrl	(Ljava/lang/String;Lcom/airbnb/lottie/network/FileExtension;Z)Ljava/lang/String;
    //   9: astore_2
    //   10: new 63	java/io/File
    //   13: dup
    //   14: aload_0
    //   15: getfield 21	com/airbnb/lottie/network/NetworkCache:appContext	Landroid/content/Context;
    //   18: invokevirtual 67	android/content/Context:getCacheDir	()Ljava/io/File;
    //   21: aload_2
    //   22: invokespecial 76	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   25: astore 4
    //   27: new 155	java/io/FileOutputStream
    //   30: dup
    //   31: aload 4
    //   33: invokespecial 156	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   36: astore_2
    //   37: sipush 1024
    //   40: newarray byte
    //   42: astore 5
    //   44: aload_1
    //   45: aload 5
    //   47: invokevirtual 162	java/io/InputStream:read	([B)I
    //   50: istore_3
    //   51: iload_3
    //   52: iconst_m1
    //   53: if_icmpeq +14 -> 67
    //   56: aload_2
    //   57: aload 5
    //   59: iconst_0
    //   60: iload_3
    //   61: invokevirtual 168	java/io/OutputStream:write	([BII)V
    //   64: goto -20 -> 44
    //   67: aload_2
    //   68: invokevirtual 171	java/io/OutputStream:flush	()V
    //   71: aload_2
    //   72: invokevirtual 174	java/io/OutputStream:close	()V
    //   75: aload_1
    //   76: invokevirtual 175	java/io/InputStream:close	()V
    //   79: aload 4
    //   81: areturn
    //   82: astore 4
    //   84: aload_2
    //   85: invokevirtual 174	java/io/OutputStream:close	()V
    //   88: aload 4
    //   90: athrow
    //   91: astore_2
    //   92: aload_1
    //   93: invokevirtual 175	java/io/InputStream:close	()V
    //   96: aload_2
    //   97: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	98	0	this	NetworkCache
    //   0	98	1	paramInputStream	java.io.InputStream
    //   0	98	2	paramFileExtension	FileExtension
    //   50	11	3	i	int
    //   25	55	4	localFile	File
    //   82	7	4	localThrowable	Throwable
    //   42	16	5	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   37	44	82	java/lang/Throwable
    //   44	51	82	java/lang/Throwable
    //   56	64	82	java/lang/Throwable
    //   67	71	82	java/lang/Throwable
    //   27	37	91	java/lang/Throwable
    //   71	75	91	java/lang/Throwable
    //   84	91	91	java/lang/Throwable
  }
}
