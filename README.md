# John 👴

> Perhaps the most modern and declarative JSON library Java has to offer.

## Getting Started ⚙️

```xml
<dependency>
    <groupId>com.manchickas</groupId>
    <artifactId>john</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Templates 🗞️

One of the most distinctive features of the John library is **Templates**. Templates may be seen as a way to _scaffold your JSON_.

The John library uses templates **excessively**, so understanding them deeply is a **crucial step** in mastering John.
A single template has three key responsibilities:

1. It defines how to **parse** a specific JSON element.
2. It defines how to **serialize** a correctly typed value back into JSON.
3. It performs any additional validation while doing both of the above-mentioned operations.

> [!NOTE]
> You _almost never_ need to define your **own** templates. The standard library provides a handful of methods to compose
> them, which, when used correctly, almost completely eliminate the purpose in defining your own templates from scratch.

Templates are **stateless**. They define how to process the value, not the value to process. This pattern allows you to **re-use** existing templates
as much as you'd want to. Most templates should therefore be defined as `public static final` constants, rather than inlined into whatever method expects one.