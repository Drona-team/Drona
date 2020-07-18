package com.bugsnag.android;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class StrictModeHandler
{
  private static final int DETECT_CUSTOM = 8;
  private static final int DETECT_DISK_READ = 2;
  private static final int DETECT_DISK_WRITE = 1;
  private static final int DETECT_NETWORK = 4;
  private static final int DETECT_RESOURCE_MISMATCH = 16;
  private static final int DETECT_VM_ACTIVITY_LEAKS = 1024;
  private static final int DETECT_VM_CLEARTEXT_NETWORK = 16384;
  private static final int DETECT_VM_CLOSABLE_LEAKS = 512;
  private static final int DETECT_VM_CURSOR_LEAKS = 256;
  private static final int DETECT_VM_FILE_URI_EXPOSURE = 8192;
  private static final int DETECT_VM_INSTANCE_LEAKS = 2048;
  private static final int DETECT_VM_REGISTRATION_LEAKS = 4096;
  @SuppressLint({"UseSparseArrays"})
  private static final Map<Integer, String> POLICY_CODE_MAP = new HashMap();
  private static final String STRICT_MODE_CLZ_NAME = "android.os.strictmode";
  
  static
  {
    POLICY_CODE_MAP.put(Integer.valueOf(1), "DiskWrite");
    POLICY_CODE_MAP.put(Integer.valueOf(2), "DiskRead");
    POLICY_CODE_MAP.put(Integer.valueOf(4), "NetworkOperation");
    POLICY_CODE_MAP.put(Integer.valueOf(8), "CustomSlowCall");
    POLICY_CODE_MAP.put(Integer.valueOf(16), "ResourceMismatch");
    POLICY_CODE_MAP.put(Integer.valueOf(256), "CursorLeak");
    POLICY_CODE_MAP.put(Integer.valueOf(512), "CloseableLeak");
    POLICY_CODE_MAP.put(Integer.valueOf(1024), "ActivityLeak");
    POLICY_CODE_MAP.put(Integer.valueOf(2048), "InstanceLeak");
    POLICY_CODE_MAP.put(Integer.valueOf(4096), "RegistrationLeak");
    POLICY_CODE_MAP.put(Integer.valueOf(8192), "FileUriLeak");
    POLICY_CODE_MAP.put(Integer.valueOf(16384), "CleartextNetwork");
  }
  
  StrictModeHandler() {}
  
  private Throwable getRootCause(Throwable paramThrowable)
  {
    Throwable localThrowable = paramThrowable.getCause();
    if (localThrowable == null) {
      return paramThrowable;
    }
    return getRootCause(localThrowable);
  }
  
  String getViolationDescription(String paramString)
  {
    if (!TextUtils.isEmpty(paramString))
    {
      int i = paramString.lastIndexOf("violation=");
      if (i != -1)
      {
        paramString = paramString.substring(i).replace("violation=", "");
        if (TextUtils.isDigitsOnly(paramString))
        {
          paramString = Integer.valueOf(paramString);
          return (String)POLICY_CODE_MAP.get(paramString);
        }
      }
      return null;
    }
    throw new IllegalArgumentException();
  }
  
  boolean isStrictModeThrowable(Throwable paramThrowable)
  {
    return getRootCause(paramThrowable).getClass().getName().toLowerCase(Locale.US).startsWith("android.os.strictmode");
  }
}
