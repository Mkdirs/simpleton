# Simpleton

Simpleton is a basic scripting language I made for fun.\
It can run simple CLI programs.

# Getting Started
- [Variable Declaration](#variable-declaration)
- [Variable Assignment](#variable-assignment)
- [Operations](#operations)
- [Control Structures](#control-structures)
- [Functions](#functions)
- [Example](#examples)

# Variable Declaration

When declaring a variable your line must match the following syntax:\
``let name:type``

Where *type* is either ``int``, ``float``, ``string``, ``char`` or ``bool``.

> ***NOTE:***
You can also give a value when declaring a variable.\
``let name:type = value``
\
> \
When doing so, the type can be inferred.\
``let name = value``


> ***WARNING:***
The language cannot infer the value ``null`` since it has no real type.\
``let name = null``

# Variable Assignment

As you may have seen, assigning a value to a variable is pretty simple:\
``name = value``

Of course, *name* must be declared beforehand.\
Also, *value* must either be ``null`` or match the type of *name*.

# Operations

The operations supported on the types are:
- ## ``+`` Plus
  - ``string + string`` returns the concatenation of two strings.
  - ``int + int``
  - ``float + float``
  - ``int + float`` (or ``float + int``) will always return a float.

- ## ``-`` Minus
  - ``int - int``
  - ``float - float``
  - ``int - float`` (or ``float - int``) will always return a float.
  - ``-int`` (or ``-float``)

- ## ``*`` Star
  - ``int * int``
  - ``float * float``
  - ``int * float`` (or ``float * int``)
  
- ## ``/`` Divide
  - ``int / int`` will return the integer part of the result.
  - ``float / float``
  - ``int / float`` (or ``float / int``)

- ## ``%`` Modulo
  - ``int % int`` will return the remainder of the division between two integer.

- ## ``&&`` And
  - ``bool && bool``

- ## ``||`` Or
  - ``bool || bool``

- ## ``!`` Not
  - ``!bool``

- ## ``==`` Equality
  - ``string == string``
  - ``char == char``
  - ``int == int``
  - ``float == float``
  - ``bool == bool``

- ## ``!=`` Inequality
  - ``string != string``
  - ``char != char``
  - ``int != int``
  - ``float != float``
  - ``bool != bool``

- ## ``>`` Greater Than
  - ``int > int``
  - ``float > float``
  - ``char > char`` returns if the first char comes after the second.

- ## ``>=`` Greater Than Or Equals To
  - ``int >= int``
  - ``float >= float``
  - ``char >= char``

- ## ``<`` Lower Than
  - ``int < int``
  - ``float < float``
  - ``char < char`` returns if the first char comes before the second.

- ## ``<=`` Lower Than Or Equals To
  - ``int <= int``
  - ``float <= float``
  - ``char <= char``

# Control Structures

- ## If branch
The syntax for if block is simple:
````
if(expression) then {
  ...
  code
  ...
}
````

- ## Else branch
Add an else branch to your if like this:
````
if(expression) then {
  ...
  code
  ...
}else do{
  ...
  code
  ...
}
````

- ## Else-If branch
There is no keyword or special way to make else-if branches.\
The way to simulate that is to nest an if branch in an else branch:
````
if(expression) then{
  ...
  code
  ...
} else do{
  if(expression) then {
    ...
    code
    ...
  }
}
````

- ## While loop
The syntax is also very simple:
````
while(expression) do {
  ...
  code
  ...
}
````


# Functions

Use the syntax below to call a function:\
``name(arg1, arg2, ..., argn)``

The language doesn't support functions declaration for the moment.\
But it provides built-in functions you can already use such as:

``print(expression)``\
This will print the value of _expression_ on the standard output.

``input(prompt)``\
This will print _prompt_ on the standard output and return a value from the standard input.
> ***NOTE:***\
> _prompt_ is a string and the function returns a string.

# Examples

Hello world program
````
print("Hello world")
````
\
\
Greet the user
````
let name = input("What is your name?: ")
let greeting : string = "Hello "+name+" !"
print(greeting)
````
\
\
FizzBuzz
````
let number = 1
let msg:string
while(number <= 10) do{
  msg = ""
  
  if(number % 3 == 0) then {
    msg = "Fizz"
  }else do{
    if(number % 5 == 0) then {
      msg = "Buzz"
    }
  }

  if(number % 15 == 0) then {
    msg = "FizzBuzz"
  }

  if(msg == "") then {
    print(number)
  }else do {
    print(msg)
  }

  number = number+1
}
````