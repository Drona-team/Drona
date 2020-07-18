package com.bugsnag.android;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class Session
  implements JsonStream.Streamable
{
  private final AtomicBoolean autoCaptured;
  private AtomicInteger handledCount = new AtomicInteger();
  final AtomicBoolean isStopped = new AtomicBoolean(false);
  private final String sessionId;
  private final Date startedAt;
  private AtomicBoolean tracked = new AtomicBoolean(false);
  private AtomicInteger unhandledCount = new AtomicInteger();
  private final User user;
  
  Session(String paramString, Date paramDate, User paramUser, int paramInt1, int paramInt2)
  {
    sessionId = paramString;
    startedAt = new Date(paramDate.getTime());
    user = paramUser;
    autoCaptured = new AtomicBoolean(false);
    unhandledCount = new AtomicInteger(paramInt1);
    handledCount = new AtomicInteger(paramInt2);
    tracked = new AtomicBoolean(true);
  }
  
  public Session(String paramString, Date paramDate, User paramUser, boolean paramBoolean)
  {
    sessionId = paramString;
    startedAt = new Date(paramDate.getTime());
    user = paramUser;
    autoCaptured = new AtomicBoolean(paramBoolean);
  }
  
  static Session copySession(Session paramSession)
  {
    Session localSession = new Session(sessionId, startedAt, user, unhandledCount.get(), handledCount.get());
    tracked.set(tracked.get());
    autoCaptured.set(paramSession.isAutoCaptured());
    return localSession;
  }
  
  int getHandledCount()
  {
    return handledCount.intValue();
  }
  
  String getId()
  {
    return sessionId;
  }
  
  Date getStartedAt()
  {
    return new Date(startedAt.getTime());
  }
  
  int getUnhandledCount()
  {
    return unhandledCount.intValue();
  }
  
  User getUser()
  {
    return user;
  }
  
  Session incrementHandledAndCopy()
  {
    handledCount.incrementAndGet();
    return copySession(this);
  }
  
  Session incrementUnhandledAndCopy()
  {
    unhandledCount.incrementAndGet();
    return copySession(this);
  }
  
  boolean isAutoCaptured()
  {
    return autoCaptured.get();
  }
  
  AtomicBoolean isTracked()
  {
    return tracked;
  }
  
  void setAutoCaptured(boolean paramBoolean)
  {
    autoCaptured.set(paramBoolean);
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    paramJsonStream.beginObject().name("id").value(sessionId).name("startedAt").value(DateUtils.toIso8601(startedAt));
    if (user != null) {
      paramJsonStream.name("user").value(user);
    }
    paramJsonStream.endObject();
  }
}
