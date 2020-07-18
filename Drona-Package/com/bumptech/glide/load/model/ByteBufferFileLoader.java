package com.bumptech.glide.load.model;

import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.signature.ObjectKey;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferFileLoader
  implements ModelLoader<File, ByteBuffer>
{
  private static final String PAGE_KEY = "ByteBufferFileLoader";
  
  public ByteBufferFileLoader() {}
  
  public ModelLoader.LoadData buildLoadData(File paramFile, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new ModelLoader.LoadData(new ObjectKey(paramFile), new ByteBufferFetcher(paramFile));
  }
  
  public boolean handles(File paramFile)
  {
    return true;
  }
  
  private static final class ByteBufferFetcher
    implements DataFetcher<ByteBuffer>
  {
    private final File file;
    
    ByteBufferFetcher(File paramFile)
    {
      file = paramFile;
    }
    
    public void cancel() {}
    
    public void cleanup() {}
    
    public Class getDataClass()
    {
      return ByteBuffer.class;
    }
    
    public DataSource getDataSource()
    {
      return DataSource.LOCAL;
    }
    
    public void loadData(Priority paramPriority, DataFetcher.DataCallback paramDataCallback)
    {
      paramPriority = file;
      try
      {
        paramPriority = ByteBufferUtil.fromFile(paramPriority);
        paramDataCallback.onDataReady(paramPriority);
        return;
      }
      catch (IOException paramPriority)
      {
        if (Log.isLoggable("ByteBufferFileLoader", 3)) {
          Log.d("ByteBufferFileLoader", "Failed to obtain ByteBuffer for file", paramPriority);
        }
        paramDataCallback.onLoadFailed(paramPriority);
      }
    }
  }
  
  public static class Factory
    implements ModelLoaderFactory<File, ByteBuffer>
  {
    public Factory() {}
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new ByteBufferFileLoader();
    }
    
    public void teardown() {}
  }
}
