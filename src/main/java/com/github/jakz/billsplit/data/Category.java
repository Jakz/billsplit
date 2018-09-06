package com.github.jakz.billsplit.data;

import java.awt.Color;

public interface Category
{
  String caption();
  default Category parent() { return null; }
  default Category root() { return null; }
}
