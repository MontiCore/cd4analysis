<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalClazzName", "attributeList","hasSetterList")}
<#assign MCTypeFacade = glex.getGlobalVar("mcTypeFacade")>
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#list 0..attributeList?size-1 as i>
<#if MCTypeFacade.getInstance().isBooleanType(attributeList[i].getMCType())>
<#------------------------------------>
  <#else>
    <#if CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCListType(attributeList[i].getMCType()) || CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(attributeList[i].getMCType()))>
<#------------------------------------>
    <#else>
      <#if CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(attributeList[i].getMCType()))>
<#------------------------------------>
      <#else>

<#------------------------------------>
    </#if>
  </#if>
</#if>
</#list>
