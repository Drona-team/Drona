package com.google.android.exoplayer2.http.okhttp;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.upstream.BaseDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource.BaseFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource.RequestProperties;
import com.google.android.exoplayer2.upstream.TransferListener;
import okhttp3.CacheControl;
import okhttp3.Call.Factory;

public final class OkHttpDataSourceFactory
  extends HttpDataSource.BaseFactory
{
  @Nullable
  private final CacheControl cacheControl;
  private final Call.Factory callFactory;
  @Nullable
  private final TransferListener listener;
  @Nullable
  private final String userAgent;
  
  public OkHttpDataSourceFactory(Call.Factory paramFactory, String paramString)
  {
    this(paramFactory, paramString, null, null);
  }
  
  public OkHttpDataSourceFactory(Call.Factory paramFactory, String paramString, TransferListener paramTransferListener)
  {
    this(paramFactory, paramString, paramTransferListener, null);
  }
  
  public OkHttpDataSourceFactory(Call.Factory paramFactory, String paramString, TransferListener paramTransferListener, CacheControl paramCacheControl)
  {
    callFactory = paramFactory;
    userAgent = paramString;
    listener = paramTransferListener;
    cacheControl = paramCacheControl;
  }
  
  public OkHttpDataSourceFactory(Call.Factory paramFactory, String paramString, CacheControl paramCacheControl)
  {
    this(paramFactory, paramString, null, paramCacheControl);
  }
  
  protected OkHttpDataSource createDataSourceInternal(HttpDataSource.RequestProperties paramRequestProperties)
  {
    paramRequestProperties = new OkHttpDataSource(callFactory, userAgent, null, cacheControl, paramRequestProperties);
    if (listener != null) {
      paramRequestProperties.addTransferListener(listener);
    }
    return paramRequestProperties;
  }
}
