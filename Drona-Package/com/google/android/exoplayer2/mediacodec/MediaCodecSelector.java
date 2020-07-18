package com.google.android.exoplayer2.mediacodec;

import java.util.Collections;
import java.util.List;

public abstract interface MediaCodecSelector
{
  public static final MediaCodecSelector DEFAULT = new MediaCodecSelector()
  {
    public List getDecoderInfos(String paramAnonymousString, boolean paramAnonymousBoolean)
      throws MediaCodecUtil.DecoderQueryException
    {
      paramAnonymousString = MediaCodecUtil.getDecoderInfos(paramAnonymousString, paramAnonymousBoolean);
      if (paramAnonymousString.isEmpty()) {
        return Collections.emptyList();
      }
      return Collections.singletonList(paramAnonymousString.get(0));
    }
    
    public MediaCodecInfo getPassthroughDecoderInfo()
      throws MediaCodecUtil.DecoderQueryException
    {
      return MediaCodecUtil.getPassthroughDecoderInfo();
    }
  };
  public static final MediaCodecSelector DEFAULT_WITH_FALLBACK = new MediaCodecSelector()
  {
    public List getDecoderInfos(String paramAnonymousString, boolean paramAnonymousBoolean)
      throws MediaCodecUtil.DecoderQueryException
    {
      return MediaCodecUtil.getDecoderInfos(paramAnonymousString, paramAnonymousBoolean);
    }
    
    public MediaCodecInfo getPassthroughDecoderInfo()
      throws MediaCodecUtil.DecoderQueryException
    {
      return MediaCodecUtil.getPassthroughDecoderInfo();
    }
  };
  
  public abstract List getDecoderInfos(String paramString, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException;
  
  public abstract MediaCodecInfo getPassthroughDecoderInfo()
    throws MediaCodecUtil.DecoderQueryException;
}
