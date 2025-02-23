/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.types.MCTypeFacade;


public class ObserverDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements CDBasisVisitor2 {

  @Override
  public void visit(ASTCDClass clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      var origParent = this.decoratorData.getParent(clazz).get();
      var decParent = this.decoratorData.getAsDecorated(origParent);

      var observerInterface = CD4CodeMill.cDInterfaceBuilder()
        .setName("I" + clazz.getName() + "Observer")
        .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build())
        .build();

      addElementToParent(decParent, observerInterface);

      var decClass = this.decoratorData.getAsDecorated(clazz);


      decClass.addCDMember(CDAttributeFacade.getInstance().createAttribute(CD4CodeMill.modifierBuilder().PROTECTED().build(),
        MCTypeFacade.getInstance().createListTypeOf(observerInterface.getName()), "_observers"));



    }
  }


  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
}
