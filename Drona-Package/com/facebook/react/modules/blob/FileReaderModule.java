package com.facebook.react.modules.blob;

import android.util.Base64;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name="FileReaderModule")
public class FileReaderModule
  extends ReactContextBaseJavaModule
{
  private static final String ERROR_INVALID_BLOB = "ERROR_INVALID_BLOB";
  public static final String NAME = "FileReaderModule";
  
  public FileReaderModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  private BlobModule getBlobModule()
  {
    return (BlobModule)getReactApplicationContext().getNativeModule(BlobModule.class);
  }
  
  public String getName()
  {
    return "FileReaderModule";
  }
  
  public void readAsDataURL(ReadableMap paramReadableMap, Promise paramPromise)
  {
    byte[] arrayOfByte = getBlobModule().resolve(paramReadableMap.getString("blobId"), paramReadableMap.getInt("offset"), paramReadableMap.getInt("size"));
    if (arrayOfByte == null)
    {
      paramPromise.reject("ERROR_INVALID_BLOB", "The specified blob is invalid");
      return;
    }
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("data:");
      boolean bool = paramReadableMap.hasKey("type");
      if (bool)
      {
        bool = paramReadableMap.getString("type").isEmpty();
        if (!bool)
        {
          localStringBuilder.append(paramReadableMap.getString("type"));
          break label120;
        }
      }
      localStringBuilder.append("application/octet-stream");
      label120:
      localStringBuilder.append(";base64,");
      localStringBuilder.append(Base64.encodeToString(arrayOfByte, 2));
      paramPromise.resolve(localStringBuilder.toString());
      return;
    }
    catch (Exception paramReadableMap)
    {
      paramPromise.reject(paramReadableMap);
    }
  }
  
  public void readAsText(ReadableMap paramReadableMap, String paramString, Promise paramPromise)
  {
    paramReadableMap = getBlobModule().resolve(paramReadableMap.getString("blobId"), paramReadableMap.getInt("offset"), paramReadableMap.getInt("size"));
    if (paramReadableMap == null)
    {
      paramPromise.reject("ERROR_INVALID_BLOB", "The specified blob is invalid");
      return;
    }
    try
    {
      paramPromise.resolve(new String(paramReadableMap, paramString));
      return;
    }
    catch (Exception paramReadableMap)
    {
      paramPromise.reject(paramReadableMap);
    }
  }
}