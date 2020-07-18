package com.facebook.imagepipeline.producers;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images.Thumbnails;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.util.UriUtil;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imageutils.JfifUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Executor;

public class LocalContentUriThumbnailFetchProducer
  extends LocalFetchProducer
  implements ThumbnailProducer<EncodedImage>
{
  private static final Rect MICRO_THUMBNAIL_DIMENSIONS = new Rect(0, 0, 96, 96);
  private static final Rect MINI_THUMBNAIL_DIMENSIONS;
  private static final int NO_THUMBNAIL = 0;
  public static final String PRODUCER_NAME = "LocalContentUriThumbnailFetchProducer";
  private static final String[] PROJECTION;
  private static final Class<?> TAG = LocalContentUriThumbnailFetchProducer.class;
  private static final String[] THUMBNAIL_PROJECTION;
  private final ContentResolver mContentResolver;
  
  static
  {
    PROJECTION = new String[] { "_id", "_data" };
    THUMBNAIL_PROJECTION = new String[] { "_data" };
    MINI_THUMBNAIL_DIMENSIONS = new Rect(0, 0, 512, 384);
  }
  
  public LocalContentUriThumbnailFetchProducer(Executor paramExecutor, PooledByteBufferFactory paramPooledByteBufferFactory, ContentResolver paramContentResolver)
  {
    super(paramExecutor, paramPooledByteBufferFactory);
    mContentResolver = paramContentResolver;
  }
  
  private EncodedImage getCameraImage(Uri paramUri, ResizeOptions paramResizeOptions)
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
      String str = paramUri.getString(paramUri.getColumnIndex("_data"));
      if (paramResizeOptions != null)
      {
        paramResizeOptions = getThumbnail(paramResizeOptions, paramUri.getInt(paramUri.getColumnIndex("_id")));
        if (paramResizeOptions != null)
        {
          paramResizeOptions.setRotationAngle(getRotationAngle(str));
          paramUri.close();
          return paramResizeOptions;
        }
      }
      paramUri.close();
      return null;
    }
    catch (Throwable paramResizeOptions)
    {
      paramUri.close();
      throw paramResizeOptions;
    }
  }
  
  private static int getLength(String paramString)
  {
    if (paramString == null) {
      return -1;
    }
    return (int)new File(paramString).length();
  }
  
  private static int getRotationAngle(String paramString)
  {
    if (paramString != null) {
      try
      {
        int i = JfifUtil.getAutoRotateAngleFromOrientation(new ExifInterface(paramString).getAttributeInt("Orientation", 1));
        return i;
      }
      catch (IOException localIOException)
      {
        FLog.e(TAG, localIOException, "Unable to retrieve thumbnail rotation for %s", new Object[] { paramString });
      }
    }
    return 0;
  }
  
  private EncodedImage getThumbnail(ResizeOptions paramResizeOptions, int paramInt)
    throws IOException
  {
    int i = getThumbnailKind(paramResizeOptions);
    if (i == 0) {
      return null;
    }
    Object localObject;
    try
    {
      Cursor localCursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(mContentResolver, paramInt, i, THUMBNAIL_PROJECTION);
      paramResizeOptions = localCursor;
      if (localCursor == null)
      {
        if (localCursor == null) {
          break label176;
        }
        localCursor.close();
        return null;
      }
      try
      {
        localCursor.moveToFirst();
        paramInt = localCursor.getCount();
        if (paramInt > 0)
        {
          localObject = localCursor.getString(localCursor.getColumnIndex("_data"));
          boolean bool = new File((String)localObject).exists();
          if (bool)
          {
            localObject = getEncodedImage(new FileInputStream((String)localObject), getLength((String)localObject));
            if (localCursor == null) {
              break label178;
            }
            localCursor.close();
            return localObject;
          }
        }
        if (localCursor == null) {
          break label181;
        }
        localCursor.close();
        return null;
      }
      catch (Throwable localThrowable1) {}
      if (paramResizeOptions == null) {
        break label173;
      }
    }
    catch (Throwable localThrowable2)
    {
      paramResizeOptions = null;
    }
    paramResizeOptions.close();
    label173:
    throw localThrowable2;
    label176:
    return null;
    label178:
    return localObject;
    label181:
    return null;
  }
  
  private static int getThumbnailKind(ResizeOptions paramResizeOptions)
  {
    if (ThumbnailSizeChecker.isImageBigEnough(MICRO_THUMBNAIL_DIMENSIONS.width(), MICRO_THUMBNAIL_DIMENSIONS.height(), paramResizeOptions)) {
      return 3;
    }
    if (ThumbnailSizeChecker.isImageBigEnough(MINI_THUMBNAIL_DIMENSIONS.width(), MINI_THUMBNAIL_DIMENSIONS.height(), paramResizeOptions)) {
      return 1;
    }
    return 0;
  }
  
  public boolean canProvideImageForSize(ResizeOptions paramResizeOptions)
  {
    return ThumbnailSizeChecker.isImageBigEnough(MINI_THUMBNAIL_DIMENSIONS.width(), MINI_THUMBNAIL_DIMENSIONS.height(), paramResizeOptions);
  }
  
  protected EncodedImage getEncodedImage(ImageRequest paramImageRequest)
    throws IOException
  {
    Uri localUri = paramImageRequest.getSourceUri();
    if (UriUtil.isLocalCameraUri(localUri))
    {
      paramImageRequest = getCameraImage(localUri, paramImageRequest.getResizeOptions());
      if (paramImageRequest != null) {
        return paramImageRequest;
      }
    }
    return null;
  }
  
  protected String getProducerName()
  {
    return "LocalContentUriThumbnailFetchProducer";
  }
}
