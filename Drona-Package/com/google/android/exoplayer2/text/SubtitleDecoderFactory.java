package com.google.android.exoplayer2.text;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.text.ClickListeners.Cea608Decoder;
import com.google.android.exoplayer2.text.ClickListeners.Cea708Decoder;
import com.google.android.exoplayer2.text.io.SsaDecoder;
import com.google.android.exoplayer2.text.subrip.SubripDecoder;
import com.google.android.exoplayer2.text.supplement.DvbDecoder;
import com.google.android.exoplayer2.text.ttml.TtmlDecoder;
import com.google.android.exoplayer2.text.tx3g.Tx3gDecoder;
import com.google.android.exoplayer2.text.wav.PgsDecoder;
import com.google.android.exoplayer2.text.webvtt.Mp4WebvttDecoder;
import com.google.android.exoplayer2.text.webvtt.WebvttDecoder;

public abstract interface SubtitleDecoderFactory
{
  public static final SubtitleDecoderFactory DEFAULT = new SubtitleDecoderFactory()
  {
    public SubtitleDecoder createDecoder(Format paramAnonymousFormat)
    {
      String str = sampleMimeType;
      switch (str.hashCode())
      {
      default: 
        break;
      case 1693976202: 
        if (str.equals("application/ttml+xml")) {
          i = 3;
        }
        break;
      case 1668750253: 
        if (str.equals("application/x-subrip")) {
          i = 4;
        }
        break;
      case 1566016562: 
        if (str.equals("application/cea-708")) {
          i = 8;
        }
        break;
      case 1566015601: 
        if (str.equals("application/cea-608")) {
          i = 6;
        }
        break;
      case 930165504: 
        if (str.equals("application/x-mp4-cea-608")) {
          i = 7;
        }
        break;
      case 822864842: 
        if (str.equals("text/x-ssa")) {
          i = 1;
        }
        break;
      case 691401887: 
        if (str.equals("application/x-quicktime-tx3g")) {
          i = 5;
        }
        break;
      case -1004728940: 
        if (str.equals("text/vtt")) {
          i = 0;
        }
        break;
      case -1026075066: 
        if (str.equals("application/x-mp4-vtt")) {
          i = 2;
        }
        break;
      case -1248334819: 
        if (str.equals("application/pgs")) {
          i = 10;
        }
        break;
      case -1351681404: 
        if (str.equals("application/dvbsubs")) {
          i = 9;
        }
        break;
      }
      int i = -1;
      switch (i)
      {
      default: 
        throw new IllegalArgumentException("Attempted to create decoder for unsupported format");
      case 10: 
        return new PgsDecoder();
      case 9: 
        return new DvbDecoder(initializationData);
      case 8: 
        return new Cea708Decoder(accessibilityChannel, initializationData);
      case 6: 
      case 7: 
        return new Cea608Decoder(sampleMimeType, accessibilityChannel);
      case 5: 
        return new Tx3gDecoder(initializationData);
      case 4: 
        return new SubripDecoder();
      case 3: 
        return new TtmlDecoder();
      case 2: 
        return new Mp4WebvttDecoder();
      case 1: 
        return new SsaDecoder(initializationData);
      }
      return new WebvttDecoder();
    }
    
    public boolean supportsFormat(Format paramAnonymousFormat)
    {
      paramAnonymousFormat = sampleMimeType;
      return ("text/vtt".equals(paramAnonymousFormat)) || ("text/x-ssa".equals(paramAnonymousFormat)) || ("application/ttml+xml".equals(paramAnonymousFormat)) || ("application/x-mp4-vtt".equals(paramAnonymousFormat)) || ("application/x-subrip".equals(paramAnonymousFormat)) || ("application/x-quicktime-tx3g".equals(paramAnonymousFormat)) || ("application/cea-608".equals(paramAnonymousFormat)) || ("application/x-mp4-cea-608".equals(paramAnonymousFormat)) || ("application/cea-708".equals(paramAnonymousFormat)) || ("application/dvbsubs".equals(paramAnonymousFormat)) || ("application/pgs".equals(paramAnonymousFormat));
    }
  };
  
  public abstract SubtitleDecoder createDecoder(Format paramFormat);
  
  public abstract boolean supportsFormat(Format paramFormat);
}
