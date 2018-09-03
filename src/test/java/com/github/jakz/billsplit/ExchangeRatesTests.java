package com.github.jakz.billsplit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.jakz.billsplit.ExchangeRates.RateMap;

public class ExchangeRatesTests
{
  private static RateMap rates = new RateMap();
  
  @BeforeClass
  public static void initialize()
  {
    ExchangeRates.Provider.setRates(rates);
  }
  
  @Before
  public void setup()
  {
    rates.clear();
  }
  
  @Test
  public void testNoConversion()
  {
    assertEquals(Amount.of("1.0 USD").convert(Currency.USD), Amount.of("1.00 USD"));
  }
  
  @Test
  public void testForwardConversion()
  {
    rates.register(Currency.EUR, Currency.USD, 1.16f);
    assertEquals(Amount.of("1.0 EUR").convert(Currency.USD), Amount.of("1.16 USD"));
  }
  
  @Test
  public void testReverseConversion()
  {
    rates.register(Currency.EUR, Currency.USD, 1.16f);
    assertEquals(Amount.of("1.16 USD").convert(Currency.EUR), Amount.of("1.00 EUR"));
  }
}
