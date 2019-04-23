#!/bin/sh
source /etc/profile
environmentParam=$1
opsname=$2
projectname=$3
param4=$4
param5=$5
param6=$6
SHELL_FOLDER=$(cd "$(dirname "$0")";pwd)
cd $SHELL_FOLDER
cd ..
if [ $# -eq 3 ];then
  java -jar azkabanTool.jar $environmentParam $opsname $projectname
  echo "param 3"
elif [ $# -eq 4 ];then
  java -jar azkabanTool.jar $environmentParam $opsname $projectname $param4
  echo "param 4"
elif [ $# -eq 5 ];then
  java -jar azkabanTool.jar $environmentParam $opsname $projectname $param4 $param5
  echo "param 5"
elif [ $# -eq 6 ];then
  java -jar azkabanTool.jar $environmentParam $opsname $projectname $param4 $param5 $param6
  echo "param 6"
else
  echo "param count error"
fi
