package com.RNFetchBlob.Response;

import com.RNFetchBlob.RNFetchBlobProgressConfig;
import com.RNFetchBlob.RNFetchBlobReq;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import java.io.IOException;
import java.nio.charset.Charset;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import okio.Timeout;

public class RNFetchBlobDefaultResp
  extends ResponseBody
{
  boolean isIncrement = false;
  String mTaskId;
  ResponseBody originalBody;
  ReactApplicationContext rctContext;
  
  public RNFetchBlobDefaultResp(ReactApplicationContext paramReactApplicationContext, String paramString, ResponseBody paramResponseBody, boolean paramBoolean)
  {
    rctContext = paramReactApplicationContext;
    mTaskId = paramString;
    originalBody = paramResponseBody;
    isIncrement = paramBoolean;
  }
  
  public long contentLength()
  {
    return originalBody.contentLength();
  }
  
  public MediaType contentType()
  {
    return originalBody.contentType();
  }
  
  public BufferedSource source()
  {
    return Okio.buffer(new ProgressReportingSource(originalBody.source()));
  }
  
  private class ProgressReportingSource
    implements Source
  {
    long bytesRead = 0L;
    BufferedSource mOriginalSource;
    
    ProgressReportingSource(BufferedSource paramBufferedSource)
    {
      mOriginalSource = paramBufferedSource;
    }
    
    public void close()
      throws IOException
    {}
    
    public long read(Buffer paramBuffer, long paramLong)
      throws IOException
    {
      long l1 = mOriginalSource.read(paramBuffer, paramLong);
      long l2 = bytesRead;
      if (l1 > 0L) {
        paramLong = l1;
      } else {
        paramLong = 0L;
      }
      bytesRead = (l2 + paramLong);
      Object localObject = RNFetchBlobReq.getReportProgress(mTaskId);
      paramLong = contentLength();
      if ((localObject != null) && (paramLong != 0L) && (((RNFetchBlobProgressConfig)localObject).shouldReport((float)(bytesRead / contentLength()))))
      {
        localObject = Arguments.createMap();
        ((WritableMap)localObject).putString("taskId", mTaskId);
        ((WritableMap)localObject).putString("written", String.valueOf(bytesRead));
        ((WritableMap)localObject).putString("total", String.valueOf(contentLength()));
        if (isIncrement) {
          ((WritableMap)localObject).putString("chunk", paramBuffer.readString(Charset.defaultCharset()));
        } else {
          ((WritableMap)localObject).putString("chunk", "");
        }
        ((DeviceEventManagerModule.RCTDeviceEventEmitter)rctContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit("RNFetchBlobProgress", localObject);
      }
      return l1;
    }
    
    public Timeout timeout()
    {
      return null;
    }
  }
}
