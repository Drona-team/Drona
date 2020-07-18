package androidx.core.view.inputmethod;

import android.content.ClipDescription;
import android.os.BaseBundle;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputContentInfo;

public final class InputConnectionCompat
{
  private static final String COMMIT_CONTENT_ACTION = "androidx.core.view.inputmethod.InputConnectionCompat.COMMIT_CONTENT";
  private static final String COMMIT_CONTENT_CONTENT_URI_INTEROP_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_URI";
  private static final String COMMIT_CONTENT_CONTENT_URI_KEY = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_URI";
  private static final String COMMIT_CONTENT_DESCRIPTION_INTEROP_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_DESCRIPTION";
  private static final String COMMIT_CONTENT_DESCRIPTION_KEY = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_DESCRIPTION";
  private static final String COMMIT_CONTENT_FLAGS_INTEROP_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_FLAGS";
  private static final String COMMIT_CONTENT_FLAGS_KEY = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_FLAGS";
  private static final String COMMIT_CONTENT_INTEROP_ACTION = "android.support.v13.view.inputmethod.InputConnectionCompat.COMMIT_CONTENT";
  private static final String COMMIT_CONTENT_LINK_URI_INTEROP_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_LINK_URI";
  private static final String COMMIT_CONTENT_LINK_URI_KEY = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_LINK_URI";
  private static final String COMMIT_CONTENT_OPTS_INTEROP_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_OPTS";
  private static final String COMMIT_CONTENT_OPTS_KEY = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_OPTS";
  private static final String COMMIT_CONTENT_RESULT_INTEROP_RECEIVER_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_RESULT_RECEIVER";
  private static final String COMMIT_CONTENT_RESULT_RECEIVER_KEY = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_RESULT_RECEIVER";
  public static final int INPUT_CONTENT_GRANT_READ_URI_PERMISSION = 1;
  
  public InputConnectionCompat() {}
  
  public static boolean commitContent(InputConnection paramInputConnection, EditorInfo paramEditorInfo, InputContentInfoCompat paramInputContentInfoCompat, int paramInt, Bundle paramBundle)
  {
    Object localObject = paramInputContentInfoCompat.getDescription();
    String[] arrayOfString = EditorInfoCompat.getContentMimeTypes(paramEditorInfo);
    int k = arrayOfString.length;
    int j = 0;
    int i = 0;
    while (i < k)
    {
      if (((ClipDescription)localObject).hasMimeType(arrayOfString[i]))
      {
        i = 1;
        break label61;
      }
      i += 1;
    }
    i = 0;
    label61:
    if (i == 0) {
      return false;
    }
    if (Build.VERSION.SDK_INT >= 25) {
      return paramInputConnection.commitContent((InputContentInfo)paramInputContentInfoCompat.unwrap(), paramInt, paramBundle);
    }
    i = j;
    switch (EditorInfoCompat.getProtocol(paramEditorInfo))
    {
    default: 
      return false;
    case 2: 
      i = 1;
    }
    localObject = new Bundle();
    if (i != 0) {
      paramEditorInfo = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_URI";
    } else {
      paramEditorInfo = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_URI";
    }
    ((Bundle)localObject).putParcelable(paramEditorInfo, paramInputContentInfoCompat.getContentUri());
    if (i != 0) {
      paramEditorInfo = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_DESCRIPTION";
    } else {
      paramEditorInfo = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_DESCRIPTION";
    }
    ((Bundle)localObject).putParcelable(paramEditorInfo, paramInputContentInfoCompat.getDescription());
    if (i != 0) {
      paramEditorInfo = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_LINK_URI";
    } else {
      paramEditorInfo = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_LINK_URI";
    }
    ((Bundle)localObject).putParcelable(paramEditorInfo, paramInputContentInfoCompat.getLinkUri());
    if (i != 0) {
      paramEditorInfo = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_FLAGS";
    } else {
      paramEditorInfo = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_FLAGS";
    }
    ((BaseBundle)localObject).putInt(paramEditorInfo, paramInt);
    if (i != 0) {
      paramEditorInfo = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_OPTS";
    } else {
      paramEditorInfo = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_OPTS";
    }
    ((Bundle)localObject).putParcelable(paramEditorInfo, paramBundle);
    if (i != 0) {
      paramEditorInfo = "android.support.v13.view.inputmethod.InputConnectionCompat.COMMIT_CONTENT";
    } else {
      paramEditorInfo = "androidx.core.view.inputmethod.InputConnectionCompat.COMMIT_CONTENT";
    }
    return paramInputConnection.performPrivateCommand(paramEditorInfo, (Bundle)localObject);
  }
  
  public static InputConnection createWrapper(InputConnection paramInputConnection, EditorInfo paramEditorInfo, final OnCommitContentListener paramOnCommitContentListener)
  {
    if (paramInputConnection != null)
    {
      if (paramEditorInfo != null)
      {
        if (paramOnCommitContentListener != null)
        {
          if (Build.VERSION.SDK_INT >= 25) {
            new InputConnectionWrapper(paramInputConnection, false)
            {
              public boolean commitContent(InputContentInfo paramAnonymousInputContentInfo, int paramAnonymousInt, Bundle paramAnonymousBundle)
              {
                if (paramOnCommitContentListener.onCommitContent(InputContentInfoCompat.wrap(paramAnonymousInputContentInfo), paramAnonymousInt, paramAnonymousBundle)) {
                  return true;
                }
                return super.commitContent(paramAnonymousInputContentInfo, paramAnonymousInt, paramAnonymousBundle);
              }
            };
          }
          if (EditorInfoCompat.getContentMimeTypes(paramEditorInfo).length == 0) {
            return paramInputConnection;
          }
          new InputConnectionWrapper(paramInputConnection, false)
          {
            public boolean performPrivateCommand(String paramAnonymousString, Bundle paramAnonymousBundle)
            {
              if (InputConnectionCompat.handlePerformPrivateCommand(paramAnonymousString, paramAnonymousBundle, paramOnCommitContentListener)) {
                return true;
              }
              return super.performPrivateCommand(paramAnonymousString, paramAnonymousBundle);
            }
          };
        }
        throw new IllegalArgumentException("onCommitContentListener must be non-null");
      }
      throw new IllegalArgumentException("editorInfo must be non-null");
    }
    throw new IllegalArgumentException("inputConnection must be non-null");
  }
  
  /* Error */
  static boolean handlePerformPrivateCommand(String paramString, Bundle paramBundle, OnCommitContentListener paramOnCommitContentListener)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 4
    //   3: aload_1
    //   4: ifnonnull +5 -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: ldc 15
    //   11: aload_0
    //   12: invokestatic 153	android/text/TextUtils:equals	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z
    //   15: ifeq +8 -> 23
    //   18: iconst_0
    //   19: istore_3
    //   20: goto +14 -> 34
    //   23: ldc 36
    //   25: aload_0
    //   26: invokestatic 153	android/text/TextUtils:equals	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z
    //   29: ifeq +229 -> 258
    //   32: iconst_1
    //   33: istore_3
    //   34: iload_3
    //   35: ifeq +15 -> 50
    //   38: ldc 51
    //   40: astore_0
    //   41: goto +12 -> 53
    //   44: astore_0
    //   45: aconst_null
    //   46: astore_1
    //   47: goto +199 -> 246
    //   50: ldc 54
    //   52: astore_0
    //   53: aload_1
    //   54: aload_0
    //   55: invokevirtual 157	android/os/Bundle:getParcelable	(Ljava/lang/String;)Landroid/os/Parcelable;
    //   58: checkcast 159	android/os/ResultReceiver
    //   61: astore 7
    //   63: iload_3
    //   64: ifeq +16 -> 80
    //   67: ldc 18
    //   69: astore_0
    //   70: goto +13 -> 83
    //   73: astore_0
    //   74: aload 7
    //   76: astore_1
    //   77: goto +169 -> 246
    //   80: ldc 21
    //   82: astore_0
    //   83: aload_1
    //   84: aload_0
    //   85: invokevirtual 157	android/os/Bundle:getParcelable	(Ljava/lang/String;)Landroid/os/Parcelable;
    //   88: checkcast 161	android/net/Uri
    //   91: astore 8
    //   93: iload_3
    //   94: ifeq +9 -> 103
    //   97: ldc 24
    //   99: astore_0
    //   100: goto +6 -> 106
    //   103: ldc 27
    //   105: astore_0
    //   106: aload_1
    //   107: aload_0
    //   108: invokevirtual 157	android/os/Bundle:getParcelable	(Ljava/lang/String;)Landroid/os/Parcelable;
    //   111: checkcast 78	android/content/ClipDescription
    //   114: astore 9
    //   116: iload_3
    //   117: ifeq +9 -> 126
    //   120: ldc 39
    //   122: astore_0
    //   123: goto +6 -> 129
    //   126: ldc 42
    //   128: astore_0
    //   129: aload_1
    //   130: aload_0
    //   131: invokevirtual 157	android/os/Bundle:getParcelable	(Ljava/lang/String;)Landroid/os/Parcelable;
    //   134: checkcast 161	android/net/Uri
    //   137: astore 10
    //   139: iload_3
    //   140: ifeq +9 -> 149
    //   143: ldc 30
    //   145: astore_0
    //   146: goto +6 -> 152
    //   149: ldc 33
    //   151: astore_0
    //   152: aload_1
    //   153: aload_0
    //   154: invokevirtual 165	android/os/BaseBundle:getInt	(Ljava/lang/String;)I
    //   157: istore 5
    //   159: iload_3
    //   160: ifeq +9 -> 169
    //   163: ldc 45
    //   165: astore_0
    //   166: goto +6 -> 172
    //   169: ldc 48
    //   171: astore_0
    //   172: aload_1
    //   173: aload_0
    //   174: invokevirtual 157	android/os/Bundle:getParcelable	(Ljava/lang/String;)Landroid/os/Parcelable;
    //   177: checkcast 104	android/os/Bundle
    //   180: astore_0
    //   181: aload 8
    //   183: ifnull +35 -> 218
    //   186: aload 9
    //   188: ifnull +30 -> 218
    //   191: aload_2
    //   192: new 66	androidx/core/view/inputmethod/InputContentInfoCompat
    //   195: dup
    //   196: aload 8
    //   198: aload 9
    //   200: aload 10
    //   202: invokespecial 168	androidx/core/view/inputmethod/InputContentInfoCompat:<init>	(Landroid/net/Uri;Landroid/content/ClipDescription;Landroid/net/Uri;)V
    //   205: iload 5
    //   207: aload_0
    //   208: invokeinterface 172 4 0
    //   213: istore 6
    //   215: goto +6 -> 221
    //   218: iconst_0
    //   219: istore 6
    //   221: aload 7
    //   223: ifnull +37 -> 260
    //   226: iload 4
    //   228: istore_3
    //   229: iload 6
    //   231: ifeq +5 -> 236
    //   234: iconst_1
    //   235: istore_3
    //   236: aload 7
    //   238: iload_3
    //   239: aconst_null
    //   240: invokevirtual 176	android/os/ResultReceiver:send	(ILandroid/os/Bundle;)V
    //   243: iload 6
    //   245: ireturn
    //   246: aload_1
    //   247: ifnull +9 -> 256
    //   250: aload_1
    //   251: iconst_0
    //   252: aconst_null
    //   253: invokevirtual 176	android/os/ResultReceiver:send	(ILandroid/os/Bundle;)V
    //   256: aload_0
    //   257: athrow
    //   258: iconst_0
    //   259: ireturn
    //   260: iload 6
    //   262: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	263	0	paramString	String
    //   0	263	1	paramBundle	Bundle
    //   0	263	2	paramOnCommitContentListener	OnCommitContentListener
    //   19	220	3	i	int
    //   1	226	4	j	int
    //   157	49	5	k	int
    //   213	48	6	bool	boolean
    //   61	176	7	localResultReceiver	android.os.ResultReceiver
    //   91	106	8	localUri1	android.net.Uri
    //   114	85	9	localClipDescription	ClipDescription
    //   137	64	10	localUri2	android.net.Uri
    // Exception table:
    //   from	to	target	type
    //   53	63	44	java/lang/Throwable
    //   83	93	73	java/lang/Throwable
    //   106	116	73	java/lang/Throwable
    //   129	139	73	java/lang/Throwable
    //   152	159	73	java/lang/Throwable
    //   172	181	73	java/lang/Throwable
    //   191	215	73	java/lang/Throwable
  }
  
  public static abstract interface OnCommitContentListener
  {
    public abstract boolean onCommitContent(InputContentInfoCompat paramInputContentInfoCompat, int paramInt, Bundle paramBundle);
  }
}
