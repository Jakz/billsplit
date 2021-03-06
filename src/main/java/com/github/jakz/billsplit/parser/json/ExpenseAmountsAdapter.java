package com.github.jakz.billsplit.parser.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.jakz.billsplit.Environment;
import com.github.jakz.billsplit.ExpenseAmounts;
import com.github.jakz.billsplit.Share;
import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Person;
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
    Person person = env.person(array.get(1).getAsString());
    
    if (person == null)
      throw new JsonParseException("Unknown person: "+array.get(1).getAsString());
    
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
      shares = new ArrayList<>();
      
      for (int i = 0; i < jamounts.size(); ++i)
        shares.add(deserializeShare(jamounts.get(i).getAsJsonArray()));
    }
    else
      shares = Collections.singletonList(deserializeShare(jamounts));
    
    return new ExpenseAmounts(shares);
  }
}
