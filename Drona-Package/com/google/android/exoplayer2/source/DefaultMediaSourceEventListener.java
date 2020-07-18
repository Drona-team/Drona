package com.google.android.exoplayer2.source;

import java.io.IOException;

public abstract class DefaultMediaSourceEventListener
  implements MediaSourceEventListener
{
  public DefaultMediaSourceEventListener() {}
  
  public void onDownstreamFormatChanged(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.MediaLoadData paramMediaLoadData) {}
  
  public void onLoadCanceled(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData) {}
  
  public void onLoadCompleted(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData) {}
  
  public void onLoadError(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData, IOException paramIOException, boolean paramBoolean) {}
  
  public void onLoadStarted(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.LoadEventInfo paramLoadEventInfo, MediaSourceEventListener.MediaLoadData paramMediaLoadData) {}
  
  public void onMediaPeriodCreated(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId) {}
  
  public void onMediaPeriodReleased(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId) {}
  
  public void onReadingStarted(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId) {}
  
  public void onUpstreamDiscarded(int paramInt, MediaSource.MediaPeriodId paramMediaPeriodId, MediaSourceEventListener.MediaLoadData paramMediaLoadData) {}
}
