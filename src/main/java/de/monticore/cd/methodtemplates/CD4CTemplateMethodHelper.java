// (c) https://github.com/MontiCore/monticore

package de.monticore.cd.methodtemplates;

import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

public class CD4CTemplateMethodHelper {
  protected Optional<ASTCDMethodSignature> astcdMethod = Optional.empty();

  public Optional<ASTCDMethodSignature> getMethod() {
    return astcdMethod;
  }

  public void method(String methodSignature) {
    if (!methodSignature.endsWith(";")) {
      methodSignature += ";";
    }

    try {
      this.astcdMethod = new CD4CodeParser()
          .parseCDMethod(new StringReader(methodSignature))
          .map(e -> e); // needed because we need Optional<ASTCDMethodSignature> and not Optional<ASTCDMethod>
    }
    catch (IOException e) {
      Log.error("11010: can't parse method signature '" + methodSignature + "': ", e);
      return;
    }

    check();
  }

  public void constructor(String constructorSignature) {
    if (!constructorSignature.endsWith(";")) {
      constructorSignature += ";";
    }
    try {
      this.astcdMethod = new CD4CodeParser()
          .parseCDConstructor(new StringReader(constructorSignature))
          .map(e -> e); // needed because we need Optional<ASTCDMethodSignature> and not Optional<ASTCDConstructor>
    }
    catch (IOException e) {
      Log.error("11011: can't parse constructor signature '" + constructorSignature + "': ", e);
      return;
    }

    check();
  }

  public boolean check() {
    // TODO: check
    //  - if parameter types are valid/exist

    return true;
  }
}
