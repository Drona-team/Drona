package com.google.android.exoplayer2.upgrade;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.NotProvisionedException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener;
import com.google.android.exoplayer2.drm.DrmInitData.SchemeData;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.util.EventDispatcher;
import com.google.android.exoplayer2.util.EventDispatcher.Event;
import com.google.android.exoplayer2.util.Log;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@TargetApi(18)
class DefaultDrmSession<T extends com.google.android.exoplayer2.drm.ExoMediaCrypto>
  implements DrmSession<T>
{
  private static final int MAX_LICENSE_DURATION_TO_RENEW = 60;
  private static final int MSG_KEYS = 1;
  private static final int MSG_PROVISION = 0;
  private static final String PAGE_KEY = "DefaultDrmSession";
  final MediaDrmCallback callback;
  private ExoMediaDrm.KeyRequest currentKeyRequest;
  private ExoMediaDrm.ProvisionRequest currentProvisionRequest;
  private final EventDispatcher<DefaultDrmSessionEventListener> eventDispatcher;
  private final int initialDrmRequestRetryCount;
  private DrmSession.DrmSessionException lastException;
  private T mediaCrypto;
  private final com.google.android.exoplayer2.drm.ExoMediaDrm<T> mediaDrm;
  private final int mode;
  @Nullable
  private byte[] offlineLicenseKeySetId;
  private int openCount;
  private final HashMap<String, String> optionalKeyRequestParameters;
  private com.google.android.exoplayer2.drm.DefaultDrmSession<T>.PostRequestHandler postRequestHandler;
  final com.google.android.exoplayer2.drm.DefaultDrmSession<T>.PostResponseHandler postResponseHandler;
  private final com.google.android.exoplayer2.drm.DefaultDrmSession.ProvisioningManager<T> provisioningManager;
  private HandlerThread requestHandlerThread;
  @Nullable
  public final List<DrmInitData.SchemeData> schemeDatas;
  private byte[] sessionId;
  private int state;
  final UUID uuid;
  
  public DefaultDrmSession(UUID paramUUID, ExoMediaDrm paramExoMediaDrm, ProvisioningManager paramProvisioningManager, List paramList, int paramInt1, byte[] paramArrayOfByte, HashMap paramHashMap, MediaDrmCallback paramMediaDrmCallback, Looper paramLooper, EventDispatcher paramEventDispatcher, int paramInt2)
  {
    uuid = paramUUID;
    provisioningManager = paramProvisioningManager;
    mediaDrm = paramExoMediaDrm;
    mode = paramInt1;
    offlineLicenseKeySetId = paramArrayOfByte;
    if (paramArrayOfByte == null) {
      paramUUID = Collections.unmodifiableList(paramList);
    } else {
      paramUUID = null;
    }
    schemeDatas = paramUUID;
    optionalKeyRequestParameters = paramHashMap;
    callback = paramMediaDrmCallback;
    initialDrmRequestRetryCount = paramInt2;
    eventDispatcher = paramEventDispatcher;
    state = 2;
    postResponseHandler = new PostResponseHandler(paramLooper);
    requestHandlerThread = new HandlerThread("DrmRequestHandler");
    requestHandlerThread.start();
    postRequestHandler = new PostRequestHandler(requestHandlerThread.getLooper());
  }
  
  private void doLicense(boolean paramBoolean)
  {
    switch (mode)
    {
    default: 
      
    case 3: 
      if (restoreKeys())
      {
        postKeyRequest(3, paramBoolean);
        return;
      }
      break;
    case 2: 
      if (offlineLicenseKeySetId == null)
      {
        postKeyRequest(2, paramBoolean);
        return;
      }
      if (restoreKeys())
      {
        postKeyRequest(2, paramBoolean);
        return;
      }
      break;
    case 0: 
    case 1: 
      if (offlineLicenseKeySetId == null)
      {
        postKeyRequest(1, paramBoolean);
        return;
      }
      if ((state == 4) || (restoreKeys()))
      {
        long l = getLicenseDurationRemainingSec();
        if ((mode == 0) && (l <= 60L))
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Offline license has expired or will expire soon. Remaining seconds: ");
          localStringBuilder.append(l);
          Log.d("DefaultDrmSession", localStringBuilder.toString());
          postKeyRequest(2, paramBoolean);
          return;
        }
        if (l <= 0L)
        {
          onError(new KeysExpiredException());
          return;
        }
        state = 4;
        eventDispatcher.dispatch(-..Lambda.tzysvANfjWo6mXRxYD2fQMdks_4.INSTANCE);
      }
      break;
    }
  }
  
  private long getLicenseDurationRemainingSec()
  {
    if (!IpAddress.WIDEVINE_UUID.equals(uuid)) {
      return Long.MAX_VALUE;
    }
    Pair localPair = WidevineUtil.getLicenseDurationRemainingSec(this);
    return Math.min(((Long)first).longValue(), ((Long)second).longValue());
  }
  
  private boolean isOpen()
  {
    return (state == 3) || (state == 4);
  }
  
  private void onError(Exception paramException)
  {
    lastException = new DrmSession.DrmSessionException(paramException);
    eventDispatcher.dispatch(new -..Lambda.DefaultDrmSession.-nKOJC1w2998gRg4Cg4l2mjlp30(paramException));
    if (state != 4) {
      state = 1;
    }
  }
  
  private void onKeyResponse(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == currentKeyRequest)
    {
      if (!isOpen()) {
        return;
      }
      currentKeyRequest = null;
      if ((paramObject2 instanceof Exception))
      {
        onKeysError((Exception)paramObject2);
        return;
      }
      paramObject1 = (byte[])paramObject2;
      if (mode == 3)
      {
        paramObject2 = mediaDrm;
        arrayOfByte = offlineLicenseKeySetId;
      }
      try
      {
        paramObject2.provideKeyResponse(arrayOfByte, paramObject1);
        paramObject1 = eventDispatcher;
        paramObject2 = -..Lambda.tzysvANfjWo6mXRxYD2fQMdks_4.INSTANCE;
        paramObject1.dispatch(paramObject2);
        return;
      }
      catch (Exception paramObject1)
      {
        onKeysError(paramObject1);
      }
      paramObject2 = mediaDrm;
      byte[] arrayOfByte = sessionId;
      paramObject1 = paramObject2.provideKeyResponse(arrayOfByte, paramObject1);
      if (((mode == 2) || ((mode == 0) && (offlineLicenseKeySetId != null))) && (paramObject1 != null) && (paramObject1.length != 0)) {
        offlineLicenseKeySetId = paramObject1;
      }
      state = 4;
      paramObject1 = eventDispatcher;
      paramObject2 = -..Lambda.wyKVEWJALn1OyjwryLo2GUxlQ2M.INSTANCE;
      paramObject1.dispatch(paramObject2);
      return;
    }
  }
  
  private void onKeysError(Exception paramException)
  {
    if ((paramException instanceof NotProvisionedException))
    {
      provisioningManager.provisionRequired(this);
      return;
    }
    onError(paramException);
  }
  
  private void onKeysExpired()
  {
    if (state == 4)
    {
      state = 3;
      onError(new KeysExpiredException());
    }
  }
  
  private void onProvisionResponse(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == currentProvisionRequest)
    {
      if ((state != 2) && (!isOpen())) {
        return;
      }
      currentProvisionRequest = null;
      if ((paramObject2 instanceof Exception))
      {
        provisioningManager.onProvisionError((Exception)paramObject2);
        return;
      }
      paramObject1 = mediaDrm;
      paramObject2 = (byte[])paramObject2;
      try
      {
        paramObject1.provideProvisionResponse(paramObject2);
        provisioningManager.onProvisionCompleted();
        return;
      }
      catch (Exception paramObject1)
      {
        provisioningManager.onProvisionError(paramObject1);
      }
    }
  }
  
  private boolean openInternal(boolean paramBoolean)
  {
    if (isOpen()) {
      return true;
    }
    Object localObject1 = mediaDrm;
    try
    {
      localObject1 = ((ExoMediaDrm)localObject1).openSession();
      sessionId = ((byte[])localObject1);
      localObject1 = eventDispatcher;
      Object localObject2 = -..Lambda.jFcVU4qXZB2nhSZWHXCB9S7MtRI.INSTANCE;
      ((EventDispatcher)localObject1).dispatch((EventDispatcher.Event)localObject2);
      localObject1 = mediaDrm;
      localObject2 = sessionId;
      localObject1 = ((ExoMediaDrm)localObject1).createMediaCrypto((byte[])localObject2);
      mediaCrypto = ((ExoMediaCrypto)localObject1);
      state = 3;
      return true;
    }
    catch (Exception localException)
    {
      onError(localException);
    }
    catch (NotProvisionedException localNotProvisionedException)
    {
      if (paramBoolean) {
        provisioningManager.provisionRequired(this);
      } else {
        onError(localNotProvisionedException);
      }
    }
    return false;
  }
  
  private void postKeyRequest(int paramInt, boolean paramBoolean)
  {
    Object localObject1;
    if (paramInt == 3) {
      localObject1 = offlineLicenseKeySetId;
    } else {
      localObject1 = sessionId;
    }
    Object localObject2 = mediaDrm;
    List localList = schemeDatas;
    HashMap localHashMap = optionalKeyRequestParameters;
    try
    {
      localObject1 = ((ExoMediaDrm)localObject2).getKeyRequest((byte[])localObject1, localList, paramInt, localHashMap);
      currentKeyRequest = ((ExoMediaDrm.KeyRequest)localObject1);
      localObject1 = postRequestHandler;
      localObject2 = currentKeyRequest;
      ((PostRequestHandler)localObject1).post(1, localObject2, paramBoolean);
      return;
    }
    catch (Exception localException)
    {
      onKeysError(localException);
    }
  }
  
  private boolean restoreKeys()
  {
    ExoMediaDrm localExoMediaDrm = mediaDrm;
    byte[] arrayOfByte1 = sessionId;
    byte[] arrayOfByte2 = offlineLicenseKeySetId;
    try
    {
      localExoMediaDrm.restoreKeys(arrayOfByte1, arrayOfByte2);
      return true;
    }
    catch (Exception localException)
    {
      Log.e("DefaultDrmSession", "Error trying to restore Widevine keys.", localException);
      onError(localException);
    }
    return false;
  }
  
  public void acquire()
  {
    int i = openCount + 1;
    openCount = i;
    if (i == 1)
    {
      if (state == 1) {
        return;
      }
      if (openInternal(true)) {
        doLicense(true);
      }
    }
  }
  
  public final DrmSession.DrmSessionException getError()
  {
    if (state == 1) {
      return lastException;
    }
    return null;
  }
  
  public final ExoMediaCrypto getMediaCrypto()
  {
    return mediaCrypto;
  }
  
  public byte[] getOfflineLicenseKeySetId()
  {
    return offlineLicenseKeySetId;
  }
  
  public final int getState()
  {
    return state;
  }
  
  public boolean hasSessionId(byte[] paramArrayOfByte)
  {
    return Arrays.equals(sessionId, paramArrayOfByte);
  }
  
  public void onMediaDrmEvent(int paramInt)
  {
    if (!isOpen()) {
      return;
    }
    switch (paramInt)
    {
    default: 
      return;
    case 3: 
      onKeysExpired();
      return;
    case 2: 
      doLicense(false);
      return;
    }
    state = 3;
    provisioningManager.provisionRequired(this);
  }
  
  public void onProvisionCompleted()
  {
    if (openInternal(false)) {
      doLicense(true);
    }
  }
  
  public void onProvisionError(Exception paramException)
  {
    onError(paramException);
  }
  
  public void provision()
  {
    currentProvisionRequest = mediaDrm.getProvisionRequest();
    postRequestHandler.post(0, currentProvisionRequest, true);
  }
  
  public Map queryKeyStatus()
  {
    if (sessionId == null) {
      return null;
    }
    return mediaDrm.queryKeyStatus(sessionId);
  }
  
  public boolean release()
  {
    int i = openCount - 1;
    openCount = i;
    if (i == 0)
    {
      state = 0;
      postResponseHandler.removeCallbacksAndMessages(null);
      postRequestHandler.removeCallbacksAndMessages(null);
      postRequestHandler = null;
      requestHandlerThread.quit();
      requestHandlerThread = null;
      mediaCrypto = null;
      lastException = null;
      currentKeyRequest = null;
      currentProvisionRequest = null;
      if (sessionId != null)
      {
        mediaDrm.closeSession(sessionId);
        sessionId = null;
        eventDispatcher.dispatch(-..Lambda.1U2yJBSMBm8ESUSz9LUzNXtoVus.INSTANCE);
        return true;
      }
    }
    else
    {
      return false;
    }
    return true;
  }
  
  @SuppressLint({"HandlerLeak"})
  class PostRequestHandler
    extends Handler
  {
    public PostRequestHandler(Looper paramLooper)
    {
      super();
    }
    
    private long getRetryDelayMillis(int paramInt)
    {
      return Math.min((paramInt - 1) * 1000, 5000);
    }
    
    private boolean maybeRetryRequest(Message paramMessage)
    {
      if (arg1 == 1) {
        i = 1;
      } else {
        i = 0;
      }
      if (i == 0) {
        return false;
      }
      int i = arg2 + 1;
      if (i > initialDrmRequestRetryCount) {
        return false;
      }
      paramMessage = Message.obtain(paramMessage);
      arg2 = i;
      sendMessageDelayed(paramMessage, getRetryDelayMillis(i));
      return true;
    }
    
    public void handleMessage(Message paramMessage)
    {
      Object localObject2 = obj;
      switch (what)
      {
      default: 
        break;
      case 1: 
        localObject1 = callback;
        localUUID = uuid;
        localObject3 = (ExoMediaDrm.KeyRequest)localObject2;
      }
      try
      {
        localObject1 = ((MediaDrmCallback)localObject1).executeKeyRequest(localUUID, (ExoMediaDrm.KeyRequest)localObject3);
      }
      catch (Exception localException)
      {
        if (!maybeRetryRequest(paramMessage)) {
          break label136;
        }
        return;
        postResponseHandler.obtainMessage(what, Pair.create(localObject2, localException)).sendToTarget();
      }
      Object localObject1 = callback;
      UUID localUUID = uuid;
      Object localObject3 = (ExoMediaDrm.ProvisionRequest)localObject2;
      localObject1 = ((MediaDrmCallback)localObject1).executeProvisionRequest(localUUID, (ExoMediaDrm.ProvisionRequest)localObject3);
      break label136;
      localObject1 = new RuntimeException();
      throw ((Throwable)localObject1);
      label136:
    }
    
    void post(int paramInt, Object paramObject, boolean paramBoolean)
    {
      throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
    }
  }
  
  @SuppressLint({"HandlerLeak"})
  class PostResponseHandler
    extends Handler
  {
    public PostResponseHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      Object localObject2 = (Pair)obj;
      Object localObject1 = first;
      localObject2 = second;
      switch (what)
      {
      default: 
        return;
      case 1: 
        DefaultDrmSession.this.onKeyResponse(localObject1, localObject2);
        return;
      }
      DefaultDrmSession.this.onProvisionResponse(localObject1, localObject2);
    }
  }
  
  public abstract interface ProvisioningManager<T extends com.google.android.exoplayer2.drm.ExoMediaCrypto>
  {
    public abstract void onProvisionCompleted();
    
    public abstract void onProvisionError(Exception paramException);
    
    public abstract void provisionRequired(DefaultDrmSession paramDefaultDrmSession);
  }
}
