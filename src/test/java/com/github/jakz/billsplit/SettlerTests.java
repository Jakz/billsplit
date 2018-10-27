package com.github.jakz.billsplit;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Debt;
import com.github.jakz.billsplit.data.Person;

public class SettlerTests
{
  /* test on generation from expense */
  
  @Test
  public void testSingleExpenseDebtSamePerson()
  {
    Person person = new Person("foo");
    Expense expense = Expense.of(Amount.of("10.00 USD"), person, new WeightedGroup(person));
    List<Debt> debts = new Settler().generateDebts(expense); 
    
    assertEquals(debts.size(), 1);
    assertEquals(debts.get(0), new Debt(person, person, Amount.of("10.00 USD")));
  }
  
  @Test
  public void testSingleExpenseDebt()
  {
    Person foo = new Person("foo"), bar = new Person("bar");
    Expense expense = Expense.of(Amount.of("10.00 USD"), foo, new WeightedGroup(bar));
    List<Debt> debts = new Settler().generateDebts(expense); 
    
    assertEquals(debts.size(), 1);
    assertEquals(debts.get(0), new Debt(bar, foo, Amount.of("10.00 USD")));
  }
  
  @Test
  public void testSingleExpenseOwedByTwo()
  {
    Person foo = new Person("foo"), bar = new Person("bar");
    Expense expense = Expense.of(Amount.of("10.00 USD"), foo, new WeightedGroup(foo,bar));
    List<Debt> debts = new Settler().generateDebts(expense); 
    
    assertEquals(debts.size(), 2);
    assertEquals(debts.get(0), new Debt(foo, foo, Amount.of("5.00 USD")));
    assertEquals(debts.get(1), new Debt(bar, foo, Amount.of("5.00 USD")));
  }
  
  @Test
  public void testSingleExpenseSharedAndOwedByTwo()
  {
    Person foo = new Person("foo"), bar = new Person("bar");
    ExpenseAmounts amounts = new ExpenseAmounts();
    amounts.add(foo, Amount.of("5 USD"));
    amounts.add(bar, Amount.of("5 USD"));
    
    Expense expense = Expense.of(amounts, new WeightedGroup(foo,bar));
    List<Debt> debts = new Settler().generateDebts(expense); 
    
    assertEquals(debts.size(), 4);
    assertEquals(debts.get(0), new Debt(foo, foo, Amount.of("2.50 USD")));
    assertEquals(debts.get(1), new Debt(foo, bar, Amount.of("2.50 USD")));
    assertEquals(debts.get(2), new Debt(bar, foo, Amount.of("2.50 USD")));
    assertEquals(debts.get(3), new Debt(bar, bar, Amount.of("2.50 USD")));
  }
  
  /* simplification tests */
  
  @Test
  public void testSumOfPositiveDebts()
  {
    Person foo = new Person("foo"), bar = new Person("bar");
    List<Debt> debts = new Settler().simplifyDirectDebts(
        Arrays.asList(new Debt[] { 
            new Debt(foo, bar, Amount.of("5 USD")), 
            new Debt(foo, bar, Amount.of("10 USD"))
        })
    );
    
    assertEquals(debts.size(), 1);
    assertEquals(new Debt(foo, bar, Amount.of("15USD")), debts.get(0));
  }
  
  @Test
  public void testSumOfReciprocalDebtsShouldBeZero()
  {
    Person foo = new Person("foo"), bar = new Person("bar");
    List<Debt> debts = new Settler().simplifyDirectDebts(
        Arrays.asList(new Debt[] { 
            new Debt(foo, bar, Amount.of("5 USD")), 
            new Debt(bar, foo, Amount.of("5 USD"))
        })
    );
    
    assertEquals(debts.size(), 0);
  }
  
  @Test
  public void testSumOfPositiveAndNegativeDebtWithPositiveResult()
  {
    Person foo = new Person("foo"), bar = new Person("bar");
    List<Debt> debts = new Settler().simplifyDirectDebts(
        Arrays.asList(new Debt[] { 
            new Debt(foo, bar, Amount.of("10 USD")), 
            new Debt(bar, foo, Amount.of("5 USD"))
        })
    );
    
    assertEquals(1, debts.size());
    assertEquals(new Debt(foo, bar, Amount.of("5USD")), debts.get(0));
  }
  
  @Test
  public void testSumOfPositiveAndNegativeDebtWithNegativeResult()
  {
    Person foo = new Person("foo"), bar = new Person("bar");
    List<Debt> debts = new Settler().simplifyDirectDebts(
        Arrays.asList(new Debt[] { 
            new Debt(foo, bar, Amount.of("5 USD")), 
            new Debt(bar, foo, Amount.of("10 USD"))
        })
    );
    
    assertEquals(1, debts.size());
    assertEquals(new Debt(foo, bar, Amount.of("-5USD")), debts.get(0));
  }
  
  /* cycles simplification tests */
  
  @Test
  public void testCycleOfTwoPeople()
  {
    System.out.println("AAA");
    
    Person a = new Person("a"), b = new Person("b");
    List<Debt> debts = new Settler().settleCycles(
        Arrays.asList(new Debt[] { 
            new Debt(a, b, Amount.of("5 USD")), 
            new Debt(b, a, Amount.of("10 USD"))
        })
    );
    
    System.out.println("BBB");

    assertEquals(Arrays.asList(new Debt(b, a, Amount.of("5USD"))), debts);
  }
  
  @Test
  public void testCycleOfThreePeople()
  {
    Person a = new Person("a"), b = new Person("b"), c = new Person("c");
    List<Debt> debts = new Settler().settleCycles(
        Arrays.asList(new Debt[] { 
            new Debt(a, b, Amount.of("50 USD")), 
            new Debt(b, c, Amount.of("20 USD")),
            new Debt(c, a, Amount.of("10 USD"))
        })
    );
    
    assertEquals(2, debts.size());
    assertEquals(new Debt(a, b, Amount.of("40USD")), debts.get(0));
    assertEquals(new Debt(b, c, Amount.of("10USD")), debts.get(1));

  }
}
