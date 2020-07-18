package androidx.core.view.inputmethod;

import android.os.BaseBundle;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;

public final class EditorInfoCompat
{
  private static final String CONTENT_MIME_TYPES_INTEROP_KEY = "android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES";
  private static final String CONTENT_MIME_TYPES_KEY = "androidx.core.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES";
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  public static final int IME_FLAG_FORCE_ASCII = Integer.MIN_VALUE;
  public static final int IME_FLAG_NO_PERSONALIZED_LEARNING = 16777216;
  
  public EditorInfoCompat() {}
  
  public static String[] getContentMimeTypes(EditorInfo paramEditorInfo)
  {
    if (Build.VERSION.SDK_INT >= 25)
    {
      paramEditorInfo = contentMimeTypes;
      if (paramEditorInfo != null) {
        return paramEditorInfo;
      }
      return EMPTY_STRING_ARRAY;
    }
    if (extras == null) {
      return EMPTY_STRING_ARRAY;
    }
    String[] arrayOfString2 = extras.getStringArray("androidx.core.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES");
    String[] arrayOfString1 = arrayOfString2;
    if (arrayOfString2 == null) {
      arrayOfString1 = extras.getStringArray("android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES");
    }
    if (arrayOfString1 != null) {
      return arrayOfString1;
    }
    return EMPTY_STRING_ARRAY;
  }
  
  static int getProtocol(EditorInfo paramEditorInfo)
  {
    if (Build.VERSION.SDK_INT >= 25) {
      return 1;
    }
    if (extras == null) {
      return 0;
    }
    boolean bool1 = extras.containsKey("androidx.core.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES");
    boolean bool2 = extras.containsKey("android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES");
    if ((bool1) && (bool2)) {
      return 4;
    }
    if (bool1) {
      return 3;
    }
    if (bool2) {
      return 2;
    }
    return 0;
  }
  
  public static void setContentMimeTypes(EditorInfo paramEditorInfo, String[] paramArrayOfString)
  {
    if (Build.VERSION.SDK_INT >= 25)
    {
      contentMimeTypes = paramArrayOfString;
      return;
    }
    if (extras == null) {
      extras = new Bundle();
    }
    extras.putStringArray("androidx.core.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES", paramArrayOfString);
    extras.putStringArray("android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES", paramArrayOfString);
  }
}
