package com.bumptech.glide.load.engine;

import android.util.Log;
import androidx.annotation.Nullable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class GlideException
  extends Exception
{
  private static final StackTraceElement[] EMPTY_ELEMENTS = new StackTraceElement[0];
  private static final long serialVersionUID = 1L;
  private final List<Throwable> causes;
  private Class<?> dataClass;
  private DataSource dataSource;
  private String detailMessage;
  @Nullable
  private Exception exception;
  private Key lookahead;
  
  public GlideException(String paramString)
  {
    this(paramString, Collections.emptyList());
  }
  
  public GlideException(String paramString, Throwable paramThrowable)
  {
    this(paramString, Collections.singletonList(paramThrowable));
  }
  
  public GlideException(String paramString, List paramList)
  {
    detailMessage = paramString;
    setStackTrace(EMPTY_ELEMENTS);
    causes = paramList;
  }
  
  private void addRootCauses(Throwable paramThrowable, List paramList)
  {
    if ((paramThrowable instanceof GlideException))
    {
      paramThrowable = ((GlideException)paramThrowable).getCauses().iterator();
      while (paramThrowable.hasNext()) {
        addRootCauses((Throwable)paramThrowable.next(), paramList);
      }
    }
    paramList.add(paramThrowable);
  }
  
  private static void appendCauses(List paramList, Appendable paramAppendable)
  {
    try
    {
      appendCausesWrapped(paramList, paramAppendable);
      return;
    }
    catch (IOException paramList)
    {
      throw new RuntimeException(paramList);
    }
  }
  
  private static void appendCausesWrapped(List paramList, Appendable paramAppendable)
    throws IOException
  {
    int k = paramList.size();
    int j;
    for (int i = 0; i < k; i = j)
    {
      Object localObject = paramAppendable.append("Cause (");
      j = i + 1;
      ((Appendable)localObject).append(String.valueOf(j)).append(" of ").append(String.valueOf(k)).append("): ");
      localObject = (Throwable)paramList.get(i);
      if ((localObject instanceof GlideException)) {
        ((GlideException)localObject).printStackTrace(paramAppendable);
      } else {
        appendExceptionMessage((Throwable)localObject, paramAppendable);
      }
    }
  }
  
  private static void appendExceptionMessage(Throwable paramThrowable, Appendable paramAppendable)
  {
    try
    {
      paramAppendable.append(paramThrowable.getClass().toString()).append(": ").append(paramThrowable.getMessage()).append('\n');
      return;
    }
    catch (IOException paramAppendable)
    {
      for (;;) {}
    }
    throw new RuntimeException(paramThrowable);
  }
  
  private void printStackTrace(Appendable paramAppendable)
  {
    appendExceptionMessage(this, paramAppendable);
    appendCauses(getCauses(), new IndentedAppendable(paramAppendable));
  }
  
  public Throwable fillInStackTrace()
  {
    return this;
  }
  
  public List getCauses()
  {
    return causes;
  }
  
  public String getMessage()
  {
    StringBuilder localStringBuilder = new StringBuilder(71);
    localStringBuilder.append(detailMessage);
    if (dataClass != null)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(", ");
      ((StringBuilder)localObject).append(dataClass);
      localObject = ((StringBuilder)localObject).toString();
    }
    else
    {
      localObject = "";
    }
    localStringBuilder.append((String)localObject);
    if (dataSource != null)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(", ");
      ((StringBuilder)localObject).append(dataSource);
      localObject = ((StringBuilder)localObject).toString();
    }
    else
    {
      localObject = "";
    }
    localStringBuilder.append((String)localObject);
    if (lookahead != null)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(", ");
      ((StringBuilder)localObject).append(lookahead);
      localObject = ((StringBuilder)localObject).toString();
    }
    else
    {
      localObject = "";
    }
    localStringBuilder.append((String)localObject);
    Object localObject = getRootCauses();
    if (((List)localObject).isEmpty()) {
      return localStringBuilder.toString();
    }
    if (((List)localObject).size() == 1)
    {
      localStringBuilder.append("\nThere was 1 cause:");
    }
    else
    {
      localStringBuilder.append("\nThere were ");
      localStringBuilder.append(((List)localObject).size());
      localStringBuilder.append(" causes:");
    }
    localObject = ((List)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      Throwable localThrowable = (Throwable)((Iterator)localObject).next();
      localStringBuilder.append('\n');
      localStringBuilder.append(localThrowable.getClass().getName());
      localStringBuilder.append('(');
      localStringBuilder.append(localThrowable.getMessage());
      localStringBuilder.append(')');
    }
    localStringBuilder.append("\n call GlideException#logRootCauses(String) for more detail");
    return localStringBuilder.toString();
  }
  
  public Exception getOrigin()
  {
    return exception;
  }
  
  public List getRootCauses()
  {
    ArrayList localArrayList = new ArrayList();
    addRootCauses(this, localArrayList);
    return localArrayList;
  }
  
  public void logRootCauses(String paramString)
  {
    List localList = getRootCauses();
    int k = localList.size();
    int j;
    for (int i = 0; i < k; i = j)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Root cause (");
      j = i + 1;
      localStringBuilder.append(j);
      localStringBuilder.append(" of ");
      localStringBuilder.append(k);
      localStringBuilder.append(")");
      Log.i(paramString, localStringBuilder.toString(), (Throwable)localList.get(i));
    }
  }
  
  public void printStackTrace()
  {
    printStackTrace(System.err);
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    printStackTrace(paramPrintStream);
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    printStackTrace(paramPrintWriter);
  }
  
  void setLoggingDetails(Key paramKey, DataSource paramDataSource)
  {
    setLoggingDetails(paramKey, paramDataSource, null);
  }
  
  void setLoggingDetails(Key paramKey, DataSource paramDataSource, Class paramClass)
  {
    lookahead = paramKey;
    dataSource = paramDataSource;
    dataClass = paramClass;
  }
  
  public void setOrigin(Exception paramException)
  {
    exception = paramException;
  }
  
  private static final class IndentedAppendable
    implements Appendable
  {
    private static final String EMPTY_SEQUENCE = "";
    private static final String INDENT = "  ";
    private final Appendable appendable;
    private boolean printedNewLine = true;
    
    IndentedAppendable(Appendable paramAppendable)
    {
      appendable = paramAppendable;
    }
    
    private CharSequence safeSequence(CharSequence paramCharSequence)
    {
      if (paramCharSequence == null) {
        return "";
      }
      return paramCharSequence;
    }
    
    public Appendable append(char paramChar)
      throws IOException
    {
      boolean bool2 = printedNewLine;
      boolean bool1 = false;
      if (bool2)
      {
        printedNewLine = false;
        appendable.append("  ");
      }
      if (paramChar == '\n') {
        bool1 = true;
      }
      printedNewLine = bool1;
      appendable.append(paramChar);
      return this;
    }
    
    public Appendable append(CharSequence paramCharSequence)
      throws IOException
    {
      paramCharSequence = safeSequence(paramCharSequence);
      return append(paramCharSequence, 0, paramCharSequence.length());
    }
    
    public Appendable append(CharSequence paramCharSequence, int paramInt1, int paramInt2)
      throws IOException
    {
      paramCharSequence = safeSequence(paramCharSequence);
      boolean bool1 = printedNewLine;
      boolean bool2 = false;
      if (bool1)
      {
        printedNewLine = false;
        appendable.append("  ");
      }
      bool1 = bool2;
      if (paramCharSequence.length() > 0)
      {
        bool1 = bool2;
        if (paramCharSequence.charAt(paramInt2 - 1) == '\n') {
          bool1 = true;
        }
      }
      printedNewLine = bool1;
      appendable.append(paramCharSequence, paramInt1, paramInt2);
      return this;
    }
  }
}
