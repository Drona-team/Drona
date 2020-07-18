package com.facebook.react.modules.fresco;

import android.net.Uri;
import android.os.SystemClock;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher.OkHttpNetworkFetchState;
import com.facebook.imagepipeline.producers.FetchState;
import com.facebook.imagepipeline.producers.NetworkFetcher.Callback;
import com.facebook.imagepipeline.producers.ProducerContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import okhttp3.CacheControl.Builder;
import okhttp3.Dispatcher;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;

class ReactOkHttpNetworkFetcher
  extends OkHttpNetworkFetcher
{
  private static final String PAGE_KEY = "ReactOkHttpNetworkFetcher";
  private final Executor mCancellationExecutor;
  private final OkHttpClient mOkHttpClient;
  
  public ReactOkHttpNetworkFetcher(OkHttpClient paramOkHttpClient)
  {
    super(paramOkHttpClient);
    mOkHttpClient = paramOkHttpClient;
    mCancellationExecutor = paramOkHttpClient.dispatcher().executorService();
  }
  
  private Map getHeaders(ReadableMap paramReadableMap)
  {
    if (paramReadableMap == null) {
      return null;
    }
    ReadableMapKeySetIterator localReadableMapKeySetIterator = paramReadableMap.keySetIterator();
    HashMap localHashMap = new HashMap();
    while (localReadableMapKeySetIterator.hasNextKey())
    {
      String str = localReadableMapKeySetIterator.nextKey();
      localHashMap.put(str, paramReadableMap.getString(str));
    }
    return localHashMap;
  }
  
  public void fetch(OkHttpNetworkFetcher.OkHttpNetworkFetchState paramOkHttpNetworkFetchState, NetworkFetcher.Callback paramCallback)
  {
    submitTime = SystemClock.elapsedRealtime();
    Uri localUri = paramOkHttpNetworkFetchState.getUri();
    Map localMap1;
    if ((paramOkHttpNetworkFetchState.getContext().getImageRequest() instanceof ReactNetworkImageRequest)) {
      localMap1 = getHeaders(((ReactNetworkImageRequest)paramOkHttpNetworkFetchState.getContext().getImageRequest()).getHeaders());
    } else {
      localMap1 = null;
    }
    Map localMap2 = localMap1;
    if (localMap1 == null) {
      localMap2 = Collections.emptyMap();
    }
    fetchWithRequest(paramOkHttpNetworkFetchState, paramCallback, new Request.Builder().cacheControl(new CacheControl.Builder().noStore().build()).url(localUri.toString()).headers(Headers.of(localMap2)).get().build());
  }
}
