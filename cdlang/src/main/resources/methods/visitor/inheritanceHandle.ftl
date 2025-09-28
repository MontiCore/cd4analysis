<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazzStrings")}
if (!getTraversedElements().contains(node)) {
  addTraversedElement(node);
  <#list clazzStrings?reverse as clazzName>
  visit((${clazzName})node);
  </#list>
  visit(node);
  traverse(node);
  endVisit(node);
  <#list clazzStrings as clazzName>
  endVisit((${clazzName})node);
  </#list>
  removeTraversedElement(node);
}
