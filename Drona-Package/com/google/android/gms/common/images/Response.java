package com.google.android.gms.common.images;

import android.net.Uri;
import com.google.android.gms.common.internal.Objects;

final class Response
{
  public final Uri url;
  
  public Response(Uri paramUri)
  {
    url = paramUri;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Response)) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    return Objects.equal(url, url);
  }
  
  public final int hashCode()
  {
    return Objects.hashCode(new Object[] { url });
  }
}
