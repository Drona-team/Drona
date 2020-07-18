package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import java.io.File;
import java.util.List;

class DataCacheGenerator
  implements DataFetcherGenerator, DataFetcher.DataCallback<Object>
{
  private File cacheFile;
  private final List<Key> cacheKeys;
  private final DecodeHelper<?> helper;
  private volatile ModelLoader.LoadData<?> loadData;
  private int modelLoaderIndex;
  private List<ModelLoader<File, ?>> modelLoaders;
  private int sourceIdIndex = -1;
  private Key sourceKey;
  private final DataFetcherGenerator.FetcherReadyCallback vector;
  
  DataCacheGenerator(DecodeHelper paramDecodeHelper, DataFetcherGenerator.FetcherReadyCallback paramFetcherReadyCallback)
  {
    this(paramDecodeHelper.getCacheKeys(), paramDecodeHelper, paramFetcherReadyCallback);
  }
  
  DataCacheGenerator(List paramList, DecodeHelper paramDecodeHelper, DataFetcherGenerator.FetcherReadyCallback paramFetcherReadyCallback)
  {
    cacheKeys = paramList;
    helper = paramDecodeHelper;
    vector = paramFetcherReadyCallback;
  }
  
  private boolean hasNextModelLoader()
  {
    return modelLoaderIndex < modelLoaders.size();
  }
  
  public void cancel()
  {
    ModelLoader.LoadData localLoadData = loadData;
    if (localLoadData != null) {
      fetcher.cancel();
    }
  }
  
  public void onDataReady(Object paramObject)
  {
    vector.onDataFetcherReady(sourceKey, paramObject, loadData.fetcher, DataSource.DATA_DISK_CACHE, sourceKey);
  }
  
  public void onLoadFailed(Exception paramException)
  {
    vector.onDataFetcherFailed(sourceKey, paramException, loadData.fetcher, DataSource.DATA_DISK_CACHE);
  }
  
  public boolean startNext()
  {
    boolean bool;
    for (;;)
    {
      Object localObject = modelLoaders;
      bool = false;
      if ((localObject != null) && (hasNextModelLoader()))
      {
        loadData = null;
        while (!bool)
        {
          if (!hasNextModelLoader()) {
            return bool;
          }
          localObject = modelLoaders;
          int i = modelLoaderIndex;
          modelLoaderIndex = (i + 1);
          loadData = ((ModelLoader)((List)localObject).get(i)).buildLoadData(cacheFile, helper.getWidth(), helper.getHeight(), helper.getOptions());
          if ((loadData != null) && (helper.hasLoadPath(loadData.fetcher.getDataClass())))
          {
            loadData.fetcher.loadData(helper.getPriority(), this);
            bool = true;
          }
        }
        return bool;
      }
      sourceIdIndex += 1;
      if (sourceIdIndex >= cacheKeys.size()) {
        return false;
      }
      localObject = (Key)cacheKeys.get(sourceIdIndex);
      DataCacheKey localDataCacheKey = new DataCacheKey((Key)localObject, helper.getSignature());
      cacheFile = helper.getDiskCache().get(localDataCacheKey);
      if (cacheFile != null)
      {
        sourceKey = ((Key)localObject);
        modelLoaders = helper.getModelLoaders(cacheFile);
        modelLoaderIndex = 0;
      }
    }
    return bool;
  }
}
