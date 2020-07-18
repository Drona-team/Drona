package com.bumptech.glide.load.model.stream;

import android.net.Uri;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HttpUriLoader
  implements ModelLoader<Uri, InputStream>
{
  private static final Set<String> SCHEMES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] { "http", "https" })));
  private final ModelLoader<GlideUrl, InputStream> urlLoader;
  
  public HttpUriLoader(ModelLoader paramModelLoader)
  {
    urlLoader = paramModelLoader;
  }
  
  public ModelLoader.LoadData buildLoadData(Uri paramUri, int paramInt1, int paramInt2, Options paramOptions)
  {
    return urlLoader.buildLoadData(new GlideUrl(paramUri.toString()), paramInt1, paramInt2, paramOptions);
  }
  
  public boolean handles(Uri paramUri)
  {
    return SCHEMES.contains(paramUri.getScheme());
  }
  
  public static class Factory
    implements ModelLoaderFactory<Uri, InputStream>
  {
    public Factory() {}
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new HttpUriLoader(paramMultiModelLoaderFactory.build(GlideUrl.class, InputStream.class));
    }
    
    public void teardown() {}
  }
}
