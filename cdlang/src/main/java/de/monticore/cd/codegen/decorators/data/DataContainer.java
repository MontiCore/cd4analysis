package de.monticore.cd.codegen.decorators.data;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;
import java.util.HashSet;
import java.util.Set;

/**
 * A Singleton class that acts as a container to collect and hold data.
 * It ensures that only one instance of this class exists globally.
 */
public class DataContainer implements CDBasisVisitor2 {
  private final Set<ASTCDClass> classes = new HashSet<>();
  private final Set<ASTCDInterface> interfaces = new HashSet<>();
  private final Set<ASTCDEnum> enums = new HashSet<>();
  private static DataContainer INSTANCE;

  private DataContainer() {}

  public Set<ASTCDClass> getClasses() {
    return classes;
  }

  public Set<ASTCDInterface> getInterfaces() {
    return interfaces;
  }

  public Set<ASTCDEnum> getEnums() {
    return enums;
  }

  public static void setINSTANCE(DataContainer INSTANCE) {
    DataContainer.INSTANCE = INSTANCE;
  }

  public void init(ASTCDCompilationUnit ast) {
    CollectorVisitor visitor = new CollectorVisitor();
    CD4CodeTraverser t = CD4CodeMill.inheritanceTraverser();
    t.add4CDBasis(visitor);
    ast.accept(t);
  }

  /**
   * Provides the global point of access to the single DataContainer instance.
   * @return The single instance of DataContainer.
   */
  public static DataContainer getInstance() {
    if(INSTANCE==null){
      INSTANCE = new DataContainer();
    }
    return INSTANCE;
  }

  private class CollectorVisitor implements CDBasisVisitor2, CDInterfaceAndEnumVisitor2 {

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
  }
}
