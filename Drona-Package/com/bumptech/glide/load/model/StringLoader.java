package com.bumptech.glide.load.model;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import com.bumptech.glide.load.Options;
import java.io.File;
import java.io.InputStream;

public class StringLoader<Data>
  implements ModelLoader<String, Data>
{
  private final ModelLoader<Uri, Data> uriLoader;
  
  public StringLoader(ModelLoader paramModelLoader)
  {
    uriLoader = paramModelLoader;
  }
  
  private static Uri parseUri(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    if (paramString.charAt(0) == '/') {
      return toFileUri(paramString);
    }
    Uri localUri2 = Uri.parse(paramString);
    Uri localUri1 = localUri2;
    if (localUri2.getScheme() == null) {
      localUri1 = toFileUri(paramString);
    }
    return localUri1;
  }
  
  private static Uri toFileUri(String paramString)
  {
    return Uri.fromFile(new File(paramString));
  }
  
  public ModelLoader.LoadData buildLoadData(String paramString, int paramInt1, int paramInt2, Options paramOptions)
  {
    paramString = parseUri(paramString);
    if ((paramString != null) && (uriLoader.handles(paramString))) {
      return uriLoader.buildLoadData(paramString, paramInt1, paramInt2, paramOptions);
    }
    return null;
  }
  
  public boolean handles(String paramString)
  {
    return true;
  }
  
  public static final class AssetFileDescriptorFactory
    implements ModelLoaderFactory<String, AssetFileDescriptor>
  {
    public AssetFileDescriptorFactory() {}
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new StringLoader(paramMultiModelLoaderFactory.build(Uri.class, AssetFileDescriptor.class));
    }
    
    public void teardown() {}
  }
  
  public static class FileDescriptorFactory
    implements ModelLoaderFactory<String, ParcelFileDescriptor>
  {
    public FileDescriptorFactory() {}
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new StringLoader(paramMultiModelLoaderFactory.build(Uri.class, ParcelFileDescriptor.class));
    }
    
    public void teardown() {}
  }
  
  public static class StreamFactory
    implements ModelLoaderFactory<String, InputStream>
  {
    public StreamFactory() {}
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new StringLoader(paramMultiModelLoaderFactory.build(Uri.class, InputStream.class));
    }
    
    public void teardown() {}
  }
}
