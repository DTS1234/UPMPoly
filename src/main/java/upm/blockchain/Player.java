package upm.blockchain;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@DataType
public final class Player {

    @Property
    private final Long playerNumber;
    @Property
    private final String name;
    @Property
    private final Double money;

    @Property
    private boolean isEliminated;

    public void setEliminated(boolean eliminated) {
        isEliminated = eliminated;
    }

    public Player(Long playerNumber, String name, Double money) {
        this.playerNumber = playerNumber;
        this.name = name;
        this.money = money;
        this.isEliminated = false;
    }

    public String serialize(final String privateProps) {
        Map<String, Object> jsonMap = new HashMap();
        jsonMap.put("playerNumber", playerNumber);
        jsonMap.put("name",  name);
        jsonMap.put("money",  Double.toString(money));
        if (privateProps != null && privateProps.length() > 0) {
            jsonMap.put("asset_properties", new JSONObject(privateProps));
        }
        return new JSONObject(jsonMap).toString();
    }

    public Player deserialize(final String assetJson) {
        JSONObject json = new JSONObject(assetJson);
        Map<String, Object> map = json.toMap();
        final Long playerNumberJson = (Long) map.get("playerNumber");
        final String nameJson = (String) map.get("name");
        final Double moneyJson = (Double) map.get("money");
        return new Player(playerNumberJson, nameJson, moneyJson);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(playerNumber, player.playerNumber) && Objects.equals(name, player.name) && Objects.equals(money, player.money);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerNumber, name, money);
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerNumber=" + playerNumber +
                ", name='" + name + '\'' +
                ", money=" + money +
                '}';
    }

    public Long getPlayerNumber() {
        return playerNumber;
    }

    public String getName() {
        return name;
    }

    public Double getMoney() {
        return money;
    }

    public boolean isEliminated() {
        return isEliminated;
    }
}
