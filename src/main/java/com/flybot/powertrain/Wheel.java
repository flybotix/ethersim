package com.flybot.powertrain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.flybot.IValue;

public class Wheel implements IValue<EWheelProperty>
{
   private Map<EWheelProperty, Object> mValues = new HashMap<>();
   
   private Wheel()
   {
      // NO-OP
   }
   
   @Override
   public String toString()
   {
      return mValues.get(EWheelProperty.TYPE).toString();
   }
   
   public Object get(EWheelProperty pProperty)
   {
      return mValues.get(pProperty);
   }

   public static List<Wheel> fromJson(JSONObject json)
   {
      List<Wheel> results = new ArrayList<>();
      JSONArray array = (JSONArray) json.get("wheels");
      Iterator it = array.iterator();
      while(it.hasNext())
      {
         JSONObject wheeljson = (JSONObject)it.next();
         Wheel wheel = new Wheel();
         for(EWheelProperty p : EWheelProperty.values())
         {
            Object value = wheeljson.get(p.name().toLowerCase());
            if(value instanceof JSONArray)
            {
               JSONArray arr = (JSONArray)value;
               List<Double> list = new ArrayList<>();
               for(int i = 0 ; i < arr.size(); i++)
               {
                  if(arr.get(i) instanceof Double)
                  {
                     list.add((Double)arr.get(i));
                  }
                  else if (arr.get(i) instanceof Long)
                  {
                     list.add(Double.parseDouble(Long.toString((Long)arr.get(i))));
                  }
               }
               wheel.mValues.put(p, 
                     FXCollections.observableArrayList(list));
            }
            else
            {
               wheel.mValues.put(p, value);               
            }
         }
         results.add(wheel);
      }
      return Collections.unmodifiableList(results);
   }
   
   
}
