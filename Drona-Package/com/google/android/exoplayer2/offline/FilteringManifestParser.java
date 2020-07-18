package com.google.android.exoplayer2.offline;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public final class FilteringManifestParser<T extends FilterableManifest<T>>
  implements ParsingLoadable.Parser<T>
{
  private final ParsingLoadable.Parser<T> parser;
  private final List<StreamKey> streamKeys;
  
  public FilteringManifestParser(ParsingLoadable.Parser paramParser, List paramList)
  {
    parser = paramParser;
    streamKeys = paramList;
  }
  
  public FilterableManifest parse(Uri paramUri, InputStream paramInputStream)
    throws IOException
  {
    paramInputStream = (FilterableManifest)parser.parse(paramUri, paramInputStream);
    paramUri = paramInputStream;
    if (streamKeys != null)
    {
      if (streamKeys.isEmpty()) {
        return paramInputStream;
      }
      paramUri = (FilterableManifest)paramInputStream.copy(streamKeys);
    }
    return paramUri;
  }
}
