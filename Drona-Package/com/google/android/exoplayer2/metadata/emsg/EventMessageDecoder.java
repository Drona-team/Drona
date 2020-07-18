package com.google.android.exoplayer2.metadata.emsg;

import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.Metadata.Entry;
import com.google.android.exoplayer2.metadata.MetadataDecoder;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class EventMessageDecoder
  implements MetadataDecoder
{
  public EventMessageDecoder() {}
  
  public Metadata decode(MetadataInputBuffer paramMetadataInputBuffer)
  {
    Object localObject = data;
    paramMetadataInputBuffer = ((ByteBuffer)localObject).array();
    int i = ((ByteBuffer)localObject).limit();
    localObject = new ParsableByteArray(paramMetadataInputBuffer, i);
    String str1 = (String)Assertions.checkNotNull(((ParsableByteArray)localObject).readNullTerminatedString());
    String str2 = (String)Assertions.checkNotNull(((ParsableByteArray)localObject).readNullTerminatedString());
    long l1 = ((ParsableByteArray)localObject).readUnsignedInt();
    long l2 = Util.scaleLargeTimestamp(((ParsableByteArray)localObject).readUnsignedInt(), 1000000L, l1);
    return new Metadata(new Metadata.Entry[] { new EventMessage(str1, str2, Util.scaleLargeTimestamp(((ParsableByteArray)localObject).readUnsignedInt(), 1000L, l1), ((ParsableByteArray)localObject).readUnsignedInt(), Arrays.copyOfRange(paramMetadataInputBuffer, ((ParsableByteArray)localObject).getPosition(), i), l2) });
  }
}
