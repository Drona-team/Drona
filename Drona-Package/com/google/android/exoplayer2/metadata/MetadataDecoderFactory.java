package com.google.android.exoplayer2.metadata;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.metadata.configurations.Id3Decoder;
import com.google.android.exoplayer2.metadata.emsg.EventMessageDecoder;
import com.google.android.exoplayer2.metadata.scte35.SpliceInfoDecoder;

public abstract interface MetadataDecoderFactory
{
  public static final MetadataDecoderFactory DEFAULT = new MetadataDecoderFactory()
  {
    public MetadataDecoder createDecoder(Format paramAnonymousFormat)
    {
      paramAnonymousFormat = sampleMimeType;
      int i = paramAnonymousFormat.hashCode();
      if (i != -1248341703)
      {
        if (i != 1154383568)
        {
          if ((i == 1652648887) && (paramAnonymousFormat.equals("application/x-scte35")))
          {
            i = 2;
            break label75;
          }
        }
        else if (paramAnonymousFormat.equals("application/x-emsg"))
        {
          i = 1;
          break label75;
        }
      }
      else if (paramAnonymousFormat.equals("application/id3"))
      {
        i = 0;
        break label75;
      }
      i = -1;
      switch (i)
      {
      default: 
        throw new IllegalArgumentException("Attempted to create decoder for unsupported format");
      case 2: 
        return new SpliceInfoDecoder();
      case 1: 
        label75:
        return new EventMessageDecoder();
      }
      return new Id3Decoder();
    }
    
    public boolean supportsFormat(Format paramAnonymousFormat)
    {
      paramAnonymousFormat = sampleMimeType;
      return ("application/id3".equals(paramAnonymousFormat)) || ("application/x-emsg".equals(paramAnonymousFormat)) || ("application/x-scte35".equals(paramAnonymousFormat));
    }
  };
  
  public abstract MetadataDecoder createDecoder(Format paramFormat);
  
  public abstract boolean supportsFormat(Format paramFormat);
}
