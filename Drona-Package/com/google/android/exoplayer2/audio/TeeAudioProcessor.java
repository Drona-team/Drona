package com.google.android.exoplayer2.audio;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class TeeAudioProcessor
  implements AudioProcessor
{
  private final AudioBufferSink audioBufferSink;
  private ByteBuffer buffer;
  private int channelCount;
  private int encoding;
  private boolean inputEnded;
  private boolean isActive;
  private ByteBuffer outputBuffer;
  private int sampleRateHz;
  
  public TeeAudioProcessor(AudioBufferSink paramAudioBufferSink)
  {
    audioBufferSink = ((AudioBufferSink)Assertions.checkNotNull(paramAudioBufferSink));
    buffer = AudioProcessor.EMPTY_BUFFER;
    outputBuffer = AudioProcessor.EMPTY_BUFFER;
    channelCount = -1;
    sampleRateHz = -1;
  }
  
  public boolean configure(int paramInt1, int paramInt2, int paramInt3)
    throws AudioProcessor.UnhandledFormatException
  {
    sampleRateHz = paramInt1;
    channelCount = paramInt2;
    encoding = paramInt3;
    boolean bool = isActive;
    isActive = true;
    return bool ^ true;
  }
  
  public void flush()
  {
    outputBuffer = AudioProcessor.EMPTY_BUFFER;
    inputEnded = false;
    audioBufferSink.flush(sampleRateHz, channelCount, encoding);
  }
  
  public ByteBuffer getOutput()
  {
    ByteBuffer localByteBuffer = outputBuffer;
    outputBuffer = AudioProcessor.EMPTY_BUFFER;
    return localByteBuffer;
  }
  
  public int getOutputChannelCount()
  {
    return channelCount;
  }
  
  public int getOutputEncoding()
  {
    return encoding;
  }
  
  public int getOutputSampleRateHz()
  {
    return sampleRateHz;
  }
  
  public boolean isActive()
  {
    return isActive;
  }
  
  public boolean isEnded()
  {
    return (inputEnded) && (buffer == AudioProcessor.EMPTY_BUFFER);
  }
  
  public void queueEndOfStream()
  {
    inputEnded = true;
  }
  
  public void queueInput(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.remaining();
    if (i == 0) {
      return;
    }
    audioBufferSink.handleBuffer(paramByteBuffer.asReadOnlyBuffer());
    if (buffer.capacity() < i) {
      buffer = ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
    } else {
      buffer.clear();
    }
    buffer.put(paramByteBuffer);
    buffer.flip();
    outputBuffer = buffer;
  }
  
  public void reset()
  {
    flush();
    buffer = AudioProcessor.EMPTY_BUFFER;
    sampleRateHz = -1;
    channelCount = -1;
    encoding = -1;
  }
  
  public static abstract interface AudioBufferSink
  {
    public abstract void flush(int paramInt1, int paramInt2, int paramInt3);
    
    public abstract void handleBuffer(ByteBuffer paramByteBuffer);
  }
  
  public static final class WavFileAudioBufferSink
    implements TeeAudioProcessor.AudioBufferSink
  {
    private static final int FILE_SIZE_MINUS_44_OFFSET = 40;
    private static final int FILE_SIZE_MINUS_8_OFFSET = 4;
    private static final int HEADER_LENGTH = 44;
    private static final String PAGE_KEY = "WaveFileAudioBufferSink";
    private int bytesWritten;
    private int channelCount;
    private int counter;
    private int encoding;
    private final String outputFileNamePrefix;
    @Nullable
    private RandomAccessFile randomAccessFile;
    private int sampleRateHz;
    private final byte[] scratchBuffer;
    private final ByteBuffer scratchByteBuffer;
    
    public WavFileAudioBufferSink(String paramString)
    {
      outputFileNamePrefix = paramString;
      scratchBuffer = new byte['?'];
      scratchByteBuffer = ByteBuffer.wrap(scratchBuffer).order(ByteOrder.LITTLE_ENDIAN);
    }
    
    private String getNextOutputFileName()
    {
      String str = outputFileNamePrefix;
      int i = counter;
      counter = (i + 1);
      return Util.formatInvariant("%s-%04d.wav", new Object[] { str, Integer.valueOf(i) });
    }
    
    private void maybePrepareFile()
      throws IOException
    {
      if (randomAccessFile != null) {
        return;
      }
      RandomAccessFile localRandomAccessFile = new RandomAccessFile(getNextOutputFileName(), "rw");
      writeFileHeader(localRandomAccessFile);
      randomAccessFile = localRandomAccessFile;
      bytesWritten = 44;
    }
    
    private void reset()
      throws IOException
    {
      RandomAccessFile localRandomAccessFile = randomAccessFile;
      if (localRandomAccessFile == null) {
        return;
      }
      Object localObject = scratchByteBuffer;
      try
      {
        ((ByteBuffer)localObject).clear();
        localObject = scratchByteBuffer;
        int i = bytesWritten;
        ((ByteBuffer)localObject).putInt(i - 8);
        localRandomAccessFile.seek(4L);
        localObject = scratchBuffer;
        localRandomAccessFile.write((byte[])localObject, 0, 4);
        localObject = scratchByteBuffer;
        ((ByteBuffer)localObject).clear();
        localObject = scratchByteBuffer;
        i = bytesWritten;
        ((ByteBuffer)localObject).putInt(i - 44);
        localRandomAccessFile.seek(40L);
        localObject = scratchBuffer;
        localRandomAccessFile.write((byte[])localObject, 0, 4);
      }
      catch (IOException localIOException)
      {
        Log.w("WaveFileAudioBufferSink", "Error updating file size", localIOException);
      }
      try
      {
        localRandomAccessFile.close();
        randomAccessFile = null;
        return;
      }
      catch (Throwable localThrowable)
      {
        randomAccessFile = null;
        throw localThrowable;
      }
    }
    
    private void writeBuffer(ByteBuffer paramByteBuffer)
      throws IOException
    {
      RandomAccessFile localRandomAccessFile = (RandomAccessFile)Assertions.checkNotNull(randomAccessFile);
      while (paramByteBuffer.hasRemaining())
      {
        int i = Math.min(paramByteBuffer.remaining(), scratchBuffer.length);
        paramByteBuffer.get(scratchBuffer, 0, i);
        localRandomAccessFile.write(scratchBuffer, 0, i);
        bytesWritten += i;
      }
    }
    
    private void writeFileHeader(RandomAccessFile paramRandomAccessFile)
      throws IOException
    {
      paramRandomAccessFile.writeInt(WavUtil.RIFF_FOURCC);
      paramRandomAccessFile.writeInt(-1);
      paramRandomAccessFile.writeInt(WavUtil.WAVE_FOURCC);
      paramRandomAccessFile.writeInt(WavUtil.FMT_FOURCC);
      scratchByteBuffer.clear();
      scratchByteBuffer.putInt(16);
      scratchByteBuffer.putShort((short)WavUtil.getTypeForEncoding(encoding));
      scratchByteBuffer.putShort((short)channelCount);
      scratchByteBuffer.putInt(sampleRateHz);
      int i = Util.getPcmFrameSize(encoding, channelCount);
      scratchByteBuffer.putInt(sampleRateHz * i);
      scratchByteBuffer.putShort((short)i);
      scratchByteBuffer.putShort((short)(i * 8 / channelCount));
      paramRandomAccessFile.write(scratchBuffer, 0, scratchByteBuffer.position());
      paramRandomAccessFile.writeInt(WavUtil.DATA_FOURCC);
      paramRandomAccessFile.writeInt(-1);
    }
    
    public void flush(int paramInt1, int paramInt2, int paramInt3)
    {
      try
      {
        reset();
      }
      catch (IOException localIOException)
      {
        Log.e("WaveFileAudioBufferSink", "Error resetting", localIOException);
      }
      sampleRateHz = paramInt1;
      channelCount = paramInt2;
      encoding = paramInt3;
    }
    
    public void handleBuffer(ByteBuffer paramByteBuffer)
    {
      try
      {
        maybePrepareFile();
        writeBuffer(paramByteBuffer);
        return;
      }
      catch (IOException paramByteBuffer)
      {
        Log.e("WaveFileAudioBufferSink", "Error writing data", paramByteBuffer);
      }
    }
  }
}
