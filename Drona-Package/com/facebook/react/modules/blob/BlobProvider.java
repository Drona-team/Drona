package com.facebook.react.modules.blob;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.ReactContext;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public final class BlobProvider
  extends ContentProvider
{
  public BlobProvider() {}
  
  public int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    return 0;
  }
  
  public String getType(Uri paramUri)
  {
    return null;
  }
  
  public Uri insert(Uri paramUri, ContentValues paramContentValues)
  {
    return null;
  }
  
  public boolean onCreate()
  {
    return true;
  }
  
  public ParcelFileDescriptor openFile(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    if (paramString.equals("r"))
    {
      paramString = getContext().getApplicationContext();
      if ((paramString instanceof ReactApplication)) {
        paramString = (BlobModule)((ReactApplication)paramString).getReactNativeHost().getReactInstanceManager().getCurrentReactContext().getNativeModule(BlobModule.class);
      } else {
        paramString = null;
      }
      if (paramString != null)
      {
        paramString = paramString.resolve(paramUri);
        if (paramString == null) {}
      }
    }
    for (;;)
    {
      try
      {
        localObject = ParcelFileDescriptor.createPipe();
        paramUri = localObject[0];
        localObject = localObject[1];
      }
      catch (IOException paramUri)
      {
        Object localObject;
        return null;
      }
      try
      {
        localObject = new ParcelFileDescriptor.AutoCloseOutputStream((ParcelFileDescriptor)localObject);
        try
        {
          ((OutputStream)localObject).write(paramString);
          StringBuilder localStringBuilder;
          try
          {
            paramUri.addSuppressed(localThrowable);
            continue;
            localThrowable.close();
            throw paramString;
          }
          catch (IOException paramUri) {}
        }
        catch (Throwable paramUri)
        {
          try
          {
            ((OutputStream)localObject).close();
            return paramUri;
          }
          catch (IOException paramUri)
          {
            return null;
          }
          paramUri = paramUri;
          try
          {
            throw paramUri;
          }
          catch (Throwable paramString)
          {
            if (paramUri != null) {
              try
              {
                ((OutputStream)localObject).close();
              }
              catch (Throwable localThrowable) {}
            }
          }
        }
        paramString = new StringBuilder();
        paramString.append("Cannot open ");
        paramString.append(paramUri.toString());
        paramString.append(", blob not found.");
        throw new FileNotFoundException(paramString.toString());
      }
      catch (IOException paramUri)
      {
        return null;
      }
    }
    throw new RuntimeException("No blob module associated with BlobProvider");
    localStringBuilder = new StringBuilder();
    localStringBuilder.append("Cannot open ");
    localStringBuilder.append(paramUri.toString());
    localStringBuilder.append(" in mode '");
    localStringBuilder.append(paramString);
    localStringBuilder.append("'");
    throw new FileNotFoundException(localStringBuilder.toString());
    return null;
  }
  
  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    return null;
  }
  
  public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    return 0;
  }
}
