package bleshadow.dagger;

public abstract interface Lazy<T>
{
  public abstract Object get();
}
