# brl php generator
A BRL generator for MySQL and PHP (util for my Electiva de Programación course).

## How to use it?
First of all, change the DB_NAME to your database name name
```java
public static final String DB_NAME = "pruebaphp";
```
Next step is to add as many tables (and it respective columns) as you need to generate, you can do it in the second line inside the main method like this:
```java
tables.add(new Table("personas", "id", "nombres", "apellidos"));
```

## What's next?
Just run the project and your files will be generated inside a 'dao' folder in the project
