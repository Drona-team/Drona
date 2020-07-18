package com.bugsnag.android;

import androidx.annotation.NonNull;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Breadcrumb
  implements JsonStream.Streamable
{
  private static final String DEFAULT_NAME = "manual";
  private static final int MAX_MESSAGE_LENGTH = 140;
  private static final String MESSAGE_METAKEY = "message";
  private static final String METADATA_KEY = "metaData";
  private static final String NAME_KEY = "name";
  private static final String TIMESTAMP_KEY = "timestamp";
  private static final String TYPE_KEY = "type";
  @NonNull
  private final Map<String, String> metadata;
  @NonNull
  private final String name;
  @NonNull
  private final String timestamp;
  @NonNull
  private final BreadcrumbType type;
  
  Breadcrumb(String paramString)
  {
    this("manual", BreadcrumbType.MANUAL, Collections.singletonMap("message", paramString.substring(0, Math.min(paramString.length(), 140))));
  }
  
  Breadcrumb(String paramString, BreadcrumbType paramBreadcrumbType, Date paramDate, Map paramMap) {}
  
  Breadcrumb(String paramString, BreadcrumbType paramBreadcrumbType, Map paramMap)
  {
    this(paramString, paramBreadcrumbType, new Date(), paramMap);
  }
  
  public Map getMetadata()
  {
    return metadata;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getTimestamp()
  {
    return timestamp;
  }
  
  public BreadcrumbType getType()
  {
    return type;
  }
  
  int payloadSize()
    throws IOException
  {
    StringWriter localStringWriter = new StringWriter();
    toStream(new JsonStream(localStringWriter));
    return localStringWriter.toString().length();
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    paramJsonStream.beginObject();
    paramJsonStream.name("timestamp").value(timestamp);
    paramJsonStream.name("name").value(name);
    paramJsonStream.name("type").value(type.toString());
    paramJsonStream.name("metaData");
    paramJsonStream.beginObject();
    Object localObject = new ArrayList(metadata.keySet());
    Collections.sort((List)localObject, String.CASE_INSENSITIVE_ORDER);
    localObject = ((List)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str = (String)((Iterator)localObject).next();
      paramJsonStream.name(str).value((String)metadata.get(str));
    }
    paramJsonStream.endObject();
    paramJsonStream.endObject();
  }
}
