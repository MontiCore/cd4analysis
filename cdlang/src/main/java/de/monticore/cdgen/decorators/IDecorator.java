/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.decorators;

import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.visitor.IVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public interface IDecorator<D> extends IVisitor {




  void addToTraverser(CD4CodeTraverser traverser);
  void init(DecoratorData util, Optional<GlobalExtensionManagement> glexOpt);

  default List<Class<? extends IDecorator<?>>> getMustRunAfter() {
    return Collections.emptyList();
  }
}
