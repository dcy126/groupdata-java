package com.groupdata.upload;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;


@Service
@Transactional
@Slf4j
public class UploadService {
    @Autowired
    private DataSource dataSource;

    @Transactional
    public void save(List<UploadData> list) throws SQLException{
        // 如果是mybatis,尽量别直接调用多次insert,自己写一个mapper里面新增一个方法batchInsert,所有数据一次性插入
        Connection connection = null;
        Statement statement = null;
        connection = this.dataSource.getConnection();
        statement = connection.createStatement();
        StringBuilder strSQL=new StringBuilder();
        strSQL.append("insert into t_base(\"DATE\",\"ZHU\",\"PLAYER_1\",\"ZHONG_1\",\"PLAYER_2\",\"ZHONG_2\",\"PLAYER_3\",\"FAN_1\",\"PLAYER_4\",\"FAN_2\",\"PLAYER_5\",\"FAN_3\",\"PLAYER_6\",\"FAN_4\",\"PLAYER_7\",\"NEI\",\"PLAYER_8\",\"WINNER\")\n");
        strSQL.append("values\n");
        for(UploadData data:list)
        {
           strSQL.append(String.format("('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',%d),\n",
                    data.getDATE(),data.getZHU(),data.getPLAYER_1(),data.getZHONG_1(),data.getPLAYER_2(),data.getZHONG_2(),data.getPLAYER_3(),
                    data.getFAN_1(),data.getPLAYER_4(),data.getFAN_2(),data.getPLAYER_5(),data.getFAN_3(),data.getPLAYER_6(),data.getFAN_4(),data.getPLAYER_7(),
                    data.getNEI(),data.getPLAYER_8(),data.getWINNER()));
        }
        strSQL.deleteCharAt(strSQL.length()-2);
        strSQL.append(";\n");
        statement.executeUpdate(strSQL.toString());
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Transactional
    public int getReadNum() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        connection = this.dataSource.getConnection();
        statement = connection.createStatement();
        StringBuilder strSQL=new StringBuilder();
        strSQL.append("select COUNT(*)\n");
        strSQL.append("from t_base\n");
        resultSet = statement.executeQuery(strSQL.toString());
        int num=0;
        while (resultSet.next()) {
            num = resultSet.getInt(1);
        }
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
        return num;
    }

}
