package com.bumptech.glide.load.data;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class AssetFileDescriptorLocalUriFetcher
  extends LocalUriFetcher<AssetFileDescriptor>
{
  public AssetFileDescriptorLocalUriFetcher(ContentResolver paramContentResolver, Uri paramUri)
  {
    super(paramContentResolver, paramUri);
  }
  
  protected void close(AssetFileDescriptor paramAssetFileDescriptor)
    throws IOException
  {
    paramAssetFileDescriptor.close();
  }
  
  public Class getDataClass()
  {
    return AssetFileDescriptor.class;
  }
  
  protected AssetFileDescriptor loadResource(Uri paramUri, ContentResolver paramContentResolver)
    throws FileNotFoundException
  {
    paramContentResolver = paramContentResolver.openAssetFileDescriptor(paramUri, "r");
    if (paramContentResolver != null) {
      return paramContentResolver;
    }
    paramContentResolver = new StringBuilder();
    paramContentResolver.append("FileDescriptor is null for: ");
    paramContentResolver.append(paramUri);
    throw new FileNotFoundException(paramContentResolver.toString());
  }
}
