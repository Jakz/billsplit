package com.github.jakz.billsplit.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import com.github.jakz.billsplit.DefaultCategory;
import com.github.jakz.billsplit.Environment;
import com.github.jakz.billsplit.Expense;
import com.github.jakz.billsplit.ExpenseAmounts;
import com.github.jakz.billsplit.ExpenseSet;
import com.github.jakz.billsplit.WeightedGroup;
import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Category;
import com.github.jakz.billsplit.data.Currency;
import com.github.jakz.billsplit.data.Group;
import com.github.jakz.billsplit.data.Person;
import com.github.jakz.billsplit.data.Timestamp;

public class SplitWiseParser implements Parser
{
  private enum State
  {
    BEGINNING,
    AFTER_HEADER,
    DATA,
    END
  };
  
  // Date,Description,Category,Cost,Currency,Person1,Person2
  
  private Category parseCategory(String category)
  {
    switch (category)
    {
      case "Gas/fuel": return DefaultCategory.GASOLINE;
      case "Liquor": return DefaultCategory.ALCHOOL;
      
      default:
        return DefaultCategory.MISC;
    }
  }

  @Override
  public ExpenseSet parse(Path path, Environment env) throws IOException
  {
    try (Stream<String> lines = Files.lines(path))
    {
      final int FPI = 5;
      AtomicReference<State> state = new AtomicReference<>(State.BEGINNING);
      List<Expense> expenses = new ArrayList<>();
      
      AtomicReference<Person[]> persons = new AtomicReference<>();
      
      lines.forEach(line -> {
        switch (state.get())
        {
          case BEGINNING:
          {
            String tokens[] = line.split("\\,");
            persons.set(new Person[tokens.length - FPI]);
            
            for (int i = FPI; i < tokens.length; ++i)
              persons.get()[i - FPI] = new Person(tokens[i]);
            
            state.set(State.AFTER_HEADER);
            break;
          }
          case AFTER_HEADER: state.set(State.DATA); break;
          case DATA:
          {
            int count = persons.get().length;
            String tokens[] = line.split("\\,");
            
            if (tokens.length < 5)
            {
              state.set(State.END);
              break;
            }
            
            Timestamp timestamp = Timestamp.of(tokens[0]);
            Category category = parseCategory(tokens[2]);
            Currency currency = Currency.forCode(tokens[4]);
            float value = Float.valueOf(tokens[3]);
            String desc = tokens[1];
            
            Amount share = Amount.of(value, currency).divide(count);
            Amount[] balances = new Amount[count];
            for (int i = FPI; i < tokens.length; ++i)
              balances[i-FPI] = Amount.of(Float.valueOf(tokens[i]), currency);

            if (Math.abs(Arrays.asList(balances).stream().mapToDouble(d -> d.unprecise()).sum()) > 0.00001)
              throw new IllegalArgumentException("Balances should be a zero-sum list: "+Arrays.toString(balances));
            
            ExpenseAmounts amounts = new ExpenseAmounts();
            WeightedGroup wgroup = new WeightedGroup(persons.get());
            
            for (int i = 0; i < count; ++i)
            {
              Amount amount = balances[i].add(share);
              if (amount.isStrictlyPositive())
                amounts.add(persons.get()[i], amount);
            }
            
            Amount total = Amount.of(value, currency);
            
            //if (!total.equals(amounts.amount(currency)))
            //  throw new IllegalArgumentException("Sum of values doesn't match: "+total+" != "+amounts.amount(currency)+" "+amounts);

            
            expenses.add(Expense.of(amounts, wgroup, timestamp, category, desc));
          }
          case END: break;
        }
      });
      
      env.setGroup(new Group(persons.get()));
      
      return new ExpenseSet(expenses);
    }
  }

}
