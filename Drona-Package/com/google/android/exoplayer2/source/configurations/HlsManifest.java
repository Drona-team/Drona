package com.google.android.exoplayer2.source.configurations;

import com.google.android.exoplayer2.source.configurations.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMediaPlaylist;

public final class HlsManifest
{
  public final HlsMasterPlaylist masterPlaylist;
  public final HlsMediaPlaylist mediaPlaylist;
  
  HlsManifest(HlsMasterPlaylist paramHlsMasterPlaylist, HlsMediaPlaylist paramHlsMediaPlaylist)
  {
    masterPlaylist = paramHlsMasterPlaylist;
    mediaPlaylist = paramHlsMediaPlaylist;
  }
}
