# John 👴

> Perhaps the most modern and declarative JSON library Java has to offer.

John is a modern, declarative, JSON library written entirely in Java that attempts to provide an alternative way to reason about your JSON. In many ways, it's a paradigm shift from other **imperative** libraries like [GSON](https://github.com/google/gson/tree/main).

## Why John? ⁉️

The John library differs **significantly** from most other JSON libraries in its core philosophy. John does **not** rely on reflection in any form. Instead,
you define the structure of your data with [Templates](#templates), which then ensure the JSON matches your expected schema **1:1**. If anything goes wrong, an _exceptionally rich exception_
will get thrown with the faulty source line and positions.

The Template system in many ways resembles [Zod](https://zod.dev/), but tailored specifically for JSON and Java design patterns.

## Getting Started ⚙️

To invite your new _co-worker_ to your project, include the following lines in your build file:

```xml
<dependency>
    <groupId>com.manchickas</groupId>
    <artifactId>john</artifactId>
    <version>1.0.0</version>
</dependency>
```

> [!TIP]
> Replace `1.0.0` with the latest version from Maven Central.

```java
public static void main(String[] args) {
    try {
        // Parse the source into a generic JSON structure.
        var json = John.parse("""
                {
                    "name": "Marie",
                    "age": 18
                }
                """);
        // Extract the 'name' property using the generic 'STRING' template.
        String name = json.get("./name", Template.STRING);
        // Greet our dear Marie!
        System.out.printf("Hello, %s!%n", name);
    } catch (JsonException e) {
        // Print the message with ASCII formatting enabled.
        System.err.println(e.getMessage(true));
    }
}
```

## Path Your Way Through 👣

The John library secretly has a **DSL** built specifically for **arbitrary** JSON navigation, called [Json Paths](./src/main/java/com/manchickas/john/path/JsonPath.java).
The syntax was specifically designed to strongly resemble _file-system paths_.

A JSON Path consists of multiple segments. Each segment is separated from the preceding one with a forward slash.
The path may start with a **dot** `./`, specifying it's relative to the current node.

> [!NOTE]
> Paths are **always relative** to the node they get resolved against.
> Including a dot at the very beginning of the path is a purely **stylistic choice**, unless you're pairing it with the _subscript operator_, or using it as a _root-path_.

## Fail-fast Model 🧨

As opposed to most other libraries, John takes a different approach to handling invalid JSON.
Instead of being lenient and unsafe, John uses a single, **checked exception** — `JsonException` — to ensure you're never left with a half-baked state.

Since in most cases JSON is parsed from an _arbitrary string_, you should **not** assume the parsing will succeed most of the time.
Quite the opposite, in fact. John thus decides to be **pessimistic** instead.

This design allows us to annotate most **JSON-related methods** and [Template](#templates) **conversions** as `@NotNull`, which resolves the unsafe chains of potential `NPE`s you get in other libraries.

It's worth noting that John keeps track of _source spans_, the source locations of **each element**, to visually enhance most error messages.

```
    "foo": "bar\w"
               ^^
(2:16-17) Encountered an unknown escape sequence '\w'.
```

## Templates 🗞️

One of the most distinctive features of the John library is **Templates**. Templates may be seen as a way to _scaffold your JSON_.

The John library uses templates **excessively**, so understanding them deeply is a **crucial step** in mastering John.
Templates are **bidirectional**. They define both how to **parse** a specific JSON element and how to **serialize** a correctly typed value back into JSON.

Any additional validation defined by the template is performed in-place for both of these steps.

> [!NOTE]
> You _almost never_ need to define your **own** templates. The standard library provides a handful of methods to compose
> them, which, when used correctly, almost completely eliminate the purpose in defining your own templates from scratch.

Templates are **stateless**. They define _how to process the value_, not the value to process. This pattern allows you to **re-use** existing templates
as much as you'd want to. Most templates should therefore be defined as `public static final` constants, rather than inlined into whichever method expects one.


