package com.flybot.apps;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import snap.app.AppLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import com.flybot.apps.ethersim.EtherSimApp;
import com.flybot.apps.simplecalc.SimpleCalculator;
import com.flybot.util.log.ILog;
import com.flybot.util.log.LogArea;
import com.flybot.util.log.LogUtils;

public class ApplicationMain extends Application
{
  private static String[] sArgs;
  private static ILog log = LogUtils.createLog(ApplicationMain.class);

  public static void main(String[] pArgs) throws IOException, ParseException
  {
//    AppLoader updater = new AppLoader();
//    updater.AppDirName = "Frc Sims" + File.separator + "app"; // Should match Java8 runtime user install location
//    updater.JarName = "FrcSims.jar";
//    updater.JarURL =  "http://www.flybotix.com/files/sims/FrcSims.jar";
//    updater.MainClass = ApplicationMain.class.getCanonicalName();
//    updater.copyDefaultMainJar();
//    log.debug("Jar URL: " + updater.JarURL);
//    boolean updates = updater.checkForUpdates();
//    log.debug("Has updates: " + updates);
//    if(updates){updater.getUpdate();};

    sArgs = pArgs;
    launch(pArgs);
  }

  @Override
  public void start(Stage pStage) throws Exception
  {
    List<OmisThrownTogether> omis = new ArrayList<>();
    omis.add(new SimpleCalculator());
    omis.add(new EtherSimApp());

    TabPane stack = new TabPane();

    for(OmisThrownTogether omi : omis)
    {
      omi.initCommon();
      omi.init();
      try
      {
        String css = omi.getAppCss();
        if(css != null)
        {
          omi.getDisplay().getStylesheets().add(css);
        }
      } catch (Exception e){}
      Tab t = new Tab(omi.getAppName());
      t.setClosable(false);
      t.setContent(omi.getDisplay());
      stack.getTabs().add(t);
    }

    Tab t = new Tab("Log");
    t.setContent(new LogArea());
    t.setClosable(false);
    //    stack.getTabs().add(t); // TODO add logging once we debug the LogArea

    Scene scene = new Scene(stack);
    pStage.setScene(scene);
    pStage.setWidth(1600);
    pStage.setHeight(900);
    pStage.setTitle("FRC Simulators");
    pStage.show();
  }
}
