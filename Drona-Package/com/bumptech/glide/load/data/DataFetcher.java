package com.bumptech.glide.load.data;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;

public abstract interface DataFetcher<T>
{
  public abstract void cancel();
  
  public abstract void cleanup();
  
  public abstract Class getDataClass();
  
  public abstract DataSource getDataSource();
  
  public abstract void loadData(Priority paramPriority, DataCallback paramDataCallback);
  
  public static abstract interface DataCallback<T>
  {
    public abstract void onDataReady(Object paramObject);
    
    public abstract void onLoadFailed(Exception paramException);
  }
}
