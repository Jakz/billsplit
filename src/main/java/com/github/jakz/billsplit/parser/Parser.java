package com.github.jakz.billsplit.parser;

import java.io.IOException;
import java.nio.file.Path;

import com.github.jakz.billsplit.Environment;
import com.github.jakz.billsplit.ExpenseSet;

@FunctionalInterface
public interface Parser
{
  ExpenseSet parse(Path path, Environment env) throws IOException;
}
