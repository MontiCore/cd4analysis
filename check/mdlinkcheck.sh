linkCheckRes=$(java -jar check/MDLinkCheckerCLI.jar "$@")
echo "[MDLinkCheck]: $linkCheckRes"
if [[ $linkCheckRes == *"ERROR"* ]]
then
  exit 1
else
  exit 0
fi