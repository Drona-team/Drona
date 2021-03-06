package com.bumptech.glide.request;

import androidx.annotation.Nullable;

public final class ErrorRequestCoordinator
  implements RequestCoordinator, Request
{
  private Request error;
  @Nullable
  private final RequestCoordinator parent;
  private Request primary;
  
  public ErrorRequestCoordinator(RequestCoordinator paramRequestCoordinator)
  {
    parent = paramRequestCoordinator;
  }
  
  private boolean isValidRequest(Request paramRequest)
  {
    return (paramRequest.equals(primary)) || ((primary.isFailed()) && (paramRequest.equals(error)));
  }
  
  private boolean parentCanNotifyCleared()
  {
    return (parent == null) || (parent.canNotifyCleared(this));
  }
  
  private boolean parentCanNotifyStatusChanged()
  {
    return (parent == null) || (parent.canNotifyStatusChanged(this));
  }
  
  private boolean parentCanSetImage()
  {
    return (parent == null) || (parent.canSetImage(this));
  }
  
  private boolean parentIsAnyResourceSet()
  {
    return (parent != null) && (parent.isAnyResourceSet());
  }
  
  public void begin()
  {
    if (!primary.isRunning()) {
      primary.begin();
    }
  }
  
  public boolean canNotifyCleared(Request paramRequest)
  {
    return (parentCanNotifyCleared()) && (isValidRequest(paramRequest));
  }
  
  public boolean canNotifyStatusChanged(Request paramRequest)
  {
    return (parentCanNotifyStatusChanged()) && (isValidRequest(paramRequest));
  }
  
  public boolean canSetImage(Request paramRequest)
  {
    return (parentCanSetImage()) && (isValidRequest(paramRequest));
  }
  
  public void clear()
  {
    primary.clear();
    if (error.isRunning()) {
      error.clear();
    }
  }
  
  public boolean isAnyResourceSet()
  {
    return (parentIsAnyResourceSet()) || (isResourceSet());
  }
  
  public boolean isCleared()
  {
    if (primary.isFailed()) {}
    for (Request localRequest = error;; localRequest = primary) {
      return localRequest.isCleared();
    }
  }
  
  public boolean isComplete()
  {
    if (primary.isFailed()) {}
    for (Request localRequest = error;; localRequest = primary) {
      return localRequest.isComplete();
    }
  }
  
  public boolean isEquivalentTo(Request paramRequest)
  {
    if ((paramRequest instanceof ErrorRequestCoordinator))
    {
      paramRequest = (ErrorRequestCoordinator)paramRequest;
      if ((primary.isEquivalentTo(primary)) && (error.isEquivalentTo(error))) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isFailed()
  {
    return (primary.isFailed()) && (error.isFailed());
  }
  
  public boolean isResourceSet()
  {
    if (primary.isFailed()) {}
    for (Request localRequest = error;; localRequest = primary) {
      return localRequest.isResourceSet();
    }
  }
  
  public boolean isRunning()
  {
    if (primary.isFailed()) {}
    for (Request localRequest = error;; localRequest = primary) {
      return localRequest.isRunning();
    }
  }
  
  public void onRequestFailed(Request paramRequest)
  {
    if (!paramRequest.equals(error))
    {
      if (!error.isRunning()) {
        error.begin();
      }
    }
    else if (parent != null) {
      parent.onRequestFailed(this);
    }
  }
  
  public void onRequestSuccess(Request paramRequest)
  {
    if (parent != null) {
      parent.onRequestSuccess(this);
    }
  }
  
  public void recycle()
  {
    primary.recycle();
    error.recycle();
  }
  
  public void setRequests(Request paramRequest1, Request paramRequest2)
  {
    primary = paramRequest1;
    error = paramRequest2;
  }
}
