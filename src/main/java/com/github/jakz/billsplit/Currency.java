package com.github.jakz.billsplit;

import java.util.Arrays;

import javax.money.CurrencyQuery;
import javax.money.CurrencyUnit;
import javax.money.Monetary;

public enum Currency
{
  USD("USD"),
  EUR("EUR")
  ;
  
  public final CurrencyUnit ref;
  
  private Currency(String code)
  {
    ref = Monetary.getCurrency(code);
  }
  
  public int number() { return ref.getNumericCode(); }
  public String code() { return ref.getCurrencyCode(); }
  
  public static Currency forCode(String code)
  {
    return Arrays.stream(values())
      .filter(c -> c.code().equals(code))
      .findFirst().orElse(null);
  }
}
