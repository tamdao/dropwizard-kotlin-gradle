package com.tamdao.webapp.mappers

import com.tamdao.webapp.entity.Address
import com.tamdao.webapp.entity.Nonprofit
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class NonprofitMapper : RowMapper<Nonprofit> {
    override fun map(rs: ResultSet, ctx: StatementContext): Nonprofit {
        val toRet: Nonprofit = Nonprofit(
            rs.getInt("id"),
            rs.getString("legal_name"),
            rs.getString("EIN"),
            rs.getString("mission"),
            address = Address(
                rs.getString("address_street"),
                rs.getString("address_city"),
                rs.getString("address_state"),
                rs.getString("address_zip")
            )
        )

        return toRet
    }
}
