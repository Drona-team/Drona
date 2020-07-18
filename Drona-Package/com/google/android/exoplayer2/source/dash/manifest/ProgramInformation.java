package com.google.android.exoplayer2.source.dash.manifest;

import com.google.android.exoplayer2.util.Util;

public class ProgramInformation
{
  public final String copyright;
  public final String lang;
  public final String moreInformationURL;
  public final String source;
  public final String title;
  
  public ProgramInformation(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    title = paramString1;
    source = paramString2;
    copyright = paramString3;
    moreInformationURL = paramString4;
    lang = paramString5;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject != null)
    {
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (ProgramInformation)paramObject;
      return (Util.areEqual(title, title)) && (Util.areEqual(source, source)) && (Util.areEqual(copyright, copyright)) && (Util.areEqual(moreInformationURL, moreInformationURL)) && (Util.areEqual(lang, lang));
    }
    return false;
  }
  
  public int hashCode()
  {
    String str = title;
    int n = 0;
    int i;
    if (str != null) {
      i = title.hashCode();
    } else {
      i = 0;
    }
    int j;
    if (source != null) {
      j = source.hashCode();
    } else {
      j = 0;
    }
    int k;
    if (copyright != null) {
      k = copyright.hashCode();
    } else {
      k = 0;
    }
    int m;
    if (moreInformationURL != null) {
      m = moreInformationURL.hashCode();
    } else {
      m = 0;
    }
    if (lang != null) {
      n = lang.hashCode();
    }
    return ((((527 + i) * 31 + j) * 31 + k) * 31 + m) * 31 + n;
  }
}
