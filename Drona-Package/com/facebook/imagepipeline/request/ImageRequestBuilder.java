package com.facebook.imagepipeline.request;

import android.net.Uri;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.util.UriUtil;
import com.facebook.imagepipeline.common.BytesRange;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineConfig.DefaultImageRequestConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import javax.annotation.Nullable;

public class ImageRequestBuilder
{
  @Nullable
  private BytesRange mBytesRange = null;
  private ImageRequest.CacheChoice mCacheChoice = ImageRequest.CacheChoice.DEFAULT;
  @Nullable
  private Boolean mDecodePrefetches = null;
  private boolean mDiskCacheEnabled = true;
  private ImageDecodeOptions mImageDecodeOptions = ImageDecodeOptions.defaults();
  private boolean mLocalThumbnailPreviewsEnabled = false;
  private ImageRequest.RequestLevel mLowestPermittedRequestLevel = ImageRequest.RequestLevel.FULL_FETCH;
  private boolean mMemoryCacheEnabled = true;
  @Nullable
  private Postprocessor mPostprocessor = null;
  private boolean mProgressiveRenderingEnabled = ImagePipelineConfig.getDefaultImageRequestConfig().isProgressiveRenderingEnabled();
  @Nullable
  private RequestListener mRequestListener;
  private Priority mRequestPriority = Priority.HIGH;
  @Nullable
  private ResizeOptions mResizeOptions = null;
  @Nullable
  private Boolean mResizingAllowedOverride = null;
  @Nullable
  private RotationOptions mRotationOptions = null;
  private Uri mSourceUri = null;
  
  private ImageRequestBuilder() {}
  
  public static ImageRequestBuilder fromRequest(ImageRequest paramImageRequest)
  {
    return newBuilderWithSource(paramImageRequest.getSourceUri()).setImageDecodeOptions(paramImageRequest.getImageDecodeOptions()).setBytesRange(paramImageRequest.getBytesRange()).setCacheChoice(paramImageRequest.getCacheChoice()).setLocalThumbnailPreviewsEnabled(paramImageRequest.getLocalThumbnailPreviewsEnabled()).setLowestPermittedRequestLevel(paramImageRequest.getLowestPermittedRequestLevel()).setPostprocessor(paramImageRequest.getPostprocessor()).setProgressiveRenderingEnabled(paramImageRequest.getProgressiveRenderingEnabled()).setRequestPriority(paramImageRequest.getPriority()).setResizeOptions(paramImageRequest.getResizeOptions()).setRequestListener(paramImageRequest.getRequestListener()).setRotationOptions(paramImageRequest.getRotationOptions()).setShouldDecodePrefetches(paramImageRequest.shouldDecodePrefetches());
  }
  
  public static ImageRequestBuilder newBuilderWithResourceId(int paramInt)
  {
    return newBuilderWithSource(UriUtil.getUriForResourceId(paramInt));
  }
  
  public static ImageRequestBuilder newBuilderWithSource(Uri paramUri)
  {
    return new ImageRequestBuilder().setSource(paramUri);
  }
  
  public ImageRequest build()
  {
    validate();
    return new ImageRequest(this);
  }
  
  public ImageRequestBuilder disableDiskCache()
  {
    mDiskCacheEnabled = false;
    return this;
  }
  
  public ImageRequestBuilder disableMemoryCache()
  {
    mMemoryCacheEnabled = false;
    return this;
  }
  
  public BytesRange getBytesRange()
  {
    return mBytesRange;
  }
  
  public ImageRequest.CacheChoice getCacheChoice()
  {
    return mCacheChoice;
  }
  
  public ImageDecodeOptions getImageDecodeOptions()
  {
    return mImageDecodeOptions;
  }
  
  public ImageRequest.RequestLevel getLowestPermittedRequestLevel()
  {
    return mLowestPermittedRequestLevel;
  }
  
  public Postprocessor getPostprocessor()
  {
    return mPostprocessor;
  }
  
  public RequestListener getRequestListener()
  {
    return mRequestListener;
  }
  
  public Priority getRequestPriority()
  {
    return mRequestPriority;
  }
  
  public ResizeOptions getResizeOptions()
  {
    return mResizeOptions;
  }
  
  public Boolean getResizingAllowedOverride()
  {
    return mResizingAllowedOverride;
  }
  
  public RotationOptions getRotationOptions()
  {
    return mRotationOptions;
  }
  
  public Uri getSourceUri()
  {
    return mSourceUri;
  }
  
  public boolean isDiskCacheEnabled()
  {
    return (mDiskCacheEnabled) && (UriUtil.isNetworkUri(mSourceUri));
  }
  
  public boolean isLocalThumbnailPreviewsEnabled()
  {
    return mLocalThumbnailPreviewsEnabled;
  }
  
  public boolean isMemoryCacheEnabled()
  {
    return mMemoryCacheEnabled;
  }
  
  public boolean isProgressiveRenderingEnabled()
  {
    return mProgressiveRenderingEnabled;
  }
  
  public ImageRequestBuilder setAutoRotateEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {
      return setRotationOptions(RotationOptions.autoRotate());
    }
    return setRotationOptions(RotationOptions.disableRotation());
  }
  
  public ImageRequestBuilder setBytesRange(BytesRange paramBytesRange)
  {
    mBytesRange = paramBytesRange;
    return this;
  }
  
  public ImageRequestBuilder setCacheChoice(ImageRequest.CacheChoice paramCacheChoice)
  {
    mCacheChoice = paramCacheChoice;
    return this;
  }
  
  public ImageRequestBuilder setImageDecodeOptions(ImageDecodeOptions paramImageDecodeOptions)
  {
    mImageDecodeOptions = paramImageDecodeOptions;
    return this;
  }
  
  public ImageRequestBuilder setLocalThumbnailPreviewsEnabled(boolean paramBoolean)
  {
    mLocalThumbnailPreviewsEnabled = paramBoolean;
    return this;
  }
  
  public ImageRequestBuilder setLowestPermittedRequestLevel(ImageRequest.RequestLevel paramRequestLevel)
  {
    mLowestPermittedRequestLevel = paramRequestLevel;
    return this;
  }
  
  public ImageRequestBuilder setPostprocessor(Postprocessor paramPostprocessor)
  {
    mPostprocessor = paramPostprocessor;
    return this;
  }
  
  public ImageRequestBuilder setProgressiveRenderingEnabled(boolean paramBoolean)
  {
    mProgressiveRenderingEnabled = paramBoolean;
    return this;
  }
  
  public ImageRequestBuilder setRequestListener(RequestListener paramRequestListener)
  {
    mRequestListener = paramRequestListener;
    return this;
  }
  
  public ImageRequestBuilder setRequestPriority(Priority paramPriority)
  {
    mRequestPriority = paramPriority;
    return this;
  }
  
  public ImageRequestBuilder setResizeOptions(ResizeOptions paramResizeOptions)
  {
    mResizeOptions = paramResizeOptions;
    return this;
  }
  
  public ImageRequestBuilder setResizingAllowedOverride(Boolean paramBoolean)
  {
    mResizingAllowedOverride = paramBoolean;
    return this;
  }
  
  public ImageRequestBuilder setRotationOptions(RotationOptions paramRotationOptions)
  {
    mRotationOptions = paramRotationOptions;
    return this;
  }
  
  public ImageRequestBuilder setShouldDecodePrefetches(Boolean paramBoolean)
  {
    mDecodePrefetches = paramBoolean;
    return this;
  }
  
  public ImageRequestBuilder setSource(Uri paramUri)
  {
    Preconditions.checkNotNull(paramUri);
    mSourceUri = paramUri;
    return this;
  }
  
  public Boolean shouldDecodePrefetches()
  {
    return mDecodePrefetches;
  }
  
  protected void validate()
  {
    Uri localUri;
    if (mSourceUri != null) {
      if (UriUtil.isLocalResourceUri(mSourceUri)) {
        if (mSourceUri.isAbsolute()) {
          if (!mSourceUri.getPath().isEmpty()) {
            localUri = mSourceUri;
          }
        }
      }
    }
    try
    {
      Integer.parseInt(localUri.getPath().substring(1));
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;) {}
    }
    throw new BuilderException("Resource URI path must be a resource id.");
    throw new BuilderException("Resource URI must not be empty");
    throw new BuilderException("Resource URI path must be absolute.");
    if (UriUtil.isLocalAssetUri(mSourceUri))
    {
      if (mSourceUri.isAbsolute()) {
        return;
      }
      throw new BuilderException("Asset URI path must be absolute.");
      throw new BuilderException("Source must be set!");
    }
  }
  
  public static class BuilderException
    extends RuntimeException
  {
    public BuilderException(String paramString)
    {
      super();
    }
  }
}