package com.github.jakz.billsplit.data;

import java.util.Arrays;

import javax.money.CurrencyQuery;
import javax.money.CurrencyUnit;
import javax.money.Monetary;

public enum Currency
{
  USD("USD", "United Stated Dollar"),
  EUR("EUR", "Euro"),
  CLP("CLP", "Chilean Peso"),
  ;
  
  public final CurrencyUnit ref;
  public final String name;
  
  private Currency(String code, String name)
  {
    ref = Monetary.getCurrency(code);
    this.name = name;
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
