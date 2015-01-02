package com.flybot.apps;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.flybot.IValue;
import com.flybot.apps.common.ITrigger;
import com.flybot.powertrain.Motor;
import com.flybot.powertrain.Wheel;
import com.flybot.util.lang.IDisplayable;
import com.flybot.util.log.ILog;
import com.flybot.util.log.LogUtils;

public abstract class OmisThrownTogether implements IApp
{
  protected static final ObservableList<Motor> sMotors;
  protected static final ObservableList<Wheel> sWheels;
  private static final ILog log = LogUtils.createLog(OmisThrownTogether.class);
  
  static
  {
    List<Motor> motors = new ArrayList<>();
    List<Wheel> wheels = new ArrayList<>();
    try
    {
      motors.addAll(parse(p->Motor.fromJson(p), "motors.json"));
      wheels.addAll(parse(p->Wheel.fromJson(p), "wheels.json"));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    sMotors = FXCollections.observableArrayList(motors);
    sWheels = FXCollections.observableArrayList(wheels);
    
    log.info("Have " + sMotors.size() + " motors");
  }
  
  protected void initCommon()
  {
    // Dev note - do not call any abstract or child-implemented
    // classes inside this method
  }
  
  protected abstract String getAppName();
  
  protected String getAppCss()
  {
    String css = OmisThrownTogether.class.getResource("sandbox.css").toExternalForm();
    return css;
  }
  
  /*
   * Static section
   */
  protected static <E, C extends Control> void addListenerToAll(Map<E, C> pMap, ITrigger pListener)
  {
    for(Control field : pMap.values())
    {
      if(field instanceof TextField)
      {
        ((TextField)field).textProperty().addListener((c1, c2, c3)->pListener.trigger());            
      }
      else if (field instanceof ComboBox)
      {
        ((ComboBox)field).setOnAction(e->pListener.trigger());
      }
    }
  }

  /**
   * Creates a combo box which, upon change, will set all of the fields in the input map
   * to the values from the TYPE
   * @param pList
   * @param pEnums
   * @param pMap
   * @return
   */
  protected static <TYPE extends IValue<KEY>, KEY extends IDisplayable> 
  ComboBox<TYPE> createComboBox(ObservableList<TYPE> pList, 
      Set<KEY> pEnums, Map<KEY, Control> pMap)
  {
    ComboBox<TYPE> result = new ComboBox<>(pList);
    result.setOnAction(e->{
      TYPE obj = result.getSelectionModel().getSelectedItem();
      for(KEY key : pEnums)
      {
        setSafely(key, obj.get(key), pMap);
      }
    });
    result.getSelectionModel().select(0);
    return result;
  }

  /**
   * Gets a value from a field.  if the value cannot be parsed into a
   * double since the field is empty or has invalid input, then
   * Double.NaN is returned.  This should quickly indicate to the
   * user that an error has occurred in the field that was just changed
   * @param pKey
   * @param pMap
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected static <KEY, CTRL extends Control> 
  double getSafely(KEY pKey, Map<KEY, CTRL> pMap)
  {
    double result = Double.NaN;
    try
    {
      CTRL ctrl = pMap.get(pKey);
      if(ctrl instanceof TextField)
      {
        result = Double.parseDouble(((TextField)ctrl).getText());
      }
      else if(ctrl instanceof ComboBox)
      {
        Object o = ((ComboBox)ctrl).getSelectionModel().getSelectedItem();;
        if(o != null) result = (Double)o;
      }
      else if(ctrl instanceof Label)
      {
        result = Double.parseDouble(((Label)ctrl).getText());
      }
    } catch (NumberFormatException nfe)
    {
      // swallow it, it's from invalid input the NaN should be an indicator
    }
    return result;
  }

  @SuppressWarnings("rawtypes")
  protected static <E extends IDisplayable> 
  void setSafely(E pEnum, Object pValue, Map<E, Control> pMap)
  {
    Control c = pMap.get(pEnum);
    if(c instanceof TextField)
    {
      ((TextField)c).setText(pValue.toString());
    }
    else if (c instanceof ComboBox && pValue instanceof ObservableList)
    {
      ComboBox box = (ComboBox)c;
      box.setItems((ObservableList<Double>)pValue);
      box.getSelectionModel().select(0);
    } else if(c instanceof Label)
    {
      ((Label)c).setText(pValue.toString());
    }
  }
  
  protected static <E> ObservableList<E> parse(IJSONParser<E> pParser, String pFileName)
  {
    ObservableList<E> results = FXCollections.observableArrayList();
    try
    {
      URL is = OmisThrownTogether.class.getResource(pFileName);
      if(is != null)
      {
         JSONObject json = (JSONObject)new JSONParser().parse(new InputStreamReader(is.openStream()));
         List<E> objects = pParser.parse(json);
         if(objects != null && objects.isEmpty() == false)
         {
           results.addAll(objects);
           System.out.println("Have " + objects.size() + " objects from " + pFileName);
         }
      }
      else
      {
         System.err.println("Could not find resource " + pFileName);
      }
    }
    catch (IOException | ParseException pException)
    {
      pException.printStackTrace();
    }
    return results;
  }

  protected static Label createUnitsLabel(String pUnits)
  {
    String result = "";
    if(pUnits != null && pUnits.isEmpty() == false)
    {
      result += " (" + pUnits + ")";
    }
    Label l = new Label(result);
    return l;
  }
  
  protected static <KEY extends IDisplayable> 
  GridPane createSimpleGrid(Set<KEY> pSet, Map<KEY, Control> pMap, 
      IControl<KEY> pControlFactory, IUnits<KEY> pUnitFactory)
  {
    GridPane result = new GridPane();
    Iterator<KEY> it = pSet.iterator();
    int rowIndex = 0;
    while(it.hasNext())
    {
      KEY k = it.next();
      if(k.isDisplayed())
      {
        pMap.put(k, pControlFactory.getControl(k));
        Label l = new Label(k.toString() + ": ");
        l.getStyleClass().add("field-label");
        result.add(l, 0, rowIndex);
        result.add(pMap.get(k), 1, rowIndex);
        result.add(createUnitsLabel(pUnitFactory.getUnits(k)), 2, rowIndex);
        rowIndex++;
      }
    }
    return result;
  }

  /*
   * Interfaces which let us use Lambdas
   */
  protected interface IJSONParser<E> { List<E> parse(JSONObject pParser); }
  protected interface IControl<KEY>  { public Control getControl(KEY pKey); }
  protected interface IUnits<KEY> { public String getUnits(KEY pKey); }
}
