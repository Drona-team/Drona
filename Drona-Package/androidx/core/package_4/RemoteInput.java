package androidx.core.package_4;

import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.Intent;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Build.VERSION;
import android.os.Bundle;
import androidx.annotation.RestrictTo;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class RemoteInput
{
  private static final String EXTRA_DATA_TYPE_RESULTS_DATA = "android.remoteinput.dataTypeResultsData";
  public static final String EXTRA_RESULTS_DATA = "android.remoteinput.resultsData";
  private static final String EXTRA_RESULTS_SOURCE = "android.remoteinput.resultsSource";
  public static final String RESULTS_CLIP_LABEL = "android.remoteinput.results";
  public static final int SOURCE_CHOICE = 1;
  public static final int SOURCE_FREE_FORM_INPUT = 0;
  private static final String TAG = "RemoteInput";
  private final boolean mAllowFreeFormTextInput;
  private final Set<String> mAllowedDataTypes;
  private final CharSequence[] mChoices;
  private final Bundle mExtras;
  private final CharSequence mLabel;
  private final String mResultKey;
  
  RemoteInput(String paramString, CharSequence paramCharSequence, CharSequence[] paramArrayOfCharSequence, boolean paramBoolean, Bundle paramBundle, Set paramSet)
  {
    mResultKey = paramString;
    mLabel = paramCharSequence;
    mChoices = paramArrayOfCharSequence;
    mAllowFreeFormTextInput = paramBoolean;
    mExtras = paramBundle;
    mAllowedDataTypes = paramSet;
  }
  
  public static void addDataResultToIntent(RemoteInput paramRemoteInput, Intent paramIntent, Map paramMap)
  {
    if (Build.VERSION.SDK_INT >= 26)
    {
      android.app.RemoteInput.addDataResultToIntent(fromCompat(paramRemoteInput), paramIntent, paramMap);
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      Object localObject2 = getClipDataIntentFromIntent(paramIntent);
      Object localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = new Intent();
      }
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        paramMap = (Map.Entry)localIterator.next();
        String str = (String)paramMap.getKey();
        Uri localUri = (Uri)paramMap.getValue();
        if (str != null)
        {
          localObject2 = ((Intent)localObject1).getBundleExtra(getExtraResultsKeyForData(str));
          paramMap = (Map)localObject2;
          if (localObject2 == null) {
            paramMap = new Bundle();
          }
          paramMap.putString(paramRemoteInput.getResultKey(), localUri.toString());
          ((Intent)localObject1).putExtra(getExtraResultsKeyForData(str), paramMap);
        }
      }
      paramIntent.setClipData(ClipData.newIntent("android.remoteinput.results", (Intent)localObject1));
    }
  }
  
  public static void addResultsToIntent(RemoteInput[] paramArrayOfRemoteInput, Intent paramIntent, Bundle paramBundle)
  {
    if (Build.VERSION.SDK_INT >= 26)
    {
      android.app.RemoteInput.addResultsToIntent(fromCompat(paramArrayOfRemoteInput), paramIntent, paramBundle);
      return;
    }
    int j = Build.VERSION.SDK_INT;
    int i = 0;
    Object localObject1;
    Object localObject2;
    if (j >= 20)
    {
      localObject1 = getResultsFromIntent(paramIntent);
      j = getResultsSource(paramIntent);
      if (localObject1 != null)
      {
        ((Bundle)localObject1).putAll(paramBundle);
        paramBundle = (Bundle)localObject1;
      }
      int k = paramArrayOfRemoteInput.length;
      i = 0;
      while (i < k)
      {
        localObject1 = paramArrayOfRemoteInput[i];
        localObject2 = getDataResultsFromIntent(paramIntent, ((RemoteInput)localObject1).getResultKey());
        android.app.RemoteInput.addResultsToIntent(fromCompat(new RemoteInput[] { localObject1 }), paramIntent, paramBundle);
        if (localObject2 != null) {
          addDataResultToIntent((RemoteInput)localObject1, paramIntent, (Map)localObject2);
        }
        i += 1;
      }
      setResultsSource(paramIntent, j);
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      localObject2 = getClipDataIntentFromIntent(paramIntent);
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = new Intent();
      }
      Object localObject3 = ((Intent)localObject1).getBundleExtra("android.remoteinput.resultsData");
      localObject2 = localObject3;
      if (localObject3 == null) {
        localObject2 = new Bundle();
      }
      j = paramArrayOfRemoteInput.length;
      while (i < j)
      {
        localObject3 = paramArrayOfRemoteInput[i];
        Object localObject4 = paramBundle.get(((RemoteInput)localObject3).getResultKey());
        if ((localObject4 instanceof CharSequence)) {
          ((Bundle)localObject2).putCharSequence(((RemoteInput)localObject3).getResultKey(), (CharSequence)localObject4);
        }
        i += 1;
      }
      ((Intent)localObject1).putExtra("android.remoteinput.resultsData", (Bundle)localObject2);
      paramIntent.setClipData(ClipData.newIntent("android.remoteinput.results", (Intent)localObject1));
    }
  }
  
  static android.app.RemoteInput fromCompat(RemoteInput paramRemoteInput)
  {
    return new android.app.RemoteInput.Builder(paramRemoteInput.getResultKey()).setLabel(paramRemoteInput.getLabel()).setChoices(paramRemoteInput.getChoices()).setAllowFreeFormInput(paramRemoteInput.getAllowFreeFormInput()).addExtras(paramRemoteInput.getExtras()).build();
  }
  
  static android.app.RemoteInput[] fromCompat(RemoteInput[] paramArrayOfRemoteInput)
  {
    if (paramArrayOfRemoteInput == null) {
      return null;
    }
    android.app.RemoteInput[] arrayOfRemoteInput = new android.app.RemoteInput[paramArrayOfRemoteInput.length];
    int i = 0;
    while (i < paramArrayOfRemoteInput.length)
    {
      arrayOfRemoteInput[i] = fromCompat(paramArrayOfRemoteInput[i]);
      i += 1;
    }
    return arrayOfRemoteInput;
  }
  
  private static Intent getClipDataIntentFromIntent(Intent paramIntent)
  {
    paramIntent = paramIntent.getClipData();
    if (paramIntent == null) {
      return null;
    }
    ClipDescription localClipDescription = paramIntent.getDescription();
    if (!localClipDescription.hasMimeType("text/vnd.android.intent")) {
      return null;
    }
    if (!localClipDescription.getLabel().equals("android.remoteinput.results")) {
      return null;
    }
    return paramIntent.getItemAt(0).getIntent();
  }
  
  public static Map getDataResultsFromIntent(Intent paramIntent, String paramString)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      return android.app.RemoteInput.getDataResultsFromIntent(paramIntent, paramString);
    }
    HashMap localHashMap;
    if (Build.VERSION.SDK_INT >= 16)
    {
      paramIntent = getClipDataIntentFromIntent(paramIntent);
      if (paramIntent == null) {
        return null;
      }
      localHashMap = new HashMap();
      Iterator localIterator = paramIntent.getExtras().keySet().iterator();
      while (localIterator.hasNext())
      {
        String str2 = (String)localIterator.next();
        if (str2.startsWith("android.remoteinput.dataTypeResultsData"))
        {
          String str1 = str2.substring("android.remoteinput.dataTypeResultsData".length());
          if (!str1.isEmpty())
          {
            str2 = paramIntent.getBundleExtra(str2).getString(paramString);
            if ((str2 != null) && (!str2.isEmpty())) {
              localHashMap.put(str1, Uri.parse(str2));
            }
          }
        }
      }
      if (!localHashMap.isEmpty()) {}
    }
    else
    {
      return null;
    }
    return localHashMap;
  }
  
  private static String getExtraResultsKeyForData(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("android.remoteinput.dataTypeResultsData");
    localStringBuilder.append(paramString);
    return localStringBuilder.toString();
  }
  
  public static Bundle getResultsFromIntent(Intent paramIntent)
  {
    if (Build.VERSION.SDK_INT >= 20) {
      return android.app.RemoteInput.getResultsFromIntent(paramIntent);
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      paramIntent = getClipDataIntentFromIntent(paramIntent);
      if (paramIntent == null) {
        return null;
      }
      return (Bundle)paramIntent.getExtras().getParcelable("android.remoteinput.resultsData");
    }
    return null;
  }
  
  public static int getResultsSource(Intent paramIntent)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return android.app.RemoteInput.getResultsSource(paramIntent);
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      paramIntent = getClipDataIntentFromIntent(paramIntent);
      if (paramIntent == null) {
        return 0;
      }
      return paramIntent.getExtras().getInt("android.remoteinput.resultsSource", 0);
    }
    return 0;
  }
  
  public static void setResultsSource(Intent paramIntent, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 28)
    {
      android.app.RemoteInput.setResultsSource(paramIntent, paramInt);
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      Intent localIntent2 = getClipDataIntentFromIntent(paramIntent);
      Intent localIntent1 = localIntent2;
      if (localIntent2 == null) {
        localIntent1 = new Intent();
      }
      localIntent1.putExtra("android.remoteinput.resultsSource", paramInt);
      paramIntent.setClipData(ClipData.newIntent("android.remoteinput.results", localIntent1));
    }
  }
  
  public boolean getAllowFreeFormInput()
  {
    return mAllowFreeFormTextInput;
  }
  
  public Set getAllowedDataTypes()
  {
    return mAllowedDataTypes;
  }
  
  public CharSequence[] getChoices()
  {
    return mChoices;
  }
  
  public Bundle getExtras()
  {
    return mExtras;
  }
  
  public CharSequence getLabel()
  {
    return mLabel;
  }
  
  public String getResultKey()
  {
    return mResultKey;
  }
  
  public boolean isDataOnly()
  {
    return (!getAllowFreeFormInput()) && ((getChoices() == null) || (getChoices().length == 0)) && (getAllowedDataTypes() != null) && (!getAllowedDataTypes().isEmpty());
  }
  
  public final class Builder
  {
    private boolean mAllowFreeFormTextInput = true;
    private final Set<String> mAllowedDataTypes = new HashSet();
    private CharSequence[] mChoices;
    private final Bundle mExtras = new Bundle();
    private CharSequence mLabel;
    
    public Builder()
    {
      if (RemoteInput.this != null) {
        return;
      }
      throw new IllegalArgumentException("Result key can't be null");
    }
    
    public Builder addExtras(Bundle paramBundle)
    {
      if (paramBundle != null) {
        mExtras.putAll(paramBundle);
      }
      return this;
    }
    
    public RemoteInput build()
    {
      return new RemoteInput(RemoteInput.this, mLabel, mChoices, mAllowFreeFormTextInput, mExtras, mAllowedDataTypes);
    }
    
    public Bundle getExtras()
    {
      return mExtras;
    }
    
    public Builder setAllowDataType(String paramString, boolean paramBoolean)
    {
      if (paramBoolean)
      {
        mAllowedDataTypes.add(paramString);
        return this;
      }
      mAllowedDataTypes.remove(paramString);
      return this;
    }
    
    public Builder setAllowFreeFormInput(boolean paramBoolean)
    {
      mAllowFreeFormTextInput = paramBoolean;
      return this;
    }
    
    public Builder setChoices(CharSequence[] paramArrayOfCharSequence)
    {
      mChoices = paramArrayOfCharSequence;
      return this;
    }
    
    public Builder setLabel(CharSequence paramCharSequence)
    {
      mLabel = paramCharSequence;
      return this;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  public @interface Source {}
}
