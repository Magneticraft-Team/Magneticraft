# Lisp

Lisp is an old programing language, it was created around 1958, but it introduced a lot of features that 
modern programing languages still use like immutable lists, garbage collection, closures, lambdas, even metaprograming macros.

### Bases
To run a function just place the name of the function between parents, for example: (free)
add arguments placing them inside the parents after the function name: (print 5)

You can create a list using the character: ' and all the elements of the list in parents: '(1 2 3 4 5)
' will tell the interpreter not to execute the next thing, if you don't put ' it will try to execute it as a function

Using ' you can print a symbol as an string: (print 'Hello 'World!)

Functions can be created with defun: (defun say-5 () (print 5)) and now run it (say-5)
Variables are defined with define: (define x 5) and we can show it with (print x)

To get a list of available functions type: (env)