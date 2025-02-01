set -x 
PARSER=/Users/hoefel/dev/parser
JAR=$PARSER/target/parser-1.0-SNAPSHOT.jar
mvn -f $PARSER -DskipTests package

PARSER2=/Users/hoefel/dev/parser2
JAR2=$PARSER2/target/parser2-1.0-SNAPSHOT.jar
TEST_ROOT=$PARSER2/src/tests
OUTPUT=$PARSER2/src/tests/target/main.s

cp $JAR $PARSER2/target/libs/parser-1.0-SNAPSHOT.jar

mvn -f $PARSER2 -DskipTests package
java -cp $JAR2 com.github.ahhoefel.lang.tools.LangToASM --root=$TEST_ROOT --targets=$1 --output=$OUTPUT

