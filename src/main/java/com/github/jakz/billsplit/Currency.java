package com.github.jakz.billsplit;

import java.util.Arrays;

public enum Currency
{
  EUR("EUR", "€"),
  USD("USD", "$")
  ;
  
  final String code;
  final String symbol;
  
  private Currency(String code, String symbol)
  {
    this.code = code;
    this.symbol = symbol;
  }
  
  public static Currency forCode(String code)
  {
    return Arrays.stream(values())
      .filter(c -> c.code.equals(code))
      .findFirst().orElse(null);
  }
}
