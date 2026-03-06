package com.groupdata.player;

import com.alibaba.fastjson.JSONObject;
import com.groupdata.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class PlayerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BaseService baseService; // 注入 BaseService 获取 overview 数据

    public JSONObject getRoleData(String c_player) {
        String[] tab = {"ZHU", "ZHONG", "FAN", "NEI", "QUAN"};
        String sql = "select \"ROLE\",\"WIN_ZHU\",\"TOTAL_ZHU\",\"WIN_ZHONG\",\"TOTAL_ZHONG\",\"WIN_FAN\",\"TOTAL_FAN\",\"WIN_NEI\",\"TOTAL_NEI\",\"WIN\",\"TOTAL\" " +
                "from f_f_role(?) order by \"TOTAL\" desc";

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
        }, c_player);

        JSONObject obj = new JSONObject();
        obj.put("results", res);
        return obj;
    }

    public JSONObject getPlayerData(String c_role) {
        String[] tab = {"ZHU", "ZHONG", "FAN", "NEI", "QUAN"};
        double[] over = baseService.getOverView(false);

        String sql = "select \"PLAYER\",\"WIN_ZHU\",\"TOTAL_ZHU\",\"WIN_ZHONG\",\"TOTAL_ZHONG\",\"WIN_FAN\",\"TOTAL_FAN\",\"WIN_NEI\",\"TOTAL_NEI\",\"WIN\",\"TOTAL\" " +
                "from f_f_player(?) order by \"TOTAL\" desc";

        List<JSONObject> res = jdbcTemplate.query(sql, (rs, rowNum) -> {
            JSONObject R = new JSONObject();
            R.put("id", rowNum);
            int i = 1;
            R.put("PLAYER", rs.getString(i++));
            for (int j = 0; j < 5; j++) {
                int win = rs.getInt(i++);
                int total = rs.getInt(i++);
                double percentage = total == 0 ? 0.0 : ((double) win) / total;
                JSONObject Z = new JSONObject();
                Z.put("WIN", win);
                Z.put("TOTAL", total);
                Z.put("PERCENTAGE", percentage);
                Z.put("RED", percentage > over[j]);
                R.put(tab[j], Z);
            }
            R.put("TOTAL", rs.getInt(i - 1));
            return R;
        }, c_role);

        JSONObject obj = new JSONObject();
        obj.put("results", res);
        return obj;
    }

    public JSONObject getPlayerRank() {
        String[] tab = {"ZHU", "ZHONG", "FAN", "NEI", "QUAN"};
        double[] over = baseService.getOverView(false);

        // 注意：原代码此处查询了 t_base，但取值时使用了类似 v_player 视图的循环。
        // 为了确保代码安全执行暂不抛弃原逻辑，直接平移。
        String sql = "select \"DJH\",\"DATE\",\"PLAYER_1\",\"PLAYER_2\",\"PLAYER_3\",\"PLAYER_4\",\"PLAYER_5\",\"PLAYER_6\",\"PLAYER_7\",\"PLAYER_8\",\"WIN\" " +
                "from t_base order by \"DATE\"";

        List<JSONObject> res = jdbcTemplate.query(sql, (rs, rowNum) -> {
            JSONObject R = new JSONObject();
            R.put("id", rowNum);
            int i = 1;
            R.put("PLAYER", rs.getString(i++));
            for (int j = 0; j < 5; j++) {
                int win = rs.getInt(i++);
                int total = rs.getInt(i++);
                double percentage = total == 0 ? 0.0 : ((double) win) / total;
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

    public JSONObject getRoleZhu(String c_role) {
        String[] tab = {"ZHONG", "FAN", "NEI", "QUAN"};
        double[] over = baseService.getOverView(false);

        String sql = "select \"ROLE\",\"WIN_ZHONG\",\"TOTAL_ZHONG\",\"WIN_FAN\",\"TOTAL_FAN\",\"WIN_NEI\",\"TOTAL_NEI\",\"WIN\",\"TOTAL\" " +
                "from f_zhu_role(?) order by \"TOTAL\"";

        List<JSONObject> res = jdbcTemplate.query(sql, (rs, rowNum) -> {
            JSONObject R = new JSONObject();
            R.put("id", rowNum);
            int i = 1;
            R.put("ROLE", rs.getString(i++));
            for (int j = 0; j < 4; j++) {
                int win = rs.getInt(i++);
                int total = rs.getInt(i++);
                double percentage = total == 0 ? 0.0 : ((double) win) / total;
                JSONObject Z = new JSONObject();
                Z.put("WIN", win);
                Z.put("TOTAL", total);
                Z.put("PERCENTAGE", percentage);
                Z.put("RED", percentage > over[j]);
                R.put(tab[j], Z);
            }
            R.put("TOTAL", rs.getInt(i - 1));
            return R;
        }, c_role);

        JSONObject obj = new JSONObject();
        obj.put("results", res);
        return obj;
    }

    public JSONObject getRoleCompare(String c_role_1, String c_role_2) {
        String sql = "select \"DESCRIBE\",\"COUNT\" from f_compare_role(?, ?) order by \"DESCRIBE\"";
        return executeCompareQuery(sql, c_role_1, c_role_2);
    }

    public JSONObject getPlayerCompare(String c_player_1, String c_player_2) {
        String sql = "select \"DESCRIBE\",\"COUNT\" from f_compare(?, ?) order by \"DESCRIBE\"";
        return executeCompareQuery(sql, c_player_1, c_player_2);
    }

    // 将两个 Compare 方法重复的解析逻辑提取成公共方法
    private JSONObject executeCompareQuery(String sql, String arg1, String arg2) {
        String[] tab = {"", "ROLE1_WIN", "ROLE2_WIN", "TWO_WIN", ""};
        JSONObject R = new JSONObject();
        int[] sums = new int[2]; // sums[0]为butong，sums[1]为tong
        int[] index = new int[1];

        jdbcTemplate.query(sql, rs -> {
            int count = rs.getInt(2);
            int i = index[0];

            if (i < 3) sums[0] += count;
            else if (i < 5) sums[1] += count;

            if (i < tab.length && !tab[i].isEmpty()) {
                R.put(tab[i], count);
            }
            index[0]++;
        }, arg1, arg2);

        R.put("SUM_BUTONG", sums[0]);
        R.put("SUM_TONG", sums[1]);
        return R;
    }
}