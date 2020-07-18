package com.bugsnag.android;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SessionTrackingPayload
  implements JsonStream.Streamable
{
  private final Map<String, Object> appDataSummary;
  private final Map<String, Object> deviceDataSummary;
  private final List<File> files;
  private final Notifier notifier;
  private final Session session;
  
  SessionTrackingPayload(Session paramSession, List paramList, AppData paramAppData, DeviceData paramDeviceData)
  {
    appDataSummary = paramAppData.getAppDataSummary();
    deviceDataSummary = paramDeviceData.getDeviceDataSummary();
    notifier = Notifier.getInstance();
    session = paramSession;
    files = paramList;
  }
  
  Map getDevice()
  {
    return deviceDataSummary;
  }
  
  Session getSession()
  {
    return session;
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    paramJsonStream.beginObject();
    paramJsonStream.name("notifier").value(notifier);
    paramJsonStream.name("app").value(appDataSummary);
    paramJsonStream.name("device").value(deviceDataSummary);
    paramJsonStream.name("sessions").beginArray();
    if (session == null)
    {
      Iterator localIterator = files.iterator();
      while (localIterator.hasNext()) {
        paramJsonStream.value((File)localIterator.next());
      }
    }
    paramJsonStream.value(session);
    paramJsonStream.endArray();
    paramJsonStream.endObject();
  }
}
