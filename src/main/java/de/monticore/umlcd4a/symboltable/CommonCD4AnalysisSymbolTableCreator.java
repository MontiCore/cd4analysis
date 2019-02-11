/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import javax.annotation.Nullable;

import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;

@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class CommonCD4AnalysisSymbolTableCreator extends CommonSymbolTableCreator implements
    CD4AnalysisSymbolTableCreator {

  private String packageName = "";
  private String fullClassDiagramName = "";

  public CommonCD4AnalysisSymbolTableCreator(ResolvingConfiguration resolvingConfig, @Nullable
  MutableScope enclosingScope) {
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
  public MutableScope getEnclosingScope() {
    return scopeStack.getFirst();
  }
}
