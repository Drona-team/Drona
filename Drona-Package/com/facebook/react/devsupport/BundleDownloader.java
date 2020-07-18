package com.facebook.react.devsupport;

import android.util.Log;
import android.util.Pair;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.NativeDeltaClient;
import com.facebook.react.common.DebugServerException;
import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import org.json.JSONException;
import org.json.JSONObject;

public class BundleDownloader
{
  private static final int FILES_CHANGED_COUNT_NOT_BUILT_BY_BUNDLER = -2;
  private static final String PAGE_KEY = "BundleDownloader";
  private BundleDeltaClient mBundleDeltaClient;
  private final OkHttpClient mClient;
  @Nullable
  private Call mDownloadBundleFromURLCall;
  
  public BundleDownloader(OkHttpClient paramOkHttpClient)
  {
    mClient = paramOkHttpClient;
  }
  
  private String formatBundleUrl(String paramString, BundleDeltaClient.ClientType paramClientType)
  {
    String str = paramString;
    if (BundleDeltaClient.isDeltaUrl(paramString))
    {
      str = paramString;
      if (mBundleDeltaClient != null)
      {
        str = paramString;
        if (mBundleDeltaClient.canHandle(paramClientType)) {
          str = mBundleDeltaClient.extendUrlForDelta(paramString);
        }
      }
    }
    return str;
  }
  
  private BundleDeltaClient getBundleDeltaClient(BundleDeltaClient.ClientType paramClientType)
  {
    if ((mBundleDeltaClient == null) || (!mBundleDeltaClient.canHandle(paramClientType))) {
      mBundleDeltaClient = BundleDeltaClient.create(paramClientType);
    }
    return mBundleDeltaClient;
  }
  
  private static void populateBundleInfo(String paramString, Headers paramHeaders, BundleDeltaClient.ClientType paramClientType, BundleInfo paramBundleInfo)
  {
    if (paramClientType == BundleDeltaClient.ClientType.NONE) {
      paramClientType = null;
    } else {
      paramClientType = paramClientType.name();
    }
    BundleInfo.access$302(paramBundleInfo, paramClientType);
    BundleInfo.access$402(paramBundleInfo, paramString);
    paramString = paramHeaders.get("X-Metro-Files-Changed-Count");
    if (paramString != null)
    {
      try
      {
        BundleInfo.access$502(paramBundleInfo, Integer.parseInt(paramString));
        return;
      }
      catch (NumberFormatException paramString)
      {
        for (;;) {}
      }
      BundleInfo.access$502(paramBundleInfo, -2);
      return;
    }
  }
  
  private void processBundleResult(String paramString, int paramInt, Headers paramHeaders, BufferedSource paramBufferedSource, File paramFile, BundleInfo paramBundleInfo, BundleDeltaClient.ClientType paramClientType, DevBundleDownloadListener paramDevBundleDownloadListener)
    throws IOException
  {
    if (paramInt != 200)
    {
      paramHeaders = paramBufferedSource.readUtf8();
      paramBufferedSource = DebugServerException.parse(paramString, paramHeaders);
      if (paramBufferedSource != null)
      {
        paramDevBundleDownloadListener.onFailure(paramBufferedSource);
        return;
      }
      paramBufferedSource = new StringBuilder();
      paramBufferedSource.append("The development server returned response error code: ");
      paramBufferedSource.append(paramInt);
      paramBufferedSource.append("\n\n");
      paramBufferedSource.append("URL: ");
      paramBufferedSource.append(paramString);
      paramBufferedSource.append("\n\n");
      paramBufferedSource.append("Body:\n");
      paramBufferedSource.append(paramHeaders);
      paramDevBundleDownloadListener.onFailure(new DebugServerException(paramBufferedSource.toString()));
      return;
    }
    if (paramBundleInfo != null) {
      populateBundleInfo(paramString, paramHeaders, paramClientType, paramBundleInfo);
    }
    paramBundleInfo = new StringBuilder();
    paramBundleInfo.append(paramFile.getPath());
    paramBundleInfo.append(".tmp");
    paramBundleInfo = new File(paramBundleInfo.toString());
    boolean bool = BundleDeltaClient.isDeltaUrl(paramString);
    paramString = null;
    if (bool)
    {
      paramString = getBundleDeltaClient(paramClientType);
      Assertions.assertNotNull(paramString);
      paramString = paramString.processDelta(paramHeaders, paramBufferedSource, paramBundleInfo);
      bool = ((Boolean)first).booleanValue();
      paramString = (NativeDeltaClient)second;
    }
    else
    {
      mBundleDeltaClient = null;
      bool = storePlainJSInFile(paramBufferedSource, paramBundleInfo);
    }
    if ((bool) && (!paramBundleInfo.renameTo(paramFile)))
    {
      paramString = new StringBuilder();
      paramString.append("Couldn't rename ");
      paramString.append(paramBundleInfo);
      paramString.append(" to ");
      paramString.append(paramFile);
      throw new IOException(paramString.toString());
    }
    paramDevBundleDownloadListener.onSuccess(paramString);
  }
  
  private void processMultipartResponse(final String paramString1, final Response paramResponse, String paramString2, final File paramFile, final BundleInfo paramBundleInfo, final BundleDeltaClient.ClientType paramClientType, final DevBundleDownloadListener paramDevBundleDownloadListener)
    throws IOException
  {
    if (!new MultipartStreamReader(paramResponse.body().source(), paramString2).readAllParts(new MultipartStreamReader.ChunkListener()
    {
      public void onChunkComplete(Map paramAnonymousMap, Buffer paramAnonymousBuffer, boolean paramAnonymousBoolean)
        throws IOException
      {
        if (paramAnonymousBoolean)
        {
          int i = paramResponse.code();
          if (paramAnonymousMap.containsKey("X-Http-Status")) {
            i = Integer.parseInt((String)paramAnonymousMap.get("X-Http-Status"));
          }
          BundleDownloader.this.processBundleResult(paramString1, i, Headers.of(paramAnonymousMap), paramAnonymousBuffer, paramFile, paramBundleInfo, paramClientType, paramDevBundleDownloadListener);
          return;
        }
        if (paramAnonymousMap.containsKey("Content-Type"))
        {
          if (!((String)paramAnonymousMap.get("Content-Type")).equals("application/json")) {
            return;
          }
          try
          {
            Object localObject = new JSONObject(paramAnonymousBuffer.readUtf8());
            paramAnonymousBoolean = ((JSONObject)localObject).has("status");
            Integer localInteger = null;
            if (paramAnonymousBoolean) {
              paramAnonymousMap = ((JSONObject)localObject).getString("status");
            } else {
              paramAnonymousMap = null;
            }
            paramAnonymousBoolean = ((JSONObject)localObject).has("done");
            if (paramAnonymousBoolean) {
              paramAnonymousBuffer = Integer.valueOf(((JSONObject)localObject).getInt("done"));
            } else {
              paramAnonymousBuffer = null;
            }
            paramAnonymousBoolean = ((JSONObject)localObject).has("total");
            if (paramAnonymousBoolean) {
              localInteger = Integer.valueOf(((JSONObject)localObject).getInt("total"));
            }
            localObject = paramDevBundleDownloadListener;
            ((DevBundleDownloadListener)localObject).onProgress(paramAnonymousMap, paramAnonymousBuffer, localInteger);
            return;
          }
          catch (JSONException paramAnonymousMap)
          {
            paramAnonymousBuffer = new StringBuilder();
            paramAnonymousBuffer.append("Error parsing progress JSON. ");
            paramAnonymousBuffer.append(paramAnonymousMap.toString());
            FLog.e("ReactNative", paramAnonymousBuffer.toString());
          }
        }
      }
      
      public void onChunkProgress(Map paramAnonymousMap, long paramAnonymousLong1, long paramAnonymousLong2)
        throws IOException
      {
        if ("application/javascript".equals(paramAnonymousMap.get("Content-Type"))) {
          paramDevBundleDownloadListener.onProgress("Downloading JavaScript bundle", Integer.valueOf((int)(paramAnonymousLong1 / 1024L)), Integer.valueOf((int)(paramAnonymousLong2 / 1024L)));
        }
      }
    }))
    {
      paramString2 = new StringBuilder();
      paramString2.append("Error while reading multipart response.\n\nResponse code: ");
      paramString2.append(paramResponse.code());
      paramString2.append("\n\nURL: ");
      paramString2.append(paramString1.toString());
      paramString2.append("\n\n");
      paramDevBundleDownloadListener.onFailure(new DebugServerException(paramString2.toString()));
    }
  }
  
  private static boolean storePlainJSInFile(BufferedSource paramBufferedSource, File paramFile)
    throws IOException
  {
    try
    {
      Sink localSink = Okio.sink(paramFile);
      paramFile = localSink;
      try
      {
        paramBufferedSource.readAll(localSink);
        if (localSink != null) {
          localSink.close();
        }
        return true;
      }
      catch (Throwable paramBufferedSource) {}
      if (paramFile == null) {
        break label44;
      }
    }
    catch (Throwable paramBufferedSource)
    {
      paramFile = null;
    }
    paramFile.close();
    label44:
    throw paramBufferedSource;
  }
  
  public void downloadBundleFromURL(DevBundleDownloadListener paramDevBundleDownloadListener, File paramFile, String paramString, BundleInfo paramBundleInfo, BundleDeltaClient.ClientType paramClientType)
  {
    downloadBundleFromURL(paramDevBundleDownloadListener, paramFile, paramString, paramBundleInfo, paramClientType, new Request.Builder());
  }
  
  public void downloadBundleFromURL(final DevBundleDownloadListener paramDevBundleDownloadListener, final File paramFile, String paramString, final BundleInfo paramBundleInfo, final BundleDeltaClient.ClientType paramClientType, Request.Builder paramBuilder)
  {
    paramString = paramBuilder.url(formatBundleUrl(paramString, paramClientType)).build();
    mDownloadBundleFromURLCall = ((Call)Assertions.assertNotNull(mClient.newCall(paramString)));
    mDownloadBundleFromURLCall.enqueue(new Callback()
    {
      public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException)
      {
        if ((mDownloadBundleFromURLCall != null) && (!mDownloadBundleFromURLCall.isCanceled()))
        {
          BundleDownloader.access$002(BundleDownloader.this, null);
          paramAnonymousCall = paramAnonymousCall.request().url().toString();
          DevBundleDownloadListener localDevBundleDownloadListener = paramDevBundleDownloadListener;
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("URL: ");
          localStringBuilder.append(paramAnonymousCall);
          localDevBundleDownloadListener.onFailure(DebugServerException.makeGeneric(paramAnonymousCall, "Could not connect to development server.", localStringBuilder.toString(), paramAnonymousIOException));
          return;
        }
        BundleDownloader.access$002(BundleDownloader.this, null);
      }
      
      /* Error */
      public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse)
        throws IOException
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 25	com/facebook/react/devsupport/BundleDownloader$1:this$0	Lcom/facebook/react/devsupport/BundleDownloader;
        //   4: invokestatic 43	com/facebook/react/devsupport/BundleDownloader:access$000	(Lcom/facebook/react/devsupport/BundleDownloader;)Lokhttp3/Call;
        //   7: ifnull +200 -> 207
        //   10: aload_0
        //   11: getfield 25	com/facebook/react/devsupport/BundleDownloader$1:this$0	Lcom/facebook/react/devsupport/BundleDownloader;
        //   14: invokestatic 43	com/facebook/react/devsupport/BundleDownloader:access$000	(Lcom/facebook/react/devsupport/BundleDownloader;)Lokhttp3/Call;
        //   17: invokeinterface 49 1 0
        //   22: ifeq +6 -> 28
        //   25: goto +182 -> 207
        //   28: aload_0
        //   29: getfield 25	com/facebook/react/devsupport/BundleDownloader$1:this$0	Lcom/facebook/react/devsupport/BundleDownloader;
        //   32: aconst_null
        //   33: invokestatic 53	com/facebook/react/devsupport/BundleDownloader:access$002	(Lcom/facebook/react/devsupport/BundleDownloader;Lokhttp3/Call;)Lokhttp3/Call;
        //   36: pop
        //   37: aload_2
        //   38: invokevirtual 101	okhttp3/Response:request	()Lokhttp3/Request;
        //   41: invokevirtual 63	okhttp3/Request:url	()Lokhttp3/HttpUrl;
        //   44: invokevirtual 69	okhttp3/HttpUrl:toString	()Ljava/lang/String;
        //   47: astore_1
        //   48: aload_2
        //   49: ldc 103
        //   51: invokevirtual 107	okhttp3/Response:header	(Ljava/lang/String;)Ljava/lang/String;
        //   54: astore 4
        //   56: ldc 109
        //   58: invokestatic 115	java/util/regex/Pattern:compile	(Ljava/lang/String;)Ljava/util/regex/Pattern;
        //   61: aload 4
        //   63: invokevirtual 119	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
        //   66: astore 4
        //   68: aload 4
        //   70: invokevirtual 124	java/util/regex/Matcher:find	()Z
        //   73: istore_3
        //   74: iload_3
        //   75: ifeq +37 -> 112
        //   78: aload_0
        //   79: getfield 25	com/facebook/react/devsupport/BundleDownloader$1:this$0	Lcom/facebook/react/devsupport/BundleDownloader;
        //   82: aload_1
        //   83: aload_2
        //   84: aload 4
        //   86: iconst_1
        //   87: invokevirtual 128	java/util/regex/Matcher:group	(I)Ljava/lang/String;
        //   90: aload_0
        //   91: getfield 29	com/facebook/react/devsupport/BundleDownloader$1:val$outputFile	Ljava/io/File;
        //   94: aload_0
        //   95: getfield 31	com/facebook/react/devsupport/BundleDownloader$1:val$bundleInfo	Lcom/facebook/react/devsupport/BundleDownloader$BundleInfo;
        //   98: aload_0
        //   99: getfield 33	com/facebook/react/devsupport/BundleDownloader$1:val$clientType	Lcom/facebook/react/devsupport/BundleDeltaClient$ClientType;
        //   102: aload_0
        //   103: getfield 27	com/facebook/react/devsupport/BundleDownloader$1:val$callback	Lcom/facebook/react/devsupport/interfaces/DevBundleDownloadListener;
        //   106: invokestatic 132	com/facebook/react/devsupport/BundleDownloader:access$100	(Lcom/facebook/react/devsupport/BundleDownloader;Ljava/lang/String;Lokhttp3/Response;Ljava/lang/String;Ljava/io/File;Lcom/facebook/react/devsupport/BundleDownloader$BundleInfo;Lcom/facebook/react/devsupport/BundleDeltaClient$ClientType;Lcom/facebook/react/devsupport/interfaces/DevBundleDownloadListener;)V
        //   109: goto +48 -> 157
        //   112: aload_0
        //   113: getfield 25	com/facebook/react/devsupport/BundleDownloader$1:this$0	Lcom/facebook/react/devsupport/BundleDownloader;
        //   116: aload_1
        //   117: aload_2
        //   118: invokevirtual 136	okhttp3/Response:code	()I
        //   121: aload_2
        //   122: invokevirtual 140	okhttp3/Response:headers	()Lokhttp3/Headers;
        //   125: aload_2
        //   126: invokevirtual 144	okhttp3/Response:body	()Lokhttp3/ResponseBody;
        //   129: invokevirtual 150	okhttp3/ResponseBody:source	()Lokio/BufferedSource;
        //   132: checkcast 152	okio/Source
        //   135: invokestatic 158	okio/Okio:buffer	(Lokio/Source;)Lokio/BufferedSource;
        //   138: aload_0
        //   139: getfield 29	com/facebook/react/devsupport/BundleDownloader$1:val$outputFile	Ljava/io/File;
        //   142: aload_0
        //   143: getfield 31	com/facebook/react/devsupport/BundleDownloader$1:val$bundleInfo	Lcom/facebook/react/devsupport/BundleDownloader$BundleInfo;
        //   146: aload_0
        //   147: getfield 33	com/facebook/react/devsupport/BundleDownloader$1:val$clientType	Lcom/facebook/react/devsupport/BundleDeltaClient$ClientType;
        //   150: aload_0
        //   151: getfield 27	com/facebook/react/devsupport/BundleDownloader$1:val$callback	Lcom/facebook/react/devsupport/interfaces/DevBundleDownloadListener;
        //   154: invokestatic 162	com/facebook/react/devsupport/BundleDownloader:access$200	(Lcom/facebook/react/devsupport/BundleDownloader;Ljava/lang/String;ILokhttp3/Headers;Lokio/BufferedSource;Ljava/io/File;Lcom/facebook/react/devsupport/BundleDownloader$BundleInfo;Lcom/facebook/react/devsupport/BundleDeltaClient$ClientType;Lcom/facebook/react/devsupport/interfaces/DevBundleDownloadListener;)V
        //   157: aload_2
        //   158: ifnull +58 -> 216
        //   161: aload_2
        //   162: invokevirtual 165	okhttp3/Response:close	()V
        //   165: return
        //   166: astore_1
        //   167: goto +8 -> 175
        //   170: astore 4
        //   172: aload 4
        //   174: athrow
        //   175: aload_2
        //   176: ifnull +29 -> 205
        //   179: aload 4
        //   181: ifnull +20 -> 201
        //   184: aload_2
        //   185: invokevirtual 165	okhttp3/Response:close	()V
        //   188: goto +17 -> 205
        //   191: astore_2
        //   192: aload 4
        //   194: aload_2
        //   195: invokevirtual 169	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
        //   198: goto +7 -> 205
        //   201: aload_2
        //   202: invokevirtual 165	okhttp3/Response:close	()V
        //   205: aload_1
        //   206: athrow
        //   207: aload_0
        //   208: getfield 25	com/facebook/react/devsupport/BundleDownloader$1:this$0	Lcom/facebook/react/devsupport/BundleDownloader;
        //   211: aconst_null
        //   212: invokestatic 53	com/facebook/react/devsupport/BundleDownloader:access$002	(Lcom/facebook/react/devsupport/BundleDownloader;Lokhttp3/Call;)Lokhttp3/Call;
        //   215: pop
        //   216: return
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	217	0	this	1
        //   0	217	1	paramAnonymousCall	Call
        //   0	217	2	paramAnonymousResponse	Response
        //   73	2	3	bool	boolean
        //   54	31	4	localObject	Object
        //   170	23	4	localThrowable	Throwable
        // Exception table:
        //   from	to	target	type
        //   172	175	166	java/lang/Throwable
        //   68	74	170	java/lang/Throwable
        //   78	109	170	java/lang/Throwable
        //   112	157	170	java/lang/Throwable
        //   184	188	191	java/lang/Throwable
      }
    });
  }
  
  public static class BundleInfo
  {
    @Nullable
    private String mDeltaClientName;
    private int mFilesChangedCount;
    @Nullable
    private String mUrl;
    
    public BundleInfo() {}
    
    public static BundleInfo fromJSONString(String paramString)
    {
      if (paramString == null) {
        return null;
      }
      BundleInfo localBundleInfo = new BundleInfo();
      try
      {
        paramString = new JSONObject(paramString);
        String str = paramString.getString("deltaClient");
        mDeltaClientName = str;
        str = paramString.getString("url");
        mUrl = str;
        int i = paramString.getInt("filesChangedCount");
        mFilesChangedCount = i;
        return localBundleInfo;
      }
      catch (JSONException paramString)
      {
        Log.e("BundleDownloader", "Invalid bundle info: ", paramString);
      }
      return null;
    }
    
    public String getDeltaClient()
    {
      return mDeltaClientName;
    }
    
    public int getFilesChangedCount()
    {
      return mFilesChangedCount;
    }
    
    public String getUrl()
    {
      if (mUrl != null) {
        return mUrl;
      }
      return "unknown";
    }
    
    public String toJSONString()
    {
      JSONObject localJSONObject = new JSONObject();
      String str = mDeltaClientName;
      try
      {
        localJSONObject.put("deltaClient", str);
        str = mUrl;
        localJSONObject.put("url", str);
        int i = mFilesChangedCount;
        localJSONObject.put("filesChangedCount", i);
        return localJSONObject.toString();
      }
      catch (JSONException localJSONException)
      {
        Log.e("BundleDownloader", "Can't serialize bundle info: ", localJSONException);
      }
      return null;
    }
  }
}
