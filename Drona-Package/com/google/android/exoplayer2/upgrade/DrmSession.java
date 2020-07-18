package com.google.android.exoplayer2.upgrade;

import android.annotation.TargetApi;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

@TargetApi(16)
public abstract interface DrmSession<T extends com.google.android.exoplayer2.drm.ExoMediaCrypto>
{
  public static final int STATE_ERROR = 1;
  public static final int STATE_OPENED = 3;
  public static final int STATE_OPENED_WITH_KEYS = 4;
  public static final int STATE_OPENING = 2;
  public static final int STATE_RELEASED = 0;
  
  public abstract DrmSessionException getError();
  
  public abstract ExoMediaCrypto getMediaCrypto();
  
  public abstract byte[] getOfflineLicenseKeySetId();
  
  public abstract int getState();
  
  public abstract Map queryKeyStatus();
  
  public class DrmSessionException
    extends Exception
  {
    public DrmSessionException()
    {
      super();
    }
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface State {}
}
