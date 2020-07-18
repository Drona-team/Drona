package com.facebook.imagepipeline.backends.okhttp3;

import android.net.Uri;
import android.os.Looper;
import android.os.SystemClock;
import com.facebook.imagepipeline.common.BytesRange;
import com.facebook.imagepipeline.producers.BaseNetworkFetcher;
import com.facebook.imagepipeline.producers.BaseProducerContextCallbacks;
import com.facebook.imagepipeline.producers.Consumer;
import com.facebook.imagepipeline.producers.FetchState;
import com.facebook.imagepipeline.producers.NetworkFetcher.Callback;
import com.facebook.imagepipeline.producers.ProducerContext;
import com.facebook.imagepipeline.request.ImageRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import okhttp3.CacheControl;
import okhttp3.CacheControl.Builder;
import okhttp3.Call;
import okhttp3.Call.Factory;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpNetworkFetcher
  extends BaseNetworkFetcher<OkHttpNetworkFetchState>
{
  private static final String FETCH_TIME = "fetch_time";
  private static final String IMAGE_SIZE = "image_size";
  private static final String QUEUE_TIME = "queue_time";
  private static final String TOTAL_TIME = "total_time";
  @Nullable
  private final CacheControl mCacheControl;
  private final Call.Factory mCallFactory;
  private Executor mCancellationExecutor;
  
  public OkHttpNetworkFetcher(Call.Factory paramFactory, Executor paramExecutor)
  {
    this(paramFactory, paramExecutor, true);
  }
  
  public OkHttpNetworkFetcher(Call.Factory paramFactory, Executor paramExecutor, boolean paramBoolean)
  {
    mCallFactory = paramFactory;
    mCancellationExecutor = paramExecutor;
    if (paramBoolean) {
      paramFactory = new CacheControl.Builder().noStore().build();
    } else {
      paramFactory = null;
    }
    mCacheControl = paramFactory;
  }
  
  public OkHttpNetworkFetcher(OkHttpClient paramOkHttpClient)
  {
    this(paramOkHttpClient, paramOkHttpClient.dispatcher().executorService());
  }
  
  private void handleException(Call paramCall, Exception paramException, NetworkFetcher.Callback paramCallback)
  {
    if (paramCall.isCanceled())
    {
      paramCallback.onCancellation();
      return;
    }
    paramCallback.onFailure(paramException);
  }
  
  public OkHttpNetworkFetchState createFetchState(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    return new OkHttpNetworkFetchState(paramConsumer, paramProducerContext);
  }
  
  public void fetch(OkHttpNetworkFetchState paramOkHttpNetworkFetchState, NetworkFetcher.Callback paramCallback)
  {
    submitTime = SystemClock.elapsedRealtime();
    Object localObject1 = paramOkHttpNetworkFetchState.getUri();
    try
    {
      localObject1 = new Request.Builder().url(((Uri)localObject1).toString()).get();
      if (mCacheControl != null)
      {
        localObject2 = mCacheControl;
        ((Request.Builder)localObject1).cacheControl((CacheControl)localObject2);
      }
      Object localObject2 = paramOkHttpNetworkFetchState.getContext().getImageRequest().getBytesRange();
      if (localObject2 != null) {
        ((Request.Builder)localObject1).addHeader("Range", ((BytesRange)localObject2).toHttpRangeHeaderValue());
      }
      fetchWithRequest(paramOkHttpNetworkFetchState, paramCallback, ((Request.Builder)localObject1).build());
      return;
    }
    catch (Exception paramOkHttpNetworkFetchState)
    {
      paramCallback.onFailure(paramOkHttpNetworkFetchState);
    }
  }
  
  protected void fetchWithRequest(final OkHttpNetworkFetchState paramOkHttpNetworkFetchState, final NetworkFetcher.Callback paramCallback, final Request paramRequest)
  {
    paramRequest = mCallFactory.newCall(paramRequest);
    paramOkHttpNetworkFetchState.getContext().addCallbacks(new BaseProducerContextCallbacks()
    {
      public void onCancellationRequested()
      {
        if (Looper.myLooper() != Looper.getMainLooper())
        {
          paramRequest.cancel();
          return;
        }
        mCancellationExecutor.execute(new Runnable()
        {
          public void run()
          {
            val$call.cancel();
          }
        });
      }
    });
    paramRequest.enqueue(new Callback()
    {
      public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException)
      {
        OkHttpNetworkFetcher.this.handleException(paramAnonymousCall, paramAnonymousIOException, paramCallback);
      }
      
      public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse)
        throws IOException
      {
        paramOkHttpNetworkFetchStateresponseTime = SystemClock.elapsedRealtime();
        ResponseBody localResponseBody = paramAnonymousResponse.body();
        try
        {
          boolean bool = paramAnonymousResponse.isSuccessful();
          if (!bool)
          {
            localObject1 = OkHttpNetworkFetcher.this;
            localObject2 = new StringBuilder();
            ((StringBuilder)localObject2).append("Unexpected HTTP code ");
            ((StringBuilder)localObject2).append(paramAnonymousResponse);
            paramAnonymousResponse = new IOException(((StringBuilder)localObject2).toString());
            localObject2 = paramCallback;
            ((OkHttpNetworkFetcher)localObject1).handleException(paramAnonymousCall, paramAnonymousResponse, (NetworkFetcher.Callback)localObject2);
            localResponseBody.close();
            return;
          }
          paramAnonymousResponse = BytesRange.fromContentRangeHeader(paramAnonymousResponse.header("Content-Range"));
          if (paramAnonymousResponse != null)
          {
            i = from;
            if (i == 0)
            {
              i = type;
              if (i == Integer.MAX_VALUE) {}
            }
            else
            {
              localObject1 = paramOkHttpNetworkFetchState;
              ((FetchState)localObject1).setResponseBytesRange(paramAnonymousResponse);
              paramAnonymousResponse = paramOkHttpNetworkFetchState;
              paramAnonymousResponse.setOnNewResultStatusFlags(8);
            }
          }
          long l2 = localResponseBody.contentLength();
          long l1 = l2;
          if (l2 < 0L) {
            l1 = 0L;
          }
          paramAnonymousResponse = paramCallback;
          localObject1 = localResponseBody.byteStream();
          int i = (int)l1;
          paramAnonymousResponse.onResponse((InputStream)localObject1, i);
        }
        catch (Throwable paramAnonymousCall)
        {
          break label232;
        }
        catch (Exception paramAnonymousResponse)
        {
          Object localObject1 = OkHttpNetworkFetcher.this;
          Object localObject2 = paramCallback;
          ((OkHttpNetworkFetcher)localObject1).handleException(paramAnonymousCall, (Exception)paramAnonymousResponse, (NetworkFetcher.Callback)localObject2);
        }
        localResponseBody.close();
        return;
        label232:
        localResponseBody.close();
        throw paramAnonymousCall;
      }
    });
  }
  
  public Map getExtraMap(OkHttpNetworkFetchState paramOkHttpNetworkFetchState, int paramInt)
  {
    HashMap localHashMap = new HashMap(4);
    localHashMap.put("queue_time", Long.toString(responseTime - submitTime));
    localHashMap.put("fetch_time", Long.toString(fetchCompleteTime - responseTime));
    localHashMap.put("total_time", Long.toString(fetchCompleteTime - submitTime));
    localHashMap.put("image_size", Integer.toString(paramInt));
    return localHashMap;
  }
  
  public void onFetchCompletion(OkHttpNetworkFetchState paramOkHttpNetworkFetchState, int paramInt)
  {
    fetchCompleteTime = SystemClock.elapsedRealtime();
  }
  
  public static class OkHttpNetworkFetchState
    extends FetchState
  {
    public long fetchCompleteTime;
    public long responseTime;
    public long submitTime;
    
    public OkHttpNetworkFetchState(Consumer paramConsumer, ProducerContext paramProducerContext)
    {
      super(paramProducerContext);
    }
  }
}