package com.RNFetchBlob.Response;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import okio.Timeout;

public class RNFetchBlobFileResp
  extends ResponseBody
{
  long bytesDownloaded = 0L;
  boolean isEndMarkerReceived;
  String mPath;
  String mTaskId;
  FileOutputStream ofStream;
  ResponseBody originalBody;
  ReactApplicationContext rctContext;
  
  public RNFetchBlobFileResp(ReactApplicationContext paramReactApplicationContext, String paramString1, ResponseBody paramResponseBody, String paramString2, boolean paramBoolean)
    throws IOException
  {
    rctContext = paramReactApplicationContext;
    mTaskId = paramString1;
    originalBody = paramResponseBody;
    mPath = paramString2;
    isEndMarkerReceived = false;
    if (paramString2 != null)
    {
      paramString1 = paramString2.replace("?append=true", "");
      mPath = paramString1;
      paramResponseBody = new File(paramString1);
      paramReactApplicationContext = paramResponseBody.getParentFile();
      if ((paramReactApplicationContext != null) && (!paramReactApplicationContext.exists()) && (!paramReactApplicationContext.mkdirs()))
      {
        paramString1 = new StringBuilder();
        paramString1.append("Couldn't create dir: ");
        paramString1.append(paramReactApplicationContext);
        throw new IllegalStateException(paramString1.toString());
      }
      if (!paramResponseBody.exists()) {
        paramResponseBody.createNewFile();
      }
      ofStream = new FileOutputStream(new File(paramString1), paramBoolean ^ true);
    }
  }
  
  public long contentLength()
  {
    return originalBody.contentLength();
  }
  
  public MediaType contentType()
  {
    return originalBody.contentType();
  }
  
  public boolean isDownloadComplete()
  {
    return (bytesDownloaded == contentLength()) || ((contentLength() == -1L) && (isEndMarkerReceived));
  }
  
  public BufferedSource source()
  {
    return Okio.buffer(new ProgressReportingSource(null));
  }
  
  private class ProgressReportingSource
    implements Source
  {
    private ProgressReportingSource() {}
    
    private void reportProgress(String paramString, long paramLong1, long paramLong2)
    {
      WritableMap localWritableMap = Arguments.createMap();
      localWritableMap.putString("taskId", paramString);
      localWritableMap.putString("written", String.valueOf(paramLong1));
      localWritableMap.putString("total", String.valueOf(paramLong2));
      ((DeviceEventManagerModule.RCTDeviceEventEmitter)rctContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit("RNFetchBlobProgress", localWritableMap);
    }
    
    public void close()
      throws IOException
    {
      ofStream.close();
    }
    
    public long read(Buffer paramBuffer, long paramLong)
      throws IOException
    {
      throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:539)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s2stmt(TypeTransformer.java:820)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:843)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
    }
    
    public Timeout timeout()
    {
      return null;
    }
  }
}
