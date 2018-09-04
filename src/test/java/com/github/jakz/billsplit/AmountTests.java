package com.github.jakz.billsplit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Currency;

public class AmountTests
{
  @Test
  public void testAmountToPrimitiveConversions()
  {
    Amount amount = new Amount(123.45, Currency.EUR);
    
    assertEquals(45, amount.centesimals());
    assertEquals(123, amount.integral());
    assertEquals(123.45f, amount.unprecise(), 0.0001f);
  }
  
  @Test
  public void testAddCentesimals()
  {
    Amount amount = new Amount(100.00f, Currency.USD);
    Amount famount = amount.add(Amount.of("0.56 USD"));
    
    assertEquals(famount, Amount.of("100.56 USD"));
  }
}
