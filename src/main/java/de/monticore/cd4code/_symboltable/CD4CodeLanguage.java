/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import com.google.common.collect.ImmutableSet;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CD4CodeLanguage extends CD4CodeLanguageTOP {
  public static final String FILE_ENDING = "cd";

  public CD4CodeLanguage() {
    super("CD 4 Code Language", FILE_ENDING);
  }

  @Override
  protected CD4CodeModelLoader provideModelLoader() {
    return new CD4CodeModelLoader(this);
  }

  protected Set<String> calculateModelNamesSimple(String name) {
    // e.g., if p.CD.Clazz, return p.CD
    if (!Names.getQualifier(name).isEmpty()) {
      return ImmutableSet.of(Names.getQualifier(name));
    }
    return Collections.emptySet();
  }

  protected Set<String> calculateModelNamesParts(String name) {
    // e.g., if p.CD.Clazz.Meth return p.CD
    List<String> nameParts = Splitters.DOT.splitToList(name);

    // at least 3, because of CD.Clazz.meth
    if (nameParts.size() >= 3) {
      // cut the last two name parts (e.g., Clazz.meth)
      return ImmutableSet.of(Joiners.DOT.join(nameParts.subList(0, nameParts.size() - 2)));
    }
    return Collections.emptySet();
  }

  @Override
  protected Set<String> calculateModelNamesForCDType(String name) {
    return calculateModelNamesSimple(name);
  }

  @Override
  protected Set<String> calculateModelNamesForOOType(String name) {
    return calculateModelNamesForCDType(name);
  }

  @Override
  protected Set<String> calculateModelNamesForType(String name) {
    return calculateModelNamesForOOType(name);
  }

  @Override
  protected Set<String> calculateModelNamesForField(String name) {
    return calculateModelNamesParts(name);
  }

  @Override
  protected Set<String> calculateModelNamesForVariable(String name) {
    return calculateModelNamesForField(name);
  }

  @Override
  protected Set<String> calculateModelNamesForCDMethodSignature(String name) {
    return calculateModelNamesParts(name);
  }

  @Override
  protected Set<String> calculateModelNamesForMethod(String name) {
    return calculateModelNamesForCDMethodSignature(name);
  }

  @Override
  protected Set<String> calculateModelNamesForFunction(String name) {
    return calculateModelNamesForMethod(name);
  }

  @Override
  protected Set<String> calculateModelNamesForCDAssociation(String name) {
    return calculateModelNamesSimple(name);
  }

  @Override
  protected Set<String> calculateModelNamesForCDRole(String name) {
    return calculateModelNamesSimple(name);
  }
}
