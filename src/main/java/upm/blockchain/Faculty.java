package upm.blockchain;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@DataType
public class Faculty {

    @Property
    private final Long facultyId;
    @Property
    private final String name;
    @Property
    private final Double rentalPrice;
    @Property
    private final Double salePrice;
    @Property
    private Long ownerNumber;

    public Faculty(Long facultyId, String name, Double rentalPrice, Double salePrice) {
        this.facultyId = facultyId;
        this.name = name;
        this.rentalPrice = rentalPrice;
        this.salePrice = salePrice;
        ownerNumber = null;
    }

    public void setOwner(Long ownerNumber){
        this.ownerNumber = ownerNumber;
    }

    public static Faculty deserialize(final byte[] jsonFaculty) {
        return deserialize(new String(jsonFaculty, StandardCharsets.UTF_8));
    }

    public static Faculty deserialize(String facultyJson) {
        JSONObject jsonObject = new JSONObject(facultyJson);
        Map<String, Object> map = jsonObject.toMap();
        final String name = (String) map.get("name");
        final Long facultyId = (Long) map.get("facultyId");
        final Double rentalPrice = (Double) map.get("rentalPrice");
        final Double salePrice = (Double) map.get("salePrice");
        return new Faculty(facultyId, name, rentalPrice, salePrice);
    }

    public String serialize() {
        Map<String, Object> tMap = new HashMap();
        tMap.put("name", name);
        tMap.put("salePrice",  Double.toString(salePrice));
        tMap.put("rentalPrice",  Double.toString(rentalPrice));
        tMap.put("owner",  ownerNumber == null ? "none" : String.valueOf(ownerNumber));
        return new JSONObject(tMap).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return Objects.equals(facultyId, faculty.facultyId) && Objects.equals(name, faculty.name) && Objects.equals(rentalPrice, faculty.rentalPrice) && Objects.equals(salePrice, faculty.salePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facultyId, name, rentalPrice, salePrice);
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "facultyId=" + facultyId +
                ", name='" + name + '\'' +
                ", rentalPrice=" + rentalPrice +
                ", salePrice=" + salePrice +
                '}';
    }

    public void setOwnerNumber(Long ownerNumber) {
        this.ownerNumber = ownerNumber;
    }

    public Long getFacultyId() {
        return facultyId;
    }

    public String getName() {
        return name;
    }

    public Double getRentalPrice() {
        return rentalPrice;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public Long getOwnerNumber() {
        return ownerNumber;
    }
}
