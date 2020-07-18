package android.support.v4.media;

import android.media.browse.MediaBrowser;
import android.media.browse.MediaBrowser.SubscriptionCallback;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import androidx.annotation.RequiresApi;
import java.util.List;

@RequiresApi(26)
class MediaBrowserCompatApi26
{
  private MediaBrowserCompatApi26() {}
  
  static Object createSubscriptionCallback(SubscriptionCallback paramSubscriptionCallback)
  {
    return new SubscriptionCallbackProxy(paramSubscriptionCallback);
  }
  
  public static void subscribe(Object paramObject1, String paramString, Bundle paramBundle, Object paramObject2)
  {
    ((MediaBrowser)paramObject1).subscribe(paramString, paramBundle, (MediaBrowser.SubscriptionCallback)paramObject2);
  }
  
  public static void unsubscribe(Object paramObject1, String paramString, Object paramObject2)
  {
    ((MediaBrowser)paramObject1).unsubscribe(paramString, (MediaBrowser.SubscriptionCallback)paramObject2);
  }
  
  static abstract interface SubscriptionCallback
    extends MediaBrowserCompatApi21.SubscriptionCallback
  {
    public abstract void onChildrenLoaded(String paramString, List paramList, Bundle paramBundle);
    
    public abstract void onError(String paramString, Bundle paramBundle);
  }
  
  static class SubscriptionCallbackProxy<T extends MediaBrowserCompatApi26.SubscriptionCallback>
    extends MediaBrowserCompatApi21.SubscriptionCallbackProxy<T>
  {
    SubscriptionCallbackProxy(MediaBrowserCompatApi26.SubscriptionCallback paramSubscriptionCallback)
    {
      super();
    }
    
    public void onChildrenLoaded(String paramString, List paramList, Bundle paramBundle)
    {
      MediaSessionCompat.ensureClassLoader(paramBundle);
      ((MediaBrowserCompatApi26.SubscriptionCallback)mSubscriptionCallback).onChildrenLoaded(paramString, paramList, paramBundle);
    }
    
    public void onError(String paramString, Bundle paramBundle)
    {
      MediaSessionCompat.ensureClassLoader(paramBundle);
      ((MediaBrowserCompatApi26.SubscriptionCallback)mSubscriptionCallback).onError(paramString, paramBundle);
    }
  }
}
