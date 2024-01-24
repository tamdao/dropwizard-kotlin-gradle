package com.tamdao.webapp

import com.fasterxml.jackson.databind.SerializationFeature
import com.tamdao.webapp.config.AppConfiguration
import com.tamdao.webapp.dao.GrantSubmissionDAO
import com.tamdao.webapp.dao.NonprofitDAO
import com.tamdao.webapp.resources.NonprofitResource
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.core.Application
import io.dropwizard.core.setup.Bootstrap
import io.dropwizard.core.setup.Environment
import io.dropwizard.jdbi3.JdbiFactory
import io.federecio.dropwizard.swagger.SwaggerBundle
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class MicroApplication : Application<AppConfiguration>() {
    private val LOGGER: Logger = LoggerFactory.getLogger(MicroApplication::class.java)

    override fun getName() = "dropwizard-kotlin-gradle"

    override fun initialize(bootstrap: Bootstrap<AppConfiguration?>) {
        bootstrap.configurationSourceProvider = SubstitutingSourceProvider(
            bootstrap.configurationSourceProvider,
            EnvironmentVariableSubstitutor(false)
        )

        bootstrap.addBundle(object : SwaggerBundle<AppConfiguration>() {
            override fun getSwaggerBundleConfiguration(conf: AppConfiguration): SwaggerBundleConfiguration {
                return conf.swagger
            }
        })
    }

    override fun run(conf: AppConfiguration, env: Environment) {
        env.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

        LOGGER.info("datasource connection {}", conf.dataSourceFactory.url)

        val datasource = conf.dataSourceFactory.build(env.metrics(), "database")

        val factory = JdbiFactory()
        val jdbi = factory.build(env, conf.dataSourceFactory, datasource, "database")

        conf.flywayFactory.build(datasource).migrate()

        val nonprofitDAO = jdbi.onDemand(NonprofitDAO::class.java)
        val grantSubmissionDAO = jdbi.onDemand(GrantSubmissionDAO::class.java)
        val nonprofitResource = NonprofitResource(nonprofitDAO, grantSubmissionDAO)

        env.jersey().register(nonprofitResource)
    }

}

fun main(args: Array<String>) = MicroApplication().run(*args)