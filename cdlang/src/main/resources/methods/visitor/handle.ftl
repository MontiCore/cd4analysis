<#-- (c) https://github.com/MontiCore/monticore -->
if (!getTraversedElements().contains(node)) {
  addTraversedElement(node);
  visit(node);
  traverse(node);
  endVisit(node);
  removeTraversedElement(node);
}
