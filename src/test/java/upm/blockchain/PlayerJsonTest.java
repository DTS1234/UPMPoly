package upm.blockchain;

import com.owlike.genson.Genson;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerJsonTest {

    @Test
    void testSerializeDeserialize() {
        final Player player = new Player(1L, "name", 2.00);
        final String serialize = player.serialize();

        final Player deserialize = new Player().deserialize(serialize);
        assertThat(deserialize).isEqualTo(player);
    }
}