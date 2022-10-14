package de.monticore.coevolution.associationupdate;
import de.monticore.cddiff.syntaxdiff.CDSyntaxDiff;
import de.monticore.coevolution.changerules.AssociationChangeRules;
import de.monticore.coevolution.globalvariableupdate.GlobalLocalVariableAssociation;
import de.monticore.coevolution.globalvariableupdate.VariableTypeRefactoring;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.filter.AbstractFilter;
import java.util.Collections;
import java.util.List;

public class CardinalityUpdate {

  private final Launcher launcher;
  private final CtModel model;
  private final CDSyntaxDiff syntaxDiff;

  public CardinalityUpdate( Launcher launcher,  CtModel model, CDSyntaxDiff syntaxDiff){
    this.launcher = launcher;
    this.model = model;
    this.syntaxDiff = syntaxDiff;
  }

  public void updateCardinality(){

    AssociationChangeRules associationChangeRules = new AssociationChangeRules(syntaxDiff);
    String[][] rules = associationChangeRules.getAssociationChangeRules();

    try {
      for (String[] rule : rules) {
        if(rule[4] != null && rule[4].equals("isBidirection")){
          updateTypeAssociation(rule[0], rule[3], rule[1], rule[5]);
          updateTypeAssociation(rule[1], rule[2], rule[0], rule[5]);
        }

        if(rule[4] != null && rule[4].equals("isDefinitiveNavigableLeft")){
          updateTypeAssociation(rule[1], rule[2], rule[0], rule[5]);
        }

        if(rule[4] != null && rule[4].equals("isDefinitiveNavigableRight")){
          updateTypeAssociation(rule[0], rule[3], rule[1], rule[5]);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void updateTypeAssociation(String tClass, String tVariable, String tType, String Cardinality){

    CtClass<?> targetClass = (CtClass<?>) launcher.getFactory().Package().getRootPackage()
      .getElements(new AbstractFilter<>(CtClass.class) {
        @Override
        public boolean matches(CtClass matchedClass) {
          return matchedClass.getSimpleName().matches(tClass);
        }
      }).get(0);

    CtVariable<?> variableToBeUpdated = targetClass.getElements(new AbstractFilter<CtVariable<?>>() {
      @Override
      public boolean matches(CtVariable element) {
        return element.getSimpleName().matches(tVariable);
      }
    }).get(0);

    if(Cardinality.equals("isOne")){
      VariableTypeRefactoring rtr = new VariableTypeRefactoring(Collections.singleton(model.getRootPackage()));
      rtr.setTarget(variableToBeUpdated).setNewName(tType).refactor();
      GlobalLocalVariableAssociation globalLocalVariableAssociation =
        new GlobalLocalVariableAssociation(variableToBeUpdated,targetClass,tVariable,tType,model);
      globalLocalVariableAssociation.connectGlobalLocalVariables();

      multiToSingleUpdate(targetClass, variableToBeUpdated);
    }

    if(Cardinality.equals("isOpt")){
      VariableTypeRefactoring rtr = new VariableTypeRefactoring(Collections.singleton(model.getRootPackage()));
      rtr.setTarget(variableToBeUpdated).setNewName("Optional" + "<" + tType + ">").refactor();
      GlobalLocalVariableAssociation globalLocalVariableAssociation =
        new GlobalLocalVariableAssociation(variableToBeUpdated,targetClass,tVariable,
          "Optional" + "<" + tType + ">",model);
      globalLocalVariableAssociation.connectGlobalLocalVariables();

      multiToSingleUpdate(targetClass, variableToBeUpdated);
    }

    if(Cardinality.equals("isMult") || Cardinality.equals("isAtLeastOne")){
      VariableTypeRefactoring rtr = new VariableTypeRefactoring(Collections.singleton(model.getRootPackage()));
      rtr.setTarget(variableToBeUpdated).setNewName("Set" + "<" + tType + ">").refactor();
      GlobalLocalVariableAssociation globalLocalVariableAssociation =
        new GlobalLocalVariableAssociation(variableToBeUpdated,targetClass,tVariable,
          "Set" + "<" + tType + ">",model);
      globalLocalVariableAssociation.connectGlobalLocalVariables();


    }
  }


  public void multiToSingleUpdate(CtClass<?> targetClass, CtVariable<?> variableToBeUpdated){
    List<CtInvocation<?>> codeSnippetStatementsList = targetClass.getElements(new AbstractFilter<CtInvocation<?>>() {
      @Override
      public boolean matches(CtInvocation<?> element) {
        return element.toString().contains(variableToBeUpdated.getSimpleName() + "." + "add");
      }
    });

    CtExpression<?> firstItem = codeSnippetStatementsList.get(0).getArguments().get(0);
    CtCodeSnippetStatement tobeInserted = launcher.getFactory().Code().createCodeSnippetStatement(
      variableToBeUpdated.getSimpleName()  + "=" + firstItem);
    codeSnippetStatementsList.get(0).insertBefore(tobeInserted);

    for(CtInvocation<?> codeSnippet : codeSnippetStatementsList){
      codeSnippet.delete();
    }
  }









}
