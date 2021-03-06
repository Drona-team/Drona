package client.testing.util;

import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class VoiceActivityDetector
{
  private static final double ENERGY_FACTOR = 3.1D;
  public static final int FRAME_SIZE_IN_BYTES = 320;
  private static final int MAX_CZ = 15;
  private static final long MAX_SILENCE_MILLIS = 3500L;
  private static final int MIN_CZ = 5;
  private static final long MIN_SILENCE_MILLIS = 800L;
  private static final int MIN_SPEECH_SEQUENCE_COUNT = 3;
  public static final int NOISE_BYTES = 4800;
  private static final int NOISE_FRAMES = 15;
  private static final int SEQUENCE_LENGTH_MILLIS = 30;
  private static final long SILENCE_DIFF_MILLIS = 2700L;
  public static final String spt = "ai.api.util.VoiceActivityDetector";
  private boolean enabled = true;
  private SpeechEventsListener eventsListener;
  private int frameNumber;
  private long lastActiveTime = -1L;
  private long lastSequenceTime = 0L;
  private double noiseEnergy = 0.0D;
  private double pressure = 0.0D;
  private boolean process = true;
  private final int sampleRate;
  private int sequenceCounter = 0;
  private long silenceMillis = 3500L;
  private int size = 0;
  private boolean speechActive = false;
  private long time = 0L;
  
  public VoiceActivityDetector(int paramInt)
  {
    sampleRate = paramInt;
  }
  
  private boolean isFrameActive(ShortBuffer paramShortBuffer)
  {
    int i1 = paramShortBuffer.limit();
    size += i1;
    double d = 0.0D;
    int k = 0;
    int j = 0;
    for (int m = 0;; m = i)
    {
      i = 1;
      if (k >= i1) {
        break;
      }
      int n = paramShortBuffer.get(k);
      float f = (float)(n / 32767.0D);
      d += f * f / i1;
      pressure += n * n;
      if (f <= 0.0F) {
        i = -1;
      }
      n = j;
      if (m != 0)
      {
        n = j;
        if (i != m) {
          n = j + 1;
        }
      }
      k += 1;
      j = n;
    }
    int i = frameNumber + 1;
    frameNumber = i;
    if (i < 15)
    {
      noiseEnergy += d / 15.0D;
      return false;
    }
    return (j >= 5) && (j <= 15) && (d > noiseEnergy * 3.1D);
  }
  
  private void onSpeechBegin()
  {
    Log.v(spt, "onSpeechBegin");
    speechActive = true;
    if (eventsListener != null) {
      eventsListener.onSpeechBegin();
    }
  }
  
  private void onSpeechCancel()
  {
    Log.v(spt, "onSpeechCancel");
    speechActive = false;
    process = false;
    if (eventsListener != null) {
      eventsListener.onSpeechCancel();
    }
  }
  
  private void onSpeechEnd()
  {
    Log.v(spt, "onSpeechEnd");
    speechActive = false;
    process = false;
    if ((enabled) && (eventsListener != null)) {
      eventsListener.onSpeechEnd();
    }
  }
  
  public double calculateRms()
  {
    double d = Math.sqrt(pressure / size) / 100.0D;
    pressure = 0.0D;
    size = 0;
    return d;
  }
  
  public void processBuffer(byte[] paramArrayOfByte, int paramInt)
  {
    if (!process) {
      return;
    }
    boolean bool = isFrameActive(ByteBuffer.wrap(paramArrayOfByte, 0, paramInt).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer());
    paramInt /= 2;
    time = (frameNumber * paramInt * 1000 / sampleRate);
    if (bool)
    {
      if ((lastActiveTime >= 0L) && (time - lastActiveTime < 30L))
      {
        paramInt = sequenceCounter + 1;
        sequenceCounter = paramInt;
        if (paramInt >= 3)
        {
          if (!speechActive) {
            onSpeechBegin();
          }
          lastSequenceTime = time;
          silenceMillis = Math.max(800L, silenceMillis - 675L);
        }
      }
      else
      {
        sequenceCounter = 1;
      }
      lastActiveTime = time;
      return;
    }
    if (time - lastSequenceTime > silenceMillis)
    {
      if (speechActive)
      {
        onSpeechEnd();
        return;
      }
      onSpeechCancel();
    }
  }
  
  public void reset()
  {
    time = 0L;
    frameNumber = 0;
    noiseEnergy = 0.0D;
    lastActiveTime = -1L;
    lastSequenceTime = 0L;
    sequenceCounter = 0;
    silenceMillis = 3500L;
    speechActive = false;
    process = true;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    enabled = paramBoolean;
  }
  
  public void setSpeechListener(SpeechEventsListener paramSpeechEventsListener)
  {
    eventsListener = paramSpeechEventsListener;
  }
  
  public abstract interface SpeechEventsListener
  {
    public abstract void onSpeechBegin();
    
    public abstract void onSpeechCancel();
    
    public abstract void onSpeechEnd();
  }
}
