package com.google.android.exoplayer2.source.dash.manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;

public final class Descriptor
{
  @NonNull
  public final String schemeIdUri;
  @Nullable
  public final String string;
  @Nullable
  public final String value;
  
  public Descriptor(String paramString1, String paramString2, String paramString3)
  {
    schemeIdUri = paramString1;
    value = paramString2;
    string = paramString3;
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
      paramObject = (Descriptor)paramObject;
      return (Util.areEqual(schemeIdUri, schemeIdUri)) && (Util.areEqual(value, value)) && (Util.areEqual(string, string));
    }
    return false;
  }
  
  public int hashCode()
  {
    String str = schemeIdUri;
    int k = 0;
    int i;
    if (str != null) {
      i = schemeIdUri.hashCode();
    } else {
      i = 0;
    }
    int j;
    if (value != null) {
      j = value.hashCode();
    } else {
      j = 0;
    }
    if (string != null) {
      k = string.hashCode();
    }
    return (i * 31 + j) * 31 + k;
  }
}
