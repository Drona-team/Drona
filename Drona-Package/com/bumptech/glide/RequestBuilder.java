package com.bumptech.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.ErrorRequestCoordinator;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestCoordinator;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.SingleRequest;
import com.bumptech.glide.request.ThumbnailRequestCoordinator;
import com.bumptech.glide.request.target.PreloadTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.signature.ApplicationVersionSignature;
import com.bumptech.glide.util.Executors;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

public class RequestBuilder<TranscodeType>
  extends BaseRequestOptions<RequestBuilder<TranscodeType>>
  implements Cloneable, ModelTypes<RequestBuilder<TranscodeType>>
{
  protected static final RequestOptions DOWNLOAD_ONLY_OPTIONS = (RequestOptions)((RequestOptions)((RequestOptions)new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA)).priority(Priority.LOW)).skipMemoryCache(true);
  private final Context context;
  @Nullable
  private RequestBuilder<TranscodeType> errorBuilder;
  private final Glide glide;
  private final GlideContext glideContext;
  private boolean isDefaultTransitionOptionsSet = true;
  private boolean isModelSet;
  private boolean isThumbnailBuilt;
  @Nullable
  private Object model;
  @Nullable
  private List<RequestListener<TranscodeType>> requestListeners;
  private final RequestManager requestManager;
  @Nullable
  private Float thumbSizeMultiplier;
  @Nullable
  private RequestBuilder<TranscodeType> thumbnailBuilder;
  private final Class<TranscodeType> transcodeClass;
  @NonNull
  private TransitionOptions<?, ? super TranscodeType> transitionOptions;
  
  protected RequestBuilder(Glide paramGlide, RequestManager paramRequestManager, Class paramClass, Context paramContext)
  {
    glide = paramGlide;
    requestManager = paramRequestManager;
    transcodeClass = paramClass;
    context = paramContext;
    transitionOptions = paramRequestManager.getDefaultTransitionOptions(paramClass);
    glideContext = paramGlide.getGlideContext();
    initRequestListeners(paramRequestManager.getDefaultRequestListeners());
    apply(paramRequestManager.getDefaultRequestOptions());
  }
  
  protected RequestBuilder(Class paramClass, RequestBuilder paramRequestBuilder)
  {
    this(glide, requestManager, paramClass, context);
    model = model;
    isModelSet = isModelSet;
    apply(paramRequestBuilder);
  }
  
  private Request buildRequest(Target paramTarget, RequestListener paramRequestListener, BaseRequestOptions paramBaseRequestOptions, Executor paramExecutor)
  {
    return buildRequestRecursive(paramTarget, paramRequestListener, null, transitionOptions, paramBaseRequestOptions.getPriority(), paramBaseRequestOptions.getOverrideWidth(), paramBaseRequestOptions.getOverrideHeight(), paramBaseRequestOptions, paramExecutor);
  }
  
  private Request buildRequestRecursive(Target paramTarget, RequestListener paramRequestListener, RequestCoordinator paramRequestCoordinator, TransitionOptions paramTransitionOptions, Priority paramPriority, int paramInt1, int paramInt2, BaseRequestOptions paramBaseRequestOptions, Executor paramExecutor)
  {
    RequestCoordinator localRequestCoordinator;
    if (errorBuilder != null)
    {
      paramRequestCoordinator = new ErrorRequestCoordinator(paramRequestCoordinator);
      localRequestCoordinator = paramRequestCoordinator;
    }
    else
    {
      Object localObject = null;
      localRequestCoordinator = paramRequestCoordinator;
      paramRequestCoordinator = localObject;
    }
    paramTransitionOptions = buildThumbnailRequestRecursive(paramTarget, paramRequestListener, localRequestCoordinator, paramTransitionOptions, paramPriority, paramInt1, paramInt2, paramBaseRequestOptions, paramExecutor);
    if (paramRequestCoordinator == null) {
      return paramTransitionOptions;
    }
    int k = errorBuilder.getOverrideWidth();
    int m = errorBuilder.getOverrideHeight();
    int j = k;
    int i = m;
    if (Util.isValidDimensions(paramInt1, paramInt2))
    {
      j = k;
      i = m;
      if (!errorBuilder.isValidOverride())
      {
        j = paramBaseRequestOptions.getOverrideWidth();
        i = paramBaseRequestOptions.getOverrideHeight();
      }
    }
    paramRequestCoordinator.setRequests(paramTransitionOptions, errorBuilder.buildRequestRecursive(paramTarget, paramRequestListener, paramRequestCoordinator, errorBuilder.transitionOptions, errorBuilder.getPriority(), j, i, errorBuilder, paramExecutor));
    return paramRequestCoordinator;
  }
  
  private Request buildThumbnailRequestRecursive(Target paramTarget, RequestListener paramRequestListener, RequestCoordinator paramRequestCoordinator, TransitionOptions paramTransitionOptions, Priority paramPriority, int paramInt1, int paramInt2, BaseRequestOptions paramBaseRequestOptions, Executor paramExecutor)
  {
    if (thumbnailBuilder != null)
    {
      if (!isThumbnailBuilt)
      {
        TransitionOptions localTransitionOptions = thumbnailBuilder.transitionOptions;
        if (thumbnailBuilder.isDefaultTransitionOptionsSet) {
          localTransitionOptions = paramTransitionOptions;
        }
        if (thumbnailBuilder.isPrioritySet()) {}
        for (Priority localPriority = thumbnailBuilder.getPriority();; localPriority = getThumbnailPriority(paramPriority)) {
          break;
        }
        int k = thumbnailBuilder.getOverrideWidth();
        int m = thumbnailBuilder.getOverrideHeight();
        int j = k;
        int i = m;
        if (Util.isValidDimensions(paramInt1, paramInt2))
        {
          j = k;
          i = m;
          if (!thumbnailBuilder.isValidOverride())
          {
            j = paramBaseRequestOptions.getOverrideWidth();
            i = paramBaseRequestOptions.getOverrideHeight();
          }
        }
        paramRequestCoordinator = new ThumbnailRequestCoordinator(paramRequestCoordinator);
        paramTransitionOptions = obtainRequest(paramTarget, paramRequestListener, paramBaseRequestOptions, paramRequestCoordinator, paramTransitionOptions, paramPriority, paramInt1, paramInt2, paramExecutor);
        isThumbnailBuilt = true;
        paramTarget = thumbnailBuilder.buildRequestRecursive(paramTarget, paramRequestListener, paramRequestCoordinator, localTransitionOptions, localPriority, j, i, thumbnailBuilder, paramExecutor);
        isThumbnailBuilt = false;
        paramRequestCoordinator.setRequests(paramTransitionOptions, paramTarget);
        return paramRequestCoordinator;
      }
      throw new IllegalStateException("You cannot use a request as both the main request and a thumbnail, consider using clone() on the request(s) passed to thumbnail()");
    }
    if (thumbSizeMultiplier != null)
    {
      paramRequestCoordinator = new ThumbnailRequestCoordinator(paramRequestCoordinator);
      paramRequestCoordinator.setRequests(obtainRequest(paramTarget, paramRequestListener, paramBaseRequestOptions, paramRequestCoordinator, paramTransitionOptions, paramPriority, paramInt1, paramInt2, paramExecutor), obtainRequest(paramTarget, paramRequestListener, paramBaseRequestOptions.clone().sizeMultiplier(thumbSizeMultiplier.floatValue()), paramRequestCoordinator, paramTransitionOptions, getThumbnailPriority(paramPriority), paramInt1, paramInt2, paramExecutor));
      return paramRequestCoordinator;
    }
    return obtainRequest(paramTarget, paramRequestListener, paramBaseRequestOptions, paramRequestCoordinator, paramTransitionOptions, paramPriority, paramInt1, paramInt2, paramExecutor);
  }
  
  private Priority getThumbnailPriority(Priority paramPriority)
  {
    switch (paramPriority)
    {
    default: 
      paramPriority = new StringBuilder();
      paramPriority.append("unknown priority: ");
      paramPriority.append(getPriority());
      throw new IllegalArgumentException(paramPriority.toString());
    case ???: 
    case ???: 
      return Priority.IMMEDIATE;
    case ???: 
      return Priority.HIGH;
    }
    return Priority.NORMAL;
  }
  
  private void initRequestListeners(List paramList)
  {
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      addListener((RequestListener)paramList.next());
    }
  }
  
  private Target into(Target paramTarget, RequestListener paramRequestListener, BaseRequestOptions paramBaseRequestOptions, Executor paramExecutor)
  {
    Preconditions.checkNotNull(paramTarget);
    if (isModelSet)
    {
      paramRequestListener = buildRequest(paramTarget, paramRequestListener, paramBaseRequestOptions, paramExecutor);
      paramExecutor = paramTarget.getRequest();
      if ((paramRequestListener.isEquivalentTo(paramExecutor)) && (!isSkipMemoryCacheWithCompletePreviousRequest(paramBaseRequestOptions, paramExecutor)))
      {
        paramRequestListener.recycle();
        if (!((Request)Preconditions.checkNotNull(paramExecutor)).isRunning())
        {
          paramExecutor.begin();
          return paramTarget;
        }
      }
      else
      {
        requestManager.clear(paramTarget);
        paramTarget.setRequest(paramRequestListener);
        requestManager.track(paramTarget, paramRequestListener);
        return paramTarget;
      }
    }
    else
    {
      throw new IllegalArgumentException("You must call #load() before calling #into()");
    }
    return paramTarget;
  }
  
  private boolean isSkipMemoryCacheWithCompletePreviousRequest(BaseRequestOptions paramBaseRequestOptions, Request paramRequest)
  {
    return (!paramBaseRequestOptions.isMemoryCacheable()) && (paramRequest.isComplete());
  }
  
  private RequestBuilder loadGeneric(Object paramObject)
  {
    model = paramObject;
    isModelSet = true;
    return this;
  }
  
  private Request obtainRequest(Target paramTarget, RequestListener paramRequestListener, BaseRequestOptions paramBaseRequestOptions, RequestCoordinator paramRequestCoordinator, TransitionOptions paramTransitionOptions, Priority paramPriority, int paramInt1, int paramInt2, Executor paramExecutor)
  {
    return SingleRequest.obtain(context, glideContext, model, transcodeClass, paramBaseRequestOptions, paramInt1, paramInt2, paramPriority, paramTarget, paramRequestListener, requestListeners, paramRequestCoordinator, glideContext.getEngine(), paramTransitionOptions.getTransitionFactory(), paramExecutor);
  }
  
  public RequestBuilder addListener(RequestListener paramRequestListener)
  {
    if (paramRequestListener != null)
    {
      if (requestListeners == null) {
        requestListeners = new ArrayList();
      }
      requestListeners.add(paramRequestListener);
    }
    return this;
  }
  
  public RequestBuilder apply(BaseRequestOptions paramBaseRequestOptions)
  {
    Preconditions.checkNotNull(paramBaseRequestOptions);
    return (RequestBuilder)super.apply(paramBaseRequestOptions);
  }
  
  public RequestBuilder clone()
  {
    RequestBuilder localRequestBuilder = (RequestBuilder)super.clone();
    transitionOptions = transitionOptions.clone();
    return localRequestBuilder;
  }
  
  public FutureTarget downloadOnly(int paramInt1, int paramInt2)
  {
    return getDownloadOnlyRequest().submit(paramInt1, paramInt2);
  }
  
  public Target downloadOnly(Target paramTarget)
  {
    return getDownloadOnlyRequest().into(paramTarget);
  }
  
  public RequestBuilder error(RequestBuilder paramRequestBuilder)
  {
    errorBuilder = paramRequestBuilder;
    return this;
  }
  
  protected RequestBuilder getDownloadOnlyRequest()
  {
    return new RequestBuilder(File.class, this).apply(DOWNLOAD_ONLY_OPTIONS);
  }
  
  public FutureTarget into(int paramInt1, int paramInt2)
  {
    return submit(paramInt1, paramInt2);
  }
  
  public Target into(Target paramTarget)
  {
    return into(paramTarget, null, Executors.mainThreadExecutor());
  }
  
  Target into(Target paramTarget, RequestListener paramRequestListener, Executor paramExecutor)
  {
    return into(paramTarget, paramRequestListener, this, paramExecutor);
  }
  
  public ViewTarget into(ImageView paramImageView)
  {
    Util.assertMainThread();
    Preconditions.checkNotNull(paramImageView);
    if ((!isTransformationSet()) && (isTransformationAllowed()) && (paramImageView.getScaleType() != null)) {
      switch (1.$SwitchMap$android$widget$ImageView$ScaleType[paramImageView.getScaleType().ordinal()])
      {
      default: 
        break;
      case 6: 
        localObject = clone().optionalCenterInside();
        break;
      case 3: 
      case 4: 
      case 5: 
        localObject = clone().optionalFitCenter();
        break;
      case 2: 
        localObject = clone().optionalCenterInside();
        break;
      case 1: 
        localObject = clone().optionalCenterCrop();
        break;
      }
    }
    Object localObject = this;
    return (ViewTarget)into(glideContext.buildImageViewTarget(paramImageView, transcodeClass), null, (BaseRequestOptions)localObject, Executors.mainThreadExecutor());
  }
  
  public RequestBuilder listener(RequestListener paramRequestListener)
  {
    requestListeners = null;
    return addListener(paramRequestListener);
  }
  
  public RequestBuilder load(Bitmap paramBitmap)
  {
    return loadGeneric(paramBitmap).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
  }
  
  public RequestBuilder load(Drawable paramDrawable)
  {
    return loadGeneric(paramDrawable).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
  }
  
  public RequestBuilder load(Uri paramUri)
  {
    return loadGeneric(paramUri);
  }
  
  public RequestBuilder load(File paramFile)
  {
    return loadGeneric(paramFile);
  }
  
  public RequestBuilder load(Integer paramInteger)
  {
    return loadGeneric(paramInteger).apply(RequestOptions.signatureOf(ApplicationVersionSignature.obtain(context)));
  }
  
  public RequestBuilder load(Object paramObject)
  {
    return loadGeneric(paramObject);
  }
  
  public RequestBuilder load(String paramString)
  {
    return loadGeneric(paramString);
  }
  
  public RequestBuilder load(URL paramURL)
  {
    return loadGeneric(paramURL);
  }
  
  public RequestBuilder load(byte[] paramArrayOfByte)
  {
    RequestBuilder localRequestBuilder = loadGeneric(paramArrayOfByte);
    paramArrayOfByte = localRequestBuilder;
    if (!localRequestBuilder.isDiskCacheStrategySet()) {
      paramArrayOfByte = localRequestBuilder.apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
    }
    if (!paramArrayOfByte.isSkipMemoryCacheSet()) {
      return paramArrayOfByte.apply(RequestOptions.skipMemoryCacheOf(true));
    }
    return paramArrayOfByte;
  }
  
  public Target preload()
  {
    return preload(Integer.MIN_VALUE, Integer.MIN_VALUE);
  }
  
  public Target preload(int paramInt1, int paramInt2)
  {
    return into(PreloadTarget.obtain(requestManager, paramInt1, paramInt2));
  }
  
  public FutureTarget submit()
  {
    return submit(Integer.MIN_VALUE, Integer.MIN_VALUE);
  }
  
  public FutureTarget submit(int paramInt1, int paramInt2)
  {
    RequestFutureTarget localRequestFutureTarget = new RequestFutureTarget(paramInt1, paramInt2);
    return (FutureTarget)into(localRequestFutureTarget, localRequestFutureTarget, Executors.directExecutor());
  }
  
  public RequestBuilder thumbnail(float paramFloat)
  {
    if ((paramFloat >= 0.0F) && (paramFloat <= 1.0F))
    {
      thumbSizeMultiplier = Float.valueOf(paramFloat);
      return this;
    }
    throw new IllegalArgumentException("sizeMultiplier must be between 0 and 1");
  }
  
  public RequestBuilder thumbnail(RequestBuilder paramRequestBuilder)
  {
    thumbnailBuilder = paramRequestBuilder;
    return this;
  }
  
  public RequestBuilder thumbnail(RequestBuilder... paramVarArgs)
  {
    Object localObject = null;
    if ((paramVarArgs != null) && (paramVarArgs.length != 0))
    {
      int i = paramVarArgs.length - 1;
      while (i >= 0)
      {
        RequestBuilder localRequestBuilder = paramVarArgs[i];
        if (localRequestBuilder != null) {
          if (localObject == null) {
            localObject = localRequestBuilder;
          } else {
            localObject = localRequestBuilder.thumbnail((RequestBuilder)localObject);
          }
        }
        i -= 1;
      }
      return thumbnail((RequestBuilder)localObject);
    }
    return thumbnail(null);
  }
  
  public RequestBuilder transition(TransitionOptions paramTransitionOptions)
  {
    transitionOptions = ((TransitionOptions)Preconditions.checkNotNull(paramTransitionOptions));
    isDefaultTransitionOptionsSet = false;
    return this;
  }
}
