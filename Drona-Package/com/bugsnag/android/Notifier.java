package com.bugsnag.android;

import androidx.annotation.NonNull;
import java.io.IOException;

public class Notifier
  implements JsonStream.Streamable
{
  private static final String NOTIFIER_NAME = "Android Bugsnag Notifier";
  private static final String NOTIFIER_URL = "https://bugsnag.com";
  private static final String NOTIFIER_VERSION = "4.22.3";
  private static final Notifier instance = new Notifier();
  @NonNull
  private String name = "Android Bugsnag Notifier";
  @NonNull
  private String url = "https://bugsnag.com";
  @NonNull
  private String version = "4.22.3";
  
  public Notifier() {}
  
  public static Notifier getInstance()
  {
    return instance;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getURL()
  {
    return url;
  }
  
  public String getVersion()
  {
    return version;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public void setURL(String paramString)
  {
    url = paramString;
  }
  
  public void setVersion(String paramString)
  {
    version = paramString;
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    paramJsonStream.beginObject();
    paramJsonStream.name("name").value(name);
    paramJsonStream.name("version").value(version);
    paramJsonStream.name("url").value(url);
    paramJsonStream.endObject();
  }
}
