package com.airbnb.lottie.utils;

public class MeanCalculator
{
  private float _count;
  private int _total;
  
  public MeanCalculator() {}
  
  public float getMean()
  {
    if (_total == 0) {
      return 0.0F;
    }
    return _count / _total;
  }
  
  public void set(float paramFloat)
  {
    _count += paramFloat;
    _total += 1;
    if (_total == Integer.MAX_VALUE)
    {
      _count /= 2.0F;
      _total /= 2;
    }
  }
}
