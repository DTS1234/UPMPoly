package upm.blockchain;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class ReadFacultyTest {

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
    void readFaculty() {

        when(context.getStub()).thenReturn(chaincodeStub);
        when(chaincodeStub.getStringState("1")).thenReturn(new Faculty(1L, "name", 20.00, 200.00).serialize());

        Faculty faculty = upmPoly.readFaculty(context, "1");

        assertThat(faculty).isEqualTo(new Faculty(1L, "name", 20.00, 200.00));
        assertThat(faculty.getOwnerNumber()).isNull();
    }

    @Test
    void readFaculty_doesNotExist() {

        when(context.getStub()).thenReturn(chaincodeStub);
        when(chaincodeStub.getStringState("1")).thenReturn(null);

        assertThatThrownBy(() -> {
            upmPoly.readFaculty(context, "1");
        }).hasMessage("Faculty 1 does not exist");

    }

}
