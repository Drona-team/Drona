package com.facebook.react.modules.network;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import com.facebook.common.logging.FLog;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.ByteString;
import okio.Okio;
import okio.Source;

class RequestBodyUtil
{
  private static final String CONTENT_ENCODING_GZIP = "gzip";
  private static final String NAME = "RequestBodyUtil";
  private static final String TEMP_FILE_SUFFIX = "temp";
  
  RequestBodyUtil() {}
  
  public static RequestBody create(MediaType paramMediaType, final InputStream paramInputStream)
  {
    new RequestBody()
    {
      public long contentLength()
      {
        InputStream localInputStream = paramInputStream;
        try
        {
          int i = localInputStream.available();
          return i;
        }
        catch (IOException localIOException)
        {
          for (;;) {}
        }
        return 0L;
      }
      
      public MediaType contentType()
      {
        return val$mediaType;
      }
      
      public void writeTo(BufferedSink paramAnonymousBufferedSink)
        throws IOException
      {
        Object localObject2 = null;
        try
        {
          Object localObject1 = Okio.source(paramInputStream);
          try
          {
            paramAnonymousBufferedSink.writeAll((Source)localObject1);
            Util.closeQuietly((Closeable)localObject1);
            return;
          }
          catch (Throwable localThrowable2)
          {
            paramAnonymousBufferedSink = (BufferedSink)localObject1;
            localObject1 = localThrowable2;
          }
          Util.closeQuietly((Closeable)paramAnonymousBufferedSink);
        }
        catch (Throwable localThrowable1)
        {
          paramAnonymousBufferedSink = localThrowable2;
        }
        throw localThrowable1;
      }
    };
  }
  
  public static RequestBody createGzip(MediaType paramMediaType, String paramString)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      GZIPOutputStream localGZIPOutputStream = new GZIPOutputStream(localByteArrayOutputStream);
      localGZIPOutputStream.write(paramString.getBytes());
      localGZIPOutputStream.close();
      return RequestBody.create(paramMediaType, localByteArrayOutputStream.toByteArray());
    }
    catch (IOException paramMediaType)
    {
      for (;;) {}
    }
    return null;
  }
  
  public static ProgressRequestBody createProgressRequest(RequestBody paramRequestBody, ProgressListener paramProgressListener)
  {
    return new ProgressRequestBody(paramRequestBody, paramProgressListener);
  }
  
  /* Error */
  private static InputStream getDownloadFileInputStream(Context paramContext, Uri paramUri)
    throws IOException
  {
    // Byte code:
    //   0: ldc 13
    //   2: ldc 16
    //   4: aload_0
    //   5: invokevirtual 78	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   8: invokevirtual 82	android/content/Context:getCacheDir	()Ljava/io/File;
    //   11: invokestatic 88	java/io/File:createTempFile	(Ljava/lang/String;Ljava/lang/String;Ljava/io/File;)Ljava/io/File;
    //   14: astore_3
    //   15: aload_3
    //   16: invokevirtual 91	java/io/File:deleteOnExit	()V
    //   19: new 93	java/net/URL
    //   22: dup
    //   23: aload_1
    //   24: invokevirtual 99	android/net/Uri:toString	()Ljava/lang/String;
    //   27: invokespecial 102	java/net/URL:<init>	(Ljava/lang/String;)V
    //   30: invokevirtual 106	java/net/URL:openStream	()Ljava/io/InputStream;
    //   33: astore_0
    //   34: aload_0
    //   35: invokestatic 112	java/nio/channels/Channels:newChannel	(Ljava/io/InputStream;)Ljava/nio/channels/ReadableByteChannel;
    //   38: astore_1
    //   39: new 114	java/io/FileOutputStream
    //   42: dup
    //   43: aload_3
    //   44: invokespecial 117	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   47: astore_2
    //   48: aload_2
    //   49: invokevirtual 121	java/io/FileOutputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   52: aload_1
    //   53: lconst_0
    //   54: ldc2_w 122
    //   57: invokevirtual 129	java/nio/channels/FileChannel:transferFrom	(Ljava/nio/channels/ReadableByteChannel;JJ)J
    //   60: pop2
    //   61: new 131	java/io/FileInputStream
    //   64: dup
    //   65: aload_3
    //   66: invokespecial 132	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   69: astore_3
    //   70: aload_2
    //   71: invokevirtual 133	java/io/FileOutputStream:close	()V
    //   74: aload_1
    //   75: invokeinterface 136 1 0
    //   80: aload_0
    //   81: invokevirtual 139	java/io/InputStream:close	()V
    //   84: aload_3
    //   85: areturn
    //   86: astore_3
    //   87: aload_2
    //   88: invokevirtual 133	java/io/FileOutputStream:close	()V
    //   91: aload_3
    //   92: athrow
    //   93: astore_2
    //   94: aload_1
    //   95: invokeinterface 136 1 0
    //   100: aload_2
    //   101: athrow
    //   102: astore_1
    //   103: aload_0
    //   104: invokevirtual 139	java/io/InputStream:close	()V
    //   107: aload_1
    //   108: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	109	0	paramContext	Context
    //   0	109	1	paramUri	Uri
    //   47	41	2	localFileOutputStream	java.io.FileOutputStream
    //   93	8	2	localThrowable1	Throwable
    //   14	71	3	localObject	Object
    //   86	6	3	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   48	70	86	java/lang/Throwable
    //   39	48	93	java/lang/Throwable
    //   70	74	93	java/lang/Throwable
    //   87	93	93	java/lang/Throwable
    //   34	39	102	java/lang/Throwable
    //   74	80	102	java/lang/Throwable
    //   94	102	102	java/lang/Throwable
  }
  
  public static RequestBody getEmptyBody(String paramString)
  {
    if ((!paramString.equals("POST")) && (!paramString.equals("PUT")) && (!paramString.equals("PATCH"))) {
      return null;
    }
    return RequestBody.create(null, ByteString.EMPTY);
  }
  
  public static InputStream getFileInputStream(Context paramContext, String paramString)
  {
    try
    {
      localObject = Uri.parse(paramString);
      boolean bool = ((Uri)localObject).getScheme().startsWith("http");
      if (bool)
      {
        paramContext = getDownloadFileInputStream(paramContext, (Uri)localObject);
        return paramContext;
      }
      paramContext = paramContext.getContentResolver().openInputStream((Uri)localObject);
      return paramContext;
    }
    catch (Exception paramContext)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Could not retrieve file for contentUri ");
      ((StringBuilder)localObject).append(paramString);
      FLog.e("ReactNative", ((StringBuilder)localObject).toString(), paramContext);
    }
    return null;
  }
  
  public static boolean isGzipEncoding(String paramString)
  {
    return "gzip".equalsIgnoreCase(paramString);
  }
}