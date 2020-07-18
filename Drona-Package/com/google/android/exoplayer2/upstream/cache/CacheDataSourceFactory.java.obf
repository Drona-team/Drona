package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSink.Factory;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;

public final class CacheDataSourceFactory
  implements DataSource.Factory
{
  private final Cache cache;
  private final DataSource.Factory cacheReadDataSourceFactory;
  private final DataSink.Factory cacheWriteDataSinkFactory;
  private final CacheDataSource.EventListener eventListener;
  private final int flags;
  private final DataSource.Factory upstreamFactory;
  
  public CacheDataSourceFactory(Cache paramCache, DataSource.Factory paramFactory)
  {
    this(paramCache, paramFactory, 0);
  }
  
  public CacheDataSourceFactory(Cache paramCache, DataSource.Factory paramFactory, int paramInt)
  {
    this(paramCache, paramFactory, paramInt, 2097152L);
  }
  
  public CacheDataSourceFactory(Cache paramCache, DataSource.Factory paramFactory, int paramInt, long paramLong)
  {
    this(paramCache, paramFactory, new FileDataSourceFactory(), new CacheDataSinkFactory(paramCache, paramLong), paramInt, null);
  }
  
  public CacheDataSourceFactory(Cache paramCache, DataSource.Factory paramFactory1, DataSource.Factory paramFactory2, DataSink.Factory paramFactory, int paramInt, CacheDataSource.EventListener paramEventListener)
  {
    cache = paramCache;
    upstreamFactory = paramFactory1;
    cacheReadDataSourceFactory = paramFactory2;
    cacheWriteDataSinkFactory = paramFactory;
    flags = paramInt;
    eventListener = paramEventListener;
  }
  
  public CacheDataSource createDataSource()
  {
    Cache localCache = cache;
    DataSource localDataSource1 = upstreamFactory.createDataSource();
    DataSource localDataSource2 = cacheReadDataSourceFactory.createDataSource();
    if (cacheWriteDataSinkFactory != null) {}
    for (DataSink localDataSink = cacheWriteDataSinkFactory.createDataSink();; localDataSink = null) {
      break;
    }
    return new CacheDataSource(localCache, localDataSource1, localDataSource2, localDataSink, flags, eventListener);
  }
}
