package com.facebook.drawee.backends.pipeline;

import android.content.res.Resources;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableList;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.components.DeferredReleaser;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.drawable.DrawableFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

public class PipelineDraweeControllerFactory
{
  private DrawableFactory mAnimatedDrawableFactory;
  @Nullable
  private Supplier<Boolean> mDebugOverlayEnabledSupplier;
  private DeferredReleaser mDeferredReleaser;
  @Nullable
  private ImmutableList<DrawableFactory> mDrawableFactories;
  private MemoryCache<CacheKey, CloseableImage> mMemoryCache;
  private Resources mResources;
  private Executor mUiThreadExecutor;
  
  public PipelineDraweeControllerFactory() {}
  
  public void init(Resources paramResources, DeferredReleaser paramDeferredReleaser, DrawableFactory paramDrawableFactory, Executor paramExecutor, MemoryCache paramMemoryCache, ImmutableList paramImmutableList, Supplier paramSupplier)
  {
    mResources = paramResources;
    mDeferredReleaser = paramDeferredReleaser;
    mAnimatedDrawableFactory = paramDrawableFactory;
    mUiThreadExecutor = paramExecutor;
    mMemoryCache = paramMemoryCache;
    mDrawableFactories = paramImmutableList;
    mDebugOverlayEnabledSupplier = paramSupplier;
  }
  
  protected PipelineDraweeController internalCreateController(Resources paramResources, DeferredReleaser paramDeferredReleaser, DrawableFactory paramDrawableFactory, Executor paramExecutor, MemoryCache paramMemoryCache, ImmutableList paramImmutableList)
  {
    return new PipelineDraweeController(paramResources, paramDeferredReleaser, paramDrawableFactory, paramExecutor, paramMemoryCache, paramImmutableList);
  }
  
  public PipelineDraweeController newController()
  {
    PipelineDraweeController localPipelineDraweeController = internalCreateController(mResources, mDeferredReleaser, mAnimatedDrawableFactory, mUiThreadExecutor, mMemoryCache, mDrawableFactories);
    if (mDebugOverlayEnabledSupplier != null) {
      localPipelineDraweeController.setDrawDebugOverlay(((Boolean)mDebugOverlayEnabledSupplier.getFolder()).booleanValue());
    }
    return localPipelineDraweeController;
  }
}
