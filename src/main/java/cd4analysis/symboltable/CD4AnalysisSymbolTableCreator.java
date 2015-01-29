/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.symboltable;

import de.cd4analysis._ast.ASTCD4AnalysisBase;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.SymbolTableCreator;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

// TODO PN introduce CD4AnalysisSymbolTableCreationVisitor as soon as the CD4ABaseVisitor is generated
public class CD4AnalysisSymbolTableCreator extends SymbolTableCreator implements
    CD4AnalysisSymbolTableCreationVisitor {

  private String packageName = "";
  private String fullClassDiagramName = "";

  public CD4AnalysisSymbolTableCreator(ResolverConfiguration resolverConfig, @Nullable MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }

  /**
   * Creates the symbol table starting from the <code>rootNode</code> and returns the first scope
   * that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
    public Scope createFromAST(ASTCD4AnalysisBase rootNode) {
      requireNonNull(rootNode);
      rootNode.accept(this);
      return getFirstCreatedScope();
    }

  @Override
  public SymbolTableCreator getSymbolTableCreator() {
    return this;
  }

  @Override
  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  @Override
  public String getPackageName() {
    return packageName;
  }

  public void setFullClassDiagramName(String fullClassDiagramName) {
    this.fullClassDiagramName = fullClassDiagramName;
  }

  @Override
  public String getFullClassDiagramName() {
    return fullClassDiagramName;
  }
}
