package com.google.android.exoplayer2.audio;

import android.annotation.SuppressLint;
import android.media.AudioAttributes.Builder;
import android.media.AudioFormat;
import android.media.AudioFormat.Builder;
import android.media.AudioTrack;
import android.os.ConditionVariable;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public final class DefaultAudioSink
  implements AudioSink
{
  private static final int AC3_BUFFER_MULTIPLICATION_FACTOR = 2;
  private static final int BUFFER_MULTIPLICATION_FACTOR = 4;
  private static final int ERROR_BAD_VALUE = -2;
  private static final long MAX_BUFFER_DURATION_US = 750000L;
  private static final long MIN_BUFFER_DURATION_US = 250000L;
  private static final int MODE_STATIC = 0;
  private static final int MODE_STREAM = 1;
  private static final long PASSTHROUGH_BUFFER_DURATION_US = 250000L;
  private static final int START_IN_SYNC = 1;
  private static final int START_NEED_SYNC = 2;
  private static final int START_NOT_SET = 0;
  private static final int STATE_INITIALIZED = 1;
  private static final String TAG = "AudioTrack";
  @SuppressLint({"InlinedApi"})
  private static final int WRITE_NON_BLOCKING = 1;
  public static boolean enablePreV21AudioSessionWorkaround;
  public static boolean failOnSpuriousAudioTimestamp;
  private AudioProcessor[] activeAudioProcessors;
  @Nullable
  private PlaybackParameters afterDrainPlaybackParameters;
  private AudioAttributes audioAttributes;
  @Nullable
  private final AudioCapabilities audioCapabilities;
  private final AudioProcessorChain audioProcessorChain;
  private int audioSessionId;
  private AudioTrack audioTrack;
  private final AudioTrackPositionTracker audioTrackPositionTracker;
  private AuxEffectInfo auxEffectInfo;
  @Nullable
  private ByteBuffer avSyncHeader;
  private int bufferSize;
  private int bytesUntilNextAvSync;
  private boolean canApplyPlaybackParameters;
  private final ChannelMappingAudioProcessor channelMappingAudioProcessor;
  private int drainingAudioProcessorIndex;
  private final boolean enableConvertHighResIntPcmToFloat;
  private int framesPerEncodedSample;
  private boolean handledEndOfStream;
  @Nullable
  private ByteBuffer inputBuffer;
  private int inputSampleRate;
  private boolean isInputPcm;
  @Nullable
  private AudioTrack keepSessionIdAudioTrack;
  private long lastFeedElapsedRealtimeMs;
  @Nullable
  private AudioSink.Listener listener;
  @Nullable
  private ByteBuffer outputBuffer;
  private ByteBuffer[] outputBuffers;
  private int outputChannelConfig;
  private int outputEncoding;
  private int outputPcmFrameSize;
  private int outputSampleRate;
  private int pcmFrameSize;
  private PlaybackParameters playbackParameters;
  private final ArrayDeque<PlaybackParametersCheckpoint> playbackParametersCheckpoints;
  private long playbackParametersOffsetUs;
  private long playbackParametersPositionUs;
  private boolean playing;
  private byte[] preV21OutputBuffer;
  private int preV21OutputBufferOffset;
  private boolean processingEnabled;
  private final ConditionVariable releasingConditionVariable;
  private boolean shouldConvertHighResIntPcmToFloat;
  private int startMediaTimeState;
  private long startMediaTimeUs;
  private long submittedEncodedFrames;
  private long submittedPcmBytes;
  private final AudioProcessor[] toFloatPcmAvailableAudioProcessors;
  private final AudioProcessor[] toIntPcmAvailableAudioProcessors;
  private final TrimmingAudioProcessor trimmingAudioProcessor;
  private boolean tunneling;
  private float volume;
  private long writtenEncodedFrames;
  private long writtenPcmBytes;
  
  public DefaultAudioSink(AudioCapabilities paramAudioCapabilities, AudioProcessorChain paramAudioProcessorChain, boolean paramBoolean)
  {
    audioCapabilities = paramAudioCapabilities;
    audioProcessorChain = ((AudioProcessorChain)Assertions.checkNotNull(paramAudioProcessorChain));
    enableConvertHighResIntPcmToFloat = paramBoolean;
    releasingConditionVariable = new ConditionVariable(true);
    audioTrackPositionTracker = new AudioTrackPositionTracker(new PositionTrackerListener(null));
    channelMappingAudioProcessor = new ChannelMappingAudioProcessor();
    trimmingAudioProcessor = new TrimmingAudioProcessor();
    paramAudioCapabilities = new ArrayList();
    Collections.addAll(paramAudioCapabilities, new AudioProcessor[] { new ResamplingAudioProcessor(), channelMappingAudioProcessor, trimmingAudioProcessor });
    Collections.addAll(paramAudioCapabilities, paramAudioProcessorChain.getAudioProcessors());
    toIntPcmAvailableAudioProcessors = ((AudioProcessor[])paramAudioCapabilities.toArray(new AudioProcessor[paramAudioCapabilities.size()]));
    toFloatPcmAvailableAudioProcessors = new AudioProcessor[] { new FloatResamplingAudioProcessor() };
    volume = 1.0F;
    startMediaTimeState = 0;
    audioAttributes = AudioAttributes.DEFAULT;
    audioSessionId = 0;
    auxEffectInfo = new AuxEffectInfo(0, 0.0F);
    playbackParameters = PlaybackParameters.DEFAULT;
    drainingAudioProcessorIndex = -1;
    activeAudioProcessors = new AudioProcessor[0];
    outputBuffers = new ByteBuffer[0];
    playbackParametersCheckpoints = new ArrayDeque();
  }
  
  public DefaultAudioSink(AudioCapabilities paramAudioCapabilities, AudioProcessor[] paramArrayOfAudioProcessor)
  {
    this(paramAudioCapabilities, paramArrayOfAudioProcessor, false);
  }
  
  public DefaultAudioSink(AudioCapabilities paramAudioCapabilities, AudioProcessor[] paramArrayOfAudioProcessor, boolean paramBoolean)
  {
    this(paramAudioCapabilities, new DefaultAudioProcessorChain(paramArrayOfAudioProcessor), paramBoolean);
  }
  
  private long applySkipping(long paramLong)
  {
    return paramLong + framesToDurationUs(audioProcessorChain.getSkippedOutputFrameCount());
  }
  
  private long applySpeedup(long paramLong)
  {
    for (PlaybackParametersCheckpoint localPlaybackParametersCheckpoint = null; (!playbackParametersCheckpoints.isEmpty()) && (paramLong >= playbackParametersCheckpoints.getFirst()).positionUs); localPlaybackParametersCheckpoint = (PlaybackParametersCheckpoint)playbackParametersCheckpoints.remove()) {}
    if (localPlaybackParametersCheckpoint != null)
    {
      playbackParameters = playbackParameters;
      playbackParametersPositionUs = positionUs;
      playbackParametersOffsetUs = (mediaTimeUs - startMediaTimeUs);
    }
    if (playbackParameters.speed == 1.0F) {
      return paramLong + playbackParametersOffsetUs - playbackParametersPositionUs;
    }
    if (playbackParametersCheckpoints.isEmpty()) {
      return playbackParametersOffsetUs + audioProcessorChain.getMediaDuration(paramLong - playbackParametersPositionUs);
    }
    return playbackParametersOffsetUs + Util.getMediaDurationForPlayoutDuration(paramLong - playbackParametersPositionUs, playbackParameters.speed);
  }
  
  private AudioTrack createAudioTrackV21()
  {
    if (tunneling) {}
    for (android.media.AudioAttributes localAudioAttributes = new AudioAttributes.Builder().setContentType(3).setFlags(16).setUsage(1).build();; localAudioAttributes = audioAttributes.getAudioAttributesV21()) {
      break;
    }
    AudioFormat localAudioFormat = new AudioFormat.Builder().setChannelMask(outputChannelConfig).setEncoding(outputEncoding).setSampleRate(outputSampleRate).build();
    int i;
    if (audioSessionId != 0) {
      i = audioSessionId;
    } else {
      i = 0;
    }
    return new AudioTrack(localAudioAttributes, localAudioFormat, bufferSize, 1, i);
  }
  
  private boolean drainAudioProcessorsToEndOfStream()
    throws AudioSink.WriteException
  {
    int i;
    if (drainingAudioProcessorIndex == -1) {
      if (processingEnabled) {
        i = 0;
      } else {
        i = activeAudioProcessors.length;
      }
    }
    for (drainingAudioProcessorIndex = i;; drainingAudioProcessorIndex += 1)
    {
      i = 1;
      break label38;
      i = 0;
      label38:
      if (drainingAudioProcessorIndex >= activeAudioProcessors.length) {
        break;
      }
      AudioProcessor localAudioProcessor = activeAudioProcessors[drainingAudioProcessorIndex];
      if (i != 0) {
        localAudioProcessor.queueEndOfStream();
      }
      processBuffers(-9223372036854775807L);
      if (!localAudioProcessor.isEnded()) {
        return false;
      }
    }
    if (outputBuffer != null)
    {
      writeBuffer(outputBuffer, -9223372036854775807L);
      if (outputBuffer != null) {
        return false;
      }
    }
    drainingAudioProcessorIndex = -1;
    return true;
  }
  
  private long durationUsToFrames(long paramLong)
  {
    return paramLong * outputSampleRate / 1000000L;
  }
  
  private void flushAudioProcessors()
  {
    int i = 0;
    while (i < activeAudioProcessors.length)
    {
      AudioProcessor localAudioProcessor = activeAudioProcessors[i];
      localAudioProcessor.flush();
      outputBuffers[i] = localAudioProcessor.getOutput();
      i += 1;
    }
  }
  
  private long framesToDurationUs(long paramLong)
  {
    return paramLong * 1000000L / outputSampleRate;
  }
  
  private AudioProcessor[] getAvailableAudioProcessors()
  {
    if (shouldConvertHighResIntPcmToFloat) {
      return toFloatPcmAvailableAudioProcessors;
    }
    return toIntPcmAvailableAudioProcessors;
  }
  
  private static int getChannelConfig(int paramInt, boolean paramBoolean)
  {
    int i = paramInt;
    if (Util.SDK_INT <= 28)
    {
      i = paramInt;
      if (!paramBoolean) {
        if (paramInt == 7)
        {
          i = 8;
        }
        else if ((paramInt != 3) && (paramInt != 4))
        {
          i = paramInt;
          if (paramInt != 5) {}
        }
        else
        {
          i = 6;
        }
      }
    }
    paramInt = i;
    if (Util.SDK_INT <= 26)
    {
      paramInt = i;
      if ("fugu".equals(Util.DEVICE))
      {
        paramInt = i;
        if (!paramBoolean)
        {
          paramInt = i;
          if (i == 1) {
            paramInt = 2;
          }
        }
      }
    }
    return Util.getAudioTrackChannelConfig(paramInt);
  }
  
  private int getDefaultBufferSize()
  {
    if (isInputPcm)
    {
      i = AudioTrack.getMinBufferSize(outputSampleRate, outputChannelConfig, outputEncoding);
      boolean bool;
      if (i != -2) {
        bool = true;
      } else {
        bool = false;
      }
      Assertions.checkState(bool);
      return Util.constrainValue(i * 4, (int)durationUsToFrames(250000L) * outputPcmFrameSize, (int)Math.max(i, durationUsToFrames(750000L) * outputPcmFrameSize));
    }
    int j = getMaximumEncodedRateBytesPerSecond(outputEncoding);
    int i = j;
    if (outputEncoding == 5) {
      i = j * 2;
    }
    return (int)(i * 250000L / 1000000L);
  }
  
  private static int getFramesPerEncodedSample(int paramInt, ByteBuffer paramByteBuffer)
  {
    if ((paramInt != 7) && (paramInt != 8))
    {
      if (paramInt == 5) {
        return Ac3Util.getAc3SyncframeAudioSampleCount();
      }
      if (paramInt == 6) {
        return Ac3Util.parseEAc3SyncframeAudioSampleCount(paramByteBuffer);
      }
      if (paramInt == 14)
      {
        paramInt = Ac3Util.findTrueHdSyncframeOffset(paramByteBuffer);
        if (paramInt == -1) {
          return 0;
        }
        return Ac3Util.parseTrueHdSyncframeAudioSampleCount(paramByteBuffer, paramInt) * 16;
      }
      paramByteBuffer = new StringBuilder();
      paramByteBuffer.append("Unexpected audio encoding: ");
      paramByteBuffer.append(paramInt);
      throw new IllegalStateException(paramByteBuffer.toString());
    }
    return DtsUtil.parseDtsAudioSampleCount(paramByteBuffer);
  }
  
  private static int getMaximumEncodedRateBytesPerSecond(int paramInt)
  {
    if (paramInt != 14)
    {
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException();
      case 8: 
        return 2250000;
      case 7: 
        return 192000;
      case 6: 
        return 768000;
      }
      return 80000;
    }
    return 3062500;
  }
  
  private long getSubmittedFrames()
  {
    if (isInputPcm) {
      return submittedPcmBytes / pcmFrameSize;
    }
    return submittedEncodedFrames;
  }
  
  private long getWrittenFrames()
  {
    if (isInputPcm) {
      return writtenPcmBytes / outputPcmFrameSize;
    }
    return writtenEncodedFrames;
  }
  
  private void initialize()
    throws AudioSink.InitializationException
  {
    releasingConditionVariable.block();
    audioTrack = initializeAudioTrack();
    int i = audioTrack.getAudioSessionId();
    if ((enablePreV21AudioSessionWorkaround) && (Util.SDK_INT < 21))
    {
      if ((keepSessionIdAudioTrack != null) && (i != keepSessionIdAudioTrack.getAudioSessionId())) {
        releaseKeepSessionIdAudioTrack();
      }
      if (keepSessionIdAudioTrack == null) {
        keepSessionIdAudioTrack = initializeKeepSessionIdAudioTrack(i);
      }
    }
    if (audioSessionId != i)
    {
      audioSessionId = i;
      if (listener != null) {
        listener.onAudioSessionId(i);
      }
    }
    PlaybackParameters localPlaybackParameters;
    if (canApplyPlaybackParameters) {
      localPlaybackParameters = audioProcessorChain.applyPlaybackParameters(playbackParameters);
    } else {
      localPlaybackParameters = PlaybackParameters.DEFAULT;
    }
    playbackParameters = localPlaybackParameters;
    setupAudioProcessors();
    audioTrackPositionTracker.setAudioTrack(audioTrack, outputEncoding, outputPcmFrameSize, bufferSize);
    setVolumeInternal();
    if (auxEffectInfo.effectId != 0)
    {
      audioTrack.attachAuxEffect(auxEffectInfo.effectId);
      audioTrack.setAuxEffectSendLevel(auxEffectInfo.sendLevel);
    }
  }
  
  private AudioTrack initializeAudioTrack()
    throws AudioSink.InitializationException
  {
    AudioTrack localAudioTrack;
    if (Util.SDK_INT >= 21)
    {
      localAudioTrack = createAudioTrackV21();
    }
    else
    {
      i = Util.getStreamTypeForAudioUsage(audioAttributes.usage);
      if (audioSessionId == 0) {
        localAudioTrack = new AudioTrack(i, outputSampleRate, outputChannelConfig, outputEncoding, bufferSize, 1);
      } else {
        localAudioTrack = new AudioTrack(i, outputSampleRate, outputChannelConfig, outputEncoding, bufferSize, 1, audioSessionId);
      }
    }
    int i = localAudioTrack.getState();
    if (i == 1) {
      return localAudioTrack;
    }
    try
    {
      localAudioTrack.release();
      throw new AudioSink.InitializationException(i, outputSampleRate, outputChannelConfig, bufferSize);
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  private AudioTrack initializeKeepSessionIdAudioTrack(int paramInt)
  {
    return new AudioTrack(3, 4000, 4, 2, 2, 0, paramInt);
  }
  
  private long inputFramesToDurationUs(long paramLong)
  {
    return paramLong * 1000000L / inputSampleRate;
  }
  
  private boolean isInitialized()
  {
    return audioTrack != null;
  }
  
  private void processBuffers(long paramLong)
    throws AudioSink.WriteException
  {
    int j = activeAudioProcessors.length;
    int i = j;
    while (i >= 0)
    {
      ByteBuffer localByteBuffer;
      if (i > 0) {
        localByteBuffer = outputBuffers[(i - 1)];
      } else if (inputBuffer != null) {
        localByteBuffer = inputBuffer;
      } else {
        localByteBuffer = AudioProcessor.EMPTY_BUFFER;
      }
      if (i == j)
      {
        writeBuffer(localByteBuffer, paramLong);
      }
      else
      {
        Object localObject = activeAudioProcessors[i];
        ((AudioProcessor)localObject).queueInput(localByteBuffer);
        localObject = ((AudioProcessor)localObject).getOutput();
        outputBuffers[i] = localObject;
        if (((ByteBuffer)localObject).hasRemaining())
        {
          i += 1;
          continue;
        }
      }
      if (localByteBuffer.hasRemaining()) {
        return;
      }
      i -= 1;
    }
  }
  
  private void releaseKeepSessionIdAudioTrack()
  {
    if (keepSessionIdAudioTrack == null) {
      return;
    }
    final AudioTrack localAudioTrack = keepSessionIdAudioTrack;
    keepSessionIdAudioTrack = null;
    new Thread()
    {
      public void run()
      {
        localAudioTrack.release();
      }
    }.start();
  }
  
  private void setVolumeInternal()
  {
    if (!isInitialized()) {
      return;
    }
    if (Util.SDK_INT >= 21)
    {
      setVolumeInternalV21(audioTrack, volume);
      return;
    }
    setVolumeInternalV3(audioTrack, volume);
  }
  
  private static void setVolumeInternalV21(AudioTrack paramAudioTrack, float paramFloat)
  {
    paramAudioTrack.setVolume(paramFloat);
  }
  
  private static void setVolumeInternalV3(AudioTrack paramAudioTrack, float paramFloat)
  {
    paramAudioTrack.setStereoVolume(paramFloat, paramFloat);
  }
  
  private void setupAudioProcessors()
  {
    ArrayList localArrayList = new ArrayList();
    AudioProcessor[] arrayOfAudioProcessor = getAvailableAudioProcessors();
    int j = arrayOfAudioProcessor.length;
    int i = 0;
    while (i < j)
    {
      AudioProcessor localAudioProcessor = arrayOfAudioProcessor[i];
      if (localAudioProcessor.isActive()) {
        localArrayList.add(localAudioProcessor);
      } else {
        localAudioProcessor.flush();
      }
      i += 1;
    }
    i = localArrayList.size();
    activeAudioProcessors = ((AudioProcessor[])localArrayList.toArray(new AudioProcessor[i]));
    outputBuffers = new ByteBuffer[i];
    flushAudioProcessors();
  }
  
  private void writeBuffer(ByteBuffer paramByteBuffer, long paramLong)
    throws AudioSink.WriteException
  {
    if (!paramByteBuffer.hasRemaining()) {
      return;
    }
    ByteBuffer localByteBuffer = outputBuffer;
    boolean bool2 = true;
    int i = 0;
    boolean bool1;
    int j;
    int k;
    if (localByteBuffer != null)
    {
      if (outputBuffer == paramByteBuffer) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      Assertions.checkArgument(bool1);
    }
    else
    {
      outputBuffer = paramByteBuffer;
      if (Util.SDK_INT < 21)
      {
        j = paramByteBuffer.remaining();
        if ((preV21OutputBuffer == null) || (preV21OutputBuffer.length < j)) {
          preV21OutputBuffer = new byte[j];
        }
        k = paramByteBuffer.position();
        paramByteBuffer.get(preV21OutputBuffer, 0, j);
        paramByteBuffer.position(k);
        preV21OutputBufferOffset = 0;
      }
    }
    int m = paramByteBuffer.remaining();
    if (Util.SDK_INT < 21)
    {
      j = audioTrackPositionTracker.getAvailableBufferSize(writtenPcmBytes);
      if (j > 0)
      {
        i = Math.min(m, j);
        k = audioTrack.write(preV21OutputBuffer, preV21OutputBufferOffset, i);
        j = k;
        i = j;
        if (k > 0)
        {
          preV21OutputBufferOffset += k;
          paramByteBuffer.position(paramByteBuffer.position() + k);
          i = j;
        }
      }
    }
    else if (tunneling)
    {
      if (paramLong != -9223372036854775807L) {
        bool1 = bool2;
      } else {
        bool1 = false;
      }
      Assertions.checkState(bool1);
      i = writeNonBlockingWithAvSyncV21(audioTrack, paramByteBuffer, m, paramLong);
    }
    else
    {
      i = writeNonBlockingV21(audioTrack, paramByteBuffer, m);
    }
    lastFeedElapsedRealtimeMs = SystemClock.elapsedRealtime();
    if (i >= 0)
    {
      if (isInputPcm) {
        writtenPcmBytes += i;
      }
      if (i == m)
      {
        if (!isInputPcm) {
          writtenEncodedFrames += framesPerEncodedSample;
        }
        outputBuffer = null;
      }
    }
    else
    {
      throw new AudioSink.WriteException(i);
    }
  }
  
  private static int writeNonBlockingV21(AudioTrack paramAudioTrack, ByteBuffer paramByteBuffer, int paramInt)
  {
    return paramAudioTrack.write(paramByteBuffer, paramInt, 1);
  }
  
  private int writeNonBlockingWithAvSyncV21(AudioTrack paramAudioTrack, ByteBuffer paramByteBuffer, int paramInt, long paramLong)
  {
    if (avSyncHeader == null)
    {
      avSyncHeader = ByteBuffer.allocate(16);
      avSyncHeader.order(ByteOrder.BIG_ENDIAN);
      avSyncHeader.putInt(1431633921);
    }
    if (bytesUntilNextAvSync == 0)
    {
      avSyncHeader.putInt(4, paramInt);
      avSyncHeader.putLong(8, paramLong * 1000L);
      avSyncHeader.position(0);
      bytesUntilNextAvSync = paramInt;
    }
    int i = avSyncHeader.remaining();
    if (i > 0)
    {
      int j = paramAudioTrack.write(avSyncHeader, i, 1);
      if (j < 0)
      {
        bytesUntilNextAvSync = 0;
        return j;
      }
      if (j < i) {
        return 0;
      }
    }
    paramInt = writeNonBlockingV21(paramAudioTrack, paramByteBuffer, paramInt);
    if (paramInt < 0)
    {
      bytesUntilNextAvSync = 0;
      return paramInt;
    }
    bytesUntilNextAvSync -= paramInt;
    return paramInt;
  }
  
  public void configure(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
    throws AudioSink.ConfigurationException
  {
    inputSampleRate = paramInt3;
    isInputPcm = Util.isEncodingLinearPcm(paramInt1);
    boolean bool = enableConvertHighResIntPcmToFloat;
    int i1 = 1;
    int j = 0;
    if ((bool) && (supportsOutput(paramInt2, 1073741824)) && (Util.isEncodingHighResolutionIntegerPcm(paramInt1))) {
      bool = true;
    } else {
      bool = false;
    }
    shouldConvertHighResIntPcmToFloat = bool;
    if (isInputPcm) {
      pcmFrameSize = Util.getPcmFrameSize(paramInt1, paramInt2);
    }
    if ((isInputPcm) && (paramInt1 != 4)) {
      bool = true;
    } else {
      bool = false;
    }
    if ((!bool) || (shouldConvertHighResIntPcmToFloat)) {
      i1 = 0;
    }
    canApplyPlaybackParameters = i1;
    Object localObject = paramArrayOfInt;
    if (Util.SDK_INT < 21)
    {
      localObject = paramArrayOfInt;
      if (paramInt2 == 8)
      {
        localObject = paramArrayOfInt;
        if (paramArrayOfInt == null)
        {
          paramArrayOfInt = new int[6];
          i = 0;
          for (;;)
          {
            localObject = paramArrayOfInt;
            if (i >= paramArrayOfInt.length) {
              break;
            }
            paramArrayOfInt[i] = i;
            i += 1;
          }
        }
      }
    }
    if (bool)
    {
      trimmingAudioProcessor.setTrimFrameCount(paramInt5, paramInt6);
      channelMappingAudioProcessor.setChannelMap((int[])localObject);
      paramArrayOfInt = getAvailableAudioProcessors();
      int n = paramArrayOfInt.length;
      paramInt5 = 0;
      paramInt6 = j;
      for (;;)
      {
        m = paramInt5;
        k = paramInt1;
        j = paramInt2;
        i = paramInt3;
        if (paramInt6 >= n) {
          break label348;
        }
        localObject = paramArrayOfInt[paramInt6];
        try
        {
          i1 = ((AudioProcessor)localObject).configure(paramInt3, paramInt2, paramInt1);
          paramInt5 |= i1;
          if (((AudioProcessor)localObject).isActive())
          {
            paramInt2 = ((AudioProcessor)localObject).getOutputChannelCount();
            paramInt3 = ((AudioProcessor)localObject).getOutputSampleRateHz();
            paramInt1 = ((AudioProcessor)localObject).getOutputEncoding();
          }
          paramInt6 += 1;
        }
        catch (AudioProcessor.UnhandledFormatException paramArrayOfInt)
        {
          throw new AudioSink.ConfigurationException(paramArrayOfInt);
        }
      }
    }
    int m = 0;
    int i = paramInt3;
    j = paramInt2;
    int k = paramInt1;
    label348:
    paramInt1 = getChannelConfig(j, isInputPcm);
    if (paramInt1 != 0)
    {
      if ((m == 0) && (isInitialized()) && (outputEncoding == k) && (outputSampleRate == i) && (outputChannelConfig == paramInt1)) {
        return;
      }
      reset();
      processingEnabled = bool;
      outputSampleRate = i;
      outputChannelConfig = paramInt1;
      outputEncoding = k;
      if (isInputPcm) {
        paramInt1 = Util.getPcmFrameSize(outputEncoding, j);
      } else {
        paramInt1 = -1;
      }
      outputPcmFrameSize = paramInt1;
      if (paramInt4 == 0) {
        paramInt4 = getDefaultBufferSize();
      }
      bufferSize = paramInt4;
      return;
    }
    paramArrayOfInt = new StringBuilder();
    paramArrayOfInt.append("Unsupported channel count: ");
    paramArrayOfInt.append(j);
    throw new AudioSink.ConfigurationException(paramArrayOfInt.toString());
  }
  
  public void disableTunneling()
  {
    if (tunneling)
    {
      tunneling = false;
      audioSessionId = 0;
      reset();
    }
  }
  
  public void enableTunnelingV21(int paramInt)
  {
    boolean bool;
    if (Util.SDK_INT >= 21) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    if ((!tunneling) || (audioSessionId != paramInt))
    {
      tunneling = true;
      audioSessionId = paramInt;
      reset();
    }
  }
  
  public long getCurrentPositionUs(boolean paramBoolean)
  {
    if ((isInitialized()) && (startMediaTimeState != 0))
    {
      long l = Math.min(audioTrackPositionTracker.getCurrentPositionUs(paramBoolean), framesToDurationUs(getWrittenFrames()));
      return startMediaTimeUs + applySkipping(applySpeedup(l));
    }
    return Long.MIN_VALUE;
  }
  
  public PlaybackParameters getPlaybackParameters()
  {
    return playbackParameters;
  }
  
  public boolean handleBuffer(ByteBuffer paramByteBuffer, long paramLong)
    throws AudioSink.InitializationException, AudioSink.WriteException
  {
    boolean bool;
    if ((inputBuffer != null) && (paramByteBuffer != inputBuffer)) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.checkArgument(bool);
    if (!isInitialized())
    {
      initialize();
      if (playing) {
        play();
      }
    }
    if (!audioTrackPositionTracker.mayHandleBuffer(getWrittenFrames())) {
      return false;
    }
    if (inputBuffer == null)
    {
      if (!paramByteBuffer.hasRemaining()) {
        return true;
      }
      if ((!isInputPcm) && (framesPerEncodedSample == 0))
      {
        framesPerEncodedSample = getFramesPerEncodedSample(outputEncoding, paramByteBuffer);
        if (framesPerEncodedSample == 0) {
          return true;
        }
      }
      Object localObject;
      if (afterDrainPlaybackParameters != null)
      {
        if (!drainAudioProcessorsToEndOfStream()) {
          return false;
        }
        localObject = afterDrainPlaybackParameters;
        afterDrainPlaybackParameters = null;
        localObject = audioProcessorChain.applyPlaybackParameters((PlaybackParameters)localObject);
        playbackParametersCheckpoints.add(new PlaybackParametersCheckpoint((PlaybackParameters)localObject, Math.max(0L, paramLong), framesToDurationUs(getWrittenFrames()), null));
        setupAudioProcessors();
      }
      if (startMediaTimeState == 0)
      {
        startMediaTimeUs = Math.max(0L, paramLong);
        startMediaTimeState = 1;
      }
      else
      {
        long l = startMediaTimeUs + inputFramesToDurationUs(getSubmittedFrames() - trimmingAudioProcessor.getTrimmedFrameCount());
        if ((startMediaTimeState == 1) && (Math.abs(l - paramLong) > 200000L))
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("Discontinuity detected [expected ");
          ((StringBuilder)localObject).append(l);
          ((StringBuilder)localObject).append(", got ");
          ((StringBuilder)localObject).append(paramLong);
          ((StringBuilder)localObject).append("]");
          Log.e("AudioTrack", ((StringBuilder)localObject).toString());
          startMediaTimeState = 2;
        }
        if (startMediaTimeState == 2)
        {
          l = paramLong - l;
          startMediaTimeUs += l;
          startMediaTimeState = 1;
          if ((listener != null) && (l != 0L)) {
            listener.onPositionDiscontinuity();
          }
        }
      }
      if (isInputPcm) {
        submittedPcmBytes += paramByteBuffer.remaining();
      } else {
        submittedEncodedFrames += framesPerEncodedSample;
      }
      inputBuffer = paramByteBuffer;
    }
    if (processingEnabled) {
      processBuffers(paramLong);
    } else {
      writeBuffer(inputBuffer, paramLong);
    }
    if (!inputBuffer.hasRemaining())
    {
      inputBuffer = null;
      return true;
    }
    if (audioTrackPositionTracker.isStalled(getWrittenFrames()))
    {
      Log.w("AudioTrack", "Resetting stalled audio track");
      reset();
      return true;
    }
    return false;
  }
  
  public void handleDiscontinuity()
  {
    if (startMediaTimeState == 1) {
      startMediaTimeState = 2;
    }
  }
  
  public boolean hasPendingData()
  {
    return (isInitialized()) && (audioTrackPositionTracker.hasPendingData(getWrittenFrames()));
  }
  
  public boolean isEnded()
  {
    return (!isInitialized()) || ((handledEndOfStream) && (!hasPendingData()));
  }
  
  public void pause()
  {
    playing = false;
    if ((isInitialized()) && (audioTrackPositionTracker.pause())) {
      audioTrack.pause();
    }
  }
  
  public void play()
  {
    playing = true;
    if (isInitialized())
    {
      audioTrackPositionTracker.start();
      audioTrack.play();
    }
  }
  
  public void playToEndOfStream()
    throws AudioSink.WriteException
  {
    if (!handledEndOfStream)
    {
      if (!isInitialized()) {
        return;
      }
      if (drainAudioProcessorsToEndOfStream())
      {
        audioTrackPositionTracker.handleEndOfStream(getWrittenFrames());
        audioTrack.stop();
        bytesUntilNextAvSync = 0;
        handledEndOfStream = true;
      }
    }
  }
  
  public void release()
  {
    reset();
    releaseKeepSessionIdAudioTrack();
    AudioProcessor[] arrayOfAudioProcessor = toIntPcmAvailableAudioProcessors;
    int j = arrayOfAudioProcessor.length;
    int i = 0;
    while (i < j)
    {
      arrayOfAudioProcessor[i].reset();
      i += 1;
    }
    arrayOfAudioProcessor = toFloatPcmAvailableAudioProcessors;
    j = arrayOfAudioProcessor.length;
    i = 0;
    while (i < j)
    {
      arrayOfAudioProcessor[i].reset();
      i += 1;
    }
    audioSessionId = 0;
    playing = false;
  }
  
  public void reset()
  {
    if (isInitialized())
    {
      submittedPcmBytes = 0L;
      submittedEncodedFrames = 0L;
      writtenPcmBytes = 0L;
      writtenEncodedFrames = 0L;
      framesPerEncodedSample = 0;
      if (afterDrainPlaybackParameters != null)
      {
        playbackParameters = afterDrainPlaybackParameters;
        afterDrainPlaybackParameters = null;
      }
      else if (!playbackParametersCheckpoints.isEmpty())
      {
        playbackParameters = playbackParametersCheckpoints.getLast()).playbackParameters;
      }
      playbackParametersCheckpoints.clear();
      playbackParametersOffsetUs = 0L;
      playbackParametersPositionUs = 0L;
      trimmingAudioProcessor.resetTrimmedFrameCount();
      inputBuffer = null;
      outputBuffer = null;
      flushAudioProcessors();
      handledEndOfStream = false;
      drainingAudioProcessorIndex = -1;
      avSyncHeader = null;
      bytesUntilNextAvSync = 0;
      startMediaTimeState = 0;
      if (audioTrackPositionTracker.isPlaying()) {
        audioTrack.pause();
      }
      final AudioTrack localAudioTrack = audioTrack;
      audioTrack = null;
      audioTrackPositionTracker.reset();
      releasingConditionVariable.close();
      new Thread()
      {
        public void run()
        {
          try
          {
            localAudioTrack.flush();
            localAudioTrack.release();
            releasingConditionVariable.open();
            return;
          }
          catch (Throwable localThrowable)
          {
            releasingConditionVariable.open();
            throw localThrowable;
          }
        }
      }.start();
    }
  }
  
  public void setAudioAttributes(AudioAttributes paramAudioAttributes)
  {
    if (audioAttributes.equals(paramAudioAttributes)) {
      return;
    }
    audioAttributes = paramAudioAttributes;
    if (tunneling) {
      return;
    }
    reset();
    audioSessionId = 0;
  }
  
  public void setAudioSessionId(int paramInt)
  {
    if (audioSessionId != paramInt)
    {
      audioSessionId = paramInt;
      reset();
    }
  }
  
  public void setAuxEffectInfo(AuxEffectInfo paramAuxEffectInfo)
  {
    if (auxEffectInfo.equals(paramAuxEffectInfo)) {
      return;
    }
    int i = effectId;
    float f = sendLevel;
    if (audioTrack != null)
    {
      if (auxEffectInfo.effectId != i) {
        audioTrack.attachAuxEffect(i);
      }
      if (i != 0) {
        audioTrack.setAuxEffectSendLevel(f);
      }
    }
    auxEffectInfo = paramAuxEffectInfo;
  }
  
  public void setListener(AudioSink.Listener paramListener)
  {
    listener = paramListener;
  }
  
  public PlaybackParameters setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    if ((isInitialized()) && (!canApplyPlaybackParameters))
    {
      playbackParameters = PlaybackParameters.DEFAULT;
      return playbackParameters;
    }
    PlaybackParameters localPlaybackParameters;
    if (afterDrainPlaybackParameters != null) {
      localPlaybackParameters = afterDrainPlaybackParameters;
    } else if (!playbackParametersCheckpoints.isEmpty()) {
      localPlaybackParameters = playbackParametersCheckpoints.getLast()).playbackParameters;
    } else {
      localPlaybackParameters = playbackParameters;
    }
    if (!paramPlaybackParameters.equals(localPlaybackParameters)) {
      if (isInitialized()) {
        afterDrainPlaybackParameters = paramPlaybackParameters;
      } else {
        playbackParameters = audioProcessorChain.applyPlaybackParameters(paramPlaybackParameters);
      }
    }
    return playbackParameters;
  }
  
  public void setVolume(float paramFloat)
  {
    if (volume != paramFloat)
    {
      volume = paramFloat;
      setVolumeInternal();
    }
  }
  
  public boolean supportsOutput(int paramInt1, int paramInt2)
  {
    if (Util.isEncodingLinearPcm(paramInt2))
    {
      if ((paramInt2 != 4) || (Util.SDK_INT >= 21)) {
        return true;
      }
    }
    else if ((audioCapabilities != null) && (audioCapabilities.supportsEncoding(paramInt2)) && ((paramInt1 == -1) || (paramInt1 <= audioCapabilities.getMaxChannelCount()))) {
      return true;
    }
    return false;
  }
  
  public static abstract interface AudioProcessorChain
  {
    public abstract PlaybackParameters applyPlaybackParameters(PlaybackParameters paramPlaybackParameters);
    
    public abstract AudioProcessor[] getAudioProcessors();
    
    public abstract long getMediaDuration(long paramLong);
    
    public abstract long getSkippedOutputFrameCount();
  }
  
  public static class DefaultAudioProcessorChain
    implements DefaultAudioSink.AudioProcessorChain
  {
    private final AudioProcessor[] audioProcessors;
    private final SilenceSkippingAudioProcessor silenceSkippingAudioProcessor;
    private final SonicAudioProcessor sonicAudioProcessor;
    
    public DefaultAudioProcessorChain(AudioProcessor... paramVarArgs)
    {
      audioProcessors = ((AudioProcessor[])Arrays.copyOf(paramVarArgs, paramVarArgs.length + 2));
      silenceSkippingAudioProcessor = new SilenceSkippingAudioProcessor();
      sonicAudioProcessor = new SonicAudioProcessor();
      audioProcessors[paramVarArgs.length] = silenceSkippingAudioProcessor;
      audioProcessors[(paramVarArgs.length + 1)] = sonicAudioProcessor;
    }
    
    public PlaybackParameters applyPlaybackParameters(PlaybackParameters paramPlaybackParameters)
    {
      silenceSkippingAudioProcessor.setEnabled(skipSilence);
      return new PlaybackParameters(sonicAudioProcessor.setSpeed(speed), sonicAudioProcessor.setPitch(pitch), skipSilence);
    }
    
    public AudioProcessor[] getAudioProcessors()
    {
      return audioProcessors;
    }
    
    public long getMediaDuration(long paramLong)
    {
      return sonicAudioProcessor.scaleDurationForSpeedup(paramLong);
    }
    
    public long getSkippedOutputFrameCount()
    {
      return silenceSkippingAudioProcessor.getSkippedFrames();
    }
  }
  
  public static final class InvalidAudioTrackTimestampException
    extends RuntimeException
  {
    private InvalidAudioTrackTimestampException(String paramString)
    {
      super();
    }
  }
  
  private static final class PlaybackParametersCheckpoint
  {
    private final long mediaTimeUs;
    private final PlaybackParameters playbackParameters;
    private final long positionUs;
    
    private PlaybackParametersCheckpoint(PlaybackParameters paramPlaybackParameters, long paramLong1, long paramLong2)
    {
      playbackParameters = paramPlaybackParameters;
      mediaTimeUs = paramLong1;
      positionUs = paramLong2;
    }
  }
  
  private final class PositionTrackerListener
    implements AudioTrackPositionTracker.Listener
  {
    private PositionTrackerListener() {}
    
    public void onInvalidLatency(long paramLong)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Ignoring impossibly large audio latency: ");
      localStringBuilder.append(paramLong);
      Log.w("AudioTrack", localStringBuilder.toString());
    }
    
    public void onPositionFramesMismatch(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Spurious audio timestamp (frame position mismatch): ");
      ((StringBuilder)localObject).append(paramLong1);
      ((StringBuilder)localObject).append(", ");
      ((StringBuilder)localObject).append(paramLong2);
      ((StringBuilder)localObject).append(", ");
      ((StringBuilder)localObject).append(paramLong3);
      ((StringBuilder)localObject).append(", ");
      ((StringBuilder)localObject).append(paramLong4);
      ((StringBuilder)localObject).append(", ");
      ((StringBuilder)localObject).append(DefaultAudioSink.this.getSubmittedFrames());
      ((StringBuilder)localObject).append(", ");
      ((StringBuilder)localObject).append(DefaultAudioSink.this.getWrittenFrames());
      localObject = ((StringBuilder)localObject).toString();
      if (!DefaultAudioSink.failOnSpuriousAudioTimestamp)
      {
        Log.w("AudioTrack", (String)localObject);
        return;
      }
      throw new DefaultAudioSink.InvalidAudioTrackTimestampException((String)localObject, null);
    }
    
    public void onSystemTimeUsMismatch(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Spurious audio timestamp (system clock mismatch): ");
      ((StringBuilder)localObject).append(paramLong1);
      ((StringBuilder)localObject).append(", ");
      ((StringBuilder)localObject).append(paramLong2);
      ((StringBuilder)localObject).append(", ");
      ((StringBuilder)localObject).append(paramLong3);
      ((StringBuilder)localObject).append(", ");
      ((StringBuilder)localObject).append(paramLong4);
      ((StringBuilder)localObject).append(", ");
      ((StringBuilder)localObject).append(DefaultAudioSink.this.getSubmittedFrames());
      ((StringBuilder)localObject).append(", ");
      ((StringBuilder)localObject).append(DefaultAudioSink.this.getWrittenFrames());
      localObject = ((StringBuilder)localObject).toString();
      if (!DefaultAudioSink.failOnSpuriousAudioTimestamp)
      {
        Log.w("AudioTrack", (String)localObject);
        return;
      }
      throw new DefaultAudioSink.InvalidAudioTrackTimestampException((String)localObject, null);
    }
    
    public void onUnderrun(int paramInt, long paramLong)
    {
      if (listener != null)
      {
        long l1 = SystemClock.elapsedRealtime();
        long l2 = lastFeedElapsedRealtimeMs;
        listener.onUnderrun(paramInt, paramLong, l1 - l2);
      }
    }
  }
}
