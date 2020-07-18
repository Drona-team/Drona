package com.google.android.exoplayer2.source.configurations.playlist;

import com.google.android.exoplayer2.offline.FilteringManifestParser;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import java.util.Collections;
import java.util.List;

public final class DefaultHlsPlaylistParserFactory
  implements HlsPlaylistParserFactory
{
  private final List<StreamKey> streamKeys;
  
  public DefaultHlsPlaylistParserFactory()
  {
    this(Collections.emptyList());
  }
  
  public DefaultHlsPlaylistParserFactory(List paramList)
  {
    streamKeys = paramList;
  }
  
  public ParsingLoadable.Parser createPlaylistParser()
  {
    return new FilteringManifestParser(new HlsPlaylistParser(), streamKeys);
  }
  
  public ParsingLoadable.Parser createPlaylistParser(HlsMasterPlaylist paramHlsMasterPlaylist)
  {
    return new FilteringManifestParser(new HlsPlaylistParser(paramHlsMasterPlaylist), streamKeys);
  }
}
