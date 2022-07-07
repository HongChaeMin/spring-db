package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * NamedParameterJdbcTemplate
 * SqlParameterSource
 * - BeanPropertySqlParameterSource
 * - MapSqlParameterSource
 * Map
 *
 * BeanPropertyRowMapper
 **/
@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcTemplateItemRepositoryV2 implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Item save(Item item) {
        String sql = "insert into item(item_name, price, quantity)"
            + "values (:itemName, :price, :quantity)";

        // 자바빈 프로퍼티 규약을 통해서 자동으로 파라미터 객체를 생성
        SqlParameterSource param = new BeanPropertySqlParameterSource(item);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);

        Long key = Objects.requireNonNull(keyHolder.getKey()).longValue();
        item.setId(key);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item "
            + "set item_name=:itemName, price=:price, quantity=:quantity "
            + "where id=:id";

        // Map 과 유사한데, SQL 타입을 지정할 수 있는 등 SQL에 좀 더 특화된 기능을 제공
        SqlParameterSource param = new MapSqlParameterSource()
            .addValue("itemName", updateParam.getItemName())
            .addValue("price", updateParam.getPrice())
            .addValue("quantity", updateParam.getQuantity())
            .addValue("id", itemId); // 이 부분이 별도로 필요하다
        jdbcTemplate.update(sql, param);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id=:id";

        try {
            // 단순히 Map을 사용한다
            Map<String, Object> param = Map.of("id", id);
            Item item = jdbcTemplate.queryForObject(sql, new Map[]{param}, itemRowMapper());
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        // 자바빈 프로퍼티 규약을 통해서 자동으로 파라미터 객체를 생성
        SqlParameterSource param = new BeanPropertySqlParameterSource(cond);

        String sql = "select id, item_name, price, quantity from item";

        //동적 쿼리
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }
        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%', :itemName, '%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= ?";
        }
        log.info("sql={}", sql);
        return jdbcTemplate.query(sql, new SqlParameterSource[]{param}, itemRowMapper());
    }

    private RowMapper<Item> itemRowMapper() {
        // ResultSet 의 결과를 받아서 자바빈 규약에 맞추어 데이터를 변환
        return BeanPropertyRowMapper.newInstance(Item.class); //camel 변환 지원
    }
}
