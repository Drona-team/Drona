package com.google.android.exoplayer2.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Handler;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.decoder.Buffer;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.mediacodec.MediaCodecInfo;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer2.mediacodec.MediaFormatUtil;
import com.google.android.exoplayer2.upgrade.DrmInitData;
import com.google.android.exoplayer2.upgrade.DrmInitData.SchemeData;
import com.google.android.exoplayer2.upgrade.DrmSessionManager;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MediaClock;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

@TargetApi(16)
public class MediaCodecAudioRenderer
  extends MediaCodecRenderer
  implements MediaClock
{
  private static final int MAX_PENDING_STREAM_CHANGE_COUNT = 10;
  private static final String PAGE_KEY = "MediaCodecAudioRenderer";
  private boolean allowFirstBufferPositionDiscontinuity;
  private boolean allowPositionDiscontinuity;
  private final AudioSink audioSink;
  private int channelCount;
  private int codecMaxInputSize;
  private boolean codecNeedsDiscardChannelsWorkaround;
  private boolean codecNeedsEosBufferTimestampWorkaround;
  private final Context context;
  private long currentPositionUs;
  private int encoderDelay;
  private int encoderPadding;
  private final AudioRendererEventListener.EventDispatcher eventDispatcher;
  private long lastInputTimeUs;
  private boolean passthroughEnabled;
  private MediaFormat passthroughMediaFormat;
  private int pcmEncoding;
  private int pendingStreamChangeCount;
  private final long[] pendingStreamChangeTimesUs;
  
  public MediaCodecAudioRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector)
  {
    this(paramContext, paramMediaCodecSelector, null, false);
  }
  
  public MediaCodecAudioRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector, Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener)
  {
    this(paramContext, paramMediaCodecSelector, null, false, paramHandler, paramAudioRendererEventListener);
  }
  
  public MediaCodecAudioRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector, DrmSessionManager paramDrmSessionManager, boolean paramBoolean)
  {
    this(paramContext, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, null, null);
  }
  
  public MediaCodecAudioRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector, DrmSessionManager paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener)
  {
    this(paramContext, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, paramHandler, paramAudioRendererEventListener, null, new AudioProcessor[0]);
  }
  
  public MediaCodecAudioRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector, DrmSessionManager paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioCapabilities paramAudioCapabilities, AudioProcessor... paramVarArgs)
  {
    this(paramContext, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, paramHandler, paramAudioRendererEventListener, new DefaultAudioSink(paramAudioCapabilities, paramVarArgs));
  }
  
  public MediaCodecAudioRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector, DrmSessionManager paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioSink paramAudioSink)
  {
    super(1, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, 44100.0F);
    context = paramContext.getApplicationContext();
    audioSink = paramAudioSink;
    lastInputTimeUs = -9223372036854775807L;
    pendingStreamChangeTimesUs = new long[10];
    eventDispatcher = new AudioRendererEventListener.EventDispatcher(paramHandler, paramAudioRendererEventListener);
    paramAudioSink.setListener(new AudioSinkListener(null));
  }
  
  private static boolean codecNeedsDiscardChannelsWorkaround(String paramString)
  {
    return (Util.SDK_INT < 24) && ("OMX.SEC.aac.dec".equals(paramString)) && ("samsung".equals(Util.MANUFACTURER)) && ((Util.DEVICE.startsWith("zeroflte")) || (Util.DEVICE.startsWith("herolte")) || (Util.DEVICE.startsWith("heroqlte")));
  }
  
  private static boolean codecNeedsEosBufferTimestampWorkaround(String paramString)
  {
    return (Util.SDK_INT < 21) && ("OMX.SEC.mp3.dec".equals(paramString)) && ("samsung".equals(Util.MANUFACTURER)) && ((Util.DEVICE.startsWith("baffin")) || (Util.DEVICE.startsWith("grand")) || (Util.DEVICE.startsWith("fortuna")) || (Util.DEVICE.startsWith("gprimelte")) || (Util.DEVICE.startsWith("j2y18lte")) || (Util.DEVICE.startsWith("ms01")));
  }
  
  private int getCodecMaxInputSize(MediaCodecInfo paramMediaCodecInfo, Format paramFormat)
  {
    if ((Util.SDK_INT < 24) && ("OMX.google.raw.decoder".equals(name)))
    {
      int j = 1;
      int i = j;
      if (Util.SDK_INT == 23)
      {
        paramMediaCodecInfo = context.getPackageManager();
        i = j;
        if (paramMediaCodecInfo != null)
        {
          i = j;
          if (paramMediaCodecInfo.hasSystemFeature("android.software.leanback")) {
            i = 0;
          }
        }
      }
      if (i != 0) {
        return -1;
      }
    }
    return maxInputSize;
  }
  
  private void updateCurrentPosition()
  {
    long l2 = audioSink.getCurrentPositionUs(isEnded());
    long l1 = l2;
    if (l2 != Long.MIN_VALUE)
    {
      if (!allowPositionDiscontinuity) {
        l1 = Math.max(currentPositionUs, l2);
      }
      currentPositionUs = l1;
      allowPositionDiscontinuity = false;
    }
  }
  
  protected boolean allowPassthrough(int paramInt, String paramString)
  {
    return audioSink.supportsOutput(paramInt, MimeTypes.getEncoding(paramString));
  }
  
  protected int canKeepCodec(MediaCodec paramMediaCodec, MediaCodecInfo paramMediaCodecInfo, Format paramFormat1, Format paramFormat2)
  {
    if ((getCodecMaxInputSize(paramMediaCodecInfo, paramFormat2) <= codecMaxInputSize) && (paramMediaCodecInfo.isSeamlessAdaptationSupported(paramFormat1, paramFormat2, true)) && (encoderDelay == 0) && (encoderPadding == 0) && (encoderDelay == 0) && (encoderPadding == 0)) {
      return 1;
    }
    return 0;
  }
  
  protected void configureCodec(MediaCodecInfo paramMediaCodecInfo, MediaCodec paramMediaCodec, Format paramFormat, MediaCrypto paramMediaCrypto, float paramFloat)
  {
    codecMaxInputSize = getCodecMaxInputSize(paramMediaCodecInfo, paramFormat, getStreamFormats());
    codecNeedsDiscardChannelsWorkaround = codecNeedsDiscardChannelsWorkaround(name);
    codecNeedsEosBufferTimestampWorkaround = codecNeedsEosBufferTimestampWorkaround(name);
    passthroughEnabled = passthrough;
    if (mimeType == null) {
      paramMediaCodecInfo = "audio/raw";
    } else {
      paramMediaCodecInfo = mimeType;
    }
    paramMediaCodecInfo = getMediaFormat(paramFormat, paramMediaCodecInfo, codecMaxInputSize, paramFloat);
    paramMediaCodec.configure(paramMediaCodecInfo, null, paramMediaCrypto, 0);
    if (passthroughEnabled)
    {
      passthroughMediaFormat = paramMediaCodecInfo;
      passthroughMediaFormat.setString("mime", sampleMimeType);
      return;
    }
    passthroughMediaFormat = null;
  }
  
  protected int getCodecMaxInputSize(MediaCodecInfo paramMediaCodecInfo, Format paramFormat, Format[] paramArrayOfFormat)
  {
    int j = getCodecMaxInputSize(paramMediaCodecInfo, paramFormat);
    if (paramArrayOfFormat.length == 1) {
      return j;
    }
    int m = paramArrayOfFormat.length;
    int i = 0;
    while (i < m)
    {
      Format localFormat = paramArrayOfFormat[i];
      int k = j;
      if (paramMediaCodecInfo.isSeamlessAdaptationSupported(paramFormat, localFormat, false)) {
        k = Math.max(j, getCodecMaxInputSize(paramMediaCodecInfo, localFormat));
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  protected float getCodecOperatingRate(float paramFloat, Format paramFormat, Format[] paramArrayOfFormat)
  {
    int m = paramArrayOfFormat.length;
    int i = 0;
    int k;
    for (int j = -1; i < m; j = k)
    {
      int n = sampleRate;
      k = j;
      if (n != -1) {
        k = Math.max(j, n);
      }
      i += 1;
    }
    if (j == -1) {
      return -1.0F;
    }
    return paramFloat * j;
  }
  
  protected List getDecoderInfos(MediaCodecSelector paramMediaCodecSelector, Format paramFormat, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    if (allowPassthrough(channelCount, sampleMimeType))
    {
      MediaCodecInfo localMediaCodecInfo = paramMediaCodecSelector.getPassthroughDecoderInfo();
      if (localMediaCodecInfo != null) {
        return Collections.singletonList(localMediaCodecInfo);
      }
    }
    return super.getDecoderInfos(paramMediaCodecSelector, paramFormat, paramBoolean);
  }
  
  public MediaClock getMediaClock()
  {
    return this;
  }
  
  protected MediaFormat getMediaFormat(Format paramFormat, String paramString, int paramInt, float paramFloat)
  {
    MediaFormat localMediaFormat = new MediaFormat();
    localMediaFormat.setString("mime", paramString);
    localMediaFormat.setInteger("channel-count", channelCount);
    localMediaFormat.setInteger("sample-rate", sampleRate);
    MediaFormatUtil.setCsdBuffers(localMediaFormat, initializationData);
    MediaFormatUtil.maybeSetInteger(localMediaFormat, "max-input-size", paramInt);
    if (Util.SDK_INT >= 23)
    {
      localMediaFormat.setInteger("priority", 0);
      if (paramFloat != -1.0F) {
        localMediaFormat.setFloat("operating-rate", paramFloat);
      }
    }
    return localMediaFormat;
  }
  
  public PlaybackParameters getPlaybackParameters()
  {
    return audioSink.getPlaybackParameters();
  }
  
  public long getPositionUs()
  {
    if (getState() == 2) {
      updateCurrentPosition();
    }
    return currentPositionUs;
  }
  
  public void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {
    if (paramInt != 5)
    {
      switch (paramInt)
      {
      default: 
        super.handleMessage(paramInt, paramObject);
        return;
      case 3: 
        paramObject = (AudioAttributes)paramObject;
        audioSink.setAudioAttributes(paramObject);
        return;
      }
      audioSink.setVolume(((Float)paramObject).floatValue());
      return;
    }
    paramObject = (AuxEffectInfo)paramObject;
    audioSink.setAuxEffectInfo(paramObject);
  }
  
  public boolean isEnded()
  {
    return (super.isEnded()) && (audioSink.isEnded());
  }
  
  public boolean isReady()
  {
    return (audioSink.hasPendingData()) || (super.isReady());
  }
  
  protected void onAudioSessionId(int paramInt) {}
  
  protected void onAudioTrackPositionDiscontinuity() {}
  
  protected void onAudioTrackUnderrun(int paramInt, long paramLong1, long paramLong2) {}
  
  protected void onCodecInitialized(String paramString, long paramLong1, long paramLong2)
  {
    eventDispatcher.decoderInitialized(paramString, paramLong1, paramLong2);
  }
  
  /* Error */
  protected void onDisabled()
  {
    // Byte code:
    //   0: aload_0
    //   1: ldc2_w 84
    //   4: putfield 87	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:lastInputTimeUs	J
    //   7: aload_0
    //   8: iconst_0
    //   9: putfield 410	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:pendingStreamChangeCount	I
    //   12: aload_0
    //   13: getfield 83	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:audioSink	Lcom/google/android/exoplayer2/audio/AudioSink;
    //   16: invokeinterface 413 1 0
    //   21: aload_0
    //   22: invokespecial 415	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:onDisabled	()V
    //   25: aload_0
    //   26: getfield 419	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   29: invokevirtual 424	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   32: aload_0
    //   33: getfield 96	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   36: aload_0
    //   37: getfield 419	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   40: invokevirtual 428	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   43: return
    //   44: astore_1
    //   45: aload_0
    //   46: getfield 419	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   49: invokevirtual 424	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   52: aload_0
    //   53: getfield 96	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   56: aload_0
    //   57: getfield 419	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   60: invokevirtual 428	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   63: aload_1
    //   64: athrow
    //   65: astore_1
    //   66: aload_0
    //   67: invokespecial 415	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:onDisabled	()V
    //   70: aload_0
    //   71: getfield 419	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   74: invokevirtual 424	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   77: aload_0
    //   78: getfield 96	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   81: aload_0
    //   82: getfield 419	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   85: invokevirtual 428	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   88: aload_1
    //   89: athrow
    //   90: astore_1
    //   91: aload_0
    //   92: getfield 419	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   95: invokevirtual 424	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   98: aload_0
    //   99: getfield 96	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   102: aload_0
    //   103: getfield 419	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   106: invokevirtual 428	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   109: aload_1
    //   110: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	111	0	this	MediaCodecAudioRenderer
    //   44	20	1	localThrowable1	Throwable
    //   65	24	1	localThrowable2	Throwable
    //   90	20	1	localThrowable3	Throwable
    // Exception table:
    //   from	to	target	type
    //   21	25	44	java/lang/Throwable
    //   0	21	65	java/lang/Throwable
    //   66	70	90	java/lang/Throwable
  }
  
  protected void onEnabled(boolean paramBoolean)
    throws ExoPlaybackException
  {
    super.onEnabled(paramBoolean);
    eventDispatcher.enabled(decoderCounters);
    int i = getConfigurationtunnelingAudioSessionId;
    if (i != 0)
    {
      audioSink.enableTunnelingV21(i);
      return;
    }
    audioSink.disableTunneling();
  }
  
  protected void onInputFormatChanged(Format paramFormat)
    throws ExoPlaybackException
  {
    super.onInputFormatChanged(paramFormat);
    eventDispatcher.inputFormatChanged(paramFormat);
    int i;
    if ("audio/raw".equals(sampleMimeType)) {
      i = pcmEncoding;
    } else {
      i = 2;
    }
    pcmEncoding = i;
    channelCount = channelCount;
    encoderDelay = encoderDelay;
    encoderPadding = encoderPadding;
  }
  
  protected void onOutputFormatChanged(MediaCodec paramMediaCodec, MediaFormat paramMediaFormat)
    throws ExoPlaybackException
  {
    int i;
    if (passthroughMediaFormat != null)
    {
      i = MimeTypes.getEncoding(passthroughMediaFormat.getString("mime"));
      paramMediaFormat = passthroughMediaFormat;
    }
    for (;;)
    {
      break;
      i = pcmEncoding;
    }
    int k = paramMediaFormat.getInteger("channel-count");
    int m = paramMediaFormat.getInteger("sample-rate");
    if ((codecNeedsDiscardChannelsWorkaround) && (k == 6) && (channelCount < 6))
    {
      paramMediaFormat = new int[channelCount];
      j = 0;
      for (;;)
      {
        paramMediaCodec = paramMediaFormat;
        if (j >= channelCount) {
          break;
        }
        paramMediaFormat[j] = j;
        j += 1;
      }
    }
    paramMediaCodec = null;
    paramMediaFormat = audioSink;
    int j = encoderDelay;
    int n = encoderPadding;
    try
    {
      paramMediaFormat.configure(i, k, m, 0, paramMediaCodec, j, n);
      return;
    }
    catch (AudioSink.ConfigurationException paramMediaCodec)
    {
      throw ExoPlaybackException.createForRenderer(paramMediaCodec, getIndex());
    }
  }
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    super.onPositionReset(paramLong, paramBoolean);
    audioSink.reset();
    currentPositionUs = paramLong;
    allowFirstBufferPositionDiscontinuity = true;
    allowPositionDiscontinuity = true;
    lastInputTimeUs = -9223372036854775807L;
    pendingStreamChangeCount = 0;
  }
  
  protected void onProcessedOutputBuffer(long paramLong)
  {
    while ((pendingStreamChangeCount != 0) && (paramLong >= pendingStreamChangeTimesUs[0]))
    {
      audioSink.handleDiscontinuity();
      pendingStreamChangeCount -= 1;
      System.arraycopy(pendingStreamChangeTimesUs, 1, pendingStreamChangeTimesUs, 0, pendingStreamChangeCount);
    }
  }
  
  protected void onQueueInputBuffer(DecoderInputBuffer paramDecoderInputBuffer)
  {
    if ((allowFirstBufferPositionDiscontinuity) && (!paramDecoderInputBuffer.isDecodeOnly()))
    {
      if (Math.abs(timeUs - currentPositionUs) > 500000L) {
        currentPositionUs = timeUs;
      }
      allowFirstBufferPositionDiscontinuity = false;
    }
    lastInputTimeUs = Math.max(timeUs, lastInputTimeUs);
  }
  
  protected void onStarted()
  {
    super.onStarted();
    audioSink.play();
  }
  
  protected void onStopped()
  {
    updateCurrentPosition();
    audioSink.pause();
    super.onStopped();
  }
  
  protected void onStreamChanged(Format[] paramArrayOfFormat, long paramLong)
    throws ExoPlaybackException
  {
    super.onStreamChanged(paramArrayOfFormat, paramLong);
    if (lastInputTimeUs != -9223372036854775807L)
    {
      if (pendingStreamChangeCount == pendingStreamChangeTimesUs.length)
      {
        paramArrayOfFormat = new StringBuilder();
        paramArrayOfFormat.append("Too many stream changes, so dropping change at ");
        paramArrayOfFormat.append(pendingStreamChangeTimesUs[(pendingStreamChangeCount - 1)]);
        Log.w("MediaCodecAudioRenderer", paramArrayOfFormat.toString());
      }
      else
      {
        pendingStreamChangeCount += 1;
      }
      pendingStreamChangeTimesUs[(pendingStreamChangeCount - 1)] = lastInputTimeUs;
    }
  }
  
  protected boolean processOutputBuffer(long paramLong1, long paramLong2, MediaCodec paramMediaCodec, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, long paramLong3, boolean paramBoolean, Format paramFormat)
    throws ExoPlaybackException
  {
    paramLong1 = paramLong3;
    if (codecNeedsEosBufferTimestampWorkaround)
    {
      paramLong1 = paramLong3;
      if (paramLong3 == 0L)
      {
        paramLong1 = paramLong3;
        if ((paramInt2 & 0x4) != 0)
        {
          paramLong1 = paramLong3;
          if (lastInputTimeUs != -9223372036854775807L) {
            paramLong1 = lastInputTimeUs;
          }
        }
      }
    }
    if ((passthroughEnabled) && ((paramInt2 & 0x2) != 0))
    {
      paramMediaCodec.releaseOutputBuffer(paramInt1, false);
      return true;
    }
    if (paramBoolean)
    {
      paramMediaCodec.releaseOutputBuffer(paramInt1, false);
      paramMediaCodec = decoderCounters;
      skippedOutputBufferCount += 1;
      audioSink.handleDiscontinuity();
      return true;
    }
    paramFormat = audioSink;
    try
    {
      paramBoolean = paramFormat.handleBuffer(paramByteBuffer, paramLong1);
      if (paramBoolean)
      {
        paramMediaCodec.releaseOutputBuffer(paramInt1, false);
        paramMediaCodec = decoderCounters;
        renderedOutputBufferCount += 1;
        return true;
      }
      return false;
    }
    catch (AudioSink.InitializationException|AudioSink.WriteException paramMediaCodec)
    {
      throw ExoPlaybackException.createForRenderer(paramMediaCodec, getIndex());
    }
  }
  
  protected void renderToEndOfStream()
    throws ExoPlaybackException
  {
    AudioSink localAudioSink = audioSink;
    try
    {
      localAudioSink.playToEndOfStream();
      return;
    }
    catch (AudioSink.WriteException localWriteException)
    {
      throw ExoPlaybackException.createForRenderer(localWriteException, getIndex());
    }
  }
  
  public PlaybackParameters setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    return audioSink.setPlaybackParameters(paramPlaybackParameters);
  }
  
  protected int supportsFormat(MediaCodecSelector paramMediaCodecSelector, DrmSessionManager paramDrmSessionManager, Format paramFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    String str = sampleMimeType;
    if (!MimeTypes.isAudio(str)) {
      return 0;
    }
    int i;
    if (Util.SDK_INT >= 21) {
      i = 32;
    } else {
      i = 0;
    }
    boolean bool3 = BaseRenderer.supportsFormatDrm(paramDrmSessionManager, drmInitData);
    int k = 4;
    int m = 8;
    if ((bool3) && (allowPassthrough(channelCount, str)) && (paramMediaCodecSelector.getPassthroughDecoderInfo() != null)) {
      return i | 0x8 | 0x4;
    }
    if ((!"audio/raw".equals(str)) || (audioSink.supportsOutput(channelCount, pcmEncoding)))
    {
      if (!audioSink.supportsOutput(channelCount, 2)) {
        return 1;
      }
      paramDrmSessionManager = drmInitData;
      int j;
      boolean bool1;
      if (paramDrmSessionManager != null)
      {
        j = 0;
        bool1 = false;
        for (;;)
        {
          bool2 = bool1;
          if (j >= schemeDataCount) {
            break;
          }
          bool1 |= getLanguagerequiresSecureDecryption;
          j += 1;
        }
      }
      boolean bool2 = false;
      paramDrmSessionManager = paramMediaCodecSelector.getDecoderInfos(sampleMimeType, bool2);
      if (paramDrmSessionManager.isEmpty())
      {
        if ((bool2) && (!paramMediaCodecSelector.getDecoderInfos(sampleMimeType, false).isEmpty())) {
          return 2;
        }
      }
      else
      {
        if (!bool3) {
          return 2;
        }
        paramMediaCodecSelector = (MediaCodecInfo)paramDrmSessionManager.get(0);
        bool1 = paramMediaCodecSelector.isFormatSupported(paramFormat);
        j = m;
        if (bool1)
        {
          j = m;
          if (paramMediaCodecSelector.isSeamlessAdaptationSupported(paramFormat)) {
            j = 16;
          }
        }
        if (!bool1) {
          k = 3;
        }
        return j | i | k;
      }
    }
    return 1;
  }
  
  private final class AudioSinkListener
    implements AudioSink.Listener
  {
    private AudioSinkListener() {}
    
    public void onAudioSessionId(int paramInt)
    {
      eventDispatcher.audioSessionId(paramInt);
      MediaCodecAudioRenderer.this.onAudioSessionId(paramInt);
    }
    
    public void onPositionDiscontinuity()
    {
      onAudioTrackPositionDiscontinuity();
      MediaCodecAudioRenderer.access$202(MediaCodecAudioRenderer.this, true);
    }
    
    public void onUnderrun(int paramInt, long paramLong1, long paramLong2)
    {
      eventDispatcher.audioTrackUnderrun(paramInt, paramLong1, paramLong2);
      onAudioTrackUnderrun(paramInt, paramLong1, paramLong2);
    }
  }
}
