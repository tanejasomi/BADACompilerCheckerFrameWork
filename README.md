# BADACompilerCheckerFrameWork

The Purpose of this project is to verify source code of Compiler for ADA type language with verification tool Checker Framework.
Master contains unannotated source code and branch "annotatedCode" contains Annotated Code.

Specifications of Original Source Code:

Project Compiler Construction for BABY ADA is part of course work of Compiler construction at CSU EASTBAY.

A Compiler is a program that takes a program written in a source language and translates it into an equivalent program in a target language.

Source language for this compiler is BABY ADA or BADA which is simpler version of ADA TYPE language. Target language is MIPS three address code low level programming language.

Compiler has 6 main phases : Lexical Analyzer, Syntax Analyzer,Semantic Analyzer, Intermediate code generator, code optimizer, code generator. Each Phase transforms the source program from one representation into another representation. They communicate using with error handler and symbol table. This software implements Front End- language specific -Scanner (lexical analyzer), Parser(syntax analyzer) and Intermediate Code generator (Semantic analyzer) with symbol table and error handling.

This compiler is written in programming language Java 8 developed with IDE Eclipse, JDK 1.8.

Purpose: Purpose of this project is to learn phases of compiler construction - scanner, parser, code generation and symbol table. Project is developed in interleaved fashion with ongoing theory in class. It was great learning experience to see theory implemented practically. List (but not limited to) of special learning with implementation:

Symbol Table: Data Structures- HashMaps,Linked List,objects etc operations Scanner/Lexical Analyzer: DFA, lexemes, token ids, language specs etc. Parser/Syntax Analyzer: CFG, RD, top down parser implementation,type checking etc. Intermediate Code Generator: semantic analysis,language translation etc.
