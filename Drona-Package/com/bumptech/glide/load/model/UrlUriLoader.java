package com.bumptech.glide.load.model;

import android.net.Uri;
import com.bumptech.glide.load.Options;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UrlUriLoader<Data>
  implements ModelLoader<Uri, Data>
{
  private static final Set<String> SCHEMES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] { "http", "https" })));
  private final ModelLoader<GlideUrl, Data> urlLoader;
  
  public UrlUriLoader(ModelLoader paramModelLoader)
  {
    urlLoader = paramModelLoader;
  }
  
  public ModelLoader.LoadData buildLoadData(Uri paramUri, int paramInt1, int paramInt2, Options paramOptions)
  {
    paramUri = new GlideUrl(paramUri.toString());
    return urlLoader.buildLoadData(paramUri, paramInt1, paramInt2, paramOptions);
  }
  
  public boolean handles(Uri paramUri)
  {
    return SCHEMES.contains(paramUri.getScheme());
  }
  
  public static class StreamFactory
    implements ModelLoaderFactory<Uri, InputStream>
  {
    public StreamFactory() {}
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new UrlUriLoader(paramMultiModelLoaderFactory.build(GlideUrl.class, InputStream.class));
    }
    
    public void teardown() {}
  }
}
