package upm.blockchain;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class ReadPlayerTest {

    @Mock
    Context context;
    @Mock
    ChaincodeStub chaincodeStub;

    UpmPoly upmPoly;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        upmPoly = new UpmPoly();
    }

    @Test
    void readPlayer() {
        when(context.getStub()).thenReturn(chaincodeStub);
        when(chaincodeStub.getStringState("1"))
                .thenReturn(new Player(1L, "name", 20.00).serialize());

        Player player = upmPoly.readPlayer(context, "1");

        assertThat(player).isEqualTo(new Player(1L, "name", 20.00));
    }

    @Test
    void readPlayer_noPlayers() {
        when(context.getStub()).thenReturn(chaincodeStub);
        when(chaincodeStub.getStringState("1"))
                .thenReturn("");

        assertThatThrownBy(() -> {
            upmPoly.readPlayer(context, "1");
        }).hasMessage("Player 1 does not exist");

    }

}