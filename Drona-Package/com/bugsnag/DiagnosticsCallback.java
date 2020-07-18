package com.bugsnag;

import com.bugsnag.android.Callback;
import com.bugsnag.android.Error;
import com.bugsnag.android.MetaData;
import com.bugsnag.android.Notifier;
import com.bugsnag.android.Report;
import com.bugsnag.android.Severity;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class DiagnosticsCallback
  implements Callback
{
  static final String NOTIFIER_NAME = "Bugsnag for React Native";
  static final String NOTIFIER_URL = "https://github.com/bugsnag/bugsnag-react-native";
  private final String bugsnagAndroidVersion;
  private final String context;
  private final String groupingHash;
  private final String libraryVersion;
  private final Map<String, Object> metadata;
  private final Severity severity;
  
  DiagnosticsCallback(String paramString1, String paramString2, ReadableMap paramReadableMap)
  {
    libraryVersion = paramString1;
    bugsnagAndroidVersion = paramString2;
    severity = parseSeverity(paramReadableMap.getString("severity"));
    metadata = readObjectMap(paramReadableMap.getMap("metadata"));
    if (paramReadableMap.hasKey("context")) {
      context = paramReadableMap.getString("context");
    } else {
      context = null;
    }
    if (paramReadableMap.hasKey("groupingHash"))
    {
      groupingHash = paramReadableMap.getString("groupingHash");
      return;
    }
    groupingHash = null;
  }
  
  public void beforeNotify(Report paramReport)
  {
    paramReport.getNotifier().setName("Bugsnag for React Native");
    paramReport.getNotifier().setURL("https://github.com/bugsnag/bugsnag-react-native");
    paramReport.getNotifier().setVersion(String.format("%s (Android %s)", new Object[] { libraryVersion, bugsnagAndroidVersion }));
    if ((groupingHash != null) && (groupingHash.length() > 0)) {
      paramReport.getError().setGroupingHash(groupingHash);
    }
    if ((context != null) && (context.length() > 0)) {
      paramReport.getError().setContext(context);
    }
    if (metadata != null)
    {
      paramReport = paramReport.getError().getMetaData();
      Iterator localIterator1 = metadata.keySet().iterator();
      while (localIterator1.hasNext())
      {
        String str1 = (String)localIterator1.next();
        Object localObject = metadata.get(str1);
        if ((localObject instanceof Map))
        {
          localObject = (Map)localObject;
          Iterator localIterator2 = ((Map)localObject).keySet().iterator();
          while (localIterator2.hasNext())
          {
            String str2 = (String)localIterator2.next();
            paramReport.addToTab(str1, str2, ((Map)localObject).get(str2));
          }
        }
      }
    }
  }
  
  Severity parseSeverity(String paramString)
  {
    int i = paramString.hashCode();
    if (i != 3237038)
    {
      if (i != 96784904)
      {
        if ((i == 1124446108) && (paramString.equals("warning")))
        {
          i = 2;
          break label70;
        }
      }
      else if (paramString.equals("error"))
      {
        i = 0;
        break label70;
      }
    }
    else if (paramString.equals("info"))
    {
      i = 1;
      break label70;
    }
    i = -1;
    switch (i)
    {
    default: 
      return Severity.WARNING;
    case 1: 
      label70:
      return Severity.INFO;
    }
    return Severity.ERROR;
  }
  
  Map readObjectMap(ReadableMap paramReadableMap)
  {
    HashMap localHashMap = new HashMap();
    ReadableMapKeySetIterator localReadableMapKeySetIterator = paramReadableMap.keySetIterator();
    while (localReadableMapKeySetIterator.hasNextKey())
    {
      String str1 = localReadableMapKeySetIterator.nextKey();
      ReadableMap localReadableMap = paramReadableMap.getMap(str1);
      String str2 = localReadableMap.getString("type");
      int i = -1;
      int j = str2.hashCode();
      if (j != -1034364087)
      {
        if (j != -891985903)
        {
          if (j != 107868)
          {
            if ((j == 64711720) && (str2.equals("boolean"))) {
              i = 0;
            }
          }
          else if (str2.equals("map")) {
            i = 3;
          }
        }
        else if (str2.equals("string")) {
          i = 2;
        }
      }
      else if (str2.equals("number")) {
        i = 1;
      }
      switch (i)
      {
      default: 
        break;
      case 3: 
        localHashMap.put(str1, readObjectMap(localReadableMap.getMap("value")));
        break;
      case 2: 
        localHashMap.put(str1, localReadableMap.getString("value"));
        break;
      case 1: 
        localHashMap.put(str1, Double.valueOf(localReadableMap.getDouble("value")));
        break;
      case 0: 
        localHashMap.put(str1, Boolean.valueOf(localReadableMap.getBoolean("value")));
      }
    }
    return localHashMap;
  }
}
