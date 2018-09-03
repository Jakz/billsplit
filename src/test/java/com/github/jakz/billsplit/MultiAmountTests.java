package com.github.jakz.billsplit;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class MultiAmountTests
{
  @Test
  public void testEmptyCollapsedIsZero()
  {
    assertEquals(new MultiAmount().collapse(), Amount.zero());
  }
  
  @Test
  public void testSingleCollapsedAmountIsEqual()
  {
    Amount amount = Amount.of("123.45 EUR");    
    assertEquals(new MultiAmount(Collections.singleton(amount)).collapse(), amount);
  }
  
  @Test
  public void testDoubleCollapsedAmountWithSameCurrency()
  {
    Amount amount1 = Amount.of("123.45 EUR");  
    Amount amount2 = Amount.of("123.45 EUR");
    
    Amount sum = amount1.add(amount2);
    assertEquals(new MultiAmount(Arrays.asList(amount1, amount2)).collapse(), sum);
  }
}
