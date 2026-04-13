# 🧠 Lox Interpreter (Java)

An interpreter for the **Lox programming language**, built in Java as Part II of the book *Crafting Interpreters*

## ✨ Project Goal

This project was built as a learning exercise to deeply understand how programming languages work by implementing an interpreter from scratch hehe...

## 📖 About

This project is a complete implementation of the tree-walk interpreter for the Lox language, following the first part of the book

It includes:

* Lexical analysis (scanner)
* Parsing into AST
* Static resolution
* Tree-walk interpretation

## 🚀 Features

* ✅ Variables and scope
* ✅ Functions (first-class)
* ✅ Control flow (if, while, for)
* ✅ Classes and inheritance
* ✅ Closures
* ✅ Error handling

## 🛠️ Technologies

* Java
* Object-Oriented Design
* Abstract Syntax Trees (AST)

## ▶️ How to Run

1. Clone the repository:

```bash
git clone [https://github.com/Tanakeo/Lox.git]
cd Lox
```

2. Compile the project:

```bash
javac -d out lox/*.java
```

3. Run the interpreter:

```bash
java -cp out lox.Lox
```

4. Run a script:

```bash
java -cp out lox.Lox ..\scripts\test.lox
```

## 💡 Example

```lox
var curr = 1;
var prev = 0;

for(var i = 0; i < 21; i = i + 1){
    print curr;
    var next = curr + prev;
    prev = curr;
    curr = next;
}
```

## 🧱 Architecture

The interpreter is structured into several main components:

* **Scanner** → converts source code into tokens
* **Parser** → builds an AST
* **Resolver** → performs static analysis
* **Interpreter** → executes the AST

## 🙌 Acknowledgements

Big thanks to Robert Nystrom for the amazing book *Crafting Interpreters*.

⭐ If you like this project, feel free to star the repo!
