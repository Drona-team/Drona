package com.google.android.exoplayer2.upgrade;

import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Map;

public final class ErrorStateDrmSession<T extends com.google.android.exoplayer2.drm.ExoMediaCrypto>
  implements DrmSession<T>
{
  private final DrmSession.DrmSessionException error;
  
  public ErrorStateDrmSession(DrmSession.DrmSessionException paramDrmSessionException)
  {
    error = ((DrmSession.DrmSessionException)Assertions.checkNotNull(paramDrmSessionException));
  }
  
  public DrmSession.DrmSessionException getError()
  {
    return error;
  }
  
  public ExoMediaCrypto getMediaCrypto()
  {
    return null;
  }
  
  public byte[] getOfflineLicenseKeySetId()
  {
    return null;
  }
  
  public int getState()
  {
    return 1;
  }
  
  public Map queryKeyStatus()
  {
    return null;
  }
}
