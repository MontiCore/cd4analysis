/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.methodtemplates;

import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

public class CD4CTemplateMethodHelper {
  protected Optional<ASTCDMethodSignature> astcdMethod = Optional.empty();

  protected Optional<ASTCDAttribute> astcdAttribute = Optional.empty();
  /**
   * get the current method we are working on
   */
  public Optional<ASTCDMethodSignature> getMethod() {
    return astcdMethod;
  }

  /**
   * create a {@link de.monticore.cd4codebasis._ast.ASTCDMethod} from the signature
   *
   * @param methodSignature the (textual/syntactical) method signature
   */
  public void method(String methodSignature) {
    // if the signature has no semicolon, add one (needed because of the concrete syntax parser)
    if (!methodSignature.endsWith(";")) {
      methodSignature += ";";
    }

    try {
      this.astcdMethod = new CD4CodeParser()
          .parseCDMethod(new StringReader(methodSignature))
          .map(m -> m); // needed because we need Optional<ASTCDMethodSignature> and not Optional<ASTCDMethod>
    }
    catch (IOException e) {
      Log.error("0x12000: can't parse method signature '" + methodSignature + "': ", e);
    }
  }

  /**
   * create a {@link de.monticore.cd4codebasis._ast.ASTCDConstructor} from the signature
   *
   * @param constructorSignature the (textual/syntactical) constructor signature
   */
  public void constructor(String constructorSignature) {
    // if the signature has no semicolon, add one (needed because of the concrete syntax parser)
    if (!constructorSignature.endsWith(";")) {
      constructorSignature += ";";
    }
    try {
      this.astcdMethod = new CD4CodeParser()
          .parseCDConstructor(new StringReader(constructorSignature))
          .map(m -> m); // needed because we need Optional<ASTCDMethodSignature> and not Optional<ASTCDConstructor>
    }
    catch (IOException e) {
      Log.error("0x12001: can't parse constructor signature '" + constructorSignature + "': ", e);
    }
  }

  /**
   * create a {@link de.monticore.cd4codebasis._ast.ASTCDMethod} from the signature
   *
   * @param attributeSignature the (textual/syntactical) attribute
   */
  public void attribute(String attributeSignature) {
    // if the signature has no semicolon, add one (needed because of the concrete syntax parser)
    if (!attributeSignature.endsWith(";")) {
      attributeSignature += ";";
    }

    try {
      this.astcdAttribute = new CD4CodeParser()
              .parseCDAttribute(new StringReader(attributeSignature))
              .map(m -> m); // needed because we need Optional<ASTCDMethodSignature> and not Optional<ASTCDMethod>
    }
    catch (IOException e) {
      Log.error("0x12002: can't parse attribute '" + attributeSignature + "': ", e);
    }
  }

}
