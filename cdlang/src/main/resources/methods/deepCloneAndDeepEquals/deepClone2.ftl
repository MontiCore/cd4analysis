<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalClazzType","attributeList","PojoClazzesAsStringList")}
<#assign MCTypeFacade = glex.getGlobalVar("mcTypeFacade")>
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- when the class is already in our map we can return the result we already computed prior -->
if(map.containsKey(this)) {
  if((boolean) map.get(this)[1]) {
    return (${originalClazzType.printType()}) map.get(this)[0];
  }else{
    map.get(this)[1] = true;
  }
}else{
<#-- if the class is not in our map we have to compute the result -->
  map.put(this, new Object[]{result, false});
}
<#list attributeList as attr>
  <#assign thisObjectName = "this.${attr.getName()}">
  <#assign resultName = "result.${attr.getName()}">
  ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", attr.getMCType(), PojoClazzesAsStringList, thisObjectName, resultName)}
</#list>
map.get(this)[1] = true;

return result;
