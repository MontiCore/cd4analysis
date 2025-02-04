/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.decorators;

import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdgen.decorators.data.AbstractDecorator;
import de.monticore.cdgen.decorators.data.DecoratorData;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.visitor.IVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Extend {@link AbstractDecorator} for shared
 */
public interface IDecorator<D> extends IVisitor {

  /**
   * Add your decorator-visitor to the given traverser
   *
   * @param traverser the traverser
   */
  void addToTraverser(CD4CodeTraverser traverser);

  void init(DecoratorData util, Optional<GlobalExtensionManagement> glexOpt);

  /**
   * @return the list of decorators which MUST traverse the AST before
   */
  default List<Class<? extends IDecorator<?>>> getMustRunAfter() {
    return Collections.emptyList();
  }
}
