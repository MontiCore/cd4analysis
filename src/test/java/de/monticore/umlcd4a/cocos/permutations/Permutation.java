package de.monticore.umlcd4a.cocos.permutations;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import mc.ast.ASTNode;
import mc.helper.IndentPrinter;

import com.google.common.collect.Sets;

import de.monticore.umlcd4a.prettyprint.CDConcretePrettyPrinter;
import de.monticore.utils.ForwardingASTNode;

public class Permutation<T extends ASTNode> extends ForwardingASTNode<T> {
  
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
    CDConcretePrettyPrinter prettyPrinter = new CDConcretePrettyPrinter();
    prettyPrinter.prettyPrint(delegate, indentPrinter);
    for (ASTNode astNode : astNodes) {
      prettyPrinter.prettyPrint(astNode, indentPrinter);
    }
    return indentPrinter.getContent();
  }
  
  @Override
  public T delegate() {
    return delegate;
  }
  
}
