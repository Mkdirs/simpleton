# Simpleton

Simpleton is a basic scripting language I made for fun.\
It can run simple CLI programs.

# Getting Started
- [Variable Declaration](#variable-declaration)
- [Variable Assignment](#variable-assignment)
- [Operators](#operators)

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
``let name = null``\
> \
The statement above will not fail but *name* will have no type and its value would not be changed *(except with `null`)* :
```
let name = null
name = "hello world" <--- Will fail
name = null <------------ Will not fail
```


# Variable Assignment

As you may have seen, assigning a value to a variable is pretty simple:\
``name = value``

Of course, *name* must be declared beforehand.\
Also, *value* must either be ``null`` or match the type of *name*.

# Operators

The operators in Simpleton consist of:
- ``+``  [Plus](#plus)
- ``-``  [Minus](#minus)
- ``*``  [Star](#star)
- ``/``  [Divide](#divide)
- ``&&`` [And](#and)
- ``||`` [Or](#or)
- ``!``  [Not](#not)
- ``>``  [Greater Than](#greater-than)
- ``>=`` [Greater Than Or Equals To](#greater-than-or-equals-to)
- ``<``  [Lower Than](#lower-than)
- ``<=`` [Lower Than Or Equals To](#lower-than-or-equals-to)
- ``==`` [Equality](#equality)
- ``!=`` [Inequality](#inequality)

## Plus
## Minus
## Star
## Divide
## And
## Or
## Not
## Greater Than
## Greater Than Or Equals To
## Lower Than
## Lower Than Or Equals To
## Equality
## Inequality