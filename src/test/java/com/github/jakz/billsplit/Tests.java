package com.github.jakz.billsplit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Tests
{
  @Test
  public void testAmount()
  {
    Amount amount = new Amount(123.45, Currency.EUR);
    
    assertEquals(45, amount.centesimals());
    assertEquals(123, amount.integral());
    assertEquals(123.45f, amount.unprecise(), 0.0001f);
  }
}
