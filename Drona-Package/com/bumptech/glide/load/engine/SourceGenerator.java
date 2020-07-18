package com.bumptech.glide.load.engine;

import android.util.Log;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskCache.Writer;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.util.LogTime;
import java.util.Collections;
import java.util.List;

class SourceGenerator
  implements DataFetcherGenerator, DataFetcher.DataCallback<Object>, DataFetcherGenerator.FetcherReadyCallback
{
  private static final String PAGE_KEY = "SourceGenerator";
  private Object dataToCache;
  private final DataFetcherGenerator.FetcherReadyCallback fetcher;
  private final DecodeHelper<?> helper;
  private volatile ModelLoader.LoadData<?> loadData;
  private int loadDataListIndex;
  private DataCacheKey originalKey;
  private DataCacheGenerator sourceCacheGenerator;
  
  SourceGenerator(DecodeHelper paramDecodeHelper, DataFetcherGenerator.FetcherReadyCallback paramFetcherReadyCallback)
  {
    helper = paramDecodeHelper;
    fetcher = paramFetcherReadyCallback;
  }
  
  private void cacheData(Object paramObject)
  {
    long l = LogTime.getLogTime();
    try
    {
      Encoder localEncoder = helper.getSourceEncoder(paramObject);
      Object localObject = new DataCacheWriter(localEncoder, paramObject, helper.getOptions());
      originalKey = new DataCacheKey(loadData.sourceKey, helper.getSignature());
      helper.getDiskCache().put(originalKey, (DiskCache.Writer)localObject);
      boolean bool = Log.isLoggable("SourceGenerator", 2);
      if (bool)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Finished encoding source to cache, key: ");
        ((StringBuilder)localObject).append(originalKey);
        ((StringBuilder)localObject).append(", data: ");
        ((StringBuilder)localObject).append(paramObject);
        ((StringBuilder)localObject).append(", encoder: ");
        ((StringBuilder)localObject).append(localEncoder);
        ((StringBuilder)localObject).append(", duration: ");
        ((StringBuilder)localObject).append(LogTime.getElapsedMillis(l));
        Log.v("SourceGenerator", ((StringBuilder)localObject).toString());
      }
      loadData.fetcher.cleanup();
      sourceCacheGenerator = new DataCacheGenerator(Collections.singletonList(loadData.sourceKey), helper, this);
      return;
    }
    catch (Throwable paramObject)
    {
      loadData.fetcher.cleanup();
      throw paramObject;
    }
  }
  
  private boolean hasNextModelLoader()
  {
    return loadDataListIndex < helper.getLoadData().size();
  }
  
  public void cancel()
  {
    ModelLoader.LoadData localLoadData = loadData;
    if (localLoadData != null) {
      fetcher.cancel();
    }
  }
  
  public void onDataFetcherFailed(Key paramKey, Exception paramException, DataFetcher paramDataFetcher, DataSource paramDataSource)
  {
    fetcher.onDataFetcherFailed(paramKey, paramException, paramDataFetcher, loadData.fetcher.getDataSource());
  }
  
  public void onDataFetcherReady(Key paramKey1, Object paramObject, DataFetcher paramDataFetcher, DataSource paramDataSource, Key paramKey2)
  {
    fetcher.onDataFetcherReady(paramKey1, paramObject, paramDataFetcher, loadData.fetcher.getDataSource(), paramKey1);
  }
  
  public void onDataReady(Object paramObject)
  {
    DiskCacheStrategy localDiskCacheStrategy = helper.getDiskCacheStrategy();
    if ((paramObject != null) && (localDiskCacheStrategy.isDataCacheable(loadData.fetcher.getDataSource())))
    {
      dataToCache = paramObject;
      fetcher.reschedule();
      return;
    }
    fetcher.onDataFetcherReady(loadData.sourceKey, paramObject, loadData.fetcher, loadData.fetcher.getDataSource(), originalKey);
  }
  
  public void onLoadFailed(Exception paramException)
  {
    fetcher.onDataFetcherFailed(originalKey, paramException, loadData.fetcher, loadData.fetcher.getDataSource());
  }
  
  public void reschedule()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean startNext()
  {
    Object localObject;
    if (dataToCache != null)
    {
      localObject = dataToCache;
      dataToCache = null;
      cacheData(localObject);
    }
    if ((sourceCacheGenerator != null) && (sourceCacheGenerator.startNext())) {
      return true;
    }
    sourceCacheGenerator = null;
    loadData = null;
    for (boolean bool = false; (!bool) && (hasNextModelLoader()); bool = true)
    {
      label53:
      localObject = helper.getLoadData();
      int i = loadDataListIndex;
      loadDataListIndex = (i + 1);
      loadData = ((ModelLoader.LoadData)((List)localObject).get(i));
      if ((loadData == null) || ((!helper.getDiskCacheStrategy().isDataCacheable(loadData.fetcher.getDataSource())) && (!helper.hasLoadPath(loadData.fetcher.getDataClass())))) {
        break label53;
      }
      loadData.fetcher.loadData(helper.getPriority(), this);
    }
    return bool;
  }
}
