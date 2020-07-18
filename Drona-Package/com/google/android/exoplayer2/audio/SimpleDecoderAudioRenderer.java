package com.google.android.exoplayer2.audio;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.decoder.Buffer;
import com.google.android.exoplayer2.decoder.Decoder;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.decoder.OutputBuffer;
import com.google.android.exoplayer2.decoder.SimpleDecoder;
import com.google.android.exoplayer2.decoder.SimpleOutputBuffer;
import com.google.android.exoplayer2.upgrade.DrmInitData;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MediaClock;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;

public abstract class SimpleDecoderAudioRenderer
  extends BaseRenderer
  implements MediaClock
{
  private static final int REINITIALIZATION_STATE_NONE = 0;
  private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
  private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
  private boolean allowFirstBufferPositionDiscontinuity;
  private boolean allowPositionDiscontinuity;
  private final AudioSink audioSink;
  private boolean audioTrackNeedsConfigure;
  private long currentPositionUs;
  private SimpleDecoder<DecoderInputBuffer, ? extends SimpleOutputBuffer, ? extends AudioDecoderException> decoder;
  private DecoderCounters decoderCounters;
  private boolean decoderReceivedBuffers;
  private int decoderReinitializationState;
  private com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.ExoMediaCrypto> drmSession;
  private final com.google.android.exoplayer2.drm.DrmSessionManager<com.google.android.exoplayer2.drm.ExoMediaCrypto> drmSessionManager;
  private int encoderDelay;
  private int encoderPadding;
  private final AudioRendererEventListener.EventDispatcher eventDispatcher;
  private final DecoderInputBuffer flagsOnlyBuffer;
  private final FormatHolder formatHolder;
  private DecoderInputBuffer inputBuffer;
  private Format inputFormat;
  private boolean inputStreamEnded;
  private SimpleOutputBuffer outputBuffer;
  private boolean outputStreamEnded;
  private com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.ExoMediaCrypto> pendingDrmSession;
  private final boolean playClearSamplesWithoutKeys;
  private boolean waitingForKeys;
  
  public SimpleDecoderAudioRenderer()
  {
    this(null, null, new AudioProcessor[0]);
  }
  
  public SimpleDecoderAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioCapabilities paramAudioCapabilities)
  {
    this(paramHandler, paramAudioRendererEventListener, paramAudioCapabilities, null, false, new AudioProcessor[0]);
  }
  
  public SimpleDecoderAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioCapabilities paramAudioCapabilities, com.google.android.exoplayer2.upgrade.DrmSessionManager paramDrmSessionManager, boolean paramBoolean, AudioProcessor... paramVarArgs)
  {
    this(paramHandler, paramAudioRendererEventListener, paramDrmSessionManager, paramBoolean, new DefaultAudioSink(paramAudioCapabilities, paramVarArgs));
  }
  
  public SimpleDecoderAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, com.google.android.exoplayer2.upgrade.DrmSessionManager paramDrmSessionManager, boolean paramBoolean, AudioSink paramAudioSink)
  {
    super(1);
    drmSessionManager = paramDrmSessionManager;
    playClearSamplesWithoutKeys = paramBoolean;
    eventDispatcher = new AudioRendererEventListener.EventDispatcher(paramHandler, paramAudioRendererEventListener);
    audioSink = paramAudioSink;
    paramAudioSink.setListener(new AudioSinkListener(null));
    formatHolder = new FormatHolder();
    flagsOnlyBuffer = DecoderInputBuffer.newFlagsOnlyInstance();
    decoderReinitializationState = 0;
    audioTrackNeedsConfigure = true;
  }
  
  public SimpleDecoderAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioProcessor... paramVarArgs)
  {
    this(paramHandler, paramAudioRendererEventListener, null, null, false, paramVarArgs);
  }
  
  private boolean drainOutputBuffer()
    throws ExoPlaybackException, AudioDecoderException, AudioSink.ConfigurationException, AudioSink.InitializationException, AudioSink.WriteException
  {
    Object localObject;
    if (outputBuffer == null)
    {
      outputBuffer = ((SimpleOutputBuffer)decoder.dequeueOutputBuffer());
      if (outputBuffer == null) {
        return false;
      }
      localObject = decoderCounters;
      skippedOutputBufferCount += outputBuffer.skippedOutputBufferCount;
    }
    if (outputBuffer.isEndOfStream())
    {
      if (decoderReinitializationState == 2)
      {
        releaseDecoder();
        maybeInitDecoder();
        audioTrackNeedsConfigure = true;
        return false;
      }
      outputBuffer.release();
      outputBuffer = null;
      processEndOfStream();
      return false;
    }
    if (audioTrackNeedsConfigure)
    {
      localObject = getOutputFormat();
      audioSink.configure(pcmEncoding, channelCount, sampleRate, 0, null, encoderDelay, encoderPadding);
      audioTrackNeedsConfigure = false;
    }
    if (audioSink.handleBuffer(outputBuffer.data, outputBuffer.timeUs))
    {
      localObject = decoderCounters;
      renderedOutputBufferCount += 1;
      outputBuffer.release();
      outputBuffer = null;
      return true;
    }
    return false;
  }
  
  private boolean feedInputBuffer()
    throws AudioDecoderException, ExoPlaybackException
  {
    if ((decoder != null) && (decoderReinitializationState != 2))
    {
      if (inputStreamEnded) {
        return false;
      }
      if (inputBuffer == null)
      {
        inputBuffer = decoder.dequeueInputBuffer();
        if (inputBuffer == null) {
          return false;
        }
      }
      if (decoderReinitializationState == 1)
      {
        inputBuffer.setFlags(4);
        decoder.queueInputBuffer(inputBuffer);
        inputBuffer = null;
        decoderReinitializationState = 2;
        return false;
      }
      int i;
      if (waitingForKeys) {
        i = -4;
      } else {
        i = readSource(formatHolder, inputBuffer, false);
      }
      if (i == -3) {
        return false;
      }
      if (i == -5)
      {
        onInputFormatChanged(formatHolder.format);
        return true;
      }
      if (inputBuffer.isEndOfStream())
      {
        inputStreamEnded = true;
        decoder.queueInputBuffer(inputBuffer);
        inputBuffer = null;
        return false;
      }
      waitingForKeys = shouldWaitForKeys(inputBuffer.isEncrypted());
      if (waitingForKeys) {
        return false;
      }
      inputBuffer.flip();
      onQueueInputBuffer(inputBuffer);
      decoder.queueInputBuffer(inputBuffer);
      decoderReceivedBuffers = true;
      DecoderCounters localDecoderCounters = decoderCounters;
      inputBufferCount += 1;
      inputBuffer = null;
      return true;
    }
    return false;
  }
  
  private void flushDecoder()
    throws ExoPlaybackException
  {
    waitingForKeys = false;
    if (decoderReinitializationState != 0)
    {
      releaseDecoder();
      maybeInitDecoder();
      return;
    }
    inputBuffer = null;
    if (outputBuffer != null)
    {
      outputBuffer.release();
      outputBuffer = null;
    }
    decoder.flush();
    decoderReceivedBuffers = false;
  }
  
  private void maybeInitDecoder()
    throws ExoPlaybackException
  {
    if (decoder != null) {
      return;
    }
    drmSession = pendingDrmSession;
    Object localObject1 = null;
    Object localObject2;
    if (drmSession != null)
    {
      com.google.android.exoplayer2.upgrade.ExoMediaCrypto localExoMediaCrypto = drmSession.getMediaCrypto();
      localObject2 = localExoMediaCrypto;
      localObject1 = localObject2;
      if (localExoMediaCrypto == null) {
        if (drmSession.getError() != null) {
          localObject1 = localObject2;
        } else {
          return;
        }
      }
    }
    try
    {
      long l1 = SystemClock.elapsedRealtime();
      TraceUtil.beginSection("createAudioDecoder");
      localObject2 = inputFormat;
      localObject1 = createDecoder((Format)localObject2, (com.google.android.exoplayer2.upgrade.ExoMediaCrypto)localObject1);
      decoder = ((SimpleDecoder)localObject1);
      TraceUtil.endSection();
      long l2 = SystemClock.elapsedRealtime();
      localObject1 = eventDispatcher;
      localObject2 = decoder;
      localObject2 = ((Decoder)localObject2).getName();
      ((AudioRendererEventListener.EventDispatcher)localObject1).decoderInitialized((String)localObject2, l2, l2 - l1);
      localObject1 = decoderCounters;
      decoderInitCount += 1;
      return;
    }
    catch (AudioDecoderException localAudioDecoderException)
    {
      throw ExoPlaybackException.createForRenderer(localAudioDecoderException, getIndex());
    }
  }
  
  private void onInputFormatChanged(Format paramFormat)
    throws ExoPlaybackException
  {
    Object localObject = inputFormat;
    inputFormat = paramFormat;
    DrmInitData localDrmInitData = inputFormat.drmInitData;
    if (localObject == null) {
      localObject = null;
    } else {
      localObject = drmInitData;
    }
    if ((Util.areEqual(localDrmInitData, localObject) ^ true)) {
      if (inputFormat.drmInitData != null)
      {
        if (drmSessionManager != null)
        {
          pendingDrmSession = drmSessionManager.acquireSession(Looper.myLooper(), inputFormat.drmInitData);
          if (pendingDrmSession == drmSession) {
            drmSessionManager.releaseSession(pendingDrmSession);
          }
        }
        else
        {
          throw ExoPlaybackException.createForRenderer(new IllegalStateException("Media requires a DrmSessionManager"), getIndex());
        }
      }
      else {
        pendingDrmSession = null;
      }
    }
    if (decoderReceivedBuffers)
    {
      decoderReinitializationState = 1;
    }
    else
    {
      releaseDecoder();
      maybeInitDecoder();
      audioTrackNeedsConfigure = true;
    }
    encoderDelay = encoderDelay;
    encoderPadding = encoderPadding;
    eventDispatcher.inputFormatChanged(paramFormat);
  }
  
  private void onQueueInputBuffer(DecoderInputBuffer paramDecoderInputBuffer)
  {
    if ((allowFirstBufferPositionDiscontinuity) && (!paramDecoderInputBuffer.isDecodeOnly()))
    {
      if (Math.abs(timeUs - currentPositionUs) > 500000L) {
        currentPositionUs = timeUs;
      }
      allowFirstBufferPositionDiscontinuity = false;
    }
  }
  
  private void processEndOfStream()
    throws ExoPlaybackException
  {
    outputStreamEnded = true;
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
  
  private void releaseDecoder()
  {
    if (decoder == null) {
      return;
    }
    inputBuffer = null;
    outputBuffer = null;
    decoder.release();
    decoder = null;
    DecoderCounters localDecoderCounters = decoderCounters;
    decoderReleaseCount += 1;
    decoderReinitializationState = 0;
    decoderReceivedBuffers = false;
  }
  
  private boolean shouldWaitForKeys(boolean paramBoolean)
    throws ExoPlaybackException
  {
    if (drmSession != null)
    {
      if ((!paramBoolean) && (playClearSamplesWithoutKeys)) {
        return false;
      }
      int i = drmSession.getState();
      if (i != 1) {
        return i != 4;
      }
      throw ExoPlaybackException.createForRenderer(drmSession.getError(), getIndex());
    }
    return false;
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
  
  protected abstract SimpleDecoder createDecoder(Format paramFormat, com.google.android.exoplayer2.upgrade.ExoMediaCrypto paramExoMediaCrypto)
    throws AudioDecoderException;
  
  public MediaClock getMediaClock()
  {
    return this;
  }
  
  protected Format getOutputFormat()
  {
    return Format.createAudioSampleFormat(null, "audio/raw", null, -1, -1, inputFormat.channelCount, inputFormat.sampleRate, 2, null, null, 0, null);
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
    return (outputStreamEnded) && (audioSink.isEnded());
  }
  
  public boolean isReady()
  {
    return (audioSink.hasPendingData()) || ((inputFormat != null) && (!waitingForKeys) && ((isSourceReady()) || (outputBuffer != null)));
  }
  
  protected void onAudioSessionId(int paramInt) {}
  
  protected void onAudioTrackPositionDiscontinuity() {}
  
  protected void onAudioTrackUnderrun(int paramInt, long paramLong1, long paramLong2) {}
  
  /* Error */
  protected void onDisabled()
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 295	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:inputFormat	Lcom/google/android/exoplayer2/Format;
    //   5: aload_0
    //   6: iconst_1
    //   7: putfield 120	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:audioTrackNeedsConfigure	Z
    //   10: aload_0
    //   11: iconst_0
    //   12: putfield 232	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:waitingForKeys	Z
    //   15: aload_0
    //   16: invokespecial 168	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:releaseDecoder	()V
    //   19: aload_0
    //   20: getfield 93	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:audioSink	Lcom/google/android/exoplayer2/audio/AudioSink;
    //   23: invokeinterface 460 1 0
    //   28: aload_0
    //   29: getfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   32: astore_1
    //   33: aload_1
    //   34: ifnull +16 -> 50
    //   37: aload_0
    //   38: getfield 82	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   41: aload_0
    //   42: getfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   45: invokeinterface 349 2 0
    //   50: aload_0
    //   51: getfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   54: astore_1
    //   55: aload_1
    //   56: ifnull +31 -> 87
    //   59: aload_0
    //   60: getfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   63: astore_1
    //   64: aload_0
    //   65: getfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   68: astore_2
    //   69: aload_1
    //   70: aload_2
    //   71: if_acmpeq +16 -> 87
    //   74: aload_0
    //   75: getfield 82	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   78: aload_0
    //   79: getfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   82: invokeinterface 349 2 0
    //   87: aload_0
    //   88: aconst_null
    //   89: putfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   92: aload_0
    //   93: aconst_null
    //   94: putfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   97: aload_0
    //   98: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   101: invokevirtual 463	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   104: aload_0
    //   105: getfield 91	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   108: aload_0
    //   109: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   112: invokevirtual 467	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   115: return
    //   116: astore_1
    //   117: aload_0
    //   118: aconst_null
    //   119: putfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   122: aload_0
    //   123: aconst_null
    //   124: putfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   127: aload_0
    //   128: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   131: invokevirtual 463	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   134: aload_0
    //   135: getfield 91	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   138: aload_0
    //   139: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   142: invokevirtual 467	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   145: aload_1
    //   146: athrow
    //   147: astore_1
    //   148: aload_0
    //   149: getfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   152: astore_2
    //   153: aload_2
    //   154: ifnull +31 -> 185
    //   157: aload_0
    //   158: getfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   161: astore_2
    //   162: aload_0
    //   163: getfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   166: astore_3
    //   167: aload_2
    //   168: aload_3
    //   169: if_acmpeq +16 -> 185
    //   172: aload_0
    //   173: getfield 82	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   176: aload_0
    //   177: getfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   180: invokeinterface 349 2 0
    //   185: aload_0
    //   186: aconst_null
    //   187: putfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   190: aload_0
    //   191: aconst_null
    //   192: putfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   195: aload_0
    //   196: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   199: invokevirtual 463	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   202: aload_0
    //   203: getfield 91	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   206: aload_0
    //   207: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   210: invokevirtual 467	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   213: aload_1
    //   214: athrow
    //   215: astore_1
    //   216: aload_0
    //   217: aconst_null
    //   218: putfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   221: aload_0
    //   222: aconst_null
    //   223: putfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   226: aload_0
    //   227: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   230: invokevirtual 463	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   233: aload_0
    //   234: getfield 91	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   237: aload_0
    //   238: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   241: invokevirtual 467	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   244: aload_1
    //   245: athrow
    //   246: astore_1
    //   247: aload_0
    //   248: getfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   251: astore_2
    //   252: aload_2
    //   253: ifnull +16 -> 269
    //   256: aload_0
    //   257: getfield 82	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   260: aload_0
    //   261: getfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   264: invokeinterface 349 2 0
    //   269: aload_0
    //   270: getfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   273: astore_2
    //   274: aload_2
    //   275: ifnull +31 -> 306
    //   278: aload_0
    //   279: getfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   282: astore_2
    //   283: aload_0
    //   284: getfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   287: astore_3
    //   288: aload_2
    //   289: aload_3
    //   290: if_acmpeq +16 -> 306
    //   293: aload_0
    //   294: getfield 82	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   297: aload_0
    //   298: getfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   301: invokeinterface 349 2 0
    //   306: aload_0
    //   307: aconst_null
    //   308: putfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   311: aload_0
    //   312: aconst_null
    //   313: putfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   316: aload_0
    //   317: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   320: invokevirtual 463	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   323: aload_0
    //   324: getfield 91	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   327: aload_0
    //   328: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   331: invokevirtual 467	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   334: aload_1
    //   335: athrow
    //   336: astore_1
    //   337: aload_0
    //   338: aconst_null
    //   339: putfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   342: aload_0
    //   343: aconst_null
    //   344: putfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   347: aload_0
    //   348: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   351: invokevirtual 463	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   354: aload_0
    //   355: getfield 91	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   358: aload_0
    //   359: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   362: invokevirtual 467	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   365: aload_1
    //   366: athrow
    //   367: astore_1
    //   368: aload_0
    //   369: getfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   372: astore_2
    //   373: aload_2
    //   374: ifnull +31 -> 405
    //   377: aload_0
    //   378: getfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   381: astore_2
    //   382: aload_0
    //   383: getfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   386: astore_3
    //   387: aload_2
    //   388: aload_3
    //   389: if_acmpeq +16 -> 405
    //   392: aload_0
    //   393: getfield 82	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   396: aload_0
    //   397: getfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   400: invokeinterface 349 2 0
    //   405: aload_0
    //   406: aconst_null
    //   407: putfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   410: aload_0
    //   411: aconst_null
    //   412: putfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   415: aload_0
    //   416: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   419: invokevirtual 463	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   422: aload_0
    //   423: getfield 91	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   426: aload_0
    //   427: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   430: invokevirtual 467	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   433: aload_1
    //   434: athrow
    //   435: astore_1
    //   436: aload_0
    //   437: aconst_null
    //   438: putfield 269	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   441: aload_0
    //   442: aconst_null
    //   443: putfield 267	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   446: aload_0
    //   447: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   450: invokevirtual 463	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   453: aload_0
    //   454: getfield 91	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   457: aload_0
    //   458: getfield 152	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   461: invokevirtual 467	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   464: aload_1
    //   465: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	466	0	this	SimpleDecoderAudioRenderer
    //   32	38	1	localDrmSession1	com.google.android.exoplayer2.upgrade.DrmSession
    //   116	30	1	localThrowable1	Throwable
    //   147	67	1	localThrowable2	Throwable
    //   215	30	1	localThrowable3	Throwable
    //   246	89	1	localThrowable4	Throwable
    //   336	30	1	localThrowable5	Throwable
    //   367	67	1	localThrowable6	Throwable
    //   435	30	1	localThrowable7	Throwable
    //   68	320	2	localDrmSession2	com.google.android.exoplayer2.upgrade.DrmSession
    //   166	223	3	localDrmSession3	com.google.android.exoplayer2.upgrade.DrmSession
    // Exception table:
    //   from	to	target	type
    //   50	55	116	java/lang/Throwable
    //   59	69	116	java/lang/Throwable
    //   74	87	116	java/lang/Throwable
    //   28	33	147	java/lang/Throwable
    //   37	50	147	java/lang/Throwable
    //   148	153	215	java/lang/Throwable
    //   157	167	215	java/lang/Throwable
    //   172	185	215	java/lang/Throwable
    //   15	28	246	java/lang/Throwable
    //   269	274	336	java/lang/Throwable
    //   278	288	336	java/lang/Throwable
    //   293	306	336	java/lang/Throwable
    //   247	252	367	java/lang/Throwable
    //   256	269	367	java/lang/Throwable
    //   368	373	435	java/lang/Throwable
    //   377	387	435	java/lang/Throwable
    //   392	405	435	java/lang/Throwable
  }
  
  protected void onEnabled(boolean paramBoolean)
    throws ExoPlaybackException
  {
    decoderCounters = new DecoderCounters();
    eventDispatcher.enabled(decoderCounters);
    int i = getConfigurationtunnelingAudioSessionId;
    if (i != 0)
    {
      audioSink.enableTunnelingV21(i);
      return;
    }
    audioSink.disableTunneling();
  }
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    audioSink.reset();
    currentPositionUs = paramLong;
    allowFirstBufferPositionDiscontinuity = true;
    allowPositionDiscontinuity = true;
    inputStreamEnded = false;
    outputStreamEnded = false;
    if (decoder != null) {
      flushDecoder();
    }
  }
  
  protected void onStarted()
  {
    audioSink.play();
  }
  
  protected void onStopped()
  {
    updateCurrentPosition();
    audioSink.pause();
  }
  
  public void render(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    if (outputStreamEnded)
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
    if (inputFormat == null)
    {
      flagsOnlyBuffer.clear();
      int i = readSource(formatHolder, flagsOnlyBuffer, true);
      if (i == -5)
      {
        onInputFormatChanged(formatHolder.format);
      }
      else
      {
        if (i == -4)
        {
          Assertions.checkState(flagsOnlyBuffer.isEndOfStream());
          inputStreamEnded = true;
          processEndOfStream();
        }
        return;
      }
    }
    maybeInitDecoder();
    if (decoder != null) {
      try
      {
        TraceUtil.beginSection("drainAndFeed");
        boolean bool;
        do
        {
          bool = drainOutputBuffer();
        } while (bool);
        do
        {
          bool = feedInputBuffer();
        } while (bool);
        TraceUtil.endSection();
        decoderCounters.ensureUpdated();
        return;
      }
      catch (AudioDecoderException|AudioSink.ConfigurationException|AudioSink.InitializationException|AudioSink.WriteException localAudioDecoderException)
      {
        throw ExoPlaybackException.createForRenderer(localAudioDecoderException, getIndex());
      }
    }
  }
  
  public PlaybackParameters setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    return audioSink.setPlaybackParameters(paramPlaybackParameters);
  }
  
  public final int supportsFormat(Format paramFormat)
  {
    boolean bool = MimeTypes.isAudio(sampleMimeType);
    int i = 0;
    if (!bool) {
      return 0;
    }
    int j = supportsFormatInternal(drmSessionManager, paramFormat);
    if (j <= 2) {
      return j;
    }
    if (Util.SDK_INT >= 21) {
      i = 32;
    }
    return j | i | 0x8;
  }
  
  protected abstract int supportsFormatInternal(com.google.android.exoplayer2.upgrade.DrmSessionManager paramDrmSessionManager, Format paramFormat);
  
  protected final boolean supportsOutput(int paramInt1, int paramInt2)
  {
    return audioSink.supportsOutput(paramInt1, paramInt2);
  }
  
  private final class AudioSinkListener
    implements AudioSink.Listener
  {
    private AudioSinkListener() {}
    
    public void onAudioSessionId(int paramInt)
    {
      eventDispatcher.audioSessionId(paramInt);
      SimpleDecoderAudioRenderer.this.onAudioSessionId(paramInt);
    }
    
    public void onPositionDiscontinuity()
    {
      onAudioTrackPositionDiscontinuity();
      SimpleDecoderAudioRenderer.access$202(SimpleDecoderAudioRenderer.this, true);
    }
    
    public void onUnderrun(int paramInt, long paramLong1, long paramLong2)
    {
      eventDispatcher.audioTrackUnderrun(paramInt, paramLong1, paramLong2);
      onAudioTrackUnderrun(paramInt, paramLong1, paramLong2);
    }
  }
}
