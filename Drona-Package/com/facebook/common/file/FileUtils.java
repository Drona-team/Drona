package com.facebook.common.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtils
{
  public FileUtils() {}
  
  public static void mkdirs(File paramFile)
    throws FileUtils.CreateDirectoryException
  {
    if (paramFile.exists())
    {
      if (paramFile.isDirectory()) {
        return;
      }
      if (!paramFile.delete()) {
        throw new CreateDirectoryException(paramFile.getAbsolutePath(), new FileDeleteException(paramFile.getAbsolutePath()));
      }
    }
    if (!paramFile.mkdirs())
    {
      if (paramFile.isDirectory()) {
        return;
      }
      throw new CreateDirectoryException(paramFile.getAbsolutePath());
    }
  }
  
  public static void rename(File paramFile1, File paramFile2)
    throws FileUtils.RenameException
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a4 = a3\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public static class CreateDirectoryException
    extends IOException
  {
    public CreateDirectoryException(String paramString)
    {
      super();
    }
    
    public CreateDirectoryException(String paramString, Throwable paramThrowable)
    {
      super();
      initCause(paramThrowable);
    }
  }
  
  public static class FileDeleteException
    extends IOException
  {
    public FileDeleteException(String paramString)
    {
      super();
    }
  }
  
  public static class ParentDirNotFoundException
    extends FileNotFoundException
  {
    public ParentDirNotFoundException(String paramString)
    {
      super();
    }
  }
  
  public static class RenameException
    extends IOException
  {
    public RenameException(String paramString)
    {
      super();
    }
    
    public RenameException(String paramString, Throwable paramThrowable)
    {
      super();
      initCause(paramThrowable);
    }
  }
}
