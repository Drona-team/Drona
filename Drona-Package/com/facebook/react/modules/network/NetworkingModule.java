package com.facebook.react.modules.network;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.GuardedAsyncTask;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.StandardCharsets;
import com.facebook.react.common.network.OkHttpCallUtil;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.Headers.Builder;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.MultipartBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;
import okio.ByteString;
import okio.GzipSource;
import okio.Okio;
import okio.Source;

@ReactModule(name="Networking")
public final class NetworkingModule
  extends ReactContextBaseJavaModule
{
  private static final int CHUNK_TIMEOUT_NS = 100000000;
  private static final String CONTENT_ENCODING_HEADER_NAME = "content-encoding";
  private static final String CONTENT_TYPE_HEADER_NAME = "content-type";
  private static final int MAX_CHUNK_SIZE_BETWEEN_FLUSHES = 8192;
  public static final String NAME = "Networking";
  private static final String PAGE_KEY = "NetworkingModule";
  private static final String REQUEST_BODY_KEY_BASE64 = "base64";
  private static final String REQUEST_BODY_KEY_FORMDATA = "formData";
  private static final String REQUEST_BODY_KEY_STRING = "string";
  private static final String REQUEST_BODY_KEY_URI = "uri";
  private static final String USER_AGENT_HEADER_NAME = "user-agent";
  @Nullable
  private static CustomClientBuilder customClientBuilder;
  private final OkHttpClient mClient;
  private final ForwardingCookieHandler mCookieHandler;
  private final CookieJarContainer mCookieJarContainer;
  @Nullable
  private final String mDefaultUserAgent;
  private final List<RequestBodyHandler> mRequestBodyHandlers = new ArrayList();
  private final Set<Integer> mRequestIds;
  private final List<ResponseHandler> mResponseHandlers = new ArrayList();
  private boolean mShuttingDown;
  private final List<UriHandler> mUriHandlers = new ArrayList();
  
  public NetworkingModule(ReactApplicationContext paramReactApplicationContext)
  {
    this(paramReactApplicationContext, null, OkHttpClientProvider.createClient(paramReactApplicationContext), null);
  }
  
  public NetworkingModule(ReactApplicationContext paramReactApplicationContext, String paramString)
  {
    this(paramReactApplicationContext, paramString, OkHttpClientProvider.createClient(paramReactApplicationContext), null);
  }
  
  NetworkingModule(ReactApplicationContext paramReactApplicationContext, String paramString, OkHttpClient paramOkHttpClient)
  {
    this(paramReactApplicationContext, paramString, paramOkHttpClient, null);
  }
  
  NetworkingModule(ReactApplicationContext paramReactApplicationContext, String paramString, OkHttpClient paramOkHttpClient, List paramList)
  {
    super(paramReactApplicationContext);
    OkHttpClient localOkHttpClient = paramOkHttpClient;
    if (paramList != null)
    {
      paramOkHttpClient = paramOkHttpClient.newBuilder();
      paramList = paramList.iterator();
      while (paramList.hasNext()) {
        paramOkHttpClient.addNetworkInterceptor(((NetworkInterceptorCreator)paramList.next()).create());
      }
      localOkHttpClient = paramOkHttpClient.build();
    }
    mClient = localOkHttpClient;
    mCookieHandler = new ForwardingCookieHandler(paramReactApplicationContext);
    mCookieJarContainer = ((CookieJarContainer)mClient.cookieJar());
    mShuttingDown = false;
    mDefaultUserAgent = paramString;
    mRequestIds = new HashSet();
  }
  
  public NetworkingModule(ReactApplicationContext paramReactApplicationContext, List paramList)
  {
    this(paramReactApplicationContext, null, OkHttpClientProvider.createClient(paramReactApplicationContext), paramList);
  }
  
  private void addRequest(int paramInt)
  {
    try
    {
      mRequestIds.add(Integer.valueOf(paramInt));
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private static void applyCustomBuilder(OkHttpClient.Builder paramBuilder)
  {
    if (customClientBuilder != null) {
      customClientBuilder.apply(paramBuilder);
    }
  }
  
  private void cancelAllRequests()
  {
    try
    {
      Iterator localIterator = mRequestIds.iterator();
      while (localIterator.hasNext()) {
        cancelRequest(((Integer)localIterator.next()).intValue());
      }
      mRequestIds.clear();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private void cancelRequest(final int paramInt)
  {
    new GuardedAsyncTask(getReactApplicationContext())
    {
      protected void doInBackgroundGuarded(Void... paramAnonymousVarArgs)
      {
        OkHttpCallUtil.cancelTag(mClient, Integer.valueOf(paramInt));
      }
    }.execute(new Void[0]);
  }
  
  private MultipartBody.Builder constructMultipartBody(ReadableArray paramReadableArray, String paramString, int paramInt)
  {
    DeviceEventManagerModule.RCTDeviceEventEmitter localRCTDeviceEventEmitter = getEventEmitter();
    MultipartBody.Builder localBuilder = new MultipartBody.Builder();
    localBuilder.setType(MediaType.parse(paramString));
    int j = paramReadableArray.size();
    int i = 0;
    while (i < j)
    {
      Object localObject3 = paramReadableArray.getMap(i);
      paramString = extractHeaders(((ReadableMap)localObject3).getArray("headers"), null);
      Object localObject1 = paramString;
      if (paramString == null)
      {
        ResponseUtil.onRequestError(localRCTDeviceEventEmitter, paramInt, "Missing or invalid header format for FormData part.", null);
        return null;
      }
      Object localObject2 = paramString.get("content-type");
      if (localObject2 != null)
      {
        localObject1 = MediaType.parse((String)localObject2);
        localObject2 = paramString.newBuilder().removeAll("content-type").build();
        paramString = (String)localObject1;
        localObject1 = localObject2;
      }
      else
      {
        paramString = null;
      }
      if (((ReadableMap)localObject3).hasKey("string"))
      {
        localBuilder.addPart((Headers)localObject1, RequestBody.create(paramString, ((ReadableMap)localObject3).getString("string")));
      }
      else if (((ReadableMap)localObject3).hasKey("uri"))
      {
        if (paramString == null)
        {
          ResponseUtil.onRequestError(localRCTDeviceEventEmitter, paramInt, "Binary FormData part needs a content-type header.", null);
          return null;
        }
        localObject2 = ((ReadableMap)localObject3).getString("uri");
        localObject3 = RequestBodyUtil.getFileInputStream(getReactApplicationContext(), (String)localObject2);
        if (localObject3 == null)
        {
          paramReadableArray = new StringBuilder();
          paramReadableArray.append("Could not retrieve file for uri ");
          paramReadableArray.append((String)localObject2);
          ResponseUtil.onRequestError(localRCTDeviceEventEmitter, paramInt, paramReadableArray.toString(), null);
          return null;
        }
        localBuilder.addPart((Headers)localObject1, RequestBodyUtil.create(paramString, (InputStream)localObject3));
      }
      else
      {
        ResponseUtil.onRequestError(localRCTDeviceEventEmitter, paramInt, "Unrecognized FormData part.", null);
      }
      i += 1;
    }
    return localBuilder;
  }
  
  private Headers extractHeaders(ReadableArray paramReadableArray, ReadableMap paramReadableMap)
  {
    if (paramReadableArray == null) {
      return null;
    }
    Headers.Builder localBuilder = new Headers.Builder();
    int k = paramReadableArray.size();
    int j = 0;
    int i = 0;
    while (i < k)
    {
      Object localObject = paramReadableArray.getArray(i);
      if (localObject != null)
      {
        if (((ReadableArray)localObject).size() != 2) {
          return null;
        }
        String str = HeaderUtil.stripHeaderName(((ReadableArray)localObject).getString(0));
        localObject = HeaderUtil.stripHeaderValue(((ReadableArray)localObject).getString(1));
        if (str != null)
        {
          if (localObject == null) {
            return null;
          }
          localBuilder.add(str, (String)localObject);
          i += 1;
          continue;
        }
      }
      return null;
    }
    if ((localBuilder.get("user-agent") == null) && (mDefaultUserAgent != null)) {
      localBuilder.add("user-agent", mDefaultUserAgent);
    }
    i = j;
    if (paramReadableMap != null)
    {
      i = j;
      if (paramReadableMap.hasKey("string")) {
        i = 1;
      }
    }
    if (i == 0) {
      localBuilder.removeAll("content-encoding");
    }
    return localBuilder.build();
  }
  
  private DeviceEventManagerModule.RCTDeviceEventEmitter getEventEmitter()
  {
    return (DeviceEventManagerModule.RCTDeviceEventEmitter)getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
  }
  
  private void readWithProgress(DeviceEventManagerModule.RCTDeviceEventEmitter paramRCTDeviceEventEmitter, int paramInt, ResponseBody paramResponseBody)
    throws IOException
  {
    long l1 = -1L;
    for (;;)
    {
      try
      {
        localObject = (ProgressResponseBody)paramResponseBody;
        l2 = ((ProgressResponseBody)localObject).totalBytesRead();
      }
      catch (ClassCastException localClassCastException1)
      {
        Object localObject;
        long l2;
        long l3;
        byte[] arrayOfByte;
        int i;
        continue;
      }
      try
      {
        l3 = ((ProgressResponseBody)localObject).contentLength();
        l1 = l3;
      }
      catch (ClassCastException localClassCastException2) {}
    }
    l2 = -1L;
    if (paramResponseBody.contentType() == null) {
      localObject = StandardCharsets.UTF_8;
    } else {
      localObject = paramResponseBody.contentType().charset(StandardCharsets.UTF_8);
    }
    localObject = new ProgressiveStringDecoder((Charset)localObject);
    paramResponseBody = paramResponseBody.byteStream();
    try
    {
      arrayOfByte = new byte['?'];
      for (;;)
      {
        i = paramResponseBody.read(arrayOfByte);
        if (i == -1) {
          break;
        }
        ResponseUtil.onIncrementalDataReceived(paramRCTDeviceEventEmitter, paramInt, ((ProgressiveStringDecoder)localObject).decodeNext(arrayOfByte, i), l2, l1);
      }
      paramResponseBody.close();
      return;
    }
    catch (Throwable paramRCTDeviceEventEmitter)
    {
      paramResponseBody.close();
      throw paramRCTDeviceEventEmitter;
    }
  }
  
  private void removeRequest(int paramInt)
  {
    try
    {
      mRequestIds.remove(Integer.valueOf(paramInt));
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public static void setCustomClientBuilder(CustomClientBuilder paramCustomClientBuilder)
  {
    customClientBuilder = paramCustomClientBuilder;
  }
  
  private static boolean shouldDispatch(long paramLong1, long paramLong2)
  {
    return paramLong2 + 100000000L < paramLong1;
  }
  
  private static WritableMap translateHeaders(Headers paramHeaders)
  {
    WritableMap localWritableMap = Arguments.createMap();
    int i = 0;
    while (i < paramHeaders.size())
    {
      String str = paramHeaders.name(i);
      if (localWritableMap.hasKey(str))
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(localWritableMap.getString(str));
        localStringBuilder.append(", ");
        localStringBuilder.append(paramHeaders.value(i));
        localWritableMap.putString(str, localStringBuilder.toString());
      }
      else
      {
        localWritableMap.putString(str, paramHeaders.value(i));
      }
      i += 1;
    }
    return localWritableMap;
  }
  
  private RequestBody wrapRequestBodyWithProgressEmitter(RequestBody paramRequestBody, final DeviceEventManagerModule.RCTDeviceEventEmitter paramRCTDeviceEventEmitter, final int paramInt)
  {
    if (paramRequestBody == null) {
      return null;
    }
    RequestBodyUtil.createProgressRequest(paramRequestBody, new ProgressListener()
    {
      long last = System.nanoTime();
      
      public void onProgress(long paramAnonymousLong1, long paramAnonymousLong2, boolean paramAnonymousBoolean)
      {
        long l = System.nanoTime();
        if ((paramAnonymousBoolean) || (NetworkingModule.shouldDispatch(l, last)))
        {
          ResponseUtil.onDataSend(paramRCTDeviceEventEmitter, paramInt, paramAnonymousLong1, paramAnonymousLong2);
          last = l;
        }
      }
    });
  }
  
  public void abortRequest(int paramInt)
  {
    cancelRequest(paramInt);
    removeRequest(paramInt);
  }
  
  public void addRequestBodyHandler(RequestBodyHandler paramRequestBodyHandler)
  {
    mRequestBodyHandlers.add(paramRequestBodyHandler);
  }
  
  public void addResponseHandler(ResponseHandler paramResponseHandler)
  {
    mResponseHandlers.add(paramResponseHandler);
  }
  
  public void addUriHandler(UriHandler paramUriHandler)
  {
    mUriHandlers.add(paramUriHandler);
  }
  
  public void clearCookies(com.facebook.react.bridge.Callback paramCallback)
  {
    mCookieHandler.clearCookies(paramCallback);
  }
  
  public String getName()
  {
    return "Networking";
  }
  
  public void initialize()
  {
    mCookieJarContainer.setCookieJar((CookieJar)new JavaNetCookieJar(mCookieHandler));
  }
  
  public void onCatalystInstanceDestroy()
  {
    mShuttingDown = true;
    cancelAllRequests();
    mCookieHandler.destroy();
    mCookieJarContainer.removeCookieJar();
    mRequestBodyHandlers.clear();
    mResponseHandlers.clear();
    mUriHandlers.clear();
  }
  
  public void removeRequestBodyHandler(RequestBodyHandler paramRequestBodyHandler)
  {
    mRequestBodyHandlers.remove(paramRequestBodyHandler);
  }
  
  public void removeResponseHandler(ResponseHandler paramResponseHandler)
  {
    mResponseHandlers.remove(paramResponseHandler);
  }
  
  public void removeUriHandler(UriHandler paramUriHandler)
  {
    mUriHandlers.remove(paramUriHandler);
  }
  
  public void sendRequest(String paramString1, String paramString2, int paramInt1, ReadableArray paramReadableArray, ReadableMap paramReadableMap, String paramString3, boolean paramBoolean1, int paramInt2, boolean paramBoolean2)
  {
    try
    {
      sendRequestInternal(paramString1, paramString2, paramInt1, paramReadableArray, paramReadableMap, paramString3, paramBoolean1, paramInt2, paramBoolean2);
      return;
    }
    catch (Throwable paramString1)
    {
      paramReadableArray = new StringBuilder();
      paramReadableArray.append("Failed to send url request: ");
      paramReadableArray.append(paramString2);
      FLog.e("NetworkingModule", paramReadableArray.toString(), paramString1);
      ResponseUtil.onRequestError(getEventEmitter(), paramInt1, paramString1.getMessage(), paramString1);
    }
  }
  
  public void sendRequestInternal(String paramString1, String paramString2, final int paramInt1, ReadableArray paramReadableArray, ReadableMap paramReadableMap, final String paramString3, final boolean paramBoolean1, int paramInt2, boolean paramBoolean2)
  {
    final DeviceEventManagerModule.RCTDeviceEventEmitter localRCTDeviceEventEmitter = getEventEmitter();
    try
    {
      Object localObject1 = Uri.parse(paramString2);
      Object localObject2 = mUriHandlers;
      localObject2 = ((List)localObject2).iterator();
      boolean bool;
      Object localObject3;
      do
      {
        bool = ((Iterator)localObject2).hasNext();
        if (!bool) {
          break;
        }
        localObject3 = ((Iterator)localObject2).next();
        localObject3 = (UriHandler)localObject3;
        bool = ((UriHandler)localObject3).supports((Uri)localObject1, paramString3);
      } while (!bool);
      ResponseUtil.onDataReceived(localRCTDeviceEventEmitter, paramInt1, ((UriHandler)localObject3).fetch((Uri)localObject1));
      ResponseUtil.onRequestSuccess(localRCTDeviceEventEmitter, paramInt1);
      return;
      try
      {
        localObject2 = new Request.Builder().url(paramString2);
        if (paramInt1 != 0) {
          ((Request.Builder)localObject2).tag(Integer.valueOf(paramInt1));
        }
        paramString2 = mClient.newBuilder();
        applyCustomBuilder(paramString2);
        if (!paramBoolean2) {
          paramString2.cookieJar(CookieJar.NO_COOKIES);
        }
        if (paramBoolean1) {
          paramString2.addNetworkInterceptor(new Interceptor()
          {
            public Response intercept(Interceptor.Chain paramAnonymousChain)
              throws IOException
            {
              paramAnonymousChain = paramAnonymousChain.proceed(paramAnonymousChain.request());
              ProgressResponseBody localProgressResponseBody = new ProgressResponseBody(paramAnonymousChain.body(), new ProgressListener()
              {
                long last = System.nanoTime();
                
                public void onProgress(long paramAnonymous2Long1, long paramAnonymous2Long2, boolean paramAnonymous2Boolean)
                {
                  long l = System.nanoTime();
                  if ((!paramAnonymous2Boolean) && (!NetworkingModule.shouldDispatch(l, last))) {
                    return;
                  }
                  if (val$responseType.equals("text")) {
                    return;
                  }
                  ResponseUtil.onDataReceivedProgress(val$eventEmitter, val$requestId, paramAnonymous2Long1, paramAnonymous2Long2);
                  last = l;
                }
              });
              return paramAnonymousChain.newBuilder().body(localProgressResponseBody).build();
            }
          });
        }
        if (paramInt2 != mClient.connectTimeoutMillis()) {
          paramString2.connectTimeout(paramInt2, TimeUnit.MILLISECONDS);
        }
        localObject3 = paramString2.build();
        paramReadableArray = extractHeaders(paramReadableArray, paramReadableMap);
        if (paramReadableArray == null)
        {
          ResponseUtil.onRequestError(localRCTDeviceEventEmitter, paramInt1, "Unrecognized headers format", null);
          return;
        }
        localObject1 = paramReadableArray.get("content-type");
        paramString2 = (String)localObject1;
        String str = paramReadableArray.get("content-encoding");
        ((Request.Builder)localObject2).headers(paramReadableArray);
        if (paramReadableMap != null)
        {
          Iterator localIterator = mRequestBodyHandlers.iterator();
          while (localIterator.hasNext())
          {
            paramReadableArray = (RequestBodyHandler)localIterator.next();
            if (paramReadableArray.supports(paramReadableMap)) {
              break label312;
            }
          }
        }
        paramReadableArray = null;
        label312:
        if ((paramReadableMap != null) && (!paramString1.toLowerCase().equals("get")) && (!paramString1.toLowerCase().equals("head")))
        {
          if (paramReadableArray != null)
          {
            paramString2 = paramReadableArray.toRequestBody(paramReadableMap, (String)localObject1);
          }
          else if (paramReadableMap.hasKey("string"))
          {
            if (localObject1 == null)
            {
              ResponseUtil.onRequestError(localRCTDeviceEventEmitter, paramInt1, "Payload is set but no content-type header specified", null);
              return;
            }
            paramReadableArray = paramReadableMap.getString("string");
            paramReadableMap = MediaType.parse((String)localObject1);
            if (RequestBodyUtil.isGzipEncoding(str))
            {
              paramReadableArray = RequestBodyUtil.createGzip(paramReadableMap, paramReadableArray);
              paramString2 = paramReadableArray;
              if (paramReadableArray == null) {
                ResponseUtil.onRequestError(localRCTDeviceEventEmitter, paramInt1, "Failed to gzip request body", null);
              }
            }
            else
            {
              if (paramReadableMap == null) {
                paramString2 = StandardCharsets.UTF_8;
              } else {
                paramString2 = paramReadableMap.charset(StandardCharsets.UTF_8);
              }
              paramString2 = RequestBody.create(paramReadableMap, paramReadableArray.getBytes(paramString2));
            }
          }
          else if (paramReadableMap.hasKey("base64"))
          {
            if (localObject1 == null)
            {
              ResponseUtil.onRequestError(localRCTDeviceEventEmitter, paramInt1, "Payload is set but no content-type header specified", null);
              return;
            }
            paramString2 = paramReadableMap.getString("base64");
            paramString2 = RequestBody.create(MediaType.parse((String)localObject1), ByteString.decodeBase64(paramString2));
          }
          else if (paramReadableMap.hasKey("uri"))
          {
            if (localObject1 == null)
            {
              ResponseUtil.onRequestError(localRCTDeviceEventEmitter, paramInt1, "Payload is set but no content-type header specified", null);
              return;
            }
            paramString2 = paramReadableMap.getString("uri");
            paramReadableArray = RequestBodyUtil.getFileInputStream(getReactApplicationContext(), paramString2);
            if (paramReadableArray == null)
            {
              paramString1 = new StringBuilder();
              paramString1.append("Could not retrieve file for uri ");
              paramString1.append(paramString2);
              ResponseUtil.onRequestError(localRCTDeviceEventEmitter, paramInt1, paramString1.toString(), null);
              return;
            }
            paramString2 = RequestBodyUtil.create(MediaType.parse((String)localObject1), paramReadableArray);
          }
          else if (paramReadableMap.hasKey("formData"))
          {
            if (localObject1 == null) {
              paramString2 = "multipart/form-data";
            }
            paramString2 = constructMultipartBody(paramReadableMap.getArray("formData"), paramString2, paramInt1);
            if (paramString2 == null) {
              return;
            }
            paramString2 = paramString2.build();
          }
          else
          {
            paramString2 = RequestBodyUtil.getEmptyBody(paramString1);
          }
        }
        else {
          paramString2 = RequestBodyUtil.getEmptyBody(paramString1);
        }
        ((Request.Builder)localObject2).method(paramString1, wrapRequestBodyWithProgressEmitter((RequestBody)paramString2, localRCTDeviceEventEmitter, paramInt1));
        addRequest(paramInt1);
        ((OkHttpClient)localObject3).newCall(((Request.Builder)localObject2).build()).enqueue(new okhttp3.Callback()
        {
          public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException)
          {
            if (mShuttingDown) {
              return;
            }
            NetworkingModule.this.removeRequest(paramInt1);
            if (paramAnonymousIOException.getMessage() != null)
            {
              paramAnonymousCall = paramAnonymousIOException.getMessage();
            }
            else
            {
              paramAnonymousCall = new StringBuilder();
              paramAnonymousCall.append("Error while executing request: ");
              paramAnonymousCall.append(paramAnonymousIOException.getClass().getSimpleName());
              paramAnonymousCall = paramAnonymousCall.toString();
            }
            ResponseUtil.onRequestError(localRCTDeviceEventEmitter, paramInt1, paramAnonymousCall, paramAnonymousIOException);
          }
          
          public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse)
            throws IOException
          {
            if (mShuttingDown) {
              return;
            }
            NetworkingModule.this.removeRequest(paramInt1);
            ResponseUtil.onResponseReceived(localRCTDeviceEventEmitter, paramInt1, paramAnonymousResponse.code(), NetworkingModule.translateHeaders(paramAnonymousResponse.headers()), paramAnonymousResponse.request().url().toString());
            try
            {
              Object localObject2 = paramAnonymousResponse.body();
              paramAnonymousCall = (Call)localObject2;
              boolean bool = "gzip".equalsIgnoreCase(paramAnonymousResponse.header("Content-Encoding"));
              Object localObject1 = paramAnonymousCall;
              if (bool)
              {
                localObject1 = paramAnonymousCall;
                if (localObject2 != null)
                {
                  paramAnonymousCall = ((ResponseBody)localObject2).source();
                  paramAnonymousCall = (Source)paramAnonymousCall;
                  localObject1 = new GzipSource(paramAnonymousCall);
                  paramAnonymousCall = paramAnonymousResponse.header("Content-Type");
                  if (paramAnonymousCall != null) {
                    paramAnonymousCall = MediaType.parse(paramAnonymousCall);
                  } else {
                    paramAnonymousCall = null;
                  }
                  localObject1 = (Source)localObject1;
                  localObject1 = ResponseBody.create(paramAnonymousCall, -1L, Okio.buffer((Source)localObject1));
                }
              }
              paramAnonymousCall = NetworkingModule.this;
              paramAnonymousCall = mResponseHandlers.iterator();
              do
              {
                bool = paramAnonymousCall.hasNext();
                if (!bool) {
                  break;
                }
                localObject2 = paramAnonymousCall.next();
                localObject2 = (NetworkingModule.ResponseHandler)localObject2;
                String str = paramString3;
                bool = ((NetworkingModule.ResponseHandler)localObject2).supports(str);
              } while (!bool);
              paramAnonymousCall = ((NetworkingModule.ResponseHandler)localObject2).toResponseData((ResponseBody)localObject1);
              paramAnonymousResponse = localRCTDeviceEventEmitter;
              int i = paramInt1;
              ResponseUtil.onDataReceived(paramAnonymousResponse, i, paramAnonymousCall);
              paramAnonymousCall = localRCTDeviceEventEmitter;
              i = paramInt1;
              ResponseUtil.onRequestSuccess(paramAnonymousCall, i);
              return;
              if (paramBoolean1)
              {
                paramAnonymousCall = paramString3;
                bool = paramAnonymousCall.equals("text");
                if (bool)
                {
                  paramAnonymousCall = NetworkingModule.this;
                  paramAnonymousResponse = localRCTDeviceEventEmitter;
                  i = paramInt1;
                  paramAnonymousCall.readWithProgress(paramAnonymousResponse, i, (ResponseBody)localObject1);
                  paramAnonymousCall = localRCTDeviceEventEmitter;
                  i = paramInt1;
                  ResponseUtil.onRequestSuccess(paramAnonymousCall, i);
                  return;
                }
              }
              paramAnonymousCall = "";
              localObject2 = paramString3;
              bool = ((String)localObject2).equals("text");
              if (bool)
              {
                try
                {
                  localObject1 = ((ResponseBody)localObject1).string();
                  paramAnonymousCall = (Call)localObject1;
                }
                catch (IOException localIOException)
                {
                  bool = paramAnonymousResponse.request().method().equalsIgnoreCase("HEAD");
                  if (!bool) {
                    break label386;
                  }
                }
                break label439;
                label386:
                paramAnonymousResponse = localRCTDeviceEventEmitter;
                i = paramInt1;
                ResponseUtil.onRequestError(paramAnonymousResponse, i, localIOException.getMessage(), localIOException);
              }
              else
              {
                paramAnonymousResponse = paramString3;
                bool = paramAnonymousResponse.equals("base64");
                if (bool) {
                  paramAnonymousCall = Base64.encodeToString(localIOException.bytes(), 2);
                }
              }
              label439:
              paramAnonymousResponse = localRCTDeviceEventEmitter;
              i = paramInt1;
              ResponseUtil.onDataReceived(paramAnonymousResponse, i, paramAnonymousCall);
              paramAnonymousCall = localRCTDeviceEventEmitter;
              i = paramInt1;
              ResponseUtil.onRequestSuccess(paramAnonymousCall, i);
              return;
            }
            catch (IOException paramAnonymousCall)
            {
              ResponseUtil.onRequestError(localRCTDeviceEventEmitter, paramInt1, ((IOException)paramAnonymousCall).getMessage(), paramAnonymousCall);
            }
          }
        });
        return;
      }
      catch (Exception paramString1)
      {
        ResponseUtil.onRequestError(localRCTDeviceEventEmitter, paramInt1, paramString1.getMessage(), null);
        return;
      }
      return;
    }
    catch (IOException paramString1)
    {
      ResponseUtil.onRequestError(localRCTDeviceEventEmitter, paramInt1, paramString1.getMessage(), paramString1);
    }
  }
  
  public static abstract interface CustomClientBuilder
  {
    public abstract void apply(OkHttpClient.Builder paramBuilder);
  }
  
  public static abstract interface RequestBodyHandler
  {
    public abstract boolean supports(ReadableMap paramReadableMap);
    
    public abstract RequestBody toRequestBody(ReadableMap paramReadableMap, String paramString);
  }
  
  public static abstract interface ResponseHandler
  {
    public abstract boolean supports(String paramString);
    
    public abstract WritableMap toResponseData(ResponseBody paramResponseBody)
      throws IOException;
  }
  
  public static abstract interface UriHandler
  {
    public abstract WritableMap fetch(Uri paramUri)
      throws IOException;
    
    public abstract boolean supports(Uri paramUri, String paramString);
  }
}