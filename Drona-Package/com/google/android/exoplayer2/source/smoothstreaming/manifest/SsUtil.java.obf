package com.google.android.exoplayer2.source.smoothstreaming.manifest;

import android.net.Uri;
import com.google.android.exoplayer2.util.Util;

public final class SsUtil
{
  private SsUtil() {}
  
  public static Uri fixManifestUri(Uri paramUri)
  {
    if (Util.toLowerInvariant(paramUri.getLastPathSegment()).matches("manifest(\\(.+\\))?")) {
      return paramUri;
    }
    return Uri.withAppendedPath(paramUri, "Manifest");
  }
}
