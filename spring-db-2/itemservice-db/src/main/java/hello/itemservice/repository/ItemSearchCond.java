package hello.itemservice.repository;

import lombok.Data;

@Data
public class ItemSearchCond { // 검색 조건으로 사용

    private String itemName;
    private Integer maxPrice;

    public ItemSearchCond() {
    }

    public ItemSearchCond(String itemName, Integer maxPrice) {
        this.itemName = itemName;
        this.maxPrice = maxPrice;
    }
}
