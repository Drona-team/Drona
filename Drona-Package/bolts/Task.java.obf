package bolts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Task<TResult>
{
  public static final ExecutorService BACKGROUND_EXECUTOR = ;
  private static final Executor IMMEDIATE_EXECUTOR = BoltsExecutors.immediate();
  private static Task<?> TASK_CANCELLED = new Task(true);
  private static Task<Boolean> TASK_FALSE;
  private static Task<?> TASK_NULL;
  private static Task<Boolean> TASK_TRUE;
  public static final Executor UI_THREAD_EXECUTOR = AndroidExecutors.uiThread();
  private static volatile UnobservedExceptionHandler unobservedExceptionHandler;
  private boolean cancelled;
  private boolean complete;
  private List<Continuation<TResult, Void>> continuations = new ArrayList();
  private Exception error;
  private boolean errorHasBeenObserved;
  private final Object lock = new Object();
  private TResult result;
  private UnobservedErrorNotifier unobservedErrorNotifier;
  
  static
  {
    TASK_NULL = new Task(null);
    TASK_TRUE = new Task(Boolean.valueOf(true));
    TASK_FALSE = new Task(Boolean.valueOf(false));
  }
  
  Task() {}
  
  private Task(TResult paramTResult)
  {
    trySetResult(paramTResult);
  }
  
  private Task(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      trySetCancelled();
      return;
    }
    trySetResult(null);
  }
  
  public static <TResult> Task<TResult> call(Callable<TResult> paramCallable)
  {
    return call(paramCallable, IMMEDIATE_EXECUTOR, null);
  }
  
  public static <TResult> Task<TResult> call(Callable<TResult> paramCallable, CancellationToken paramCancellationToken)
  {
    return call(paramCallable, IMMEDIATE_EXECUTOR, paramCancellationToken);
  }
  
  public static <TResult> Task<TResult> call(Callable<TResult> paramCallable, Executor paramExecutor)
  {
    return call(paramCallable, paramExecutor, null);
  }
  
  public static <TResult> Task<TResult> call(final Callable<TResult> paramCallable, Executor paramExecutor, CancellationToken paramCancellationToken)
  {
    final TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    try
    {
      paramExecutor.execute(new Runnable()
      {
        public void run()
        {
          if ((val$ct != null) && (val$ct.isCancellationRequested()))
          {
            localTaskCompletionSource.setCancelled();
            return;
          }
          try
          {
            localTaskCompletionSource.setResult(paramCallable.call());
            return;
          }
          catch (Exception localException)
          {
            localTaskCompletionSource.setError(localException);
            return;
            localTaskCompletionSource.setCancelled();
            return;
          }
          catch (CancellationException localCancellationException)
          {
            for (;;) {}
          }
        }
      });
    }
    catch (Exception paramCallable)
    {
      localTaskCompletionSource.setError(new ExecutorException(paramCallable));
    }
    return localTaskCompletionSource.getTask();
  }
  
  public static <TResult> Task<TResult> callInBackground(Callable<TResult> paramCallable)
  {
    return call(paramCallable, BACKGROUND_EXECUTOR, null);
  }
  
  public static <TResult> Task<TResult> callInBackground(Callable<TResult> paramCallable, CancellationToken paramCancellationToken)
  {
    return call(paramCallable, BACKGROUND_EXECUTOR, paramCancellationToken);
  }
  
  public static <TResult> Task<TResult> cancelled()
  {
    return TASK_CANCELLED;
  }
  
  private static <TContinuationResult, TResult> void completeAfterTask(final TaskCompletionSource<TContinuationResult> paramTaskCompletionSource, final Continuation<TResult, Task<TContinuationResult>> paramContinuation, final Task<TResult> paramTask, Executor paramExecutor, CancellationToken paramCancellationToken)
  {
    try
    {
      paramExecutor.execute(new Runnable()
      {
        public void run()
        {
          if ((val$ct != null) && (val$ct.isCancellationRequested()))
          {
            paramTaskCompletionSource.setCancelled();
            return;
          }
          try
          {
            Task localTask = (Task)paramContinuation.then(paramTask);
            if (localTask == null)
            {
              paramTaskCompletionSource.setResult(null);
              return;
            }
            localTask.continueWith(new Continuation()
            {
              public Void then(Task<TContinuationResult> paramAnonymous2Task)
              {
                if ((val$ct != null) && (val$ct.isCancellationRequested()))
                {
                  val$tcs.setCancelled();
                  return null;
                }
                if (paramAnonymous2Task.isCancelled())
                {
                  val$tcs.setCancelled();
                  return null;
                }
                if (paramAnonymous2Task.isFaulted())
                {
                  val$tcs.setError(paramAnonymous2Task.getError());
                  return null;
                }
                val$tcs.setResult(paramAnonymous2Task.getResult());
                return null;
              }
            });
            return;
          }
          catch (Exception localException)
          {
            paramTaskCompletionSource.setError(localException);
            return;
            paramTaskCompletionSource.setCancelled();
            return;
          }
          catch (CancellationException localCancellationException)
          {
            for (;;) {}
          }
        }
      });
      return;
    }
    catch (Exception paramContinuation)
    {
      paramTaskCompletionSource.setError(new ExecutorException(paramContinuation));
    }
  }
  
  private static <TContinuationResult, TResult> void completeImmediately(final TaskCompletionSource<TContinuationResult> paramTaskCompletionSource, final Continuation<TResult, TContinuationResult> paramContinuation, final Task<TResult> paramTask, Executor paramExecutor, CancellationToken paramCancellationToken)
  {
    try
    {
      paramExecutor.execute(new Runnable()
      {
        public void run()
        {
          if ((val$ct != null) && (val$ct.isCancellationRequested()))
          {
            paramTaskCompletionSource.setCancelled();
            return;
          }
          try
          {
            Object localObject = paramContinuation.then(paramTask);
            paramTaskCompletionSource.setResult(localObject);
            return;
          }
          catch (Exception localException)
          {
            paramTaskCompletionSource.setError(localException);
            return;
            paramTaskCompletionSource.setCancelled();
            return;
          }
          catch (CancellationException localCancellationException)
          {
            for (;;) {}
          }
        }
      });
      return;
    }
    catch (Exception paramContinuation)
    {
      paramTaskCompletionSource.setError(new ExecutorException(paramContinuation));
    }
  }
  
  public static <TResult> Task<TResult>.TaskCompletionSource create()
  {
    Task localTask = new Task();
    localTask.getClass();
    return new TaskCompletionSource();
  }
  
  public static Task<Void> delay(long paramLong)
  {
    return delay(paramLong, BoltsExecutors.scheduled(), null);
  }
  
  public static Task<Void> delay(long paramLong, CancellationToken paramCancellationToken)
  {
    return delay(paramLong, BoltsExecutors.scheduled(), paramCancellationToken);
  }
  
  static Task<Void> delay(long paramLong, ScheduledExecutorService paramScheduledExecutorService, CancellationToken paramCancellationToken)
  {
    if ((paramCancellationToken != null) && (paramCancellationToken.isCancellationRequested())) {
      return cancelled();
    }
    if (paramLong <= 0L) {
      return forResult(null);
    }
    final TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    paramScheduledExecutorService = paramScheduledExecutorService.schedule(new Runnable()
    {
      public void run()
      {
        val$tcs.trySetResult(null);
      }
    }, paramLong, TimeUnit.MILLISECONDS);
    if (paramCancellationToken != null) {
      paramCancellationToken.register(new Runnable()
      {
        public void run()
        {
          val$scheduled.cancel(true);
          localTaskCompletionSource.trySetCancelled();
        }
      });
    }
    return localTaskCompletionSource.getTask();
  }
  
  public static <TResult> Task<TResult> forError(Exception paramException)
  {
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    localTaskCompletionSource.setError(paramException);
    return localTaskCompletionSource.getTask();
  }
  
  public static <TResult> Task<TResult> forResult(TResult paramTResult)
  {
    if (paramTResult == null) {
      return TASK_NULL;
    }
    if ((paramTResult instanceof Boolean))
    {
      if (((Boolean)paramTResult).booleanValue()) {
        return TASK_TRUE;
      }
      return TASK_FALSE;
    }
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    localTaskCompletionSource.setResult(paramTResult);
    return localTaskCompletionSource.getTask();
  }
  
  public static UnobservedExceptionHandler getUnobservedExceptionHandler()
  {
    return unobservedExceptionHandler;
  }
  
  private void runContinuations()
  {
    synchronized (lock)
    {
      Iterator localIterator = continuations.iterator();
      while (localIterator.hasNext())
      {
        Continuation localContinuation = (Continuation)localIterator.next();
        try
        {
          localContinuation.then(this);
        }
        catch (Exception localException)
        {
          throw new RuntimeException(localException);
        }
        catch (RuntimeException localRuntimeException)
        {
          throw localRuntimeException;
        }
      }
      continuations = null;
      return;
    }
  }
  
  public static void setUnobservedExceptionHandler(UnobservedExceptionHandler paramUnobservedExceptionHandler)
  {
    unobservedExceptionHandler = paramUnobservedExceptionHandler;
  }
  
  public static Task<Void> whenAll(Collection<? extends Task<?>> paramCollection)
  {
    if (paramCollection.size() == 0) {
      return forResult(null);
    }
    final TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    final ArrayList localArrayList = new ArrayList();
    Object localObject = new Object();
    final AtomicInteger localAtomicInteger = new AtomicInteger(paramCollection.size());
    final AtomicBoolean localAtomicBoolean = new AtomicBoolean(false);
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      ((Task)paramCollection.next()).continueWith(new Continuation()
      {
        public Void then(Task<Object> paramAnonymousTask)
        {
          if (paramAnonymousTask.isFaulted()) {
            synchronized (val$errorLock)
            {
              localArrayList.add(paramAnonymousTask.getError());
            }
          }
          if (paramAnonymousTask.isCancelled()) {
            localAtomicBoolean.set(true);
          }
          if (localAtomicInteger.decrementAndGet() == 0)
          {
            if (localArrayList.size() != 0)
            {
              if (localArrayList.size() == 1)
              {
                localTaskCompletionSource.setError((Exception)localArrayList.get(0));
                return null;
              }
              paramAnonymousTask = new AggregateException(String.format("There were %d exceptions.", new Object[] { Integer.valueOf(localArrayList.size()) }), localArrayList);
              localTaskCompletionSource.setError(paramAnonymousTask);
              return null;
            }
            if (localAtomicBoolean.get())
            {
              localTaskCompletionSource.setCancelled();
              return null;
            }
            localTaskCompletionSource.setResult(null);
          }
          return null;
        }
      });
    }
    return localTaskCompletionSource.getTask();
  }
  
  public static <TResult> Task<List<TResult>> whenAllResult(Collection<? extends Task<TResult>> paramCollection)
  {
    whenAll(paramCollection).onSuccess(new Continuation()
    {
      public List<TResult> then(Task<Void> paramAnonymousTask)
        throws Exception
      {
        if (val$tasks.size() == 0) {
          return Collections.emptyList();
        }
        paramAnonymousTask = new ArrayList();
        Iterator localIterator = val$tasks.iterator();
        while (localIterator.hasNext()) {
          paramAnonymousTask.add(((Task)localIterator.next()).getResult());
        }
        return paramAnonymousTask;
      }
    });
  }
  
  public static Task<Task<?>> whenAny(Collection<? extends Task<?>> paramCollection)
  {
    if (paramCollection.size() == 0) {
      return forResult(null);
    }
    final TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    AtomicBoolean localAtomicBoolean = new AtomicBoolean(false);
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      ((Task)paramCollection.next()).continueWith(new Continuation()
      {
        public Void then(Task<Object> paramAnonymousTask)
        {
          if (val$isAnyTaskComplete.compareAndSet(false, true)) {
            localTaskCompletionSource.setResult(paramAnonymousTask);
          } else {
            paramAnonymousTask.getError();
          }
          return null;
        }
      });
    }
    return localTaskCompletionSource.getTask();
  }
  
  public static <TResult> Task<Task<TResult>> whenAnyResult(Collection<? extends Task<TResult>> paramCollection)
  {
    if (paramCollection.size() == 0) {
      return forResult(null);
    }
    final TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    AtomicBoolean localAtomicBoolean = new AtomicBoolean(false);
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      ((Task)paramCollection.next()).continueWith(new Continuation()
      {
        public Void then(Task<TResult> paramAnonymousTask)
        {
          if (val$isAnyTaskComplete.compareAndSet(false, true)) {
            localTaskCompletionSource.setResult(paramAnonymousTask);
          } else {
            paramAnonymousTask.getError();
          }
          return null;
        }
      });
    }
    return localTaskCompletionSource.getTask();
  }
  
  public <TOut> Task<TOut> cast()
  {
    return this;
  }
  
  public Task<Void> continueWhile(Callable<Boolean> paramCallable, Continuation<Void, Task<Void>> paramContinuation)
  {
    return continueWhile(paramCallable, paramContinuation, IMMEDIATE_EXECUTOR, null);
  }
  
  public Task<Void> continueWhile(Callable<Boolean> paramCallable, Continuation<Void, Task<Void>> paramContinuation, CancellationToken paramCancellationToken)
  {
    return continueWhile(paramCallable, paramContinuation, IMMEDIATE_EXECUTOR, paramCancellationToken);
  }
  
  public Task<Void> continueWhile(Callable<Boolean> paramCallable, Continuation<Void, Task<Void>> paramContinuation, Executor paramExecutor)
  {
    return continueWhile(paramCallable, paramContinuation, paramExecutor, null);
  }
  
  public Task<Void> continueWhile(final Callable<Boolean> paramCallable, final Continuation<Void, Task<Void>> paramContinuation, final Executor paramExecutor, final CancellationToken paramCancellationToken)
  {
    final Capture localCapture = new Capture();
    localCapture.set(new Continuation()
    {
      public Task<Void> then(Task<Void> paramAnonymousTask)
        throws Exception
      {
        if ((paramCancellationToken != null) && (paramCancellationToken.isCancellationRequested())) {
          return Task.cancelled();
        }
        if (((Boolean)paramCallable.call()).booleanValue()) {
          return Task.forResult(null).onSuccessTask(paramContinuation, paramExecutor).onSuccessTask((Continuation)localCapture.get(), paramExecutor);
        }
        return Task.forResult(null);
      }
    });
    return makeVoid().continueWithTask((Continuation)localCapture.get(), paramExecutor);
  }
  
  public <TContinuationResult> Task<TContinuationResult> continueWith(Continuation<TResult, TContinuationResult> paramContinuation)
  {
    return continueWith(paramContinuation, IMMEDIATE_EXECUTOR, null);
  }
  
  public <TContinuationResult> Task<TContinuationResult> continueWith(Continuation<TResult, TContinuationResult> paramContinuation, CancellationToken paramCancellationToken)
  {
    return continueWith(paramContinuation, IMMEDIATE_EXECUTOR, paramCancellationToken);
  }
  
  public <TContinuationResult> Task<TContinuationResult> continueWith(Continuation<TResult, TContinuationResult> paramContinuation, Executor paramExecutor)
  {
    return continueWith(paramContinuation, paramExecutor, null);
  }
  
  public <TContinuationResult> Task<TContinuationResult> continueWith(final Continuation<TResult, TContinuationResult> paramContinuation, final Executor paramExecutor, final CancellationToken paramCancellationToken)
  {
    final TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    synchronized (lock)
    {
      boolean bool = isCompleted();
      if (!bool) {
        continuations.add(new Continuation()
        {
          public Void then(Task<TResult> paramAnonymousTask)
          {
            Task.completeImmediately(localTaskCompletionSource, paramContinuation, paramAnonymousTask, paramExecutor, paramCancellationToken);
            return null;
          }
        });
      }
      if (bool) {
        completeImmediately(localTaskCompletionSource, paramContinuation, this, paramExecutor, paramCancellationToken);
      }
      return localTaskCompletionSource.getTask();
    }
  }
  
  public <TContinuationResult> Task<TContinuationResult> continueWithTask(Continuation<TResult, Task<TContinuationResult>> paramContinuation)
  {
    return continueWithTask(paramContinuation, IMMEDIATE_EXECUTOR, null);
  }
  
  public <TContinuationResult> Task<TContinuationResult> continueWithTask(Continuation<TResult, Task<TContinuationResult>> paramContinuation, CancellationToken paramCancellationToken)
  {
    return continueWithTask(paramContinuation, IMMEDIATE_EXECUTOR, paramCancellationToken);
  }
  
  public <TContinuationResult> Task<TContinuationResult> continueWithTask(Continuation<TResult, Task<TContinuationResult>> paramContinuation, Executor paramExecutor)
  {
    return continueWithTask(paramContinuation, paramExecutor, null);
  }
  
  public <TContinuationResult> Task<TContinuationResult> continueWithTask(final Continuation<TResult, Task<TContinuationResult>> paramContinuation, final Executor paramExecutor, final CancellationToken paramCancellationToken)
  {
    final TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    synchronized (lock)
    {
      boolean bool = isCompleted();
      if (!bool) {
        continuations.add(new Continuation()
        {
          public Void then(Task<TResult> paramAnonymousTask)
          {
            Task.completeAfterTask(localTaskCompletionSource, paramContinuation, paramAnonymousTask, paramExecutor, paramCancellationToken);
            return null;
          }
        });
      }
      if (bool) {
        completeAfterTask(localTaskCompletionSource, paramContinuation, this, paramExecutor, paramCancellationToken);
      }
      return localTaskCompletionSource.getTask();
    }
  }
  
  public Exception getError()
  {
    synchronized (lock)
    {
      if (error != null)
      {
        errorHasBeenObserved = true;
        if (unobservedErrorNotifier != null)
        {
          unobservedErrorNotifier.setObserved();
          unobservedErrorNotifier = null;
        }
      }
      Exception localException = error;
      return localException;
    }
  }
  
  public TResult getResult()
  {
    synchronized (lock)
    {
      Object localObject2 = result;
      return localObject2;
    }
  }
  
  public boolean isCancelled()
  {
    synchronized (lock)
    {
      boolean bool = cancelled;
      return bool;
    }
  }
  
  public boolean isCompleted()
  {
    synchronized (lock)
    {
      boolean bool = complete;
      return bool;
    }
  }
  
  public boolean isFaulted()
  {
    for (;;)
    {
      synchronized (lock)
      {
        if (getError() != null)
        {
          bool = true;
          return bool;
        }
      }
      boolean bool = false;
    }
  }
  
  public Task<Void> makeVoid()
  {
    continueWithTask(new Continuation()
    {
      public Task<Void> then(Task<TResult> paramAnonymousTask)
        throws Exception
      {
        if (paramAnonymousTask.isCancelled()) {
          return Task.cancelled();
        }
        if (paramAnonymousTask.isFaulted()) {
          return Task.forError(paramAnonymousTask.getError());
        }
        return Task.forResult(null);
      }
    });
  }
  
  public <TContinuationResult> Task<TContinuationResult> onSuccess(Continuation<TResult, TContinuationResult> paramContinuation)
  {
    return onSuccess(paramContinuation, IMMEDIATE_EXECUTOR, null);
  }
  
  public <TContinuationResult> Task<TContinuationResult> onSuccess(Continuation<TResult, TContinuationResult> paramContinuation, CancellationToken paramCancellationToken)
  {
    return onSuccess(paramContinuation, IMMEDIATE_EXECUTOR, paramCancellationToken);
  }
  
  public <TContinuationResult> Task<TContinuationResult> onSuccess(Continuation<TResult, TContinuationResult> paramContinuation, Executor paramExecutor)
  {
    return onSuccess(paramContinuation, paramExecutor, null);
  }
  
  public <TContinuationResult> Task<TContinuationResult> onSuccess(final Continuation<TResult, TContinuationResult> paramContinuation, Executor paramExecutor, final CancellationToken paramCancellationToken)
  {
    continueWithTask(new Continuation()
    {
      public Task<TContinuationResult> then(Task<TResult> paramAnonymousTask)
      {
        if ((paramCancellationToken != null) && (paramCancellationToken.isCancellationRequested())) {
          return Task.cancelled();
        }
        if (paramAnonymousTask.isFaulted()) {
          return Task.forError(paramAnonymousTask.getError());
        }
        if (paramAnonymousTask.isCancelled()) {
          return Task.cancelled();
        }
        return paramAnonymousTask.continueWith(paramContinuation);
      }
    }, paramExecutor);
  }
  
  public <TContinuationResult> Task<TContinuationResult> onSuccessTask(Continuation<TResult, Task<TContinuationResult>> paramContinuation)
  {
    return onSuccessTask(paramContinuation, IMMEDIATE_EXECUTOR);
  }
  
  public <TContinuationResult> Task<TContinuationResult> onSuccessTask(Continuation<TResult, Task<TContinuationResult>> paramContinuation, CancellationToken paramCancellationToken)
  {
    return onSuccessTask(paramContinuation, IMMEDIATE_EXECUTOR, paramCancellationToken);
  }
  
  public <TContinuationResult> Task<TContinuationResult> onSuccessTask(Continuation<TResult, Task<TContinuationResult>> paramContinuation, Executor paramExecutor)
  {
    return onSuccessTask(paramContinuation, paramExecutor, null);
  }
  
  public <TContinuationResult> Task<TContinuationResult> onSuccessTask(final Continuation<TResult, Task<TContinuationResult>> paramContinuation, Executor paramExecutor, final CancellationToken paramCancellationToken)
  {
    continueWithTask(new Continuation()
    {
      public Task<TContinuationResult> then(Task<TResult> paramAnonymousTask)
      {
        if ((paramCancellationToken != null) && (paramCancellationToken.isCancellationRequested())) {
          return Task.cancelled();
        }
        if (paramAnonymousTask.isFaulted()) {
          return Task.forError(paramAnonymousTask.getError());
        }
        if (paramAnonymousTask.isCancelled()) {
          return Task.cancelled();
        }
        return paramAnonymousTask.continueWithTask(paramContinuation);
      }
    }, paramExecutor);
  }
  
  boolean trySetCancelled()
  {
    synchronized (lock)
    {
      if (complete) {
        return false;
      }
      complete = true;
      cancelled = true;
      lock.notifyAll();
      runContinuations();
      return true;
    }
  }
  
  boolean trySetError(Exception paramException)
  {
    synchronized (lock)
    {
      if (complete) {
        return false;
      }
      complete = true;
      error = paramException;
      errorHasBeenObserved = false;
      lock.notifyAll();
      runContinuations();
      if ((!errorHasBeenObserved) && (getUnobservedExceptionHandler() != null)) {
        unobservedErrorNotifier = new UnobservedErrorNotifier(this);
      }
      return true;
    }
  }
  
  boolean trySetResult(TResult paramTResult)
  {
    synchronized (lock)
    {
      if (complete) {
        return false;
      }
      complete = true;
      result = paramTResult;
      lock.notifyAll();
      runContinuations();
      return true;
    }
  }
  
  public void waitForCompletion()
    throws InterruptedException
  {
    synchronized (lock)
    {
      if (!isCompleted()) {
        lock.wait();
      }
      return;
    }
  }
  
  public boolean waitForCompletion(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    synchronized (lock)
    {
      if (!isCompleted()) {
        lock.wait(paramTimeUnit.toMillis(paramLong));
      }
      boolean bool = isCompleted();
      return bool;
    }
  }
  
  public class TaskCompletionSource
    extends TaskCompletionSource<TResult>
  {
    TaskCompletionSource() {}
  }
  
  public static abstract interface UnobservedExceptionHandler
  {
    public abstract void unobservedException(Task<?> paramTask, UnobservedTaskException paramUnobservedTaskException);
  }
}
