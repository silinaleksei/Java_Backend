package lesson4;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status",
        "username",
        "spoonacularPassword",
        "hash"
})
@Data
public class UsersConnectResponse {

    @JsonProperty("status")
    private String status;
    @JsonProperty("username")
    private String username;
    @JsonProperty("spoonacularPassword")
    private String spoonacularPassword;
    @JsonProperty("hash")
    private String hash;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();


}
