package com.flybot.apps.ethersim;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.Region;

import com.flybot.apps.OmisThrownTogether;
import com.flybot.fxwidgets.graph.FXControllableLineGraph;
import com.flybot.fxwidgets.graph.FXControlledGraphPanel;

public class EtherSimGraph extends OmisThrownTogether
{
  private FXControlledGraphPanel<EResults> mChart;
  private DoubleProperty mMaxTime = new SimpleDoubleProperty(1d);
  private DoubleProperty mMaxValue = new SimpleDoubleProperty(0d);
  private final EtherSim mSim;
  
  public EtherSimGraph(EtherSim pSim)
  {
    mSim = pSim;
    mSim.addSimListener(()->recalculate());
    createChart();
  }
  
  private void createChart()
  {
    NumberAxis xAxis = new NumberAxis();
    NumberAxis yAxis = new NumberAxis();
    FXControllableLineGraph<Number, Number> graph = new FXControllableLineGraph<>(xAxis, yAxis);
    graph.setAnimated(false);
    graph.setCreateSymbols(false);
    
    mChart = new FXControlledGraphPanel<>(graph, 
        EnumSet.allOf(EResults.class).toArray(new EResults[0]), mMaxTime, mMaxValue);
    
    xAxis.setLabel("Time (s)");
  }

  /*package*/
  void recalculate()
  {
    
    List<EtherSimResult> results = mSim.mResults;
    System.out.println("Recalculated; setting up chart with " + results.size() + " results");
    
    mChart.updateDataSafely(series->{
      for(Series<Number, Number> s : series.values())
      {
        s.getData().clear();
      }
      
      mMaxTime.set(mSim.mInputs.get(EInputs.tstop).getValue().si());
      Iterator<EtherSimResult> it = results.iterator();
      it.next(); // weird result in the first spot...
      double max = Double.MIN_VALUE;
      while(it.hasNext())
      {
        EtherSimResult result = it.next();
        double time = result.mElapsedTime.si();
        for(EResults er : EResults.values())
        {
          max = Math.max(max, er.value(result));
          series.get(er).getData().add(new Data<>(
              time,
              er.value(result)
          ));
        }
      }
      mMaxValue.set(max);
    });
  }

  @Override
  public void init()
  {
    // TODO Auto-generated method stub
  }

  @Override
  protected String getAppName()
  {
    return "Ether Sim Chart";
  }

  @Override
  public void destroy()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Region getDisplay()
  {
    return mChart;
  }
}
