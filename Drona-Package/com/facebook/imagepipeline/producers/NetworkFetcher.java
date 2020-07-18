package com.facebook.imagepipeline.producers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public abstract interface NetworkFetcher<FETCH_STATE extends FetchState>
{
  public abstract FetchState createFetchState(Consumer paramConsumer, ProducerContext paramProducerContext);
  
  public abstract void fetch(FetchState paramFetchState, Callback paramCallback);
  
  public abstract Map getExtraMap(FetchState paramFetchState, int paramInt);
  
  public abstract void onFetchCompletion(FetchState paramFetchState, int paramInt);
  
  public abstract boolean shouldPropagate(FetchState paramFetchState);
  
  public static abstract interface Callback
  {
    public abstract void onCancellation();
    
    public abstract void onFailure(Throwable paramThrowable);
    
    public abstract void onResponse(InputStream paramInputStream, int paramInt)
      throws IOException;
  }
}