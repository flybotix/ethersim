package com.flybot.powertrain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.flybot.IValue;
import com.flybot.sci.AngularVelocity;
import com.flybot.sci.Current;
import com.flybot.sci.Torque;
import com.flybot.sci.Voltage;


public class Motor implements IValue<EMotorProperty>
{
   private Map<EMotorProperty, Object> mValues = new HashMap<>();
   
   public static List<DcMotor> toDcMotors(List<Motor> pMotors)
   {
     List<DcMotor> results = new ArrayList<>();
     for(Motor m : pMotors)
     {
       results.add(m.toDcMotor());
     }
     return results;
   }
   
   public DcMotor toDcMotor()
   {
     Voltage v = new Voltage(value(EMotorProperty.VOLTAGE));
     AngularVelocity fs = new AngularVelocity(AngularVelocity.RPM, value(EMotorProperty.FREE_SPEED));
     Torque st = new Torque(Torque.NEWTON_METER, value(EMotorProperty.STALL_TORQUE));
     Current sc = new Current(value(EMotorProperty.STALL_CURRENT));
     Current fc = new Current(value(EMotorProperty.FREE_CURRENT));
     String name = get(EMotorProperty.NAME).toString();
     return new DcMotor(v, fs, st, sc, fc, name);
   }
   public Object get(EMotorProperty pKey)
   {
     return mValues.get(pKey);
   }
   public double value(EMotorProperty pProperty)
   {
     return Double.parseDouble(get(pProperty).toString());
   }
   
   private Motor()
   {
      // NO-OP
   }
   
   @Override
   public String toString()
   {
      return mValues.get(EMotorProperty.NAME).toString();
   }

   public static List<Motor> fromJson(JSONObject json)
   {
      List<Motor> results = new ArrayList<>();
      JSONArray array = (JSONArray) json.get("motors");
      Iterator it = array.iterator();
      while(it.hasNext())
      {
         JSONObject motorjson = (JSONObject)it.next();
         Motor motor = new Motor();
         for(EMotorProperty p : EMotorProperty.values())
         {
            Object value = motorjson.get(p.name().toLowerCase());
            motor.mValues.put(p, value);
         }
         results.add(motor);
      }
      return Collections.unmodifiableList(results);
   }
}
