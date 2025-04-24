<#-- (c) https://github.com/MontiCore/monticore -->
<#-- 3 stands for 3 argument which is the object to compare with -->
<#-- the first argument is the object to compare with -->
<#-- the second argument is a boolean which will decide if the right order of elements in set and lists is enforced -->
<#-- the third argument is a set which will be used to store the already visited objects as the language can have circular structure -->
<#-- to remember the visited objects we therefore need to save them -->
${tc.signature("originalClazzType", "attributeList", "PojoClazzesAsStringList")}
if(visitedObjects.contains(this)){
  return true;
}
visitedObjects.add(this);
if(!(o instanceof ${originalClazzType.printType()})){
  return false;
}
${originalClazzType.printType()} castO = (${originalClazzType.printType()}) o;
<#if attributeList??>
<#list attributeList as attr>
<#assign resultBooleanName = "result" + attr.getName()?cap_first + attr.getMCType().printType()?cap_first?replace(".","")?replace("<","")?replace(">","")>
boolean ${resultBooleanName} = true;
<#assign firstObjectName = "this." + attr.getName()>
<#assign secondObjectName = "castO." + attr.getName()>
  ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", originalClazzType, attr.getMCType(),attr.getName(), PojoClazzesAsStringList, firstObjectName, secondObjectName, resultBooleanName)};
if(! ${resultBooleanName}){
  return false;
}
</#list>
</#if>
return true;
