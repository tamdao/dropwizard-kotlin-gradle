package com.tamdao.webapp.dao

import com.tamdao.webapp.entity.GrantSubmission
import com.tamdao.webapp.mappers.GrantSubmissionMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.statement.UseRowMapper


interface GrantSubmissionDAO {
    @SqlQuery("select * from grant_submissions")
    @UseRowMapper(GrantSubmissionMapper::class)
    fun find(): List<GrantSubmission>

    @SqlQuery("select * from grant_submissions where nonprofit_id = :nonprofit_id")
    @UseRowMapper(GrantSubmissionMapper::class)
    fun findByNonprofitId(@Bind("nonprofit_id") nonprofitId: Int): List<GrantSubmission>

    @SqlQuery("select * from grant_submissions where ID = :id")
    @UseRowMapper(GrantSubmissionMapper::class)
    fun findById(@Bind("id") id: Int): GrantSubmission?

    @SqlUpdate("delete from grant_submissions where ID = :id")
    fun deleteById(@Bind("id") id: Int)


    @SqlUpdate(
        "insert into grant_submissions (nonprofit_id, grant_name, requested_amount, awarded_amount, grant_type, tags, duration_grant_start, duration_grant_end) " +
                "values (:nonprofitId, :grantName, :requestedAmount, :awardedAmount, cast(:grantType as grant_type), :tags, :duration.grantStart, :duration.grantEnd)"
    )
    @GetGeneratedKeys
    fun insert(@BindBean grantSubmission: GrantSubmission): Int


    @SqlUpdate(
        "update grant_submissions set grant_name = :grantName, requested_amount = :requestedAmount, awarded_amount = :awardedAmount, " +
                "grant_type = cast(:grantType as grant_type), tags = :tags, duration_grant_start = :duration.grantStart, duration_grant_end = :duration.grantEnd " +
                "where id = :id"
    )
    @GetGeneratedKeys
    fun update(@BindBean grantSubmission: GrantSubmission): Int
}