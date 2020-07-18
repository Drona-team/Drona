package androidx.media;

import android.content.Context;
import android.media.browse.MediaBrowser.MediaItem;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.service.media.MediaBrowserService;
import android.service.media.MediaBrowserService.Result;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import androidx.annotation.RequiresApi;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiresApi(26)
class MediaBrowserServiceCompatApi26
{
  private static final String PAGE_KEY = "MBSCompatApi26";
  static Field sResultFlags;
  
  static
  {
    try
    {
      Field localField = MediaBrowserService.Result.class.getDeclaredField("mFlags");
      sResultFlags = localField;
      localField = sResultFlags;
      localField.setAccessible(true);
      return;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      Log.w("MBSCompatApi26", localNoSuchFieldException);
    }
  }
  
  private MediaBrowserServiceCompatApi26() {}
  
  public static Object createService(Context paramContext, ServiceCompatProxy paramServiceCompatProxy)
  {
    return new MediaBrowserServiceAdaptor(paramContext, paramServiceCompatProxy);
  }
  
  public static Bundle getBrowserRootHints(Object paramObject)
  {
    return ((MediaBrowserService)paramObject).getBrowserRootHints();
  }
  
  public static void notifyChildrenChanged(Object paramObject, String paramString, Bundle paramBundle)
  {
    ((MediaBrowserService)paramObject).notifyChildrenChanged(paramString, paramBundle);
  }
  
  static class MediaBrowserServiceAdaptor
    extends MediaBrowserServiceCompatApi23.MediaBrowserServiceAdaptor
  {
    MediaBrowserServiceAdaptor(Context paramContext, MediaBrowserServiceCompatApi26.ServiceCompatProxy paramServiceCompatProxy)
    {
      super(paramServiceCompatProxy);
    }
    
    public void onLoadChildren(String paramString, MediaBrowserService.Result paramResult, Bundle paramBundle)
    {
      MediaSessionCompat.ensureClassLoader(paramBundle);
      ((MediaBrowserServiceCompatApi26.ServiceCompatProxy)mServiceProxy).onLoadChildren(paramString, new MediaBrowserServiceCompatApi26.ResultWrapper(paramResult), paramBundle);
    }
  }
  
  static class ResultWrapper
  {
    MediaBrowserService.Result mResultObj;
    
    ResultWrapper(MediaBrowserService.Result paramResult)
    {
      mResultObj = paramResult;
    }
    
    public void detach()
    {
      mResultObj.detach();
    }
    
    List parcelListToItemList(List paramList)
    {
      if (paramList == null) {
        return null;
      }
      ArrayList localArrayList = new ArrayList();
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        Parcel localParcel = (Parcel)paramList.next();
        localParcel.setDataPosition(0);
        localArrayList.add(MediaBrowser.MediaItem.CREATOR.createFromParcel(localParcel));
        localParcel.recycle();
      }
      return localArrayList;
    }
    
    public void sendResult(List paramList, int paramInt)
    {
      Field localField = MediaBrowserServiceCompatApi26.sResultFlags;
      MediaBrowserService.Result localResult = mResultObj;
      try
      {
        localField.setInt(localResult, paramInt);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        Log.w("MBSCompatApi26", localIllegalAccessException);
      }
      mResultObj.sendResult(parcelListToItemList(paramList));
    }
  }
  
  public static abstract interface ServiceCompatProxy
    extends MediaBrowserServiceCompatApi23.ServiceCompatProxy
  {
    public abstract void onLoadChildren(String paramString, MediaBrowserServiceCompatApi26.ResultWrapper paramResultWrapper, Bundle paramBundle);
  }
}
