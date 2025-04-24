<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute","oldValueName")}
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
${attribute.getMCType().printType()} ${oldValueName} = this.${attribute.getName()};
<#if CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(attribute.getMCType())>
this.${attribute.getName()} = Optional.ofNullable(${attribute.getName()});
<#else>
this.${attribute.getName()} = ${attribute.getName()};
</#if>
notifyObserver${attribute.getName()?cap_first}(this,${oldValueName});
