2024-08-25
- Lexer tests are passing.
- Full grammar tests are failing. For example, the FormatVisitorTest is having class cast exceptions, receiving tokens where it expects expressions. It's not clear why this is happening. The expression grammar rules look to correctly return Expressions. It's hard to tell what parser layer the exception is happening on -- perhaps it's the lexer layer that's the issue. Additional debugging lines in the LRParser around the class cast exception shows that a plus(+) token is being cast in to the expression. But it's not clear why that's being returned from the expression rule. Perhaps additional testing in the parser respository is needed.

2024-10-06
- Tests in parser2 are starting to work while depending on the parser library.
- There's now a workspace in ~/dev that contains both parser and parser2. Changes can be made to parser and tested in parser2 without commiting. 
- Code for parser2 is being pushed to an extract-parser branch.
- Next: There's issues with the LangToASM (and tests/run_tests.sh script). Also, there are some build errors when packaging parser2. These are probably a good place to start -- for instance, the ShiftReduceResolverTest is in parser2, while it's testing code in parser. 