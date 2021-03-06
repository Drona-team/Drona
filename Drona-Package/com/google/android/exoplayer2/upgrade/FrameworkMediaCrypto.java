package com.google.android.exoplayer2.upgrade;

import android.annotation.TargetApi;
import android.media.MediaCrypto;
import com.google.android.exoplayer2.util.Assertions;

@TargetApi(16)
public final class FrameworkMediaCrypto
  implements ExoMediaCrypto
{
  private final boolean forceAllowInsecureDecoderComponents;
  private final MediaCrypto mediaCrypto;
  
  public FrameworkMediaCrypto(MediaCrypto paramMediaCrypto)
  {
    this(paramMediaCrypto, false);
  }
  
  public FrameworkMediaCrypto(MediaCrypto paramMediaCrypto, boolean paramBoolean)
  {
    mediaCrypto = ((MediaCrypto)Assertions.checkNotNull(paramMediaCrypto));
    forceAllowInsecureDecoderComponents = paramBoolean;
  }
  
  public MediaCrypto getWrappedMediaCrypto()
  {
    return mediaCrypto;
  }
  
  public boolean requiresSecureDecoderComponent(String paramString)
  {
    return (!forceAllowInsecureDecoderComponents) && (mediaCrypto.requiresSecureDecoderComponent(paramString));
  }
}
