package com.google.android.exoplayer2.source.configurations.playlist;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.offline.StreamKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class HlsMasterPlaylist
  extends HlsPlaylist
{
  public static final HlsMasterPlaylist EMPTY = new HlsMasterPlaylist("", Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null, Collections.emptyList(), false, Collections.emptyMap());
  public static final int GROUP_INDEX_AUDIO = 1;
  public static final int GROUP_INDEX_SUBTITLE = 2;
  public static final int GROUP_INDEX_VARIANT = 0;
  public final List<com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl> audios;
  public final Format muxedAudioFormat;
  public final List<Format> muxedCaptionFormats;
  public final List<com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl> subtitles;
  public final Map<String, String> variableDefinitions;
  public final List<com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl> variants;
  
  public HlsMasterPlaylist(String paramString, List paramList1, List paramList2, List paramList3, List paramList4, Format paramFormat, List paramList5, boolean paramBoolean, Map paramMap)
  {
    super(paramString, paramList1, paramBoolean);
    variants = Collections.unmodifiableList(paramList2);
    audios = Collections.unmodifiableList(paramList3);
    subtitles = Collections.unmodifiableList(paramList4);
    muxedAudioFormat = paramFormat;
    if (paramList5 != null) {
      paramString = Collections.unmodifiableList(paramList5);
    } else {
      paramString = null;
    }
    muxedCaptionFormats = paramString;
    variableDefinitions = Collections.unmodifiableMap(paramMap);
  }
  
  private static List copyRenditionsList(List paramList1, int paramInt, List paramList2)
  {
    ArrayList localArrayList = new ArrayList(paramList2.size());
    int i = 0;
    while (i < paramList1.size())
    {
      HlsUrl localHlsUrl = (HlsUrl)paramList1.get(i);
      int j = 0;
      while (j < paramList2.size())
      {
        StreamKey localStreamKey = (StreamKey)paramList2.get(j);
        if ((groupIndex == paramInt) && (trackIndex == i))
        {
          localArrayList.add(localHlsUrl);
          break;
        }
        j += 1;
      }
      i += 1;
    }
    return localArrayList;
  }
  
  public static HlsMasterPlaylist createSingleVariantMasterPlaylist(String paramString)
  {
    paramString = Collections.singletonList(HlsUrl.createMediaPlaylistHlsUrl(paramString));
    List localList = Collections.emptyList();
    return new HlsMasterPlaylist(null, Collections.emptyList(), paramString, localList, localList, null, null, false, Collections.emptyMap());
  }
  
  public HlsMasterPlaylist copy(List paramList)
  {
    return new HlsMasterPlaylist(baseUri, tags, copyRenditionsList(variants, 0, paramList), copyRenditionsList(audios, 1, paramList), copyRenditionsList(subtitles, 2, paramList), muxedAudioFormat, muxedCaptionFormats, hasIndependentSegments, variableDefinitions);
  }
  
  public final class HlsUrl
  {
    public final Format format;
    
    public HlsUrl(Format paramFormat)
    {
      format = paramFormat;
    }
    
    public static HlsUrl createMediaPlaylistHlsUrl(String paramString)
    {
      return new HlsUrl(paramString, Format.createContainerFormat("0", null, "application/x-mpegURL", null, null, -1, 0, null));
    }
  }
}
