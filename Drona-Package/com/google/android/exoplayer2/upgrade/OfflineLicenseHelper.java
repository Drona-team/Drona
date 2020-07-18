package com.google.android.exoplayer2.upgrade;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Pair;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.upstream.HttpDataSource.Factory;
import com.google.android.exoplayer2.util.Assertions;
import java.util.HashMap;
import java.util.UUID;

public final class OfflineLicenseHelper<T extends ExoMediaCrypto>
{
  private final ConditionVariable conditionVariable;
  private final com.google.android.exoplayer2.drm.DefaultDrmSessionManager<T> drmSessionManager;
  private final HandlerThread handlerThread = new HandlerThread("OfflineLicenseHelper");
  
  public OfflineLicenseHelper(UUID paramUUID, ExoMediaDrm paramExoMediaDrm, MediaDrmCallback paramMediaDrmCallback, HashMap paramHashMap)
  {
    handlerThread.start();
    conditionVariable = new ConditionVariable();
    OfflineLicenseHelper.1 local1 = new OfflineLicenseHelper.1(this);
    drmSessionManager = new DefaultDrmSessionManager(paramUUID, paramExoMediaDrm, paramMediaDrmCallback, paramHashMap);
    drmSessionManager.addListener(new Handler(handlerThread.getLooper()), local1);
  }
  
  private byte[] blockingKeyRequest(int paramInt, byte[] paramArrayOfByte, DrmInitData paramDrmInitData)
    throws DrmSession.DrmSessionException
  {
    paramArrayOfByte = openBlockingKeyRequest(paramInt, paramArrayOfByte, paramDrmInitData);
    paramDrmInitData = paramArrayOfByte.getError();
    byte[] arrayOfByte = paramArrayOfByte.getOfflineLicenseKeySetId();
    drmSessionManager.releaseSession(paramArrayOfByte);
    if (paramDrmInitData == null) {
      return arrayOfByte;
    }
    throw paramDrmInitData;
  }
  
  public static OfflineLicenseHelper newWidevineInstance(String paramString, HttpDataSource.Factory paramFactory)
    throws UnsupportedDrmException
  {
    return newWidevineInstance(paramString, false, paramFactory, null);
  }
  
  public static OfflineLicenseHelper newWidevineInstance(String paramString, boolean paramBoolean, HttpDataSource.Factory paramFactory)
    throws UnsupportedDrmException
  {
    return newWidevineInstance(paramString, paramBoolean, paramFactory, null);
  }
  
  public static OfflineLicenseHelper newWidevineInstance(String paramString, boolean paramBoolean, HttpDataSource.Factory paramFactory, HashMap paramHashMap)
    throws UnsupportedDrmException
  {
    return new OfflineLicenseHelper(IpAddress.WIDEVINE_UUID, FrameworkMediaDrm.newInstance(IpAddress.WIDEVINE_UUID), new HttpMediaDrmCallback(paramString, paramBoolean, paramFactory), paramHashMap);
  }
  
  private DrmSession openBlockingKeyRequest(int paramInt, byte[] paramArrayOfByte, DrmInitData paramDrmInitData)
  {
    drmSessionManager.setMode(paramInt, paramArrayOfByte);
    conditionVariable.close();
    paramArrayOfByte = drmSessionManager.acquireSession(handlerThread.getLooper(), paramDrmInitData);
    conditionVariable.block();
    return paramArrayOfByte;
  }
  
  public byte[] downloadLicense(DrmInitData paramDrmInitData)
    throws DrmSession.DrmSessionException
  {
    boolean bool;
    if (paramDrmInitData != null) {
      bool = true;
    } else {
      bool = false;
    }
    try
    {
      Assertions.checkArgument(bool);
      paramDrmInitData = blockingKeyRequest(2, null, paramDrmInitData);
      return paramDrmInitData;
    }
    catch (Throwable paramDrmInitData)
    {
      throw paramDrmInitData;
    }
  }
  
  public Pair getLicenseDurationRemainingSec(byte[] paramArrayOfByte)
    throws DrmSession.DrmSessionException
  {
    try
    {
      Assertions.checkNotNull(paramArrayOfByte);
      paramArrayOfByte = openBlockingKeyRequest(1, paramArrayOfByte, null);
      DrmSession.DrmSessionException localDrmSessionException = paramArrayOfByte.getError();
      Pair localPair = WidevineUtil.getLicenseDurationRemainingSec(paramArrayOfByte);
      drmSessionManager.releaseSession(paramArrayOfByte);
      if (localDrmSessionException != null)
      {
        if ((localDrmSessionException.getCause() instanceof KeysExpiredException))
        {
          paramArrayOfByte = Pair.create(Long.valueOf(0L), Long.valueOf(0L));
          return paramArrayOfByte;
        }
        throw localDrmSessionException;
      }
      return localPair;
    }
    catch (Throwable paramArrayOfByte)
    {
      throw paramArrayOfByte;
    }
  }
  
  public byte[] getPropertyByteArray(String paramString)
  {
    try
    {
      paramString = drmSessionManager.getPropertyByteArray(paramString);
      return paramString;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public String getPropertyString(String paramString)
  {
    try
    {
      paramString = drmSessionManager.getPropertyString(paramString);
      return paramString;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void release()
  {
    handlerThread.quit();
  }
  
  public void releaseLicense(byte[] paramArrayOfByte)
    throws DrmSession.DrmSessionException
  {
    try
    {
      Assertions.checkNotNull(paramArrayOfByte);
      blockingKeyRequest(3, paramArrayOfByte, null);
      return;
    }
    catch (Throwable paramArrayOfByte)
    {
      throw paramArrayOfByte;
    }
  }
  
  public byte[] renewLicense(byte[] paramArrayOfByte)
    throws DrmSession.DrmSessionException
  {
    try
    {
      Assertions.checkNotNull(paramArrayOfByte);
      paramArrayOfByte = blockingKeyRequest(2, paramArrayOfByte, null);
      return paramArrayOfByte;
    }
    catch (Throwable paramArrayOfByte)
    {
      throw paramArrayOfByte;
    }
  }
  
  public void setPropertyByteArray(String paramString, byte[] paramArrayOfByte)
  {
    try
    {
      drmSessionManager.setPropertyByteArray(paramString, paramArrayOfByte);
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void setPropertyString(String paramString1, String paramString2)
  {
    try
    {
      drmSessionManager.setPropertyString(paramString1, paramString2);
      return;
    }
    catch (Throwable paramString1)
    {
      throw paramString1;
    }
  }
}
