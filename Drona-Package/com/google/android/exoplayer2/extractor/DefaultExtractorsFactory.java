package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.extractor.ClickListeners.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.ClickListeners.Mp4Extractor;
import com.google.android.exoplayer2.extractor.configurations.Ac3Extractor;
import com.google.android.exoplayer2.extractor.configurations.AdtsExtractor;
import com.google.android.exoplayer2.extractor.configurations.PsExtractor;
import com.google.android.exoplayer2.extractor.configurations.TsExtractor;
import com.google.android.exoplayer2.extractor.flv.FlvExtractor;
import com.google.android.exoplayer2.extractor.labs.OggExtractor;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.extractor.ogg.AmrExtractor;
import com.google.android.exoplayer2.extractor.wav.WavExtractor;
import com.google.android.exoplayer2.extractor.webm.MatroskaExtractor;
import java.lang.reflect.Constructor;

public final class DefaultExtractorsFactory
  implements ExtractorsFactory
{
  private static final Constructor<? extends Extractor> FLAC_EXTRACTOR_CONSTRUCTOR;
  private int adtsFlags;
  private int amrFlags;
  private boolean constantBitrateSeekingEnabled;
  private int fragmentedMp4Flags;
  private int matroskaFlags;
  private int mp3Flags;
  private int mp4Flags;
  private int tsFlags;
  private int tsMode = 1;
  
  static
  {
    try
    {
      try
      {
        Object localObject1 = Class.forName("com.google.android.exoplayer2.ext.flac.FlacExtractor").asSubclass(Extractor.class);
        localObject1 = ((Class)localObject1).getConstructor(new Class[0]);
      }
      catch (Exception localException)
      {
        throw new RuntimeException("Error instantiating FLAC extension", localException);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      Object localObject2;
      for (;;) {}
    }
    localObject2 = null;
    FLAC_EXTRACTOR_CONSTRUCTOR = localObject2;
  }
  
  public DefaultExtractorsFactory() {}
  
  public Extractor[] createExtractors()
  {
    for (;;)
    {
      try
      {
        if (FLAC_EXTRACTOR_CONSTRUCTOR == null)
        {
          i = 12;
          Extractor[] arrayOfExtractor = new Extractor[i];
          arrayOfExtractor[0] = new MatroskaExtractor(matroskaFlags);
          arrayOfExtractor[1] = new FragmentedMp4Extractor(fragmentedMp4Flags);
          arrayOfExtractor[2] = new Mp4Extractor(mp4Flags);
          arrayOfExtractor[3] = new Mp3Extractor(mp3Flags | constantBitrateSeekingEnabled);
          arrayOfExtractor[4] = new AdtsExtractor(0L, adtsFlags | constantBitrateSeekingEnabled);
          arrayOfExtractor[5] = new Ac3Extractor();
          arrayOfExtractor[6] = new TsExtractor(tsMode, tsFlags);
          arrayOfExtractor[7] = new FlvExtractor();
          arrayOfExtractor[8] = new OggExtractor();
          arrayOfExtractor[9] = new PsExtractor();
          arrayOfExtractor[10] = new WavExtractor();
          arrayOfExtractor[11] = new AmrExtractor(amrFlags | constantBitrateSeekingEnabled);
          Object localObject = FLAC_EXTRACTOR_CONSTRUCTOR;
          if (localObject != null)
          {
            localObject = FLAC_EXTRACTOR_CONSTRUCTOR;
            try
            {
              localObject = ((Constructor)localObject).newInstance(new Object[0]);
              arrayOfExtractor[12] = ((Extractor)localObject);
            }
            catch (Exception localException)
            {
              throw new IllegalStateException("Unexpected error creating FLAC extractor", localException);
            }
          }
          return localException;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      int i = 13;
    }
  }
  
  public DefaultExtractorsFactory setAdtsExtractorFlags(int paramInt)
  {
    try
    {
      adtsFlags = paramInt;
      return this;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public DefaultExtractorsFactory setAmrExtractorFlags(int paramInt)
  {
    try
    {
      amrFlags = paramInt;
      return this;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public DefaultExtractorsFactory setConstantBitrateSeekingEnabled(boolean paramBoolean)
  {
    try
    {
      constantBitrateSeekingEnabled = paramBoolean;
      return this;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public DefaultExtractorsFactory setFragmentedMp4ExtractorFlags(int paramInt)
  {
    try
    {
      fragmentedMp4Flags = paramInt;
      return this;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public DefaultExtractorsFactory setMatroskaExtractorFlags(int paramInt)
  {
    try
    {
      matroskaFlags = paramInt;
      return this;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public DefaultExtractorsFactory setMp3ExtractorFlags(int paramInt)
  {
    try
    {
      mp3Flags = paramInt;
      return this;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public DefaultExtractorsFactory setMp4ExtractorFlags(int paramInt)
  {
    try
    {
      mp4Flags = paramInt;
      return this;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public DefaultExtractorsFactory setTsExtractorFlags(int paramInt)
  {
    try
    {
      tsFlags = paramInt;
      return this;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public DefaultExtractorsFactory setTsExtractorMode(int paramInt)
  {
    try
    {
      tsMode = paramInt;
      return this;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
