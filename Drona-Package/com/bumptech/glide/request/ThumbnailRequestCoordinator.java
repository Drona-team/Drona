package com.bumptech.glide.request;

import androidx.annotation.Nullable;

public class ThumbnailRequestCoordinator
  implements RequestCoordinator, Request
{
  private Request full;
  private boolean isRunning;
  @Nullable
  private final RequestCoordinator parent;
  private Request thumb;
  
  ThumbnailRequestCoordinator()
  {
    this(null);
  }
  
  public ThumbnailRequestCoordinator(RequestCoordinator paramRequestCoordinator)
  {
    parent = paramRequestCoordinator;
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
    isRunning = true;
    if ((!full.isComplete()) && (!thumb.isRunning())) {
      thumb.begin();
    }
    if ((isRunning) && (!full.isRunning())) {
      full.begin();
    }
  }
  
  public boolean canNotifyCleared(Request paramRequest)
  {
    return (parentCanNotifyCleared()) && (paramRequest.equals(full));
  }
  
  public boolean canNotifyStatusChanged(Request paramRequest)
  {
    return (parentCanNotifyStatusChanged()) && (paramRequest.equals(full)) && (!isAnyResourceSet());
  }
  
  public boolean canSetImage(Request paramRequest)
  {
    return (parentCanSetImage()) && ((paramRequest.equals(full)) || (!full.isResourceSet()));
  }
  
  public void clear()
  {
    isRunning = false;
    thumb.clear();
    full.clear();
  }
  
  public boolean isAnyResourceSet()
  {
    return (parentIsAnyResourceSet()) || (isResourceSet());
  }
  
  public boolean isCleared()
  {
    return full.isCleared();
  }
  
  public boolean isComplete()
  {
    return (full.isComplete()) || (thumb.isComplete());
  }
  
  public boolean isEquivalentTo(Request paramRequest)
  {
    if ((paramRequest instanceof ThumbnailRequestCoordinator))
    {
      paramRequest = (ThumbnailRequestCoordinator)paramRequest;
      if ((full == null ? full == null : full.isEquivalentTo(full)) && (thumb == null ? thumb == null : thumb.isEquivalentTo(thumb))) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isFailed()
  {
    return full.isFailed();
  }
  
  public boolean isResourceSet()
  {
    return (full.isResourceSet()) || (thumb.isResourceSet());
  }
  
  public boolean isRunning()
  {
    return full.isRunning();
  }
  
  public void onRequestFailed(Request paramRequest)
  {
    if (!paramRequest.equals(full)) {
      return;
    }
    if (parent != null) {
      parent.onRequestFailed(this);
    }
  }
  
  public void onRequestSuccess(Request paramRequest)
  {
    if (paramRequest.equals(thumb)) {
      return;
    }
    if (parent != null) {
      parent.onRequestSuccess(this);
    }
    if (!thumb.isComplete()) {
      thumb.clear();
    }
  }
  
  public void recycle()
  {
    full.recycle();
    thumb.recycle();
  }
  
  public void setRequests(Request paramRequest1, Request paramRequest2)
  {
    full = paramRequest1;
    thumb = paramRequest2;
  }
}
