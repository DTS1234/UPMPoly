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
    private  Long playerNumber;
    @Property
    private  String name;
    @Property
    private  Double money;

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

    public Player(Long playerNumber, String name, Double money, boolean isEliminated) {
        this.playerNumber = playerNumber;
        this.name = name;
        this.money = money;
        this.isEliminated = isEliminated;
    }

    public Player() {
    }

    public String serialize() {
        Map<String, Object> jsonMap = new HashMap();
        jsonMap.put("playerNumber", playerNumber);
        jsonMap.put("name",  name);
        jsonMap.put("money",  Double.toString(money));
        jsonMap.put("isEliminated",  Boolean.toString(isEliminated));
        return new JSONObject(jsonMap).toString();
    }

    public Player deserialize(final String assetJson) {
        JSONObject json = new JSONObject(assetJson);
        Map<String, Object> map = json.toMap();
        final Long playerNumberJson = Long.valueOf((Integer)map.get("playerNumber"));
        final String nameJson = (String) map.get("name");
        final Double moneyJson = Double.valueOf((String) map.get("money"));
        final boolean isEliminatedJson = Boolean.valueOf((String) map.get("isEliminated"));
        final Player player = new Player(playerNumberJson, nameJson, moneyJson);
        player.setEliminated(isEliminatedJson);
        return player;
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
