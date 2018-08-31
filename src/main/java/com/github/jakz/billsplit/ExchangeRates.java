package com.github.jakz.billsplit;

import javax.money.convert.MonetaryConversions;

public class ExchangeRates
{
  public float exchangeRateFor(Currency from, Currency to)
  {
    if (from == Currency.EUR && to == Currency.USD)
      return 1.16132f;
    else if (from == Currency.USD && to == Currency.EUR)
      return 1 / 1.16132f;
    
    else
      return 1.0f;
  }
  
  public Amount convertedValue(Amount amount, Currency to)
  {
    return amount.multiply(exchangeRateFor(amount.currency(), to)).with(to);
  }
}
