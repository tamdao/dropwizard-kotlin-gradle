package com.tamdao.webapp.mappers

import com.tamdao.webapp.entity.Duration
import com.tamdao.webapp.entity.GrantSubmission
import com.tamdao.webapp.entity.GrantType
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.time.ZoneId

class GrantSubmissionMapper : RowMapper<GrantSubmission> {
    override fun map(rs: ResultSet, ctx: StatementContext): GrantSubmission {
        val grantStart = rs.getTimestamp("duration_grant_start")?.toInstant()?.atZone(ZoneId.of("UTC"))?.toLocalDate()
        val grantEnd = rs.getTimestamp("duration_grant_end")?.toInstant()?.atZone(ZoneId.of("UTC"))?.toLocalDate()

        val toRet = GrantSubmission(
            rs.getInt("id"),
            rs.getInt("nonprofit_id"),
            rs.getString("grant_name"),
            rs.getLong("requested_amount"),
            rs.getLong("awarded_amount"),
            GrantType.valueOf(rs.getString("grant_type")),
            rs.getString("tags"),
            Duration(grantStart, grantEnd)
        )

        return toRet
    }
}
