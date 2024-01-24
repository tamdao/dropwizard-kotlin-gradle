package com.tamdao.webapp.dao

import com.tamdao.webapp.entity.Nonprofit
import com.tamdao.webapp.mappers.NonprofitMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.statement.UseRowMapper


interface NonprofitDAO {
    @SqlQuery("select * from nonprofits")
    @UseRowMapper(NonprofitMapper::class)
    fun find(): List<Nonprofit>

    @SqlQuery("select * from nonprofits where ID = :id")
    @UseRowMapper(NonprofitMapper::class)
    fun findById(@Bind("id") id: Int): Nonprofit?

    @SqlUpdate("delete from nonprofits where ID = :id")
    fun deleteById(@Bind("id") id: Int)

    @SqlUpdate(
        "insert into nonprofits (legal_name, EIN, mission, address_street, address_city, address_state, address_zip) " +
                "values (:legalName, :ein, :mission, :address.street, :address.city, :address.state, :address.zip)"
    )
    @GetGeneratedKeys
    fun insert(@BindBean nonprofit: Nonprofit): Int

    @SqlUpdate(
        "update nonprofits set legal_name = :legalName, EIN = :ein, mission = :mission, " +
                "address_street = :address.street, address_city = :address.city, address_state = :address.state, address_zip = :address.zip " +
                "where id = :id"
    )
    @GetGeneratedKeys
    fun update(@BindBean nonprofit: Nonprofit): Int
}