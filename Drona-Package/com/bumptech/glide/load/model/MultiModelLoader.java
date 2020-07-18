package com.bumptech.glide.load.model;

import androidx.annotation.Nullable;
import androidx.core.util.Pools.Pool;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class MultiModelLoader<Model, Data>
  implements ModelLoader<Model, Data>
{
  private final Pools.Pool<List<Throwable>> exceptionListPool;
  private final List<ModelLoader<Model, Data>> modelLoaders;
  
  MultiModelLoader(List paramList, Pools.Pool paramPool)
  {
    modelLoaders = paramList;
    exceptionListPool = paramPool;
  }
  
  public ModelLoader.LoadData buildLoadData(Object paramObject, int paramInt1, int paramInt2, Options paramOptions)
  {
    int j = modelLoaders.size();
    ArrayList localArrayList = new ArrayList(j);
    int i = 0;
    Object localObject2;
    for (Object localObject1 = null; i < j; localObject1 = localObject2)
    {
      Object localObject3 = (ModelLoader)modelLoaders.get(i);
      localObject2 = localObject1;
      if (((ModelLoader)localObject3).handles(paramObject))
      {
        localObject3 = ((ModelLoader)localObject3).buildLoadData(paramObject, paramInt1, paramInt2, paramOptions);
        localObject2 = localObject1;
        if (localObject3 != null)
        {
          localObject2 = sourceKey;
          localArrayList.add(fetcher);
        }
      }
      i += 1;
    }
    if ((!localArrayList.isEmpty()) && (localObject1 != null)) {
      return new ModelLoader.LoadData(localObject1, new MultiFetcher(localArrayList, exceptionListPool));
    }
    return null;
  }
  
  public boolean handles(Object paramObject)
  {
    Iterator localIterator = modelLoaders.iterator();
    while (localIterator.hasNext()) {
      if (((ModelLoader)localIterator.next()).handles(paramObject)) {
        return true;
      }
    }
    return false;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("MultiModelLoader{modelLoaders=");
    localStringBuilder.append(Arrays.toString(modelLoaders.toArray()));
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  static class MultiFetcher<Data>
    implements DataFetcher<Data>, DataFetcher.DataCallback<Data>
  {
    private DataFetcher.DataCallback<? super Data> callback;
    private int currentIndex;
    @Nullable
    private List<Throwable> exceptions;
    private final List<DataFetcher<Data>> fetchers;
    private boolean isCancelled;
    private Priority priority;
    private final Pools.Pool<List<Throwable>> throwableListPool;
    
    MultiFetcher(List paramList, Pools.Pool paramPool)
    {
      throwableListPool = paramPool;
      Preconditions.checkNotEmpty(paramList);
      fetchers = paramList;
      currentIndex = 0;
    }
    
    private void startNextOrFail()
    {
      if (isCancelled) {
        return;
      }
      if (currentIndex < fetchers.size() - 1)
      {
        currentIndex += 1;
        loadData(priority, callback);
        return;
      }
      Preconditions.checkNotNull(exceptions);
      callback.onLoadFailed(new GlideException("Fetch failed", new ArrayList(exceptions)));
    }
    
    public void cancel()
    {
      isCancelled = true;
      Iterator localIterator = fetchers.iterator();
      while (localIterator.hasNext()) {
        ((DataFetcher)localIterator.next()).cancel();
      }
    }
    
    public void cleanup()
    {
      if (exceptions != null) {
        throwableListPool.release(exceptions);
      }
      exceptions = null;
      Iterator localIterator = fetchers.iterator();
      while (localIterator.hasNext()) {
        ((DataFetcher)localIterator.next()).cleanup();
      }
    }
    
    public Class getDataClass()
    {
      return ((DataFetcher)fetchers.get(0)).getDataClass();
    }
    
    public DataSource getDataSource()
    {
      return ((DataFetcher)fetchers.get(0)).getDataSource();
    }
    
    public void loadData(Priority paramPriority, DataFetcher.DataCallback paramDataCallback)
    {
      priority = paramPriority;
      callback = paramDataCallback;
      exceptions = ((List)throwableListPool.acquire());
      ((DataFetcher)fetchers.get(currentIndex)).loadData(paramPriority, this);
      if (isCancelled) {
        cancel();
      }
    }
    
    public void onDataReady(Object paramObject)
    {
      if (paramObject != null)
      {
        callback.onDataReady(paramObject);
        return;
      }
      startNextOrFail();
    }
    
    public void onLoadFailed(Exception paramException)
    {
      ((List)Preconditions.checkNotNull(exceptions)).add(paramException);
      startNextOrFail();
    }
  }
}
