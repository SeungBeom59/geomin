package com.geomin.demo.config;

import com.geomin.demo.domain.UserRole;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRoleTypeHandler extends EnumTypeHandler<UserRole> {

    public UserRoleTypeHandler(Class<UserRole> type) {
        super(type);
    }

    @Override
    public UserRole getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 데이터베이스에서 가져온 문자열을 UserRole enum 상수로 변환하여 반환합니다.
        String roleString = rs.getString(columnName);
        return UserRole.valueOf(roleString);
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, UserRole parameter, JdbcType jdbcType) throws SQLException {
        // 열거형 값을 데이터베이스 열에 매핑
        ps.setString(i, parameter.name());
    }

    @Override
    public UserRole getResult(ResultSet rs, String columnName) throws SQLException {
        // 데이터베이스에서 가져온 값을 열거형으로 변환
        return UserRole.valueOf(rs.getString(columnName));
    }

    @Override
    public UserRole getResult(ResultSet rs, int columnIndex) throws SQLException {
        // 데이터베이스에서 가져온 값을 열거형으로 변환
        return UserRole.valueOf(rs.getString(columnIndex));
    }

    @Override
    public UserRole getResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 데이터베이스에서 가져온 값을 열거형으로 변환
        return UserRole.valueOf(cs.getString(columnIndex));
    }
}
