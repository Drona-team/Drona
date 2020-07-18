package com.facebook.react.modules.camera;

import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64OutputStream;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.GuardedAsyncTask;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.module.annotations.ReactModule;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@ReactModule(name="ImageStoreManager")
public class ImageStoreManager
  extends ReactContextBaseJavaModule
{
  private static final int BUFFER_SIZE = 8192;
  public static final String NAME = "ImageStoreManager";
  
  public ImageStoreManager(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  private static void closeQuietly(Closeable paramCloseable)
  {
    try
    {
      paramCloseable.close();
      return;
    }
    catch (IOException paramCloseable) {}
  }
  
  String convertInputStreamToBase64OutputStream(InputStream paramInputStream)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    Base64OutputStream localBase64OutputStream = new Base64OutputStream(localByteArrayOutputStream, 2);
    byte[] arrayOfByte = new byte['?'];
    try
    {
      for (;;)
      {
        int i = paramInputStream.read(arrayOfByte);
        if (i <= -1) {
          break;
        }
        localBase64OutputStream.write(arrayOfByte, 0, i);
      }
      closeQuietly(localBase64OutputStream);
      return localByteArrayOutputStream.toString();
    }
    catch (Throwable paramInputStream)
    {
      closeQuietly(localBase64OutputStream);
      throw paramInputStream;
    }
  }
  
  public void getBase64ForTag(String paramString, Callback paramCallback1, Callback paramCallback2)
  {
    new GetBase64Task(getReactApplicationContext(), paramString, paramCallback1, paramCallback2, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  public String getName()
  {
    return "ImageStoreManager";
  }
  
  private class GetBase64Task
    extends GuardedAsyncTask<Void, Void>
  {
    private final Callback mError;
    private final Callback mSuccess;
    private final String mUri;
    
    private GetBase64Task(ReactContext paramReactContext, String paramString, Callback paramCallback1, Callback paramCallback2)
    {
      super();
      mUri = paramString;
      mSuccess = paramCallback1;
      mError = paramCallback2;
    }
    
    protected void doInBackgroundGuarded(Void... paramVarArgs)
    {
      paramVarArgs = ImageStoreManager.this;
      try
      {
        paramVarArgs = paramVarArgs.getReactApplicationContext().getContentResolver();
        Object localObject1 = mUri;
        paramVarArgs = paramVarArgs.openInputStream(Uri.parse((String)localObject1));
        for (;;)
        {
          try
          {
            localObject1 = mSuccess;
            localObject2 = ImageStoreManager.this;
          }
          catch (Throwable localThrowable)
          {
            Object localObject2;
            ImageStoreManager.closeQuietly(paramVarArgs);
            throw localIOException;
          }
          try
          {
            localObject2 = ((ImageStoreManager)localObject2).convertInputStreamToBase64OutputStream(paramVarArgs);
            ((Callback)localObject1).invoke(new Object[] { localObject2 });
          }
          catch (IOException localIOException)
          {
            mError.invoke(new Object[] { localIOException.getMessage() });
          }
        }
        ImageStoreManager.closeQuietly(paramVarArgs);
        return;
      }
      catch (FileNotFoundException paramVarArgs)
      {
        mError.invoke(new Object[] { ((FileNotFoundException)paramVarArgs).getMessage() });
      }
    }
  }
}
