package com.github.jakz.billsplit;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Debt;
import com.github.jakz.billsplit.data.Person;

public class DebtTests
{
  @Test
  public void testSumOfDebts()
  {
    Person debtor = new Person("foo"), creditor = new Person("bar");
    
    Debt debt1 = new Debt(debtor, creditor, Amount.of("10.00 USD"));
    Debt debt2 = new Debt(debtor, creditor, Amount.of("22.22 USD"));
    Debt result = debt1.add(debt2);
    
    assertEquals(result.debtor, debtor);
    assertEquals(result.creditor, creditor);
    assertEquals(result.amount, Amount.of("32.22 USD"));
  }
  
  @Test
  public void testSumOfDebtAndCreditToPositive()
  {
    Person debtor = new Person("foo"), creditor = new Person("bar");
    
    Debt debt1 = new Debt(debtor, creditor, Amount.of("10.00 USD"));
    Debt debt2 = new Debt(debtor, creditor, Amount.of("-8.11 USD"));
    Debt result = debt1.add(debt2);
    
    assertEquals(result.debtor, debtor);
    assertEquals(result.creditor, creditor);
    assertEquals(result.amount, Amount.of("1.89 USD"));
  }
  
  @Test
  public void testSumOfDebtAndCreditToNegative()
  {
    Person debtor = new Person("foo"), creditor = new Person("bar");
    
    Debt debt1 = new Debt(debtor, creditor, Amount.of("10.00 USD"));
    Debt debt2 = new Debt(debtor, creditor, Amount.of("-22.22 USD"));
    Debt result = debt1.add(debt2);
    
    assertEquals(result.debtor, debtor);
    assertEquals(result.creditor, creditor);
    assertEquals(result.amount, Amount.of("-12.22 USD"));
  }
  
  @Test
  public void testSumOfCredits()
  {
    Person debtor = new Person("foo"), creditor = new Person("bar");
    
    Debt debt1 = new Debt(debtor, creditor, Amount.of("-1.56 USD"));
    Debt debt2 = new Debt(debtor, creditor, Amount.of("-1.44 USD"));
    Debt result = debt1.add(debt2);
    
    assertEquals(result.debtor, debtor);
    assertEquals(result.creditor, creditor);
    assertEquals(result.amount, Amount.of("-3 USD"));
  }
}
