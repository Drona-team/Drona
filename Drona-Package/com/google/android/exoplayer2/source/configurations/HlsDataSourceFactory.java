package com.google.android.exoplayer2.source.configurations;

import com.google.android.exoplayer2.upstream.DataSource;

public abstract interface HlsDataSourceFactory
{
  public abstract DataSource createDataSource(int paramInt);
}
