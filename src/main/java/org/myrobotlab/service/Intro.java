package org.myrobotlab.service;

import org.myrobotlab.framework.Service;
import org.myrobotlab.framework.ServiceType;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.LoggingFactory;
import org.slf4j.Logger;

public class Intro extends Service {

  private static final long serialVersionUID = 1L;

  public final static Logger log = LoggerFactory.getLogger(Intro.class);

  public Intro(String n, String id) {
    super(n, id);
    try {
      Runtime runtime = Runtime.getInstance();
      Repo repo = runtime.getRepo();
      TutorialInfo tuto = new TutorialInfo();
      tuto.id = "servo-hardware";
      tuto.title = "Servo with Arduino Hardware";
      tuto.servicesRequired = new String[] { "Servo", "Arduino" };
      tuto.isInstalled = repo.isInstalled(Servo.class) && repo.isInstalled(Arduino.class);

      tuto.script = getResourceAsString(Servo.class, "Servo.py"); //FileIO.toString(getResource(Servo.class,"Servo.py"));
      tutorials.put(tuto.title, tuto);
    } catch (Exception e) {
      log.error("Intro constructor threw", e);
    }
  }

  /**
   * This static method returns all the details of the class without it having
   * to be constructed. It has description, categories, dependencies, and peer
   * definitions.
   * 
   * @return ServiceType - returns all the data
   * 
   */
  static public ServiceType getMetaData() {

    ServiceType meta = new ServiceType(Intro.class);
    meta.addDescription("Introduction to MyRobotlab");
    meta.setAvailable(true); 
    meta.addCategory("general");
    return meta;
  }

  public static void main(String[] args) {
    try {

      LoggingFactory.init(Level.INFO);

      Runtime.start("intro", "Intro");
      Runtime.start("servo", "Servo");
      Runtime.start("gui", "SwingGui");

    } catch (Exception e) {
      log.error("main threw", e);
    }
  }
}
