package com.bugsnag.android;

import android.content.Context;
import java.io.File;
import java.util.Comparator;
import java.util.Locale;
import java.util.UUID;

class SessionStore
  extends FileStore<Session>
{
  static final Comparator<File> SESSION_COMPARATOR = new Comparator()
  {
    public int compare(File paramAnonymousFile1, File paramAnonymousFile2)
    {
      if ((paramAnonymousFile1 == null) && (paramAnonymousFile2 == null)) {
        return 0;
      }
      if (paramAnonymousFile1 == null) {
        return 1;
      }
      if (paramAnonymousFile2 == null) {
        return -1;
      }
      return paramAnonymousFile1.getName().compareTo(paramAnonymousFile2.getName());
    }
  };
  
  SessionStore(Configuration paramConfiguration, Context paramContext, FileStore.Delegate paramDelegate)
  {
    super(paramConfiguration, paramContext, "/bugsnag-sessions/", 128, SESSION_COMPARATOR, paramDelegate);
  }
  
  String getFilename(Object paramObject)
  {
    return String.format(Locale.US, "%s%s%d.json", new Object[] { storeDirectory, UUID.randomUUID().toString(), Long.valueOf(System.currentTimeMillis()) });
  }
}
