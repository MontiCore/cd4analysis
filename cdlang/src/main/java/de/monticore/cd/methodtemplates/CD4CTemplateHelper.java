/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.methodtemplates;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import java.io.StringReader;
import java.util.Optional;

public class CD4CTemplateHelper {
  
  protected Optional<ASTCDMethodSignature> astcdMethod = Optional.empty();
  
  protected Optional<ASTCDAttribute> astcdAttribute = Optional.empty();
  
  protected Optional<ASTMCImportStatement> astcdImport = Optional.empty();
  
  /** get the current method we are working on */
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
    
    this.astcdMethod = CD4CodeMill.parser()
        .parseCDMethod(new StringReader(methodSignature)).map(
            m -> m); // needed because we need Optional<ASTCDMethodSignature> and not
    // Optional<ASTCDMethod>
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
    this.astcdMethod = CD4CodeMill.parser().parseCDConstructor(new StringReader(
        constructorSignature)).map(m -> m); // needed because we need Optional<ASTCDMethodSignature> and not
    // Optional<ASTCDConstructor>
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
    
    this.astcdAttribute = CD4CodeMill.parser().parseCDAttribute(new StringReader(
        attributeSignature)).map(m -> m); // needed because we need Optional<ASTCDMethodSignature> and not
    // Optional<ASTCDMethod>
  }
  
  /**
   * create a {@link de.monticore.cdbasis._ast.ASTCDTargetImportStatement} from the signature
   *
   * @param importSignature the (textual/syntactical) targetimport
   */
  public void importStr(String importSignature) {
    // if the signature has no semicolon, add one (needed because of the concrete syntax parser)
    if (!importSignature.startsWith("import ")) {
      importSignature = "import " + importSignature;
    }
    
    if (!importSignature.endsWith(";")) {
      importSignature += ";";
    }
    
    this.astcdImport = CD4CodeMill.parser().parseMCImportStatement(new StringReader(
        importSignature)).map(m -> m); // needed because we need Optional<ASTCDMethodSignature> and not
    // Optional<ASTCDMethod>
  }
  
}
