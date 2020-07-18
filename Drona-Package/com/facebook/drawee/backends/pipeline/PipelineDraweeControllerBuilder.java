package com.facebook.drawee.backends.pipeline;

import android.content.Context;
import android.net.Uri;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableList;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.info.ImageOriginListener;
import com.facebook.drawee.backends.pipeline.info.ImagePerfDataListener;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder.CacheLevel;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.drawable.DrawableFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.util.Set;
import javax.annotation.Nullable;

public class PipelineDraweeControllerBuilder
  extends AbstractDraweeControllerBuilder<PipelineDraweeControllerBuilder, ImageRequest, CloseableReference<CloseableImage>, ImageInfo>
{
  @Nullable
  private ImmutableList<DrawableFactory> mCustomDrawableFactories;
  @Nullable
  private ImageOriginListener mImageOriginListener;
  @Nullable
  private ImagePerfDataListener mImagePerfDataListener;
  private final ImagePipeline mImagePipeline;
  private final PipelineDraweeControllerFactory mPipelineDraweeControllerFactory;
  
  public PipelineDraweeControllerBuilder(Context paramContext, PipelineDraweeControllerFactory paramPipelineDraweeControllerFactory, ImagePipeline paramImagePipeline, Set paramSet)
  {
    super(paramContext, paramSet);
    mImagePipeline = paramImagePipeline;
    mPipelineDraweeControllerFactory = paramPipelineDraweeControllerFactory;
  }
  
  public static ImageRequest.RequestLevel convertCacheLevelToRequestLevel(AbstractDraweeControllerBuilder.CacheLevel paramCacheLevel)
  {
    switch (1.$SwitchMap$com$facebook$drawee$controller$AbstractDraweeControllerBuilder$CacheLevel[paramCacheLevel.ordinal()])
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Cache level");
      localStringBuilder.append(paramCacheLevel);
      localStringBuilder.append("is not supported. ");
      throw new RuntimeException(localStringBuilder.toString());
    case 3: 
      return ImageRequest.RequestLevel.BITMAP_MEMORY_CACHE;
    case 2: 
      return ImageRequest.RequestLevel.DISK_CACHE;
    }
    return ImageRequest.RequestLevel.FULL_FETCH;
  }
  
  private CacheKey getCacheKey()
  {
    ImageRequest localImageRequest = (ImageRequest)getImageRequest();
    CacheKeyFactory localCacheKeyFactory = mImagePipeline.getCacheKeyFactory();
    if ((localCacheKeyFactory != null) && (localImageRequest != null))
    {
      if (localImageRequest.getPostprocessor() != null) {
        return localCacheKeyFactory.getPostprocessedBitmapCacheKey(localImageRequest, getCallerContext());
      }
      return localCacheKeyFactory.getBitmapCacheKey(localImageRequest, getCallerContext());
    }
    return null;
  }
  
  protected DataSource getDataSourceForRequest(DraweeController paramDraweeController, String paramString, ImageRequest paramImageRequest, Object paramObject, AbstractDraweeControllerBuilder.CacheLevel paramCacheLevel)
  {
    return mImagePipeline.fetchDecodedImage(paramImageRequest, paramObject, convertCacheLevelToRequestLevel(paramCacheLevel), getRequestListener(paramDraweeController));
  }
  
  protected RequestListener getRequestListener(DraweeController paramDraweeController)
  {
    if ((paramDraweeController instanceof PipelineDraweeController)) {
      return ((PipelineDraweeController)paramDraweeController).getRequestListener();
    }
    return null;
  }
  
  protected PipelineDraweeController obtainController()
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("PipelineDraweeControllerBuilder#obtainController");
    }
    try
    {
      Object localObject = getOldController();
      String str = AbstractDraweeControllerBuilder.generateUniqueControllerId();
      boolean bool = localObject instanceof PipelineDraweeController;
      if (bool) {
        localObject = (PipelineDraweeController)localObject;
      } else {
        localObject = mPipelineDraweeControllerFactory.newController();
      }
      ((PipelineDraweeController)localObject).initialize(obtainDataSourceSupplier((DraweeController)localObject, str), str, getCacheKey(), getCallerContext(), mCustomDrawableFactories, mImageOriginListener);
      ((PipelineDraweeController)localObject).initializePerformanceMonitoring(mImagePerfDataListener);
      if (FrescoSystrace.isTracing())
      {
        FrescoSystrace.endSection();
        return localObject;
      }
    }
    catch (Throwable localThrowable)
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      throw localThrowable;
    }
    return localThrowable;
  }
  
  public PipelineDraweeControllerBuilder setCustomDrawableFactories(ImmutableList paramImmutableList)
  {
    mCustomDrawableFactories = paramImmutableList;
    return (PipelineDraweeControllerBuilder)getThis();
  }
  
  public PipelineDraweeControllerBuilder setCustomDrawableFactories(DrawableFactory... paramVarArgs)
  {
    Preconditions.checkNotNull(paramVarArgs);
    return setCustomDrawableFactories(ImmutableList.of(paramVarArgs));
  }
  
  public PipelineDraweeControllerBuilder setCustomDrawableFactory(DrawableFactory paramDrawableFactory)
  {
    Preconditions.checkNotNull(paramDrawableFactory);
    return setCustomDrawableFactories(ImmutableList.of(new DrawableFactory[] { paramDrawableFactory }));
  }
  
  public PipelineDraweeControllerBuilder setImageOriginListener(ImageOriginListener paramImageOriginListener)
  {
    mImageOriginListener = paramImageOriginListener;
    return (PipelineDraweeControllerBuilder)getThis();
  }
  
  public PipelineDraweeControllerBuilder setPerfDataListener(ImagePerfDataListener paramImagePerfDataListener)
  {
    mImagePerfDataListener = paramImagePerfDataListener;
    return (PipelineDraweeControllerBuilder)getThis();
  }
  
  public PipelineDraweeControllerBuilder setUri(Uri paramUri)
  {
    if (paramUri == null) {
      return (PipelineDraweeControllerBuilder)super.setImageRequest(null);
    }
    return (PipelineDraweeControllerBuilder)super.setImageRequest(ImageRequestBuilder.newBuilderWithSource(paramUri).setRotationOptions(RotationOptions.autoRotateAtRenderTime()).build());
  }
  
  public PipelineDraweeControllerBuilder setUri(String paramString)
  {
    if ((paramString != null) && (!paramString.isEmpty())) {
      return setUri(Uri.parse(paramString));
    }
    return (PipelineDraweeControllerBuilder)super.setImageRequest(ImageRequest.fromUri(paramString));
  }
}