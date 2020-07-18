package com.google.android.exoplayer2.text;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.Buffer;
import com.google.android.exoplayer2.decoder.Decoder;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.decoder.OutputBuffer;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.util.Collections;
import java.util.List;

public final class TextRenderer
  extends BaseRenderer
  implements Handler.Callback
{
  private static final int MSG_UPDATE_OUTPUT = 0;
  private static final int REPLACEMENT_STATE_NONE = 0;
  private static final int REPLACEMENT_STATE_SIGNAL_END_OF_STREAM = 1;
  private static final int REPLACEMENT_STATE_WAIT_END_OF_STREAM = 2;
  private SubtitleDecoder decoder;
  private final SubtitleDecoderFactory decoderFactory;
  private int decoderReplacementState;
  private final FormatHolder formatHolder;
  private boolean inputStreamEnded;
  private SubtitleInputBuffer nextInputBuffer;
  private SubtitleOutputBuffer nextSubtitle;
  private int nextSubtitleEventIndex;
  private final TextOutput output;
  @Nullable
  private final Handler outputHandler;
  private boolean outputStreamEnded;
  private Format streamFormat;
  private SubtitleOutputBuffer subtitle;
  
  public TextRenderer(TextOutput paramTextOutput, Looper paramLooper)
  {
    this(paramTextOutput, paramLooper, SubtitleDecoderFactory.DEFAULT);
  }
  
  public TextRenderer(TextOutput paramTextOutput, Looper paramLooper, SubtitleDecoderFactory paramSubtitleDecoderFactory)
  {
    super(3);
    output = ((TextOutput)Assertions.checkNotNull(paramTextOutput));
    if (paramLooper == null) {
      paramTextOutput = null;
    } else {
      paramTextOutput = Util.createHandler(paramLooper, this);
    }
    outputHandler = paramTextOutput;
    decoderFactory = paramSubtitleDecoderFactory;
    formatHolder = new FormatHolder();
  }
  
  private void clearOutput()
  {
    updateOutput(Collections.emptyList());
  }
  
  private long getNextEventTime()
  {
    if ((nextSubtitleEventIndex != -1) && (nextSubtitleEventIndex < subtitle.getEventTimeCount())) {
      return subtitle.getEventTime(nextSubtitleEventIndex);
    }
    return Long.MAX_VALUE;
  }
  
  private void invokeUpdateOutputInternal(List paramList)
  {
    output.onCues(paramList);
  }
  
  private void releaseBuffers()
  {
    nextInputBuffer = null;
    nextSubtitleEventIndex = -1;
    if (subtitle != null)
    {
      subtitle.release();
      subtitle = null;
    }
    if (nextSubtitle != null)
    {
      nextSubtitle.release();
      nextSubtitle = null;
    }
  }
  
  private void releaseDecoder()
  {
    releaseBuffers();
    decoder.release();
    decoder = null;
    decoderReplacementState = 0;
  }
  
  private void replaceDecoder()
  {
    releaseDecoder();
    decoder = decoderFactory.createDecoder(streamFormat);
  }
  
  private void updateOutput(List paramList)
  {
    if (outputHandler != null)
    {
      outputHandler.obtainMessage(0, paramList).sendToTarget();
      return;
    }
    invokeUpdateOutputInternal(paramList);
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    if (what == 0)
    {
      invokeUpdateOutputInternal((List)obj);
      return true;
    }
    throw new IllegalStateException();
  }
  
  public boolean isEnded()
  {
    return outputStreamEnded;
  }
  
  public boolean isReady()
  {
    return true;
  }
  
  protected void onDisabled()
  {
    streamFormat = null;
    clearOutput();
    releaseDecoder();
  }
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
  {
    clearOutput();
    inputStreamEnded = false;
    outputStreamEnded = false;
    if (decoderReplacementState != 0)
    {
      replaceDecoder();
      return;
    }
    releaseBuffers();
    decoder.flush();
  }
  
  protected void onStreamChanged(Format[] paramArrayOfFormat, long paramLong)
    throws ExoPlaybackException
  {
    streamFormat = paramArrayOfFormat[0];
    if (decoder != null)
    {
      decoderReplacementState = 1;
      return;
    }
    decoder = decoderFactory.createDecoder(streamFormat);
  }
  
  public void render(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    if (outputStreamEnded) {
      return;
    }
    if (nextSubtitle == null)
    {
      decoder.setPositionUs(paramLong1);
      Object localObject1 = decoder;
      try
      {
        localObject1 = ((Decoder)localObject1).dequeueOutputBuffer();
        nextSubtitle = ((SubtitleOutputBuffer)localObject1);
      }
      catch (SubtitleDecoderException localSubtitleDecoderException1)
      {
        throw ExoPlaybackException.createForRenderer(localSubtitleDecoderException1, getIndex());
      }
    }
    if (getState() != 2) {
      return;
    }
    if (subtitle != null)
    {
      paramLong2 = getNextEventTime();
      for (i = 0; paramLong2 <= paramLong1; i = 1)
      {
        nextSubtitleEventIndex += 1;
        paramLong2 = getNextEventTime();
      }
    }
    int i = 0;
    int j = i;
    if (nextSubtitle != null) {
      if (nextSubtitle.isEndOfStream())
      {
        j = i;
        if (i == 0)
        {
          j = i;
          if (getNextEventTime() == Long.MAX_VALUE) {
            if (decoderReplacementState == 2)
            {
              replaceDecoder();
              j = i;
            }
            else
            {
              releaseBuffers();
              outputStreamEnded = true;
              j = i;
            }
          }
        }
      }
      else
      {
        j = i;
        if (nextSubtitle.timeUs <= paramLong1)
        {
          if (subtitle != null) {
            subtitle.release();
          }
          subtitle = nextSubtitle;
          nextSubtitle = null;
          nextSubtitleEventIndex = subtitle.getNextEventTimeIndex(paramLong1);
          j = 1;
        }
      }
    }
    if (j != 0) {
      updateOutput(subtitle.getCues(paramLong1));
    }
    if (decoderReplacementState == 2) {
      return;
    }
    for (;;)
    {
      Object localObject2;
      if (!inputStreamEnded) {
        if (nextInputBuffer == null) {
          localObject2 = decoder;
        }
      }
      try
      {
        localObject2 = ((Decoder)localObject2).dequeueInputBuffer();
        nextInputBuffer = ((SubtitleInputBuffer)localObject2);
        if (nextInputBuffer == null) {
          return;
        }
        if (decoderReplacementState == 1)
        {
          localObject2 = nextInputBuffer;
          ((Buffer)localObject2).setFlags(4);
          localObject2 = decoder;
          localSubtitleInputBuffer = nextInputBuffer;
          ((Decoder)localObject2).queueInputBuffer(localSubtitleInputBuffer);
          nextInputBuffer = null;
          decoderReplacementState = 2;
          return;
        }
        localObject2 = formatHolder;
        SubtitleInputBuffer localSubtitleInputBuffer = nextInputBuffer;
        i = readSource((FormatHolder)localObject2, localSubtitleInputBuffer, false);
        if (i == -4)
        {
          localObject2 = nextInputBuffer;
          boolean bool = ((Buffer)localObject2).isEndOfStream();
          if (bool)
          {
            inputStreamEnded = true;
          }
          else
          {
            nextInputBuffer.subsampleOffsetUs = formatHolder.format.subsampleOffsetUs;
            localObject2 = nextInputBuffer;
            ((DecoderInputBuffer)localObject2).flip();
          }
          localObject2 = decoder;
          localSubtitleInputBuffer = nextInputBuffer;
          ((Decoder)localObject2).queueInputBuffer(localSubtitleInputBuffer);
          nextInputBuffer = null;
          continue;
        }
        if (i != -3) {
          continue;
        }
        return;
      }
      catch (SubtitleDecoderException localSubtitleDecoderException2)
      {
        throw ExoPlaybackException.createForRenderer(localSubtitleDecoderException2, getIndex());
      }
    }
  }
  
  public int supportsFormat(Format paramFormat)
  {
    if (decoderFactory.supportsFormat(paramFormat))
    {
      if (BaseRenderer.supportsFormatDrm(null, drmInitData)) {
        return 4;
      }
      return 2;
    }
    if (MimeTypes.isText(sampleMimeType)) {
      return 1;
    }
    return 0;
  }
  
  @Deprecated
  public static abstract interface Output
    extends TextOutput
  {}
}