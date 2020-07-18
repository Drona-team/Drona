package com.bugsnag.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.File;
import java.io.IOException;

public class Report
  implements JsonStream.Streamable
{
  @NonNull
  private String apiKey;
  private transient boolean cachingDisabled;
  @Nullable
  private final Error error;
  @Nullable
  private final File errorFile;
  @NonNull
  private final Notifier notifier;
  
  Report(String paramString, Error paramError)
  {
    this(paramString, null, paramError);
  }
  
  Report(String paramString, File paramFile)
  {
    this(paramString, paramFile, null);
  }
  
  private Report(String paramString, File paramFile, Error paramError)
  {
    error = paramError;
    errorFile = paramFile;
    notifier = Notifier.getInstance();
    apiKey = paramString;
  }
  
  public String getApiKey()
  {
    return apiKey;
  }
  
  public Error getError()
  {
    return error;
  }
  
  public Notifier getNotifier()
  {
    return notifier;
  }
  
  boolean isCachingDisabled()
  {
    return cachingDisabled;
  }
  
  public void setApiKey(String paramString)
  {
    apiKey = paramString;
  }
  
  void setCachingDisabled(boolean paramBoolean)
  {
    cachingDisabled = paramBoolean;
  }
  
  public void setNotifierName(String paramString)
  {
    notifier.setName(paramString);
  }
  
  public void setNotifierURL(String paramString)
  {
    notifier.setURL(paramString);
  }
  
  public void setNotifierVersion(String paramString)
  {
    notifier.setVersion(paramString);
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    paramJsonStream.beginObject();
    paramJsonStream.name("apiKey").value(apiKey);
    paramJsonStream.name("payloadVersion").value("4.0");
    paramJsonStream.name("notifier").value(notifier);
    paramJsonStream.name("events").beginArray();
    if (error != null) {
      paramJsonStream.value(error);
    } else if (errorFile != null) {
      paramJsonStream.value(errorFile);
    } else {
      Logger.warn("Expected error or errorFile, found empty payload instead");
    }
    paramJsonStream.endArray();
    paramJsonStream.endObject();
  }
}
