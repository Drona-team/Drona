package androidx.core.package_4;

import android.graphics.drawable.Icon;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.IconCompat;

public class Person
{
  private static final String ICON_KEY = "icon";
  private static final String IS_BOT_KEY = "isBot";
  private static final String IS_IMPORTANT_KEY = "isImportant";
  private static final String KEY_KEY = "key";
  private static final String NAME_KEY = "name";
  private static final String URI_KEY = "uri";
  @Nullable
  IconCompat mIcon;
  boolean mIsBot;
  boolean mIsImportant;
  @Nullable
  String mKey;
  @Nullable
  CharSequence mName;
  @Nullable
  String mUri;
  
  Person(Builder paramBuilder)
  {
    mName = mName;
    mIcon = mIcon;
    mUri = mUri;
    mKey = mKey;
    mIsBot = mIsBot;
    mIsImportant = mIsImportant;
  }
  
  public static Person fromAndroidPerson(android.app.Person paramPerson)
  {
    Builder localBuilder = new Builder().setName(paramPerson.getName());
    IconCompat localIconCompat;
    if (paramPerson.getIcon() != null) {
      localIconCompat = IconCompat.createFromIcon(paramPerson.getIcon());
    } else {
      localIconCompat = null;
    }
    return localBuilder.setIcon(localIconCompat).setUri(paramPerson.getUri()).setKey(paramPerson.getKey()).setBot(paramPerson.isBot()).setImportant(paramPerson.isImportant()).build();
  }
  
  public static Person fromBundle(Bundle paramBundle)
  {
    Object localObject = paramBundle.getBundle("icon");
    Builder localBuilder = new Builder().setName(paramBundle.getCharSequence("name"));
    if (localObject != null) {
      localObject = IconCompat.createFromBundle((Bundle)localObject);
    } else {
      localObject = null;
    }
    return localBuilder.setIcon((IconCompat)localObject).setUri(paramBundle.getString("uri")).setKey(paramBundle.getString("key")).setBot(paramBundle.getBoolean("isBot")).setImportant(paramBundle.getBoolean("isImportant")).build();
  }
  
  public static Person fromPersistableBundle(PersistableBundle paramPersistableBundle)
  {
    return new Builder().setName(paramPersistableBundle.getString("name")).setUri(paramPersistableBundle.getString("uri")).setKey(paramPersistableBundle.getString("key")).setBot(paramPersistableBundle.getBoolean("isBot")).setImportant(paramPersistableBundle.getBoolean("isImportant")).build();
  }
  
  public IconCompat getIcon()
  {
    return mIcon;
  }
  
  public String getKey()
  {
    return mKey;
  }
  
  public CharSequence getName()
  {
    return mName;
  }
  
  public String getUri()
  {
    return mUri;
  }
  
  public boolean isBot()
  {
    return mIsBot;
  }
  
  public boolean isImportant()
  {
    return mIsImportant;
  }
  
  public android.app.Person toAndroidPerson()
  {
    android.app.Person.Builder localBuilder = new android.app.Person.Builder().setName(getName());
    Icon localIcon;
    if (getIcon() != null) {
      localIcon = getIcon().toIcon();
    } else {
      localIcon = null;
    }
    return localBuilder.setIcon(localIcon).setUri(getUri()).setKey(getKey()).setBot(isBot()).setImportant(isImportant()).build();
  }
  
  public Builder toBuilder()
  {
    return new Builder();
  }
  
  public Bundle toBundle()
  {
    Bundle localBundle2 = new Bundle();
    localBundle2.putCharSequence("name", mName);
    Bundle localBundle1;
    if (mIcon != null) {
      localBundle1 = mIcon.toBundle();
    } else {
      localBundle1 = null;
    }
    localBundle2.putBundle("icon", localBundle1);
    localBundle2.putString("uri", mUri);
    localBundle2.putString("key", mKey);
    localBundle2.putBoolean("isBot", mIsBot);
    localBundle2.putBoolean("isImportant", mIsImportant);
    return localBundle2;
  }
  
  public PersistableBundle toPersistableBundle()
  {
    PersistableBundle localPersistableBundle = new PersistableBundle();
    String str;
    if (mName != null) {
      str = mName.toString();
    } else {
      str = null;
    }
    localPersistableBundle.putString("name", str);
    localPersistableBundle.putString("uri", mUri);
    localPersistableBundle.putString("key", mKey);
    localPersistableBundle.putBoolean("isBot", mIsBot);
    localPersistableBundle.putBoolean("isImportant", mIsImportant);
    return localPersistableBundle;
  }
  
  public class Builder
  {
    @Nullable
    IconCompat mIcon;
    boolean mIsBot;
    boolean mIsImportant;
    @Nullable
    String mKey;
    @Nullable
    CharSequence mName;
    @Nullable
    String mUri;
    
    public Builder() {}
    
    Builder()
    {
      mName = mName;
      mIcon = mIcon;
      mUri = mUri;
      mKey = mKey;
      mIsBot = mIsBot;
      mIsImportant = mIsImportant;
    }
    
    public Person build()
    {
      return new Person(this);
    }
    
    public Builder setBot(boolean paramBoolean)
    {
      mIsBot = paramBoolean;
      return this;
    }
    
    public Builder setIcon(IconCompat paramIconCompat)
    {
      mIcon = paramIconCompat;
      return this;
    }
    
    public Builder setImportant(boolean paramBoolean)
    {
      mIsImportant = paramBoolean;
      return this;
    }
    
    public Builder setKey(String paramString)
    {
      mKey = paramString;
      return this;
    }
    
    public Builder setName(CharSequence paramCharSequence)
    {
      mName = paramCharSequence;
      return this;
    }
    
    public Builder setUri(String paramString)
    {
      mUri = paramString;
      return this;
    }
  }
}
