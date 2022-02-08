/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.methods;

import de.monticore.cd.codegen.AbstractCreator;
import de.monticore.cd.codegen.AbstractService;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.List;

public class MethodDecorator extends AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> {

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

  public MethodDecorator(final GlobalExtensionManagement glex) {
    this(glex, new AccessorDecorator(glex), new MutatorDecorator(glex));
  }

  public MethodDecorator(final GlobalExtensionManagement glex,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator) {
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

  public AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> getAccessorDecorator() {
    return accessorDecorator;
  }

  public AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> getMutatorDecorator() {
    return mutatorDecorator;
  }
}
