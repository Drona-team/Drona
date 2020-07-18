package com.facebook.imagepipeline.producers;

public class BaseProducerContextCallbacks
  implements ProducerContextCallbacks
{
  public BaseProducerContextCallbacks() {}
  
  public void onCancellationRequested() {}
  
  public void onIsIntermediateResultExpectedChanged() {}
  
  public void onIsPrefetchChanged() {}
  
  public void onPriorityChanged() {}
}
