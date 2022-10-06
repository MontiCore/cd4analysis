/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.methods;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.List;

public class MethodDecorator extends AbstractMethodDecorator {

  protected final AbstractMethodDecorator accessorDecorator;

  protected final AbstractMethodDecorator mutatorDecorator;

  public MethodDecorator(final GlobalExtensionManagement glex) {
    this(glex, new AccessorDecorator(glex), new MutatorDecorator(glex));
  }

  public MethodDecorator(final GlobalExtensionManagement glex,
      final AbstractMethodDecorator accessorDecorator,
      final AbstractMethodDecorator mutatorDecorator) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.mutatorDecorator = mutatorDecorator;
  }

  @Override
  public void enableTemplates() {
    accessorDecorator.enableTemplates();
    mutatorDecorator.enableTemplates();
  }

  @Override
  public void disableTemplates() {
    accessorDecorator.disableTemplates();
    mutatorDecorator.disableTemplates();
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    List<ASTCDMethod> result = new ArrayList<>();
    result.addAll(accessorDecorator.decorate(ast));
    result.addAll(mutatorDecorator.decorate(ast));
    return result;
  }

  public AbstractMethodDecorator getAccessorDecorator() {
    return accessorDecorator;
  }

  public AbstractMethodDecorator getMutatorDecorator() {
    return mutatorDecorator;
  }
}
