package upm.blockchain;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class BuyFacultyTest {

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
    void buyFaculty_happy_path() {
        when(context.getStub()).thenReturn(chaincodeStub);
        Faculty faculty = new Faculty(100L, "name", 100.00, 120.00);
        Player buyer = new Player(1L, "buyer", 1000.00);

        when(chaincodeStub.getStringState("100")).thenReturn(faculty.serialize());
        when(chaincodeStub.getStringState("1")).thenReturn(buyer.serialize());

        upmPoly.buyFaculty(context, "1", "100");

        faculty.setOwner(1L);
        verify(chaincodeStub, times(1)).putStringState("100", faculty.serialize());

        Player updatedBuyer = new Player(1L, "buyer", 1000.00 - 120.00);
        verify(chaincodeStub, times(1)).putStringState("1", updatedBuyer.serialize());
    }
}
