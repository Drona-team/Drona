package com.bumptech.glide.load.model;

import android.content.res.AssetManager;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.FileDescriptorAssetPathFetcher;
import com.bumptech.glide.load.data.StreamAssetPathFetcher;
import com.bumptech.glide.signature.ObjectKey;
import java.io.InputStream;
import java.util.List;

public class AssetUriLoader<Data>
  implements ModelLoader<Uri, Data>
{
  private static final String ASSET_PATH_SEGMENT = "android_asset";
  private static final String ASSET_PREFIX = "file:///android_asset/";
  private static final int ASSET_PREFIX_LENGTH = "file:///android_asset/".length();
  private final AssetManager assetManager;
  private final AssetFetcherFactory<Data> factory;
  
  public AssetUriLoader(AssetManager paramAssetManager, AssetFetcherFactory paramAssetFetcherFactory)
  {
    assetManager = paramAssetManager;
    factory = paramAssetFetcherFactory;
  }
  
  public ModelLoader.LoadData buildLoadData(Uri paramUri, int paramInt1, int paramInt2, Options paramOptions)
  {
    paramOptions = paramUri.toString().substring(ASSET_PREFIX_LENGTH);
    return new ModelLoader.LoadData(new ObjectKey(paramUri), factory.buildFetcher(assetManager, paramOptions));
  }
  
  public boolean handles(Uri paramUri)
  {
    return ("file".equals(paramUri.getScheme())) && (!paramUri.getPathSegments().isEmpty()) && ("android_asset".equals(paramUri.getPathSegments().get(0)));
  }
  
  public static abstract interface AssetFetcherFactory<Data>
  {
    public abstract DataFetcher buildFetcher(AssetManager paramAssetManager, String paramString);
  }
  
  public static class FileDescriptorFactory
    implements ModelLoaderFactory<Uri, ParcelFileDescriptor>, AssetUriLoader.AssetFetcherFactory<ParcelFileDescriptor>
  {
    private final AssetManager assetManager;
    
    public FileDescriptorFactory(AssetManager paramAssetManager)
    {
      assetManager = paramAssetManager;
    }
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new AssetUriLoader(assetManager, this);
    }
    
    public DataFetcher buildFetcher(AssetManager paramAssetManager, String paramString)
    {
      return new FileDescriptorAssetPathFetcher(paramAssetManager, paramString);
    }
    
    public void teardown() {}
  }
  
  public static class StreamFactory
    implements ModelLoaderFactory<Uri, InputStream>, AssetUriLoader.AssetFetcherFactory<InputStream>
  {
    private final AssetManager assetManager;
    
    public StreamFactory(AssetManager paramAssetManager)
    {
      assetManager = paramAssetManager;
    }
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new AssetUriLoader(assetManager, this);
    }
    
    public DataFetcher buildFetcher(AssetManager paramAssetManager, String paramString)
    {
      return new StreamAssetPathFetcher(paramAssetManager, paramString);
    }
    
    public void teardown() {}
  }
}
