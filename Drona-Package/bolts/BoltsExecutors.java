package bolts;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

final class BoltsExecutors
{
  private static final BoltsExecutors INSTANCE = new BoltsExecutors();
  private final ExecutorService background;
  private final Executor immediate;
  private final ScheduledExecutorService scheduled;
  
  private BoltsExecutors()
  {
    ExecutorService localExecutorService;
    if (!isAndroidRuntime()) {
      localExecutorService = Executors.newCachedThreadPool();
    } else {
      localExecutorService = AndroidExecutors.newCachedThreadPool();
    }
    background = localExecutorService;
    scheduled = Executors.newSingleThreadScheduledExecutor();
    immediate = new ImmediateExecutor(null);
  }
  
  public static ExecutorService background()
  {
    return INSTANCEbackground;
  }
  
  static Executor immediate()
  {
    return INSTANCEimmediate;
  }
  
  private static boolean isAndroidRuntime()
  {
    String str = System.getProperty("java.runtime.name");
    if (str == null) {
      return false;
    }
    return str.toLowerCase(Locale.US).contains("android");
  }
  
  static ScheduledExecutorService scheduled()
  {
    return INSTANCEscheduled;
  }
  
  private static class ImmediateExecutor
    implements Executor
  {
    private static final int MAX_DEPTH = 15;
    private ThreadLocal<Integer> executionDepth = new ThreadLocal();
    
    private ImmediateExecutor() {}
    
    private int decrementDepth()
    {
      Integer localInteger2 = (Integer)executionDepth.get();
      Integer localInteger1 = localInteger2;
      if (localInteger2 == null) {
        localInteger1 = Integer.valueOf(0);
      }
      int i = localInteger1.intValue() - 1;
      if (i == 0)
      {
        executionDepth.remove();
        return i;
      }
      executionDepth.set(Integer.valueOf(i));
      return i;
    }
    
    private int incrementDepth()
    {
      Integer localInteger2 = (Integer)executionDepth.get();
      Integer localInteger1 = localInteger2;
      if (localInteger2 == null) {
        localInteger1 = Integer.valueOf(0);
      }
      int i = localInteger1.intValue() + 1;
      executionDepth.set(Integer.valueOf(i));
      return i;
    }
    
    public void execute(Runnable paramRunnable)
    {
      if (incrementDepth() <= 15) {
        try
        {
          paramRunnable.run();
        }
        catch (Throwable paramRunnable)
        {
          break label37;
        }
      } else {
        BoltsExecutors.background().execute(paramRunnable);
      }
      decrementDepth();
      return;
      label37:
      decrementDepth();
      throw paramRunnable;
    }
  }
}
