/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.decorators;

import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdgen.decorators.data.AbstractDecorator;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.*;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class ObserverDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements CDBasisVisitor2 {

  @Override
  public void visit(ASTCDClass clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      ASTCDClass decClazz = decoratorData.getAsDecorated(clazz);

      //TODO implement Observer and Observable classes
      String pathToObserverPatternInterfaces ="test";
      //add an interface list if not present in the clazz
      if(!decClazz.isPresentCDInterfaceUsage()){
        decClazz.setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().build());
      }
      //add the observable interfaces to the class
      ASTMCQualifiedType observerInterface = MCTypeFacade.getInstance().createQualifiedType(pathToObserverPatternInterfaces + ".Observer");
      ASTMCQualifiedType observableInterface = MCTypeFacade.getInstance().createQualifiedType(pathToObserverPatternInterfaces + ".Observable");
      decClazz.getCDInterfaceUsage().addInterface(observableInterface);

      // add an import statement for the Observer interface
      CD4C.getInstance().addImport(decClazz, pathToObserverPatternInterfaces +".Observer");

      //add the observable interfaces methods to the class
      ASTCDParameter observerParameter = CD4CodeMill.cDParameterBuilder().setName("observer").setMCType(observerInterface).build();
      // addObserver
      ASTCDMethod addObserver = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(),"addObserver",observerParameter);
      addToClass(decClazz, addObserver);
      // removeObserver
      ASTCDMethod removeObserver = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(),"removeObserver",observerParameter);
      addToClass(decClazz, removeObserver);
      // notifyObservers
      ASTCDMethod notifyObservers = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), "notifyObservers");
      addToClass(decClazz, notifyObservers);

      //TODO check if needed
      // getUpdatedData
      ASTCDMethod getUpdatedData = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), MCTypeFacade.getInstance().createQualifiedType("java.lang.Object"),"getUpdatedDate");
      addToClass(decClazz, getUpdatedData);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, getUpdatedData, new StringHookPoint("return null;")));
    }
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
}
