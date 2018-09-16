package com.github.jakz.billsplit.ui;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.jakz.billsplit.data.Amount;
import com.pixbits.lib.functional.MinMaxTotCollector;
import com.pixbits.lib.lang.AtomicFloat;

public interface SummaryBarBehavior<T>
{
  public float barWidthFor(SummaryEntry<T> entry);
  public String barLabelFor(SummaryEntry<T> entry);
  
  public static <U> SummaryBarBehavior<U> ofAveraging(Iterable<SummaryEntry<U>> data, Function<SummaryEntry<U>, Float> classifier, BiFunction<SummaryEntry<U>, Float, String> labeler)
  {
    Stream<SummaryEntry<U>> stream = StreamSupport.stream(data.spliterator(), false);
    
    AtomicFloat max = new AtomicFloat(0.0f);
    AtomicFloat tot = new AtomicFloat(0.0f);
    
    stream.forEach(u -> {
      max.set(Math.max(max.get(), classifier.apply(u)));
      tot.set(tot.get() + classifier.apply(u));
    });

    return new SummaryBarBehavior<>()
    {
      @Override
      public float barWidthFor(SummaryEntry<U> entry)
      {
        return classifier.apply(entry) / max.get();
      }

      @Override
      public String barLabelFor(SummaryEntry<U> entry)
      {
        return labeler.apply(entry, entry.amount.unprecise() / tot.get());
      }
    };
  }
}
