package com.google.android.exoplayer2.source.configurations.playlist;

import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;

public abstract interface HlsPlaylistParserFactory
{
  public abstract ParsingLoadable.Parser createPlaylistParser();
  
  public abstract ParsingLoadable.Parser createPlaylistParser(HlsMasterPlaylist paramHlsMasterPlaylist);
}
