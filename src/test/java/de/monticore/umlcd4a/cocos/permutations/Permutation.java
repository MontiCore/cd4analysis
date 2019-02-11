/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.umlcd4a.cocos.permutations;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import de.monticore.ast.ASTNode;

import com.google.common.collect.Sets;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.umlcd4a.prettyprint.CDPrettyPrinterConcreteVisitor;

@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class Permutation<T extends ASTNode> {
  
  private final T delegate;
  
  private final Set<ASTNode> astNodes = new LinkedHashSet<>();
  
  private final Set<BiConsumer<Collection<ASTNode>, String>> idSetters = new LinkedHashSet<>();
  
  Permutation(T assoc) {
    this.delegate = assoc;
  }
  
  Set<ASTNode> getAstNodes() {
    return Sets.union(astNodes, Collections.singleton(delegate));
  }
  
  void addAstNode(ASTNode astNode) {
    astNodes.add(astNode);
  }
  
  void addIdSetter(BiConsumer<Collection<ASTNode>, String> idSetter) {
    this.idSetters.add(idSetter);
  }
  
  void addAstNodes(Collection<? extends ASTNode> astNodes) {
    this.astNodes.addAll(astNodes);
  }
  
  void applyIdSetters(String id) {
    for (BiConsumer<Collection<ASTNode>, String> idSetter : idSetters) {
      idSetter.accept(getAstNodes(), id);
    }
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  Permutation<T> copy() {
    Permutation<T> copy = new Permutation(delegate.deepClone());
    for (ASTNode astNode : astNodes) {
      copy.addAstNode(astNode.deepClone());
    }
    for (BiConsumer<Collection<ASTNode>, String> idSetter : idSetters) {
      copy.addIdSetter(idSetter);
    }
    return copy;
  }
  
  @Override
  public String toString() {
    IndentPrinter indentPrinter = new IndentPrinter();
    CDPrettyPrinterConcreteVisitor prettyPrinter = new CDPrettyPrinterConcreteVisitor(indentPrinter);
    // TODO MB,SO Find better solution
    if (delegate instanceof ASTCD4AnalysisNode) {
      prettyPrinter.visit(delegate);
    }
    for (ASTNode astNode : astNodes) {
      if (astNode instanceof ASTCD4AnalysisNode) {
        prettyPrinter.prettyprint((ASTCD4AnalysisNode) astNode);
      }
    }
    return indentPrinter.getContent();
  }
  
  public T delegate() {
    return delegate;
  }
  
}
