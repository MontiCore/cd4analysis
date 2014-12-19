/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos._tobegenerated;

import java.util.Collection;

import com.google.common.collect.Sets;

import de.monticore.cocos.ContextConditionProfile;

/**
 * TODO: generate this file
 *
 * @author Robert Heim
 */
// TODO RH remove all methods from ContextConditionProfile that enable adding
// CoCos which are not AST-Node specific, because only the add-methods in this
// class here are valid.
public class CD4ACoCoProfile extends ContextConditionProfile {
  
  Collection<CD4AClassCoCo> astCDClassCocos = Sets.newHashSet();
  
  Collection<CD4AAttributeCoCo> astCDAttributeCocos = Sets.newHashSet();
  
  public void addCoCo(CD4AClassCoCo coco) {
    astCDClassCocos.add(coco);
  }
  
  public void addCoCo(CD4AAttributeCoCo coco) {
    astCDAttributeCocos.add(coco);
  }
  
  public Collection<CD4AClassCoCo> getAstCDClassCocos() {
    return this.astCDClassCocos;
  }
  
  public Collection<CD4AAttributeCoCo> getAstCDAttributeCocos() {
    return this.astCDAttributeCocos;
  }
}
