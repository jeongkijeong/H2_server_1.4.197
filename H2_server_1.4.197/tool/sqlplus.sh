# Get the aliases and functions
if [ -f ~/.bashrc ]; then
        . ~/.bashrc
fi

export PROC_HOME=$HOME/app/H2Database
export PROC_NAME=$PROC_HOME/lib/h2-1.4.192.jar
 
$JAVA_HOME/java -classpath $PROC_NAME org.h2.tools.Shell -url jdbc:h2:tcp://127.0.0.1:4951/mem:H2M -user NMS -password NMS

