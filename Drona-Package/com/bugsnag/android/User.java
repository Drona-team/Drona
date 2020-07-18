package com.bugsnag.android;

import androidx.annotation.Nullable;
import java.io.IOException;
import java.util.Observable;

class User
  extends Observable
  implements JsonStream.Streamable
{
  @Nullable
  private String email;
  @Nullable
  private String id;
  @Nullable
  private String name;
  
  User() {}
  
  User(User paramUser)
  {
    this(id, email, name);
  }
  
  User(String paramString1, String paramString2, String paramString3)
  {
    id = paramString1;
    email = paramString2;
    name = paramString3;
  }
  
  public String getEmail()
  {
    return email;
  }
  
  public String getId()
  {
    return id;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setEmail(String paramString)
  {
    email = paramString;
    setChanged();
    notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.UPDATE_USER_EMAIL, paramString));
  }
  
  public void setId(String paramString)
  {
    id = paramString;
    setChanged();
    notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.UPDATE_USER_ID, paramString));
  }
  
  public void setName(String paramString)
  {
    name = paramString;
    setChanged();
    notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.UPDATE_USER_NAME, paramString));
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    paramJsonStream.beginObject();
    paramJsonStream.name("id").value(id);
    paramJsonStream.name("email").value(email);
    paramJsonStream.name("name").value(name);
    paramJsonStream.endObject();
  }
}
