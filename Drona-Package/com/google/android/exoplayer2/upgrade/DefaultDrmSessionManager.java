package com.google.android.exoplayer2.upgrade;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.drm.DefaultDrmSession.ProvisioningManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.drm.ExoMediaDrm.OnEventListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.EventDispatcher;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@TargetApi(18)
public class DefaultDrmSessionManager<T extends ExoMediaCrypto>
  implements DrmSessionManager<T>, DefaultDrmSession.ProvisioningManager<T>
{
  public static final int INITIAL_DRM_REQUEST_RETRY_COUNT = 3;
  public static final int MODE_DOWNLOAD = 2;
  public static final int MODE_PLAYBACK = 0;
  public static final int MODE_QUERY = 1;
  public static final int MODE_RELEASE = 3;
  private static final String PAGE_KEY = "DefaultDrmSessionMgr";
  public static final String PLAYREADY_CUSTOM_DATA_KEY = "PRCustomData";
  private final MediaDrmCallback callback;
  private final EventDispatcher<com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener> eventDispatcher;
  private final int initialDrmRequestRetryCount;
  private final com.google.android.exoplayer2.drm.ExoMediaDrm<T> mediaDrm;
  volatile com.google.android.exoplayer2.drm.DefaultDrmSessionManager<T>.MediaDrmHandler mediaDrmHandler;
  private int mode;
  private final boolean multiSession;
  private byte[] offlineLicenseKeySetId;
  private final HashMap<String, String> optionalKeyRequestParameters;
  private Looper playbackLooper;
  private final List<com.google.android.exoplayer2.drm.DefaultDrmSession<T>> provisioningSessions;
  private final List<com.google.android.exoplayer2.drm.DefaultDrmSession<T>> sessions;
  private final UUID uuid;
  
  public DefaultDrmSessionManager(UUID paramUUID, ExoMediaDrm paramExoMediaDrm, MediaDrmCallback paramMediaDrmCallback, HashMap paramHashMap)
  {
    this(paramUUID, paramExoMediaDrm, paramMediaDrmCallback, paramHashMap, false, 3);
  }
  
  public DefaultDrmSessionManager(UUID paramUUID, ExoMediaDrm paramExoMediaDrm, MediaDrmCallback paramMediaDrmCallback, HashMap paramHashMap, Handler paramHandler, DefaultDrmSessionEventListener paramDefaultDrmSessionEventListener)
  {
    this(paramUUID, paramExoMediaDrm, paramMediaDrmCallback, paramHashMap);
    if ((paramHandler != null) && (paramDefaultDrmSessionEventListener != null)) {
      addListener(paramHandler, paramDefaultDrmSessionEventListener);
    }
  }
  
  public DefaultDrmSessionManager(UUID paramUUID, ExoMediaDrm paramExoMediaDrm, MediaDrmCallback paramMediaDrmCallback, HashMap paramHashMap, Handler paramHandler, DefaultDrmSessionEventListener paramDefaultDrmSessionEventListener, boolean paramBoolean)
  {
    this(paramUUID, paramExoMediaDrm, paramMediaDrmCallback, paramHashMap, paramBoolean);
    if ((paramHandler != null) && (paramDefaultDrmSessionEventListener != null)) {
      addListener(paramHandler, paramDefaultDrmSessionEventListener);
    }
  }
  
  public DefaultDrmSessionManager(UUID paramUUID, ExoMediaDrm paramExoMediaDrm, MediaDrmCallback paramMediaDrmCallback, HashMap paramHashMap, Handler paramHandler, DefaultDrmSessionEventListener paramDefaultDrmSessionEventListener, boolean paramBoolean, int paramInt)
  {
    this(paramUUID, paramExoMediaDrm, paramMediaDrmCallback, paramHashMap, paramBoolean, paramInt);
    if ((paramHandler != null) && (paramDefaultDrmSessionEventListener != null)) {
      addListener(paramHandler, paramDefaultDrmSessionEventListener);
    }
  }
  
  public DefaultDrmSessionManager(UUID paramUUID, ExoMediaDrm paramExoMediaDrm, MediaDrmCallback paramMediaDrmCallback, HashMap paramHashMap, boolean paramBoolean)
  {
    this(paramUUID, paramExoMediaDrm, paramMediaDrmCallback, paramHashMap, paramBoolean, 3);
  }
  
  public DefaultDrmSessionManager(UUID paramUUID, ExoMediaDrm paramExoMediaDrm, MediaDrmCallback paramMediaDrmCallback, HashMap paramHashMap, boolean paramBoolean, int paramInt)
  {
    Assertions.checkNotNull(paramUUID);
    Assertions.checkNotNull(paramExoMediaDrm);
    Assertions.checkArgument(IpAddress.COMMON_PSSH_UUID.equals(paramUUID) ^ true, "Use C.CLEARKEY_UUID instead");
    uuid = paramUUID;
    mediaDrm = paramExoMediaDrm;
    callback = paramMediaDrmCallback;
    optionalKeyRequestParameters = paramHashMap;
    eventDispatcher = new EventDispatcher();
    multiSession = paramBoolean;
    initialDrmRequestRetryCount = paramInt;
    mode = 0;
    sessions = new ArrayList();
    provisioningSessions = new ArrayList();
    if ((paramBoolean) && (IpAddress.WIDEVINE_UUID.equals(paramUUID)) && (Util.SDK_INT >= 19)) {
      paramExoMediaDrm.setPropertyString("sessionSharing", "enable");
    }
    paramExoMediaDrm.setOnEventListener(new MediaDrmEventListener(null));
  }
  
  private static List getSchemeDatas(DrmInitData paramDrmInitData, UUID paramUUID, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList(schemeDataCount);
    int i = 0;
    while (i < schemeDataCount)
    {
      DrmInitData.SchemeData localSchemeData = paramDrmInitData.getLanguage(i);
      int j;
      if ((!localSchemeData.matches(paramUUID)) && ((!IpAddress.CLEARKEY_UUID.equals(paramUUID)) || (!localSchemeData.matches(IpAddress.COMMON_PSSH_UUID)))) {
        j = 0;
      } else {
        j = 1;
      }
      if ((j != 0) && ((data != null) || (paramBoolean))) {
        localArrayList.add(localSchemeData);
      }
      i += 1;
    }
    return localArrayList;
  }
  
  public static DefaultDrmSessionManager newFrameworkInstance(UUID paramUUID, MediaDrmCallback paramMediaDrmCallback, HashMap paramHashMap)
    throws UnsupportedDrmException
  {
    return new DefaultDrmSessionManager(paramUUID, FrameworkMediaDrm.newInstance(paramUUID), paramMediaDrmCallback, paramHashMap, false, 3);
  }
  
  public static DefaultDrmSessionManager newFrameworkInstance(UUID paramUUID, MediaDrmCallback paramMediaDrmCallback, HashMap paramHashMap, Handler paramHandler, DefaultDrmSessionEventListener paramDefaultDrmSessionEventListener)
    throws UnsupportedDrmException
  {
    paramUUID = newFrameworkInstance(paramUUID, paramMediaDrmCallback, paramHashMap);
    if ((paramHandler != null) && (paramDefaultDrmSessionEventListener != null)) {
      paramUUID.addListener(paramHandler, paramDefaultDrmSessionEventListener);
    }
    return paramUUID;
  }
  
  public static DefaultDrmSessionManager newPlayReadyInstance(MediaDrmCallback paramMediaDrmCallback, String paramString)
    throws UnsupportedDrmException
  {
    if (!TextUtils.isEmpty(paramString))
    {
      HashMap localHashMap = new HashMap();
      localHashMap.put("PRCustomData", paramString);
      paramString = localHashMap;
    }
    else
    {
      paramString = null;
    }
    return newFrameworkInstance(IpAddress.PLAYREADY_UUID, paramMediaDrmCallback, paramString);
  }
  
  public static DefaultDrmSessionManager newPlayReadyInstance(MediaDrmCallback paramMediaDrmCallback, String paramString, Handler paramHandler, DefaultDrmSessionEventListener paramDefaultDrmSessionEventListener)
    throws UnsupportedDrmException
  {
    paramMediaDrmCallback = newPlayReadyInstance(paramMediaDrmCallback, paramString);
    if ((paramHandler != null) && (paramDefaultDrmSessionEventListener != null)) {
      paramMediaDrmCallback.addListener(paramHandler, paramDefaultDrmSessionEventListener);
    }
    return paramMediaDrmCallback;
  }
  
  public static DefaultDrmSessionManager newWidevineInstance(MediaDrmCallback paramMediaDrmCallback, HashMap paramHashMap)
    throws UnsupportedDrmException
  {
    return newFrameworkInstance(IpAddress.WIDEVINE_UUID, paramMediaDrmCallback, paramHashMap);
  }
  
  public static DefaultDrmSessionManager newWidevineInstance(MediaDrmCallback paramMediaDrmCallback, HashMap paramHashMap, Handler paramHandler, DefaultDrmSessionEventListener paramDefaultDrmSessionEventListener)
    throws UnsupportedDrmException
  {
    paramMediaDrmCallback = newWidevineInstance(paramMediaDrmCallback, paramHashMap);
    if ((paramHandler != null) && (paramDefaultDrmSessionEventListener != null)) {
      paramMediaDrmCallback.addListener(paramHandler, paramDefaultDrmSessionEventListener);
    }
    return paramMediaDrmCallback;
  }
  
  public DrmSession acquireSession(Looper paramLooper, DrmInitData paramDrmInitData)
  {
    boolean bool;
    if ((playbackLooper != null) && (playbackLooper != paramLooper)) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.checkState(bool);
    if (sessions.isEmpty())
    {
      playbackLooper = paramLooper;
      if (mediaDrmHandler == null) {
        mediaDrmHandler = new MediaDrmHandler(paramLooper);
      }
    }
    Object localObject1 = offlineLicenseKeySetId;
    Object localObject2 = null;
    if (localObject1 == null)
    {
      localObject1 = getSchemeDatas(paramDrmInitData, uuid, false);
      if (((List)localObject1).isEmpty())
      {
        paramLooper = new MissingSchemeDataException(uuid, null);
        eventDispatcher.dispatch(new -..Lambda.DefaultDrmSessionManager.lsU4S5fVqixyNsHyDBIvI3jEzVc(paramLooper));
        return new ErrorStateDrmSession(new DrmSession.DrmSessionException(paramLooper));
      }
    }
    else
    {
      localObject1 = null;
    }
    if (!multiSession)
    {
      if (sessions.isEmpty()) {
        paramDrmInitData = (DrmInitData)localObject2;
      } else {
        paramDrmInitData = (DefaultDrmSession)sessions.get(0);
      }
    }
    else
    {
      Iterator localIterator = sessions.iterator();
      do
      {
        paramDrmInitData = (DrmInitData)localObject2;
        if (!localIterator.hasNext()) {
          break;
        }
        paramDrmInitData = (DefaultDrmSession)localIterator.next();
      } while (!Util.areEqual(schemeDatas, localObject1));
    }
    localObject2 = paramDrmInitData;
    if (paramDrmInitData == null)
    {
      localObject2 = new DefaultDrmSession(uuid, mediaDrm, this, (List)localObject1, mode, offlineLicenseKeySetId, optionalKeyRequestParameters, callback, paramLooper, eventDispatcher, initialDrmRequestRetryCount);
      sessions.add(localObject2);
    }
    ((DefaultDrmSession)localObject2).acquire();
    return localObject2;
  }
  
  public final void addListener(Handler paramHandler, DefaultDrmSessionEventListener paramDefaultDrmSessionEventListener)
  {
    eventDispatcher.addListener(paramHandler, paramDefaultDrmSessionEventListener);
  }
  
  public boolean canAcquireSession(DrmInitData paramDrmInitData)
  {
    if (offlineLicenseKeySetId != null) {
      return true;
    }
    if (getSchemeDatas(paramDrmInitData, uuid, true).isEmpty()) {
      if (schemeDataCount == 1)
      {
        if (!paramDrmInitData.getLanguage(0).matches(IpAddress.COMMON_PSSH_UUID)) {
          break label157;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("DrmInitData only contains common PSSH SchemeData. Assuming support for: ");
        localStringBuilder.append(uuid);
        Log.w("DefaultDrmSessionMgr", localStringBuilder.toString());
      }
      else
      {
        return false;
      }
    }
    paramDrmInitData = schemeType;
    if (paramDrmInitData != null)
    {
      if ("cenc".equals(paramDrmInitData)) {
        return true;
      }
      if ((!"cbc1".equals(paramDrmInitData)) && (!"cbcs".equals(paramDrmInitData)) && (!"cens".equals(paramDrmInitData))) {
        return true;
      }
      return Util.SDK_INT >= 25;
    }
    return true;
    label157:
    return false;
  }
  
  public final byte[] getPropertyByteArray(String paramString)
  {
    return mediaDrm.getPropertyByteArray(paramString);
  }
  
  public final String getPropertyString(String paramString)
  {
    return mediaDrm.getPropertyString(paramString);
  }
  
  public void onProvisionCompleted()
  {
    Iterator localIterator = provisioningSessions.iterator();
    while (localIterator.hasNext()) {
      ((DefaultDrmSession)localIterator.next()).onProvisionCompleted();
    }
    provisioningSessions.clear();
  }
  
  public void onProvisionError(Exception paramException)
  {
    Iterator localIterator = provisioningSessions.iterator();
    while (localIterator.hasNext()) {
      ((DefaultDrmSession)localIterator.next()).onProvisionError(paramException);
    }
    provisioningSessions.clear();
  }
  
  public void provisionRequired(DefaultDrmSession paramDefaultDrmSession)
  {
    provisioningSessions.add(paramDefaultDrmSession);
    if (provisioningSessions.size() == 1) {
      paramDefaultDrmSession.provision();
    }
  }
  
  public void releaseSession(DrmSession paramDrmSession)
  {
    if ((paramDrmSession instanceof ErrorStateDrmSession)) {
      return;
    }
    paramDrmSession = (DefaultDrmSession)paramDrmSession;
    if (paramDrmSession.release())
    {
      sessions.remove(paramDrmSession);
      if ((provisioningSessions.size() > 1) && (provisioningSessions.get(0) == paramDrmSession)) {
        ((DefaultDrmSession)provisioningSessions.get(1)).provision();
      }
      provisioningSessions.remove(paramDrmSession);
    }
  }
  
  public final void removeListener(DefaultDrmSessionEventListener paramDefaultDrmSessionEventListener)
  {
    eventDispatcher.removeListener(paramDefaultDrmSessionEventListener);
  }
  
  public void setMode(int paramInt, byte[] paramArrayOfByte)
  {
    Assertions.checkState(sessions.isEmpty());
    if ((paramInt == 1) || (paramInt == 3)) {
      Assertions.checkNotNull(paramArrayOfByte);
    }
    mode = paramInt;
    offlineLicenseKeySetId = paramArrayOfByte;
  }
  
  public final void setPropertyByteArray(String paramString, byte[] paramArrayOfByte)
  {
    mediaDrm.setPropertyByteArray(paramString, paramArrayOfByte);
  }
  
  public final void setPropertyString(String paramString1, String paramString2)
  {
    mediaDrm.setPropertyString(paramString1, paramString2);
  }
  
  @Deprecated
  public abstract interface EventListener
    extends DefaultDrmSessionEventListener
  {}
  
  class MediaDrmEventListener
    implements ExoMediaDrm.OnEventListener<T>
  {
    private MediaDrmEventListener() {}
    
    public void onEvent(ExoMediaDrm paramExoMediaDrm, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2)
    {
      if (mode == 0) {
        mediaDrmHandler.obtainMessage(paramInt1, paramArrayOfByte1).sendToTarget();
      }
    }
  }
  
  @SuppressLint({"HandlerLeak"})
  class MediaDrmHandler
    extends Handler
  {
    public MediaDrmHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      byte[] arrayOfByte = (byte[])obj;
      Iterator localIterator = sessions.iterator();
      while (localIterator.hasNext())
      {
        DefaultDrmSession localDefaultDrmSession = (DefaultDrmSession)localIterator.next();
        if (localDefaultDrmSession.hasSessionId(arrayOfByte)) {
          localDefaultDrmSession.onMediaDrmEvent(what);
        }
      }
    }
  }
  
  public final class MissingSchemeDataException
    extends Exception
  {
    private MissingSchemeDataException()
    {
      super();
    }
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface Mode {}
}
