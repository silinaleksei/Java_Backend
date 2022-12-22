package lesson4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "measures", "usages", "usageRecipeIds", "pantryItem", "aisle", "cost", "ingredientId"})
@Data
public class ResponseShoppingList {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("measures")
    private Measures measures;
    @JsonProperty("usages")
    private List<Object> usages = null;
    @JsonProperty("usageRecipeIds")
    private List<Object> usageRecipeIds = null;
    @JsonProperty("pantryItem")
    private Boolean pantryItem;
    @JsonProperty("aisle")
    private String aisle;
    @JsonProperty("cost")
    private Double cost;
    @JsonProperty("ingredientId")
    private Integer ingredientId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonPropertyOrder({"original", "metric", "us"})
    @Data
    public static class Measures {
        @JsonProperty("original")
        private Original original;
        @JsonProperty("metric")
        private Metric metric;
        @JsonProperty("us")
        private Us us;

        @JsonPropertyOrder({"amount", "unit"})
        @Data
        public static class Metric {
            @JsonProperty("amount")
            private Double amount;
            @JsonProperty("unit")
            private String unit;
        }

        @JsonPropertyOrder({"amount", "unit"})
        @Data
        public static class Original {
            @JsonProperty("amount")
            private Double amount;
            @JsonProperty("unit")
            private String unit;
        }

        @JsonPropertyOrder({"amount", "unit"})
        @Data
        public static class Us {
            @JsonProperty("amount")
            private Double amount;
            @JsonProperty("unit")
            private String unit;
        }
    }
}
