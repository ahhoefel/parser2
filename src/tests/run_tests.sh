ROOT=/Users/hoefel/dev/parser2
JAR=$ROOT/target/parser-1.0-SNAPSHOT.jar
TEST_ROOT=$ROOT/src/tests
OUTPUT=$ROOT/src/tests/target/main.s

mvn -f $ROOT -DskipTests package
echo "java -jar $JAR --root=$TEST_ROOT --targets=$1 --output=$OUTPUT"
java -jar $JAR --root=$TEST_ROOT --targets=$1 --output=$OUTPUT

