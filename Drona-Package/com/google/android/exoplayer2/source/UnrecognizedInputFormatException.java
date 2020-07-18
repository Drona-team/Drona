package com.google.android.exoplayer2.source;

import android.net.Uri;
import com.google.android.exoplayer2.ParserException;

public class UnrecognizedInputFormatException
  extends ParserException
{
  public final Uri ringtoneUri;
  
  public UnrecognizedInputFormatException(String paramString, Uri paramUri)
  {
    super(paramString);
    ringtoneUri = paramUri;
  }
}