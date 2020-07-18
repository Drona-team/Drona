package com.google.android.exoplayer2.upgrade;

import android.annotation.TargetApi;
import android.media.DeniedByServerException;
import android.media.MediaCrypto;
import android.media.MediaCryptoException;
import android.media.MediaDrm;
import android.media.MediaDrm.KeyRequest;
import android.media.MediaDrm.ProvisionRequest;
import android.media.MediaDrmException;
import android.media.NotProvisionedException;
import android.media.UnsupportedSchemeException;
import android.text.TextUtils;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.drm.ExoMediaDrm;
import com.google.android.exoplayer2.extractor.ClickListeners.PsshAtomUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@TargetApi(23)
public final class FrameworkMediaDrm
  implements ExoMediaDrm<com.google.android.exoplayer2.drm.FrameworkMediaCrypto>
{
  private static final String CENC_SCHEME_MIME_TYPE = "cenc";
  private final MediaDrm mediaDrm;
  private final UUID uuid;
  
  private FrameworkMediaDrm(UUID paramUUID)
    throws UnsupportedSchemeException
  {
    Assertions.checkNotNull(paramUUID);
    Assertions.checkArgument(IpAddress.COMMON_PSSH_UUID.equals(paramUUID) ^ true, "Use C.CLEARKEY_UUID instead");
    uuid = paramUUID;
    mediaDrm = new MediaDrm(adjustUuid(paramUUID));
    if ((IpAddress.WIDEVINE_UUID.equals(paramUUID)) && (needsForceWidevineL3Workaround())) {
      forceWidevineL3(mediaDrm);
    }
  }
  
  private static byte[] adjustRequestData(UUID paramUUID, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = paramArrayOfByte;
    if (IpAddress.CLEARKEY_UUID.equals(paramUUID)) {
      arrayOfByte = ClearKeyUtil.adjustRequestData(paramArrayOfByte);
    }
    return arrayOfByte;
  }
  
  private static byte[] adjustRequestInitData(UUID paramUUID, byte[] paramArrayOfByte)
  {
    if (((Util.SDK_INT < 21) && (IpAddress.WIDEVINE_UUID.equals(paramUUID))) || ((IpAddress.PLAYREADY_UUID.equals(paramUUID)) && ("Amazon".equals(Util.MANUFACTURER)) && (("AFTB".equals(Util.MODEL)) || ("AFTS".equals(Util.MODEL)) || ("AFTM".equals(Util.MODEL)))))
    {
      paramUUID = PsshAtomUtil.parseSchemeSpecificData(paramArrayOfByte, paramUUID);
      if (paramUUID != null) {
        return paramUUID;
      }
    }
    return paramArrayOfByte;
  }
  
  private static String adjustRequestMimeType(UUID paramUUID, String paramString)
  {
    if ((Util.SDK_INT < 26) && (IpAddress.CLEARKEY_UUID.equals(paramUUID)) && (("video/mp4".equals(paramString)) || ("audio/mp4".equals(paramString)))) {
      return "cenc";
    }
    return paramString;
  }
  
  private static UUID adjustUuid(UUID paramUUID)
  {
    UUID localUUID = paramUUID;
    if (Util.SDK_INT < 27)
    {
      localUUID = paramUUID;
      if (IpAddress.CLEARKEY_UUID.equals(paramUUID)) {
        localUUID = IpAddress.COMMON_PSSH_UUID;
      }
    }
    return localUUID;
  }
  
  private static void forceWidevineL3(MediaDrm paramMediaDrm)
  {
    paramMediaDrm.setPropertyString("securityLevel", "L3");
  }
  
  private static DrmInitData.SchemeData getSchemeData(UUID paramUUID, List paramList)
  {
    if (!IpAddress.WIDEVINE_UUID.equals(paramUUID)) {
      return (DrmInitData.SchemeData)paramList.get(0);
    }
    int j;
    if ((Util.SDK_INT >= 28) && (paramList.size() > 1))
    {
      paramUUID = (DrmInitData.SchemeData)paramList.get(0);
      j = 0;
      i = 0;
      Object localObject;
      while (j < paramList.size())
      {
        localObject = (DrmInitData.SchemeData)paramList.get(j);
        if ((requiresSecureDecryption == requiresSecureDecryption) && (Util.areEqual(mimeType, mimeType)) && (Util.areEqual(licenseServerUrl, licenseServerUrl)) && (PsshAtomUtil.isPsshAtom(data)))
        {
          i += data.length;
          j += 1;
        }
        else
        {
          j = 0;
          break label152;
        }
      }
      j = 1;
      label152:
      if (j != 0)
      {
        localObject = new byte[i];
        i = 0;
        j = 0;
        while (i < paramList.size())
        {
          DrmInitData.SchemeData localSchemeData = (DrmInitData.SchemeData)paramList.get(i);
          int k = data.length;
          System.arraycopy(data, 0, localObject, j, k);
          j += k;
          i += 1;
        }
        return paramUUID.copyWithData((byte[])localObject);
      }
    }
    int i = 0;
    while (i < paramList.size())
    {
      paramUUID = (DrmInitData.SchemeData)paramList.get(i);
      j = PsshAtomUtil.parseVersion(data);
      if ((Util.SDK_INT < 23) && (j == 0)) {
        return paramUUID;
      }
      if ((Util.SDK_INT >= 23) && (j == 1)) {
        return paramUUID;
      }
      i += 1;
    }
    return (DrmInitData.SchemeData)paramList.get(0);
  }
  
  private static boolean needsForceWidevineL3Workaround()
  {
    return "ASUS_Z00AD".equals(Util.MODEL);
  }
  
  public static FrameworkMediaDrm newInstance(UUID paramUUID)
    throws UnsupportedDrmException
  {
    try
    {
      paramUUID = new FrameworkMediaDrm(paramUUID);
      return paramUUID;
    }
    catch (Exception paramUUID)
    {
      throw new UnsupportedDrmException(2, paramUUID);
    }
    catch (UnsupportedSchemeException paramUUID)
    {
      throw new UnsupportedDrmException(1, paramUUID);
    }
  }
  
  public void closeSession(byte[] paramArrayOfByte)
  {
    mediaDrm.closeSession(paramArrayOfByte);
  }
  
  public FrameworkMediaCrypto createMediaCrypto(byte[] paramArrayOfByte)
    throws MediaCryptoException
  {
    boolean bool;
    if ((Util.SDK_INT < 21) && (IpAddress.WIDEVINE_UUID.equals(uuid)) && ("L3".equals(getPropertyString("securityLevel")))) {
      bool = true;
    } else {
      bool = false;
    }
    return new FrameworkMediaCrypto(new MediaCrypto(adjustUuid(uuid), paramArrayOfByte), bool);
  }
  
  public ExoMediaDrm.KeyRequest getKeyRequest(byte[] paramArrayOfByte, List paramList, int paramInt, HashMap paramHashMap)
    throws NotProvisionedException
  {
    Object localObject3 = null;
    if (paramList != null)
    {
      localObject2 = getSchemeData(uuid, paramList);
      paramList = (List)localObject2;
      localObject1 = adjustRequestInitData(uuid, data);
      localObject2 = adjustRequestMimeType(uuid, mimeType);
    }
    else
    {
      localObject1 = null;
      localObject2 = null;
      paramList = localObject3;
    }
    paramArrayOfByte = mediaDrm.getKeyRequest(paramArrayOfByte, (byte[])localObject1, (String)localObject2, paramInt, paramHashMap);
    Object localObject2 = adjustRequestData(uuid, paramArrayOfByte.getData());
    Object localObject1 = paramArrayOfByte.getDefaultUrl();
    paramArrayOfByte = (byte[])localObject1;
    paramHashMap = paramArrayOfByte;
    if (TextUtils.isEmpty((CharSequence)localObject1))
    {
      paramHashMap = paramArrayOfByte;
      if (paramList != null)
      {
        paramHashMap = paramArrayOfByte;
        if (!TextUtils.isEmpty(licenseServerUrl)) {
          paramHashMap = licenseServerUrl;
        }
      }
    }
    return new ExoMediaDrm.KeyRequest((byte[])localObject2, paramHashMap);
  }
  
  public byte[] getPropertyByteArray(String paramString)
  {
    return mediaDrm.getPropertyByteArray(paramString);
  }
  
  public String getPropertyString(String paramString)
  {
    return mediaDrm.getPropertyString(paramString);
  }
  
  public ExoMediaDrm.ProvisionRequest getProvisionRequest()
  {
    MediaDrm.ProvisionRequest localProvisionRequest = mediaDrm.getProvisionRequest();
    return new ExoMediaDrm.ProvisionRequest(localProvisionRequest.getData(), localProvisionRequest.getDefaultUrl());
  }
  
  public byte[] openSession()
    throws MediaDrmException
  {
    return mediaDrm.openSession();
  }
  
  public byte[] provideKeyResponse(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws NotProvisionedException, DeniedByServerException
  {
    byte[] arrayOfByte = paramArrayOfByte2;
    if (IpAddress.CLEARKEY_UUID.equals(uuid)) {
      arrayOfByte = ClearKeyUtil.adjustResponseData(paramArrayOfByte2);
    }
    return mediaDrm.provideKeyResponse(paramArrayOfByte1, arrayOfByte);
  }
  
  public void provideProvisionResponse(byte[] paramArrayOfByte)
    throws DeniedByServerException
  {
    mediaDrm.provideProvisionResponse(paramArrayOfByte);
  }
  
  public Map queryKeyStatus(byte[] paramArrayOfByte)
  {
    return mediaDrm.queryKeyStatus(paramArrayOfByte);
  }
  
  public void release()
  {
    mediaDrm.release();
  }
  
  public void restoreKeys(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    mediaDrm.restoreKeys(paramArrayOfByte1, paramArrayOfByte2);
  }
  
  public void setOnEventListener(ExoMediaDrm.OnEventListener paramOnEventListener)
  {
    MediaDrm localMediaDrm = mediaDrm;
    if (paramOnEventListener == null) {
      paramOnEventListener = null;
    } else {
      paramOnEventListener = new -..Lambda.FrameworkMediaDrm.zJ3h9UKP9ayPF2iQATh7r7bKJes(this, paramOnEventListener);
    }
    localMediaDrm.setOnEventListener(paramOnEventListener);
  }
  
  public void setOnKeyStatusChangeListener(ExoMediaDrm.OnKeyStatusChangeListener paramOnKeyStatusChangeListener)
  {
    if (Util.SDK_INT >= 23)
    {
      MediaDrm localMediaDrm = mediaDrm;
      if (paramOnKeyStatusChangeListener == null) {
        paramOnKeyStatusChangeListener = null;
      } else {
        paramOnKeyStatusChangeListener = new -..Lambda.FrameworkMediaDrm.WcqXRf-ZlBuRYiaqpRgpL0-wRvg(this, paramOnKeyStatusChangeListener);
      }
      localMediaDrm.setOnKeyStatusChangeListener(paramOnKeyStatusChangeListener, null);
      return;
    }
    throw new UnsupportedOperationException();
  }
  
  public void setPropertyByteArray(String paramString, byte[] paramArrayOfByte)
  {
    mediaDrm.setPropertyByteArray(paramString, paramArrayOfByte);
  }
  
  public void setPropertyString(String paramString1, String paramString2)
  {
    mediaDrm.setPropertyString(paramString1, paramString2);
  }
}
