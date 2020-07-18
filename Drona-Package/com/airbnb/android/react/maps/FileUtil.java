package com.airbnb.android.react.maps;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import com.facebook.common.logging.FLog;
import java.io.InputStream;

public class FileUtil
  extends AsyncTask<String, Void, InputStream>
{
  private final String NAME = "FileUtil";
  private final String TEMP_FILE_SUFFIX = "temp";
  private Context context;
  private Exception exception;
  
  public FileUtil(Context paramContext)
  {
    context = paramContext;
  }
  
  /* Error */
  private InputStream getDownloadFileInputStream(Context paramContext, Uri paramUri)
    throws java.io.IOException
  {
    // Byte code:
    //   0: ldc 19
    //   2: ldc 23
    //   4: aload_1
    //   5: invokevirtual 40	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   8: invokevirtual 44	android/content/Context:getCacheDir	()Ljava/io/File;
    //   11: invokestatic 50	java/io/File:createTempFile	(Ljava/lang/String;Ljava/lang/String;Ljava/io/File;)Ljava/io/File;
    //   14: astore 4
    //   16: aload 4
    //   18: invokevirtual 53	java/io/File:deleteOnExit	()V
    //   21: new 55	java/net/URL
    //   24: dup
    //   25: aload_2
    //   26: invokevirtual 61	android/net/Uri:toString	()Ljava/lang/String;
    //   29: invokespecial 64	java/net/URL:<init>	(Ljava/lang/String;)V
    //   32: invokevirtual 68	java/net/URL:openStream	()Ljava/io/InputStream;
    //   35: astore_1
    //   36: aload_1
    //   37: invokestatic 74	java/nio/channels/Channels:newChannel	(Ljava/io/InputStream;)Ljava/nio/channels/ReadableByteChannel;
    //   40: astore_2
    //   41: new 76	java/io/FileOutputStream
    //   44: dup
    //   45: aload 4
    //   47: invokespecial 79	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   50: astore_3
    //   51: aload_3
    //   52: invokevirtual 83	java/io/FileOutputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   55: aload_2
    //   56: lconst_0
    //   57: ldc2_w 84
    //   60: invokevirtual 91	java/nio/channels/FileChannel:transferFrom	(Ljava/nio/channels/ReadableByteChannel;JJ)J
    //   63: pop2
    //   64: new 93	java/io/FileInputStream
    //   67: dup
    //   68: aload 4
    //   70: invokespecial 94	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   73: astore 4
    //   75: aload_3
    //   76: invokevirtual 97	java/io/FileOutputStream:close	()V
    //   79: aload_2
    //   80: invokeinterface 100 1 0
    //   85: aload_1
    //   86: invokevirtual 103	java/io/InputStream:close	()V
    //   89: aload 4
    //   91: areturn
    //   92: astore 4
    //   94: aload_3
    //   95: invokevirtual 97	java/io/FileOutputStream:close	()V
    //   98: aload 4
    //   100: athrow
    //   101: astore_3
    //   102: aload_2
    //   103: invokeinterface 100 1 0
    //   108: aload_3
    //   109: athrow
    //   110: astore_2
    //   111: aload_1
    //   112: invokevirtual 103	java/io/InputStream:close	()V
    //   115: aload_2
    //   116: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	117	0	this	FileUtil
    //   0	117	1	paramContext	Context
    //   0	117	2	paramUri	Uri
    //   50	45	3	localFileOutputStream	java.io.FileOutputStream
    //   101	8	3	localThrowable1	Throwable
    //   14	76	4	localObject	Object
    //   92	7	4	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   51	75	92	java/lang/Throwable
    //   41	51	101	java/lang/Throwable
    //   75	79	101	java/lang/Throwable
    //   94	101	101	java/lang/Throwable
    //   36	41	110	java/lang/Throwable
    //   79	85	110	java/lang/Throwable
    //   102	110	110	java/lang/Throwable
  }
  
  protected InputStream doInBackground(String... paramVarArgs)
  {
    Object localObject1 = paramVarArgs[0];
    try
    {
      localObject1 = Uri.parse((String)localObject1);
      boolean bool = ((Uri)localObject1).getScheme().startsWith("http");
      if (bool)
      {
        localObject2 = context;
        localObject1 = getDownloadFileInputStream((Context)localObject2, (Uri)localObject1);
        return localObject1;
      }
      localObject2 = context;
      localObject1 = ((Context)localObject2).getContentResolver().openInputStream((Uri)localObject1);
      return localObject1;
    }
    catch (Exception localException)
    {
      exception = localException;
      Object localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("Could not retrieve file for contentUri ");
      ((StringBuilder)localObject2).append(paramVarArgs[0]);
      FLog.e("ReactNative", ((StringBuilder)localObject2).toString(), localException);
    }
    return null;
  }
}
