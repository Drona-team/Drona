package com.google.android.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.CodecException;
import android.media.MediaCodec.CryptoException;
import android.media.MediaCodec.CryptoInfo;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.Buffer;
import com.google.android.exoplayer2.decoder.CryptoInfo;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.TimedValueQueue;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

@TargetApi(16)
public abstract class MediaCodecRenderer
  extends BaseRenderer
{
  private static final byte[] ADAPTATION_WORKAROUND_BUFFER = Util.getBytesFromHexString("0000016742C00BDA259000000168CE0F13200000016588840DCE7118A0002FBF1C31C3275D78");
  private static final int ADAPTATION_WORKAROUND_MODE_ALWAYS = 2;
  private static final int ADAPTATION_WORKAROUND_MODE_NEVER = 0;
  private static final int ADAPTATION_WORKAROUND_MODE_SAME_RESOLUTION = 1;
  private static final int ADAPTATION_WORKAROUND_SLICE_WIDTH_HEIGHT = 32;
  protected static final float CODEC_OPERATING_RATE_UNSET = -1.0F;
  protected static final int KEEP_CODEC_RESULT_NO = 0;
  protected static final int KEEP_CODEC_RESULT_YES_WITHOUT_RECONFIGURATION = 1;
  protected static final int KEEP_CODEC_RESULT_YES_WITH_RECONFIGURATION = 3;
  private static final long MAX_CODEC_HOTSWAP_TIME_MS = 1000L;
  private static final String PAGE_KEY = "MediaCodecRenderer";
  private static final int RECONFIGURATION_STATE_NONE = 0;
  private static final int RECONFIGURATION_STATE_QUEUE_PENDING = 2;
  private static final int RECONFIGURATION_STATE_WRITE_PENDING = 1;
  private static final int REINITIALIZATION_STATE_NONE = 0;
  private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
  private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
  private final float assumedMinimumCodecOperatingRate;
  @Nullable
  private ArrayDeque<MediaCodecInfo> availableCodecInfos;
  private final DecoderInputBuffer buffer;
  private MediaCodec codec;
  private int codecAdaptationWorkaroundMode;
  private boolean codecConfiguredWithOperatingRate;
  private long codecHotswapDeadlineMs;
  @Nullable
  private MediaCodecInfo codecInfo;
  private boolean codecNeedsAdaptationWorkaroundBuffer;
  private boolean codecNeedsDiscardToSpsWorkaround;
  private boolean codecNeedsEosFlushWorkaround;
  private boolean codecNeedsEosOutputExceptionWorkaround;
  private boolean codecNeedsEosPropagation;
  private boolean codecNeedsFlushWorkaround;
  private boolean codecNeedsMonoChannelCountWorkaround;
  private boolean codecNeedsReconfigureWorkaround;
  private float codecOperatingRate;
  private boolean codecReceivedBuffers;
  private boolean codecReceivedEos;
  private int codecReconfigurationState;
  private boolean codecReconfigured;
  private int codecReinitializationState;
  private final List<Long> decodeOnlyPresentationTimestamps;
  protected DecoderCounters decoderCounters;
  private com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> drmSession;
  @Nullable
  private final com.google.android.exoplayer2.drm.DrmSessionManager<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> drmSessionManager;
  private final DecoderInputBuffer flagsOnlyBuffer;
  private Format format;
  private final FormatHolder formatHolder;
  private final TimedValueQueue<Format> formatQueue;
  private ByteBuffer[] inputBuffers;
  private int inputIndex;
  private boolean inputStreamEnded;
  private final MediaCodecSelector mediaCodecSelector;
  private ByteBuffer outputBuffer;
  private final MediaCodec.BufferInfo outputBufferInfo;
  private ByteBuffer[] outputBuffers;
  private Format outputFormat;
  private int outputIndex;
  private boolean outputStreamEnded;
  private com.google.android.exoplayer2.drm.DrmSession<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> pendingDrmSession;
  private Format pendingFormat;
  private final boolean playClearSamplesWithoutKeys;
  @Nullable
  private DecoderInitializationException preferredDecoderInitializationException;
  private float rendererOperatingRate;
  private boolean shouldSkipAdaptationWorkaroundOutputBuffer;
  private boolean shouldSkipOutputBuffer;
  private boolean waitingForFirstSyncFrame;
  private boolean waitingForKeys;
  
  public MediaCodecRenderer(int paramInt, MediaCodecSelector paramMediaCodecSelector, com.google.android.exoplayer2.upgrade.DrmSessionManager paramDrmSessionManager, boolean paramBoolean, float paramFloat)
  {
    super(paramInt);
    boolean bool;
    if (Util.SDK_INT >= 16) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    mediaCodecSelector = ((MediaCodecSelector)Assertions.checkNotNull(paramMediaCodecSelector));
    drmSessionManager = paramDrmSessionManager;
    playClearSamplesWithoutKeys = paramBoolean;
    assumedMinimumCodecOperatingRate = paramFloat;
    buffer = new DecoderInputBuffer(0);
    flagsOnlyBuffer = DecoderInputBuffer.newFlagsOnlyInstance();
    formatHolder = new FormatHolder();
    formatQueue = new TimedValueQueue();
    decodeOnlyPresentationTimestamps = new ArrayList();
    outputBufferInfo = new MediaCodec.BufferInfo();
    codecReconfigurationState = 0;
    codecReinitializationState = 0;
    codecOperatingRate = -1.0F;
    rendererOperatingRate = 1.0F;
  }
  
  private int codecAdaptationWorkaroundMode(String paramString)
  {
    if ((Util.SDK_INT <= 25) && ("OMX.Exynos.avc.dec.secure".equals(paramString)) && ((Util.MODEL.startsWith("SM-T585")) || (Util.MODEL.startsWith("SM-A510")) || (Util.MODEL.startsWith("SM-A520")) || (Util.MODEL.startsWith("SM-J700")))) {
      return 2;
    }
    if ((Util.SDK_INT < 24) && (("OMX.Nvidia.h264.decode".equals(paramString)) || ("OMX.Nvidia.h264.decode.secure".equals(paramString))) && (("flounder".equals(Util.DEVICE)) || ("flounder_lte".equals(Util.DEVICE)) || ("grouper".equals(Util.DEVICE)) || ("tilapia".equals(Util.DEVICE)))) {
      return 1;
    }
    return 0;
  }
  
  private static boolean codecNeedsDiscardToSpsWorkaround(String paramString, Format paramFormat)
  {
    return (Util.SDK_INT < 21) && (initializationData.isEmpty()) && ("OMX.MTK.VIDEO.DECODER.AVC".equals(paramString));
  }
  
  private static boolean codecNeedsEosFlushWorkaround(String paramString)
  {
    return ((Util.SDK_INT <= 23) && ("OMX.google.vorbis.decoder".equals(paramString))) || ((Util.SDK_INT <= 19) && ("hb2000".equals(Util.DEVICE)) && (("OMX.amlogic.avc.decoder.awesome".equals(paramString)) || ("OMX.amlogic.avc.decoder.awesome.secure".equals(paramString))));
  }
  
  private static boolean codecNeedsEosOutputExceptionWorkaround(String paramString)
  {
    return (Util.SDK_INT == 21) && ("OMX.google.aac.decoder".equals(paramString));
  }
  
  private static boolean codecNeedsEosPropagationWorkaround(MediaCodecInfo paramMediaCodecInfo)
  {
    String str = name;
    return ((Util.SDK_INT <= 17) && (("OMX.rk.video_decoder.avc".equals(str)) || ("OMX.allwinner.video.decoder.avc".equals(str)))) || (("Amazon".equals(Util.MANUFACTURER)) && ("AFTS".equals(Util.MODEL)) && (secure));
  }
  
  private static boolean codecNeedsFlushWorkaround(String paramString)
  {
    return (Util.SDK_INT < 18) || ((Util.SDK_INT == 18) && (("OMX.SEC.avc.dec".equals(paramString)) || ("OMX.SEC.avc.dec.secure".equals(paramString)))) || ((Util.SDK_INT == 19) && (Util.MODEL.startsWith("SM-G800")) && (("OMX.Exynos.avc.dec".equals(paramString)) || ("OMX.Exynos.avc.dec.secure".equals(paramString))));
  }
  
  private static boolean codecNeedsMonoChannelCountWorkaround(String paramString, Format paramFormat)
  {
    return (Util.SDK_INT <= 18) && (channelCount == 1) && ("OMX.MTK.AUDIO.DECODER.MP3".equals(paramString));
  }
  
  private static boolean codecNeedsReconfigureWorkaround(String paramString)
  {
    return (Util.MODEL.startsWith("SM-T230")) && ("OMX.MARVELL.VIDEO.HW.CODA7542DECODER".equals(paramString));
  }
  
  private boolean deviceNeedsDrmKeysToConfigureCodecWorkaround()
  {
    return ("Amazon".equals(Util.MANUFACTURER)) && (("AFTM".equals(Util.MODEL)) || ("AFTB".equals(Util.MODEL)));
  }
  
  private boolean drainOutputBuffer(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    MediaCodec localMediaCodec;
    Object localObject;
    if (!hasOutputBuffer()) {
      if ((codecNeedsEosOutputExceptionWorkaround) && (codecReceivedEos))
      {
        localMediaCodec = codec;
        localObject = outputBufferInfo;
      }
    }
    try
    {
      i = localMediaCodec.dequeueOutputBuffer((MediaCodec.BufferInfo)localObject, getDequeueOutputBufferTimeoutUs());
    }
    catch (IllegalStateException localIllegalStateException1)
    {
      int i;
      int j;
      long l;
      boolean bool;
      Format localFormat;
      for (;;) {}
    }
    processEndOfStream();
    if (outputStreamEnded)
    {
      releaseCodec();
      return false;
      i = codec.dequeueOutputBuffer(outputBufferInfo, getDequeueOutputBufferTimeoutUs());
      if (i < 0)
      {
        if (i == -2)
        {
          processOutputFormat();
          return true;
        }
        if (i == -3)
        {
          processOutputBuffersChanged();
          return true;
        }
        if ((codecNeedsEosPropagation) && ((inputStreamEnded) || (codecReinitializationState == 2)))
        {
          processEndOfStream();
          return false;
        }
      }
      else
      {
        if (shouldSkipAdaptationWorkaroundOutputBuffer)
        {
          shouldSkipAdaptationWorkaroundOutputBuffer = false;
          codec.releaseOutputBuffer(i, false);
          return true;
        }
        if ((outputBufferInfo.size == 0) && ((outputBufferInfo.flags & 0x4) != 0))
        {
          processEndOfStream();
          return false;
        }
        outputIndex = i;
        outputBuffer = getOutputBuffer(i);
        if (outputBuffer != null)
        {
          outputBuffer.position(outputBufferInfo.offset);
          outputBuffer.limit(outputBufferInfo.offset + outputBufferInfo.size);
        }
        shouldSkipOutputBuffer = shouldSkipOutputBuffer(outputBufferInfo.presentationTimeUs);
        updateOutputFormatForTime(outputBufferInfo.presentationTimeUs);
        if ((codecNeedsEosOutputExceptionWorkaround) && (codecReceivedEos))
        {
          localMediaCodec = codec;
          localObject = outputBuffer;
          i = outputIndex;
          j = outputBufferInfo.flags;
          l = outputBufferInfo.presentationTimeUs;
          bool = shouldSkipOutputBuffer;
          localFormat = outputFormat;
        }
        try
        {
          bool = processOutputBuffer(paramLong1, paramLong2, localMediaCodec, (ByteBuffer)localObject, i, j, l, bool, localFormat);
        }
        catch (IllegalStateException localIllegalStateException2)
        {
          for (;;) {}
        }
        processEndOfStream();
        if (outputStreamEnded)
        {
          releaseCodec();
          return false;
          bool = processOutputBuffer(paramLong1, paramLong2, codec, outputBuffer, outputIndex, outputBufferInfo.flags, outputBufferInfo.presentationTimeUs, shouldSkipOutputBuffer, outputFormat);
          if (bool)
          {
            onProcessedOutputBuffer(outputBufferInfo.presentationTimeUs);
            if ((outputBufferInfo.flags & 0x4) != 0) {
              i = 1;
            } else {
              i = 0;
            }
            resetOutputBuffer();
            if (i == 0) {
              return true;
            }
            processEndOfStream();
            return false;
          }
        }
      }
    }
    return false;
  }
  
  private boolean feedInputBuffer()
    throws ExoPlaybackException
  {
    if ((codec != null) && (codecReinitializationState != 2))
    {
      if (inputStreamEnded) {
        return false;
      }
      if (inputIndex < 0)
      {
        inputIndex = codec.dequeueInputBuffer(0L);
        if (inputIndex < 0) {
          return false;
        }
        buffer.data = getInputBuffer(inputIndex);
        buffer.clear();
      }
      if (codecReinitializationState == 1)
      {
        if (!codecNeedsEosPropagation)
        {
          codecReceivedEos = true;
          codec.queueInputBuffer(inputIndex, 0, 0, 0L, 4);
          resetInputBuffer();
        }
        codecReinitializationState = 2;
        return false;
      }
      if (codecNeedsAdaptationWorkaroundBuffer)
      {
        codecNeedsAdaptationWorkaroundBuffer = false;
        buffer.data.put(ADAPTATION_WORKAROUND_BUFFER);
        codec.queueInputBuffer(inputIndex, 0, ADAPTATION_WORKAROUND_BUFFER.length, 0L, 0);
        resetInputBuffer();
        codecReceivedBuffers = true;
        return true;
      }
      int j;
      int i;
      Object localObject1;
      if (waitingForKeys)
      {
        j = -4;
        i = 0;
      }
      else
      {
        if (codecReconfigurationState == 1)
        {
          i = 0;
          while (i < format.initializationData.size())
          {
            localObject1 = (byte[])format.initializationData.get(i);
            buffer.data.put((byte[])localObject1);
            i += 1;
          }
          codecReconfigurationState = 2;
        }
        i = buffer.data.position();
        j = readSource(formatHolder, buffer, false);
      }
      if (j == -3) {
        return false;
      }
      if (j == -5)
      {
        if (codecReconfigurationState == 2)
        {
          buffer.clear();
          codecReconfigurationState = 1;
        }
        onInputFormatChanged(formatHolder.format);
        return true;
      }
      if (buffer.isEndOfStream())
      {
        if (codecReconfigurationState == 2)
        {
          buffer.clear();
          codecReconfigurationState = 1;
        }
        inputStreamEnded = true;
        if (!codecReceivedBuffers)
        {
          processEndOfStream();
          return false;
        }
        if (codecNeedsEosPropagation) {
          return false;
        }
        codecReceivedEos = true;
        localObject1 = codec;
        i = inputIndex;
        try
        {
          ((MediaCodec)localObject1).queueInputBuffer(i, 0, 0, 0L, 4);
          resetInputBuffer();
          return false;
        }
        catch (MediaCodec.CryptoException localCryptoException1)
        {
          throw ExoPlaybackException.createForRenderer(localCryptoException1, getIndex());
        }
      }
      if ((waitingForFirstSyncFrame) && (!buffer.isKeyFrame()))
      {
        buffer.clear();
        if (codecReconfigurationState == 2)
        {
          codecReconfigurationState = 1;
          return true;
        }
      }
      else
      {
        waitingForFirstSyncFrame = false;
        boolean bool1 = buffer.isEncrypted();
        waitingForKeys = shouldWaitForKeys(bool1);
        if (waitingForKeys) {
          return false;
        }
        if ((codecNeedsDiscardToSpsWorkaround) && (!bool1))
        {
          NalUnitUtil.discardToSps(buffer.data);
          if (buffer.data.position() == 0) {
            return true;
          }
          codecNeedsDiscardToSpsWorkaround = false;
        }
        long l = buffer.timeUs;
        Object localObject2 = buffer;
        try
        {
          boolean bool2 = ((Buffer)localObject2).isDecodeOnly();
          if (bool2)
          {
            localObject2 = decodeOnlyPresentationTimestamps;
            ((List)localObject2).add(Long.valueOf(l));
          }
          Object localObject3;
          if (pendingFormat != null)
          {
            localObject2 = formatQueue;
            localObject3 = pendingFormat;
            ((TimedValueQueue)localObject2).concatenate(l, localObject3);
            pendingFormat = null;
          }
          localObject2 = buffer;
          ((DecoderInputBuffer)localObject2).flip();
          localObject2 = buffer;
          onQueueInputBuffer((DecoderInputBuffer)localObject2);
          if (bool1)
          {
            localObject2 = buffer;
            localObject2 = getFrameworkCryptoInfo((DecoderInputBuffer)localObject2, i);
            localObject3 = codec;
            i = inputIndex;
            ((MediaCodec)localObject3).queueSecureInputBuffer(i, 0, (MediaCodec.CryptoInfo)localObject2, l, 0);
          }
          else
          {
            localObject2 = codec;
            i = inputIndex;
            localObject3 = buffer.data;
            ((MediaCodec)localObject2).queueInputBuffer(i, 0, ((ByteBuffer)localObject3).limit(), l, 0);
          }
          resetInputBuffer();
          codecReceivedBuffers = true;
          codecReconfigurationState = 0;
          localObject2 = decoderCounters;
          inputBufferCount += 1;
          return true;
        }
        catch (MediaCodec.CryptoException localCryptoException2)
        {
          throw ExoPlaybackException.createForRenderer(localCryptoException2, getIndex());
        }
      }
    }
    else
    {
      return false;
    }
    return true;
  }
  
  private List getAvailableCodecInfos(boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    List localList = getDecoderInfos(mediaCodecSelector, format, paramBoolean);
    Object localObject = localList;
    if (localList.isEmpty())
    {
      localObject = localList;
      if (paramBoolean)
      {
        localList = getDecoderInfos(mediaCodecSelector, format, false);
        localObject = localList;
        if (!localList.isEmpty())
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("Drm session requires secure decoder for ");
          ((StringBuilder)localObject).append(format.sampleMimeType);
          ((StringBuilder)localObject).append(", but no secure decoder available. Trying to proceed with ");
          ((StringBuilder)localObject).append(localList);
          ((StringBuilder)localObject).append(".");
          Log.w("MediaCodecRenderer", ((StringBuilder)localObject).toString());
          localObject = localList;
        }
      }
    }
    return localObject;
  }
  
  private void getCodecBuffers(MediaCodec paramMediaCodec)
  {
    if (Util.SDK_INT < 21)
    {
      inputBuffers = paramMediaCodec.getInputBuffers();
      outputBuffers = paramMediaCodec.getOutputBuffers();
    }
  }
  
  private static MediaCodec.CryptoInfo getFrameworkCryptoInfo(DecoderInputBuffer paramDecoderInputBuffer, int paramInt)
  {
    paramDecoderInputBuffer = cryptoInfo.getFrameworkCryptoInfoV16();
    if (paramInt == 0) {
      return paramDecoderInputBuffer;
    }
    if (numBytesOfClearData == null) {
      numBytesOfClearData = new int[1];
    }
    int[] arrayOfInt = numBytesOfClearData;
    arrayOfInt[0] += paramInt;
    return paramDecoderInputBuffer;
  }
  
  private ByteBuffer getInputBuffer(int paramInt)
  {
    if (Util.SDK_INT >= 21) {
      return codec.getInputBuffer(paramInt);
    }
    return inputBuffers[paramInt];
  }
  
  private ByteBuffer getOutputBuffer(int paramInt)
  {
    if (Util.SDK_INT >= 21) {
      return codec.getOutputBuffer(paramInt);
    }
    return outputBuffers[paramInt];
  }
  
  private boolean hasOutputBuffer()
  {
    return outputIndex >= 0;
  }
  
  private void initCodec(MediaCodecInfo paramMediaCodecInfo, MediaCrypto paramMediaCrypto)
    throws Exception
  {
    String str = name;
    updateCodecOperatingRate();
    boolean bool;
    if (codecOperatingRate > assumedMinimumCodecOperatingRate) {
      bool = true;
    } else {
      bool = false;
    }
    Object localObject;
    try
    {
      long l1 = SystemClock.elapsedRealtime();
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("createCodec:");
      ((StringBuilder)localObject).append(str);
      TraceUtil.beginSection(((StringBuilder)localObject).toString());
      MediaCodec localMediaCodec = MediaCodec.createByCodecName(str);
      localObject = localMediaCodec;
      try
      {
        TraceUtil.endSection();
        TraceUtil.beginSection("configureCodec");
        Format localFormat = format;
        float f;
        if (bool) {
          f = codecOperatingRate;
        } else {
          f = -1.0F;
        }
        configureCodec(paramMediaCodecInfo, localMediaCodec, localFormat, paramMediaCrypto, f);
        codecConfiguredWithOperatingRate = bool;
        TraceUtil.endSection();
        TraceUtil.beginSection("startCodec");
        localMediaCodec.start();
        TraceUtil.endSection();
        long l2 = SystemClock.elapsedRealtime();
        getCodecBuffers(localMediaCodec);
        codec = localMediaCodec;
        codecInfo = paramMediaCodecInfo;
        onCodecInitialized(str, l2, l2 - l1);
        return;
      }
      catch (Exception paramMediaCodecInfo) {}
      if (localObject == null) {
        break label204;
      }
    }
    catch (Exception paramMediaCodecInfo)
    {
      localObject = null;
    }
    resetCodecBuffers();
    ((MediaCodec)localObject).release();
    label204:
    throw paramMediaCodecInfo;
  }
  
  private boolean initCodecWithFallback(MediaCrypto paramMediaCrypto, boolean paramBoolean)
    throws MediaCodecRenderer.DecoderInitializationException
  {
    Object localObject;
    if (availableCodecInfos == null) {
      try
      {
        localObject = new ArrayDeque(getAvailableCodecInfos(paramBoolean));
        availableCodecInfos = ((ArrayDeque)localObject);
        preferredDecoderInitializationException = null;
      }
      catch (MediaCodecUtil.DecoderQueryException paramMediaCrypto)
      {
        throw new DecoderInitializationException(format, paramMediaCrypto, paramBoolean, -49998);
      }
    }
    if (!availableCodecInfos.isEmpty())
    {
      do
      {
        localObject = (MediaCodecInfo)availableCodecInfos.peekFirst();
        if (!shouldInitCodec((MediaCodecInfo)localObject)) {
          return false;
        }
        try
        {
          initCodec((MediaCodecInfo)localObject, paramMediaCrypto);
          return true;
        }
        catch (Exception localException)
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Failed to initialize decoder: ");
          localStringBuilder.append(localObject);
          Log.w("MediaCodecRenderer", localStringBuilder.toString(), localException);
          availableCodecInfos.removeFirst();
          localObject = new DecoderInitializationException(format, localException, paramBoolean, name);
          if (preferredDecoderInitializationException == null) {
            preferredDecoderInitializationException = ((DecoderInitializationException)localObject);
          } else {
            preferredDecoderInitializationException = preferredDecoderInitializationException.copyWithFallbackException((DecoderInitializationException)localObject);
          }
        }
      } while (!availableCodecInfos.isEmpty());
      throw preferredDecoderInitializationException;
    }
    throw new DecoderInitializationException(format, null, paramBoolean, -49999);
  }
  
  private void processEndOfStream()
    throws ExoPlaybackException
  {
    if (codecReinitializationState == 2)
    {
      releaseCodec();
      maybeInitCodec();
      return;
    }
    outputStreamEnded = true;
    renderToEndOfStream();
  }
  
  private void processOutputBuffersChanged()
  {
    if (Util.SDK_INT < 21) {
      outputBuffers = codec.getOutputBuffers();
    }
  }
  
  private void processOutputFormat()
    throws ExoPlaybackException
  {
    MediaFormat localMediaFormat = codec.getOutputFormat();
    if ((codecAdaptationWorkaroundMode != 0) && (localMediaFormat.getInteger("width") == 32) && (localMediaFormat.getInteger("height") == 32))
    {
      shouldSkipAdaptationWorkaroundOutputBuffer = true;
      return;
    }
    if (codecNeedsMonoChannelCountWorkaround) {
      localMediaFormat.setInteger("channel-count", 1);
    }
    onOutputFormatChanged(codec, localMediaFormat);
  }
  
  private void reinitializeCodec()
    throws ExoPlaybackException
  {
    availableCodecInfos = null;
    if (codecReceivedBuffers)
    {
      codecReinitializationState = 1;
      return;
    }
    releaseCodec();
    maybeInitCodec();
  }
  
  private void resetCodecBuffers()
  {
    if (Util.SDK_INT < 21)
    {
      inputBuffers = null;
      outputBuffers = null;
    }
  }
  
  private void resetInputBuffer()
  {
    inputIndex = -1;
    buffer.data = null;
  }
  
  private void resetOutputBuffer()
  {
    outputIndex = -1;
    outputBuffer = null;
  }
  
  private boolean shouldSkipOutputBuffer(long paramLong)
  {
    int j = decodeOnlyPresentationTimestamps.size();
    int i = 0;
    while (i < j)
    {
      if (((Long)decodeOnlyPresentationTimestamps.get(i)).longValue() == paramLong)
      {
        decodeOnlyPresentationTimestamps.remove(i);
        return true;
      }
      i += 1;
    }
    return false;
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
  
  private void updateCodecOperatingRate()
    throws ExoPlaybackException
  {
    if (format != null)
    {
      if (Util.SDK_INT < 23) {
        return;
      }
      float f = getCodecOperatingRate(rendererOperatingRate, format, getStreamFormats());
      if (codecOperatingRate == f) {
        return;
      }
      codecOperatingRate = f;
      if (codec != null)
      {
        if (codecReinitializationState != 0) {
          return;
        }
        boolean bool = f < -1.0F;
        if ((!bool) && (codecConfiguredWithOperatingRate))
        {
          reinitializeCodec();
          return;
        }
        if ((bool) && ((codecConfiguredWithOperatingRate) || (f > assumedMinimumCodecOperatingRate)))
        {
          Bundle localBundle = new Bundle();
          localBundle.putFloat("operating-rate", f);
          codec.setParameters(localBundle);
          codecConfiguredWithOperatingRate = true;
        }
      }
    }
  }
  
  protected int canKeepCodec(MediaCodec paramMediaCodec, MediaCodecInfo paramMediaCodecInfo, Format paramFormat1, Format paramFormat2)
  {
    return 0;
  }
  
  protected abstract void configureCodec(MediaCodecInfo paramMediaCodecInfo, MediaCodec paramMediaCodec, Format paramFormat, MediaCrypto paramMediaCrypto, float paramFloat)
    throws MediaCodecUtil.DecoderQueryException;
  
  protected void flushCodec()
    throws ExoPlaybackException
  {
    codecHotswapDeadlineMs = -9223372036854775807L;
    resetInputBuffer();
    resetOutputBuffer();
    waitingForFirstSyncFrame = true;
    waitingForKeys = false;
    shouldSkipOutputBuffer = false;
    decodeOnlyPresentationTimestamps.clear();
    codecNeedsAdaptationWorkaroundBuffer = false;
    shouldSkipAdaptationWorkaroundOutputBuffer = false;
    if ((!codecNeedsFlushWorkaround) && ((!codecNeedsEosFlushWorkaround) || (!codecReceivedEos)))
    {
      if (codecReinitializationState != 0)
      {
        releaseCodec();
        maybeInitCodec();
      }
      else
      {
        codec.flush();
        codecReceivedBuffers = false;
      }
    }
    else
    {
      releaseCodec();
      maybeInitCodec();
    }
    if ((codecReconfigured) && (format != null)) {
      codecReconfigurationState = 1;
    }
  }
  
  protected final MediaCodec getCodec()
  {
    return codec;
  }
  
  protected final MediaCodecInfo getCodecInfo()
  {
    return codecInfo;
  }
  
  protected boolean getCodecNeedsEosPropagation()
  {
    return false;
  }
  
  protected float getCodecOperatingRate(float paramFloat, Format paramFormat, Format[] paramArrayOfFormat)
  {
    return -1.0F;
  }
  
  protected List getDecoderInfos(MediaCodecSelector paramMediaCodecSelector, Format paramFormat, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    return paramMediaCodecSelector.getDecoderInfos(sampleMimeType, paramBoolean);
  }
  
  protected long getDequeueOutputBufferTimeoutUs()
  {
    return 0L;
  }
  
  public boolean isEnded()
  {
    return outputStreamEnded;
  }
  
  public boolean isReady()
  {
    return (format != null) && (!waitingForKeys) && ((isSourceReady()) || (hasOutputBuffer()) || ((codecHotswapDeadlineMs != -9223372036854775807L) && (SystemClock.elapsedRealtime() < codecHotswapDeadlineMs)));
  }
  
  protected final void maybeInitCodec()
    throws ExoPlaybackException
  {
    if (codec == null)
    {
      if (format == null) {
        return;
      }
      drmSession = pendingDrmSession;
      String str = format.sampleMimeType;
      Object localObject2 = null;
      Object localObject1 = null;
      com.google.android.exoplayer2.upgrade.DrmSession localDrmSession = drmSession;
      boolean bool3 = false;
      boolean bool1;
      boolean bool2;
      if (localDrmSession != null)
      {
        localObject2 = (com.google.android.exoplayer2.upgrade.FrameworkMediaCrypto)drmSession.getMediaCrypto();
        if (localObject2 == null)
        {
          if (drmSession.getError() != null) {
            bool1 = false;
          }
        }
        else
        {
          localObject1 = ((com.google.android.exoplayer2.upgrade.FrameworkMediaCrypto)localObject2).getWrappedMediaCrypto();
          bool1 = ((com.google.android.exoplayer2.upgrade.FrameworkMediaCrypto)localObject2).requiresSecureDecoderComponent(str);
        }
        localObject2 = localObject1;
        bool2 = bool1;
        if (deviceNeedsDrmKeysToConfigureCodecWorkaround())
        {
          int i = drmSession.getState();
          if (i != 1)
          {
            localObject2 = localObject1;
            bool2 = bool1;
            if (i == 4) {}
          }
          else
          {
            throw ExoPlaybackException.createForRenderer(drmSession.getError(), getIndex());
          }
        }
      }
      else
      {
        bool2 = false;
      }
      try
      {
        bool1 = initCodecWithFallback((MediaCrypto)localObject2, bool2);
        if (!bool1) {
          return;
        }
        localObject1 = codecInfo.name;
        codecAdaptationWorkaroundMode = codecAdaptationWorkaroundMode((String)localObject1);
        codecNeedsReconfigureWorkaround = codecNeedsReconfigureWorkaround((String)localObject1);
        codecNeedsDiscardToSpsWorkaround = codecNeedsDiscardToSpsWorkaround((String)localObject1, format);
        codecNeedsFlushWorkaround = codecNeedsFlushWorkaround((String)localObject1);
        codecNeedsEosFlushWorkaround = codecNeedsEosFlushWorkaround((String)localObject1);
        codecNeedsEosOutputExceptionWorkaround = codecNeedsEosOutputExceptionWorkaround((String)localObject1);
        codecNeedsMonoChannelCountWorkaround = codecNeedsMonoChannelCountWorkaround((String)localObject1, format);
        if (!codecNeedsEosPropagationWorkaround(codecInfo))
        {
          bool1 = bool3;
          if (!getCodecNeedsEosPropagation()) {}
        }
        else
        {
          bool1 = true;
        }
        codecNeedsEosPropagation = bool1;
        long l;
        if (getState() == 2) {
          l = SystemClock.elapsedRealtime() + 1000L;
        } else {
          l = -9223372036854775807L;
        }
        codecHotswapDeadlineMs = l;
        resetInputBuffer();
        resetOutputBuffer();
        waitingForFirstSyncFrame = true;
        localObject1 = decoderCounters;
        decoderInitCount += 1;
        return;
      }
      catch (DecoderInitializationException localDecoderInitializationException)
      {
        throw ExoPlaybackException.createForRenderer(localDecoderInitializationException, getIndex());
      }
    }
  }
  
  protected void onCodecInitialized(String paramString, long paramLong1, long paramLong2) {}
  
  /* Error */
  protected void onDisabled()
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 439	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:format	Lcom/google/android/exoplayer2/Format;
    //   5: aload_0
    //   6: aconst_null
    //   7: putfield 660	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:availableCodecInfos	Ljava/util/ArrayDeque;
    //   10: aload_0
    //   11: invokevirtual 334	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:releaseCodec	()V
    //   14: aload_0
    //   15: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   18: astore_1
    //   19: aload_1
    //   20: ifnull +16 -> 36
    //   23: aload_0
    //   24: getfield 151	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   27: aload_0
    //   28: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   31: invokeinterface 855 2 0
    //   36: aload_0
    //   37: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   40: astore_1
    //   41: aload_1
    //   42: ifnull +31 -> 73
    //   45: aload_0
    //   46: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   49: astore_1
    //   50: aload_0
    //   51: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   54: astore_2
    //   55: aload_1
    //   56: aload_2
    //   57: if_acmpeq +16 -> 73
    //   60: aload_0
    //   61: getfield 151	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   64: aload_0
    //   65: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   68: invokeinterface 855 2 0
    //   73: aload_0
    //   74: aconst_null
    //   75: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   78: aload_0
    //   79: aconst_null
    //   80: putfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   83: return
    //   84: astore_1
    //   85: aload_0
    //   86: aconst_null
    //   87: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   90: aload_0
    //   91: aconst_null
    //   92: putfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   95: aload_1
    //   96: athrow
    //   97: astore_1
    //   98: aload_0
    //   99: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   102: astore_2
    //   103: aload_2
    //   104: ifnull +31 -> 135
    //   107: aload_0
    //   108: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   111: astore_2
    //   112: aload_0
    //   113: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   116: astore_3
    //   117: aload_2
    //   118: aload_3
    //   119: if_acmpeq +16 -> 135
    //   122: aload_0
    //   123: getfield 151	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   126: aload_0
    //   127: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   130: invokeinterface 855 2 0
    //   135: aload_0
    //   136: aconst_null
    //   137: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   140: aload_0
    //   141: aconst_null
    //   142: putfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   145: aload_1
    //   146: athrow
    //   147: astore_1
    //   148: aload_0
    //   149: aconst_null
    //   150: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   153: aload_0
    //   154: aconst_null
    //   155: putfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   158: aload_1
    //   159: athrow
    //   160: astore_1
    //   161: aload_0
    //   162: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   165: astore_2
    //   166: aload_2
    //   167: ifnull +16 -> 183
    //   170: aload_0
    //   171: getfield 151	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   174: aload_0
    //   175: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   178: invokeinterface 855 2 0
    //   183: aload_0
    //   184: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   187: astore_2
    //   188: aload_2
    //   189: ifnull +31 -> 220
    //   192: aload_0
    //   193: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   196: astore_2
    //   197: aload_0
    //   198: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   201: astore_3
    //   202: aload_2
    //   203: aload_3
    //   204: if_acmpeq +16 -> 220
    //   207: aload_0
    //   208: getfield 151	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   211: aload_0
    //   212: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   215: invokeinterface 855 2 0
    //   220: aload_0
    //   221: aconst_null
    //   222: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   225: aload_0
    //   226: aconst_null
    //   227: putfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   230: aload_1
    //   231: athrow
    //   232: astore_1
    //   233: aload_0
    //   234: aconst_null
    //   235: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   238: aload_0
    //   239: aconst_null
    //   240: putfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   243: aload_1
    //   244: athrow
    //   245: astore_1
    //   246: aload_0
    //   247: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   250: astore_2
    //   251: aload_2
    //   252: ifnull +31 -> 283
    //   255: aload_0
    //   256: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   259: astore_2
    //   260: aload_0
    //   261: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   264: astore_3
    //   265: aload_2
    //   266: aload_3
    //   267: if_acmpeq +16 -> 283
    //   270: aload_0
    //   271: getfield 151	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   274: aload_0
    //   275: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   278: invokeinterface 855 2 0
    //   283: aload_0
    //   284: aconst_null
    //   285: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   288: aload_0
    //   289: aconst_null
    //   290: putfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   293: aload_1
    //   294: athrow
    //   295: astore_1
    //   296: aload_0
    //   297: aconst_null
    //   298: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   301: aload_0
    //   302: aconst_null
    //   303: putfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   306: aload_1
    //   307: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	308	0	this	MediaCodecRenderer
    //   18	38	1	localDrmSession1	com.google.android.exoplayer2.upgrade.DrmSession
    //   84	12	1	localThrowable1	Throwable
    //   97	49	1	localThrowable2	Throwable
    //   147	12	1	localThrowable3	Throwable
    //   160	71	1	localThrowable4	Throwable
    //   232	12	1	localThrowable5	Throwable
    //   245	49	1	localThrowable6	Throwable
    //   295	12	1	localThrowable7	Throwable
    //   54	212	2	localDrmSession2	com.google.android.exoplayer2.upgrade.DrmSession
    //   116	151	3	localDrmSession3	com.google.android.exoplayer2.upgrade.DrmSession
    // Exception table:
    //   from	to	target	type
    //   36	41	84	java/lang/Throwable
    //   45	55	84	java/lang/Throwable
    //   60	73	84	java/lang/Throwable
    //   14	19	97	java/lang/Throwable
    //   23	36	97	java/lang/Throwable
    //   98	103	147	java/lang/Throwable
    //   107	117	147	java/lang/Throwable
    //   122	135	147	java/lang/Throwable
    //   10	14	160	java/lang/Throwable
    //   183	188	232	java/lang/Throwable
    //   192	202	232	java/lang/Throwable
    //   207	220	232	java/lang/Throwable
    //   161	166	245	java/lang/Throwable
    //   170	183	245	java/lang/Throwable
    //   246	251	295	java/lang/Throwable
    //   255	265	295	java/lang/Throwable
    //   270	283	295	java/lang/Throwable
  }
  
  protected void onEnabled(boolean paramBoolean)
    throws ExoPlaybackException
  {
    decoderCounters = new DecoderCounters();
  }
  
  protected void onInputFormatChanged(Format paramFormat)
    throws ExoPlaybackException
  {
    Format localFormat = format;
    format = paramFormat;
    pendingFormat = paramFormat;
    Object localObject = format.drmInitData;
    if (localFormat == null) {
      paramFormat = null;
    } else {
      paramFormat = drmInitData;
    }
    boolean bool1 = Util.areEqual(localObject, paramFormat);
    int i = 1;
    if ((bool1 ^ true)) {
      if (format.drmInitData != null)
      {
        if (drmSessionManager != null)
        {
          pendingDrmSession = drmSessionManager.acquireSession(Looper.myLooper(), format.drmInitData);
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
    paramFormat = pendingDrmSession;
    localObject = drmSession;
    boolean bool2 = false;
    int j;
    if ((paramFormat == localObject) && (codec != null))
    {
      j = canKeepCodec(codec, codecInfo, localFormat, format);
      if (j == 3) {}
    }
    switch (j)
    {
    default: 
      throw new IllegalStateException();
      if (!codecNeedsReconfigureWorkaround)
      {
        codecReconfigured = true;
        codecReconfigurationState = 1;
        if (codecAdaptationWorkaroundMode != 2)
        {
          bool1 = bool2;
          if (codecAdaptationWorkaroundMode == 1)
          {
            bool1 = bool2;
            if (format.width == width)
            {
              bool1 = bool2;
              if (format.height != height) {}
            }
          }
        }
        else
        {
          bool1 = true;
        }
        codecNeedsAdaptationWorkaroundBuffer = bool1;
      }
      break;
    case 0: 
      i = 0;
    }
    if (i == 0)
    {
      reinitializeCodec();
      return;
    }
    updateCodecOperatingRate();
  }
  
  protected void onOutputFormatChanged(MediaCodec paramMediaCodec, MediaFormat paramMediaFormat)
    throws ExoPlaybackException
  {}
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    inputStreamEnded = false;
    outputStreamEnded = false;
    if (codec != null) {
      flushCodec();
    }
    formatQueue.clear();
  }
  
  protected void onProcessedOutputBuffer(long paramLong) {}
  
  protected void onQueueInputBuffer(DecoderInputBuffer paramDecoderInputBuffer) {}
  
  protected void onStarted() {}
  
  protected void onStopped() {}
  
  protected abstract boolean processOutputBuffer(long paramLong1, long paramLong2, MediaCodec paramMediaCodec, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, long paramLong3, boolean paramBoolean, Format paramFormat)
    throws ExoPlaybackException;
  
  /* Error */
  protected void releaseCodec()
  {
    // Byte code:
    //   0: aload_0
    //   1: ldc2_w 777
    //   4: putfield 780	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecHotswapDeadlineMs	J
    //   7: aload_0
    //   8: invokespecial 427	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:resetInputBuffer	()V
    //   11: aload_0
    //   12: invokespecial 401	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:resetOutputBuffer	()V
    //   15: aload_0
    //   16: iconst_0
    //   17: putfield 437	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:waitingForKeys	Z
    //   20: aload_0
    //   21: iconst_0
    //   22: putfield 384	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:shouldSkipOutputBuffer	Z
    //   25: aload_0
    //   26: getfield 182	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:decodeOnlyPresentationTimestamps	Ljava/util/List;
    //   29: invokeinterface 781 1 0
    //   34: aload_0
    //   35: invokespecial 653	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:resetCodecBuffers	()V
    //   38: aload_0
    //   39: aconst_null
    //   40: putfield 646	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecInfo	Lcom/google/android/exoplayer2/mediacodec/MediaCodecInfo;
    //   43: aload_0
    //   44: iconst_0
    //   45: putfield 790	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigured	Z
    //   48: aload_0
    //   49: iconst_0
    //   50: putfield 435	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecReceivedBuffers	Z
    //   53: aload_0
    //   54: iconst_0
    //   55: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsDiscardToSpsWorkaround	Z
    //   58: aload_0
    //   59: iconst_0
    //   60: putfield 783	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsFlushWorkaround	Z
    //   63: aload_0
    //   64: iconst_0
    //   65: putfield 711	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecAdaptationWorkaroundMode	I
    //   68: aload_0
    //   69: iconst_0
    //   70: putfield 828	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsReconfigureWorkaround	Z
    //   73: aload_0
    //   74: iconst_0
    //   75: putfield 785	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsEosFlushWorkaround	Z
    //   78: aload_0
    //   79: iconst_0
    //   80: putfield 722	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsMonoChannelCountWorkaround	Z
    //   83: aload_0
    //   84: iconst_0
    //   85: putfield 429	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsAdaptationWorkaroundBuffer	Z
    //   88: aload_0
    //   89: iconst_0
    //   90: putfield 346	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:shouldSkipAdaptationWorkaroundOutputBuffer	Z
    //   93: aload_0
    //   94: iconst_0
    //   95: putfield 342	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsEosPropagation	Z
    //   98: aload_0
    //   99: iconst_0
    //   100: putfield 314	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecReceivedEos	Z
    //   103: aload_0
    //   104: iconst_0
    //   105: putfield 189	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigurationState	I
    //   108: aload_0
    //   109: iconst_0
    //   110: putfield 191	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecReinitializationState	I
    //   113: aload_0
    //   114: iconst_0
    //   115: putfield 637	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecConfiguredWithOperatingRate	Z
    //   118: aload_0
    //   119: getfield 316	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   122: ifnull +254 -> 376
    //   125: aload_0
    //   126: getfield 530	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   129: astore_1
    //   130: aload_1
    //   131: aload_1
    //   132: getfield 896	com/google/android/exoplayer2/decoder/DecoderCounters:decoderReleaseCount	I
    //   135: iconst_1
    //   136: iadd
    //   137: putfield 896	com/google/android/exoplayer2/decoder/DecoderCounters:decoderReleaseCount	I
    //   140: aload_0
    //   141: getfield 316	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   144: invokevirtual 899	android/media/MediaCodec:stop	()V
    //   147: aload_0
    //   148: getfield 316	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   151: invokevirtual 656	android/media/MediaCodec:release	()V
    //   154: aload_0
    //   155: aconst_null
    //   156: putfield 316	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   159: aload_0
    //   160: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   163: ifnull +213 -> 376
    //   166: aload_0
    //   167: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   170: aload_0
    //   171: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   174: if_acmpeq +202 -> 376
    //   177: aload_0
    //   178: getfield 151	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   181: aload_0
    //   182: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   185: invokeinterface 855 2 0
    //   190: aload_0
    //   191: aconst_null
    //   192: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   195: return
    //   196: astore_1
    //   197: aload_0
    //   198: aconst_null
    //   199: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   202: aload_1
    //   203: athrow
    //   204: astore_1
    //   205: aload_0
    //   206: aconst_null
    //   207: putfield 316	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   210: aload_0
    //   211: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   214: ifnull +43 -> 257
    //   217: aload_0
    //   218: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   221: aload_0
    //   222: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   225: if_acmpeq +32 -> 257
    //   228: aload_0
    //   229: getfield 151	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   232: aload_0
    //   233: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   236: invokeinterface 855 2 0
    //   241: aload_0
    //   242: aconst_null
    //   243: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   246: goto +11 -> 257
    //   249: astore_1
    //   250: aload_0
    //   251: aconst_null
    //   252: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   255: aload_1
    //   256: athrow
    //   257: aload_1
    //   258: athrow
    //   259: astore_1
    //   260: aload_0
    //   261: getfield 316	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   264: invokevirtual 656	android/media/MediaCodec:release	()V
    //   267: aload_0
    //   268: aconst_null
    //   269: putfield 316	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   272: aload_0
    //   273: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   276: ifnull +43 -> 319
    //   279: aload_0
    //   280: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   283: aload_0
    //   284: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   287: if_acmpeq +32 -> 319
    //   290: aload_0
    //   291: getfield 151	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   294: aload_0
    //   295: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   298: invokeinterface 855 2 0
    //   303: aload_0
    //   304: aconst_null
    //   305: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   308: goto +11 -> 319
    //   311: astore_1
    //   312: aload_0
    //   313: aconst_null
    //   314: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   317: aload_1
    //   318: athrow
    //   319: aload_1
    //   320: athrow
    //   321: astore_1
    //   322: aload_0
    //   323: aconst_null
    //   324: putfield 316	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   327: aload_0
    //   328: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   331: ifnull +43 -> 374
    //   334: aload_0
    //   335: getfield 805	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   338: aload_0
    //   339: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   342: if_acmpeq +32 -> 374
    //   345: aload_0
    //   346: getfield 151	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/upgrade/DrmSessionManager;
    //   349: aload_0
    //   350: getfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   353: invokeinterface 855 2 0
    //   358: aload_0
    //   359: aconst_null
    //   360: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   363: goto +11 -> 374
    //   366: astore_1
    //   367: aload_0
    //   368: aconst_null
    //   369: putfield 741	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/upgrade/DrmSession;
    //   372: aload_1
    //   373: athrow
    //   374: aload_1
    //   375: athrow
    //   376: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	377	0	this	MediaCodecRenderer
    //   129	3	1	localDecoderCounters	DecoderCounters
    //   196	7	1	localThrowable1	Throwable
    //   204	1	1	localThrowable2	Throwable
    //   249	9	1	localThrowable3	Throwable
    //   259	1	1	localThrowable4	Throwable
    //   311	9	1	localThrowable5	Throwable
    //   321	1	1	localThrowable6	Throwable
    //   366	9	1	localThrowable7	Throwable
    // Exception table:
    //   from	to	target	type
    //   177	190	196	java/lang/Throwable
    //   147	154	204	java/lang/Throwable
    //   228	241	249	java/lang/Throwable
    //   140	147	259	java/lang/Throwable
    //   290	303	311	java/lang/Throwable
    //   260	267	321	java/lang/Throwable
    //   345	358	366	java/lang/Throwable
  }
  
  public void render(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    if (outputStreamEnded)
    {
      renderToEndOfStream();
      return;
    }
    int i;
    if (format == null)
    {
      flagsOnlyBuffer.clear();
      i = readSource(formatHolder, flagsOnlyBuffer, true);
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
    maybeInitCodec();
    if (codec != null)
    {
      TraceUtil.beginSection("drainAndFeed");
      while (drainOutputBuffer(paramLong1, paramLong2)) {}
      while (feedInputBuffer()) {}
      TraceUtil.endSection();
    }
    else
    {
      DecoderCounters localDecoderCounters = decoderCounters;
      skippedInputBufferCount += skipSource(paramLong1);
      flagsOnlyBuffer.clear();
      i = readSource(formatHolder, flagsOnlyBuffer, false);
      if (i == -5)
      {
        onInputFormatChanged(formatHolder.format);
      }
      else if (i == -4)
      {
        Assertions.checkState(flagsOnlyBuffer.isEndOfStream());
        inputStreamEnded = true;
        processEndOfStream();
      }
    }
    decoderCounters.ensureUpdated();
  }
  
  protected void renderToEndOfStream()
    throws ExoPlaybackException
  {}
  
  public final void setOperatingRate(float paramFloat)
    throws ExoPlaybackException
  {
    rendererOperatingRate = paramFloat;
    updateCodecOperatingRate();
  }
  
  protected boolean shouldInitCodec(MediaCodecInfo paramMediaCodecInfo)
  {
    return true;
  }
  
  public final int supportsFormat(Format paramFormat)
    throws ExoPlaybackException
  {
    MediaCodecSelector localMediaCodecSelector = mediaCodecSelector;
    com.google.android.exoplayer2.upgrade.DrmSessionManager localDrmSessionManager = drmSessionManager;
    try
    {
      int i = supportsFormat(localMediaCodecSelector, localDrmSessionManager, paramFormat);
      return i;
    }
    catch (MediaCodecUtil.DecoderQueryException paramFormat)
    {
      throw ExoPlaybackException.createForRenderer(paramFormat, getIndex());
    }
  }
  
  protected abstract int supportsFormat(MediaCodecSelector paramMediaCodecSelector, com.google.android.exoplayer2.upgrade.DrmSessionManager paramDrmSessionManager, Format paramFormat)
    throws MediaCodecUtil.DecoderQueryException;
  
  public final int supportsMixedMimeTypeAdaptation()
  {
    return 8;
  }
  
  protected final Format updateOutputFormatForTime(long paramLong)
  {
    Format localFormat = (Format)formatQueue.pollFloor(paramLong);
    if (localFormat != null) {
      outputFormat = localFormat;
    }
    return localFormat;
  }
  
  public static class DecoderInitializationException
    extends Exception
  {
    private static final int CUSTOM_ERROR_CODE_BASE = -50000;
    private static final int DECODER_QUERY_ERROR = -49998;
    private static final int NO_SUITABLE_DECODER_ERROR = -49999;
    public final String decoderName;
    public final String diagnosticInfo;
    @Nullable
    public final DecoderInitializationException fallbackDecoderInitializationException;
    public final String mimeType;
    public final boolean secureDecoderRequired;
    
    public DecoderInitializationException(Format paramFormat, Throwable paramThrowable, boolean paramBoolean, int paramInt)
    {
      this(localStringBuilder.toString(), paramThrowable, sampleMimeType, paramBoolean, null, buildCustomDiagnosticInfo(paramInt), null);
    }
    
    public DecoderInitializationException(Format paramFormat, Throwable paramThrowable, boolean paramBoolean, String paramString)
    {
      this((String)localObject, paramThrowable, str, paramBoolean, paramString, paramFormat, null);
    }
    
    private DecoderInitializationException(String paramString1, Throwable paramThrowable, String paramString2, boolean paramBoolean, String paramString3, String paramString4, DecoderInitializationException paramDecoderInitializationException)
    {
      super(paramThrowable);
      mimeType = paramString2;
      secureDecoderRequired = paramBoolean;
      decoderName = paramString3;
      diagnosticInfo = paramString4;
      fallbackDecoderInitializationException = paramDecoderInitializationException;
    }
    
    private static String buildCustomDiagnosticInfo(int paramInt)
    {
      String str;
      if (paramInt < 0) {
        str = "neg_";
      } else {
        str = "";
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("com.google.android.exoplayer.MediaCodecTrackRenderer_");
      localStringBuilder.append(str);
      localStringBuilder.append(Math.abs(paramInt));
      return localStringBuilder.toString();
    }
    
    private DecoderInitializationException copyWithFallbackException(DecoderInitializationException paramDecoderInitializationException)
    {
      return new DecoderInitializationException(getMessage(), getCause(), mimeType, secureDecoderRequired, decoderName, diagnosticInfo, paramDecoderInitializationException);
    }
    
    private static String getDiagnosticInfoV21(Throwable paramThrowable)
    {
      if ((paramThrowable instanceof MediaCodec.CodecException)) {
        return ((MediaCodec.CodecException)paramThrowable).getDiagnosticInfo();
      }
      return null;
    }
  }
}
