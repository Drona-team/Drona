package com.bumptech.glide.load.model.stream;

import android.content.Context;
import android.net.Uri;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.mediastore.MediaStoreUtil;
import com.bumptech.glide.load.data.mediastore.ThumbFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.resource.bitmap.VideoDecoder;
import com.bumptech.glide.signature.ObjectKey;
import java.io.InputStream;

public class MediaStoreVideoThumbLoader
  implements ModelLoader<Uri, InputStream>
{
  private final Context context;
  
  public MediaStoreVideoThumbLoader(Context paramContext)
  {
    context = paramContext.getApplicationContext();
  }
  
  private boolean isRequestingDefaultFrame(Options paramOptions)
  {
    paramOptions = (Long)paramOptions.getOption(VideoDecoder.TARGET_FRAME);
    return (paramOptions != null) && (paramOptions.longValue() == -1L);
  }
  
  public ModelLoader.LoadData buildLoadData(Uri paramUri, int paramInt1, int paramInt2, Options paramOptions)
  {
    if ((MediaStoreUtil.isThumbnailSize(paramInt1, paramInt2)) && (isRequestingDefaultFrame(paramOptions))) {
      return new ModelLoader.LoadData(new ObjectKey(paramUri), ThumbFetcher.buildVideoFetcher(context, paramUri));
    }
    return null;
  }
  
  public boolean handles(Uri paramUri)
  {
    return MediaStoreUtil.isMediaStoreVideoUri(paramUri);
  }
  
  public static class Factory
    implements ModelLoaderFactory<Uri, InputStream>
  {
    private final Context context;
    
    public Factory(Context paramContext)
    {
      context = paramContext;
    }
    
    public ModelLoader build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new MediaStoreVideoThumbLoader(context);
    }
    
    public void teardown() {}
  }
}
