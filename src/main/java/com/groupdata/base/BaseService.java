package com.groupdata.base;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service // 注意：官方推荐业务层使用 @Service
@Transactional
@Slf4j
public class BaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JSONObject getBaseData() {
        String sql = "select \"DJH\",\"DATE\",\"WINNER\",\"ZHU\",\"PLAYER_1\",\"ZHONG_1\",\"PLAYER_2\",\"ZHONG_2\",\"PLAYER_3\",\"FAN_1\",\"PLAYER_4\",\"FAN_2\",\"PLAYER_5\",\"FAN_3\",\"PLAYER_6\",\"FAN_4\",\"PLAYER_7\",\"NEI\",\"PLAYER_8\" from t_base order by \"DJH\"";
        String[] tab = {"ZHU", "ZHONG1", "ZHONG2", "FAN1", "FAN2", "FAN3", "FAN4", "NEI"};

        List<JSONObject> res = jdbcTemplate.query(sql, (rs, rowNum) -> {
            JSONObject R = new JSONObject();
            R.put("id", rowNum);
            int i = 1;
            R.put("DJH", rs.getInt(i++));
            R.put("DATE", rs.getDate(i++));
            int winner = rs.getInt(i++);
            R.put("WINNER", winner);

            for (String j : tab) {
                JSONObject Z = new JSONObject();
                Z.put("ROLE", rs.getString(i++));
                Z.put("PLAYER", rs.getString(i++));
                String style = "";
                switch (j) {
                    case "ZHU":
                        style = winner == 0 ? "background: rgb(255, 204, 199);" : ""; break;
                    case "ZHONG1":
                    case "ZHONG2":
                        style = winner == 0 ? "background: rgb(255, 241, 184);" : ""; break;
                    case "FAN1":
                    case "FAN2":
                    case "FAN3":
                    case "FAN4":
                        style = winner == 1 ? "background: rgb(217, 247, 190);" : ""; break;
                    case "NEI":
                        style = winner == 2 ? "background: rgb(186, 231, 255);" : ""; break;
                }
                Z.put("STYLE", style);
                R.put(j, Z);
            }
            return R;
        });

        JSONObject obj = new JSONObject();
        obj.put("results", res);
        return obj;
    }

    public JSONObject getPlayerData() {
        return getPlayerData(false);
    }

    public JSONObject getPlayerData(boolean recent) {
        String[] tab = {"ZHU", "ZHONG", "FAN", "NEI", "QUAN"};
        double[] over = getOverView(recent);
        String sql = "select \"PLAYER\",\"WIN_ZHU\",\"TOTAL_ZHU\",\"WIN_ZHONG\",\"TOTAL_ZHONG\",\"WIN_FAN\",\"TOTAL_FAN\",\"WIN_NEI\",\"TOTAL_NEI\",\"WIN\",\"TOTAL\" " +
                "from " + (recent ? "v_r_player" : "v_player") + " order by \"TOTAL\" desc";

        List<JSONObject> res = jdbcTemplate.query(sql, (rs, rowNum) -> {
            JSONObject R = new JSONObject();
            R.put("id", rowNum);
            int i = 1;
            R.put("PLAYER", rs.getString(i++));
            for (int j = 0; j < 5; j++) {
                int win = rs.getInt(i++);
                int total = rs.getInt(i++);
                double percentage = total == 0 ? 0.0 : ((double) win) / total; // 防止除以0
                JSONObject Z = new JSONObject();
                Z.put("WIN", win);
                Z.put("TOTAL", total);
                Z.put("PERCENTAGE", percentage);
                Z.put("RED", percentage > over[j]);
                R.put(tab[j], Z);
            }
            R.put("TOTAL", rs.getInt(i - 1));
            return R;
        });

        JSONObject obj = new JSONObject();
        obj.put("results", res);
        return obj;
    }

    public JSONObject getRoleData() {
        return getRoleData(false);
    }

    public JSONObject getRoleData(boolean recent) {
        String[] tab = {"ZHU", "ZHONG", "FAN", "NEI", "QUAN"};
        String sql = "select \"ROLE\",\"WIN_ZHU\",\"TOTAL_ZHU\",\"WIN_ZHONG\",\"TOTAL_ZHONG\",\"WIN_FAN\",\"TOTAL_FAN\",\"WIN_NEI\",\"TOTAL_NEI\",\"WIN\",\"TOTAL\" " +
                "from " + (recent ? "v_r_role" : "v_role") + " order by \"TOTAL\" desc";

        List<JSONObject> res = jdbcTemplate.query(sql, (rs, rowNum) -> {
            JSONObject R = new JSONObject();
            R.put("id", rowNum);
            int i = 1;
            R.put("ROLE", rs.getString(i++));
            double weight = 0.0;
            int w = 0;
            for (String j : tab) {
                int win = rs.getInt(i++);
                int total = rs.getInt(i++);
                double percentage = total == 0 ? 0.0 : ((double) win) / total;
                if (!Double.isNaN(percentage)) {
                    switch (j) {
                        case "ZHU":
                        case "NEI":
                            weight += percentage; w += 1; break;
                        case "ZHONG":
                            weight += 2 * percentage; w += 2; break;
                        case "FAN":
                            weight += 4 * percentage; w += 4; break;
                    }
                }
                JSONObject Z = new JSONObject();
                Z.put("WIN", win);
                Z.put("TOTAL", total);
                Z.put("PERCENTAGE", percentage);
                R.put(j, Z);
            }
            R.put("WEIGHT", w == 0 ? 0 : weight / w);
            R.put("TOTAL", rs.getInt(i - 1));
            return R;
        });

        JSONObject obj = new JSONObject();
        obj.put("results", res);
        return obj;
    }

    public JSONObject getOverView() {
        String str = jdbcTemplate.queryForObject("select f_over()", String.class);
        return str != null ? JSONObject.parseObject(str) : new JSONObject();
    }

    // 删除了原有的静态方法 getOverView(DataSource)，不再需要传递 DataSource，
    // 其他服务可通过 @Autowired BaseService 直接调用下方的 getOverView(boolean) 方法。

    public List<JSONObject> getZHU(boolean recent) {
        String sql = "select \"ZHU\",\"TOTAL\",\"WIN\" from " + (recent ? "v_r_zhu" : "v_zhu") + " order by \"WIN\" desc";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            JSONObject R = new JSONObject();
            R.put("id", rowNum);
            R.put("ZHU", rs.getString(1));
            int total = rs.getInt(2);
            int win = rs.getInt(3);
            R.put("TOTAL", total);
            R.put("WIN", win);
            R.put("PERCENTAGE", total == 0 ? 0.0 : ((double) win) / total);
            return R;
        });
    }

    public double[] getOverView(boolean recent) {
        String sql = "select \"WINNER\",\"TOTAL\" from " + (recent ? "v_r_over" : "v_over");
        Map<String, Integer> overView = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            overView.put(rs.getString(1), rs.getInt(2));
        });

        int sum = overView.getOrDefault("总场次", 0);
        if (sum == 0) sum = 1;
        int zhu = overView.getOrDefault("主忠", 0);
        int fan = overView.getOrDefault("反贼", 0);
        int nei = overView.getOrDefault("内奸", 0);

        double[] over = new double[5];
        over[0] = over[1] = ((double) zhu) / sum;
        over[2] = ((double) fan) / sum;
        over[3] = ((double) nei) / sum;
        over[4] = ((double) zhu * 3 + fan * 4 + nei) / sum / 8;
        return over;
    }

    public JSONObject getPlayerList() {
        String sql = "select \"PLAYER\",\"WIN_ZHU\",\"TOTAL_ZHU\",\"WIN_ZHONG\",\"TOTAL_ZHONG\",\"WIN_FAN\",\"TOTAL_FAN\",\"WIN_NEI\",\"TOTAL_NEI\",\"WIN\",\"TOTAL\" from v_player order by \"TOTAL\" desc";
        List<JSONObject> res = jdbcTemplate.query(sql, (rs, rowNum) -> {
            JSONObject R = new JSONObject();
            R.put("value", rs.getString(1));
            return R;
        });
        JSONObject obj = new JSONObject();
        obj.put("results", res);
        return obj;
    }

    public JSONObject getRoleList() {
        String sql = "select \"ROLE\",\"WIN_ZHU\",\"TOTAL_ZHU\",\"WIN_ZHONG\",\"TOTAL_ZHONG\",\"WIN_FAN\",\"TOTAL_FAN\",\"WIN_NEI\",\"TOTAL_NEI\",\"WIN\",\"TOTAL\" from v_role order by \"TOTAL\" desc";
        List<JSONObject> res = jdbcTemplate.query(sql, (rs, rowNum) -> {
            JSONObject R = new JSONObject();
            R.put("value", rs.getString(1));
            return R;
        });
        JSONObject obj = new JSONObject();
        obj.put("results", res);
        return obj;
    }
}