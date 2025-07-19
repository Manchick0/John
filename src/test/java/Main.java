import com.manchickas.john.John;
import com.manchickas.john.exception.JsonException;
import com.manchickas.john.template.Template;

public class Main {

    public static void main(String[] args) {
        try {
            var person = new Person("Marie", 25);
            var json = John.serialize(person, Person.TEMPLATE);
            System.out.println(John.stringify(json, 4));
        } catch (JsonException e) {
            System.out.println(e.getMessage(true));
        }
    }

    public record Person(String name, int age, Person[] friends) {

        public static final Template<Person> TEMPLATE = Template.record(
                Template.STRING.property("name", Person::name),
                Template.range(18, 65).requireWhole()
                        .asInteger()
                        .property("age", Person::age),
                Template.lazy(() -> Person.TEMPLATE)
                        .array(Person[]::new)
                        .property("friends", Person::friends)
                        .orElse(new Person[0]),
                Person::new
        );
    }
}
