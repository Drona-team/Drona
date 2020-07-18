package com.facebook.upgrade;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.atomic.AtomicReference;

public class DestructorThread
{
  private static DestructorList sDestructorList;
  private static DestructorStack sDestructorStack = new DestructorStack(null);
  private static ReferenceQueue sReferenceQueue = new ReferenceQueue();
  private static Thread sThread;
  
  static
  {
    sDestructorList = new DestructorList();
    sThread = new Thread("HybridData DestructorThread")
    {
      public void run()
      {
        try
        {
          for (;;)
          {
            Object localObject = DestructorThread.sReferenceQueue.remove();
            localObject = (DestructorThread.Destructor)localObject;
            ((DestructorThread.Destructor)localObject).destruct();
            DestructorThread.Destructor localDestructor = DestructorThread.Destructor.access$300((DestructorThread.Destructor)localObject);
            if (localDestructor == null) {
              DestructorThread.sDestructorStack.transferAllToList();
            }
            DestructorThread.DestructorList.access$400((DestructorThread.Destructor)localObject);
          }
        }
        catch (InterruptedException localInterruptedException) {}
      }
    };
    sThread.start();
  }
  
  public DestructorThread() {}
  
  public abstract class Destructor
    extends PhantomReference<Object>
  {
    private Destructor next;
    private Destructor previous;
    
    private Destructor()
    {
      super(DestructorThread.sReferenceQueue);
    }
    
    public Destructor()
    {
      super(DestructorThread.sReferenceQueue);
      DestructorThread.sDestructorStack.push(this);
    }
    
    protected abstract void destruct();
  }
  
  class DestructorList
  {
    private DestructorThread.Destructor mHead = new DestructorThread.Terminus(null);
    
    public DestructorList()
    {
      DestructorThread.Destructor.access$602(mHead, new DestructorThread.Terminus(null));
      DestructorThread.Destructor.access$302(mHead.next, mHead);
    }
    
    private static void drop(DestructorThread.Destructor paramDestructor)
    {
      DestructorThread.Destructor.access$302(next, previous);
      DestructorThread.Destructor.access$602(previous, next);
    }
    
    public void enqueue(DestructorThread.Destructor paramDestructor)
    {
      DestructorThread.Destructor.access$602(paramDestructor, mHead.next);
      DestructorThread.Destructor.access$602(mHead, paramDestructor);
      DestructorThread.Destructor.access$302(next, paramDestructor);
      DestructorThread.Destructor.access$302(paramDestructor, mHead);
    }
  }
  
  class DestructorStack
  {
    private AtomicReference<com.facebook.jni.DestructorThread.Destructor> mHead = new AtomicReference();
    
    private DestructorStack() {}
    
    public void push(DestructorThread.Destructor paramDestructor)
    {
      DestructorThread.Destructor localDestructor;
      do
      {
        localDestructor = (DestructorThread.Destructor)mHead.get();
        DestructorThread.Destructor.access$602(paramDestructor, localDestructor);
      } while (!mHead.compareAndSet(localDestructor, paramDestructor));
    }
    
    public void transferAllToList()
    {
      DestructorThread.Destructor localDestructor;
      for (Object localObject = (DestructorThread.Destructor)mHead.getAndSet(null); localObject != null; localObject = localDestructor)
      {
        localDestructor = next;
        DestructorThread.sDestructorList.enqueue((DestructorThread.Destructor)localObject);
      }
    }
  }
  
  class Terminus
    extends DestructorThread.Destructor
  {
    private Terminus()
    {
      super();
    }
    
    protected void destruct()
    {
      throw new IllegalStateException("Cannot destroy Terminus Destructor.");
    }
  }
}
