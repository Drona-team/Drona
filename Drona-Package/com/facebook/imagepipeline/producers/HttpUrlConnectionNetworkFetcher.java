package com.facebook.imagepipeline.producers;

import android.net.Uri;
import com.facebook.common.time.MonotonicClock;
import com.facebook.common.time.RealtimeSinceBootClock;
import com.facebook.common.util.UriUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HttpUrlConnectionNetworkFetcher
  extends BaseNetworkFetcher<HttpUrlConnectionNetworkFetchState>
{
  private static final String FETCH_TIME = "fetch_time";
  public static final int HTTP_DEFAULT_TIMEOUT = 30000;
  public static final int HTTP_PERMANENT_REDIRECT = 308;
  public static final int HTTP_TEMPORARY_REDIRECT = 307;
  private static final String IMAGE_SIZE = "image_size";
  private static final int MAX_REDIRECTS = 5;
  private static final int NUM_NETWORK_THREADS = 3;
  private static final String QUEUE_TIME = "queue_time";
  private static final String TOTAL_TIME = "total_time";
  private final ExecutorService mExecutorService = Executors.newFixedThreadPool(3);
  private int mHttpConnectionTimeout;
  private final MonotonicClock mMonotonicClock;
  
  public HttpUrlConnectionNetworkFetcher()
  {
    this(RealtimeSinceBootClock.notNull());
  }
  
  public HttpUrlConnectionNetworkFetcher(int paramInt)
  {
    this(RealtimeSinceBootClock.notNull());
    mHttpConnectionTimeout = paramInt;
  }
  
  HttpUrlConnectionNetworkFetcher(MonotonicClock paramMonotonicClock)
  {
    mMonotonicClock = paramMonotonicClock;
  }
  
  private HttpURLConnection downloadFrom(Uri paramUri, int paramInt)
    throws IOException
  {
    Object localObject = openConnectionTo(paramUri);
    ((HttpURLConnection)localObject).setConnectTimeout(mHttpConnectionTimeout);
    int i = ((HttpURLConnection)localObject).getResponseCode();
    if (isHttpSuccess(i)) {
      return localObject;
    }
    if (isHttpRedirect(i))
    {
      String str = ((HttpURLConnection)localObject).getHeaderField("Location");
      ((HttpURLConnection)localObject).disconnect();
      if (str == null) {
        localObject = null;
      } else {
        localObject = Uri.parse(str);
      }
      str = paramUri.getScheme();
      if ((paramInt > 0) && (localObject != null) && (!((Uri)localObject).getScheme().equals(str))) {
        return downloadFrom((Uri)localObject, paramInt - 1);
      }
      if (paramInt == 0) {
        paramUri = error("URL %s follows too many redirects", new Object[] { paramUri.toString() });
      } else {
        paramUri = error("URL %s returned %d without a valid redirect", new Object[] { paramUri.toString(), Integer.valueOf(i) });
      }
      throw new IOException(paramUri);
    }
    ((HttpURLConnection)localObject).disconnect();
    throw new IOException(String.format("Image URL %s returned HTTP code %d", new Object[] { paramUri.toString(), Integer.valueOf(i) }));
  }
  
  private static String error(String paramString, Object... paramVarArgs)
  {
    return String.format(Locale.getDefault(), paramString, paramVarArgs);
  }
  
  private static boolean isHttpRedirect(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      break;
    case 304: 
    case 305: 
    case 306: 
      return false;
    }
    return true;
  }
  
  private static boolean isHttpSuccess(int paramInt)
  {
    return (paramInt >= 200) && (paramInt < 300);
  }
  
  static HttpURLConnection openConnectionTo(Uri paramUri)
    throws IOException
  {
    return (HttpURLConnection)UriUtil.uriToUrl(paramUri).openConnection();
  }
  
  public HttpUrlConnectionNetworkFetchState createFetchState(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    return new HttpUrlConnectionNetworkFetchState(paramConsumer, paramProducerContext);
  }
  
  public void fetch(final HttpUrlConnectionNetworkFetchState paramHttpUrlConnectionNetworkFetchState, final NetworkFetcher.Callback paramCallback)
  {
    HttpUrlConnectionNetworkFetchState.access$002(paramHttpUrlConnectionNetworkFetchState, mMonotonicClock.now());
    final Future localFuture = mExecutorService.submit(new Runnable()
    {
      public void run()
      {
        fetchSync(paramHttpUrlConnectionNetworkFetchState, paramCallback);
      }
    });
    paramHttpUrlConnectionNetworkFetchState.getContext().addCallbacks(new BaseProducerContextCallbacks()
    {
      public void onCancellationRequested()
      {
        if (localFuture.cancel(false)) {
          paramCallback.onCancellation();
        }
      }
    });
  }
  
  void fetchSync(HttpUrlConnectionNetworkFetchState paramHttpUrlConnectionNetworkFetchState, NetworkFetcher.Callback paramCallback)
  {
    Object localObject5 = null;
    Object localObject2 = null;
    Object localObject4 = null;
    for (;;)
    {
      Object localObject1;
      Object localObject3;
      try
      {
        localHttpURLConnection = downloadFrom(paramHttpUrlConnectionNetworkFetchState.getUri(), 5);
        localObject1 = localHttpURLConnection;
        localMonotonicClock = mMonotonicClock;
        localObject2 = localObject5;
        localObject3 = localObject1;
      }
      catch (Throwable paramHttpUrlConnectionNetworkFetchState)
      {
        HttpURLConnection localHttpURLConnection;
        MonotonicClock localMonotonicClock;
        localObject1 = null;
      }
      catch (IOException localIOException3)
      {
        localObject1 = null;
        paramHttpUrlConnectionNetworkFetchState = localObject4;
      }
      try
      {
        try
        {
          HttpUrlConnectionNetworkFetchState.access$102(paramHttpUrlConnectionNetworkFetchState, localMonotonicClock.now());
          if (localHttpURLConnection != null)
          {
            localObject2 = localObject5;
            localObject3 = localObject1;
            paramHttpUrlConnectionNetworkFetchState = localHttpURLConnection.getInputStream();
            localObject2 = paramHttpUrlConnectionNetworkFetchState;
            try
            {
              paramCallback.onResponse(paramHttpUrlConnectionNetworkFetchState, -1);
            }
            catch (Throwable paramCallback)
            {
              localObject2 = paramHttpUrlConnectionNetworkFetchState;
              paramHttpUrlConnectionNetworkFetchState = paramCallback;
              break label177;
            }
            catch (IOException localIOException1)
            {
              break label139;
            }
          }
          else
          {
            localObject2 = null;
          }
          if (localObject2 == null) {}
        }
        catch (IOException localIOException2)
        {
          paramHttpUrlConnectionNetworkFetchState = localObject4;
        }
      }
      catch (Throwable paramHttpUrlConnectionNetworkFetchState)
      {
        label139:
        label168:
        localObject1 = localObject3;
        if (localObject2 != null) {}
        try
        {
          ((InputStream)localObject2).close();
        }
        catch (IOException paramCallback)
        {
          for (;;) {}
        }
        if (localObject1 != null) {
          ((HttpURLConnection)localObject1).disconnect();
        }
        throw paramHttpUrlConnectionNetworkFetchState;
      }
      try
      {
        ((InputStream)localObject2).close();
      }
      catch (IOException paramHttpUrlConnectionNetworkFetchState) {}
    }
    if (localIOException1 != null)
    {
      break label168;
      localObject2 = paramHttpUrlConnectionNetworkFetchState;
      localObject3 = localObject1;
      paramCallback.onFailure(localIOException3);
      if (paramHttpUrlConnectionNetworkFetchState != null) {}
      try
      {
        paramHttpUrlConnectionNetworkFetchState.close();
      }
      catch (IOException paramHttpUrlConnectionNetworkFetchState)
      {
        for (;;) {}
      }
      if (localObject1 != null)
      {
        ((HttpURLConnection)localObject1).disconnect();
        return;
      }
    }
    label177:
  }
  
  public Map getExtraMap(HttpUrlConnectionNetworkFetchState paramHttpUrlConnectionNetworkFetchState, int paramInt)
  {
    HashMap localHashMap = new HashMap(4);
    localHashMap.put("queue_time", Long.toString(responseTime - submitTime));
    localHashMap.put("fetch_time", Long.toString(fetchCompleteTime - responseTime));
    localHashMap.put("total_time", Long.toString(fetchCompleteTime - submitTime));
    localHashMap.put("image_size", Integer.toString(paramInt));
    return localHashMap;
  }
  
  public void onFetchCompletion(HttpUrlConnectionNetworkFetchState paramHttpUrlConnectionNetworkFetchState, int paramInt)
  {
    HttpUrlConnectionNetworkFetchState.access$202(paramHttpUrlConnectionNetworkFetchState, mMonotonicClock.now());
  }
  
  public static class HttpUrlConnectionNetworkFetchState
    extends FetchState
  {
    private long fetchCompleteTime;
    private long responseTime;
    private long submitTime;
    
    public HttpUrlConnectionNetworkFetchState(Consumer paramConsumer, ProducerContext paramProducerContext)
    {
      super(paramProducerContext);
    }
  }
}
