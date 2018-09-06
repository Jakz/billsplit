package com.github.jakz.billsplit.ui;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.jakz.billsplit.data.Amount;
import com.pixbits.lib.functional.MinMaxTotCollector;

public interface SummaryBarBehavior<T extends SummaryEntry>
{
  public float barWidthFor(T entry);
  public String barLabelFor(T entry);
  
  public static <U extends SummaryEntry> SummaryBarBehavior<U> ofAveraging(Iterable<U> data, Function<U, Float> classifier, BiFunction<U, Float, String> labeler)
  {
    Stream<U> stream = StreamSupport.stream(data.spliterator(), false);
    
    //final float total = (float)(StreamSupport.stream(data.spliterator(), false)).map(classifier).mapToDouble(f -> f).sum();
    //final float max = (float)stream.map(classifier).mapToDouble(f -> f).max().getAsDouble();
    MinMaxTotCollector<U, Float> collector = stream.collect(
      MinMaxTotCollector.of(
        classifier,
        (BinaryOperator<Float>) (s1, s2) -> s1 + s2,
        0.0f
      )
    );

    return new SummaryBarBehavior<>()
    {
      @Override
      public float barWidthFor(U entry)
      {
        return classifier.apply(entry) / collector.max().amount.unprecise();
      }

      @Override
      public String barLabelFor(U entry)
      {
        return labeler.apply(entry, entry.amount.unprecise() / collector.total());
      }
    };
  }
}
