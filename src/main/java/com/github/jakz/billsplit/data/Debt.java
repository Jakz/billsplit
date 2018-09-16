package com.github.jakz.billsplit.data;

import com.pixbits.lib.algorithm.graphs.DirectedEdge;
import com.pixbits.lib.algorithm.graphs.Vertex;

public class Debt
{
  public final Person debtor;
  public final Person creditor;
  public final Amount amount;
  
  public Debt(Person debtor, Person creditor, Amount amount)
  {
    this.debtor = debtor;
    this.creditor = creditor;
    this.amount = amount;
  }
  
  public boolean equals(Object o)
  {
    if (o instanceof Debt)
    {
      Debt d = (Debt)o;
      return d.debtor.equals(debtor) && d.creditor.equals(creditor) && d.amount.equals(amount);
    }
    else
      return false;
  }
  
  public String toString() { return String.format("(%s, %s, %s)", debtor, creditor, amount); }
 
  
  public Debt positivize()
  {
    return amount.isNegative() ? new Debt(creditor, debtor, amount.negate()) : this;
  }
  
  public boolean canBeCombined(Debt other)
  {
    return (debtor == other.debtor && creditor == other.creditor) || 
        (creditor == other.creditor && debtor == other.debtor);
  }
  
  public Debt add(Debt other) { return this.combine(other); }
  
  Debt combine(Debt other)
  {
    if (!canBeCombined(other))
      throw new IllegalArgumentException("Two incompatible Debts can't be combined");
    
    if (debtor == other.debtor)
      return new Debt(debtor, creditor, amount.add(other.amount));
    else
      return new Debt(debtor, creditor, amount.subtract(other.amount));
  }
}
