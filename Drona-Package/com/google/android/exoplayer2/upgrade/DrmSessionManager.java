package com.google.android.exoplayer2.upgrade;

import android.annotation.TargetApi;
import android.os.Looper;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;

@TargetApi(16)
public abstract interface DrmSessionManager<T extends ExoMediaCrypto>
{
  public abstract DrmSession acquireSession(Looper paramLooper, DrmInitData paramDrmInitData);
  
  public abstract boolean canAcquireSession(DrmInitData paramDrmInitData);
  
  public abstract void releaseSession(DrmSession paramDrmSession);
}
