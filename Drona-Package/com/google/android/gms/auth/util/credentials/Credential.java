package com.google.android.gms.auth.util.credentials;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.google.android.gms.auth.api.credentials.IdToken;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Reserved;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

@SafeParcelable.Class(creator="CredentialCreator")
@SafeParcelable.Reserved({1000})
public class Credential
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<com.google.android.gms.auth.api.credentials.Credential> CREATOR = new VerticalProgressBar.SavedState.1();
  public static final String EXTRA_KEY = "com.google.android.gms.credentials.Credential";
  @Nullable
  @SafeParcelable.Field(getter="getFamilyName", id=10)
  private final String accessToken;
  @Nonnull
  @SafeParcelable.Field(getter="getId", id=1)
  private final String mId;
  @Nullable
  @SafeParcelable.Field(getter="getName", id=2)
  private final String mName;
  @Nullable
  @SafeParcelable.Field(getter="getProfilePictureUri", id=3)
  private final Uri mUrl;
  @Nullable
  @SafeParcelable.Field(getter="getPassword", id=5)
  private final String password;
  @Nullable
  @SafeParcelable.Field(getter="getGivenName", id=9)
  private final String refreshToken;
  @Nonnull
  @SafeParcelable.Field(getter="getIdTokens", id=4)
  private final List<IdToken> relations;
  @Nullable
  @SafeParcelable.Field(getter="getAccountType", id=6)
  private final String username;
  
  Credential(String paramString1, String paramString2, Uri paramUri, List paramList, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    String str = ((String)Preconditions.checkNotNull(paramString1, "credential identifier cannot be null")).trim();
    Preconditions.checkNotEmpty(str, "credential identifier cannot be empty");
    if ((paramString3 != null) && (TextUtils.isEmpty(paramString3))) {
      throw new IllegalArgumentException("Password must not be empty if set");
    }
    if (paramString4 != null)
    {
      boolean bool3 = TextUtils.isEmpty(paramString4);
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (!bool3)
      {
        paramString1 = Uri.parse(paramString4);
        bool1 = bool2;
        if (paramString1.isAbsolute())
        {
          bool1 = bool2;
          if (paramString1.isHierarchical())
          {
            bool1 = bool2;
            if (!TextUtils.isEmpty(paramString1.getScheme())) {
              if (TextUtils.isEmpty(paramString1.getAuthority()))
              {
                bool1 = bool2;
              }
              else if (!"http".equalsIgnoreCase(paramString1.getScheme()))
              {
                bool1 = bool2;
                if (!"https".equalsIgnoreCase(paramString1.getScheme())) {}
              }
              else
              {
                bool1 = true;
              }
            }
          }
        }
      }
      if (!Boolean.valueOf(bool1).booleanValue()) {
        throw new IllegalArgumentException("Account type must be a valid Http/Https URI");
      }
    }
    if ((!TextUtils.isEmpty(paramString4)) && (!TextUtils.isEmpty(paramString3))) {
      throw new IllegalArgumentException("Password and AccountType are mutually exclusive");
    }
    paramString1 = paramString2;
    if (paramString2 != null)
    {
      paramString1 = paramString2;
      if (TextUtils.isEmpty(paramString2.trim())) {
        paramString1 = null;
      }
    }
    mName = paramString1;
    mUrl = paramUri;
    if (paramList == null) {
      paramString1 = Collections.emptyList();
    } else {
      paramString1 = Collections.unmodifiableList(paramList);
    }
    relations = paramString1;
    mId = str;
    password = paramString3;
    username = paramString4;
    refreshToken = paramString5;
    accessToken = paramString6;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Credential)) {
      return false;
    }
    paramObject = (Credential)paramObject;
    return (TextUtils.equals(mId, mId)) && (TextUtils.equals(mName, mName)) && (Objects.equal(mUrl, mUrl)) && (TextUtils.equals(password, password)) && (TextUtils.equals(username, username));
  }
  
  public String getAccountType()
  {
    return username;
  }
  
  public String getFamilyName()
  {
    return accessToken;
  }
  
  public String getGivenName()
  {
    return refreshToken;
  }
  
  public String getId()
  {
    return mId;
  }
  
  public List getIdTokens()
  {
    return relations;
  }
  
  public String getName()
  {
    return mName;
  }
  
  public String getPassword()
  {
    return password;
  }
  
  public Uri getProfilePictureUri()
  {
    return mUrl;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { mId, mName, mUrl, password, username });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 1, getId(), false);
    SafeParcelWriter.writeString(paramParcel, 2, getName(), false);
    SafeParcelWriter.writeParcelable(paramParcel, 3, getProfilePictureUri(), paramInt, false);
    SafeParcelWriter.writeTypedList(paramParcel, 4, getIdTokens(), false);
    SafeParcelWriter.writeString(paramParcel, 5, getPassword(), false);
    SafeParcelWriter.writeString(paramParcel, 6, getAccountType(), false);
    SafeParcelWriter.writeString(paramParcel, 9, getGivenName(), false);
    SafeParcelWriter.writeString(paramParcel, 10, getFamilyName(), false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public class Builder
  {
    private Uri mAddress;
    private String mName;
    private String mPassword;
    private String mPermissions;
    private List<IdToken> mPort;
    private String params;
    private String restUrl;
    
    public Builder()
    {
      mName = Credential.getName(Credential.this);
      mAddress = Credential.getUrl(Credential.this);
      mPort = Credential.getTransport(Credential.this);
      mPassword = Credential.getPassword(Credential.this);
      mPermissions = Credential.getUsername(Credential.this);
      restUrl = Credential.getRefreshToken(Credential.this);
      params = Credential.getAccessToken(Credential.this);
    }
    
    public Builder() {}
    
    public Credential build()
    {
      return new Credential(Credential.this, mName, mAddress, mPort, mPassword, mPermissions, restUrl, params);
    }
    
    public Builder setAccountType(String paramString)
    {
      mPermissions = paramString;
      return this;
    }
    
    public Builder setName(String paramString)
    {
      mName = paramString;
      return this;
    }
    
    public Builder setPassword(String paramString)
    {
      mPassword = paramString;
      return this;
    }
    
    public Builder setProfilePictureUri(Uri paramUri)
    {
      mAddress = paramUri;
      return this;
    }
  }
}
