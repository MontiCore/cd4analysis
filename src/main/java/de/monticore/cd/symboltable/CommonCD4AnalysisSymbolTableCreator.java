/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.symboltable;

import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;

import javax.annotation.Nullable;


public class CommonCD4AnalysisSymbolTableCreator extends CommonSymbolTableCreator implements CD4AnalysisSymbolTableCreator {

  private String packageName = "";
  private String fullClassDiagramName = "";

  public CommonCD4AnalysisSymbolTableCreator(ResolvingConfiguration resolvingConfig, @Nullable
          Scope enclosingScope) {
    super(resolvingConfig, enclosingScope);
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

  @Override
  public Scope getEnclosingScope() {
    return scopeStack.getFirst();
  }
}
