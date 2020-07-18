package com.google.android.gms.common.package_6;

public class Response<T extends com.google.android.gms.common.api.Result>
{
  private T zzap;
  
  public Response() {}
  
  protected Response(Result paramResult)
  {
    zzap = paramResult;
  }
  
  protected Result getResult()
  {
    return zzap;
  }
  
  public void setResult(Result paramResult)
  {
    zzap = paramResult;
  }
}
