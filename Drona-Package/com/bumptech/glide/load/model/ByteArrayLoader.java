package com.bumptech.glide.load.model;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.signature.ObjectKey;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteArrayLoader<Data>
  implements ModelLoader<byte[], Data>
{
  private final Converter<Data> converter;
  
  public ByteArrayLoader(Converter paramConverter)
  {
    converter = paramConverter;
  }
  
  public ModelLoader.LoadData buildLoadData(byte[] paramArrayOfByte, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new ModelLoader.LoadData(new ObjectKey(paramArrayOfByte), new Fetcher(paramArrayOfByte, converter));
  }
  
  public boolean handles(byte[] paramArrayOfByte)
  {
    return true;
  }
  
  public static class ByteBufferFactory
    implements ModelLoaderFactory<byte[], ByteBuffer>
  {
    public ByteBufferFactory() {}
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      new ByteArrayLoader(new ByteArrayLoader.Converter()
      {
        public ByteBuffer convert(byte[] paramAnonymousArrayOfByte)
        {
          return ByteBuffer.wrap(paramAnonymousArrayOfByte);
        }
        
        public Class getDataClass()
        {
          return ByteBuffer.class;
        }
      });
    }
    
    public void teardown() {}
  }
  
  public static abstract interface Converter<Data>
  {
    public abstract Object convert(byte[] paramArrayOfByte);
    
    public abstract Class getDataClass();
  }
  
  private static class Fetcher<Data>
    implements DataFetcher<Data>
  {
    private final ByteArrayLoader.Converter<Data> converter;
    private final byte[] model;
    
    Fetcher(byte[] paramArrayOfByte, ByteArrayLoader.Converter paramConverter)
    {
      model = paramArrayOfByte;
      converter = paramConverter;
    }
    
    public void cancel() {}
    
    public void cleanup() {}
    
    public Class getDataClass()
    {
      return converter.getDataClass();
    }
    
    public DataSource getDataSource()
    {
      return DataSource.LOCAL;
    }
    
    public void loadData(Priority paramPriority, DataFetcher.DataCallback paramDataCallback)
    {
      paramDataCallback.onDataReady(converter.convert(model));
    }
  }
  
  public static class StreamFactory
    implements ModelLoaderFactory<byte[], InputStream>
  {
    public StreamFactory() {}
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      new ByteArrayLoader(new ByteArrayLoader.Converter()
      {
        public InputStream convert(byte[] paramAnonymousArrayOfByte)
        {
          return new ByteArrayInputStream(paramAnonymousArrayOfByte);
        }
        
        public Class getDataClass()
        {
          return InputStream.class;
        }
      });
    }
    
    public void teardown() {}
  }
}
