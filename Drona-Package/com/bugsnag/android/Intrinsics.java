package com.bugsnag.android;

class Intrinsics
{
  Intrinsics() {}
  
  static boolean isEmpty(CharSequence paramCharSequence)
  {
    return (paramCharSequence == null) || (paramCharSequence.length() == 0);
  }
}
