package com.facebook.imagepipeline.producers;

import com.facebook.common.executors.StatefulRunnable;
import java.util.Map;

public abstract class StatefulProducerRunnable<T>
  extends StatefulRunnable<T>
{
  private final Consumer<T> mConsumer;
  private final ProducerListener mProducerListener;
  private final String mProducerName;
  private final String mRequestId;
  
  public StatefulProducerRunnable(Consumer paramConsumer, ProducerListener paramProducerListener, String paramString1, String paramString2)
  {
    mConsumer = paramConsumer;
    mProducerListener = paramProducerListener;
    mProducerName = paramString1;
    mRequestId = paramString2;
    mProducerListener.onProducerStart(mRequestId, mProducerName);
  }
  
  protected abstract void disposeResult(Object paramObject);
  
  protected Map getExtraMapOnCancellation()
  {
    return null;
  }
  
  protected Map getExtraMapOnFailure(Exception paramException)
  {
    return null;
  }
  
  protected Map getExtraMapOnSuccess(Object paramObject)
  {
    return null;
  }
  
  protected void onCancellation()
  {
    ProducerListener localProducerListener = mProducerListener;
    String str1 = mRequestId;
    String str2 = mProducerName;
    Map localMap;
    if (mProducerListener.requiresExtraMap(mRequestId)) {
      localMap = getExtraMapOnCancellation();
    } else {
      localMap = null;
    }
    localProducerListener.onProducerFinishWithCancellation(str1, str2, localMap);
    mConsumer.onCancellation();
  }
  
  protected void onFailure(Exception paramException)
  {
    ProducerListener localProducerListener = mProducerListener;
    String str1 = mRequestId;
    String str2 = mProducerName;
    Map localMap;
    if (mProducerListener.requiresExtraMap(mRequestId)) {
      localMap = getExtraMapOnFailure(paramException);
    } else {
      localMap = null;
    }
    localProducerListener.onProducerFinishWithFailure(str1, str2, paramException, localMap);
    mConsumer.onFailure(paramException);
  }
  
  protected void onSuccess(Object paramObject)
  {
    ProducerListener localProducerListener = mProducerListener;
    String str1 = mRequestId;
    String str2 = mProducerName;
    Map localMap;
    if (mProducerListener.requiresExtraMap(mRequestId)) {
      localMap = getExtraMapOnSuccess(paramObject);
    } else {
      localMap = null;
    }
    localProducerListener.onProducerFinishWithSuccess(str1, str2, localMap);
    mConsumer.onNewResult(paramObject, 1);
  }
}
