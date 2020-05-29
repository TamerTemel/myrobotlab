package org.myrobotlab.service.meta;

import org.myrobotlab.framework.Platform;
import org.myrobotlab.framework.ServiceType;
import org.myrobotlab.logging.LoggerFactory;
import org.slf4j.Logger;

public class BodyPartMeta {
  public final static Logger log = LoggerFactory.getLogger(BodyPartMeta.class);
  
  /**
   * This static method returns all the details of the class without it having
   * to be constructed. It has description, categories, dependencies, and peer
   * definitions.
   * 
   * @return ServiceType - returns all the data
   * 
   */
  static public ServiceType getMetaData() {

    ServiceType meta = new ServiceType("org.myrobotlab.service.BodyPart");
    Platform platform = Platform.getLocalInstance();
    
    meta.addDescription("An easier way to control a body ...");
    meta.addCategory("robot");
    meta.setAvailable(true);
    meta.addDependency("org.apache.commons", "commons-lang3", "3.3.2");
    return meta;
  }

  
  
}
