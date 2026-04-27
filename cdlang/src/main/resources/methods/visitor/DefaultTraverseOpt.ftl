<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("getter", "isPresent", "clazzname")}
if (node.${isPresent}()) {
  node.${getter}().accept((${clazzname})this);
}
