package com.bumptech.glide.load.model.stream;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import java.io.InputStream;
import java.net.URL;

public class UrlLoader
  implements ModelLoader<URL, InputStream>
{
  private final ModelLoader<GlideUrl, InputStream> glideUrlLoader;
  
  public UrlLoader(ModelLoader paramModelLoader)
  {
    glideUrlLoader = paramModelLoader;
  }
  
  public ModelLoader.LoadData buildLoadData(URL paramURL, int paramInt1, int paramInt2, Options paramOptions)
  {
    return glideUrlLoader.buildLoadData(new GlideUrl(paramURL), paramInt1, paramInt2, paramOptions);
  }
  
  public boolean handles(URL paramURL)
  {
    return true;
  }
  
  public static class StreamFactory
    implements ModelLoaderFactory<URL, InputStream>
  {
    public StreamFactory() {}
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new UrlLoader(paramMultiModelLoaderFactory.build(GlideUrl.class, InputStream.class));
    }
    
    public void teardown() {}
  }
}
