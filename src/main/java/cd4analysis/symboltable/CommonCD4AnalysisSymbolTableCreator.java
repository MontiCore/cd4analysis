/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.symboltable;

import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;

import javax.annotation.Nullable;

// TODO PN introduce CD4AnalysisSymbolTableCreationVisitor as soon as the CD4ABaseVisitor is generated
public class CommonCD4AnalysisSymbolTableCreator extends CommonSymbolTableCreator implements
    CD4AnalysisSymbolTableCreator {

  private String packageName = "";
  private String fullClassDiagramName = "";

  public CommonCD4AnalysisSymbolTableCreator(ResolverConfiguration resolverConfig, @Nullable
  MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
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
