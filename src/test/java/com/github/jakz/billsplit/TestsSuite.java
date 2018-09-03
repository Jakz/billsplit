package com.github.jakz.billsplit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  MultiAmountTests.class,
  ExchangeRatesTests.class
})
public class TestsSuite
{
  
}