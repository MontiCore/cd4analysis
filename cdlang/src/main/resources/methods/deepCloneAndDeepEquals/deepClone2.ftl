<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalClazzType","attributeList","PojoClazzesAsStringList")}
<#assign MCTypeFacade = glex.getGlobalVar("mcTypeFacade")>
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- We need the map to check if the current object we want to copy was already copied to make sure that an object -->
<#-- with multiple references will be copied as such and not as multiple objects -->
<#-- We need the value argument of the map to be an Array, because when we iterate over the this object, -->
<#-- we create the Object of the pojo class and directly add it to the map before calling its deepClone method. -->
<#-- This is needed to prevent stack overflows when having circular relations -->
<#-- Because the deepClone method would not create a object if it is in the map we check if its the first time, in which we see the item on the map.-->
<#-- if this is the case we still copy the object. -->
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
