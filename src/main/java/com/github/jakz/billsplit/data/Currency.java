package com.github.jakz.billsplit.data;

import java.util.Arrays;
import java.util.Comparator;

import javax.money.CurrencyQuery;
import javax.money.CurrencyUnit;
import javax.money.Monetary;

public enum Currency
{
  NONE(null, "None"),
  
  USD("USD", "United Stated Dollar"),
  EUR("EUR", "Euro"),
  
  CLP("CLP", "Chilean Peso"),
  PEN("PEN", "Peruvian Sol"),
  ;
  
  public final CurrencyUnit ref;
  public final String name;
  
  private Currency(String code, String name)
  {
    if (code != null)
      ref = Monetary.getCurrency(code);
    else
      ref = null;
    this.name = name;
  }
  
  public boolean isReal() { return ref != null; }
  public int number() { return ref.getNumericCode(); }
  public String code() { return ref.getCurrencyCode(); }
  public String caption() { return name; }
  public String longName() { return ref != null ? String.format("%s (%s)", name, code()) : "None"; }
  
  public static Currency forCode(String code)
  {
    return Arrays.stream(values())
      .filter(c -> c.ref != null)
      .filter(c -> c.code().equals(code))
      .findFirst().orElse(null);
  }
  
  public static Comparator<Currency> COMPARATOR_BY_NAME = (c1, c2) -> {
    if (c1.ref == null && c2.ref == null)
      return 0;
    if (c1.ref == null)
      return -1;
    else if (c2.ref == null)
      return 1;
    else
      return c1.name.compareTo(c2.name);
  };
}
