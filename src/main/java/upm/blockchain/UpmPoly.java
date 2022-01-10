package upm.blockchain;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.List;

@Contract
@Default
public class UpmPoly implements ContractInterface {

    private final Genson genson = new Genson();

    private enum Errors {
        PLAYER_ALREADY_EXISTS,
        PLAYER_NOT_FOUND,
        PLAYER_ELIMINATED,
        FACULTY_NOT_FOUND,
        FACULTY_ALREADY_OWNED,
        PLAYER_NOT_ENOUGH_MONEY,
        FACULTY_ALREADY_EXISTS,
        FACULTY_NO_OWNER
    }

    /**
     * Initialization of ledger with faculties and players.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void init(final Context context) {
        ChaincodeStub stub = context.getStub();
        Player(context, String.valueOf(1L), "player 1", String.valueOf(500.00));
        Player(context, String.valueOf(2L), "player 2", String.valueOf(500.00));
        Player(context, String.valueOf(3L), "player 3", String.valueOf(500.00));
        Player(context, String.valueOf(4L), "player 4", String.valueOf(500.00));
        Faculty(context, String.valueOf(1L), "Industrial Engineering", String.valueOf(25.00), String.valueOf(125.00));
        Faculty(context, String.valueOf(1L), "Mathematics", String.valueOf(50.00), String.valueOf(150.00));
        Faculty(context, String.valueOf(2L), "Physics", String.valueOf(75.00), String.valueOf(175.00));
        Faculty(context, String.valueOf(3L), "Economy", String.valueOf(100.00), String.valueOf(200.00));
        Faculty(context, String.valueOf(4L), "Business", String.valueOf(125.00), String.valueOf(225.00));
        Faculty(context, String.valueOf(5L), "Mechanics", String.valueOf(150.00), String.valueOf(250.00));
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void buyFaculty(final Context context, String playerNumber, final String facultyId) {

        if (!playerExists(context, Long.valueOf(playerNumber))) {
            throw new ChaincodeException(String.format("Player %s does not exist", playerNumber), Errors.PLAYER_NOT_FOUND.toString());
        }
        if (!facultyExists(context, String.valueOf(facultyId))) {
            throw new ChaincodeException(String.format("Faculty %s does not exist", facultyId), Errors.FACULTY_NOT_FOUND.toString());
        }

        final Faculty faculty = readFaculty(context, String.valueOf(facultyId));
        if (faculty.getOwnerNumber() != null) {
            throw new ChaincodeException(String.format("Faculty already owned by player with id %s", playerNumber), Errors.FACULTY_ALREADY_OWNED.toString());
        }

        final Player player = readPlayer(context, playerNumber);
        if (player.isEliminated()) {
            throw new ChaincodeException(String.format("Player %s is eliminated !", playerNumber), Errors.PLAYER_ELIMINATED.toString());
        }

        final Double playerBalance = player.getMoney();
        if (playerBalance < faculty.getSalePrice()) {
            throw new ChaincodeException("Player does not have enough money to buy this faculty!", Errors.PLAYER_NOT_ENOUGH_MONEY.toString());
        }

        ChaincodeStub stub = context.getStub();

        // update faculty owner
        faculty.setOwner(Long.valueOf(playerNumber));
        stub.putStringState(String.valueOf(facultyId), faculty.serialize());
        // update player balance
        final Player updatedPlayer = new Player(Long.valueOf(playerNumber), player.getName(), playerBalance - faculty.getSalePrice());
        stub.putStringState(playerNumber, updatedPlayer.serialize());

        System.out.println("Faculty with id " + facultyId + " is now owned by player with id : " + playerNumber);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void payRental(final Context context, String visitorNumber, String facultyId) {

        final Faculty faculty = readFaculty(context, facultyId);
        final Long ownerNumber = faculty.getOwnerNumber();

        if (!playerExists(context, Long.valueOf(visitorNumber))) {
            throw new ChaincodeException(String.format("Player %s does not exist", visitorNumber), Errors.PLAYER_NOT_FOUND.toString());
        }

        if (!facultyExists(context, String.valueOf(facultyId))) {
            throw new ChaincodeException(String.format("Faculty %s does not exist", facultyId), Errors.FACULTY_NOT_FOUND.toString());
        }

        if (ownerNumber == null) {
            throw new ChaincodeException(String.format("Faculty %s does not have an owner", facultyId), Errors.FACULTY_NO_OWNER.toString());
        }

        final Player visitor = readPlayer(context, visitorNumber);
        if (visitor.isEliminated()) {
            throw new ChaincodeException(String.format("Player %s is eliminated !", visitorNumber), Errors.PLAYER_ELIMINATED.toString());
        }

        final ChaincodeStub stub = context.getStub();

        if (visitor.getMoney() < faculty.getRentalPrice()) {

            // eliminate the player
            Double restOfTheMoney = visitor.getMoney();
            Player eliminatedVisitor = new Player(visitor.getPlayerNumber(), visitor.getName(), 0.00, true);
            stub.putStringState(visitorNumber, eliminatedVisitor.serialize());
            // set free visitors faculties
            for (int i = 1; i <= 4; i++) {
                Faculty currentFaculty = readFaculty(context, String.valueOf(i));
                if (currentFaculty.getOwnerNumber().equals(Long.valueOf(visitorNumber))) {
                    Faculty eliminatedFaculty = new Faculty(currentFaculty.getFacultyId(), currentFaculty.getName(), 0.00, 0.00);
                    eliminatedFaculty.setOwnerNumber(eliminatedVisitor.getPlayerNumber());
                    stub.putStringState(String.valueOf(eliminatedFaculty.getFacultyId()), eliminatedFaculty.serialize());
                }
            }


            // send rest of the visitor's money to faculty owner
            final Player facultyOwner = readPlayer(context, String.valueOf(ownerNumber));
            final Player updatedOwner = new Player(ownerNumber, facultyOwner.getName(), facultyOwner.getMoney() + restOfTheMoney);
            stub.putStringState(String.valueOf(ownerNumber), updatedOwner.serialize());

        } else {
            // update faculty owner's balance
            final Player facultyOwner = readPlayer(context, String.valueOf(ownerNumber));
            if (facultyOwner.isEliminated()) {
                throw new ChaincodeException(String.format("Player %s is eliminated !", facultyOwner.getPlayerNumber()), Errors.PLAYER_ELIMINATED.toString());
            }
            final Player updatedOwner = new Player(ownerNumber, facultyOwner.getName(), facultyOwner.getMoney() + faculty.getRentalPrice());
            stub.putStringState(String.valueOf(ownerNumber), updatedOwner.serialize());

            // update visitor's balance
            final double newVisitorBalance = visitor.getMoney() - faculty.getRentalPrice();
            final Player updatedVisitor = new Player(Long.valueOf(visitorNumber), visitor.getName(), newVisitorBalance);
            stub.putStringState(visitorNumber, updatedVisitor.serialize());
        }

    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void tradeFaculty(Context context, Double negotiatedPrice, String facultyId, String newOwnerId) {

        if (!playerExists(context, Long.valueOf(newOwnerId))) {
            String errorMessage = String.format("Player %s does not exist", newOwnerId);
            throw new ChaincodeException(errorMessage, Errors.PLAYER_NOT_FOUND.toString());
        }

        if (!facultyExists(context, String.valueOf(facultyId))) {
            throw new ChaincodeException(String.format("Faculty %s does not exist", facultyId), Errors.FACULTY_NOT_FOUND.toString());
        }

        final Faculty faculty = readFaculty(context, facultyId);
        final Long ownerNumber = faculty.getOwnerNumber();

        if (ownerNumber == null) {
            throw new ChaincodeException(String.format("Faculty %s does not have an owner", facultyId), Errors.FACULTY_NO_OWNER.toString());
        }

        final Player buyer = readPlayer(context, newOwnerId);
        if (buyer.getMoney() < negotiatedPrice) {
            throw new ChaincodeException(String.format("Player %s does not have enough money!", newOwnerId), Errors.PLAYER_NOT_ENOUGH_MONEY.toString());
        }

        if (buyer.isEliminated()) {
            throw new ChaincodeException(String.format("Player %s is eliminated !", newOwnerId), Errors.PLAYER_ELIMINATED.toString());
        }

        final Player seller = readPlayer(context, String.valueOf(faculty.getOwnerNumber()));
        if (seller.isEliminated()) {
            throw new ChaincodeException(String.format("Player %s is eliminated !", newOwnerId), Errors.PLAYER_ELIMINATED.toString());
        }

        final ChaincodeStub stub = context.getStub();

        // update buyer balance
        final Player updatedBuyer = new Player(Long.valueOf(newOwnerId), buyer.getName(), buyer.getMoney() - negotiatedPrice);
        stub.putStringState(newOwnerId, updatedBuyer.serialize());

        // update seller balance
        final Player updatedSeller = new Player(faculty.getOwnerNumber(), seller.getName(), seller.getMoney() + negotiatedPrice);
        stub.putStringState(String.valueOf(faculty.getOwnerNumber()), updatedSeller.serialize());

        // update faculty's owner
        final Faculty updatedFaculty = new Faculty(Long.valueOf(facultyId), faculty.getName(), faculty.getRentalPrice(), faculty.getSalePrice());
        updatedFaculty.setOwnerNumber(Long.valueOf(newOwnerId));
        stub.putStringState(facultyId, updatedFaculty.serialize());
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Player readPlayer(final Context ctx, final String playerNumber) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(playerNumber);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Player %s does not exist", playerNumber);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, Errors.PLAYER_NOT_FOUND.toString());
        }

        return new Player().deserialize(assetJSON);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Faculty readFaculty(final Context ctx, final String facultyId) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(facultyId);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Faculty %s does not exist", facultyId);
            throw new ChaincodeException(errorMessage, Errors.FACULTY_NOT_FOUND.toString());
        }

        return Faculty.deserialize(assetJSON);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Player Player(final Context context, final String playerNumber, final String name, final String money) {

        if (playerExists(context, Long.valueOf(playerNumber))) {
            String errorMessage = String.format("Player %s already exists", playerNumber);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, Errors.PLAYER_ALREADY_EXISTS.toString());
        }

        final Player player = new Player(Long.valueOf(playerNumber), name, Double.valueOf(money));
        final String playerJson = player.serialize();
        context.getStub().putStringState(playerNumber, playerJson);

        return player;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Faculty Faculty(final Context context, final String facultyId, final String name, final String rentalPrice, final String salePrice) {

        if (facultyExists(context, String.valueOf(facultyId))) {
            String errorMessage = String.format("Faculty %s already exists", facultyId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, Errors.FACULTY_ALREADY_EXISTS.toString());
        }

        Faculty faculty = new Faculty(Long.valueOf(facultyId), name, Double.valueOf(rentalPrice), Double.valueOf(salePrice));
        final String facultyJson = faculty.serialize();
        context.getStub().putStringState(facultyId, facultyJson);

        return faculty;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Long getOwner(Context context, String facultyId) {
        return readFaculty(context, facultyId).getOwnerNumber();
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Double getMoney(Context context, String playerNumber) {
        return readPlayer(context, playerNumber).getMoney();
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isEliminated(Context context, String playerNumber) {
        return readPlayer(context, playerNumber).isEliminated();
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getPlayers(final Context ctx) throws Exception {
        ChaincodeStub stub = ctx.getStub();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");
        List<Player> players = new ArrayList<>();

        for (KeyValue result: results) {
            try {
                Player player = new Player().deserialize(result.getStringValue());
                players.add(player);
            } catch (Exception e) {
                System.out.println("Tried to read faculty, but we only need players, continue.");
            }
        }

        results.close();

        return genson.serialize(players);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean playerExists(final Context ctx, final Long playerNumber) {
        ChaincodeStub stub = ctx.getStub();
        String playerJson = stub.getStringState(playerNumber.toString());
        return (playerJson != null && !playerJson.isEmpty());
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean facultyExists(final Context ctx, final String facultyId) {
        ChaincodeStub stub = ctx.getStub();
        String facultyJson = stub.getStringState(facultyId);
        return (facultyJson != null && !facultyJson.isEmpty());
    }

}
