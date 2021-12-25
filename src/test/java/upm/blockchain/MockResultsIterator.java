package upm.blockchain;

import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MockResultsIterator implements QueryResultsIterator<KeyValue> {

    private final List<KeyValue> assetList;

    MockResultsIterator() {
        super();

        assetList = new ArrayList<KeyValue>();

        assetList.add(new MockKeyValue("asset1",
                "{ \"assetID\": \"asset1\", \"color\": \"blue\", \"size\": 5, \"owner\": \"Tomoko\", \"appraisedValue\": 300 }"));
        assetList.add(new MockKeyValue("asset2",
                "{ \"assetID\": \"asset2\", \"color\": \"red\", \"size\": 5,\"owner\": \"Brad\", \"appraisedValue\": 400 }"));
        assetList.add(new MockKeyValue("asset3",
                "{ \"assetID\": \"asset3\", \"color\": \"green\", \"size\": 10,\"owner\": \"Jin Soo\", \"appraisedValue\": 500 }"));
        assetList.add(new MockKeyValue("asset4",
                "{ \"assetID\": \"asset4\", \"color\": \"yellow\", \"size\": 10,\"owner\": \"Max\", \"appraisedValue\": 600 }"));
        assetList.add(new MockKeyValue("asset5",
                "{ \"assetID\": \"asset5\", \"color\": \"black\", \"size\": 15,\"owner\": \"Adrian\", \"appraisedValue\": 700 }"));
        assetList.add(new MockKeyValue("asset6",
                "{ \"assetID\": \"asset6\", \"color\": \"white\", \"size\": 15,\"owner\": \"Michel\", \"appraisedValue\": 800 }"));
    }

    @Override
    public Iterator<KeyValue> iterator() {
        return assetList.iterator();
    }

    @Override
    public void close() throws Exception {
        // do nothing
    }

}
