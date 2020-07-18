package com.bumptech.glide.load.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class LocalUriFetcher<T>
  implements DataFetcher<T>
{
  private static final String TAG = "LocalUriFetcher";
  private final ContentResolver contentResolver;
  private T data;
  private final Uri uri;
  
  public LocalUriFetcher(ContentResolver paramContentResolver, Uri paramUri)
  {
    contentResolver = paramContentResolver;
    uri = paramUri;
  }
  
  public void cancel() {}
  
  public void cleanup()
  {
    if (data != null)
    {
      Object localObject = data;
      try
      {
        close(localObject);
        return;
      }
      catch (IOException localIOException) {}
    }
  }
  
  protected abstract void close(Object paramObject)
    throws IOException;
  
  public DataSource getDataSource()
  {
    return DataSource.LOCAL;
  }
  
  public final void loadData(Priority paramPriority, DataFetcher.DataCallback paramDataCallback)
  {
    paramPriority = uri;
    ContentResolver localContentResolver = contentResolver;
    try
    {
      paramPriority = loadResource(paramPriority, localContentResolver);
      data = paramPriority;
      paramDataCallback.onDataReady(data);
      return;
    }
    catch (FileNotFoundException paramPriority)
    {
      if (Log.isLoggable("LocalUriFetcher", 3)) {
        Log.d("LocalUriFetcher", "Failed to open Uri", paramPriority);
      }
      paramDataCallback.onLoadFailed(paramPriority);
    }
  }
  
  protected abstract Object loadResource(Uri paramUri, ContentResolver paramContentResolver)
    throws FileNotFoundException;
}
