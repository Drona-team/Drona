package com.bumptech.glide;

import android.content.Context;
import android.content.ContextWrapper;
import android.widget.ImageView;
import androidx.annotation.VisibleForTesting;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.bumptech.glide.request.target.ViewTarget;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class GlideContext
  extends ContextWrapper
{
  @VisibleForTesting
  static final TransitionOptions<?, ?> DEFAULT_TRANSITION_OPTIONS = new GenericTransitionOptions();
  private final ArrayPool arrayPool;
  private final List<RequestListener<Object>> defaultRequestListeners;
  private final RequestOptions defaultRequestOptions;
  private final Map<Class<?>, TransitionOptions<?, ?>> defaultTransitionOptions;
  private final Engine engine;
  private final ImageViewTargetFactory imageViewTargetFactory;
  private final boolean isLoggingRequestOriginsEnabled;
  private final int logLevel;
  private final Registry registry;
  
  public GlideContext(Context paramContext, ArrayPool paramArrayPool, Registry paramRegistry, ImageViewTargetFactory paramImageViewTargetFactory, RequestOptions paramRequestOptions, Map paramMap, List paramList, Engine paramEngine, boolean paramBoolean, int paramInt)
  {
    super(paramContext.getApplicationContext());
    arrayPool = paramArrayPool;
    registry = paramRegistry;
    imageViewTargetFactory = paramImageViewTargetFactory;
    defaultRequestOptions = paramRequestOptions;
    defaultRequestListeners = paramList;
    defaultTransitionOptions = paramMap;
    engine = paramEngine;
    isLoggingRequestOriginsEnabled = paramBoolean;
    logLevel = paramInt;
  }
  
  public ViewTarget buildImageViewTarget(ImageView paramImageView, Class paramClass)
  {
    return imageViewTargetFactory.buildTarget(paramImageView, paramClass);
  }
  
  public ArrayPool getArrayPool()
  {
    return arrayPool;
  }
  
  public List getDefaultRequestListeners()
  {
    return defaultRequestListeners;
  }
  
  public RequestOptions getDefaultRequestOptions()
  {
    return defaultRequestOptions;
  }
  
  public TransitionOptions getDefaultTransitionOptions(Class paramClass)
  {
    TransitionOptions localTransitionOptions = (TransitionOptions)defaultTransitionOptions.get(paramClass);
    Object localObject = localTransitionOptions;
    if (localTransitionOptions == null)
    {
      Iterator localIterator = defaultTransitionOptions.entrySet().iterator();
      for (;;)
      {
        localObject = localTransitionOptions;
        if (!localIterator.hasNext()) {
          break;
        }
        localObject = (Map.Entry)localIterator.next();
        if (((Class)((Map.Entry)localObject).getKey()).isAssignableFrom(paramClass)) {
          localTransitionOptions = (TransitionOptions)((Map.Entry)localObject).getValue();
        }
      }
    }
    paramClass = (Class)localObject;
    if (localObject == null) {
      paramClass = DEFAULT_TRANSITION_OPTIONS;
    }
    return paramClass;
  }
  
  public Engine getEngine()
  {
    return engine;
  }
  
  public int getLogLevel()
  {
    return logLevel;
  }
  
  public Registry getRegistry()
  {
    return registry;
  }
  
  public boolean isLoggingRequestOriginsEnabled()
  {
    return isLoggingRequestOriginsEnabled;
  }
}
