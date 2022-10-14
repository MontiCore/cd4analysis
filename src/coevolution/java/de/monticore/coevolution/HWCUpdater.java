package de.monticore.coevolution;

import de.monticore.cddiff.syntaxdiff.CDSyntaxDiff;
import de.monticore.coevolution.associationupdate.CardinalityUpdate;
import de.monticore.coevolution.changerules.MethodReturnTypeChangeRules;
import de.monticore.coevolution.changerules.VariableTypeChangeRules;
import de.monticore.coevolution.globalvariableupdate.GlobalLocalVariableAssociation;
import de.monticore.coevolution.globalvariableupdate.VariableTypeRefactoring;
import de.monticore.coevolution.methodupdate.MethodReturnTypRefactoring;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class HWCUpdater {
  private final CDSyntaxDiff syntaxDiff;
  private final String pathInput;
  private final String pathOutput;

  public HWCUpdater(CDSyntaxDiff syntaxDiff, String pathInput, String pathOutput){
    this.syntaxDiff = syntaxDiff;
    this.pathInput = pathInput;
    this.pathOutput = pathOutput;
  }

  public void hwcupdater(){

    Launcher launcher = new Launcher();
    launcher.getEnvironment().setAutoImports(true);
    System.out.println(new File(pathInput).getAbsolutePath());
    launcher.addInputResource(pathInput);
    CtModel model = launcher.buildModel();

    /* get the new variable type stored in an array, e.g [Car, productionYear, String] means the variable productionYear in Class Car
    should be updated as String.*/
    VariableTypeChangeRules variableTypeChangeRules = new VariableTypeChangeRules(syntaxDiff);
    String[][] changeRulesVariableType = variableTypeChangeRules.getVariableTypeChangeRules();

    try {
      for (String[] rule : changeRulesVariableType) {
        if(rule[0] != null && rule[1] != null && rule[2] != null){
          //get the target class.
          CtClass<?> targetClass = (CtClass<?>) launcher.getFactory().Package().getRootPackage()
            .getElements(new AbstractFilter<>(CtClass.class) {
              @Override
              public boolean matches(CtClass matchedClass) {
                return matchedClass.getSimpleName().matches(rule[0]);
              }
            }).get(0);
          //get the target variable.
          CtVariable<?> variableToBeUpdated = targetClass.getElements(new AbstractFilter<CtVariable<?>>() {
            @Override
            public boolean matches(CtVariable element) {
              return element.getSimpleName().matches(rule[1]);
            }
          }).get(0);

          // updated the target variable in target class.
          VariableTypeRefactoring rtr = new VariableTypeRefactoring(Collections.singleton(model.getRootPackage()));
          rtr.setTarget(variableToBeUpdated).setNewName(rule[2]).refactor();
          //find the local parameter associated with the global variable.
          GlobalLocalVariableAssociation globalLocalVariableAssociation =
            new GlobalLocalVariableAssociation(variableToBeUpdated,targetClass,rule[1],rule[2],model);
          globalLocalVariableAssociation.connectGlobalLocalVariables();

        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    // get all method that the return type should be updated.
    MethodReturnTypeChangeRules methodReturnTypeChangeRules = new MethodReturnTypeChangeRules(syntaxDiff);
    String[][] changeRulesMethodReturnType = methodReturnTypeChangeRules.getMethodReturnTypeChangeRules();

    try {
      for (String[] rule : changeRulesMethodReturnType) {
        if(rule[0] != null && rule[1] != null && rule[2] != null){
          CtClass<?> targetClass = (CtClass<?>) launcher.getFactory().Package().getRootPackage()
            .getElements(new AbstractFilter<>(CtClass.class) {
              @Override
              public boolean matches(CtClass matchedClass) {
                return matchedClass.getSimpleName().matches(rule[0]);
              }
            }).get(0);

          CtMethod<?> methodToBeUpdated = targetClass.getElements(new AbstractFilter<CtMethod<?>>() {
            @Override
            public boolean matches(CtMethod element) {

              CtClass<?> cl = element.getParent(CtClass.class);
              return cl.getSimpleName().matches(rule[0]) &&
                element.getSimpleName().matches(rule[1]);
            }
          }).get(0);

          MethodReturnTypRefactoring rtr = new MethodReturnTypRefactoring(Collections.singleton(model.getRootPackage()), targetClass);
          rtr.setTarget(methodToBeUpdated).setNewName(rule[2]).refactor();
        }
      }
    }catch (Exception e){
      e.printStackTrace();
    }

    CardinalityUpdate cardinalityUpdate  = new CardinalityUpdate(launcher,model,syntaxDiff);
    cardinalityUpdate.updateCardinality();


    //output the updated classes.
    for(CtPackage pack : model.getAllPackages()){
      for(int i = 0; i< pack.getElements(new TypeFilter<>(CtClass.class)).size(); i++) {
        CtClass<?> ctClass = pack.getElements(new TypeFilter<>(CtClass.class)).get(i);
        String contentOfUpdatedClass = ctClass.toString();
        String filename = "/"+ctClass.getSimpleName()+".java";
        UpdatedClassWriter updatedMethodTypeClassWriter = new UpdatedClassWriter(contentOfUpdatedClass,
          pathOutput, filename);
        try {
          updatedMethodTypeClassWriter.createUpdatedClassFile();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
}
