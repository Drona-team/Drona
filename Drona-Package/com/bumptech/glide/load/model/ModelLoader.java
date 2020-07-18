package com.bumptech.glide.load.model;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.util.Preconditions;
import java.util.Collections;
import java.util.List;

public abstract interface ModelLoader<Model, Data>
{
  public abstract LoadData buildLoadData(Object paramObject, int paramInt1, int paramInt2, Options paramOptions);
  
  public abstract boolean handles(Object paramObject);
  
  public static class LoadData<Data>
  {
    public final List<Key> alternateKeys;
    public final DataFetcher<Data> fetcher;
    public final Key sourceKey;
    
    public LoadData(Key paramKey, DataFetcher paramDataFetcher)
    {
      this(paramKey, Collections.emptyList(), paramDataFetcher);
    }
    
    public LoadData(Key paramKey, List paramList, DataFetcher paramDataFetcher)
    {
      sourceKey = ((Key)Preconditions.checkNotNull(paramKey));
      alternateKeys = ((List)Preconditions.checkNotNull(paramList));
      fetcher = ((DataFetcher)Preconditions.checkNotNull(paramDataFetcher));
    }
  }
}
