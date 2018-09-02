package com.github.jakz.billsplit;

import java.time.LocalDate;
import java.time.Month;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Objects;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryRounding;
import javax.money.convert.ConversionQuery;
import javax.money.convert.ConversionQueryBuilder;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.javamoney.moneta.ExchangeRateType;
import org.javamoney.moneta.Money;

public class Amount
{
  private final static MonetaryRounding rounding = Monetary.getDefaultRounding();
  
  private MonetaryAmount amount;
  private Currency currency;
  
  public Amount(double value, Currency currency) { this((float)value, currency); }

  public Amount(float value, Currency currency)
  {
    this(Money.of(value, currency.ref), currency);
  }
  
  private Amount(MonetaryAmount value, Currency currency)
  {
    Objects.requireNonNull(currency);
    this.currency = currency;
    this.amount = value;
  }
  
  private static MonetaryAmountFormat formatter = MonetaryFormats.getAmountFormat(Locale.US);
  public String toString()
  {
    return formatter.format(amount).toString();
  }
  
  public Currency currency() { return currency; }
  public long integral() { return rounding.apply(amount).getNumber().intValue(); }
  public long centesimals() { return rounding.apply(amount).getNumber().getAmountFractionNumerator(); }
  public float unprecise() { return rounding.apply(amount).getNumber().floatValue(); }
  
  public static Amount of(float value, Currency currency)
  {
    return new Amount(value, currency);
  }
  
  public static Amount of(String string)
  {
    string = string.trim();
    for (int i = string.length()-1; i >= 0; --i)
    {
      char c = string.charAt(i);
      if (Character.isDigit(c) || Character.isWhitespace(c))
      {
        String currencyString = string.substring(i+1, string.length());
        String valueString = string.substring(0, i+1).trim();
        
        Currency currency = Currency.forCode(currencyString);
        if (currency == null)
          throw new IllegalArgumentException("Currency not found for "+currencyString);
        else
        {
          float value = Float.parseFloat(valueString);
          return new Amount(value, currency);
        }  
      }
    }
    
    throw new IllegalArgumentException("No currency found for string "+string);
  }
  
  public Amount convert(Currency currency)
  {
    return ExchangeRates.RATES.convertedValue(this, currency);
  }
  
  public Amount with(Currency currency)
  { 
    if (currency == this.currency)
      return new Amount(amount, currency);
    else
    {
      MonetaryAmount newAmount = Monetary.getDefaultAmountFactory()
          .setNumber(amount.getNumber())
          .setCurrency(currency.ref)
          .create();
      
      return new Amount(newAmount, currency);
    }
  }
  
  public Amount multiply(float v)
  {
    return new Amount(amount.multiply(v), currency);
  }
  
  public Amount add(Amount amount)
  {    
    if (currency != amount.currency)
      throw new IllegalArgumentException("Cannot add two amounts of different currencies");
    
    return new Amount(this.amount.add(amount.amount), currency);
  }
}
