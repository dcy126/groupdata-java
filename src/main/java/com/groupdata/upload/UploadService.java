package com.groupdata.upload;


import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UploadService {

    // Spring Boot 会自动配置并注入 JdbcTemplate
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 加上 rollbackFor = Exception.class 确保任何异常都会回滚事务
    @Transactional(rollbackFor = Exception.class)
    public void save(List<UploadData> list) {
        // 使用 ? 作为占位符，完美防止 SQL 注入
        String sql = "INSERT INTO t_base(\"DATE\",\"ZHU\",\"PLAYER_1\",\"ZHONG_1\",\"PLAYER_2\",\"ZHONG_2\",\"PLAYER_3\",\"FAN_1\",\"PLAYER_4\",\"FAN_2\",\"PLAYER_5\",\"FAN_3\",\"PLAYER_6\",\"FAN_4\",\"PLAYER_7\",\"NEI\",\"PLAYER_8\",\"WINNER\") " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        // 使用 JdbcTemplate 的批量插入功能
        jdbcTemplate.batchUpdate(sql, list, list.size(), (ps, data) -> {
            ps.setString(1, data.getDATE());
            ps.setString(2, data.getZHU());
            ps.setString(3, data.getPLAYER_1());
            ps.setString(4, data.getZHONG_1());
            ps.setString(5, data.getPLAYER_2());
            ps.setString(6, data.getZHONG_2());
            ps.setString(7, data.getPLAYER_3());
            ps.setString(8, data.getFAN_1());
            ps.setString(9, data.getPLAYER_4());
            ps.setString(10, data.getFAN_2());
            ps.setString(11, data.getPLAYER_5());
            ps.setString(12, data.getFAN_3());
            ps.setString(13, data.getPLAYER_6());
            ps.setString(14, data.getFAN_4());
            ps.setString(15, data.getPLAYER_7());
            ps.setString(16, data.getNEI());
            ps.setString(17, data.getPLAYER_8());
            // 假设 WINNER 是 Integer 类型
            if (data.getWINNER() != null) {
                ps.setInt(18, data.getWINNER());
            } else {
                ps.setNull(18, java.sql.Types.INTEGER);
            }
        });
    }

    public int getReadNum() {
        String sql = "SELECT COUNT(*) FROM t_base";
        // 一行代码搞定查询，不用手动管理连接和 ResultSet
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}
