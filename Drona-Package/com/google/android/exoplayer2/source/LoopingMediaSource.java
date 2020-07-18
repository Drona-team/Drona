package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.util.HashMap;
import java.util.Map;

public final class LoopingMediaSource
  extends CompositeMediaSource<Void>
{
  private final Map<MediaSource.MediaPeriodId, MediaSource.MediaPeriodId> childMediaPeriodIdToMediaPeriodId;
  private final MediaSource childSource;
  private final int loopCount;
  private final Map<MediaPeriod, MediaSource.MediaPeriodId> mediaPeriodToChildMediaPeriodId;
  
  public LoopingMediaSource(MediaSource paramMediaSource)
  {
    this(paramMediaSource, Integer.MAX_VALUE);
  }
  
  public LoopingMediaSource(MediaSource paramMediaSource, int paramInt)
  {
    boolean bool;
    if (paramInt > 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    childSource = paramMediaSource;
    loopCount = paramInt;
    childMediaPeriodIdToMediaPeriodId = new HashMap();
    mediaPeriodToChildMediaPeriodId = new HashMap();
  }
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    if (loopCount == Integer.MAX_VALUE) {
      return childSource.createPeriod(paramMediaPeriodId, paramAllocator);
    }
    MediaSource.MediaPeriodId localMediaPeriodId = paramMediaPeriodId.copyWithPeriodUid(AbstractConcatenatedTimeline.getChildPeriodUidFromConcatenatedUid(periodUid));
    childMediaPeriodIdToMediaPeriodId.put(localMediaPeriodId, paramMediaPeriodId);
    paramMediaPeriodId = childSource.createPeriod(localMediaPeriodId, paramAllocator);
    mediaPeriodToChildMediaPeriodId.put(paramMediaPeriodId, localMediaPeriodId);
    return paramMediaPeriodId;
  }
  
  protected MediaSource.MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(Void paramVoid, MediaSource.MediaPeriodId paramMediaPeriodId)
  {
    paramVoid = paramMediaPeriodId;
    if (loopCount != Integer.MAX_VALUE) {
      paramVoid = (MediaSource.MediaPeriodId)childMediaPeriodIdToMediaPeriodId.get(paramMediaPeriodId);
    }
    return paramVoid;
  }
  
  protected void onChildSourceInfoRefreshed(Void paramVoid, MediaSource paramMediaSource, Timeline paramTimeline, Object paramObject)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a6 = a5\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public void prepareSourceInternal(ExoPlayer paramExoPlayer, boolean paramBoolean, TransferListener paramTransferListener)
  {
    super.prepareSourceInternal(paramExoPlayer, paramBoolean, paramTransferListener);
    prepareChildSource(null, childSource);
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    childSource.releasePeriod(paramMediaPeriod);
    paramMediaPeriod = (MediaSource.MediaPeriodId)mediaPeriodToChildMediaPeriodId.remove(paramMediaPeriod);
    if (paramMediaPeriod != null) {
      childMediaPeriodIdToMediaPeriodId.remove(paramMediaPeriod);
    }
  }
  
  private static final class InfinitelyLoopingTimeline
    extends ForwardingTimeline
  {
    public InfinitelyLoopingTimeline(Timeline paramTimeline)
    {
      super();
    }
    
    public int getNextWindowIndex(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      paramInt2 = timeline.getNextWindowIndex(paramInt1, paramInt2, paramBoolean);
      paramInt1 = paramInt2;
      if (paramInt2 == -1) {
        paramInt1 = getFirstWindowIndex(paramBoolean);
      }
      return paramInt1;
    }
    
    public int getPreviousWindowIndex(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      paramInt2 = timeline.getPreviousWindowIndex(paramInt1, paramInt2, paramBoolean);
      paramInt1 = paramInt2;
      if (paramInt2 == -1) {
        paramInt1 = getLastWindowIndex(paramBoolean);
      }
      return paramInt1;
    }
  }
  
  private static final class LoopingTimeline
    extends AbstractConcatenatedTimeline
  {
    private final int childPeriodCount;
    private final Timeline childTimeline;
    private final int childWindowCount;
    private final int loopCount;
    
    public LoopingTimeline(Timeline paramTimeline, int paramInt)
    {
      super(localUnshuffledShuffleOrder);
      childTimeline = paramTimeline;
      childPeriodCount = paramTimeline.getPeriodCount();
      childWindowCount = paramTimeline.getWindowCount();
      loopCount = paramInt;
      if (childPeriodCount > 0)
      {
        if (paramInt <= Integer.MAX_VALUE / childPeriodCount) {
          bool = true;
        }
        Assertions.checkState(bool, "LoopingMediaSource contains too many periods");
      }
    }
    
    protected int getChildIndexByChildUid(Object paramObject)
    {
      if (!(paramObject instanceof Integer)) {
        return -1;
      }
      return ((Integer)paramObject).intValue();
    }
    
    protected int getChildIndexByPeriodIndex(int paramInt)
    {
      return paramInt / childPeriodCount;
    }
    
    protected int getChildIndexByWindowIndex(int paramInt)
    {
      return paramInt / childWindowCount;
    }
    
    protected Object getChildUidByChildIndex(int paramInt)
    {
      return Integer.valueOf(paramInt);
    }
    
    protected int getFirstPeriodIndexByChildIndex(int paramInt)
    {
      return paramInt * childPeriodCount;
    }
    
    protected int getFirstWindowIndexByChildIndex(int paramInt)
    {
      return paramInt * childWindowCount;
    }
    
    public int getPeriodCount()
    {
      return childPeriodCount * loopCount;
    }
    
    protected Timeline getTimelineByChildIndex(int paramInt)
    {
      return childTimeline;
    }
    
    public int getWindowCount()
    {
      return childWindowCount * loopCount;
    }
  }
}
