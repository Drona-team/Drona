package com.facebook.drawee.controller;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Objects.ToStringHelper;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.components.DeferredReleaser;
import com.facebook.drawee.components.DeferredReleaser.Releasable;
import com.facebook.drawee.components.DraweeEventTracker;
import com.facebook.drawee.components.DraweeEventTracker.Event;
import com.facebook.drawee.components.RetryManager;
import com.facebook.drawee.gestures.GestureDetector;
import com.facebook.drawee.gestures.GestureDetector.ClickListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.drawee.interfaces.SettableDraweeHierarchy;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class AbstractDraweeController<T, INFO>
  implements DraweeController, DeferredReleaser.Releasable, GestureDetector.ClickListener
{
  private static final Class<?> TAG = AbstractDraweeController.class;
  private Object mCallerContext;
  @Nullable
  private String mContentDescription;
  @Nullable
  protected ControllerListener<INFO> mControllerListener;
  @Nullable
  private Drawable mControllerOverlay;
  @Nullable
  private ControllerViewportVisibilityListener mControllerViewportVisibilityListener;
  @Nullable
  private DataSource<T> mDataSource;
  private final DeferredReleaser mDeferredReleaser;
  @Nullable
  private Drawable mDrawable;
  private final DraweeEventTracker mEventTracker = DraweeEventTracker.newInstance();
  @Nullable
  private T mFetchedImage;
  @Nullable
  private GestureDetector mGestureDetector;
  private boolean mHasFetchFailed;
  private String mId;
  private boolean mIsAttached;
  private boolean mIsRequestSubmitted;
  private boolean mIsVisibleInViewportHint;
  private boolean mJustConstructed = true;
  private boolean mRetainImageOnFailure;
  @Nullable
  private RetryManager mRetryManager;
  @Nullable
  private SettableDraweeHierarchy mSettableDraweeHierarchy;
  private final Executor mUiThreadImmediateExecutor;
  
  public AbstractDraweeController(DeferredReleaser paramDeferredReleaser, Executor paramExecutor, String paramString, Object paramObject)
  {
    mDeferredReleaser = paramDeferredReleaser;
    mUiThreadImmediateExecutor = paramExecutor;
    init(paramString, paramObject);
  }
  
  private void init(String paramString, Object paramObject)
  {
    try
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.beginSection("AbstractDraweeController#init");
      }
      mEventTracker.recordEvent(DraweeEventTracker.Event.ON_INIT_CONTROLLER);
      if ((!mJustConstructed) && (mDeferredReleaser != null)) {
        mDeferredReleaser.cancelDeferredRelease(this);
      }
      mIsAttached = false;
      mIsVisibleInViewportHint = false;
      releaseFetch();
      mRetainImageOnFailure = false;
      if (mRetryManager != null) {
        mRetryManager.init();
      }
      if (mGestureDetector != null)
      {
        mGestureDetector.init();
        mGestureDetector.setClickListener(this);
      }
      if ((mControllerListener instanceof InternalForwardingListener)) {
        ((InternalForwardingListener)mControllerListener).clearListeners();
      } else {
        mControllerListener = null;
      }
      mControllerViewportVisibilityListener = null;
      if (mSettableDraweeHierarchy != null)
      {
        mSettableDraweeHierarchy.reset();
        mSettableDraweeHierarchy.setControllerOverlay(null);
        mSettableDraweeHierarchy = null;
      }
      mControllerOverlay = null;
      if (FLog.isLoggable(2)) {
        FLog.v(TAG, "controller %x %s -> %s: initialize", Integer.valueOf(System.identityHashCode(this)), mId, paramString);
      }
      mId = paramString;
      mCallerContext = paramObject;
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  private boolean isExpectedDataSource(String paramString, DataSource paramDataSource)
  {
    if ((paramDataSource == null) && (mDataSource == null)) {
      return true;
    }
    return (paramString.equals(mId)) && (paramDataSource == mDataSource) && (mIsRequestSubmitted);
  }
  
  private void logMessageAndFailure(String paramString, Throwable paramThrowable)
  {
    if (FLog.isLoggable(2)) {
      FLog.v(TAG, "controller %x %s: %s: failure: %s", Integer.valueOf(System.identityHashCode(this)), mId, paramString, paramThrowable);
    }
  }
  
  private void logMessageAndImage(String paramString, Object paramObject)
  {
    if (FLog.isLoggable(2)) {
      FLog.v(TAG, "controller %x %s: %s: image: %s %x", new Object[] { Integer.valueOf(System.identityHashCode(this)), mId, paramString, getImageClass(paramObject), Integer.valueOf(getImageHash(paramObject)) });
    }
  }
  
  private void onFailureInternal(String paramString, DataSource paramDataSource, Throwable paramThrowable, boolean paramBoolean)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("AbstractDraweeController#onFailureInternal");
    }
    if (!isExpectedDataSource(paramString, paramDataSource))
    {
      logMessageAndFailure("ignore_old_datasource @ onFailure", paramThrowable);
      paramDataSource.close();
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
    else
    {
      paramDataSource = mEventTracker;
      if (paramBoolean) {
        paramString = DraweeEventTracker.Event.ON_DATASOURCE_FAILURE;
      } else {
        paramString = DraweeEventTracker.Event.ON_DATASOURCE_FAILURE_INT;
      }
      paramDataSource.recordEvent(paramString);
      if (paramBoolean)
      {
        logMessageAndFailure("final_failed @ onFailure", paramThrowable);
        mDataSource = null;
        mHasFetchFailed = true;
        if ((mRetainImageOnFailure) && (mDrawable != null)) {
          mSettableDraweeHierarchy.setImage(mDrawable, 1.0F, true);
        } else if (shouldRetryOnTap()) {
          mSettableDraweeHierarchy.setRetry(paramThrowable);
        } else {
          mSettableDraweeHierarchy.setFailure(paramThrowable);
        }
        getControllerListener().onFailure(mId, paramThrowable);
      }
      else
      {
        logMessageAndFailure("intermediate_failed @ onFailure", paramThrowable);
        getControllerListener().onIntermediateImageFailed(mId, paramThrowable);
      }
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
  }
  
  private void onNewResultInternal(String paramString, DataSource paramDataSource, Object paramObject, float paramFloat, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    try
    {
      boolean bool = FrescoSystrace.isTracing();
      if (bool) {
        FrescoSystrace.beginSection("AbstractDraweeController#onNewResultInternal");
      }
      bool = isExpectedDataSource(paramString, paramDataSource);
      if (!bool)
      {
        logMessageAndImage("ignore_old_datasource @ onNewResult", paramObject);
        releaseImage(paramObject);
        paramDataSource.close();
        if (!FrescoSystrace.isTracing()) {
          return;
        }
        FrescoSystrace.endSection();
        return;
      }
      Object localObject2 = mEventTracker;
      Object localObject1;
      if (paramBoolean1) {
        localObject1 = DraweeEventTracker.Event.ON_DATASOURCE_RESULT;
      } else {
        localObject1 = DraweeEventTracker.Event.ON_DATASOURCE_RESULT_INT;
      }
      ((DraweeEventTracker)localObject2).recordEvent((DraweeEventTracker.Event)localObject1);
      try
      {
        localObject1 = createDrawable(paramObject);
        paramDataSource = mFetchedImage;
        localObject2 = mDrawable;
        mFetchedImage = paramObject;
        mDrawable = ((Drawable)localObject1);
        if (paramBoolean1)
        {
          try
          {
            logMessageAndImage("set_final_result @ onNewResult", paramObject);
            mDataSource = null;
            mSettableDraweeHierarchy.setImage((Drawable)localObject1, 1.0F, paramBoolean2);
            getControllerListener().onFinalImageSet(paramString, getImageInfo(paramObject), getAnimatable());
          }
          catch (Throwable paramString)
          {
            break label314;
          }
        }
        else if (paramBoolean3)
        {
          logMessageAndImage("set_temporary_result @ onNewResult", paramObject);
          mSettableDraweeHierarchy.setImage((Drawable)localObject1, 1.0F, paramBoolean2);
          getControllerListener().onFinalImageSet(paramString, getImageInfo(paramObject), getAnimatable());
        }
        else
        {
          logMessageAndImage("set_intermediate_result @ onNewResult", paramObject);
          mSettableDraweeHierarchy.setImage((Drawable)localObject1, paramFloat, paramBoolean2);
          getControllerListener().onIntermediateImageSet(paramString, getImageInfo(paramObject));
        }
        if ((localObject2 != null) && (localObject2 != localObject1)) {
          releaseDrawable((Drawable)localObject2);
        }
        if ((paramDataSource != null) && (paramDataSource != paramObject))
        {
          logMessageAndImage("release_previous_result @ onNewResult", paramDataSource);
          releaseImage(paramDataSource);
        }
        if (!FrescoSystrace.isTracing()) {
          return;
        }
        FrescoSystrace.endSection();
        return;
      }
      catch (Exception localException)
      {
        label314:
        logMessageAndImage("drawable_failed @ onNewResult", paramObject);
        releaseImage(paramObject);
        onFailureInternal(paramString, paramDataSource, localException, paramBoolean1);
        if (!FrescoSystrace.isTracing()) {
          return;
        }
      }
      if ((localObject2 != null) && (localObject2 != localObject1)) {
        releaseDrawable((Drawable)localObject2);
      }
      if ((paramDataSource != null) && (paramDataSource != paramObject))
      {
        logMessageAndImage("release_previous_result @ onNewResult", paramDataSource);
        releaseImage(paramDataSource);
      }
      throw paramString;
    }
    catch (Throwable paramString)
    {
      if (!FrescoSystrace.isTracing()) {
        break label401;
      }
      FrescoSystrace.endSection();
      label401:
      throw paramString;
    }
    FrescoSystrace.endSection();
    return;
  }
  
  private void onProgressUpdateInternal(String paramString, DataSource paramDataSource, float paramFloat, boolean paramBoolean)
  {
    if (!isExpectedDataSource(paramString, paramDataSource))
    {
      logMessageAndFailure("ignore_old_datasource @ onProgress", null);
      paramDataSource.close();
      return;
    }
    if (!paramBoolean) {
      mSettableDraweeHierarchy.setProgress(paramFloat, false);
    }
  }
  
  private void releaseFetch()
  {
    boolean bool = mIsRequestSubmitted;
    mIsRequestSubmitted = false;
    mHasFetchFailed = false;
    if (mDataSource != null)
    {
      mDataSource.close();
      mDataSource = null;
    }
    if (mDrawable != null) {
      releaseDrawable(mDrawable);
    }
    if (mContentDescription != null) {
      mContentDescription = null;
    }
    mDrawable = null;
    if (mFetchedImage != null)
    {
      logMessageAndImage("release", mFetchedImage);
      releaseImage(mFetchedImage);
      mFetchedImage = null;
    }
    if (bool) {
      getControllerListener().onRelease(mId);
    }
  }
  
  private boolean shouldRetryOnTap()
  {
    return (mHasFetchFailed) && (mRetryManager != null) && (mRetryManager.shouldRetryOnTap());
  }
  
  public void addControllerListener(ControllerListener paramControllerListener)
  {
    Preconditions.checkNotNull(paramControllerListener);
    if ((mControllerListener instanceof InternalForwardingListener))
    {
      ((InternalForwardingListener)mControllerListener).addListener(paramControllerListener);
      return;
    }
    if (mControllerListener != null)
    {
      mControllerListener = InternalForwardingListener.createInternal(mControllerListener, paramControllerListener);
      return;
    }
    mControllerListener = paramControllerListener;
  }
  
  protected abstract Drawable createDrawable(Object paramObject);
  
  public Animatable getAnimatable()
  {
    if ((mDrawable instanceof Animatable)) {
      return (Animatable)mDrawable;
    }
    return null;
  }
  
  protected Object getCachedImage()
  {
    return null;
  }
  
  public Object getCallerContext()
  {
    return mCallerContext;
  }
  
  public String getContentDescription()
  {
    return mContentDescription;
  }
  
  protected ControllerListener getControllerListener()
  {
    if (mControllerListener == null) {
      return BaseControllerListener.getNoOpListener();
    }
    return mControllerListener;
  }
  
  protected Drawable getControllerOverlay()
  {
    return mControllerOverlay;
  }
  
  protected abstract DataSource getDataSource();
  
  protected GestureDetector getGestureDetector()
  {
    return mGestureDetector;
  }
  
  public DraweeHierarchy getHierarchy()
  {
    return mSettableDraweeHierarchy;
  }
  
  public String getId()
  {
    return mId;
  }
  
  protected String getImageClass(Object paramObject)
  {
    if (paramObject != null) {
      return paramObject.getClass().getSimpleName();
    }
    return "<null>";
  }
  
  protected int getImageHash(Object paramObject)
  {
    return System.identityHashCode(paramObject);
  }
  
  protected abstract Object getImageInfo(Object paramObject);
  
  protected RetryManager getRetryManager()
  {
    if (mRetryManager == null) {
      mRetryManager = new RetryManager();
    }
    return mRetryManager;
  }
  
  protected void initialize(String paramString, Object paramObject)
  {
    init(paramString, paramObject);
    mJustConstructed = false;
  }
  
  public void onAttach()
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("AbstractDraweeController#onAttach");
    }
    if (FLog.isLoggable(2))
    {
      Class localClass = TAG;
      int i = System.identityHashCode(this);
      String str2 = mId;
      String str1;
      if (mIsRequestSubmitted) {
        str1 = "request already submitted";
      } else {
        str1 = "request needs submit";
      }
      FLog.v(localClass, "controller %x %s: onAttach: %s", Integer.valueOf(i), str2, str1);
    }
    mEventTracker.recordEvent(DraweeEventTracker.Event.ON_ATTACH_CONTROLLER);
    Preconditions.checkNotNull(mSettableDraweeHierarchy);
    mDeferredReleaser.cancelDeferredRelease(this);
    mIsAttached = true;
    if (!mIsRequestSubmitted) {
      submitRequest();
    }
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
  }
  
  public boolean onClick()
  {
    if (FLog.isLoggable(2)) {
      FLog.v(TAG, "controller %x %s: onClick", Integer.valueOf(System.identityHashCode(this)), mId);
    }
    if (shouldRetryOnTap())
    {
      mRetryManager.notifyTapToRetry();
      mSettableDraweeHierarchy.reset();
      submitRequest();
      return true;
    }
    return false;
  }
  
  public void onDetach()
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("AbstractDraweeController#onDetach");
    }
    if (FLog.isLoggable(2)) {
      FLog.v(TAG, "controller %x %s: onDetach", Integer.valueOf(System.identityHashCode(this)), mId);
    }
    mEventTracker.recordEvent(DraweeEventTracker.Event.ON_DETACH_CONTROLLER);
    mIsAttached = false;
    mDeferredReleaser.scheduleDeferredRelease(this);
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
  }
  
  protected void onImageLoadedFromCacheImmediately(String paramString, Object paramObject) {}
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (FLog.isLoggable(2)) {
      FLog.v(TAG, "controller %x %s: onTouchEvent %s", Integer.valueOf(System.identityHashCode(this)), mId, paramMotionEvent);
    }
    if (mGestureDetector == null) {
      return false;
    }
    if ((!mGestureDetector.isCapturingGesture()) && (!shouldHandleGesture())) {
      return false;
    }
    mGestureDetector.onTouchEvent(paramMotionEvent);
    return true;
  }
  
  public void onViewportVisibilityHint(boolean paramBoolean)
  {
    ControllerViewportVisibilityListener localControllerViewportVisibilityListener = mControllerViewportVisibilityListener;
    if (localControllerViewportVisibilityListener != null) {
      if ((paramBoolean) && (!mIsVisibleInViewportHint)) {
        localControllerViewportVisibilityListener.onDraweeViewportEntry(mId);
      } else if ((!paramBoolean) && (mIsVisibleInViewportHint)) {
        localControllerViewportVisibilityListener.onDraweeViewportExit(mId);
      }
    }
    mIsVisibleInViewportHint = paramBoolean;
  }
  
  public void release()
  {
    mEventTracker.recordEvent(DraweeEventTracker.Event.ON_RELEASE_CONTROLLER);
    if (mRetryManager != null) {
      mRetryManager.reset();
    }
    if (mGestureDetector != null) {
      mGestureDetector.reset();
    }
    if (mSettableDraweeHierarchy != null) {
      mSettableDraweeHierarchy.reset();
    }
    releaseFetch();
  }
  
  protected abstract void releaseDrawable(Drawable paramDrawable);
  
  protected abstract void releaseImage(Object paramObject);
  
  public void removeControllerListener(ControllerListener paramControllerListener)
  {
    Preconditions.checkNotNull(paramControllerListener);
    if ((mControllerListener instanceof InternalForwardingListener))
    {
      ((InternalForwardingListener)mControllerListener).removeListener(paramControllerListener);
      return;
    }
    if (mControllerListener == paramControllerListener) {
      mControllerListener = null;
    }
  }
  
  public void setContentDescription(String paramString)
  {
    mContentDescription = paramString;
  }
  
  protected void setControllerOverlay(Drawable paramDrawable)
  {
    mControllerOverlay = paramDrawable;
    if (mSettableDraweeHierarchy != null) {
      mSettableDraweeHierarchy.setControllerOverlay(mControllerOverlay);
    }
  }
  
  public void setControllerViewportVisibilityListener(ControllerViewportVisibilityListener paramControllerViewportVisibilityListener)
  {
    mControllerViewportVisibilityListener = paramControllerViewportVisibilityListener;
  }
  
  protected void setGestureDetector(GestureDetector paramGestureDetector)
  {
    mGestureDetector = paramGestureDetector;
    if (mGestureDetector != null) {
      mGestureDetector.setClickListener(this);
    }
  }
  
  public void setHierarchy(DraweeHierarchy paramDraweeHierarchy)
  {
    if (FLog.isLoggable(2)) {
      FLog.v(TAG, "controller %x %s: setHierarchy: %s", Integer.valueOf(System.identityHashCode(this)), mId, paramDraweeHierarchy);
    }
    DraweeEventTracker localDraweeEventTracker = mEventTracker;
    DraweeEventTracker.Event localEvent;
    if (paramDraweeHierarchy != null) {
      localEvent = DraweeEventTracker.Event.ON_SET_HIERARCHY;
    } else {
      localEvent = DraweeEventTracker.Event.ON_CLEAR_HIERARCHY;
    }
    localDraweeEventTracker.recordEvent(localEvent);
    if (mIsRequestSubmitted)
    {
      mDeferredReleaser.cancelDeferredRelease(this);
      release();
    }
    if (mSettableDraweeHierarchy != null)
    {
      mSettableDraweeHierarchy.setControllerOverlay(null);
      mSettableDraweeHierarchy = null;
    }
    if (paramDraweeHierarchy != null)
    {
      Preconditions.checkArgument(paramDraweeHierarchy instanceof SettableDraweeHierarchy);
      mSettableDraweeHierarchy = ((SettableDraweeHierarchy)paramDraweeHierarchy);
      mSettableDraweeHierarchy.setControllerOverlay(mControllerOverlay);
    }
  }
  
  protected void setRetainImageOnFailure(boolean paramBoolean)
  {
    mRetainImageOnFailure = paramBoolean;
  }
  
  protected boolean shouldHandleGesture()
  {
    return shouldRetryOnTap();
  }
  
  protected void submitRequest()
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("AbstractDraweeController#submitRequest");
    }
    Object localObject = getCachedImage();
    if (localObject != null)
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.beginSection("AbstractDraweeController#submitRequest->cache");
      }
      mDataSource = null;
      mIsRequestSubmitted = true;
      mHasFetchFailed = false;
      mEventTracker.recordEvent(DraweeEventTracker.Event.ON_SUBMIT_CACHE_HIT);
      getControllerListener().onSubmit(mId, mCallerContext);
      onImageLoadedFromCacheImmediately(mId, localObject);
      onNewResultInternal(mId, mDataSource, localObject, 1.0F, true, true, true);
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
    else
    {
      mEventTracker.recordEvent(DraweeEventTracker.Event.ON_DATASOURCE_SUBMIT);
      getControllerListener().onSubmit(mId, mCallerContext);
      mSettableDraweeHierarchy.setProgress(0.0F, true);
      mIsRequestSubmitted = true;
      mHasFetchFailed = false;
      mDataSource = getDataSource();
      if (FLog.isLoggable(2)) {
        FLog.v(TAG, "controller %x %s: submitRequest: dataSource: %x", Integer.valueOf(System.identityHashCode(this)), mId, Integer.valueOf(System.identityHashCode(mDataSource)));
      }
      localObject = new BaseDataSubscriber()
      {
        public void onFailureImpl(DataSource paramAnonymousDataSource)
        {
          AbstractDraweeController.this.onFailureInternal(val$id, paramAnonymousDataSource, paramAnonymousDataSource.getFailureCause(), true);
        }
        
        public void onNewResultImpl(DataSource paramAnonymousDataSource)
        {
          boolean bool1 = paramAnonymousDataSource.isFinished();
          boolean bool2 = paramAnonymousDataSource.hasMultipleResults();
          float f = paramAnonymousDataSource.getProgress();
          Object localObject = paramAnonymousDataSource.getResult();
          if (localObject != null)
          {
            AbstractDraweeController.this.onNewResultInternal(val$id, paramAnonymousDataSource, localObject, f, bool1, val$wasImmediate, bool2);
            return;
          }
          if (bool1) {
            AbstractDraweeController.this.onFailureInternal(val$id, paramAnonymousDataSource, new NullPointerException(), true);
          }
        }
        
        public void onProgressUpdate(DataSource paramAnonymousDataSource)
        {
          boolean bool = paramAnonymousDataSource.isFinished();
          float f = paramAnonymousDataSource.getProgress();
          AbstractDraweeController.this.onProgressUpdateInternal(val$id, paramAnonymousDataSource, f, bool);
        }
      };
      mDataSource.subscribe((DataSubscriber)localObject, mUiThreadImmediateExecutor);
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
  }
  
  public String toString()
  {
    return Objects.toStringHelper(this).addValue("isAttached", mIsAttached).addValue("isRequestSubmitted", mIsRequestSubmitted).addValue("hasFetchFailed", mHasFetchFailed).addValue("fetchedImage", getImageHash(mFetchedImage)).addValue("events", mEventTracker.toString()).toString();
  }
  
  private static class InternalForwardingListener<INFO>
    extends ForwardingControllerListener<INFO>
  {
    private InternalForwardingListener() {}
    
    public static InternalForwardingListener createInternal(ControllerListener paramControllerListener1, ControllerListener paramControllerListener2)
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.beginSection("AbstractDraweeController#createInternal");
      }
      InternalForwardingListener localInternalForwardingListener = new InternalForwardingListener();
      localInternalForwardingListener.addListener(paramControllerListener1);
      localInternalForwardingListener.addListener(paramControllerListener2);
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      return localInternalForwardingListener;
    }
  }
}
