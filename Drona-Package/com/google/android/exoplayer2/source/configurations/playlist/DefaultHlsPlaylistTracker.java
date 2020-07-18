package com.google.android.exoplayer2.source.configurations.playlist;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.configurations.HlsDataSourceFactory;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.Callback;
import com.google.android.exoplayer2.upstream.Loader.LoadErrorAction;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.UriUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

public final class DefaultHlsPlaylistTracker
  implements HlsPlaylistTracker, Loader.Callback<ParsingLoadable<com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist>>
{
  public static final HlsPlaylistTracker.Factory FACTORY = -..Lambda.lKTLOVxne0MoBOOliKH0gO2KDMM.INSTANCE;
  private static final double PLAYLIST_STUCK_TARGET_DURATION_COEFFICIENT = 3.5D;
  private final HlsDataSourceFactory dataSourceFactory;
  @Nullable
  private MediaSourceEventListener.EventDispatcher eventDispatcher;
  @Nullable
  private Loader initialPlaylistLoader;
  private long initialStartTimeUs;
  private boolean isLive;
  private final List<com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PlaylistEventListener> listeners;
  private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
  @Nullable
  private HlsMasterPlaylist masterPlaylist;
  @Nullable
  private ParsingLoadable.Parser<com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist> mediaPlaylistParser;
  private final IdentityHashMap<com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl, com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistTracker.MediaPlaylistBundle> playlistBundles;
  private final HlsPlaylistParserFactory playlistParserFactory;
  @Nullable
  private Handler playlistRefreshHandler;
  @Nullable
  private HlsMasterPlaylist.HlsUrl primaryHlsUrl;
  @Nullable
  private HlsPlaylistTracker.PrimaryPlaylistListener primaryPlaylistListener;
  @Nullable
  private HlsMediaPlaylist primaryUrlSnapshot;
  
  public DefaultHlsPlaylistTracker(HlsDataSourceFactory paramHlsDataSourceFactory, LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy, HlsPlaylistParserFactory paramHlsPlaylistParserFactory)
  {
    dataSourceFactory = paramHlsDataSourceFactory;
    playlistParserFactory = paramHlsPlaylistParserFactory;
    loadErrorHandlingPolicy = paramLoadErrorHandlingPolicy;
    listeners = new ArrayList();
    playlistBundles = new IdentityHashMap();
    initialStartTimeUs = -9223372036854775807L;
  }
  
  public DefaultHlsPlaylistTracker(HlsDataSourceFactory paramHlsDataSourceFactory, LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy, ParsingLoadable.Parser paramParser)
  {
    this(paramHlsDataSourceFactory, paramLoadErrorHandlingPolicy, createFixedFactory(paramParser));
  }
  
  private void createBundles(List paramList)
  {
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      HlsMasterPlaylist.HlsUrl localHlsUrl = (HlsMasterPlaylist.HlsUrl)paramList.get(i);
      MediaPlaylistBundle localMediaPlaylistBundle = new MediaPlaylistBundle(localHlsUrl);
      playlistBundles.put(localHlsUrl, localMediaPlaylistBundle);
      i += 1;
    }
  }
  
  private static HlsPlaylistParserFactory createFixedFactory(ParsingLoadable.Parser paramParser)
  {
    return new DefaultHlsPlaylistTracker.1(paramParser);
  }
  
  private static HlsMediaPlaylist.Segment getFirstOldOverlappingSegment(HlsMediaPlaylist paramHlsMediaPlaylist1, HlsMediaPlaylist paramHlsMediaPlaylist2)
  {
    int i = (int)(mediaSequence - mediaSequence);
    paramHlsMediaPlaylist1 = segments;
    if (i < paramHlsMediaPlaylist1.size()) {
      return (HlsMediaPlaylist.Segment)paramHlsMediaPlaylist1.get(i);
    }
    return null;
  }
  
  private HlsMediaPlaylist getLatestPlaylistSnapshot(HlsMediaPlaylist paramHlsMediaPlaylist1, HlsMediaPlaylist paramHlsMediaPlaylist2)
  {
    if (!paramHlsMediaPlaylist2.isNewerThan(paramHlsMediaPlaylist1))
    {
      HlsMediaPlaylist localHlsMediaPlaylist = paramHlsMediaPlaylist1;
      if (hasEndTag) {
        localHlsMediaPlaylist = paramHlsMediaPlaylist1.copyWithEndTag();
      }
      return localHlsMediaPlaylist;
    }
    return paramHlsMediaPlaylist2.copyWith(getLoadedPlaylistStartTimeUs(paramHlsMediaPlaylist1, paramHlsMediaPlaylist2), getLoadedPlaylistDiscontinuitySequence(paramHlsMediaPlaylist1, paramHlsMediaPlaylist2));
  }
  
  private int getLoadedPlaylistDiscontinuitySequence(HlsMediaPlaylist paramHlsMediaPlaylist1, HlsMediaPlaylist paramHlsMediaPlaylist2)
  {
    if (hasDiscontinuitySequence) {
      return discontinuitySequence;
    }
    int i;
    if (primaryUrlSnapshot != null) {
      i = primaryUrlSnapshot.discontinuitySequence;
    } else {
      i = 0;
    }
    if (paramHlsMediaPlaylist1 == null) {
      return i;
    }
    HlsMediaPlaylist.Segment localSegment = getFirstOldOverlappingSegment(paramHlsMediaPlaylist1, paramHlsMediaPlaylist2);
    if (localSegment != null) {
      i = discontinuitySequence + relativeDiscontinuitySequence - segments.get(0)).relativeDiscontinuitySequence;
    }
    return i;
  }
  
  private long getLoadedPlaylistStartTimeUs(HlsMediaPlaylist paramHlsMediaPlaylist1, HlsMediaPlaylist paramHlsMediaPlaylist2)
  {
    if (hasProgramDateTime) {
      return startTimeUs;
    }
    long l;
    if (primaryUrlSnapshot != null) {
      l = primaryUrlSnapshot.startTimeUs;
    } else {
      l = 0L;
    }
    if (paramHlsMediaPlaylist1 == null) {
      return l;
    }
    int i = segments.size();
    HlsMediaPlaylist.Segment localSegment = getFirstOldOverlappingSegment(paramHlsMediaPlaylist1, paramHlsMediaPlaylist2);
    if (localSegment != null) {
      return startTimeUs + relativeStartTimeUs;
    }
    if (i == mediaSequence - mediaSequence) {
      l = paramHlsMediaPlaylist1.getEndTimeUs();
    }
    return l;
  }
  
  private boolean maybeSelectNewPrimaryUrl()
  {
    List localList = masterPlaylist.variants;
    int j = localList.size();
    long l = SystemClock.elapsedRealtime();
    int i = 0;
    while (i < j)
    {
      MediaPlaylistBundle localMediaPlaylistBundle = (MediaPlaylistBundle)playlistBundles.get(localList.get(i));
      if (l > blacklistUntilMs)
      {
        primaryHlsUrl = playlistUrl;
        localMediaPlaylistBundle.loadPlaylist();
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void maybeSetPrimaryUrl(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    if ((paramHlsUrl != primaryHlsUrl) && (masterPlaylist.variants.contains(paramHlsUrl)))
    {
      if ((primaryUrlSnapshot != null) && (primaryUrlSnapshot.hasEndTag)) {
        return;
      }
      primaryHlsUrl = paramHlsUrl;
      ((MediaPlaylistBundle)playlistBundles.get(primaryHlsUrl)).loadPlaylist();
    }
  }
  
  private boolean notifyPlaylistError(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong)
  {
    int j = listeners.size();
    int i = 0;
    boolean bool = false;
    while (i < j)
    {
      bool |= ((HlsPlaylistTracker.PlaylistEventListener)listeners.get(i)).onPlaylistError(paramHlsUrl, paramLong) ^ true;
      i += 1;
    }
    return bool;
  }
  
  private void onPlaylistUpdated(HlsMasterPlaylist.HlsUrl paramHlsUrl, HlsMediaPlaylist paramHlsMediaPlaylist)
  {
    if (paramHlsUrl == primaryHlsUrl)
    {
      if (primaryUrlSnapshot == null)
      {
        isLive = (hasEndTag ^ true);
        initialStartTimeUs = startTimeUs;
      }
      primaryUrlSnapshot = paramHlsMediaPlaylist;
      primaryPlaylistListener.onPrimaryPlaylistRefreshed(paramHlsMediaPlaylist);
    }
    int j = listeners.size();
    int i = 0;
    while (i < j)
    {
      ((HlsPlaylistTracker.PlaylistEventListener)listeners.get(i)).onPlaylistChanged();
      i += 1;
    }
  }
  
  public void addListener(HlsPlaylistTracker.PlaylistEventListener paramPlaylistEventListener)
  {
    listeners.add(paramPlaylistEventListener);
  }
  
  public long getInitialStartTimeUs()
  {
    return initialStartTimeUs;
  }
  
  public HlsMasterPlaylist getMasterPlaylist()
  {
    return masterPlaylist;
  }
  
  public HlsMediaPlaylist getPlaylistSnapshot(HlsMasterPlaylist.HlsUrl paramHlsUrl, boolean paramBoolean)
  {
    HlsMediaPlaylist localHlsMediaPlaylist = ((MediaPlaylistBundle)playlistBundles.get(paramHlsUrl)).getPlaylistSnapshot();
    if ((localHlsMediaPlaylist != null) && (paramBoolean)) {
      maybeSetPrimaryUrl(paramHlsUrl);
    }
    return localHlsMediaPlaylist;
  }
  
  public boolean isLive()
  {
    return isLive;
  }
  
  public boolean isSnapshotValid(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    return ((MediaPlaylistBundle)playlistBundles.get(paramHlsUrl)).isSnapshotValid();
  }
  
  public void maybeThrowPlaylistRefreshError(HlsMasterPlaylist.HlsUrl paramHlsUrl)
    throws IOException
  {
    ((MediaPlaylistBundle)playlistBundles.get(paramHlsUrl)).maybeThrowPlaylistRefreshError();
  }
  
  public void maybeThrowPrimaryPlaylistRefreshError()
    throws IOException
  {
    if (initialPlaylistLoader != null) {
      initialPlaylistLoader.maybeThrowError();
    }
    if (primaryHlsUrl != null) {
      maybeThrowPlaylistRefreshError(primaryHlsUrl);
    }
  }
  
  public void onLoadCanceled(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    eventDispatcher.loadCanceled(dataSpec, paramParsingLoadable.getUri(), paramParsingLoadable.getResponseHeaders(), 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
  }
  
  public void onLoadCompleted(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2)
  {
    HlsPlaylist localHlsPlaylist = (HlsPlaylist)paramParsingLoadable.getResult();
    boolean bool = localHlsPlaylist instanceof HlsMediaPlaylist;
    if (bool) {
      localObject = HlsMasterPlaylist.createSingleVariantMasterPlaylist(baseUri);
    } else {
      localObject = (HlsMasterPlaylist)localHlsPlaylist;
    }
    masterPlaylist = ((HlsMasterPlaylist)localObject);
    mediaPlaylistParser = playlistParserFactory.createPlaylistParser((HlsMasterPlaylist)localObject);
    primaryHlsUrl = ((HlsMasterPlaylist.HlsUrl)variants.get(0));
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(variants);
    localArrayList.addAll(audios);
    localArrayList.addAll(subtitles);
    createBundles(localArrayList);
    Object localObject = (MediaPlaylistBundle)playlistBundles.get(primaryHlsUrl);
    if (bool) {
      ((MediaPlaylistBundle)localObject).processLoadedPlaylist((HlsMediaPlaylist)localHlsPlaylist, paramLong2);
    } else {
      ((MediaPlaylistBundle)localObject).loadPlaylist();
    }
    eventDispatcher.loadCompleted(dataSpec, paramParsingLoadable.getUri(), paramParsingLoadable.getResponseHeaders(), 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
  }
  
  public Loader.LoadErrorAction onLoadError(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException, int paramInt)
  {
    long l = loadErrorHandlingPolicy.getRetryDelayMsFor(type, paramLong2, paramIOException, paramInt);
    boolean bool;
    if (l == -9223372036854775807L) {
      bool = true;
    } else {
      bool = false;
    }
    eventDispatcher.loadError(dataSpec, paramParsingLoadable.getUri(), paramParsingLoadable.getResponseHeaders(), 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, bool);
    if (bool) {
      return Loader.DONT_RETRY_FATAL;
    }
    return Loader.createRetryAction(false, l);
  }
  
  public void refreshPlaylist(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    ((MediaPlaylistBundle)playlistBundles.get(paramHlsUrl)).loadPlaylist();
  }
  
  public void removeListener(HlsPlaylistTracker.PlaylistEventListener paramPlaylistEventListener)
  {
    listeners.remove(paramPlaylistEventListener);
  }
  
  public void start(Uri paramUri, MediaSourceEventListener.EventDispatcher paramEventDispatcher, HlsPlaylistTracker.PrimaryPlaylistListener paramPrimaryPlaylistListener)
  {
    playlistRefreshHandler = new Handler();
    eventDispatcher = paramEventDispatcher;
    primaryPlaylistListener = paramPrimaryPlaylistListener;
    paramUri = new ParsingLoadable(dataSourceFactory.createDataSource(4), paramUri, 4, playlistParserFactory.createPlaylistParser());
    boolean bool;
    if (initialPlaylistLoader == null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    initialPlaylistLoader = new Loader("DefaultHlsPlaylistTracker:MasterPlaylist");
    long l = initialPlaylistLoader.startLoading(paramUri, this, loadErrorHandlingPolicy.getMinimumLoadableRetryCount(type));
    paramEventDispatcher.loadStarted(dataSpec, type, l);
  }
  
  public void stop()
  {
    primaryHlsUrl = null;
    primaryUrlSnapshot = null;
    masterPlaylist = null;
    initialStartTimeUs = -9223372036854775807L;
    initialPlaylistLoader.release();
    initialPlaylistLoader = null;
    Iterator localIterator = playlistBundles.values().iterator();
    while (localIterator.hasNext()) {
      ((MediaPlaylistBundle)localIterator.next()).release();
    }
    playlistRefreshHandler.removeCallbacksAndMessages(null);
    playlistRefreshHandler = null;
    playlistBundles.clear();
  }
  
  final class MediaPlaylistBundle
    implements Loader.Callback<ParsingLoadable<com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist>>, Runnable
  {
    private long blacklistUntilMs;
    private long earliestNextLoadTimeMs;
    private long lastSnapshotChangeMs;
    private long lastSnapshotLoadMs;
    private boolean loadPending;
    private final ParsingLoadable<com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist> mediaPlaylistLoadable;
    private final Loader mediaPlaylistLoader;
    private IOException playlistError;
    private HlsMediaPlaylist playlistSnapshot;
    private final HlsMasterPlaylist.HlsUrl playlistUrl;
    
    public MediaPlaylistBundle(HlsMasterPlaylist.HlsUrl paramHlsUrl)
    {
      playlistUrl = paramHlsUrl;
      mediaPlaylistLoader = new Loader("DefaultHlsPlaylistTracker:MediaPlaylist");
      mediaPlaylistLoadable = new ParsingLoadable(dataSourceFactory.createDataSource(4), UriUtil.resolveToUri(masterPlaylist.baseUri, url), 4, mediaPlaylistParser);
    }
    
    private boolean blacklistPlaylist(long paramLong)
    {
      blacklistUntilMs = (SystemClock.elapsedRealtime() + paramLong);
      return (primaryHlsUrl == playlistUrl) && (!DefaultHlsPlaylistTracker.this.maybeSelectNewPrimaryUrl());
    }
    
    private void loadPlaylistImmediately()
    {
      long l = mediaPlaylistLoader.startLoading(mediaPlaylistLoadable, this, loadErrorHandlingPolicy.getMinimumLoadableRetryCount(mediaPlaylistLoadable.type));
      eventDispatcher.loadStarted(mediaPlaylistLoadable.dataSpec, mediaPlaylistLoadable.type, l);
    }
    
    private void processLoadedPlaylist(HlsMediaPlaylist paramHlsMediaPlaylist, long paramLong)
    {
      HlsMediaPlaylist localHlsMediaPlaylist = playlistSnapshot;
      long l = SystemClock.elapsedRealtime();
      lastSnapshotLoadMs = l;
      playlistSnapshot = DefaultHlsPlaylistTracker.this.getLatestPlaylistSnapshot(localHlsMediaPlaylist, paramHlsMediaPlaylist);
      if (playlistSnapshot != localHlsMediaPlaylist)
      {
        playlistError = null;
        lastSnapshotChangeMs = l;
        DefaultHlsPlaylistTracker.this.onPlaylistUpdated(playlistUrl, playlistSnapshot);
      }
      else if (!playlistSnapshot.hasEndTag)
      {
        if (mediaSequence + segments.size() < playlistSnapshot.mediaSequence)
        {
          playlistError = new HlsPlaylistTracker.PlaylistResetException(playlistUrl.url);
          DefaultHlsPlaylistTracker.this.notifyPlaylistError(playlistUrl, -9223372036854775807L);
        }
        else if (l - lastSnapshotChangeMs > IpAddress.usToMs(playlistSnapshot.targetDurationUs) * 3.5D)
        {
          playlistError = new HlsPlaylistTracker.PlaylistStuckException(playlistUrl.url);
          paramLong = loadErrorHandlingPolicy.getBlacklistDurationMsFor(4, paramLong, playlistError, 1);
          DefaultHlsPlaylistTracker.this.notifyPlaylistError(playlistUrl, paramLong);
          if (paramLong != -9223372036854775807L) {
            blacklistPlaylist(paramLong);
          }
        }
      }
      if (playlistSnapshot != localHlsMediaPlaylist) {
        paramLong = playlistSnapshot.targetDurationUs;
      } else {
        paramLong = playlistSnapshot.targetDurationUs / 2L;
      }
      earliestNextLoadTimeMs = (l + IpAddress.usToMs(paramLong));
      if ((playlistUrl == primaryHlsUrl) && (!playlistSnapshot.hasEndTag)) {
        loadPlaylist();
      }
    }
    
    public HlsMediaPlaylist getPlaylistSnapshot()
    {
      return playlistSnapshot;
    }
    
    public boolean isSnapshotValid()
    {
      if (playlistSnapshot == null) {
        return false;
      }
      long l1 = SystemClock.elapsedRealtime();
      long l2 = Math.max(30000L, IpAddress.usToMs(playlistSnapshot.durationUs));
      return (playlistSnapshot.hasEndTag) || (playlistSnapshot.playlistType == 2) || (playlistSnapshot.playlistType == 1) || (lastSnapshotLoadMs + l2 > l1);
    }
    
    public void loadPlaylist()
    {
      blacklistUntilMs = 0L;
      if (!loadPending)
      {
        if (mediaPlaylistLoader.isLoading()) {
          return;
        }
        long l = SystemClock.elapsedRealtime();
        if (l < earliestNextLoadTimeMs)
        {
          loadPending = true;
          playlistRefreshHandler.postDelayed(this, earliestNextLoadTimeMs - l);
          return;
        }
        loadPlaylistImmediately();
      }
    }
    
    public void maybeThrowPlaylistRefreshError()
      throws IOException
    {
      mediaPlaylistLoader.maybeThrowError();
      if (playlistError == null) {
        return;
      }
      throw playlistError;
    }
    
    public void onLoadCanceled(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
    {
      eventDispatcher.loadCanceled(dataSpec, paramParsingLoadable.getUri(), paramParsingLoadable.getResponseHeaders(), 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
    }
    
    public void onLoadCompleted(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2)
    {
      HlsPlaylist localHlsPlaylist = (HlsPlaylist)paramParsingLoadable.getResult();
      if ((localHlsPlaylist instanceof HlsMediaPlaylist))
      {
        processLoadedPlaylist((HlsMediaPlaylist)localHlsPlaylist, paramLong2);
        eventDispatcher.loadCompleted(dataSpec, paramParsingLoadable.getUri(), paramParsingLoadable.getResponseHeaders(), 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
        return;
      }
      playlistError = new ParserException("Loaded playlist has unexpected type.");
    }
    
    public Loader.LoadErrorAction onLoadError(ParsingLoadable paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException, int paramInt)
    {
      long l = loadErrorHandlingPolicy.getBlacklistDurationMsFor(type, paramLong2, paramIOException, paramInt);
      int i;
      if (l != -9223372036854775807L) {
        i = 1;
      } else {
        i = 0;
      }
      boolean bool1;
      if ((!DefaultHlsPlaylistTracker.this.notifyPlaylistError(playlistUrl, l)) && (i != 0)) {
        bool1 = false;
      } else {
        bool1 = true;
      }
      boolean bool2 = bool1;
      if (i != 0) {
        bool2 = bool1 | blacklistPlaylist(l);
      }
      Loader.LoadErrorAction localLoadErrorAction;
      if (bool2)
      {
        l = loadErrorHandlingPolicy.getRetryDelayMsFor(type, paramLong2, paramIOException, paramInt);
        if (l != -9223372036854775807L) {
          localLoadErrorAction = Loader.createRetryAction(false, l);
        } else {
          localLoadErrorAction = Loader.DONT_RETRY_FATAL;
        }
      }
      else
      {
        localLoadErrorAction = Loader.DONT_RETRY;
      }
      eventDispatcher.loadError(dataSpec, paramParsingLoadable.getUri(), paramParsingLoadable.getResponseHeaders(), 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, localLoadErrorAction.isRetry() ^ true);
      return localLoadErrorAction;
    }
    
    public void release()
    {
      mediaPlaylistLoader.release();
    }
    
    public void run()
    {
      loadPending = false;
      loadPlaylistImmediately();
    }
  }
}
