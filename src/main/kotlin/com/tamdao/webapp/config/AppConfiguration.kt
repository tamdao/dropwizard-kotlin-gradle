package com.tamdao.webapp.config

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.core.Configuration
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.flyway.FlywayFactory
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

class AppConfiguration : Configuration() {
    @NotNull
    @Valid
    @JsonProperty("database")
    val dataSourceFactory: DataSourceFactory = DataSourceFactory()

    @NotNull
    @Valid
    @JsonProperty("flyway")
    val flywayFactory: FlywayFactory = FlywayFactory()

    @Valid
    @NotNull
    @JsonProperty("swagger")
    val swagger: SwaggerBundleConfiguration = SwaggerBundleConfiguration()
}