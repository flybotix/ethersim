package com.flybot.apps.motorsim;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import com.flybot.powertrain.DcMotor;
import com.flybot.sci.AngularVelocity;
import com.flybot.sci.Torque;

public class MotorGraphPanel implements ChangeListener<DcMotor>
{
  private enum ESeries {
    Cursor("Cursor", "N-m"),
    RotationSpeed("Rotation Speed", "RPM"),
    Efficiency("Efficiency", "%"),
    Power("Power Curve", "Watts"),
    Current("Current Draw", "A");
    
    private String mLabel;
    private String mUnits;
    private ESeries(String pLabel, String pUnits)
    {
      mLabel = pLabel;
      mUnits = pUnits;
    }
  };
  private DcMotor mMotor = null;
  private LineChart<Number, Number> mChart;
  private VBox mPane = new VBox();
  
  private Map<ESeries, Series<Number, Number>> mSeries = new HashMap<>();
  private Map<ESeries, ToggleButton> mToggles = new HashMap<>();
  private Map<ESeries, HBox> mLabelPanes = new HashMap<>();
  private Map<ESeries, DoubleProperty> mCursorValues = new HashMap<>();
  private Slider mCursorSlider = new Slider(0d, 5d, 2.5d);
  private DoubleProperty mStallTorque = new SimpleDoubleProperty(5d);
  private static final DecimalFormat sFORMAT = new DecimalFormat("0.000");
  
  /**
   * Constructs a motor graph panel.  Note that the chart isn't populated until
   * this class receives a change notification via the ChangeListener interface
   */
  public MotorGraphPanel()
  {
    NumberAxis xAxis = new NumberAxis();
    NumberAxis yAxis = new NumberAxis();
    mChart = new LineChart<>(xAxis, yAxis);
    mChart.setAnimated(false); // Is cool, except when changing the cursor
    
    for(int i = 0 ; i < ESeries.values().length; i++)
    {
      ESeries s = ESeries.values()[i];
      Series<Number, Number> series = new Series<>();
      series.setName(s.mLabel);
      ToggleButton b = new ToggleButton(s.mLabel);
      mSeries.put(s, series);
      mToggles.put(s, b);
      b.selectedProperty().addListener(e -> {
        if(b.isSelected())
        {
          mChart.getData().add(series);
        }
        else
        {
          mChart.getData().remove(series);
        }
      });
      b.selectedProperty().set(true);
      
      HBox labelbox = new HBox(2d);
      Label valuelabel = new Label("0.000");
      labelbox.getChildren().add(new Label(s.mLabel + ": "));
      labelbox.getChildren().add(valuelabel);
      labelbox.getChildren().add(new Label(s.mUnits));
      mCursorValues.put(s, new SimpleDoubleProperty());
      valuelabel.textProperty().bindBidirectional(mCursorValues.get(s), sFORMAT);
      mLabelPanes.put(s, labelbox);
    }
    
    mToggles.get(ESeries.Cursor).selectedProperty().addListener(e -> {
      boolean enabled = mToggles.get(ESeries.Cursor).selectedProperty().get();
      for(HBox box : mLabelPanes.values())
      {
        box.setDisable(!enabled);
      }
      mCursorSlider.setDisable(!enabled); // wish this was bindable...
    });
    
    yAxis.setMinorTickVisible(true);
    yAxis.setMinorTickCount(2);
    yAxis.autoRangingProperty().set(false);
    yAxis.tickUnitProperty().set(0.1);
    yAxis.upperBoundProperty().set(1.0);
    
    xAxis.setLabel("Stall Torque (N-m)");
    xAxis.autoRangingProperty().set(false);
    xAxis.setMinorTickCount(2);
    
    mChart.createSymbolsProperty().set(false);
    
    mCursorSlider.maxProperty().bind(mStallTorque);
    xAxis.upperBoundProperty().bind(mStallTorque);
    xAxis.tickUnitProperty().bind(mStallTorque.divide(10d));
    
    rebuildLayout();
    
    mCursorSlider.valueProperty().addListener(new SliderListener());
  }
  
  /**
   * @return the panel which contains everything
   */
  public Node getGraphic()
  {
    return mPane;
  }

  /**
   * Rebuild the chart after we receive a new motor to display
   */
  private void rebuildGraph()
  {
    int numTicks = mMotor.getCalculatedEfficiency().length;
    double maxFreeSpeed = mMotor.getFreeSpeed().si(); // Do not convert since it's for normalization only
    double maxCurrent = mMotor.getStallCurrent().si();
    double peakpower = mMotor.getPeakPower().si();
    
    for(Series<Number, Number> s : mSeries.values())
    {
      s.getData().clear();
    }
    
    for(int i = 0; i < numTicks; i++)
    {
      mSeries.get(ESeries.Current).getData().add(new Data<>(
          mMotor.getCalculatedTorques()[i].si(), 
          mMotor.getCalculatedCurrent()[i].si() / maxCurrent));
      mSeries.get(ESeries.Power).getData().add(new Data<>(
          mMotor.getCalculatedTorques()[i].si(),
          mMotor.getOutputPower()[i].si() / peakpower));
      mSeries.get(ESeries.Efficiency).getData().add(new Data<>(
          mMotor.getCalculatedTorques()[i].si(),
          mMotor.getCalculatedEfficiency()[i]));
      mSeries.get(ESeries.RotationSpeed).getData().add(new Data<>(
          mMotor.getCalculatedTorques()[i].si(),
          mMotor.getCalculatedAngularVelocities()[i].si() / maxFreeSpeed
          ));
    }
    mStallTorque.set(mMotor.getStallTorque().si());
  }
  
  /**
   * Simple method to contain where we actually put the components.  This
   * doesn't construct any components we keep a reference to
   */
  private void rebuildLayout()
  {
    mPane.getChildren().clear();
    
    HBox upper = new HBox(20d);
    HBox lower = new HBox(20d);
    upper.setAlignment(Pos.CENTER);
    lower.setAlignment(Pos.CENTER);
    for(int i = 0; i < ESeries.values().length; i++)
    {
      ESeries s = ESeries.values()[i];
      upper.getChildren().add(mToggles.get(s));
      lower.getChildren().add(mLabelPanes.get(s));
    }
    mPane.getChildren().add(upper);
    mPane.getChildren().add(mChart);
    mPane.getChildren().add(mCursorSlider);
    mPane.getChildren().add(lower);
  }

  @Override
  public void changed(ObservableValue<? extends DcMotor> pObservable,
      DcMotor pOldValue, DcMotor pNewValue)
  {
      mMotor = pNewValue;
      rebuildGraph();
  }
  
  /**
   * @author JesseK
   *  Interpolates the index of the cursor slider (i.e. torque) and then
   *  sets the values of all labels
   */
  private class SliderListener implements ChangeListener<Number>
  {
    @Override
    public void changed(ObservableValue<? extends Number> pObservable,
        Number pOldValue, Number pNewValue)
    {
      int i = indexOf(mMotor.getCalculatedTorques(), mCursorSlider.getValue());
      mCursorValues.get(ESeries.RotationSpeed).set(
          mMotor.getCalculatedAngularVelocities()[i].to(AngularVelocity.RPM));
      mCursorValues.get(ESeries.Efficiency).set(mMotor.getCalculatedEfficiency()[i] * 100);
      mCursorValues.get(ESeries.Power).set(mMotor.getOutputPower()[i].si());
      mCursorValues.get(ESeries.Current).set(mMotor.getCalculatedCurrent()[i].si());
      
      double interpTorque = mMotor.getCalculatedTorques()[i].si();
      mCursorValues.get(ESeries.Cursor).set(interpTorque);
      
      Series<Number, Number> cursorSeries = mSeries.get(ESeries.Cursor);
      cursorSeries.getData().clear();
      cursorSeries.getData().add(new Data<>(interpTorque, 0d));
      cursorSeries.getData().add(new Data<>(interpTorque, 1d));
    }
    
    private int indexOf(Torque[] pArray, double pValue)
    {
      int index = 0;
      while(index <= pArray.length && pArray[index].si() < pValue)
      {
        index++;
      }
      return index;
    }
  }
}
