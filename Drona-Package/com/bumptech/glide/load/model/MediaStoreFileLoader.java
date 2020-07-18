package com.bumptech.glide.load.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.data.mediastore.MediaStoreUtil;
import com.bumptech.glide.signature.ObjectKey;
import java.io.File;
import java.io.FileNotFoundException;

public final class MediaStoreFileLoader
  implements ModelLoader<Uri, File>
{
  private final Context context;
  
  public MediaStoreFileLoader(Context paramContext)
  {
    context = paramContext;
  }
  
  public ModelLoader.LoadData buildLoadData(Uri paramUri, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new ModelLoader.LoadData(new ObjectKey(paramUri), new FilePathFetcher(context, paramUri));
  }
  
  public boolean handles(Uri paramUri)
  {
    return MediaStoreUtil.isMediaStoreUri(paramUri);
  }
  
  public static final class Factory
    implements ModelLoaderFactory<Uri, File>
  {
    private final Context context;
    
    public Factory(Context paramContext)
    {
      context = paramContext;
    }
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new MediaStoreFileLoader(context);
    }
    
    public void teardown() {}
  }
  
  private static class FilePathFetcher
    implements DataFetcher<File>
  {
    private static final String[] PROJECTION = { "_data" };
    private final Context context;
    private final Uri uri;
    
    FilePathFetcher(Context paramContext, Uri paramUri)
    {
      context = paramContext;
      uri = paramUri;
    }
    
    public void cancel() {}
    
    public void cleanup() {}
    
    public Class getDataClass()
    {
      return File.class;
    }
    
    public DataSource getDataSource()
    {
      return DataSource.LOCAL;
    }
    
    public void loadData(Priority paramPriority, DataFetcher.DataCallback paramDataCallback)
    {
      Cursor localCursor = context.getContentResolver().query(uri, PROJECTION, null, null, null);
      paramPriority = null;
      Object localObject = null;
      if (localCursor != null) {
        try
        {
          boolean bool = localCursor.moveToFirst();
          paramPriority = localObject;
          if (bool) {
            paramPriority = localCursor.getString(localCursor.getColumnIndexOrThrow("_data"));
          }
          localCursor.close();
        }
        catch (Throwable paramPriority)
        {
          localCursor.close();
          throw paramPriority;
        }
      }
      if (TextUtils.isEmpty(paramPriority))
      {
        paramPriority = new StringBuilder();
        paramPriority.append("Failed to find file path for: ");
        paramPriority.append(uri);
        paramDataCallback.onLoadFailed(new FileNotFoundException(paramPriority.toString()));
        return;
      }
      paramDataCallback.onDataReady(new File(paramPriority));
    }
  }
}
