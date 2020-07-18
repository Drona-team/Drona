package com.google.android.exoplayer2.upstream.cache;

import android.net.Uri;

final class ContentMetadataInternal
{
  private static final String METADATA_NAME_CONTENT_LENGTH = "exo_len";
  private static final String METADATA_NAME_REDIRECTED_URI = "exo_redir";
  private static final String PREFIX = "exo_";
  
  private ContentMetadataInternal() {}
  
  public static long getContentLength(ContentMetadata paramContentMetadata)
  {
    return paramContentMetadata.get("exo_len", -1L);
  }
  
  public static Uri getRedirectedUri(ContentMetadata paramContentMetadata)
  {
    paramContentMetadata = paramContentMetadata.get("exo_redir", null);
    if (paramContentMetadata == null) {
      return null;
    }
    return Uri.parse(paramContentMetadata);
  }
  
  public static void removeContentLength(ContentMetadataMutations paramContentMetadataMutations)
  {
    paramContentMetadataMutations.remove("exo_len");
  }
  
  public static void removeRedirectedUri(ContentMetadataMutations paramContentMetadataMutations)
  {
    paramContentMetadataMutations.remove("exo_redir");
  }
  
  public static void setContentLength(ContentMetadataMutations paramContentMetadataMutations, long paramLong)
  {
    paramContentMetadataMutations.add("exo_len", paramLong);
  }
  
  public static void setRedirectedUri(ContentMetadataMutations paramContentMetadataMutations, Uri paramUri)
  {
    paramContentMetadataMutations.remove("exo_redir", paramUri.toString());
  }
}
