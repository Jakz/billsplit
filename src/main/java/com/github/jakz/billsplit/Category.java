package com.github.jakz.billsplit;

import java.awt.Color;

public interface Category
{
  String caption();
  default Category parent() { return null; }
}
