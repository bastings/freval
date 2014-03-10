export CLASSPATH=.:bin/:jar/*
DATA_PATH=sample
java -Dproperties=properties/default.properties -Dgold=$DATA_PATH/sample.gld -Dtest=$DATA_PATH/sample.tst -jar freval.jar
