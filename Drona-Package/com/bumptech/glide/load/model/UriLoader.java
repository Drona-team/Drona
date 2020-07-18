package com.bumptech.glide.load.model;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.AssetFileDescriptorLocalUriFetcher;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.FileDescriptorLocalUriFetcher;
import com.bumptech.glide.load.data.StreamLocalUriFetcher;
import com.bumptech.glide.signature.ObjectKey;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UriLoader<Data>
  implements ModelLoader<Uri, Data>
{
  private static final Set<String> SCHEMES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] { "file", "android.resource", "content" })));
  private final LocalUriFetcherFactory<Data> factory;
  
  public UriLoader(LocalUriFetcherFactory paramLocalUriFetcherFactory)
  {
    factory = paramLocalUriFetcherFactory;
  }
  
  public ModelLoader.LoadData buildLoadData(Uri paramUri, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new ModelLoader.LoadData(new ObjectKey(paramUri), factory.build(paramUri));
  }
  
  public boolean handles(Uri paramUri)
  {
    return SCHEMES.contains(paramUri.getScheme());
  }
  
  public static final class AssetFileDescriptorFactory
    implements ModelLoaderFactory<Uri, AssetFileDescriptor>, UriLoader.LocalUriFetcherFactory<AssetFileDescriptor>
  {
    private final ContentResolver contentResolver;
    
    public AssetFileDescriptorFactory(ContentResolver paramContentResolver)
    {
      contentResolver = paramContentResolver;
    }
    
    public DataFetcher build(Uri paramUri)
    {
      return new AssetFileDescriptorLocalUriFetcher(contentResolver, paramUri);
    }
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new UriLoader(this);
    }
    
    public void teardown() {}
  }
  
  public static class FileDescriptorFactory
    implements ModelLoaderFactory<Uri, ParcelFileDescriptor>, UriLoader.LocalUriFetcherFactory<ParcelFileDescriptor>
  {
    private final ContentResolver contentResolver;
    
    public FileDescriptorFactory(ContentResolver paramContentResolver)
    {
      contentResolver = paramContentResolver;
    }
    
    public DataFetcher build(Uri paramUri)
    {
      return new FileDescriptorLocalUriFetcher(contentResolver, paramUri);
    }
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new UriLoader(this);
    }
    
    public void teardown() {}
  }
  
  public static abstract interface LocalUriFetcherFactory<Data>
  {
    public abstract DataFetcher build(Uri paramUri);
  }
  
  public static class StreamFactory
    implements ModelLoaderFactory<Uri, InputStream>, UriLoader.LocalUriFetcherFactory<InputStream>
  {
    private final ContentResolver contentResolver;
    
    public StreamFactory(ContentResolver paramContentResolver)
    {
      contentResolver = paramContentResolver;
    }
    
    public DataFetcher build(Uri paramUri)
    {
      return new StreamLocalUriFetcher(contentResolver, paramUri);
    }
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new UriLoader(this);
    }
    
    public void teardown() {}
  }
}
