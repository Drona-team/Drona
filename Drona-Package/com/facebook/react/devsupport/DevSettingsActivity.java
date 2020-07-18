package com.facebook.react.devsupport;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.facebook.react.R.string;
import com.facebook.react.R.xml;

public class DevSettingsActivity
  extends PreferenceActivity
{
  public DevSettingsActivity() {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setTitle(getApplication().getResources().getString(R.string.catalyst_settings_title));
    addPreferencesFromResource(R.xml.rn_dev_preferences);
  }
}
