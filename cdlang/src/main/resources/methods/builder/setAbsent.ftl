<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#if CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCListType(attribute.getMCType())>
this.${attribute.name} = new ArrayList<>()
  <#else>
  <#if CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(attribute)>
this.${attribute.name} = new HashSet<>();
    <#else>
    <#if CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(attribute)>
this.${attribute.name} = Optional.empty();
    </#if>
  </#if>
</#if>
return this.realBuilder;


