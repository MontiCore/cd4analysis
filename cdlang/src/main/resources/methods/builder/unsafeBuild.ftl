<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalClazzName", "attributeList","hasSetterList")}

<#assign MCTypeFacade = glex.getGlobalVar("mcTypeFacade")>
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>

var v = new ${originalClazzName}();

<#list 0..attributeList?size-1 as i>
<#if MCTypeFacade.getInstance().isBooleanType(attributeList[i].getMCType())>
  <#if hasSetterList[i]>
v.set${attributeList[i].getName()?cap_first}(this.${attributeList[i].getName()});
  <#else>
v.${attributeList[i].getName()} = this.${attributeList[i].getName()};
  </#if>
<#------------------------------------>
<#else>
  <#if CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCListType(attributeList[i].getMCType()) || CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(attributeList[i].getMCType())>
    <#if hasSetterList[i]>
v.set${attributeList[i].getName()?cap_first}(this.${attributeList[i].getName()});
      <#else>
v.${attributeList[i].getName()} = this.${attributeList[i].getName()};
    </#if>
<#------------------------------------>
  <#else>
    <#if CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(attributeList[i].getMCType())>
       <#if hasSetterList[i]>
if(this.${attributeList[i].getName()} != null && this.${attributeList[i].getName()}.isPresent()){
  v.set${attributeList[i].getName()?cap_first}(this.${attributeList[i].getName()}.get());
}else{
  v.set${attributeList[i].getName()?cap_first}(null);
}
      <#else>
if(this.${attributeList[i].getName()} != null && this.${attributeList[i].getName()}.isPresent()){
  v.${attributeList[i].getName()} = this.${attributeList[i].getName()};
}else{
  v.${attributeList[i].getName()} = Optional.empty();
}
      </#if>
<#------------------------------------>
   <#else>
      <#if hasSetterList[i]>
v.set${attributeList[i].getName()?cap_first}(this.${attributeList[i].getName()});
      <#else>
v.${attributeList[i].getName()} = this.${attributeList[i].getName()};
      </#if>
<#------------------------------------>
    </#if>
  </#if>
</#if>
</#list>
${defineHookPoint("methods.builder.unsafeBuild:Inner")}
return v;
