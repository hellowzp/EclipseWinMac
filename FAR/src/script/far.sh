# !/bin/bash
#
# Running FAR on linux
#
java -cp FAR-@APPLICATION_VERSION@.jar:lib/commons-logging-1.1.jar:lib/log4j-1.2.7.jar:lib/j6plugin-0.2.jar:conf net.pandoragames.far.ui.swing.FindAndReplace $*
