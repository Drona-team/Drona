package com.google.android.exoplayer2.source.configurations;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;

public final class DefaultHlsDataSourceFactory
  implements HlsDataSourceFactory
{
  private final DataSource.Factory dataSourceFactory;
  
  public DefaultHlsDataSourceFactory(DataSource.Factory paramFactory)
  {
    dataSourceFactory = paramFactory;
  }
  
  public DataSource createDataSource(int paramInt)
  {
    return dataSourceFactory.createDataSource();
  }
}
