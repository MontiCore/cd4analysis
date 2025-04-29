<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalClazzType", "attributeList","PojoClazzesAsStringList")}
<#assign MCTypeFacade = glex.getGlobalVar("mcTypeFacade")>
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- when the class is already in our map we can return the result we already computed prior -->
if(map.containsKey(this)) {
  return map.get(this);
}
<#-- if the class is not in our map we have to compute the result -->
<#list attributeList as attr>
  <#assign thisObjectName = "this.${attr.getName()}">
  <#assign resultName = "result.${attr.getName()}">
  ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", originalClazzType, attr.getMCType(), PojoClazzesAsStringList, thisObjectName, resultName)}
</#list>
map.put(this, result);

return result;
