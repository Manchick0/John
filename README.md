# John 👴
[![Maven Central](https://img.shields.io/badge/Maven-Central-F18900?style=flat&logo=sonatype&link=https%3A%2F%2Fcentral.sonatype.com%2Fartifact%2Fcom.manchickas%2Fjohn)](https://central.sonatype.com/artifact/com.manchickas/john)

> Perhaps the most modern and declarative JSON library Java has to offer.

John is a modern, declarative, JSON library written entirely in Java that attempts to provide an alternative way to reason about your JSON. In many ways, it's a paradigm shift from other **imperative** libraries like [GSON](https://github.com/google/gson/tree/main).

## Why John? ⁉️

The John library differs **significantly** from most other JSON libraries in its core philosophy. John does **not** rely on reflection in any form. Instead,
you define the structure of your data with [Template](#templates), which then ensure the JSON matches your expected schema **1:1**. If anything goes wrong, an _exceptionally rich exception_
will get thrown with the faulty source line and positions.

The Template system in many ways resembles [Zod](https://zod.dev/), but tailored specifically for JSON and Java design patterns.

## Getting Started ⚙️

To invite your new _co-worker_ to your project, include the following lines in your build file:

```xml
<dependency>
    <groupId>com.manchickas</groupId>
    <artifactId>john</artifactId>
    <version>1.2.0</version>
</dependency>
```

> ```kts
> implementation("com.manchickas:john:1.2.0")
> ```

```java
public static void main(String[] args) {
    try {
        // Parse the source into a generic JSON structure.
        var json = John.parse("""
                {
                    "name": "Marie",
                    "age": 18,
                    "friends": [
                        {
                            "name": "Bob",
                            "age": 20
                        },
                        {
                            "name": "Alice",
                            "age": 19
                        }
                    ]
                }
                """);
        // Extract the 'name' property of the second friend using the generic 'STRING' template.
        String name = json.get("./friends[1]/name", Template.STRING);
        // Greet our dear Alice!
        System.out.printf("Hello, %s!%n", name);
    } catch (JsonException e) {
        // Print the message with ASCII formatting enabled.
        System.err.println(e.getMessage(true));
    }
}
```

## Path Your Way Through 👣

The John library has a **DSL** built for quick JSON navigation, called [Json Paths](./src/main/java/com/manchickas/john/path/JsonPath.java).
The syntax was specifically designed to strongly resemble _file-system paths_.

A JSON Path consists of multiple segments. Each segment is separated from the preceding one with a forward slash, and represents
a property in a JSON object. The path may start with a **dot** `./`, specifying it's relative to the current node.

> [!NOTE]
> Paths are **always relative** to the node they get resolved against.
> Including a dot at the very beginning of the path is purely a **stylistic choice**, unless you're pairing it with the _subscript operator_, or using it as a _root-path_.

A segment may be followed by a **subscript operator**, in form of `segment[0]`. The subscript operator expects
the segment to resolve to an array, and then accesses the element of that array at the provided index. Subscript operators
may be chained for multidimensional arrays.

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

[Templates](#templates) also benefit from this implicit source tracking, producing most error messages with the source attached.

```
"Hello!"
^^^^^^^^
(1:1-8) Expected a value that would satisfy the template of type '{ name: string, age: 18.0..65.0, friends?: >...[] }'
```


<a name="templates"></a>
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

## Capabilities ⚓

Although the Template system may seem restrictive because of its declarative nature, we assure you it's possible to model
most JSON structures using clever Template composition.

The example we've provided at the very top of the document showcases the simplest template usage to access a nested property
from a JSON object. We could, however, use more advanced templates to model the whole `Person` directly, including validation
and recursion.

```java
public record Person(String name, int age, Person[] friends) {

    public static final Template<Person> TEMPLATE = Template.record(
            Template.STRING.property("name", Person::name),
            Template.range(18, 65)
                    .requireWhole()
                    .asInteger()
                    .property("age", Person::age),
            Template.lazy(() -> Person.TEMPLATE)
                    .array(Person[]::new)
                    .property("friends", Person::friends)
                    .orElse(new Person[0]),
            Person::new
    );
}
```
