package com.google.android.gms.package_7;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

final class Cache
{
  Cache() {}
  
  private final Buffer get(Context paramContext, String paramString)
    throws DigesterLoadingException
  {
    try
    {
      Buffer localBuffer1 = load(paramContext, paramString);
      if (localBuffer1 != null)
      {
        put(paramContext, paramString, localBuffer1);
        return localBuffer1;
      }
      localBuffer1 = null;
    }
    catch (DigesterLoadingException localDigesterLoadingException1) {}
    try
    {
      Buffer localBuffer2 = get(paramContext.getSharedPreferences("com.google.android.gms.appid", 0), paramString);
      if (localBuffer2 != null)
      {
        write(paramContext, paramString, localBuffer2);
        return localBuffer2;
      }
    }
    catch (DigesterLoadingException localDigesterLoadingException2)
    {
      if (localDigesterLoadingException2 == null) {
        return null;
      }
      throw localDigesterLoadingException2;
    }
  }
  
  private static Buffer get(SharedPreferences paramSharedPreferences, String paramString)
    throws DigesterLoadingException
  {
    String str1 = paramSharedPreferences.getString(zzak.getValue(paramString, "|P|"), null);
    String str2 = paramSharedPreferences.getString(zzak.getValue(paramString, "|K|"), null);
    if (str1 != null)
    {
      if (str2 == null) {
        return null;
      }
      return new Buffer(load(str1, str2), getId(paramSharedPreferences, paramString));
    }
    return null;
  }
  
  private static long getId(SharedPreferences paramSharedPreferences, String paramString)
  {
    paramSharedPreferences = paramSharedPreferences.getString(zzak.getValue(paramString, "cre"), null);
    if (paramSharedPreferences != null) {}
    try
    {
      long l = Long.parseLong(paramSharedPreferences);
      return l;
    }
    catch (NumberFormatException paramSharedPreferences)
    {
      for (;;) {}
    }
    return 0L;
  }
  
  static void initialize(Context paramContext)
  {
    paramContext = listFiles(paramContext).listFiles();
    int j = paramContext.length;
    int i = 0;
    while (i < j)
    {
      Object localObject = paramContext[i];
      if (localObject.getName().startsWith("com.google.InstanceId")) {
        localObject.delete();
      }
      i += 1;
    }
  }
  
  static void initialize(Context paramContext, String paramString)
  {
    paramContext = read(paramContext, paramString);
    if (paramContext.exists()) {
      paramContext.delete();
    }
  }
  
  private static File listFiles(Context paramContext)
  {
    File localFile = ContextCompat.getNoBackupFilesDir(paramContext);
    if ((localFile != null) && (localFile.isDirectory())) {
      return localFile;
    }
    Log.w("InstanceID", "noBackupFilesDir doesn't exist, using regular files directory instead");
    return paramContext.getFilesDir();
  }
  
  private final Buffer load(Context paramContext, String paramString)
    throws DigesterLoadingException
  {
    paramContext = read(paramContext, paramString);
    if (!paramContext.exists()) {
      return null;
    }
    try
    {
      paramString = load(paramContext);
      return paramString;
    }
    catch (IOException paramString)
    {
      StringBuilder localStringBuilder;
      if (Log.isLoggable("InstanceID", 3))
      {
        paramString = String.valueOf(paramString);
        localStringBuilder = new StringBuilder(String.valueOf(paramString).length() + 40);
        localStringBuilder.append("Failed to read key from file, retrying: ");
        localStringBuilder.append(paramString);
        Log.d("InstanceID", localStringBuilder.toString());
      }
      try
      {
        paramContext = load(paramContext);
        return paramContext;
      }
      catch (IOException paramContext)
      {
        paramString = String.valueOf(paramContext);
        localStringBuilder = new StringBuilder(String.valueOf(paramString).length() + 45);
        localStringBuilder.append("IID file exists, but failed to read from it: ");
        localStringBuilder.append(paramString);
        Log.w("InstanceID", localStringBuilder.toString());
        throw new DigesterLoadingException(paramContext);
      }
    }
  }
  
  /* Error */
  private static Buffer load(File paramFile)
    throws DigesterLoadingException, IOException
  {
    // Byte code:
    //   0: new 185	java/io/FileInputStream
    //   3: dup
    //   4: aload_0
    //   5: invokespecial 188	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   8: astore_0
    //   9: new 190	java/util/Properties
    //   12: dup
    //   13: invokespecial 191	java/util/Properties:<init>	()V
    //   16: astore_3
    //   17: aload_3
    //   18: aload_0
    //   19: invokevirtual 194	java/util/Properties:load	(Ljava/io/InputStream;)V
    //   22: aload_3
    //   23: ldc -60
    //   25: invokevirtual 200	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   28: astore 4
    //   30: aload_3
    //   31: ldc -54
    //   33: invokevirtual 200	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   36: astore 5
    //   38: aload 4
    //   40: ifnull +58 -> 98
    //   43: aload 5
    //   45: ifnonnull +6 -> 51
    //   48: goto +50 -> 98
    //   51: aload 4
    //   53: aload 5
    //   55: invokestatic 70	com/google/android/gms/package_7/Cache:load	(Ljava/lang/String;Ljava/lang/String;)Ljava/security/KeyPair;
    //   58: astore 4
    //   60: aload_3
    //   61: ldc 81
    //   63: invokevirtual 200	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   66: invokestatic 87	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   69: lstore_1
    //   70: new 67	com/google/android/gms/package_7/Buffer
    //   73: dup
    //   74: aload 4
    //   76: lload_1
    //   77: invokespecial 77	com/google/android/gms/package_7/Buffer:<init>	(Ljava/security/KeyPair;J)V
    //   80: astore_3
    //   81: aconst_null
    //   82: aload_0
    //   83: invokestatic 205	com/google/android/gms/package_7/Cache:load	(Ljava/lang/Throwable;Ljava/io/FileInputStream;)V
    //   86: aload_3
    //   87: areturn
    //   88: astore_3
    //   89: new 28	com/google/android/gms/package_7/DigesterLoadingException
    //   92: dup
    //   93: aload_3
    //   94: invokespecial 183	com/google/android/gms/package_7/DigesterLoadingException:<init>	(Ljava/lang/Exception;)V
    //   97: athrow
    //   98: aconst_null
    //   99: aload_0
    //   100: invokestatic 205	com/google/android/gms/package_7/Cache:load	(Ljava/lang/Throwable;Ljava/io/FileInputStream;)V
    //   103: aconst_null
    //   104: areturn
    //   105: astore_3
    //   106: goto +8 -> 114
    //   109: astore 4
    //   111: aload 4
    //   113: athrow
    //   114: aload 4
    //   116: aload_0
    //   117: invokestatic 205	com/google/android/gms/package_7/Cache:load	(Ljava/lang/Throwable;Ljava/io/FileInputStream;)V
    //   120: aload_3
    //   121: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	122	0	paramFile	File
    //   69	8	1	l	long
    //   16	71	3	localObject1	Object
    //   88	6	3	localNumberFormatException	NumberFormatException
    //   105	16	3	localThrowable1	Throwable
    //   28	47	4	localObject2	Object
    //   109	6	4	localThrowable2	Throwable
    //   36	18	5	str	String
    // Exception table:
    //   from	to	target	type
    //   60	70	88	java/lang/NumberFormatException
    //   111	114	105	java/lang/Throwable
    //   9	38	109	java/lang/Throwable
    //   51	60	109	java/lang/Throwable
    //   60	70	109	java/lang/Throwable
    //   70	81	109	java/lang/Throwable
    //   89	98	109	java/lang/Throwable
  }
  
  /* Error */
  private static java.security.KeyPair load(String paramString1, String paramString2)
    throws DigesterLoadingException
  {
    // Byte code:
    //   0: aload_0
    //   1: bipush 8
    //   3: invokestatic 217	android/util/Base64:decode	(Ljava/lang/String;I)[B
    //   6: astore_0
    //   7: aload_1
    //   8: bipush 8
    //   10: invokestatic 217	android/util/Base64:decode	(Ljava/lang/String;I)[B
    //   13: astore_1
    //   14: ldc -37
    //   16: invokestatic 225	java/security/KeyFactory:getInstance	(Ljava/lang/String;)Ljava/security/KeyFactory;
    //   19: astore_2
    //   20: aload_2
    //   21: new 227	java/security/spec/X509EncodedKeySpec
    //   24: dup
    //   25: aload_0
    //   26: invokespecial 230	java/security/spec/X509EncodedKeySpec:<init>	([B)V
    //   29: invokevirtual 234	java/security/KeyFactory:generatePublic	(Ljava/security/spec/KeySpec;)Ljava/security/PublicKey;
    //   32: astore_0
    //   33: aload_2
    //   34: new 236	java/security/spec/PKCS8EncodedKeySpec
    //   37: dup
    //   38: aload_1
    //   39: invokespecial 237	java/security/spec/PKCS8EncodedKeySpec:<init>	([B)V
    //   42: invokevirtual 241	java/security/KeyFactory:generatePrivate	(Ljava/security/spec/KeySpec;)Ljava/security/PrivateKey;
    //   45: astore_1
    //   46: new 243	java/security/KeyPair
    //   49: dup
    //   50: aload_0
    //   51: aload_1
    //   52: invokespecial 246	java/security/KeyPair:<init>	(Ljava/security/PublicKey;Ljava/security/PrivateKey;)V
    //   55: astore_0
    //   56: aload_0
    //   57: areturn
    //   58: astore_0
    //   59: aload_0
    //   60: invokestatic 157	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   63: astore_1
    //   64: new 159	java/lang/StringBuilder
    //   67: dup
    //   68: aload_1
    //   69: invokestatic 157	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   72: invokevirtual 163	java/lang/String:length	()I
    //   75: bipush 19
    //   77: iadd
    //   78: invokespecial 166	java/lang/StringBuilder:<init>	(I)V
    //   81: astore_2
    //   82: aload_2
    //   83: ldc -8
    //   85: invokevirtual 172	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   88: pop
    //   89: aload_2
    //   90: aload_1
    //   91: invokevirtual 172	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   94: pop
    //   95: ldc -124
    //   97: aload_2
    //   98: invokevirtual 175	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   101: invokestatic 140	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   104: pop
    //   105: new 28	com/google/android/gms/package_7/DigesterLoadingException
    //   108: dup
    //   109: aload_0
    //   110: checkcast 250	java/lang/Exception
    //   113: invokespecial 183	com/google/android/gms/package_7/DigesterLoadingException:<init>	(Ljava/lang/Exception;)V
    //   116: athrow
    //   117: astore_0
    //   118: new 28	com/google/android/gms/package_7/DigesterLoadingException
    //   121: dup
    //   122: aload_0
    //   123: invokespecial 183	com/google/android/gms/package_7/DigesterLoadingException:<init>	(Ljava/lang/Exception;)V
    //   126: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	127	0	paramString1	String
    //   0	127	1	paramString2	String
    //   19	79	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   14	20	58	java/security/spec/InvalidKeySpecException
    //   14	20	58	java/security/NoSuchAlgorithmException
    //   20	33	58	java/security/spec/InvalidKeySpecException
    //   20	33	58	java/security/NoSuchAlgorithmException
    //   33	46	58	java/security/spec/InvalidKeySpecException
    //   33	46	58	java/security/NoSuchAlgorithmException
    //   46	56	58	java/security/spec/InvalidKeySpecException
    //   46	56	58	java/security/NoSuchAlgorithmException
    //   0	14	117	java/lang/IllegalArgumentException
  }
  
  private final void put(Context paramContext, String paramString, Buffer paramBuffer)
  {
    paramContext = paramContext.getSharedPreferences("com.google.android.gms.appid", 0);
    try
    {
      boolean bool = paramBuffer.equals(get(paramContext, paramString));
      if (bool) {
        return;
      }
    }
    catch (DigesterLoadingException localDigesterLoadingException)
    {
      for (;;) {}
    }
    if (Log.isLoggable("InstanceID", 3)) {
      Log.d("InstanceID", "Writing key to shared preferences");
    }
    paramContext = paramContext.edit();
    paramContext.putString(zzak.getValue(paramString, "|P|"), Buffer.encode(paramBuffer));
    paramContext.putString(zzak.getValue(paramString, "|K|"), Buffer.toString(paramBuffer));
    paramContext.putString(zzak.getValue(paramString, "cre"), String.valueOf(Buffer.indexOf(paramBuffer)));
    paramContext.commit();
  }
  
  private static File read(Context paramContext, String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      paramString = "com.google.InstanceId.properties";
    }
    try
    {
      paramString = Base64.encodeToString(paramString.getBytes("UTF-8"), 11);
      int i = String.valueOf(paramString).length();
      StringBuilder localStringBuilder = new StringBuilder(i + 33);
      localStringBuilder.append("com.google.InstanceId_");
      localStringBuilder.append(paramString);
      localStringBuilder.append(".properties");
      paramString = localStringBuilder.toString();
      return new File(listFiles(paramContext), paramString);
    }
    catch (UnsupportedEncodingException paramContext)
    {
      throw new AssertionError(paramContext);
    }
  }
  
  /* Error */
  private static void write(Context paramContext, String paramString, Buffer paramBuffer)
  {
    // Byte code:
    //   0: ldc -124
    //   2: iconst_3
    //   3: invokestatic 153	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   6: istore_3
    //   7: iload_3
    //   8: ifeq +12 -> 20
    //   11: ldc -124
    //   13: ldc_w 317
    //   16: invokestatic 178	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   19: pop
    //   20: aload_0
    //   21: aload_1
    //   22: invokestatic 119	com/google/android/gms/package_7/Cache:read	(Landroid/content/Context;Ljava/lang/String;)Ljava/io/File;
    //   25: astore_1
    //   26: aload_1
    //   27: invokevirtual 320	java/io/File:createNewFile	()Z
    //   30: pop
    //   31: new 190	java/util/Properties
    //   34: dup
    //   35: invokespecial 191	java/util/Properties:<init>	()V
    //   38: astore_0
    //   39: aload_0
    //   40: ldc -60
    //   42: aload_2
    //   43: invokestatic 265	com/google/android/gms/package_7/Buffer:encode	(Lcom/google/android/gms/package_7/Buffer;)Ljava/lang/String;
    //   46: invokevirtual 324	java/util/Properties:setProperty	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;
    //   49: pop
    //   50: aload_0
    //   51: ldc -54
    //   53: aload_2
    //   54: invokestatic 273	com/google/android/gms/package_7/Buffer:toString	(Lcom/google/android/gms/package_7/Buffer;)Ljava/lang/String;
    //   57: invokevirtual 324	java/util/Properties:setProperty	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;
    //   60: pop
    //   61: aload_0
    //   62: ldc 81
    //   64: aload_2
    //   65: invokestatic 277	com/google/android/gms/package_7/Buffer:indexOf	(Lcom/google/android/gms/package_7/Buffer;)J
    //   68: invokestatic 280	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   71: invokevirtual 324	java/util/Properties:setProperty	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;
    //   74: pop
    //   75: new 15	java/io/FileOutputStream
    //   78: dup
    //   79: aload_1
    //   80: invokespecial 325	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   83: astore_1
    //   84: aload_0
    //   85: aload_1
    //   86: aconst_null
    //   87: invokevirtual 329	java/util/Properties:store	(Ljava/io/OutputStream;Ljava/lang/String;)V
    //   90: aconst_null
    //   91: aload_1
    //   92: invokestatic 331	com/google/android/gms/package_7/Cache:generate	(Ljava/lang/Throwable;Ljava/io/FileOutputStream;)V
    //   95: return
    //   96: astore_0
    //   97: goto +6 -> 103
    //   100: astore_2
    //   101: aload_2
    //   102: athrow
    //   103: aload_2
    //   104: aload_1
    //   105: invokestatic 331	com/google/android/gms/package_7/Cache:generate	(Ljava/lang/Throwable;Ljava/io/FileOutputStream;)V
    //   108: aload_0
    //   109: athrow
    //   110: astore_0
    //   111: aload_0
    //   112: invokestatic 157	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   115: astore_0
    //   116: new 159	java/lang/StringBuilder
    //   119: dup
    //   120: aload_0
    //   121: invokestatic 157	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   124: invokevirtual 163	java/lang/String:length	()I
    //   127: bipush 21
    //   129: iadd
    //   130: invokespecial 166	java/lang/StringBuilder:<init>	(I)V
    //   133: astore_1
    //   134: aload_1
    //   135: ldc_w 333
    //   138: invokevirtual 172	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   141: pop
    //   142: aload_1
    //   143: aload_0
    //   144: invokevirtual 172	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   147: pop
    //   148: ldc -124
    //   150: aload_1
    //   151: invokevirtual 175	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   154: invokestatic 140	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   157: pop
    //   158: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	159	0	paramContext	Context
    //   0	159	1	paramString	String
    //   0	159	2	paramBuffer	Buffer
    //   6	2	3	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   101	103	96	java/lang/Throwable
    //   84	90	100	java/lang/Throwable
    //   0	7	110	java/io/IOException
    //   11	20	110	java/io/IOException
    //   20	31	110	java/io/IOException
    //   31	75	110	java/io/IOException
    //   75	84	110	java/io/IOException
    //   90	95	110	java/io/IOException
    //   103	110	110	java/io/IOException
  }
  
  final Buffer save(Context paramContext, String paramString)
    throws DigesterLoadingException
  {
    Buffer localBuffer = get(paramContext, paramString);
    if (localBuffer != null) {
      return localBuffer;
    }
    return write(paramContext, paramString);
  }
  
  final Buffer write(Context paramContext, String paramString)
  {
    Buffer localBuffer1 = new Buffer(CharacterReference.decode(), System.currentTimeMillis());
    try
    {
      Buffer localBuffer2 = get(paramContext, paramString);
      if (localBuffer2 != null)
      {
        boolean bool = Log.isLoggable("InstanceID", 3);
        if (!bool) {
          break label92;
        }
        Log.d("InstanceID", "Loaded key after generating new one, using loaded one");
        return localBuffer2;
      }
    }
    catch (DigesterLoadingException localDigesterLoadingException)
    {
      for (;;) {}
      return localDigesterLoadingException;
    }
    if (Log.isLoggable("InstanceID", 3)) {
      Log.d("InstanceID", "Generated new key");
    }
    write(paramContext, paramString, localBuffer1);
    put(paramContext, paramString, localBuffer1);
    return localBuffer1;
  }
}
