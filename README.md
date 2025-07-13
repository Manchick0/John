Woah yeah what's that yet another JSON library?! As we didn't have enough by this point.

Regardless, this one's at least a bit different. Not just your normal reflection-based one. Nope, this one's
Java as fuck -- it forces you to write boilerplate. Pure awesomeness.

Jokes aside though, you gotta give it a try. We use user-defined templates for parsing and even have an own path DSL
for random JSON access. Not really random, but you get the point.

Anyways, here's some code:

```java
var src = """
        {
            name: "Marie",
            age: 18
        }
        """;
var json = John.parse(src);
var age = json.get("./name", Template.STRING);
var age = json.get("./age", Template.NUMBER);
```

Now wait for a better README.
