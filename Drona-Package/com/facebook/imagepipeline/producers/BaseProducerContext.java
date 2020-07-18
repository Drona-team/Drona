package com.facebook.imagepipeline.producers;

import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.concurrent.GuardedBy;

public class BaseProducerContext
  implements ProducerContext
{
  @GuardedBy("this")
  private final List<ProducerContextCallbacks> mCallbacks;
  private final Object mCallerContext;
  private final ImageRequest mImageRequest;
  @GuardedBy("this")
  private boolean mIsCancelled;
  @GuardedBy("this")
  private boolean mIsIntermediateResultExpected;
  @GuardedBy("this")
  private boolean mIsPrefetch;
  private final ImageRequest.RequestLevel mLowestPermittedRequestLevel;
  @GuardedBy("this")
  private Priority mPriority;
  private final ProducerListener mProducerListener;
  private final String mUserId;
  
  public BaseProducerContext(ImageRequest paramImageRequest, String paramString, ProducerListener paramProducerListener, Object paramObject, ImageRequest.RequestLevel paramRequestLevel, boolean paramBoolean1, boolean paramBoolean2, Priority paramPriority)
  {
    mImageRequest = paramImageRequest;
    mUserId = paramString;
    mProducerListener = paramProducerListener;
    mCallerContext = paramObject;
    mLowestPermittedRequestLevel = paramRequestLevel;
    mIsPrefetch = paramBoolean1;
    mPriority = paramPriority;
    mIsIntermediateResultExpected = paramBoolean2;
    mIsCancelled = false;
    mCallbacks = new ArrayList();
  }
  
  public static void callOnCancellationRequested(List paramList)
  {
    if (paramList == null) {
      return;
    }
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      ((ProducerContextCallbacks)paramList.next()).onCancellationRequested();
    }
  }
  
  public static void callOnIsIntermediateResultExpectedChanged(List paramList)
  {
    if (paramList == null) {
      return;
    }
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      ((ProducerContextCallbacks)paramList.next()).onIsIntermediateResultExpectedChanged();
    }
  }
  
  public static void callOnIsPrefetchChanged(List paramList)
  {
    if (paramList == null) {
      return;
    }
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      ((ProducerContextCallbacks)paramList.next()).onIsPrefetchChanged();
    }
  }
  
  public static void callOnPriorityChanged(List paramList)
  {
    if (paramList == null) {
      return;
    }
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      ((ProducerContextCallbacks)paramList.next()).onPriorityChanged();
    }
  }
  
  public void addCallbacks(ProducerContextCallbacks paramProducerContextCallbacks)
  {
    try
    {
      mCallbacks.add(paramProducerContextCallbacks);
      boolean bool = mIsCancelled;
      if (bool)
      {
        paramProducerContextCallbacks.onCancellationRequested();
        return;
      }
    }
    catch (Throwable paramProducerContextCallbacks)
    {
      throw paramProducerContextCallbacks;
    }
  }
  
  public void cancel()
  {
    callOnCancellationRequested(cancelNoCallbacks());
  }
  
  public List cancelNoCallbacks()
  {
    try
    {
      boolean bool = mIsCancelled;
      if (bool) {
        return null;
      }
      mIsCancelled = true;
      ArrayList localArrayList = new ArrayList(mCallbacks);
      return localArrayList;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public Object getCallerContext()
  {
    return mCallerContext;
  }
  
  public String getId()
  {
    return mUserId;
  }
  
  public ImageRequest getImageRequest()
  {
    return mImageRequest;
  }
  
  public ProducerListener getListener()
  {
    return mProducerListener;
  }
  
  public ImageRequest.RequestLevel getLowestPermittedRequestLevel()
  {
    return mLowestPermittedRequestLevel;
  }
  
  public Priority getPriority()
  {
    try
    {
      Priority localPriority = mPriority;
      return localPriority;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean isCancelled()
  {
    try
    {
      boolean bool = mIsCancelled;
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean isIntermediateResultExpected()
  {
    try
    {
      boolean bool = mIsIntermediateResultExpected;
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean isPrefetch()
  {
    try
    {
      boolean bool = mIsPrefetch;
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public List setIsIntermediateResultExpectedNoCallbacks(boolean paramBoolean)
  {
    try
    {
      boolean bool = mIsIntermediateResultExpected;
      if (paramBoolean == bool) {
        return null;
      }
      mIsIntermediateResultExpected = paramBoolean;
      ArrayList localArrayList = new ArrayList(mCallbacks);
      return localArrayList;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public List setIsPrefetchNoCallbacks(boolean paramBoolean)
  {
    try
    {
      boolean bool = mIsPrefetch;
      if (paramBoolean == bool) {
        return null;
      }
      mIsPrefetch = paramBoolean;
      ArrayList localArrayList = new ArrayList(mCallbacks);
      return localArrayList;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public List setPriorityNoCallbacks(Priority paramPriority)
  {
    try
    {
      Priority localPriority = mPriority;
      if (paramPriority == localPriority) {
        return null;
      }
      mPriority = paramPriority;
      paramPriority = new ArrayList(mCallbacks);
      return paramPriority;
    }
    catch (Throwable paramPriority)
    {
      throw paramPriority;
    }
  }
}
