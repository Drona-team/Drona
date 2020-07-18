package com.bumptech.glide.load.model;

import android.util.Base64;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.signature.ObjectKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class DataUrlLoader<Model, Data>
  implements ModelLoader<Model, Data>
{
  private static final String BASE64_TAG = ";base64";
  private static final String DATA_SCHEME_IMAGE = "data:image";
  private final DataDecoder<Data> dataDecoder;
  
  public DataUrlLoader(DataDecoder paramDataDecoder)
  {
    dataDecoder = paramDataDecoder;
  }
  
  public ModelLoader.LoadData buildLoadData(Object paramObject, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new ModelLoader.LoadData(new ObjectKey(paramObject), new DataUriFetcher(paramObject.toString(), dataDecoder));
  }
  
  public boolean handles(Object paramObject)
  {
    return paramObject.toString().startsWith("data:image");
  }
  
  public static abstract interface DataDecoder<Data>
  {
    public abstract void close(Object paramObject)
      throws IOException;
    
    public abstract Object decode(String paramString)
      throws IllegalArgumentException;
    
    public abstract Class getDataClass();
  }
  
  private static final class DataUriFetcher<Data>
    implements DataFetcher<Data>
  {
    private Data data;
    private final String dataUri;
    private final DataUrlLoader.DataDecoder<Data> reader;
    
    DataUriFetcher(String paramString, DataUrlLoader.DataDecoder paramDataDecoder)
    {
      dataUri = paramString;
      reader = paramDataDecoder;
    }
    
    public void cancel() {}
    
    public void cleanup()
    {
      DataUrlLoader.DataDecoder localDataDecoder = reader;
      Object localObject = data;
      try
      {
        localDataDecoder.close(localObject);
        return;
      }
      catch (IOException localIOException) {}
    }
    
    public Class getDataClass()
    {
      return reader.getDataClass();
    }
    
    public DataSource getDataSource()
    {
      return DataSource.LOCAL;
    }
    
    public void loadData(Priority paramPriority, DataFetcher.DataCallback paramDataCallback)
    {
      paramPriority = reader;
      String str = dataUri;
      try
      {
        paramPriority = paramPriority.decode(str);
        data = paramPriority;
        paramPriority = data;
        paramDataCallback.onDataReady(paramPriority);
        return;
      }
      catch (IllegalArgumentException paramPriority)
      {
        paramDataCallback.onLoadFailed(paramPriority);
      }
    }
  }
  
  public static final class StreamFactory<Model>
    implements ModelLoaderFactory<Model, InputStream>
  {
    private final DataUrlLoader.DataDecoder<InputStream> opener = new DataUrlLoader.DataDecoder()
    {
      public void close(InputStream paramAnonymousInputStream)
        throws IOException
      {
        paramAnonymousInputStream.close();
      }
      
      public InputStream decode(String paramAnonymousString)
      {
        if (paramAnonymousString.startsWith("data:image"))
        {
          int i = paramAnonymousString.indexOf(',');
          if (i != -1)
          {
            if (paramAnonymousString.substring(0, i).endsWith(";base64")) {
              return new ByteArrayInputStream(Base64.decode(paramAnonymousString.substring(i + 1), 0));
            }
            throw new IllegalArgumentException("Not a base64 image data URL.");
          }
          throw new IllegalArgumentException("Missing comma in data URL.");
        }
        throw new IllegalArgumentException("Not a valid image data URL.");
      }
      
      public Class getDataClass()
      {
        return InputStream.class;
      }
    };
    
    public StreamFactory() {}
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new DataUrlLoader(opener);
    }
    
    public void teardown() {}
  }
}
