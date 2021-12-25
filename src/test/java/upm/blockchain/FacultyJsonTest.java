package upm.blockchain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FacultyJsonTest {

    @Test
    void testSerializeDeserialize() {
        final Faculty faculty = new Faculty(1L, "name",20.00, 100.00);
        final String serialize = faculty.serialize();

        final Faculty deserialize = Faculty.deserialize(serialize);
        assertThat(deserialize).isEqualTo(faculty);
        assertThat(deserialize.getOwnerNumber()).isNull();
    }



}
