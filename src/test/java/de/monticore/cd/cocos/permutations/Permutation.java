/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.permutations;

import com.google.common.collect.Sets;
import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.cd.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.cd.prettyprint.CDPrettyPrinter;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;

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
    CDPrettyPrinter prettyPrinter = new CDPrettyPrinter(indentPrinter);
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
