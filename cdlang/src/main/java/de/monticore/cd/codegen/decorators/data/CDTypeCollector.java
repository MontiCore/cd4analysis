/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators.data;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;
import java.util.HashSet;
import java.util.Set;

/**
 * A visitor class that acts as a container to collect and hold data.
 * It ensures that only one instance of this class exists globally.
 */
public class CDTypeCollector implements CDBasisVisitor2, CDInterfaceAndEnumVisitor2 {
  
  protected final Set<ASTCDClass> classes = new HashSet<>();
  protected final Set<ASTCDInterface> interfaces = new HashSet<>();
  protected final Set<ASTCDEnum> enums = new HashSet<>();
  
  @Override
  public void visit(ASTCDClass node) {
    classes.add(node);
  }
  
  @Override
  public void visit(ASTCDInterface node) {
    interfaces.add(node);
  }
  
  @Override
  public void visit(ASTCDEnum node) {
    enums.add(node);
  }
  
  public Set<ASTCDClass> getClasses() { return classes; }
  
  public Set<ASTCDInterface> getInterfaces() { return interfaces; }
  
  public Set<ASTCDEnum> getEnums() { return enums; }
  
}
