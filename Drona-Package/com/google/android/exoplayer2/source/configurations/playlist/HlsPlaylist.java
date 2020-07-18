package com.google.android.exoplayer2.source.configurations.playlist;

import com.google.android.exoplayer2.offline.FilterableManifest;
import java.util.Collections;
import java.util.List;

public abstract class HlsPlaylist
  implements FilterableManifest<com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist>
{
  public final String baseUri;
  public final boolean hasIndependentSegments;
  public final List<String> tags;
  
  protected HlsPlaylist(String paramString, List paramList, boolean paramBoolean)
  {
    baseUri = paramString;
    tags = Collections.unmodifiableList(paramList);
    hasIndependentSegments = paramBoolean;
  }
}
