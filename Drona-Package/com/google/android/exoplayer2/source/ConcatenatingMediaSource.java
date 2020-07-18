package com.google.android.exoplayer2.source;

import android.os.Handler;
import android.util.Pair;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.PlayerMessage;
import com.google.android.exoplayer2.PlayerMessage.Target;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConcatenatingMediaSource
  extends CompositeMediaSource<MediaSourceHolder>
  implements PlayerMessage.Target
{
  private static final int MSG_ADD = 0;
  private static final int MSG_MOVE = 2;
  private static final int MSG_NOTIFY_LISTENER = 4;
  private static final int MSG_ON_COMPLETION = 5;
  private static final int MSG_REMOVE = 1;
  private static final int MSG_SET_SHUFFLE_ORDER = 3;
  private final boolean isAtomic;
  private boolean listenerNotificationScheduled;
  private final Map<MediaPeriod, MediaSourceHolder> mediaSourceByMediaPeriod;
  private final Map<Object, MediaSourceHolder> mediaSourceByUid;
  private final List<MediaSourceHolder> mediaSourceHolders;
  private final List<MediaSourceHolder> mediaSourcesPublic;
  private final List<Runnable> pendingOnCompletionActions;
  private final Timeline.Period period;
  private int periodCount;
  @Nullable
  private ExoPlayer player;
  @Nullable
  private Handler playerApplicationHandler;
  private ShuffleOrder shuffleOrder;
  private final boolean useLazyPreparation;
  private final Timeline.Window window;
  private int windowCount;
  
  public ConcatenatingMediaSource(boolean paramBoolean, ShuffleOrder paramShuffleOrder, MediaSource... paramVarArgs)
  {
    this(paramBoolean, false, paramShuffleOrder, paramVarArgs);
  }
  
  public ConcatenatingMediaSource(boolean paramBoolean1, boolean paramBoolean2, ShuffleOrder paramShuffleOrder, MediaSource... paramVarArgs)
  {
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      Assertions.checkNotNull(paramVarArgs[i]);
      i += 1;
    }
    ShuffleOrder localShuffleOrder = paramShuffleOrder;
    if (paramShuffleOrder.getLength() > 0) {
      localShuffleOrder = paramShuffleOrder.cloneAndClear();
    }
    shuffleOrder = localShuffleOrder;
    mediaSourceByMediaPeriod = new IdentityHashMap();
    mediaSourceByUid = new HashMap();
    mediaSourcesPublic = new ArrayList();
    mediaSourceHolders = new ArrayList();
    pendingOnCompletionActions = new ArrayList();
    isAtomic = paramBoolean1;
    useLazyPreparation = paramBoolean2;
    window = new Timeline.Window();
    period = new Timeline.Period();
    addMediaSources(Arrays.asList(paramVarArgs));
  }
  
  public ConcatenatingMediaSource(boolean paramBoolean, MediaSource... paramVarArgs)
  {
    this(paramBoolean, new ShuffleOrder.DefaultShuffleOrder(0), paramVarArgs);
  }
  
  public ConcatenatingMediaSource(MediaSource... paramVarArgs)
  {
    this(false, paramVarArgs);
  }
  
  private void addMediaSourceInternal(int paramInt, MediaSourceHolder paramMediaSourceHolder)
  {
    if (paramInt > 0)
    {
      MediaSourceHolder localMediaSourceHolder = (MediaSourceHolder)mediaSourceHolders.get(paramInt - 1);
      paramMediaSourceHolder.reset(paramInt, firstWindowIndexInChild + timeline.getWindowCount(), firstPeriodIndexInChild + timeline.getPeriodCount());
    }
    else
    {
      paramMediaSourceHolder.reset(paramInt, 0, 0);
    }
    correctOffsets(paramInt, 1, timeline.getWindowCount(), timeline.getPeriodCount());
    mediaSourceHolders.add(paramInt, paramMediaSourceHolder);
    mediaSourceByUid.put(colors, paramMediaSourceHolder);
    if (!useLazyPreparation)
    {
      hasStartedPreparing = true;
      prepareChildSource(paramMediaSourceHolder, mediaSource);
    }
  }
  
  private void addMediaSourcesInternal(int paramInt, Collection paramCollection)
  {
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      addMediaSourceInternal(paramInt, (MediaSourceHolder)paramCollection.next());
      paramInt += 1;
    }
  }
  
  private void correctOffsets(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    windowCount += paramInt3;
    periodCount += paramInt4;
    while (paramInt1 < mediaSourceHolders.size())
    {
      MediaSourceHolder localMediaSourceHolder = (MediaSourceHolder)mediaSourceHolders.get(paramInt1);
      childIndex += paramInt2;
      localMediaSourceHolder = (MediaSourceHolder)mediaSourceHolders.get(paramInt1);
      firstWindowIndexInChild += paramInt3;
      localMediaSourceHolder = (MediaSourceHolder)mediaSourceHolders.get(paramInt1);
      firstPeriodIndexInChild += paramInt4;
      paramInt1 += 1;
    }
  }
  
  private static Object getChildPeriodUid(MediaSourceHolder paramMediaSourceHolder, Object paramObject)
  {
    Object localObject = AbstractConcatenatedTimeline.getChildPeriodUidFromConcatenatedUid(paramObject);
    paramObject = localObject;
    if (localObject.equals(DeferredTimeline.DUMMY_ID)) {
      paramObject = timeline.replacedId;
    }
    return paramObject;
  }
  
  private static Object getMediaSourceHolderUid(Object paramObject)
  {
    return AbstractConcatenatedTimeline.getChildTimelineUidFromConcatenatedUid(paramObject);
  }
  
  private static Object getPeriodUid(MediaSourceHolder paramMediaSourceHolder, Object paramObject)
  {
    Object localObject = paramObject;
    if (timeline.replacedId.equals(paramObject)) {
      localObject = DeferredTimeline.DUMMY_ID;
    }
    return AbstractConcatenatedTimeline.getConcatenatedUid(colors, localObject);
  }
  
  private void maybeReleaseChildSource(MediaSourceHolder paramMediaSourceHolder)
  {
    if ((isRemoved) && (hasStartedPreparing) && (activeMediaPeriods.isEmpty())) {
      releaseChildSource(paramMediaSourceHolder);
    }
  }
  
  private void moveMediaSourceInternal(int paramInt1, int paramInt2)
  {
    int j = Math.min(paramInt1, paramInt2);
    int i = j;
    int m = Math.max(paramInt1, paramInt2);
    int k = mediaSourceHolders.get(j)).firstWindowIndexInChild;
    j = mediaSourceHolders.get(j)).firstPeriodIndexInChild;
    mediaSourceHolders.add(paramInt2, mediaSourceHolders.remove(paramInt1));
    paramInt2 = k;
    paramInt1 = j;
    while (i <= m)
    {
      MediaSourceHolder localMediaSourceHolder = (MediaSourceHolder)mediaSourceHolders.get(i);
      firstWindowIndexInChild = paramInt2;
      firstPeriodIndexInChild = paramInt1;
      paramInt2 += timeline.getWindowCount();
      paramInt1 += timeline.getPeriodCount();
      i += 1;
    }
  }
  
  private void notifyListener()
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a3 = a2\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  private void removeMediaSourceInternal(int paramInt)
  {
    MediaSourceHolder localMediaSourceHolder = (MediaSourceHolder)mediaSourceHolders.remove(paramInt);
    mediaSourceByUid.remove(colors);
    DeferredTimeline localDeferredTimeline = timeline;
    correctOffsets(paramInt, -1, -localDeferredTimeline.getWindowCount(), -localDeferredTimeline.getPeriodCount());
    isRemoved = true;
    maybeReleaseChildSource(localMediaSourceHolder);
  }
  
  private void scheduleListenerNotification(Runnable paramRunnable)
  {
    if (!listenerNotificationScheduled)
    {
      ((ExoPlayer)Assertions.checkNotNull(player)).createMessage(this).setType(4).send();
      listenerNotificationScheduled = true;
    }
    if (paramRunnable != null) {
      pendingOnCompletionActions.add(paramRunnable);
    }
  }
  
  private void updateMediaSourceInternal(MediaSourceHolder paramMediaSourceHolder, Timeline paramTimeline)
  {
    if (paramMediaSourceHolder != null)
    {
      Object localObject1 = timeline;
      if (((DeferredTimeline)localObject1).getTimeline() == paramTimeline) {
        return;
      }
      int i = paramTimeline.getWindowCount() - ((ForwardingTimeline)localObject1).getWindowCount();
      int j = paramTimeline.getPeriodCount() - ((ForwardingTimeline)localObject1).getPeriodCount();
      if ((i != 0) || (j != 0)) {
        correctOffsets(childIndex + 1, 0, i, j);
      }
      if (isPrepared)
      {
        timeline = ((DeferredTimeline)localObject1).cloneWithUpdatedTimeline(paramTimeline);
      }
      else if (paramTimeline.isEmpty())
      {
        timeline = DeferredTimeline.createWithRealTimeline(paramTimeline, DeferredTimeline.DUMMY_ID);
      }
      else
      {
        boolean bool;
        if (activeMediaPeriods.size() <= 1) {
          bool = true;
        } else {
          bool = false;
        }
        Assertions.checkState(bool);
        if (activeMediaPeriods.isEmpty()) {
          localObject1 = null;
        } else {
          localObject1 = (DeferredMediaPeriod)activeMediaPeriods.get(0);
        }
        long l2 = window.getDefaultPositionUs();
        long l1 = l2;
        if (localObject1 != null)
        {
          long l3 = ((DeferredMediaPeriod)localObject1).getPreparePositionUs();
          l1 = l2;
          if (l3 != 0L) {
            l1 = l3;
          }
        }
        Pair localPair = paramTimeline.getPeriodPosition(window, period, 0, l1);
        Object localObject2 = first;
        l1 = ((Long)second).longValue();
        timeline = DeferredTimeline.createWithRealTimeline(paramTimeline, localObject2);
        if (localObject1 != null)
        {
          ((DeferredMediaPeriod)localObject1).overridePreparePositionUs(l1);
          ((DeferredMediaPeriod)localObject1).createPeriod(move.copyWithPeriodUid(getChildPeriodUid(paramMediaSourceHolder, move.periodUid)));
        }
      }
      isPrepared = true;
      scheduleListenerNotification(null);
      return;
    }
    throw new IllegalArgumentException();
  }
  
  public final void addMediaSource(int paramInt, MediaSource paramMediaSource)
  {
    try
    {
      addMediaSource(paramInt, paramMediaSource, null);
      return;
    }
    catch (Throwable paramMediaSource)
    {
      throw paramMediaSource;
    }
  }
  
  public final void addMediaSource(int paramInt, MediaSource paramMediaSource, Runnable paramRunnable)
  {
    try
    {
      addMediaSources(paramInt, Collections.singletonList(paramMediaSource), paramRunnable);
      return;
    }
    catch (Throwable paramMediaSource)
    {
      throw paramMediaSource;
    }
  }
  
  public final void addMediaSource(MediaSource paramMediaSource)
  {
    try
    {
      addMediaSource(mediaSourcesPublic.size(), paramMediaSource, null);
      return;
    }
    catch (Throwable paramMediaSource)
    {
      throw paramMediaSource;
    }
  }
  
  public final void addMediaSource(MediaSource paramMediaSource, Runnable paramRunnable)
  {
    try
    {
      addMediaSource(mediaSourcesPublic.size(), paramMediaSource, paramRunnable);
      return;
    }
    catch (Throwable paramMediaSource)
    {
      throw paramMediaSource;
    }
  }
  
  public final void addMediaSources(int paramInt, Collection paramCollection)
  {
    try
    {
      addMediaSources(paramInt, paramCollection, null);
      return;
    }
    catch (Throwable paramCollection)
    {
      throw paramCollection;
    }
  }
  
  public final void addMediaSources(int paramInt, Collection paramCollection, Runnable paramRunnable)
  {
    try
    {
      Object localObject = paramCollection.iterator();
      while (((Iterator)localObject).hasNext()) {
        Assertions.checkNotNull((MediaSource)((Iterator)localObject).next());
      }
      localObject = new ArrayList(paramCollection.size());
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext()) {
        ((List)localObject).add(new MediaSourceHolder((MediaSource)localIterator.next()));
      }
      mediaSourcesPublic.addAll(paramInt, (Collection)localObject);
      if ((player != null) && (!paramCollection.isEmpty())) {
        player.createMessage(this).setType(0).setPayload(new MessageData(paramInt, localObject, paramRunnable)).send();
      } else if (paramRunnable != null) {
        paramRunnable.run();
      }
      return;
    }
    catch (Throwable paramCollection)
    {
      throw paramCollection;
    }
  }
  
  public final void addMediaSources(Collection paramCollection)
  {
    try
    {
      addMediaSources(mediaSourcesPublic.size(), paramCollection, null);
      return;
    }
    catch (Throwable paramCollection)
    {
      throw paramCollection;
    }
  }
  
  public final void addMediaSources(Collection paramCollection, Runnable paramRunnable)
  {
    try
    {
      addMediaSources(mediaSourcesPublic.size(), paramCollection, paramRunnable);
      return;
    }
    catch (Throwable paramCollection)
    {
      throw paramCollection;
    }
  }
  
  public final void clear()
  {
    try
    {
      clear(null);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void clear(Runnable paramRunnable)
  {
    try
    {
      removeMediaSourceRange(0, getSize(), paramRunnable);
      return;
    }
    catch (Throwable paramRunnable)
    {
      throw paramRunnable;
    }
  }
  
  public final MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    Object localObject = getMediaSourceHolderUid(periodUid);
    MediaSourceHolder localMediaSourceHolder = (MediaSourceHolder)mediaSourceByUid.get(localObject);
    localObject = localMediaSourceHolder;
    if (localMediaSourceHolder == null)
    {
      localObject = new MediaSourceHolder(new DummyMediaSource(null));
      hasStartedPreparing = true;
    }
    paramAllocator = new DeferredMediaPeriod(mediaSource, paramMediaPeriodId, paramAllocator);
    mediaSourceByMediaPeriod.put(paramAllocator, localObject);
    activeMediaPeriods.add(paramAllocator);
    if (!hasStartedPreparing)
    {
      hasStartedPreparing = true;
      prepareChildSource(localObject, mediaSource);
      return paramAllocator;
    }
    if (isPrepared) {
      paramAllocator.createPeriod(paramMediaPeriodId.copyWithPeriodUid(getChildPeriodUid((MediaSourceHolder)localObject, periodUid)));
    }
    return paramAllocator;
  }
  
  protected MediaSource.MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(MediaSourceHolder paramMediaSourceHolder, MediaSource.MediaPeriodId paramMediaPeriodId)
  {
    int i = 0;
    while (i < activeMediaPeriods.size())
    {
      if (activeMediaPeriods.get(i)).move.windowSequenceNumber == windowSequenceNumber) {
        return paramMediaPeriodId.copyWithPeriodUid(getPeriodUid(paramMediaSourceHolder, periodUid));
      }
      i += 1;
    }
    return null;
  }
  
  public final MediaSource getMediaSource(int paramInt)
  {
    try
    {
      MediaSource localMediaSource = mediaSourcesPublic.get(paramInt)).mediaSource;
      return localMediaSource;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final int getSize()
  {
    try
    {
      int i = mediaSourcesPublic.size();
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  protected int getWindowIndexForChildWindowIndex(MediaSourceHolder paramMediaSourceHolder, int paramInt)
  {
    return paramInt + firstWindowIndexInChild;
  }
  
  public final void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {
    if (player == null) {
      return;
    }
    Handler localHandler;
    switch (paramInt)
    {
    default: 
      throw new IllegalStateException();
    case 5: 
      paramObject = (List)Util.castNonNull(paramObject);
      localHandler = (Handler)Assertions.checkNotNull(playerApplicationHandler);
      paramInt = 0;
    }
    while (paramInt < paramObject.size())
    {
      localHandler.post((Runnable)paramObject.get(paramInt));
      paramInt += 1;
      continue;
      notifyListener();
      return;
      paramObject = (MessageData)Util.castNonNull(paramObject);
      shuffleOrder = ((ShuffleOrder)customData);
      scheduleListenerNotification(actionOnCompletion);
      return;
      paramObject = (MessageData)Util.castNonNull(paramObject);
      shuffleOrder = shuffleOrder.cloneAndRemove(index, index + 1);
      shuffleOrder = shuffleOrder.cloneAndInsert(((Integer)customData).intValue(), 1);
      moveMediaSourceInternal(index, ((Integer)customData).intValue());
      scheduleListenerNotification(actionOnCompletion);
      return;
      paramObject = (MessageData)Util.castNonNull(paramObject);
      int i = index;
      paramInt = ((Integer)customData).intValue();
      if ((i == 0) && (paramInt == shuffleOrder.getLength())) {
        shuffleOrder = shuffleOrder.cloneAndClear();
      } else {
        shuffleOrder = shuffleOrder.cloneAndRemove(i, paramInt);
      }
      paramInt -= 1;
      while (paramInt >= i)
      {
        removeMediaSourceInternal(paramInt);
        paramInt -= 1;
      }
      scheduleListenerNotification(actionOnCompletion);
      return;
      paramObject = (MessageData)Util.castNonNull(paramObject);
      shuffleOrder = shuffleOrder.cloneAndInsert(index, ((Collection)customData).size());
      addMediaSourcesInternal(index, (Collection)customData);
      scheduleListenerNotification(actionOnCompletion);
    }
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {}
  
  public final void moveMediaSource(int paramInt1, int paramInt2)
  {
    try
    {
      moveMediaSource(paramInt1, paramInt2, null);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void moveMediaSource(int paramInt1, int paramInt2, Runnable paramRunnable)
  {
    if ((paramInt1 != paramInt2) || (paramRunnable != null)) {}
    try
    {
      paramRunnable.run();
      return;
    }
    catch (Throwable paramRunnable)
    {
      throw paramRunnable;
    }
    mediaSourcesPublic.add(paramInt2, mediaSourcesPublic.remove(paramInt1));
    if (player != null) {
      player.createMessage(this).setType(2).setPayload(new MessageData(paramInt1, Integer.valueOf(paramInt2), paramRunnable)).send();
    } else if (paramRunnable != null) {
      paramRunnable.run();
    }
  }
  
  protected final void onChildSourceInfoRefreshed(MediaSourceHolder paramMediaSourceHolder, MediaSource paramMediaSource, Timeline paramTimeline, Object paramObject)
  {
    updateMediaSourceInternal(paramMediaSourceHolder, paramTimeline);
  }
  
  public final void prepareSourceInternal(ExoPlayer paramExoPlayer, boolean paramBoolean, TransferListener paramTransferListener)
  {
    try
    {
      super.prepareSourceInternal(paramExoPlayer, paramBoolean, paramTransferListener);
      player = paramExoPlayer;
      playerApplicationHandler = new Handler(paramExoPlayer.getApplicationLooper());
      if (mediaSourcesPublic.isEmpty())
      {
        notifyListener();
      }
      else
      {
        shuffleOrder = shuffleOrder.cloneAndInsert(0, mediaSourcesPublic.size());
        addMediaSourcesInternal(0, mediaSourcesPublic);
        scheduleListenerNotification(null);
      }
      return;
    }
    catch (Throwable paramExoPlayer)
    {
      throw paramExoPlayer;
    }
  }
  
  public final void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    MediaSourceHolder localMediaSourceHolder = (MediaSourceHolder)Assertions.checkNotNull(mediaSourceByMediaPeriod.remove(paramMediaPeriod));
    ((DeferredMediaPeriod)paramMediaPeriod).releasePeriod();
    activeMediaPeriods.remove(paramMediaPeriod);
    maybeReleaseChildSource(localMediaSourceHolder);
  }
  
  public final void releaseSourceInternal()
  {
    super.releaseSourceInternal();
    mediaSourceHolders.clear();
    mediaSourceByUid.clear();
    player = null;
    playerApplicationHandler = null;
    shuffleOrder = shuffleOrder.cloneAndClear();
    windowCount = 0;
    periodCount = 0;
  }
  
  public final void removeMediaSource(int paramInt)
  {
    try
    {
      removeMediaSource(paramInt, null);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void removeMediaSource(int paramInt, Runnable paramRunnable)
  {
    try
    {
      removeMediaSourceRange(paramInt, paramInt + 1, paramRunnable);
      return;
    }
    catch (Throwable paramRunnable)
    {
      throw paramRunnable;
    }
  }
  
  public final void removeMediaSourceRange(int paramInt1, int paramInt2)
  {
    try
    {
      removeMediaSourceRange(paramInt1, paramInt2, null);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void removeMediaSourceRange(int paramInt1, int paramInt2, Runnable paramRunnable)
  {
    try
    {
      Util.removeRange(mediaSourcesPublic, paramInt1, paramInt2);
      if (paramInt1 == paramInt2)
      {
        if (paramRunnable != null) {
          paramRunnable.run();
        }
        return;
      }
      if (player != null) {
        player.createMessage(this).setType(1).setPayload(new MessageData(paramInt1, Integer.valueOf(paramInt2), paramRunnable)).send();
      } else if (paramRunnable != null) {
        paramRunnable.run();
      }
      return;
    }
    catch (Throwable paramRunnable)
    {
      throw paramRunnable;
    }
  }
  
  public final void setShuffleOrder(ShuffleOrder paramShuffleOrder)
  {
    try
    {
      setShuffleOrder(paramShuffleOrder, null);
      return;
    }
    catch (Throwable paramShuffleOrder)
    {
      throw paramShuffleOrder;
    }
  }
  
  public final void setShuffleOrder(ShuffleOrder paramShuffleOrder, Runnable paramRunnable)
  {
    try
    {
      ExoPlayer localExoPlayer = player;
      ShuffleOrder localShuffleOrder;
      if (localExoPlayer != null)
      {
        int i = getSize();
        localShuffleOrder = paramShuffleOrder;
        if (paramShuffleOrder.getLength() != i) {
          localShuffleOrder = paramShuffleOrder.cloneAndClear().cloneAndInsert(0, i);
        }
        localExoPlayer.createMessage(this).setType(3).setPayload(new MessageData(0, localShuffleOrder, paramRunnable)).send();
      }
      else
      {
        localShuffleOrder = paramShuffleOrder;
        if (paramShuffleOrder.getLength() > 0) {
          localShuffleOrder = paramShuffleOrder.cloneAndClear();
        }
        shuffleOrder = localShuffleOrder;
        if (paramRunnable != null) {
          paramRunnable.run();
        }
      }
      return;
    }
    catch (Throwable paramShuffleOrder)
    {
      throw paramShuffleOrder;
    }
  }
  
  private static final class ConcatenatedTimeline
    extends AbstractConcatenatedTimeline
  {
    private final HashMap<Object, Integer> childIndexByUid;
    private final int[] firstPeriodInChildIndices;
    private final int[] firstWindowInChildIndices;
    private final int periodCount;
    private final Timeline[] timelines;
    private final Object[] uids;
    private final int windowCount;
    
    public ConcatenatedTimeline(Collection paramCollection, int paramInt1, int paramInt2, ShuffleOrder paramShuffleOrder, boolean paramBoolean)
    {
      super(paramShuffleOrder);
      windowCount = paramInt1;
      periodCount = paramInt2;
      paramInt1 = paramCollection.size();
      firstPeriodInChildIndices = new int[paramInt1];
      firstWindowInChildIndices = new int[paramInt1];
      timelines = new Timeline[paramInt1];
      uids = new Object[paramInt1];
      childIndexByUid = new HashMap();
      paramCollection = paramCollection.iterator();
      paramInt1 = 0;
      while (paramCollection.hasNext())
      {
        paramShuffleOrder = (ConcatenatingMediaSource.MediaSourceHolder)paramCollection.next();
        timelines[paramInt1] = timeline;
        firstPeriodInChildIndices[paramInt1] = firstPeriodIndexInChild;
        firstWindowInChildIndices[paramInt1] = firstWindowIndexInChild;
        uids[paramInt1] = colors;
        childIndexByUid.put(uids[paramInt1], Integer.valueOf(paramInt1));
        paramInt1 += 1;
      }
    }
    
    protected int getChildIndexByChildUid(Object paramObject)
    {
      paramObject = (Integer)childIndexByUid.get(paramObject);
      if (paramObject == null) {
        return -1;
      }
      return paramObject.intValue();
    }
    
    protected int getChildIndexByPeriodIndex(int paramInt)
    {
      return Util.binarySearchFloor(firstPeriodInChildIndices, paramInt + 1, false, false);
    }
    
    protected int getChildIndexByWindowIndex(int paramInt)
    {
      return Util.binarySearchFloor(firstWindowInChildIndices, paramInt + 1, false, false);
    }
    
    protected Object getChildUidByChildIndex(int paramInt)
    {
      return uids[paramInt];
    }
    
    protected int getFirstPeriodIndexByChildIndex(int paramInt)
    {
      return firstPeriodInChildIndices[paramInt];
    }
    
    protected int getFirstWindowIndexByChildIndex(int paramInt)
    {
      return firstWindowInChildIndices[paramInt];
    }
    
    public int getPeriodCount()
    {
      return periodCount;
    }
    
    protected Timeline getTimelineByChildIndex(int paramInt)
    {
      return timelines[paramInt];
    }
    
    public int getWindowCount()
    {
      return windowCount;
    }
  }
  
  private static final class DeferredTimeline
    extends ForwardingTimeline
  {
    private static final Object DUMMY_ID = new Object();
    private static final ConcatenatingMediaSource.DummyTimeline DUMMY_TIMELINE = new ConcatenatingMediaSource.DummyTimeline(null);
    private final Object replacedId;
    
    public DeferredTimeline()
    {
      this(DUMMY_TIMELINE, DUMMY_ID);
    }
    
    private DeferredTimeline(Timeline paramTimeline, Object paramObject)
    {
      super();
      replacedId = paramObject;
    }
    
    public static DeferredTimeline createWithRealTimeline(Timeline paramTimeline, Object paramObject)
    {
      return new DeferredTimeline(paramTimeline, paramObject);
    }
    
    public DeferredTimeline cloneWithUpdatedTimeline(Timeline paramTimeline)
    {
      return new DeferredTimeline(paramTimeline, replacedId);
    }
    
    public int getIndexOfPeriod(Object paramObject)
    {
      Timeline localTimeline = timeline;
      Object localObject = paramObject;
      if (DUMMY_ID.equals(paramObject)) {
        localObject = replacedId;
      }
      return localTimeline.getIndexOfPeriod(localObject);
    }
    
    public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
    {
      timeline.getPeriod(paramInt, paramPeriod, paramBoolean);
      if (Util.areEqual(element, replacedId)) {
        element = DUMMY_ID;
      }
      return paramPeriod;
    }
    
    public Timeline getTimeline()
    {
      return timeline;
    }
    
    public Object getUidOfPeriod(int paramInt)
    {
      Object localObject2 = timeline.getUidOfPeriod(paramInt);
      Object localObject1 = localObject2;
      if (Util.areEqual(localObject2, replacedId)) {
        localObject1 = DUMMY_ID;
      }
      return localObject1;
    }
  }
  
  private static final class DummyMediaSource
    extends BaseMediaSource
  {
    private DummyMediaSource() {}
    
    public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
    {
      throw new UnsupportedOperationException();
    }
    
    public void maybeThrowSourceInfoRefreshError()
      throws IOException
    {}
    
    protected void prepareSourceInternal(ExoPlayer paramExoPlayer, boolean paramBoolean, TransferListener paramTransferListener) {}
    
    public void releasePeriod(MediaPeriod paramMediaPeriod) {}
    
    protected void releaseSourceInternal() {}
  }
  
  private static final class DummyTimeline
    extends Timeline
  {
    private DummyTimeline() {}
    
    public int getIndexOfPeriod(Object paramObject)
    {
      if (paramObject == ConcatenatingMediaSource.DeferredTimeline.DUMMY_ID) {
        return 0;
      }
      return -1;
    }
    
    public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
    {
      return paramPeriod.get(Integer.valueOf(0), ConcatenatingMediaSource.DeferredTimeline.DUMMY_ID, 0, -9223372036854775807L, 0L);
    }
    
    public int getPeriodCount()
    {
      return 1;
    }
    
    public Object getUidOfPeriod(int paramInt)
    {
      return ConcatenatingMediaSource.DeferredTimeline.DUMMY_ID;
    }
    
    public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
    {
      return paramWindow.readInt(null, -9223372036854775807L, -9223372036854775807L, false, true, 0L, -9223372036854775807L, 0, 0, 0L);
    }
    
    public int getWindowCount()
    {
      return 1;
    }
  }
  
  static final class MediaSourceHolder
    implements Comparable<MediaSourceHolder>
  {
    public List<DeferredMediaPeriod> activeMediaPeriods;
    public int childIndex;
    public final Object colors;
    public int firstPeriodIndexInChild;
    public int firstWindowIndexInChild;
    public boolean hasStartedPreparing;
    public boolean isPrepared;
    public boolean isRemoved;
    public final MediaSource mediaSource;
    public ConcatenatingMediaSource.DeferredTimeline timeline;
    
    public MediaSourceHolder(MediaSource paramMediaSource)
    {
      mediaSource = paramMediaSource;
      timeline = new ConcatenatingMediaSource.DeferredTimeline();
      activeMediaPeriods = new ArrayList();
      colors = new Object();
    }
    
    public int compareTo(MediaSourceHolder paramMediaSourceHolder)
    {
      return firstPeriodIndexInChild - firstPeriodIndexInChild;
    }
    
    public void reset(int paramInt1, int paramInt2, int paramInt3)
    {
      childIndex = paramInt1;
      firstWindowIndexInChild = paramInt2;
      firstPeriodIndexInChild = paramInt3;
      hasStartedPreparing = false;
      isPrepared = false;
      isRemoved = false;
      activeMediaPeriods.clear();
    }
  }
  
  private static final class MessageData<T>
  {
    @Nullable
    public final Runnable actionOnCompletion;
    public final T customData;
    public final int index;
    
    public MessageData(int paramInt, Object paramObject, Runnable paramRunnable)
    {
      index = paramInt;
      actionOnCompletion = paramRunnable;
      customData = paramObject;
    }
  }
}
