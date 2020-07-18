package com.google.android.exoplayer2.video.spherical;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.Buffer;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;

public class CameraMotionRenderer
  extends BaseRenderer
{
  private static final int SAMPLE_WINDOW_DURATION_US = 100000;
  private final DecoderInputBuffer buffer = new DecoderInputBuffer(1);
  private final FormatHolder formatHolder = new FormatHolder();
  private long lastTimestampUs;
  @Nullable
  private CameraMotionListener listener;
  private long offsetUs;
  private final ParsableByteArray scratch = new ParsableByteArray();
  
  public CameraMotionRenderer()
  {
    super(5);
  }
  
  private float[] parseMetadata(ByteBuffer paramByteBuffer)
  {
    if (paramByteBuffer.remaining() != 16) {
      return null;
    }
    scratch.reset(paramByteBuffer.array(), paramByteBuffer.limit());
    scratch.setPosition(paramByteBuffer.arrayOffset() + 4);
    paramByteBuffer = new float[3];
    int i = 0;
    while (i < 3)
    {
      paramByteBuffer[i] = Float.intBitsToFloat(scratch.readLittleEndianInt());
      i += 1;
    }
    return paramByteBuffer;
  }
  
  private void reset()
  {
    lastTimestampUs = 0L;
    if (listener != null) {
      listener.onCameraMotionReset();
    }
  }
  
  public void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {
    if (paramInt == 7)
    {
      listener = ((CameraMotionListener)paramObject);
      return;
    }
    super.handleMessage(paramInt, paramObject);
  }
  
  public boolean isEnded()
  {
    return hasReadStreamToEnd();
  }
  
  public boolean isReady()
  {
    return true;
  }
  
  protected void onDisabled()
  {
    reset();
  }
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    reset();
  }
  
  protected void onStreamChanged(Format[] paramArrayOfFormat, long paramLong)
    throws ExoPlaybackException
  {
    offsetUs = paramLong;
  }
  
  public void render(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    while ((!hasReadStreamToEnd()) && (lastTimestampUs < 100000L + paramLong1))
    {
      buffer.clear();
      if (readSource(formatHolder, buffer, false) != -4) {
        break;
      }
      if (buffer.isEndOfStream()) {
        return;
      }
      buffer.flip();
      lastTimestampUs = buffer.timeUs;
      if (listener != null)
      {
        float[] arrayOfFloat = parseMetadata(buffer.data);
        if (arrayOfFloat != null) {
          ((CameraMotionListener)Util.castNonNull(listener)).onCameraMotion(lastTimestampUs - offsetUs, arrayOfFloat);
        }
      }
    }
  }
  
  public int supportsFormat(Format paramFormat)
  {
    if ("application/x-camera-motion".equals(sampleMimeType)) {
      return 4;
    }
    return 0;
  }
}
