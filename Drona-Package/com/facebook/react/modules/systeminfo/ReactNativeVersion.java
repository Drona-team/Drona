package com.facebook.react.modules.systeminfo;

import com.facebook.react.common.MapBuilder;
import java.util.Map;

public class ReactNativeVersion
{
  public static final Map<String, Object> VERSION = MapBuilder.get("major", Integer.valueOf(0), "minor", Integer.valueOf(61), "patch", Integer.valueOf(4), "prerelease", null);
  
  public ReactNativeVersion() {}
}
