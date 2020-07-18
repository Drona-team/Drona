package com.bumptech.glide.load.model.stream;

import androidx.annotation.Nullable;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.HttpUrlFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import java.io.InputStream;

public class HttpGlideUrlLoader
  implements ModelLoader<GlideUrl, InputStream>
{
  public static final Option<Integer> TIMEOUT = Option.memory("com.bumptech.glide.load.model.stream.HttpGlideUrlLoader.Timeout", Integer.valueOf(2500));
  @Nullable
  private final ModelCache<GlideUrl, GlideUrl> modelCache;
  
  public HttpGlideUrlLoader()
  {
    this(null);
  }
  
  public HttpGlideUrlLoader(ModelCache paramModelCache)
  {
    modelCache = paramModelCache;
  }
  
  public ModelLoader.LoadData buildLoadData(GlideUrl paramGlideUrl, int paramInt1, int paramInt2, Options paramOptions)
  {
    GlideUrl localGlideUrl = paramGlideUrl;
    if (modelCache != null)
    {
      localGlideUrl = (GlideUrl)modelCache.put(paramGlideUrl, 0, 0);
      if (localGlideUrl == null)
      {
        modelCache.put(paramGlideUrl, 0, 0, paramGlideUrl);
        localGlideUrl = paramGlideUrl;
      }
    }
    return new ModelLoader.LoadData(localGlideUrl, new HttpUrlFetcher(localGlideUrl, ((Integer)paramOptions.getOption(TIMEOUT)).intValue()));
  }
  
  public boolean handles(GlideUrl paramGlideUrl)
  {
    return true;
  }
  
  public static class Factory
    implements ModelLoaderFactory<GlideUrl, InputStream>
  {
    private final ModelCache<GlideUrl, GlideUrl> modelCache = new ModelCache(500L);
    
    public Factory() {}
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new HttpGlideUrlLoader(modelCache);
    }
    
    public void teardown() {}
  }
}
