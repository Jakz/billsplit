package com.github.jakz.billsplit;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Currency;
import com.github.jakz.billsplit.data.Debt;
import com.github.jakz.billsplit.data.Person;
import com.github.jakz.billsplit.data.Pool;
import com.github.jakz.billsplit.data.Timestamp;

public class MoneyPoolTests
{
  private List<Debt> settle(ExpenseSet expenses)
  {
    Settler settler = new Settler();
    List<Debt> debts = settler.generateDebts(expenses, Currency.EUR);
    return settler.settle(debts);
  }
  
  private void assertEqualDebts(List<Debt> actual, Debt... expected)
  {
    Set<Debt> s1 = new HashSet<>(actual), s2 = new HashSet<>(Arrays.asList(expected));
    assertEquals(s1, s2);
  }
  
  private Debt debt(Person debtor, String amount, Person creditor)
  {
    return Debt.of(debtor, Amount.of(amount), creditor);
  }
  
  @Test
  public void testSinglePersonPool()
  {
    Person a = new Person("a");
    Pool pool = new Pool("pool");
    
    List<Expense> expenses = Arrays.asList(
       Expense.deposit(Amount.of("10 EUR"), a, pool, Timestamp.today())
    );
    
    ExpenseSet set = new ExpenseSet(expenses);
    List<Debt> debts = settle(set); 
    
    assertEqualDebts(debts, 
        debt(pool, "10 EUR", a)
    );
  }
}
