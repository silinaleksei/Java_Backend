package lesson4;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "results",
        "offset",
        "number",
        "totalResults"
})
@Data
public class SearchRecipesComplexSearchResponse {

    @JsonProperty("results")
    public List<Result> results = null;
    @JsonProperty("offset")
    public Integer offset;
    @JsonProperty("number")
    public Integer number;
    @JsonProperty("totalResults")
    public Integer totalResults;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "name",
            "amount",
            "unit"
    })
    @Data
    static
    class Nutrient {

        @JsonProperty("name")
        public String name;
        @JsonProperty("amount")
        public Double amount;
        @JsonProperty("unit")
        public String unit;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<>();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "nutrients"
    })
    @Data
    static
    class Nutrition {

        @JsonProperty("nutrients")
        public List<Nutrient> nutrients = null;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<>();

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "id",
            "title",
            "image",
            "imageType",
            "nutrition"
    })
    @Data
    static
    class Result {

        @JsonProperty("id")
        public Integer id;
        @JsonProperty("title")
        public String title;
        @JsonProperty("image")
        public String image;
        @JsonProperty("imageType")
        public String imageType;
        @JsonProperty("nutrition")
        public Nutrition nutrition;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<>();

    }


}