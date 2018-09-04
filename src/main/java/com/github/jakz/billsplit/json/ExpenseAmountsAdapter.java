package com.github.jakz.billsplit.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.jakz.billsplit.Amount;
import com.github.jakz.billsplit.Environment;
import com.github.jakz.billsplit.ExpenseAmounts;
import com.github.jakz.billsplit.Person;
import com.github.jakz.billsplit.Share;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.pixbits.lib.json.JsonAdapter;

public class ExpenseAmountsAdapter implements JsonAdapter<ExpenseAmounts>
{
  private final Environment env;
  
  Share<Amount> deserializeShare(JsonArray array)
  {
    Amount amount = Amount.of(array.get(0).getAsString());
    Person person = env.group.forName(array.get(1).getAsString());
    
    return new Share<>(person, amount);
  }
  
  public ExpenseAmountsAdapter(Environment env)
  {
    this.env = env;
  }

  @Override
  public JsonElement serialize(ExpenseAmounts src, Type typeOfSrc, JsonSerializationContext context) 
  {
    return null;
  }

  @Override
  public ExpenseAmounts deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    List<Share<Amount>> shares;
    
    JsonArray jamounts = json.getAsJsonArray();
    
    /* multiple amounts */
    if (jamounts.get(0).isJsonArray())
    {
      JsonArray innerAmounts = jamounts.get(0).getAsJsonArray();
      shares = new ArrayList<>();
      
      for (int i = 0; i < innerAmounts.size(); ++i)
        shares.add(deserializeShare(innerAmounts.get(i).getAsJsonArray()));
    }
    else
      shares = Collections.singletonList(deserializeShare(jamounts));
    
    return new ExpenseAmounts(shares);
  }
}
