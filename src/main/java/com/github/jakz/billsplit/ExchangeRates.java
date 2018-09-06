package com.github.jakz.billsplit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.money.MonetaryOperator;
import javax.money.convert.MonetaryConversions;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Currency;
import com.pixbits.lib.lang.Pair;

public interface ExchangeRates
{
  default public Currency baseCurrency() { return Currency.EUR; }
  public float exchangeRateFor(Currency from, Currency to);
  
  default public Amount convertedValue(Amount amount, Currency to)
  {
    if (to != amount.currency())    
      return amount.multiply(exchangeRateFor(amount.currency(), to)).with(to);
    else
      return amount.with(to);
  }
  
  public static class Provider
  {
    private static ExchangeRates instance;
    
    static
    {
      RateMap map = new RateMap();
      instance = map;
      
      map.register(Currency.EUR, Currency.USD, 1.1632f);
      map.register(Currency.EUR, Currency.CLP, 750.0f);
      map.register(Currency.EUR, Currency.PEN, 3.80f);

    }
    
    public static ExchangeRates rates() { return instance; }
    public static void setRates(ExchangeRates rates) { instance = rates; }   
  }
  
  public static class RateMap implements ExchangeRates
  {
    private Map<Currency, Map<Currency, Float>> rates;
    
    public RateMap()
    {
      rates = new HashMap<>();
    }

    @Override
    public float exchangeRateFor(Currency from, Currency to)
    {
      Map<Currency, Float> outer = rates.get(from);
      
      if (outer != null && outer.containsKey(to))
        return outer.get(to);
      
      outer = rates.get(to);
      
      if (outer != null && outer.containsKey(from))
        return 1.0f / outer.get(from);
      
      return 1.0f;
    }
    
    public void register(Currency from, Currency to, float rate)
    {
      rates.computeIfAbsent(from, c -> new HashMap<>()).put(to, rate);
    }
    
    public void clear()
    {
      rates.clear();
    }
  }
}
