package com.bumptech.glide.load.data;

import android.content.res.AssetManager;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import java.io.IOException;

public abstract class AssetPathFetcher<T>
  implements DataFetcher<T>
{
  private static final String TAG = "AssetPathFetcher";
  private final AssetManager assetManager;
  private final String assetPath;
  private T data;
  
  public AssetPathFetcher(AssetManager paramAssetManager, String paramString)
  {
    assetManager = paramAssetManager;
    assetPath = paramString;
  }
  
  public void cancel() {}
  
  public void cleanup()
  {
    if (data == null) {
      return;
    }
    Object localObject = data;
    try
    {
      close(localObject);
      return;
    }
    catch (IOException localIOException) {}
  }
  
  protected abstract void close(Object paramObject)
    throws IOException;
  
  public DataSource getDataSource()
  {
    return DataSource.LOCAL;
  }
  
  public void loadData(Priority paramPriority, DataFetcher.DataCallback paramDataCallback)
  {
    paramPriority = assetManager;
    String str = assetPath;
    try
    {
      paramPriority = loadResource(paramPriority, str);
      data = paramPriority;
      paramDataCallback.onDataReady(data);
      return;
    }
    catch (IOException paramPriority)
    {
      if (Log.isLoggable("AssetPathFetcher", 3)) {
        Log.d("AssetPathFetcher", "Failed to load data from asset manager", paramPriority);
      }
      paramDataCallback.onLoadFailed(paramPriority);
    }
  }
  
  protected abstract Object loadResource(AssetManager paramAssetManager, String paramString)
    throws IOException;
}
