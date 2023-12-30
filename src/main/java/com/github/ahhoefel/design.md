Install process for LLVM:
- install xcode
- install cmake
- install llvm: https://llvm.org/docs/GettingStarted.html
- install maven
- install javacpp
-

Code Layout
- Key classes:
  - LanguageRules. Contains all the specific rules for parsing the ro language. Contains the parse method for parsing a file.
    - See com.github.ahhoefel.rules.
    - The resulting AST is in com.github.ahhoefel.ast.{expression,statements,symbols,type}
    - A better organization is probably language.rules, language.ast, language.visitors and simply parser. 
  - FileTree. Contains logic for reading in multiple files based on their imports. This may be a bit backwards or need refactoring.
  - Visitors. The logic of going up and down the AST is done by visitors. Contained in com.github.ahhoefel.ast.visitor.

- Interpreter
  - There's an old interpreter which predates the vistor model and runs the code on a stack machine. This should be removed and replaced with LLVM visitor to improve cleanliness.

- Testing
  - Format visitor is used in the LangaugeRulesTest to ensure that input and output for well formed files are equal. This tests the AST's coverage.
  - There's a TestHarness in com.github.ahhoefel.interpreter

- TODOS:
  - Kill dead code.
  - Separate the generic parser from the language specific code. 
  - Improve testing.
  - Implement LLVM visitor.
  - Remove interpretor and IR.


Language ideas.
- go like syntax
- go style packages and imports, though perhaps autonamed by file.
- private/public via captialization? Maybe not.
- perhaps visibility can be modified by config.
- manual memory management, though with smart pointers built in.
- perhaps no casting between value and reference types. e.g. create me a reference
  a value on the stack that I can pass into functions, but not return or assign to
  member variables. Can we do this is in a way that'll be consistent and usable?
  What if I pass a stack reference, then someone wants to put in a struct? Can I tell how
  long that struck is allowed to live? Maybe.. maybe..
  THe semantics of collections and these reference types need to be super clear.
- Java style generic types. At least able to define arbitary collections.
- I'd like to have support for running arbitrary code at compile time.
  Specifically, you should be able to call a function at compile time to
  define a symbol, class, function, etc. This would replace macros in all cases.
  Code using the compile-time constructed symbols should be type checked/error checked
  at compile time.
- Specifically, List<Foo> could be an implicit call to function List(Type t) Type
  which would return the singleton List class associated type T.
  I'd need to have types parameterized by tuples.
- Most features of the language should be defined within the language as much as possible.
  E.g. arrays or other basic containers might be defined on top of a malloc. The
  compile-time code generation might make this possible, though there will be bootstrapping
  issues. (What if I need an array to define the type tuples needed in defining an array?)
- Compile-time symbol generation should be used to find a way out of dependency ingection hell.
  That is, all dependencies should be injected statically rather than at runtime.
- Code should compile to assembly. I'd like it make it so you could build an operating system from
  scratch without too much falling back to much explicit assembly. Though, it's likely impossible
  for the compiler to have all the assembly concepts natively. Perhaps there should be libraries for
  exactly this.
- Code should also be interpretable, as will be used at compile time. An interactive shell would be nice.
- All references are non-null. You have to explicitly use an Optional<T> when you want it.
- Concatenating strings must be fast and easy. Mutable strings are supported through arrays.
- go style duck typed interfaces.


Scoping

func foo(x int) {
  var y int
  func bar (z int) int {
     var y int // allowed because y is not in scope.
     func yolo() { } // new function in bar scope
     foo(z) // allowed because foo is in scope.
     biz() // allowed? is biz declared statically within foo?
  }
  func biz () {
     func yolo() { } // new function in biz scope. Doesn't conflict with yolo from bar.
     func bar() { // not allowed. Can't redeclare bar in foo.
     }
     func yolo() {

     }
  }
  func pho() {} // not allowed. Can't redeclare in the file scope.
  if x {
    var y // not allowed. Can't redefine a variable in the same scope.
    var w
  }
  if x {
    var w // allowed because the previous declaration is an inner scope.
  }
  w = 1 // not allowed. because w is not longer in scope.
}

func pho() {
  bar() // not allowed because bar is not in scope.
}

scope file
  func foo
  func pho
  scope foo
    func bar
    func biz
    var y
    scope bar
      func yolo
    scope biz
      func yolo
    scope if
      var w
    scope if
      var w

  pho

Unique names


Live registers
   1
 /   \ \
2     3  4
     /

Perform a post order traversal.
At each point, list the roots of all the completed subtree.

fn postOrder(List roots, Node n) {
  for (Node child : n.children) {
    postOrder(roots, child)
  }

  visit(n)
  for (Node child : n.children) {
    roots.remove(child)
  }
  roots.add(n);
}

- structs

- Comment idea:
  - Separate comment types for code and text to perserve formatting.
  - Comments should have a line separator so they can be autoformatted, word wrapped properly.