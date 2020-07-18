package com.google.android.exoplayer2.upgrade;

import android.media.DeniedByServerException;
import android.media.MediaCryptoException;
import android.media.MediaDrmException;
import android.media.NotProvisionedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract interface ExoMediaDrm<T extends com.google.android.exoplayer2.drm.ExoMediaCrypto>
{
  public static final int EVENT_KEY_EXPIRED = 3;
  public static final int EVENT_KEY_REQUIRED = 2;
  public static final int EVENT_PROVISION_REQUIRED = 1;
  public static final int KEY_TYPE_OFFLINE = 2;
  public static final int KEY_TYPE_RELEASE = 3;
  public static final int KEY_TYPE_STREAMING = 1;
  
  public abstract void closeSession(byte[] paramArrayOfByte);
  
  public abstract ExoMediaCrypto createMediaCrypto(byte[] paramArrayOfByte)
    throws MediaCryptoException;
  
  public abstract KeyRequest getKeyRequest(byte[] paramArrayOfByte, List paramList, int paramInt, HashMap paramHashMap)
    throws NotProvisionedException;
  
  public abstract byte[] getPropertyByteArray(String paramString);
  
  public abstract String getPropertyString(String paramString);
  
  public abstract ProvisionRequest getProvisionRequest();
  
  public abstract byte[] openSession()
    throws MediaDrmException;
  
  public abstract byte[] provideKeyResponse(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws NotProvisionedException, DeniedByServerException;
  
  public abstract void provideProvisionResponse(byte[] paramArrayOfByte)
    throws DeniedByServerException;
  
  public abstract Map queryKeyStatus(byte[] paramArrayOfByte);
  
  public abstract void release();
  
  public abstract void restoreKeys(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  
  public abstract void setOnEventListener(OnEventListener paramOnEventListener);
  
  public abstract void setOnKeyStatusChangeListener(OnKeyStatusChangeListener paramOnKeyStatusChangeListener);
  
  public abstract void setPropertyByteArray(String paramString, byte[] paramArrayOfByte);
  
  public abstract void setPropertyString(String paramString1, String paramString2);
  
  public final class KeyRequest
  {
    private final String licenseServerUrl;
    
    public KeyRequest(String paramString)
    {
      licenseServerUrl = paramString;
    }
    
    public byte[] getData()
    {
      return ExoMediaDrm.this;
    }
    
    public String getLicenseServerUrl()
    {
      return licenseServerUrl;
    }
  }
  
  public final class KeyStatus
  {
    private final byte[] keyId;
    private final int statusCode;
    
    public KeyStatus(byte[] paramArrayOfByte)
    {
      statusCode = this$1;
      keyId = paramArrayOfByte;
    }
    
    public byte[] getKeyId()
    {
      return keyId;
    }
    
    public int getStatusCode()
    {
      return statusCode;
    }
  }
  
  public abstract interface OnEventListener<T extends com.google.android.exoplayer2.drm.ExoMediaCrypto>
  {
    public abstract void onEvent(ExoMediaDrm paramExoMediaDrm, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2);
  }
  
  public abstract interface OnKeyStatusChangeListener<T extends com.google.android.exoplayer2.drm.ExoMediaCrypto>
  {
    public abstract void onKeyStatusChange(ExoMediaDrm paramExoMediaDrm, byte[] paramArrayOfByte, List paramList, boolean paramBoolean);
  }
  
  public final class ProvisionRequest
  {
    private final String defaultUrl;
    
    public ProvisionRequest(String paramString)
    {
      defaultUrl = paramString;
    }
    
    public byte[] getData()
    {
      return ExoMediaDrm.this;
    }
    
    public String getDefaultUrl()
    {
      return defaultUrl;
    }
  }
}
