package com.flybot.apps.motorsim;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class GearboxDesignPanel extends HBox
{
  private static final double sDPI = 48d;
  
  private static final DecimalFormat sDISTFORMAT = new DecimalFormat("0.000");
  private static final DecimalFormat sNUMTEETHFORMAT = new DecimalFormat("00");
  private static final DecimalFormat sRATIOFORMAT = new DecimalFormat("0.00000");
  private static final DecimalFormat sVISRATIOFORMAT = new DecimalFormat("0.0");

  private ObservableList<GearsetFullPanel> mPanels = FXCollections
      .observableArrayList();
  
  private final StatPanel mStatPanel = new StatPanel(mPanels);

  public GearboxDesignPanel()
  {
    mPanels.addListener(new ListChangeListener<GearsetFullPanel>()
    {
      @SuppressWarnings("rawtypes")
      public void onChanged(ListChangeListener.Change change)
      {
        rebuild();
      }
    });
    setSpacing(sDPI/2);
    setPadding(new Insets(sDPI/2));
    createStageButtonPanel(null);
  }

  private void rebuild()
  {
    Iterator<GearsetFullPanel> panels = mPanels.iterator();
    getChildren().clear();
    getChildren().add(mStatPanel);
    int index = 0;
    while(panels.hasNext())
    {
      GearsetFullPanel panel = panels.next();
      if(index == 0){

        panel.removeButton.setDisable(true);
      }
      panel.setStage(index);
      index++;
      getChildren().add(panel);
    }
  }

  private Node createStageButtonPanel(Node pCreateAfterNode)
  {
    GearsetFullPanel result = new GearsetFullPanel(pCreateAfterNode);
    int index = (mPanels.isEmpty() ? 0 : mPanels.indexOf(pCreateAfterNode) + 1);
    if(mPanels.isEmpty())
    {
      mPanels.add(result);
    }
    else
    {
      mPanels.add(index, result);
    }
    return result;
  }
  
  private class StatPanel extends VBox
  {
    private Label mTotalRatio = new Label("1");
    private Label mVisibleRatio = new Label("1:1");
    private final List<GearsetFullPanel> mGearPanels;
    ChangeListener<? super Number> listener = (c1,c2,c3)->{recalculate();};
    
    private StatPanel(ObservableList<GearsetFullPanel> pPanels)
    {
      mGearPanels = pPanels;
      pPanels.addListener(new ListChangeListener<GearsetFullPanel>()
          {
        @SuppressWarnings("rawtypes")
        public void onChanged(ListChangeListener.Change change)
        {
          reconcileListeners();
        }
      });
      
      setSpacing(20);
      getChildren().add(mTotalRatio);
      getChildren().add(mVisibleRatio);
    }
    
    private void reconcileListeners()
    {
      for(GearsetFullPanel panel : mGearPanels)
      {
        panel.mGearsetPanel.mGears.get(EGear.DRIVING).mNumTeeth.removeListener(listener);
        panel.mGearsetPanel.mGears.get(EGear.DRIVEN).mNumTeeth.removeListener(listener);
        panel.mGearsetPanel.mGears.get(EGear.DRIVING).mNumTeeth.addListener(listener);
        panel.mGearsetPanel.mGears.get(EGear.DRIVEN).mNumTeeth.addListener(listener);
      }
    }
    
    private void recalculate()
    {
      double ratio = 1d;
      for(GearsetFullPanel panel : mGearPanels)
      {
        ratio *= panel.mGearsetPanel.mGears.get(EGear.DRIVING).mNumTeeth.get();
        ratio /= panel.mGearsetPanel.mGears.get(EGear.DRIVEN).mNumTeeth.get();
      }
      mTotalRatio.setText(sRATIOFORMAT.format(ratio));
      mVisibleRatio.setText(sVISRATIOFORMAT.format(1/ratio) + " : 1");
    }
  }
  
  private class GearsetFullPanel extends VBox
  {
    private Label mStageLabel = new Label("Stage ");
    Button removeButton = new Button("Remove Stage");
    Button addButton = new Button("Add Stage");
    GearsetPanel mGearsetPanel = new GearsetPanel();
    private GearsetFullPanel(Node pCreateAfterNode)
    {
      addButton.setOnAction(e -> createStageButtonPanel(this));
      removeButton.setOnAction(e -> mPanels.remove(this));
      HBox buttons = new HBox();
      buttons.setSpacing(20d);
      buttons.setAlignment(Pos.CENTER);;
      buttons.getChildren().add(removeButton);
      buttons.getChildren().add(addButton);
      
      getChildren().add(buttons);
      getChildren().add(mStageLabel);
      getChildren().add(mGearsetPanel);
    }
    
    private void setStage(int pStage)
    {
      mStageLabel.setText("Stage " + pStage);
    }
  }
  
  private enum EGear { DRIVING, DRIVEN }
  
  private class GearsetPanel extends VBox
  {
    private Map<EGear, GearSubPanel> mGears = new HashMap<>();
    
    private GearsetPanel()
    {
      GearSubPanel driving = new GearSubPanel();
      GearSubPanel driven = new GearSubPanel();
      mGears.put(EGear.DRIVING, driving);
      mGears.put(EGear.DRIVEN, driven);
      
      VBox circles = new VBox(driving.mCircle, driven.mCircle);
      double w = driving.mSlider.getMax() / driving.mDiametricPitch.get() * sDPI;
      circles.setPrefSize(w, 2*w);
      circles.setAlignment(Pos.CENTER);
      
      getChildren().add(driving.mLabel);
      getChildren().add(driving.mDiameter);
      getChildren().add(driving.mSliderBox);
      getChildren().add(circles);
      getChildren().add(driven.mSliderBox);
      getChildren().add(driven.mDiameter);
      getChildren().add(driven.mLabel);
    }
  }
  
  private class GearSubPanel 
  {
    private DoubleProperty mDiametricPitch = new SimpleDoubleProperty(20);
    private DoubleProperty mNumTeeth = new SimpleDoubleProperty(50);
    private Circle mCircle = new Circle();
    private Slider mSlider = new Slider(2, 100, 50);
    private Label mLabel = new Label();
    private Label mDiameter = new Label();
    private Button mIncr = new Button("+");
    private Button mDecr = new Button("-");
    private HBox mSliderBox = new HBox(25d, mDecr, mSlider, mIncr);
    
    private GearSubPanel()
    {
      mSlider.setSnapToTicks(true);
      mSlider.setMajorTickUnit(1d);
      mSliderBox.setAlignment(Pos.CENTER);
      mNumTeeth.bind(mSlider.valueProperty());
      mCircle.radiusProperty().bind(mNumTeeth.divide(mDiametricPitch).divide(2d/sDPI));

      mNumTeeth.addListener(e->{
        mLabel.setText("# of Teeth: " + sNUMTEETHFORMAT.format(mNumTeeth.get()));
        mDiameter.setText("Diameter: " + 
            sDISTFORMAT.format(mNumTeeth.get() / mDiametricPitch.get()) + "\"");
      });
      
      mIncr.setOnAction(e->mSlider.setValue(mSlider.getValue()+1));
      mDecr.setOnAction(e->mSlider.setValue(mSlider.getValue()-1));
      
      mSlider.setValue(25); // So the text populates
    }
    
  }
}
