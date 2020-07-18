package com.google.android.exoplayer2.source.configurations.playlist;

import android.net.Uri;
import com.google.android.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.configurations.HlsDataSourceFactory;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import java.io.IOException;

public abstract interface HlsPlaylistTracker
{
  public abstract void addListener(PlaylistEventListener paramPlaylistEventListener);
  
  public abstract long getInitialStartTimeUs();
  
  public abstract HlsMasterPlaylist getMasterPlaylist();
  
  public abstract HlsMediaPlaylist getPlaylistSnapshot(HlsMasterPlaylist.HlsUrl paramHlsUrl, boolean paramBoolean);
  
  public abstract boolean isLive();
  
  public abstract boolean isSnapshotValid(HlsMasterPlaylist.HlsUrl paramHlsUrl);
  
  public abstract void maybeThrowPlaylistRefreshError(HlsMasterPlaylist.HlsUrl paramHlsUrl)
    throws IOException;
  
  public abstract void maybeThrowPrimaryPlaylistRefreshError()
    throws IOException;
  
  public abstract void refreshPlaylist(HlsMasterPlaylist.HlsUrl paramHlsUrl);
  
  public abstract void removeListener(PlaylistEventListener paramPlaylistEventListener);
  
  public abstract void start(Uri paramUri, MediaSourceEventListener.EventDispatcher paramEventDispatcher, PrimaryPlaylistListener paramPrimaryPlaylistListener);
  
  public abstract void stop();
  
  public abstract interface Factory
  {
    public abstract HlsPlaylistTracker createTracker(HlsDataSourceFactory paramHlsDataSourceFactory, LoadErrorHandlingPolicy paramLoadErrorHandlingPolicy, HlsPlaylistParserFactory paramHlsPlaylistParserFactory);
  }
  
  public abstract interface PlaylistEventListener
  {
    public abstract void onPlaylistChanged();
    
    public abstract boolean onPlaylistError(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong);
  }
  
  public final class PlaylistResetException
    extends IOException
  {
    public PlaylistResetException() {}
  }
  
  public final class PlaylistStuckException
    extends IOException
  {
    public PlaylistStuckException() {}
  }
  
  public abstract interface PrimaryPlaylistListener
  {
    public abstract void onPrimaryPlaylistRefreshed(HlsMediaPlaylist paramHlsMediaPlaylist);
  }
}
