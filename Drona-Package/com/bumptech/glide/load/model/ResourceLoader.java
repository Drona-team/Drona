package com.bumptech.glide.load.model;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.bumptech.glide.load.Options;
import java.io.InputStream;

public class ResourceLoader<Data>
  implements ModelLoader<Integer, Data>
{
  private static final String TAG = "ResourceLoader";
  private final Resources resources;
  private final ModelLoader<Uri, Data> uriLoader;
  
  public ResourceLoader(Resources paramResources, ModelLoader paramModelLoader)
  {
    resources = paramResources;
    uriLoader = paramModelLoader;
  }
  
  private Uri getResourceUri(Integer paramInteger)
  {
    try
    {
      Object localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("android.resource://");
      localObject2 = resources;
      ((StringBuilder)localObject1).append(((Resources)localObject2).getResourcePackageName(paramInteger.intValue()));
      ((StringBuilder)localObject1).append('/');
      localObject2 = resources;
      ((StringBuilder)localObject1).append(((Resources)localObject2).getResourceTypeName(paramInteger.intValue()));
      ((StringBuilder)localObject1).append('/');
      localObject2 = resources;
      ((StringBuilder)localObject1).append(((Resources)localObject2).getResourceEntryName(paramInteger.intValue()));
      localObject1 = Uri.parse(((StringBuilder)localObject1).toString());
      return localObject1;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      Object localObject2;
      if (Log.isLoggable("ResourceLoader", 5))
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("Received invalid resource id: ");
        ((StringBuilder)localObject2).append(paramInteger);
        Log.w("ResourceLoader", ((StringBuilder)localObject2).toString(), localNotFoundException);
      }
    }
    return null;
  }
  
  public ModelLoader.LoadData buildLoadData(Integer paramInteger, int paramInt1, int paramInt2, Options paramOptions)
  {
    paramInteger = getResourceUri(paramInteger);
    if (paramInteger == null) {
      return null;
    }
    return uriLoader.buildLoadData(paramInteger, paramInt1, paramInt2, paramOptions);
  }
  
  public boolean handles(Integer paramInteger)
  {
    return true;
  }
  
  public static final class AssetFileDescriptorFactory
    implements ModelLoaderFactory<Integer, AssetFileDescriptor>
  {
    private final Resources resources;
    
    public AssetFileDescriptorFactory(Resources paramResources)
    {
      resources = paramResources;
    }
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new ResourceLoader(resources, paramMultiModelLoaderFactory.build(Uri.class, AssetFileDescriptor.class));
    }
    
    public void teardown() {}
  }
  
  public static class FileDescriptorFactory
    implements ModelLoaderFactory<Integer, ParcelFileDescriptor>
  {
    private final Resources resources;
    
    public FileDescriptorFactory(Resources paramResources)
    {
      resources = paramResources;
    }
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new ResourceLoader(resources, paramMultiModelLoaderFactory.build(Uri.class, ParcelFileDescriptor.class));
    }
    
    public void teardown() {}
  }
  
  public static class StreamFactory
    implements ModelLoaderFactory<Integer, InputStream>
  {
    private final Resources resources;
    
    public StreamFactory(Resources paramResources)
    {
      resources = paramResources;
    }
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new ResourceLoader(resources, paramMultiModelLoaderFactory.build(Uri.class, InputStream.class));
    }
    
    public void teardown() {}
  }
  
  public static class UriFactory
    implements ModelLoaderFactory<Integer, Uri>
  {
    private final Resources resources;
    
    public UriFactory(Resources paramResources)
    {
      resources = paramResources;
    }
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new ResourceLoader(resources, UnitModelLoader.getInstance());
    }
    
    public void teardown() {}
  }
}
