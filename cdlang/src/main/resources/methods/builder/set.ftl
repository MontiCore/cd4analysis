<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#if CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(attribute.getMCType())>
this.${attribute.getName()} = Optional.ofNullable(${attribute.getName()});
<#else>
this.${attribute.getName()} = ${attribute.getName()};
</#if>
return this.realBuilder;
