package com.facebook.imagepipeline.producers;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.util.UriUtil;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

public class LocalContentUriFetchProducer
  extends LocalFetchProducer
{
  public static final String PRODUCER_NAME = "LocalContentUriFetchProducer";
  private static final String[] PROJECTION = { "_id", "_data" };
  private final ContentResolver mContentResolver;
  
  public LocalContentUriFetchProducer(Executor paramExecutor, PooledByteBufferFactory paramPooledByteBufferFactory, ContentResolver paramContentResolver)
  {
    super(paramExecutor, paramPooledByteBufferFactory);
    mContentResolver = paramContentResolver;
  }
  
  private EncodedImage getCameraImage(Uri paramUri)
    throws IOException
  {
    paramUri = mContentResolver.query(paramUri, PROJECTION, null, null, null);
    if (paramUri == null) {
      return null;
    }
    try
    {
      int i = paramUri.getCount();
      if (i == 0)
      {
        paramUri.close();
        return null;
      }
      paramUri.moveToFirst();
      Object localObject = paramUri.getString(paramUri.getColumnIndex("_data"));
      if (localObject != null)
      {
        localObject = getEncodedImage(new FileInputStream((String)localObject), getLength((String)localObject));
        paramUri.close();
        return localObject;
      }
      paramUri.close();
      return null;
    }
    catch (Throwable localThrowable)
    {
      paramUri.close();
      throw localThrowable;
    }
  }
  
  private static int getLength(String paramString)
  {
    if (paramString == null) {
      return -1;
    }
    return (int)new File(paramString).length();
  }
  
  protected EncodedImage getEncodedImage(ImageRequest paramImageRequest)
    throws IOException
  {
    Uri localUri = paramImageRequest.getSourceUri();
    if (UriUtil.isLocalContactUri(localUri)) {
      if (localUri.toString().endsWith("/photo")) {
        paramImageRequest = mContentResolver.openInputStream(localUri);
      } else if (localUri.toString().endsWith("/display_photo")) {
        paramImageRequest = mContentResolver;
      }
    }
    try
    {
      paramImageRequest = paramImageRequest.openAssetFileDescriptor(localUri, "r").createInputStream();
    }
    catch (IOException paramImageRequest)
    {
      InputStream localInputStream;
      for (;;) {}
    }
    paramImageRequest = new StringBuilder();
    paramImageRequest.append("Contact photo does not exist: ");
    paramImageRequest.append(localUri);
    throw new IOException(paramImageRequest.toString());
    localInputStream = ContactsContract.Contacts.openContactPhotoInputStream(mContentResolver, localUri);
    paramImageRequest = localInputStream;
    if (localInputStream != null) {
      return getEncodedImage((InputStream)paramImageRequest, -1);
    }
    paramImageRequest = new StringBuilder();
    paramImageRequest.append("Contact photo does not exist: ");
    paramImageRequest.append(localUri);
    throw new IOException(paramImageRequest.toString());
    if (UriUtil.isLocalCameraUri(localUri))
    {
      paramImageRequest = getCameraImage(localUri);
      if (paramImageRequest != null) {
        return paramImageRequest;
      }
    }
    return getEncodedImage(mContentResolver.openInputStream(localUri), -1);
  }
  
  protected String getProducerName()
  {
    return "LocalContentUriFetchProducer";
  }
}
