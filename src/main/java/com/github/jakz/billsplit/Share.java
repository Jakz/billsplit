package com.github.jakz.billsplit;

import java.util.Objects;

public class Share<T>
{
  public final Person person;
  public final T value;
  
  public Share(Person person, T value)
  {
    this.person = person;
    this.value = value;
  }
  
  @Override public int hashCode() { return Objects.hash(person, value); }
  @Override public boolean equals(Object o)
  {
    if (o instanceof Share)
      return ((Share<?>)o).person.equals(person) && ((Share<?>)o).value.equals(value);
    else return false;
  }
  
  @Override public String toString()
  {
    return String.format("[%s, %s]", person.nickname(), value.toString());
  }
}
