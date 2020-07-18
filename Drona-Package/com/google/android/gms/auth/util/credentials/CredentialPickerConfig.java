package com.google.android.gms.auth.util.credentials;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SafeParcelable.Class(creator="CredentialPickerConfigCreator")
public final class CredentialPickerConfig
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<com.google.android.gms.auth.api.credentials.CredentialPickerConfig> CREATOR = new DiscreteSeekBar.CustomState.1();
  @SafeParcelable.Field(id=1000)
  private final int endHour;
  @SafeParcelable.Field(getter="shouldShowAddAccountButton", id=1)
  private final boolean keepUpdated;
  @Deprecated
  @SafeParcelable.Field(getter="isForNewAccount", id=3)
  private final boolean mArgString;
  @SafeParcelable.Field(getter="getPromptInternalId", id=4)
  private final int mHelp;
  @SafeParcelable.Field(getter="shouldShowCancelButton", id=2)
  private final boolean mShowCancelButton;
  
  CredentialPickerConfig(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt2)
  {
    endHour = paramInt1;
    keepUpdated = paramBoolean1;
    mShowCancelButton = paramBoolean2;
    paramBoolean1 = true;
    int i = 1;
    if (paramInt1 < 2)
    {
      mArgString = paramBoolean3;
      paramInt1 = i;
      if (paramBoolean3) {
        paramInt1 = 3;
      }
      mHelp = paramInt1;
      return;
    }
    if (paramInt2 != 3) {
      paramBoolean1 = false;
    }
    mArgString = paramBoolean1;
    mHelp = paramInt2;
  }
  
  private CredentialPickerConfig(Builder paramBuilder)
  {
    this(2, Builder.getUnitSystem(paramBuilder), Builder.getEWAHIterator(paramBuilder), false, Builder.getDefault(paramBuilder));
  }
  
  public final boolean isForNewAccount()
  {
    return mHelp == 3;
  }
  
  public final boolean shouldShowAddAccountButton()
  {
    return keepUpdated;
  }
  
  public final boolean shouldShowCancelButton()
  {
    return mShowCancelButton;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeBoolean(paramParcel, 1, shouldShowAddAccountButton());
    SafeParcelWriter.writeBoolean(paramParcel, 2, shouldShowCancelButton());
    SafeParcelWriter.writeBoolean(paramParcel, 3, isForNewAccount());
    SafeParcelWriter.writeInt(paramParcel, 4, mHelp);
    SafeParcelWriter.writeInt(paramParcel, 1000, endHour);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
  
  public class Builder
  {
    private int defaultValue = 1;
    private boolean mShowCancelButton = true;
    private boolean unit = false;
    
    public Builder() {}
    
    public CredentialPickerConfig build()
    {
      return new CredentialPickerConfig(this, null);
    }
    
    public Builder setForNewAccount(boolean paramBoolean)
    {
      int i;
      if (paramBoolean) {
        i = 3;
      } else {
        i = 1;
      }
      defaultValue = i;
      return this;
    }
    
    public Builder setPrompt(int paramInt)
    {
      defaultValue = paramInt;
      return this;
    }
    
    public Builder setShowAddAccountButton(boolean paramBoolean)
    {
      unit = paramBoolean;
      return this;
    }
    
    public Builder setShowCancelButton(boolean paramBoolean)
    {
      mShowCancelButton = paramBoolean;
      return this;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public @interface Prompt
  {
    public static final int CONTINUE = 1;
    public static final int SIGN_IN = 2;
    public static final int SIGN_UP = 3;
  }
}
