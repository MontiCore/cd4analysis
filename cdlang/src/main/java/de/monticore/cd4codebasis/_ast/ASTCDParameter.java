/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis._ast;

import java.util.Optional;

public class ASTCDParameter extends ASTCDParameterTOP {

  private Optional<String> annotation = Optional.empty();

  public Optional<String> getAnnotation() {
    return annotation;
  }

  public void setAnnotation(Optional<String> annotation) {
    this.annotation = annotation;
  }
}
