package com.bumptech.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.annotation.GuardedBy;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.manager.ConnectivityMonitor;
import com.bumptech.glide.manager.ConnectivityMonitor.ConnectivityListener;
import com.bumptech.glide.manager.ConnectivityMonitorFactory;
import com.bumptech.glide.manager.Lifecycle;
import com.bumptech.glide.manager.LifecycleListener;
import com.bumptech.glide.manager.RequestManagerTreeNode;
import com.bumptech.glide.manager.RequestTracker;
import com.bumptech.glide.manager.TargetTracker;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class RequestManager
  implements LifecycleListener, ModelTypes<RequestBuilder<Drawable>>
{
  private static final RequestOptions DECODE_TYPE_BITMAP = (RequestOptions)RequestOptions.decodeTypeOf(Bitmap.class).lock();
  private static final RequestOptions DECODE_TYPE_GIF = (RequestOptions)RequestOptions.decodeTypeOf(GifDrawable.class).lock();
  private static final RequestOptions DOWNLOAD_ONLY_OPTIONS = (RequestOptions)((RequestOptions)RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA).priority(Priority.LOW)).skipMemoryCache(true);
  private final Runnable addSelfToLifecycle = new Runnable()
  {
    public void run()
    {
      lifecycle.addListener(RequestManager.this);
    }
  };
  private final ConnectivityMonitor connectivityMonitor;
  protected final Context context;
  private final CopyOnWriteArrayList<RequestListener<Object>> defaultRequestListeners;
  protected final Glide glide;
  final Lifecycle lifecycle;
  private final Handler mainHandler = new Handler(Looper.getMainLooper());
  @GuardedBy("this")
  private RequestOptions requestOptions;
  @GuardedBy("this")
  private final RequestTracker requestTracker;
  @GuardedBy("this")
  private final TargetTracker targetTracker = new TargetTracker();
  @GuardedBy("this")
  private final RequestManagerTreeNode treeNode;
  
  public RequestManager(Glide paramGlide, Lifecycle paramLifecycle, RequestManagerTreeNode paramRequestManagerTreeNode, Context paramContext)
  {
    this(paramGlide, paramLifecycle, paramRequestManagerTreeNode, new RequestTracker(), paramGlide.getConnectivityMonitorFactory(), paramContext);
  }
  
  RequestManager(Glide paramGlide, Lifecycle paramLifecycle, RequestManagerTreeNode paramRequestManagerTreeNode, RequestTracker paramRequestTracker, ConnectivityMonitorFactory paramConnectivityMonitorFactory, Context paramContext)
  {
    glide = paramGlide;
    lifecycle = paramLifecycle;
    treeNode = paramRequestManagerTreeNode;
    requestTracker = paramRequestTracker;
    context = paramContext;
    connectivityMonitor = paramConnectivityMonitorFactory.build(paramContext.getApplicationContext(), new RequestManagerConnectivityListener(paramRequestTracker));
    if (Util.isOnBackgroundThread()) {
      mainHandler.post(addSelfToLifecycle);
    } else {
      paramLifecycle.addListener(this);
    }
    paramLifecycle.addListener(connectivityMonitor);
    defaultRequestListeners = new CopyOnWriteArrayList(paramGlide.getGlideContext().getDefaultRequestListeners());
    setRequestOptions(paramGlide.getGlideContext().getDefaultRequestOptions());
    paramGlide.registerRequestManager(this);
  }
  
  private void untrackOrDelegate(Target paramTarget)
  {
    if ((!untrack(paramTarget)) && (!glide.removeFromManagers(paramTarget)) && (paramTarget.getRequest() != null))
    {
      Request localRequest = paramTarget.getRequest();
      paramTarget.setRequest(null);
      localRequest.clear();
    }
  }
  
  private void updateRequestOptions(RequestOptions paramRequestOptions)
  {
    try
    {
      requestOptions = ((RequestOptions)requestOptions.apply(paramRequestOptions));
      return;
    }
    catch (Throwable paramRequestOptions)
    {
      throw paramRequestOptions;
    }
  }
  
  public RequestManager addDefaultRequestListener(RequestListener paramRequestListener)
  {
    defaultRequestListeners.add(paramRequestListener);
    return this;
  }
  
  public RequestManager applyDefaultRequestOptions(RequestOptions paramRequestOptions)
  {
    try
    {
      updateRequestOptions(paramRequestOptions);
      return this;
    }
    catch (Throwable paramRequestOptions)
    {
      throw paramRequestOptions;
    }
  }
  
  public RequestBuilder asBitmap()
  {
    return get(Bitmap.class).apply(DECODE_TYPE_BITMAP);
  }
  
  public RequestBuilder asDrawable()
  {
    return get(Drawable.class);
  }
  
  public RequestBuilder asFile()
  {
    return get(File.class).apply(RequestOptions.skipMemoryCacheOf(true));
  }
  
  public RequestBuilder asGif()
  {
    return get(GifDrawable.class).apply(DECODE_TYPE_GIF);
  }
  
  public void clear(View paramView)
  {
    clear(new ClearTarget(paramView));
  }
  
  public void clear(Target paramTarget)
  {
    if (paramTarget == null) {
      return;
    }
    try
    {
      untrackOrDelegate(paramTarget);
      return;
    }
    catch (Throwable paramTarget)
    {
      throw paramTarget;
    }
  }
  
  public RequestBuilder download(Object paramObject)
  {
    return downloadOnly().load(paramObject);
  }
  
  public RequestBuilder downloadOnly()
  {
    return get(File.class).apply(DOWNLOAD_ONLY_OPTIONS);
  }
  
  public RequestBuilder get(Class paramClass)
  {
    return new RequestBuilder(glide, this, paramClass, context);
  }
  
  List getDefaultRequestListeners()
  {
    return defaultRequestListeners;
  }
  
  RequestOptions getDefaultRequestOptions()
  {
    try
    {
      RequestOptions localRequestOptions = requestOptions;
      return localRequestOptions;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  TransitionOptions getDefaultTransitionOptions(Class paramClass)
  {
    return glide.getGlideContext().getDefaultTransitionOptions(paramClass);
  }
  
  public boolean isPaused()
  {
    try
    {
      boolean bool = requestTracker.isPaused();
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public RequestBuilder load(Bitmap paramBitmap)
  {
    return asDrawable().load(paramBitmap);
  }
  
  public RequestBuilder load(Drawable paramDrawable)
  {
    return asDrawable().load(paramDrawable);
  }
  
  public RequestBuilder load(Uri paramUri)
  {
    return asDrawable().load(paramUri);
  }
  
  public RequestBuilder load(File paramFile)
  {
    return asDrawable().load(paramFile);
  }
  
  public RequestBuilder load(Integer paramInteger)
  {
    return asDrawable().load(paramInteger);
  }
  
  public RequestBuilder load(Object paramObject)
  {
    return asDrawable().load(paramObject);
  }
  
  public RequestBuilder load(String paramString)
  {
    return asDrawable().load(paramString);
  }
  
  public RequestBuilder load(URL paramURL)
  {
    return asDrawable().load(paramURL);
  }
  
  public RequestBuilder load(byte[] paramArrayOfByte)
  {
    return asDrawable().load(paramArrayOfByte);
  }
  
  public void onDestroy()
  {
    try
    {
      targetTracker.onDestroy();
      Iterator localIterator = targetTracker.getAll().iterator();
      while (localIterator.hasNext()) {
        clear((Target)localIterator.next());
      }
      targetTracker.clear();
      requestTracker.clearRequests();
      lifecycle.removeListener(this);
      lifecycle.removeListener(connectivityMonitor);
      mainHandler.removeCallbacks(addSelfToLifecycle);
      glide.unregisterRequestManager(this);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void onStart()
  {
    try
    {
      resumeRequests();
      targetTracker.onStart();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void onStop()
  {
    try
    {
      pauseRequests();
      targetTracker.onStop();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void pauseAllRequests()
  {
    try
    {
      requestTracker.pauseAllRequests();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void pauseRequests()
  {
    try
    {
      requestTracker.pauseRequests();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void pauseRequestsRecursive()
  {
    try
    {
      pauseRequests();
      Iterator localIterator = treeNode.getDescendants().iterator();
      while (localIterator.hasNext()) {
        ((RequestManager)localIterator.next()).pauseRequests();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void resumeRequests()
  {
    try
    {
      requestTracker.resumeRequests();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void resumeRequestsRecursive()
  {
    try
    {
      Util.assertMainThread();
      resumeRequests();
      Iterator localIterator = treeNode.getDescendants().iterator();
      while (localIterator.hasNext()) {
        ((RequestManager)localIterator.next()).resumeRequests();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public RequestManager setDefaultRequestOptions(RequestOptions paramRequestOptions)
  {
    try
    {
      setRequestOptions(paramRequestOptions);
      return this;
    }
    catch (Throwable paramRequestOptions)
    {
      throw paramRequestOptions;
    }
  }
  
  protected void setRequestOptions(RequestOptions paramRequestOptions)
  {
    try
    {
      requestOptions = ((RequestOptions)((RequestOptions)paramRequestOptions.clone()).autoClone());
      return;
    }
    catch (Throwable paramRequestOptions)
    {
      throw paramRequestOptions;
    }
  }
  
  public String toString()
  {
    try
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append(super.toString());
      ((StringBuilder)localObject).append("{tracker=");
      ((StringBuilder)localObject).append(requestTracker);
      ((StringBuilder)localObject).append(", treeNode=");
      ((StringBuilder)localObject).append(treeNode);
      ((StringBuilder)localObject).append("}");
      localObject = ((StringBuilder)localObject).toString();
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  void track(Target paramTarget, Request paramRequest)
  {
    try
    {
      targetTracker.track(paramTarget);
      requestTracker.runRequest(paramRequest);
      return;
    }
    catch (Throwable paramTarget)
    {
      throw paramTarget;
    }
  }
  
  boolean untrack(Target paramTarget)
  {
    try
    {
      Request localRequest = paramTarget.getRequest();
      if (localRequest == null) {
        return true;
      }
      if (requestTracker.clearRemoveAndRecycle(localRequest))
      {
        targetTracker.untrack(paramTarget);
        paramTarget.setRequest(null);
        return true;
      }
      return false;
    }
    catch (Throwable paramTarget)
    {
      throw paramTarget;
    }
  }
  
  private static class ClearTarget
    extends ViewTarget<View, Object>
  {
    ClearTarget(View paramView)
    {
      super();
    }
    
    public void onResourceReady(Object paramObject, Transition paramTransition) {}
  }
  
  private class RequestManagerConnectivityListener
    implements ConnectivityMonitor.ConnectivityListener
  {
    @GuardedBy("RequestManager.this")
    private final RequestTracker requestTracker;
    
    RequestManagerConnectivityListener(RequestTracker paramRequestTracker)
    {
      requestTracker = paramRequestTracker;
    }
    
    public void onConnectivityChanged(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        RequestManager localRequestManager = RequestManager.this;
        try
        {
          requestTracker.restartRequests();
          return;
        }
        catch (Throwable localThrowable)
        {
          throw localThrowable;
        }
      }
    }
  }
}
