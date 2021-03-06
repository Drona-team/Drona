package com.bumptech.glide.load.data;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileDescriptorLocalUriFetcher
  extends LocalUriFetcher<ParcelFileDescriptor>
{
  public FileDescriptorLocalUriFetcher(ContentResolver paramContentResolver, Uri paramUri)
  {
    super(paramContentResolver, paramUri);
  }
  
  protected void close(ParcelFileDescriptor paramParcelFileDescriptor)
    throws IOException
  {
    paramParcelFileDescriptor.close();
  }
  
  public Class getDataClass()
  {
    return ParcelFileDescriptor.class;
  }
  
  protected ParcelFileDescriptor loadResource(Uri paramUri, ContentResolver paramContentResolver)
    throws FileNotFoundException
  {
    paramContentResolver = paramContentResolver.openAssetFileDescriptor(paramUri, "r");
    if (paramContentResolver != null) {
      return paramContentResolver.getParcelFileDescriptor();
    }
    paramContentResolver = new StringBuilder();
    paramContentResolver.append("FileDescriptor is null for: ");
    paramContentResolver.append(paramUri);
    throw new FileNotFoundException(paramContentResolver.toString());
  }
}
