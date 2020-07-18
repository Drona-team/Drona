package android.support.v4.media;

import android.media.MediaDescription;
import android.media.MediaDescription.Builder;
import android.net.Uri;
import androidx.annotation.RequiresApi;

@RequiresApi(23)
class MediaDescriptionCompatApi23
{
  private MediaDescriptionCompatApi23() {}
  
  public static Uri getMediaUri(Object paramObject)
  {
    return ((MediaDescription)paramObject).getMediaUri();
  }
  
  static class Builder
  {
    private Builder() {}
    
    public static void setMediaUri(Object paramObject, Uri paramUri)
    {
      ((MediaDescription.Builder)paramObject).setMediaUri(paramUri);
    }
  }
}
