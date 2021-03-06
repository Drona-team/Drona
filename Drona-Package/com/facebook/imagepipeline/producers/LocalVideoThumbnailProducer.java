package com.facebook.imagepipeline.producers;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Video.Media;
import com.facebook.common.executors.StatefulRunnable;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.imagepipeline.bitmaps.SimpleBitmapReleaser;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.Executor;

public class LocalVideoThumbnailProducer
  implements Producer<CloseableReference<CloseableImage>>
{
  @VisibleForTesting
  static final String CREATED_THUMBNAIL = "createdThumbnail";
  public static final String PRODUCER_NAME = "VideoThumbnailProducer";
  private final ContentResolver mContentResolver;
  private final Executor mExecutor;
  
  public LocalVideoThumbnailProducer(Executor paramExecutor, ContentResolver paramContentResolver)
  {
    mExecutor = paramExecutor;
    mContentResolver = paramContentResolver;
  }
  
  private static int calculateKind(ImageRequest paramImageRequest)
  {
    if ((paramImageRequest.getPreferredWidth() <= 96) && (paramImageRequest.getPreferredHeight() <= 96)) {
      return 3;
    }
    return 1;
  }
  
  private static Bitmap createThumbnailFromContentProvider(ContentResolver paramContentResolver, Uri paramUri)
  {
    if (Build.VERSION.SDK_INT >= 10) {}
    try
    {
      paramContentResolver = paramContentResolver.openFileDescriptor(paramUri, "r");
      paramUri = new MediaMetadataRetriever();
      paramUri.setDataSource(paramContentResolver.getFileDescriptor());
      paramContentResolver = paramUri.getFrameAtTime(-1L);
      return paramContentResolver;
    }
    catch (FileNotFoundException paramContentResolver) {}
    return null;
    return null;
  }
  
  private String getLocalFilePath(ImageRequest paramImageRequest)
  {
    Uri localUri = paramImageRequest.getSourceUri();
    if (UriUtil.isLocalFileUri(localUri)) {
      return paramImageRequest.getSourceFile().getPath();
    }
    if (UriUtil.isLocalContentUri(localUri))
    {
      String str;
      String[] arrayOfString;
      if ((Build.VERSION.SDK_INT >= 19) && ("com.android.providers.media.documents".equals(localUri.getAuthority())))
      {
        str = DocumentsContract.getDocumentId(localUri);
        paramImageRequest = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        arrayOfString = new String[1];
        arrayOfString[0] = str.split(":")[1];
        str = "_id=?";
      }
      else
      {
        str = null;
        arrayOfString = null;
        paramImageRequest = localUri;
      }
      paramImageRequest = mContentResolver.query(paramImageRequest, new String[] { "_data" }, str, arrayOfString, null);
      if (paramImageRequest != null) {
        try
        {
          boolean bool = paramImageRequest.moveToFirst();
          if (bool)
          {
            str = paramImageRequest.getString(paramImageRequest.getColumnIndexOrThrow("_data"));
            if (paramImageRequest == null) {
              break label182;
            }
            paramImageRequest.close();
            return str;
          }
        }
        catch (Throwable localThrowable)
        {
          if (paramImageRequest != null) {
            paramImageRequest.close();
          }
          throw localThrowable;
        }
      }
      if (paramImageRequest != null) {
        paramImageRequest.close();
      }
    }
    else
    {
      return null;
      label182:
      return localThrowable;
    }
    return null;
  }
  
  public void produceResults(final Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    final ProducerListener localProducerListener = paramProducerContext.getListener();
    final String str = paramProducerContext.getId();
    paramConsumer = new StatefulProducerRunnable(paramConsumer, localProducerListener, "VideoThumbnailProducer", str)
    {
      protected void disposeResult(CloseableReference paramAnonymousCloseableReference)
      {
        CloseableReference.closeSafely(paramAnonymousCloseableReference);
      }
      
      protected Map getExtraMapOnSuccess(CloseableReference paramAnonymousCloseableReference)
      {
        boolean bool;
        if (paramAnonymousCloseableReference != null) {
          bool = true;
        } else {
          bool = false;
        }
        return ImmutableMap.of("createdThumbnail", String.valueOf(bool));
      }
      
      protected CloseableReference getResult()
        throws Exception
      {
        Object localObject = LocalVideoThumbnailProducer.this;
        ImageRequest localImageRequest = val$imageRequest;
        try
        {
          localObject = ((LocalVideoThumbnailProducer)localObject).getLocalFilePath(localImageRequest);
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          for (;;) {}
        }
        localObject = null;
        if (localObject != null) {
          localObject = ThumbnailUtils.createVideoThumbnail((String)localObject, LocalVideoThumbnailProducer.calculateKind(val$imageRequest));
        } else {
          localObject = LocalVideoThumbnailProducer.createThumbnailFromContentProvider(mContentResolver, val$imageRequest.getSourceUri());
        }
        if (localObject == null) {
          return null;
        }
        return CloseableReference.of(new CloseableStaticBitmap((Bitmap)localObject, SimpleBitmapReleaser.getInstance(), ImmutableQualityInfo.FULL_QUALITY, 0));
      }
      
      protected void onFailure(Exception paramAnonymousException)
      {
        super.onFailure(paramAnonymousException);
        localProducerListener.onUltimateProducerReached(str, "VideoThumbnailProducer", false);
      }
      
      protected void onSuccess(CloseableReference paramAnonymousCloseableReference)
      {
        super.onSuccess(paramAnonymousCloseableReference);
        ProducerListener localProducerListener = localProducerListener;
        String str = str;
        boolean bool;
        if (paramAnonymousCloseableReference != null) {
          bool = true;
        } else {
          bool = false;
        }
        localProducerListener.onUltimateProducerReached(str, "VideoThumbnailProducer", bool);
      }
    };
    paramProducerContext.addCallbacks(new BaseProducerContextCallbacks()
    {
      public void onCancellationRequested()
      {
        paramConsumer.cancel();
      }
    });
    mExecutor.execute(paramConsumer);
  }
}
