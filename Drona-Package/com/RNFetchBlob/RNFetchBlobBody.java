package com.RNFetchBlob;

import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Base64;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

class RNFetchBlobBody
  extends RequestBody
{
  private File bodyCache;
  private Boolean chunkedEncoding = Boolean.valueOf(false);
  private long contentLength = 0L;
  private ReadableArray form;
  private String mTaskId;
  private MediaType mime;
  private String rawBody;
  int reported = 0;
  private InputStream requestStream;
  private RNFetchBlobReq.RequestType requestType;
  
  RNFetchBlobBody(String paramString)
  {
    mTaskId = paramString;
  }
  
  private ArrayList countFormDataLength()
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    ReactApplicationContext localReactApplicationContext = RNFetchBlob.RCTContext;
    long l2 = 0L;
    int i = 0;
    while (i < form.size())
    {
      Object localObject1 = new FormField(form.getMap(i));
      localArrayList.add(localObject1);
      Object localObject3;
      long l1;
      if (data == null)
      {
        localObject3 = new StringBuilder();
        ((StringBuilder)localObject3).append("RNFetchBlob multipart request builder has found a field without `data` property, the field `");
        ((StringBuilder)localObject3).append(name);
        ((StringBuilder)localObject3).append("` will be removed implicitly.");
        RNFetchBlobUtils.emitWarningEvent(((StringBuilder)localObject3).toString());
        l1 = l2;
      }
      else
      {
        Object localObject2;
        if (filename != null)
        {
          localObject1 = data;
          int j;
          if (((String)localObject1).startsWith("RNFetchBlob-file://"))
          {
            localObject1 = RNFetchBlobFS.normalizePath(((String)localObject1).substring("RNFetchBlob-file://".length()));
            if (RNFetchBlobFS.isAsset((String)localObject1)) {
              try
              {
                localObject1 = ((String)localObject1).replace("bundle-assets://", "");
                j = localReactApplicationContext.getAssets().open((String)localObject1).available();
                l1 = l2 + j;
              }
              catch (IOException localIOException)
              {
                RNFetchBlobUtils.emitWarningEvent(localIOException.getLocalizedMessage());
                l1 = l2;
              }
            } else {
              l1 = l2 + new File(RNFetchBlobFS.normalizePath(localIOException)).length();
            }
          }
          else if (localIOException.startsWith("RNFetchBlob-content://"))
          {
            String str = localIOException.substring("RNFetchBlob-content://".length());
            StringBuilder localStringBuilder = null;
            localObject2 = null;
            Object localObject4;
            try
            {
              localObject3 = localReactApplicationContext.getContentResolver().openInputStream(Uri.parse(str));
              try
              {
                j = ((InputStream)localObject3).available();
                l2 += j;
                l1 = l2;
                if (localObject3 == null) {
                  break label479;
                }
                ((InputStream)localObject3).close();
                l1 = l2;
              }
              catch (Throwable localThrowable2)
              {
                localObject2 = localObject3;
                localObject3 = localThrowable2;
                break label437;
              }
              catch (Exception localException1) {}
              localObject2 = localObject4;
            }
            catch (Throwable localThrowable1) {}catch (Exception localException2)
            {
              localObject4 = localStringBuilder;
            }
            localStringBuilder = new StringBuilder();
            localObject2 = localObject4;
            localStringBuilder.append("Failed to estimate form data length from content URI:");
            localObject2 = localObject4;
            localStringBuilder.append(str);
            localObject2 = localObject4;
            localStringBuilder.append(", ");
            localObject2 = localObject4;
            localStringBuilder.append(localException2.getLocalizedMessage());
            localObject2 = localObject4;
            RNFetchBlobUtils.emitWarningEvent(localStringBuilder.toString());
            l1 = l2;
            if (localObject4 != null)
            {
              localObject4.close();
              l1 = l2;
              break label479;
              label437:
              if (localObject2 != null) {
                localObject2.close();
              }
              throw localObject4;
            }
          }
          else
          {
            l1 = l2 + Base64.decode(localObject2, 0).length;
          }
        }
        else
        {
          l1 = l2 + data.getBytes().length;
        }
      }
      label479:
      i += 1;
      l2 = l1;
    }
    contentLength = l2;
    return localArrayList;
  }
  
  private File createMultipartBodyCache()
    throws IOException
  {
    Object localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("RNFetchBlob-");
    ((StringBuilder)localObject1).append(mTaskId);
    String str2 = ((StringBuilder)localObject1).toString();
    File localFile = File.createTempFile("rnfb-form-tmp", "", RNFetchBlob.RCTContext.getCacheDir());
    FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
    localObject1 = countFormDataLength();
    ReactApplicationContext localReactApplicationContext = RNFetchBlob.RCTContext;
    Iterator localIterator = ((ArrayList)localObject1).iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (FormField)localIterator.next();
      String str1 = data;
      Object localObject4 = name;
      if ((localObject4 != null) && (str1 != null))
      {
        Object localObject5 = new StringBuilder();
        ((StringBuilder)localObject5).append("--");
        ((StringBuilder)localObject5).append(str2);
        ((StringBuilder)localObject5).append("\r\n");
        localObject5 = ((StringBuilder)localObject5).toString();
        Object localObject3;
        if (filename != null)
        {
          Object localObject6 = new StringBuilder();
          ((StringBuilder)localObject6).append((String)localObject5);
          ((StringBuilder)localObject6).append("Content-Disposition: form-data; name=\"");
          ((StringBuilder)localObject6).append((String)localObject4);
          ((StringBuilder)localObject6).append("\"; filename=\"");
          ((StringBuilder)localObject6).append(filename);
          ((StringBuilder)localObject6).append("\"\r\n");
          localObject4 = ((StringBuilder)localObject6).toString();
          localObject5 = new StringBuilder();
          ((StringBuilder)localObject5).append((String)localObject4);
          ((StringBuilder)localObject5).append("Content-Type: ");
          ((StringBuilder)localObject5).append(mime);
          ((StringBuilder)localObject5).append("\r\n\r\n");
          localFileOutputStream.write(((StringBuilder)localObject5).toString().getBytes());
          Object localObject2;
          if (str1.startsWith("RNFetchBlob-file://"))
          {
            localObject1 = RNFetchBlobFS.normalizePath(str1.substring("RNFetchBlob-file://".length()));
            if (RNFetchBlobFS.isAsset((String)localObject1))
            {
              try
              {
                str1 = ((String)localObject1).replace("bundle-assets://", "");
                pipeStreamToFileStream(localReactApplicationContext.getAssets().open(str1), localFileOutputStream);
              }
              catch (IOException localIOException)
              {
                localObject4 = new StringBuilder();
                ((StringBuilder)localObject4).append("Failed to create form data asset :");
                ((StringBuilder)localObject4).append((String)localObject1);
                ((StringBuilder)localObject4).append(", ");
                ((StringBuilder)localObject4).append(localIOException.getLocalizedMessage());
                RNFetchBlobUtils.emitWarningEvent(((StringBuilder)localObject4).toString());
              }
            }
            else
            {
              localObject2 = new File(RNFetchBlobFS.normalizePath((String)localObject1));
              if (((File)localObject2).exists())
              {
                pipeStreamToFileStream(new FileInputStream((File)localObject2), localFileOutputStream);
              }
              else
              {
                localObject2 = new StringBuilder();
                ((StringBuilder)localObject2).append("Failed to create form data from path :");
                ((StringBuilder)localObject2).append((String)localObject1);
                ((StringBuilder)localObject2).append(", file not exists.");
                RNFetchBlobUtils.emitWarningEvent(((StringBuilder)localObject2).toString());
              }
            }
          }
          else if (((String)localObject2).startsWith("RNFetchBlob-content://"))
          {
            localObject6 = ((String)localObject2).substring("RNFetchBlob-content://".length());
            localObject5 = null;
            localObject1 = null;
            try
            {
              localObject2 = localReactApplicationContext.getContentResolver().openInputStream(Uri.parse((String)localObject6));
              try
              {
                pipeStreamToFileStream((InputStream)localObject2, localFileOutputStream);
                if (localObject2 == null) {
                  break label739;
                }
                ((InputStream)localObject2).close();
              }
              catch (Throwable localThrowable2)
              {
                localObject1 = localObject2;
                localObject2 = localThrowable2;
                break label614;
              }
              catch (Exception localException1) {}
              localObject1 = localObject3;
            }
            catch (Throwable localThrowable1) {}catch (Exception localException2)
            {
              localObject3 = localObject5;
            }
            localObject5 = new StringBuilder();
            localObject1 = localObject3;
            ((StringBuilder)localObject5).append("Failed to create form data from content URI:");
            localObject1 = localObject3;
            ((StringBuilder)localObject5).append((String)localObject6);
            localObject1 = localObject3;
            ((StringBuilder)localObject5).append(", ");
            localObject1 = localObject3;
            ((StringBuilder)localObject5).append(localException2.getLocalizedMessage());
            localObject1 = localObject3;
            RNFetchBlobUtils.emitWarningEvent(((StringBuilder)localObject5).toString());
            if (localObject3 != null)
            {
              ((InputStream)localObject3).close();
              break label739;
              label614:
              if (localObject1 != null) {
                ((InputStream)localObject1).close();
              }
              throw ((Throwable)localObject3);
            }
          }
          else
          {
            localFileOutputStream.write(Base64.decode((String)localObject3, 0));
          }
        }
        else
        {
          localObject3 = new StringBuilder();
          ((StringBuilder)localObject3).append((String)localObject5);
          ((StringBuilder)localObject3).append("Content-Disposition: form-data; name=\"");
          ((StringBuilder)localObject3).append(localException2);
          ((StringBuilder)localObject3).append("\"\r\n");
          localObject3 = ((StringBuilder)localObject3).toString();
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append((String)localObject3);
          localStringBuilder.append("Content-Type: ");
          localStringBuilder.append(mime);
          localStringBuilder.append("\r\n\r\n");
          localFileOutputStream.write(localStringBuilder.toString().getBytes());
          localFileOutputStream.write(data.getBytes());
        }
        label739:
        localFileOutputStream.write("\r\n".getBytes());
      }
    }
    localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("--");
    ((StringBuilder)localObject1).append(str2);
    ((StringBuilder)localObject1).append("--\r\n");
    localFileOutputStream.write(((StringBuilder)localObject1).toString().getBytes());
    localFileOutputStream.flush();
    localFileOutputStream.close();
    return localFile;
  }
  
  private void emitUploadProgress(long paramLong)
  {
    Object localObject = RNFetchBlobReq.getReportUploadProgress(mTaskId);
    if ((localObject != null) && (contentLength != 0L) && (((RNFetchBlobProgressConfig)localObject).shouldReport((float)paramLong / (float)contentLength)))
    {
      localObject = Arguments.createMap();
      ((WritableMap)localObject).putString("taskId", mTaskId);
      ((WritableMap)localObject).putString("written", String.valueOf(paramLong));
      ((WritableMap)localObject).putString("total", String.valueOf(contentLength));
      ((DeviceEventManagerModule.RCTDeviceEventEmitter)RNFetchBlob.RCTContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit("RNFetchBlobProgress-upload", localObject);
    }
  }
  
  private InputStream getRequestStream()
    throws Exception
  {
    Object localObject4;
    if (rawBody.startsWith("RNFetchBlob-file://"))
    {
      Object localObject1 = RNFetchBlobFS.normalizePath(rawBody.substring("RNFetchBlob-file://".length()));
      if (RNFetchBlobFS.isAsset((String)localObject1)) {
        try
        {
          localObject1 = ((String)localObject1).replace("bundle-assets://", "");
          localObject4 = RNFetchBlob.RCTContext;
          localObject1 = ((ContextWrapper)localObject4).getAssets().open((String)localObject1);
          return localObject1;
        }
        catch (Exception localException1)
        {
          localObject4 = new StringBuilder();
          ((StringBuilder)localObject4).append("error when getting request stream from asset : ");
          ((StringBuilder)localObject4).append(localException1.getLocalizedMessage());
          throw new Exception(((StringBuilder)localObject4).toString());
        }
      }
      Object localObject2 = new File(RNFetchBlobFS.normalizePath(localException1));
      try
      {
        boolean bool = ((File)localObject2).exists();
        if (!bool) {
          ((File)localObject2).createNewFile();
        }
        localObject2 = new FileInputStream((File)localObject2);
        return localObject2;
      }
      catch (Exception localException2)
      {
        localObject4 = new StringBuilder();
        ((StringBuilder)localObject4).append("error when getting request stream: ");
        ((StringBuilder)localObject4).append(localException2.getLocalizedMessage());
        throw new Exception(((StringBuilder)localObject4).toString());
      }
    }
    if (rawBody.startsWith("RNFetchBlob-content://"))
    {
      localObject3 = rawBody.substring("RNFetchBlob-content://".length());
      localObject4 = RNFetchBlob.RCTContext;
      try
      {
        localObject4 = ((ContextWrapper)localObject4).getContentResolver().openInputStream(Uri.parse((String)localObject3));
        return localObject4;
      }
      catch (Exception localException4)
      {
        StringBuilder localStringBuilder2 = new StringBuilder();
        localStringBuilder2.append("error when getting request stream for content URI: ");
        localStringBuilder2.append((String)localObject3);
        throw new Exception(localStringBuilder2.toString(), localException4);
      }
    }
    Object localObject3 = rawBody;
    try
    {
      localObject3 = Base64.decode((String)localObject3, 0);
      localObject3 = new ByteArrayInputStream((byte[])localObject3);
      return localObject3;
    }
    catch (Exception localException3)
    {
      StringBuilder localStringBuilder1 = new StringBuilder();
      localStringBuilder1.append("error when getting request stream: ");
      localStringBuilder1.append(localException3.getLocalizedMessage());
      throw new Exception(localStringBuilder1.toString());
    }
  }
  
  private void pipeStreamToFileStream(InputStream paramInputStream, FileOutputStream paramFileOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte['?'];
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i <= 0) {
        break;
      }
      paramFileOutputStream.write(arrayOfByte, 0, i);
    }
    paramInputStream.close();
  }
  
  private void pipeStreamToSink(InputStream paramInputStream, BufferedSink paramBufferedSink)
    throws IOException
  {
    byte[] arrayOfByte = new byte['?'];
    long l = 0L;
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte, 0, 10240);
      if (i <= 0) {
        break;
      }
      paramBufferedSink.write(arrayOfByte, 0, i);
      l += i;
      emitUploadProgress(l);
    }
    paramInputStream.close();
  }
  
  RNFetchBlobBody chunkedEncoding(boolean paramBoolean)
  {
    chunkedEncoding = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  boolean clearRequestBody()
  {
    File localFile;
    if (bodyCache != null) {
      localFile = bodyCache;
    }
    try
    {
      boolean bool = localFile.exists();
      if (bool)
      {
        localFile = bodyCache;
        localFile.delete();
      }
      return true;
    }
    catch (Exception localException)
    {
      RNFetchBlobUtils.emitWarningEvent(localException.getLocalizedMessage());
    }
    return false;
  }
  
  public long contentLength()
  {
    if (chunkedEncoding.booleanValue()) {
      return -1L;
    }
    return contentLength;
  }
  
  public MediaType contentType()
  {
    return mime;
  }
  
  RNFetchBlobBody setBody(ReadableArray paramReadableArray)
  {
    form = paramReadableArray;
    try
    {
      paramReadableArray = createMultipartBodyCache();
      bodyCache = paramReadableArray;
      paramReadableArray = bodyCache;
      paramReadableArray = new FileInputStream(paramReadableArray);
      requestStream = paramReadableArray;
      paramReadableArray = bodyCache;
      long l = paramReadableArray.length();
      contentLength = l;
      return this;
    }
    catch (Exception paramReadableArray)
    {
      paramReadableArray.printStackTrace();
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("RNFetchBlob failed to create request multipart body :");
      localStringBuilder.append(paramReadableArray.getLocalizedMessage());
      RNFetchBlobUtils.emitWarningEvent(localStringBuilder.toString());
    }
    return this;
  }
  
  RNFetchBlobBody setBody(String paramString)
  {
    rawBody = paramString;
    if (rawBody == null)
    {
      rawBody = "";
      requestType = RNFetchBlobReq.RequestType.AsIs;
    }
    paramString = 1.$SwitchMap$com$RNFetchBlob$RNFetchBlobReq$RequestType;
    Object localObject = requestType;
    try
    {
      int i = ((Enum)localObject).ordinal();
      switch (paramString[i])
      {
      default: 
        return this;
      case 2: 
        paramString = rawBody;
        paramString = paramString.getBytes();
        contentLength = paramString.length;
        paramString = rawBody;
        paramString = new ByteArrayInputStream(paramString.getBytes());
        requestStream = paramString;
        return this;
      }
      paramString = getRequestStream();
      requestStream = paramString;
      paramString = requestStream;
      i = paramString.available();
      contentLength = i;
      return this;
    }
    catch (Exception paramString)
    {
      paramString.printStackTrace();
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("RNFetchBlob failed to create single content request body :");
      ((StringBuilder)localObject).append(paramString.getLocalizedMessage());
      ((StringBuilder)localObject).append("\r\n");
      RNFetchBlobUtils.emitWarningEvent(((StringBuilder)localObject).toString());
    }
    return this;
  }
  
  RNFetchBlobBody setMIME(MediaType paramMediaType)
  {
    mime = paramMediaType;
    return this;
  }
  
  RNFetchBlobBody setRequestType(RNFetchBlobReq.RequestType paramRequestType)
  {
    requestType = paramRequestType;
    return this;
  }
  
  public void writeTo(BufferedSink paramBufferedSink)
  {
    InputStream localInputStream = requestStream;
    try
    {
      pipeStreamToSink(localInputStream, paramBufferedSink);
      return;
    }
    catch (Exception paramBufferedSink)
    {
      RNFetchBlobUtils.emitWarningEvent(paramBufferedSink.getLocalizedMessage());
      paramBufferedSink.printStackTrace();
    }
  }
  
  private class FormField
  {
    public String data;
    String filename;
    String mime;
    public String name;
    
    FormField(ReadableMap paramReadableMap)
    {
      if (paramReadableMap.hasKey("name")) {
        name = paramReadableMap.getString("name");
      }
      if (paramReadableMap.hasKey("filename")) {
        filename = paramReadableMap.getString("filename");
      }
      if (paramReadableMap.hasKey("type"))
      {
        mime = paramReadableMap.getString("type");
      }
      else
      {
        if (filename == null) {
          this$1 = "text/plain";
        } else {
          this$1 = "application/octet-stream";
        }
        mime = RNFetchBlobBody.this;
      }
      if (paramReadableMap.hasKey("data")) {
        data = paramReadableMap.getString("data");
      }
    }
  }
}
