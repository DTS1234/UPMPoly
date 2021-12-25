package upm.blockchain;

import org.hyperledger.fabric.shim.ledger.KeyValue;

public class MockKeyValue implements KeyValue {

    private final String key;
    private final String value;

    MockKeyValue(final String key, final String value) {
        super();
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getStringValue() {
        return this.value;
    }

    @Override
    public byte[] getValue() {
        return this.value.getBytes();
    }

}
