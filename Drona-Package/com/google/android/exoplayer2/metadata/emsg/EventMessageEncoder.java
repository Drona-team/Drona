package com.google.android.exoplayer2.metadata.emsg;

import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class EventMessageEncoder
{
  private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
  private final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
  
  public EventMessageEncoder() {}
  
  private static void writeNullTerminatedString(DataOutputStream paramDataOutputStream, String paramString)
    throws IOException
  {
    paramDataOutputStream.writeBytes(paramString);
    paramDataOutputStream.writeByte(0);
  }
  
  private static void writeUnsignedInt(DataOutputStream paramDataOutputStream, long paramLong)
    throws IOException
  {
    paramDataOutputStream.writeByte((int)(paramLong >>> 24) & 0xFF);
    paramDataOutputStream.writeByte((int)(paramLong >>> 16) & 0xFF);
    paramDataOutputStream.writeByte((int)(paramLong >>> 8) & 0xFF);
    paramDataOutputStream.writeByte((int)paramLong & 0xFF);
  }
  
  public byte[] encode(EventMessage paramEventMessage, long paramLong)
  {
    boolean bool;
    if (paramLong >= 0L) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    byteArrayOutputStream.reset();
    Object localObject1 = dataOutputStream;
    Object localObject2 = schemeIdUri;
    try
    {
      writeNullTerminatedString((DataOutputStream)localObject1, (String)localObject2);
      if (value != null) {
        localObject1 = value;
      } else {
        localObject1 = "";
      }
      localObject2 = dataOutputStream;
      writeNullTerminatedString((DataOutputStream)localObject2, (String)localObject1);
      localObject1 = dataOutputStream;
      writeUnsignedInt((DataOutputStream)localObject1, paramLong);
      long l = presentationTimeUs;
      l = Util.scaleLargeTimestamp(l, paramLong, 1000000L);
      localObject1 = dataOutputStream;
      writeUnsignedInt((DataOutputStream)localObject1, l);
      l = durationMs;
      paramLong = Util.scaleLargeTimestamp(l, paramLong, 1000L);
      localObject1 = dataOutputStream;
      writeUnsignedInt((DataOutputStream)localObject1, paramLong);
      localObject1 = dataOutputStream;
      paramLong = id;
      writeUnsignedInt((DataOutputStream)localObject1, paramLong);
      localObject1 = dataOutputStream;
      paramEventMessage = messageData;
      ((DataOutputStream)localObject1).write(paramEventMessage);
      paramEventMessage = dataOutputStream;
      paramEventMessage.flush();
      paramEventMessage = byteArrayOutputStream;
      paramEventMessage = paramEventMessage.toByteArray();
      return paramEventMessage;
    }
    catch (IOException paramEventMessage)
    {
      throw new RuntimeException(paramEventMessage);
    }
  }
}
