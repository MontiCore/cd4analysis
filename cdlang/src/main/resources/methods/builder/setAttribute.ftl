<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "hasSetter")}
<#assign MCTypeFacade = glex.getGlobalVar("mcTypeFacade")>
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#if MCTypeFacade.getInstance().isBooleanType(attribute.getMCType())>
  <#if hasSetter>
v.set${attribute.getName()?cap_first}(this.${attribute.getName()});
  <#else>
v.${attribute.getName()} = this.${attribute.getName()};
  </#if>
<#------------------------------------>
  <#else>
    <#if (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCListType(attribute.getMCType()) || CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(attribute.getMCType()))>
      <#if hasSetter>
if(this.${attribute.getName()}!=null){
  v.set${attribute.getName()?cap_first}(this.${attribute.getName()});
}
      <#else>
if(this.${attribute.getName()}!=null){
  v.${attribute.getName()} = this.${attribute.getName()};
}
      </#if>
<#------------------------------------>
    <#else>
      <#if CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(attribute.getMCType())>
        <#if hasSetter>
if(this.${attribute.getName()} != null && this.${attribute.getName()}.isPresent()){
  v.set${attribute.getName()?cap_first}(this.${attribute.getName()}.get());
}else{
  v.set${attribute.getName()?cap_first}(null);
}
      <#else>
if(this.${attribute.getName()} != null && this.${attribute.getName()}.isPresent()){
  v.${attribute.getName()} = this.${attribute.getName()};
}else{
  v.${attribute.getName()} = Optional.empty();
}
      </#if>
<#------------------------------------>
    <#else>
      <#if hasSetter>
v.set${attribute.getName()?cap_first}(this.${attribute.getName()});
      <#else>
v.${attribute.getName()} = this.${attribute.getName()};
      </#if>
<#------------------------------------>
    </#if>
  </#if>
</#if>
