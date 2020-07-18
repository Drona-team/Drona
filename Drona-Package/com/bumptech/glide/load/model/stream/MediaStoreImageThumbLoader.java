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
import com.bumptech.glide.signature.ObjectKey;
import java.io.InputStream;

public class MediaStoreImageThumbLoader
  implements ModelLoader<Uri, InputStream>
{
  private final Context context;
  
  public MediaStoreImageThumbLoader(Context paramContext)
  {
    context = paramContext.getApplicationContext();
  }
  
  public ModelLoader.LoadData buildLoadData(Uri paramUri, int paramInt1, int paramInt2, Options paramOptions)
  {
    if (MediaStoreUtil.isThumbnailSize(paramInt1, paramInt2)) {
      return new ModelLoader.LoadData(new ObjectKey(paramUri), ThumbFetcher.buildImageFetcher(context, paramUri));
    }
    return null;
  }
  
  public boolean handles(Uri paramUri)
  {
    return MediaStoreUtil.isMediaStoreImageUri(paramUri);
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
      return new MediaStoreImageThumbLoader(context);
    }
    
    public void teardown() {}
  }
}
