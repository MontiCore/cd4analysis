package de.monticore.cdconcretization.type;

import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultEnumConstantsCompleter extends AbstractTypeCompleter {
  @Override
  public void completeEnumDetails(
      ASTCDEnum concreteEnum, ASTCDEnum referenceEnum, TypeCompletionContext context)
      throws CompletionException {
    List<ASTCDEnumConstant> processed = new ArrayList<>();
    List<ASTCDEnumConstant> toProcess = new ArrayList<>(concreteEnum.getCDEnumConstantList());

    for (ASTCDEnumConstant rConstant : referenceEnum.getCDEnumConstantList()) {
      Optional<ASTCDEnumConstant> cConstant =
          toProcess.stream().filter(r -> r.getName().equals(rConstant.getName())).findFirst();
      if (cConstant.isPresent()) {
        processed.addAll(toProcess.subList(0, toProcess.indexOf(cConstant.get()) + 1));
        toProcess =
            new ArrayList<>(
                toProcess.subList(toProcess.indexOf(cConstant.get()) + 1, toProcess.size()));
      } else {
        if (concreteEnum.getCDEnumConstantList().stream()
            .anyMatch(c -> c.getName().equals(rConstant.getName()))) {
          throw new CompletionException(
              "Order of enum constant incarnations in "
                  + concreteEnum.getName()
                  + " is not conform! Completion will be aborted");
        }
        processed.add(rConstant.deepClone());
      }
    }

    processed.addAll(toProcess);
    concreteEnum.setCDEnumConstantList(processed);

    // continue chain
    next(concreteEnum, referenceEnum, context);
  }
}
