#!/bin/bash
# (c) https://github.com/MontiCore/monticore
# script for all preprocessing steps of the pages job
# is used to have uniform bases for both gitlab and github pages
#
# remove all occurrences of '[[_TOC_]]' in markdown files
# because mkdocs already renders its own toc
case " $* " in
  *" inplace "*)
  for file in $(find ./docs/docs -type f -name "*.md")
  do
    sed -i 's/\[\[_TOC_\]\]//' $file
    perl -pi -e 's/\[([^\[\]\(\)]*)\]\([^\[\]\(\)]*git.rwth-aachen.de[^\[\]\(\)]*?\)/$1/g' $file
  done
  echo "[INFO] Removed all occurrences of '[[_TOC_]]' in *.md files"
  echo "[INFO] Removed all links to https://git.rwth-aachen.de in *.md files"
    ;;
esac
# move all directories that contain *.md files to the docs folder
# because mkdocs can only find *.md files there
rm -r docs_wd || true

case " $* " in
  *" symlink "*)
    # use symlinks to track updates
    mkdir docs_wd
    ln -s ../docs/overrides docs_wd/
    ln -s ../docs/stylesheets docs_wd/
    ln -s ../docs/scripts docs_wd/
    ln -s ../docs/img docs_wd/
    echo "[INFO] Using symlinks for live editing"
    ;;
  *)
    cp -r docs docs_wd
    rm docs_wd/*.md
    cp README.md docs_wd/README.md
    # all images referenced in the root-Readme must be handled specially :(
    # mkdir -p docs_wd/docs/img
    # cp docs/img/MC_Symp_Banner.png docs_wd/docs/img/MC_Symp_Banner.png
    # echo "[INFO] Copied site design"
    # Copy the javadoc directories for cd2plantuml, cd2smt, cd-runtime, cddiff, cdlang, cdmerge, cdtool, language-server, symtabdefinitiontool
    mkdir -p docs_wd/cd2plantuml
    cp -r cd2plantuml/target/docs/javadoc docs_wd/cd2plantuml/javadoc
    cp -r cd2plantuml/target/docs/testFixturesJavadoc docs_wd/cd2plantuml/testFixturesJavadoc
    mkdir -p docs_wd/cd2smt
    cp -r cd2smt/target/docs/javadoc docs_wd/cd2smt/javadoc
    cp -r cd2smt/target/docs/testFixturesJavadoc docs_wd/cd2smt/testFixturesJavadoc
    mkdir -p docs_wd/cd-runtime
    cp -r cd-runtime/target/docs/javadoc docs_wd/cd-runtime/javadoc
    cp -r cd-runtime/target/docs/testFixturesJavadoc docs_wd/cd-runtime/testFixturesJavadoc
    mkdir -p docs_wd/cddiff
    cp -r cddiff/target/docs/javadoc docs_wd/cddiff/javadoc
    cp -r cddiff/target/docs/testFixturesJavadoc docs_wd/cddiff/testFixturesJavadoc
    mkdir -p docs_wd/cdlang
    cp -r cdlang/target/docs/javadoc docs_wd/cdlang/javadoc
    cp -r cdlang/target/docs/testFixturesJavadoc docs_wd/cdlang/testFixturesJavadoc
    mkdir -p docs_wd/cdmerge
    cp -r cdmerge/target/docs/javadoc docs_wd/cdmerge/javadoc
    cp -r cdmerge/target/docs/testFixturesJavadoc docs_wd/cdmerge/testFixturesJavadoc
    mkdir -p docs_wd/cdtool
    cp -r cdtool/target/docs/javadoc docs_wd/cdtool/javadoc
    cp -r cdtool/target/docs/testFixturesJavadoc docs_wd/cdtool/testFixturesJavadoc
    mkdir -p docs_wd/language-server
    cp -r language-server/target/docs/javadoc docs_wd/language-server/javadoc
    cp -r language-server/target/docs/testFixturesJavadoc docs_wd/language-server/testFixturesJavadoc
    mkdir -p docs_wd/symtabdefinitiontool
    cp -r symtabdefinitiontool/target/docs/javadoc docs_wd/symtabdefinitiontool/javadoc
    cp -r symtabdefinitiontool/target/docs/testFixturesJavadoc docs_wd/symtabdefinitiontool/testFixturesJavadoc
    echo "[INFO] Copied JavaDocs"
    ;;
esac


for SOURCE_DIR in "docs" "cd2plantuml/src" "cd2smt/src" "cd-runtime/src" "cddiff/src" "cdlang/src" "cdmerge/src" "cdtool/src" "language-server/src" "symtabdefinitiontool/src"; do
  # We link to java & mc4 files in our md files - which is why we have to redirect them too
  find "$SOURCE_DIR" -type f \( -name "*.md" \) | while read -r filepath; do
     target_file="docs_wd/$filepath"
     mkdir -p "$(dirname "$target_file")"
     # use snippets to include the original files content
     if [ ! -f "$target_file" ]; then
       echo "--8<-- \"$filepath\"" > "$target_file"
     fi
  done
done
echo "[INFO] Created snippet files"

# the landing page snippet has to be removed again
# rm docs_wd/docs/README.md
