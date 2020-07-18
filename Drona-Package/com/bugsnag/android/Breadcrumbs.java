package com.bugsnag.android;

import java.io.IOException;
import java.util.Iterator;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class Breadcrumbs
  extends Observable
  implements JsonStream.Streamable
{
  private static final int MAX_PAYLOAD_SIZE = 4096;
  private final Configuration configuration;
  final Queue<Breadcrumb> store = new ConcurrentLinkedQueue();
  
  Breadcrumbs(Configuration paramConfiguration)
  {
    configuration = paramConfiguration;
  }
  
  private void addToStore(Breadcrumb paramBreadcrumb)
  {
    try
    {
      int i = paramBreadcrumb.payloadSize();
      if (i > 4096)
      {
        Logger.warn("Dropping breadcrumb because payload exceeds 4KB limit");
        return;
      }
      Object localObject = store;
      ((Queue)localObject).add(paramBreadcrumb);
      pruneBreadcrumbs();
      setChanged();
      localObject = NativeInterface.MessageType.ADD_BREADCRUMB;
      notifyObservers(new NativeInterface.Message((NativeInterface.MessageType)localObject, paramBreadcrumb));
      return;
    }
    catch (IOException paramBreadcrumb)
    {
      Logger.warn("Dropping breadcrumb because it could not be serialized", paramBreadcrumb);
    }
  }
  
  private void pruneBreadcrumbs()
  {
    int i = configuration.getMaxBreadcrumbs();
    while (store.size() > i) {
      store.poll();
    }
  }
  
  void clear()
  {
    store.clear();
    setChanged();
    notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.CLEAR_BREADCRUMBS, null));
  }
  
  void i(Breadcrumb paramBreadcrumb)
  {
    addToStore(paramBreadcrumb);
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    pruneBreadcrumbs();
    paramJsonStream.beginArray();
    Iterator localIterator = store.iterator();
    while (localIterator.hasNext()) {
      ((Breadcrumb)localIterator.next()).toStream(paramJsonStream);
    }
    paramJsonStream.endArray();
  }
}
