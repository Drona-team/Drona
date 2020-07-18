package com.facebook.drawee.backends.pipeline;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableList;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Objects.ToStringHelper;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.logging.FLog;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.time.AwakeTimeSinceBootClock;
import com.facebook.datasource.DataSource;
import com.facebook.drawable.base.DrawableWithCaches;
import com.facebook.drawee.backends.pipeline.debug.DebugOverlayImageOriginListener;
import com.facebook.drawee.backends.pipeline.info.ForwardingImageOriginListener;
import com.facebook.drawee.backends.pipeline.info.ImageOriginListener;
import com.facebook.drawee.backends.pipeline.info.ImageOriginRequestListener;
import com.facebook.drawee.backends.pipeline.info.ImagePerfDataListener;
import com.facebook.drawee.backends.pipeline.info.ImagePerfMonitor;
import com.facebook.drawee.components.DeferredReleaser;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.debug.DebugControllerOverlayDrawable;
import com.facebook.drawee.debug.listener.ImageLoadingTimeControllerListener;
import com.facebook.drawee.drawable.ScaleTypeDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.drawable.DrawableFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.listener.ForwardingRequestListener;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

public class PipelineDraweeController
  extends AbstractDraweeController<CloseableReference<CloseableImage>, ImageInfo>
{
  private static final Class<?> TAG = PipelineDraweeController.class;
  private CacheKey mCacheKey;
  @Nullable
  private ImmutableList<DrawableFactory> mCustomDrawableFactories;
  private Supplier<DataSource<CloseableReference<CloseableImage>>> mDataSourceSupplier;
  private DebugOverlayImageOriginListener mDebugOverlayImageOriginListener;
  private final DrawableFactory mDefaultDrawableFactory;
  private boolean mDrawDebugOverlay;
  @Nullable
  private final ImmutableList<DrawableFactory> mGlobalDrawableFactories;
  @Nullable
  @GuardedBy("this")
  private ImageOriginListener mImageOriginListener;
  @Nullable
  private ImagePerfMonitor mImagePerfMonitor;
  @Nullable
  private final MemoryCache<CacheKey, CloseableImage> mMemoryCache;
  @Nullable
  @GuardedBy("this")
  private Set<RequestListener> mRequestListeners;
  private final Resources mResources;
  
  public PipelineDraweeController(Resources paramResources, DeferredReleaser paramDeferredReleaser, DrawableFactory paramDrawableFactory, Executor paramExecutor, MemoryCache paramMemoryCache, ImmutableList paramImmutableList)
  {
    super(paramDeferredReleaser, paramExecutor, null, null);
    mResources = paramResources;
    mDefaultDrawableFactory = new DefaultDrawableFactory(paramResources, paramDrawableFactory);
    mGlobalDrawableFactories = paramImmutableList;
    mMemoryCache = paramMemoryCache;
  }
  
  private void init(Supplier paramSupplier)
  {
    mDataSourceSupplier = paramSupplier;
    maybeUpdateDebugOverlay(null);
  }
  
  private Drawable maybeCreateDrawableFromFactories(ImmutableList paramImmutableList, CloseableImage paramCloseableImage)
  {
    if (paramImmutableList == null) {
      return null;
    }
    paramImmutableList = paramImmutableList.iterator();
    while (paramImmutableList.hasNext())
    {
      Object localObject = (DrawableFactory)paramImmutableList.next();
      if (((DrawableFactory)localObject).supportsImageType(paramCloseableImage))
      {
        localObject = ((DrawableFactory)localObject).createDrawable(paramCloseableImage);
        if (localObject != null) {
          return localObject;
        }
      }
    }
    return null;
  }
  
  private void maybeUpdateDebugOverlay(CloseableImage paramCloseableImage)
  {
    if (!mDrawDebugOverlay) {
      return;
    }
    if (getControllerOverlay() == null)
    {
      DebugControllerOverlayDrawable localDebugControllerOverlayDrawable = new DebugControllerOverlayDrawable();
      ImageLoadingTimeControllerListener localImageLoadingTimeControllerListener = new ImageLoadingTimeControllerListener(localDebugControllerOverlayDrawable);
      mDebugOverlayImageOriginListener = new DebugOverlayImageOriginListener();
      addControllerListener(localImageLoadingTimeControllerListener);
      setControllerOverlay(localDebugControllerOverlayDrawable);
    }
    if (mImageOriginListener == null) {
      addImageOriginListener(mDebugOverlayImageOriginListener);
    }
    if ((getControllerOverlay() instanceof DebugControllerOverlayDrawable)) {
      updateDebugOverlay(paramCloseableImage, (DebugControllerOverlayDrawable)getControllerOverlay());
    }
  }
  
  public void addImageOriginListener(ImageOriginListener paramImageOriginListener)
  {
    try
    {
      if ((mImageOriginListener instanceof ForwardingImageOriginListener)) {
        ((ForwardingImageOriginListener)mImageOriginListener).addImageOriginListener(paramImageOriginListener);
      } else if (mImageOriginListener != null) {
        mImageOriginListener = new ForwardingImageOriginListener(new ImageOriginListener[] { mImageOriginListener, paramImageOriginListener });
      } else {
        mImageOriginListener = paramImageOriginListener;
      }
      return;
    }
    catch (Throwable paramImageOriginListener)
    {
      throw paramImageOriginListener;
    }
  }
  
  public void addRequestListener(RequestListener paramRequestListener)
  {
    try
    {
      if (mRequestListeners == null) {
        mRequestListeners = new HashSet();
      }
      mRequestListeners.add(paramRequestListener);
      return;
    }
    catch (Throwable paramRequestListener)
    {
      throw paramRequestListener;
    }
  }
  
  protected void clearImageOriginListeners()
  {
    try
    {
      mImageOriginListener = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  protected Drawable createDrawable(CloseableReference paramCloseableReference)
  {
    try
    {
      boolean bool = FrescoSystrace.isTracing();
      if (bool) {
        FrescoSystrace.beginSection("PipelineDraweeController#createDrawable");
      }
      Preconditions.checkState(CloseableReference.isValid(paramCloseableReference));
      paramCloseableReference = (CloseableImage)paramCloseableReference.get();
      maybeUpdateDebugOverlay(paramCloseableReference);
      Object localObject = maybeCreateDrawableFromFactories(mCustomDrawableFactories, paramCloseableReference);
      if (localObject != null)
      {
        paramCloseableReference = (CloseableReference)localObject;
        if (FrescoSystrace.isTracing())
        {
          FrescoSystrace.endSection();
          return localObject;
        }
      }
      else
      {
        localObject = maybeCreateDrawableFromFactories(mGlobalDrawableFactories, paramCloseableReference);
        if (localObject != null)
        {
          paramCloseableReference = (CloseableReference)localObject;
          if (FrescoSystrace.isTracing())
          {
            FrescoSystrace.endSection();
            return localObject;
          }
        }
        else
        {
          localObject = mDefaultDrawableFactory.createDrawable(paramCloseableReference);
          if (localObject != null)
          {
            paramCloseableReference = (CloseableReference)localObject;
            if (FrescoSystrace.isTracing())
            {
              FrescoSystrace.endSection();
              return localObject;
            }
          }
          else
          {
            localObject = new StringBuilder();
            ((StringBuilder)localObject).append("Unrecognized image class: ");
            ((StringBuilder)localObject).append(paramCloseableReference);
            throw new UnsupportedOperationException(((StringBuilder)localObject).toString());
          }
        }
      }
    }
    catch (Throwable paramCloseableReference)
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      throw paramCloseableReference;
    }
    return paramCloseableReference;
  }
  
  protected CacheKey getCacheKey()
  {
    return mCacheKey;
  }
  
  protected CloseableReference getCachedImage()
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("PipelineDraweeController#getCachedImage");
    }
    try
    {
      Object localObject = mMemoryCache;
      if (localObject != null)
      {
        localObject = mCacheKey;
        if (localObject != null)
        {
          localObject = mMemoryCache.cache(mCacheKey);
          if (localObject != null)
          {
            boolean bool = ((CloseableImage)((CloseableReference)localObject).get()).getQualityInfo().isOfFullQuality();
            if (!bool)
            {
              ((CloseableReference)localObject).close();
              if (!FrescoSystrace.isTracing()) {
                break label119;
              }
              FrescoSystrace.endSection();
              return null;
            }
          }
          if (!FrescoSystrace.isTracing()) {
            break label121;
          }
          FrescoSystrace.endSection();
          return localObject;
        }
      }
      if (!FrescoSystrace.isTracing()) {
        break label123;
      }
      FrescoSystrace.endSection();
      return null;
    }
    catch (Throwable localThrowable)
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      throw localThrowable;
    }
    label119:
    return null;
    label121:
    return localThrowable;
    label123:
    return null;
  }
  
  protected DataSource getDataSource()
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("PipelineDraweeController#getDataSource");
    }
    if (FLog.isLoggable(2)) {
      FLog.v(TAG, "controller %x: getDataSource", Integer.valueOf(System.identityHashCode(this)));
    }
    DataSource localDataSource = (DataSource)mDataSourceSupplier.getFolder();
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
    return localDataSource;
  }
  
  protected Supplier getDataSourceSupplier()
  {
    return mDataSourceSupplier;
  }
  
  protected int getImageHash(CloseableReference paramCloseableReference)
  {
    if (paramCloseableReference != null) {
      return paramCloseableReference.getValueHash();
    }
    return 0;
  }
  
  protected ImageInfo getImageInfo(CloseableReference paramCloseableReference)
  {
    Preconditions.checkState(CloseableReference.isValid(paramCloseableReference));
    return (ImageInfo)paramCloseableReference.get();
  }
  
  public RequestListener getRequestListener()
  {
    ImageOriginRequestListener localImageOriginRequestListener = null;
    try
    {
      if (mImageOriginListener != null) {
        localImageOriginRequestListener = new ImageOriginRequestListener(getId(), mImageOriginListener);
      }
      if (mRequestListeners != null)
      {
        ForwardingRequestListener localForwardingRequestListener = new ForwardingRequestListener(mRequestListeners);
        if (localImageOriginRequestListener != null) {
          localForwardingRequestListener.addRequestListener(localImageOriginRequestListener);
        }
        return localForwardingRequestListener;
      }
      return localImageOriginRequestListener;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  protected Resources getResources()
  {
    return mResources;
  }
  
  public void initialize(Supplier paramSupplier, String paramString, CacheKey paramCacheKey, Object paramObject, ImmutableList paramImmutableList, ImageOriginListener paramImageOriginListener)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("PipelineDraweeController#initialize");
    }
    super.initialize(paramString, paramObject);
    init(paramSupplier);
    mCacheKey = paramCacheKey;
    setCustomDrawableFactories(paramImmutableList);
    clearImageOriginListeners();
    maybeUpdateDebugOverlay(null);
    addImageOriginListener(paramImageOriginListener);
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
  }
  
  protected void initializePerformanceMonitoring(ImagePerfDataListener paramImagePerfDataListener)
  {
    try
    {
      if (mImagePerfMonitor != null) {
        mImagePerfMonitor.reset();
      }
      if (paramImagePerfDataListener != null)
      {
        if (mImagePerfMonitor == null) {
          mImagePerfMonitor = new ImagePerfMonitor(AwakeTimeSinceBootClock.deepCopy(), this);
        }
        mImagePerfMonitor.addImagePerfDataListener(paramImagePerfDataListener);
        mImagePerfMonitor.setEnabled(true);
      }
      return;
    }
    catch (Throwable paramImagePerfDataListener)
    {
      throw paramImagePerfDataListener;
    }
  }
  
  public boolean isSameImageRequest(DraweeController paramDraweeController)
  {
    if ((mCacheKey != null) && ((paramDraweeController instanceof PipelineDraweeController))) {
      return Objects.equal(mCacheKey, ((PipelineDraweeController)paramDraweeController).getCacheKey());
    }
    return false;
  }
  
  protected void onImageLoadedFromCacheImmediately(String paramString, CloseableReference paramCloseableReference)
  {
    super.onImageLoadedFromCacheImmediately(paramString, paramCloseableReference);
    try
    {
      if (mImageOriginListener != null) {
        mImageOriginListener.onImageLoaded(paramString, 5, true, "PipelineDraweeController");
      }
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  protected void releaseDrawable(Drawable paramDrawable)
  {
    if ((paramDrawable instanceof DrawableWithCaches)) {
      ((DrawableWithCaches)paramDrawable).dropCaches();
    }
  }
  
  protected void releaseImage(CloseableReference paramCloseableReference)
  {
    CloseableReference.closeSafely(paramCloseableReference);
  }
  
  public void removeImageOriginListener(ImageOriginListener paramImageOriginListener)
  {
    try
    {
      if ((mImageOriginListener instanceof ForwardingImageOriginListener)) {
        ((ForwardingImageOriginListener)mImageOriginListener).removeImageOriginListener(paramImageOriginListener);
      } else if (mImageOriginListener != null) {
        mImageOriginListener = new ForwardingImageOriginListener(new ImageOriginListener[] { mImageOriginListener, paramImageOriginListener });
      } else {
        mImageOriginListener = paramImageOriginListener;
      }
      return;
    }
    catch (Throwable paramImageOriginListener)
    {
      throw paramImageOriginListener;
    }
  }
  
  public void removeRequestListener(RequestListener paramRequestListener)
  {
    try
    {
      Set localSet = mRequestListeners;
      if (localSet == null) {
        return;
      }
      mRequestListeners.remove(paramRequestListener);
      return;
    }
    catch (Throwable paramRequestListener)
    {
      throw paramRequestListener;
    }
  }
  
  public void setCustomDrawableFactories(ImmutableList paramImmutableList)
  {
    mCustomDrawableFactories = paramImmutableList;
  }
  
  public void setDrawDebugOverlay(boolean paramBoolean)
  {
    mDrawDebugOverlay = paramBoolean;
  }
  
  public void setHierarchy(DraweeHierarchy paramDraweeHierarchy)
  {
    super.setHierarchy(paramDraweeHierarchy);
    maybeUpdateDebugOverlay(null);
  }
  
  public String toString()
  {
    return Objects.toStringHelper(this).addValue("super", super.toString()).addValue("dataSourceSupplier", mDataSourceSupplier).toString();
  }
  
  protected void updateDebugOverlay(CloseableImage paramCloseableImage, DebugControllerOverlayDrawable paramDebugControllerOverlayDrawable)
  {
    paramDebugControllerOverlayDrawable.setControllerId(getId());
    Object localObject3 = getHierarchy();
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (localObject3 != null)
    {
      localObject3 = ScalingUtils.getActiveScaleTypeDrawable(((DraweeHierarchy)localObject3).getTopLevelDrawable());
      localObject1 = localObject2;
      if (localObject3 != null) {
        localObject1 = ((ScaleTypeDrawable)localObject3).getScaleType();
      }
    }
    paramDebugControllerOverlayDrawable.setScaleType((ScalingUtils.ScaleType)localObject1);
    paramDebugControllerOverlayDrawable.setOrigin(mDebugOverlayImageOriginListener.getImageOrigin());
    if (paramCloseableImage != null)
    {
      paramDebugControllerOverlayDrawable.setDimensions(paramCloseableImage.getWidth(), paramCloseableImage.getHeight());
      paramDebugControllerOverlayDrawable.setImageSize(paramCloseableImage.getSizeInBytes());
      return;
    }
    paramDebugControllerOverlayDrawable.reset();
  }
}