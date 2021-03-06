package client.testing.util;

import android.content.Context;
import android.text.TextUtils;
import client.testing.android.GsonFactory;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

public class VersionConfig
{
  private static final Pattern DOT_PATTERN = Pattern.compile(".", 16);
  private static final String PAGE_KEY = "ai.api.util.VersionConfig";
  private static final Map<String, ai.api.util.VersionConfig> configuration = new HashMap();
  private boolean autoStopRecognizer = false;
  private boolean destroyRecognizer = true;
  
  static
  {
    configuration.put("5.9.26", new VersionConfig(true, true));
    configuration.put("4.7.13", new VersionConfig(false, false));
  }
  
  private VersionConfig() {}
  
  private VersionConfig(boolean paramBoolean1, boolean paramBoolean2)
  {
    destroyRecognizer = paramBoolean1;
    autoStopRecognizer = paramBoolean2;
  }
  
  private static VersionConfig getConfigByVersion(Context paramContext)
  {
    long l3 = numberFromBuildVersion(RecognizerChecker.getGoogleRecognizerVersion(paramContext));
    paramContext = new VersionConfig();
    Iterator localIterator = configuration.entrySet().iterator();
    long l1 = 0L;
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      if (!TextUtils.isEmpty(str))
      {
        long l2 = numberFromBuildVersion(str);
        if ((l3 >= l2) && (l1 < l2))
        {
          destroyRecognizer = getValuedestroyRecognizer;
          autoStopRecognizer = getValueautoStopRecognizer;
          l1 = l2;
        }
      }
    }
    return paramContext;
  }
  
  public static VersionConfig init(Context paramContext)
  {
    return getConfigByVersion(paramContext);
  }
  
  private static long numberFromBuildVersion(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return 0L;
    }
    paramString = DOT_PATTERN.split(paramString);
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < Math.min(3, paramString.length))
    {
      localStringBuilder.append(paramString[i]);
      i += 1;
    }
    try
    {
      long l = Long.parseLong(localStringBuilder.toString());
      return l;
    }
    catch (NumberFormatException paramString) {}
    return 0L;
  }
  
  public boolean isAutoStopRecognizer()
  {
    return autoStopRecognizer;
  }
  
  public boolean isDestroyRecognizer()
  {
    return destroyRecognizer;
  }
  
  public String toString()
  {
    return GsonFactory.getGson().toJson(this);
  }
}
