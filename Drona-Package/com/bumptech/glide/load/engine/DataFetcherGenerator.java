package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;

abstract interface DataFetcherGenerator
{
  public abstract void cancel();
  
  public abstract boolean startNext();
  
  public static abstract interface FetcherReadyCallback
  {
    public abstract void onDataFetcherFailed(Key paramKey, Exception paramException, DataFetcher paramDataFetcher, DataSource paramDataSource);
    
    public abstract void onDataFetcherReady(Key paramKey1, Object paramObject, DataFetcher paramDataFetcher, DataSource paramDataSource, Key paramKey2);
    
    public abstract void reschedule();
  }
}
