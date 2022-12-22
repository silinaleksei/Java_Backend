package lesson4;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonPropertyOrder({"item", "aisle", "parse"})
@JsonAutoDetect
@Data
public class RequestShoppingList {
    @JsonProperty("item")
    public String item;
    @JsonProperty("aisle")
    public String aisle;
    @JsonProperty("parse")
    public Boolean parse;
}
