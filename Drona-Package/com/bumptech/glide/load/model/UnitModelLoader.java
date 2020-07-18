package com.bumptech.glide.load.model;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.signature.ObjectKey;

public class UnitModelLoader<Model>
  implements ModelLoader<Model, Model>
{
  private static final UnitModelLoader<?> INSTANCE = new UnitModelLoader();
  
  public UnitModelLoader() {}
  
  public static UnitModelLoader getInstance()
  {
    return INSTANCE;
  }
  
  public ModelLoader.LoadData buildLoadData(Object paramObject, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new ModelLoader.LoadData(new ObjectKey(paramObject), new UnitFetcher(paramObject));
  }
  
  public boolean handles(Object paramObject)
  {
    return true;
  }
  
  public static class Factory<Model>
    implements ModelLoaderFactory<Model, Model>
  {
    private static final Factory<?> FACTORY = new Factory();
    
    public Factory() {}
    
    public static Factory getInstance()
    {
      return FACTORY;
    }
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return UnitModelLoader.getInstance();
    }
    
    public void teardown() {}
  }
  
  private static class UnitFetcher<Model>
    implements DataFetcher<Model>
  {
    private final Model resource;
    
    UnitFetcher(Object paramObject)
    {
      resource = paramObject;
    }
    
    public void cancel() {}
    
    public void cleanup() {}
    
    public Class getDataClass()
    {
      return resource.getClass();
    }
    
    public DataSource getDataSource()
    {
      return DataSource.LOCAL;
    }
    
    public void loadData(Priority paramPriority, DataFetcher.DataCallback paramDataCallback)
    {
      paramDataCallback.onDataReady(resource);
    }
  }
}
