<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalClazzType","attributeList","PojoClazzesAsStringList")}
<#assign MCTypeFacade = glex.getGlobalVar("mcTypeFacade")>
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- We need the map to check if the current object we want to copy was already copied to make sure that an object -->
<#-- with multiple references will be copied as such and not as multiple different objects -->
<#-- we create the Object of the pojo class and directly add it to the map before calling its deepClone method. -->
<#-- This is needed to prevent stack overflows when having circular relations -->
<#-- Because the deepClone method would not create a object if it is in the map we check if it is at first by looking in the map.-->
<#-- If the item is contained as a key we simple return the value of map.get(key). -->
if(map.containsKey(this)) {
  return (${originalClazzType.printType()}) map.get(this);
}else{
<#-- if the class is not in our map we have to compute the result -->
  map.put(this, result);
}
<#list attributeList as attr>
  <#assign thisObjectName = "this.${attr.getName()}">
  <#assign resultName = "result.${attr.getName()}">
  ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", attr.getMCType(), PojoClazzesAsStringList, thisObjectName, resultName)}
</#list>

return result;
