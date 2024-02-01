package com.tamdao.webapp.resources

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.tamdao.webapp.dao.GrantSubmissionDAO
import com.tamdao.webapp.dao.NonprofitDAO
import com.tamdao.webapp.entity.GrantSubmission
import com.tamdao.webapp.entity.GrantSubmissionRequest
import com.tamdao.webapp.entity.Nonprofit
import com.tamdao.webapp.entity.NonprofitRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Path("/api/v1/nonprofits")
@Produces(MediaType.APPLICATION_JSON)
class NonprofitResource(private val nonprofitDAO: NonprofitDAO, private val grantSubmissionDAO: GrantSubmissionDAO) {

    private val LOGGER: Logger = LoggerFactory.getLogger(NonprofitResource::class.java)


    @GET
    @Operation(summary = "Get a list of nonprofits", description = "Returns a list of nonprofits")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved list of nonprofits",
                content = [Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = Nonprofit::class))
                )]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )
    fun getNonprofits(): Response {
        LOGGER.info("Fetching nonprofits")
        try {
            val nonprofits = nonprofitDAO.find()
            LOGGER.info("Fetched nonprofits count - " + nonprofits.size)
            return Response.ok(nonprofits).build()
        } catch (e: Exception) {
            LOGGER.error("Error while fetching nonprofits", e)
            return Response.serverError().build()
        }
    }

    @GET
    @Path("/export")
    @Operation(summary = "Export a list of nonprofits", description = "Returns a csv of nonprofits")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved csv of nonprofits",
                content = [Content(
                    mediaType = "text/csv"
                )]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun exportNonprofits(): Response {
        LOGGER.info("Exporting nonprofits")
        try {
            val nonprofits = nonprofitDAO.find()
            LOGGER.info("Exported nonprofits count - " + nonprofits.size)

            val rows = mutableListOf(listOf("ID", "Legal Name", "EIN", "Mission", "Street", "City", "State", "Zip"))

            nonprofits.forEach { nonprofit ->
                rows.add(
                    listOf(
                        nonprofit.id.toString(),
                        nonprofit.legalName,
                        nonprofit.ein,
                        nonprofit.mission,
                        nonprofit.address.street,
                        nonprofit.address.city,
                        nonprofit.address.state,
                        nonprofit.address.zip
                    )
                )
            }

            val csvString = csvWriter().writeAllAsString(rows)

            return Response.ok(csvString)
                .type("text/csv")
                .header("Content-Disposition", "attachment; filename=\"nonprofits.csv\"")
                .build()
        } catch (e: Exception) {
            LOGGER.error("Error while exporting nonprofits", e)
            return Response.serverError().build()
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a nonprofit by ID", description = "Fetches a nonprofit entity by its ID")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved the nonprofit",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = Nonprofit::class))]
            ),
            ApiResponse(responseCode = "404", description = "Nonprofit not found"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun getNonprofit(@Parameter(description = "ID of the nonprofit to fetch") @PathParam("id") id: Int): Response {
        LOGGER.info("Fetching nonprofit {}", id)
        try {
            val nonprofit = nonprofitDAO.findById(id)
            return nonprofit?.let {
                Response.ok(it).build()
            } ?: Response.status(Response.Status.NOT_FOUND).entity("Nonprofit not found").build()
        } catch (e: Exception) {
            LOGGER.error("Error while fetching nonprofit", e)
            return Response.serverError().build()
        }
    }

    @POST
    @Operation(summary = "Add a new nonprofit", description = "Creates a new nonprofit entity")

    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully added the nonprofit",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = Nonprofit::class))]
            ),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun addNonprofit(
        @RequestBody(
            description = "Nonprofit object that needs to be added",
            required = true,
            content = [Content(schema = Schema(implementation = NonprofitRequest::class))]
        ) nonprofit: NonprofitRequest
    ): Response {
        LOGGER.info("Adding nonprofit {}", nonprofit)
        try {
            val id = nonprofitDAO.insert(
                Nonprofit(
                    0,
                    nonprofit.legalName,
                    nonprofit.ein,
                    nonprofit.mission,
                    nonprofit.address
                )
            )

            val createdNonprofit = nonprofitDAO.findById(id)

            return Response.ok(createdNonprofit).build()
        } catch (e: Exception) {
            LOGGER.error("Error while adding nonprofit", e)
            return Response.serverError().build()
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update an existing nonprofit", description = "Updates an existing nonprofit entity by its ID")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully updated the nonprofit",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = Nonprofit::class))]
            ),
            ApiResponse(responseCode = "404", description = "Nonprofit not found"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun updateNonprofit(
        @Parameter(description = "ID of the nonprofit to be updated", required = true)
        @PathParam("id") id: Int,
        @RequestBody(
            description = "Updated details of the nonprofit", required = true,
            content = [Content(schema = Schema(implementation = NonprofitRequest::class))]
        )
        nonprofit: NonprofitRequest
    ): Response {
        LOGGER.info("Updating nonprofit {}", nonprofit)
        try {
            val existing = nonprofitDAO.findById(id)
            if (existing === null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Nonprofit not found").build()
            }

            nonprofitDAO.update(Nonprofit(id, nonprofit.legalName, nonprofit.ein, nonprofit.mission, nonprofit.address))

            val updatedNonprofit = nonprofitDAO.findById(id)

            return Response.ok(updatedNonprofit).build()
        } catch (e: Exception) {
            LOGGER.error("Error while updating nonprofit", e)
            return Response.serverError().build()
        }
    }

    @GET
    @Path("/submissions")
    @Operation(summary = "Get all grant submissions", description = "Fetches all grant submissions")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved all grant submissions",
                content = [Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = GrantSubmission::class))
                )]
            ),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun getAllSubmissions(): Response {
        LOGGER.info("Fetching submissions")
        try {
            val submissions = grantSubmissionDAO.find()
            return Response.ok(submissions).build()
        } catch (e: Exception) {
            LOGGER.error("Error while fetching submission", e)
            return Response.serverError().build()
        }
    }

    @GET
    @Path("/submissions/export")
    @Operation(summary = "Export a list of grant submissions", description = "Returns a csv of grant submissions")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved csv of grant submissions",
                content = [Content(
                    mediaType = "text/csv"
                )]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun exportSubmissions(): Response {
        LOGGER.info("Exporting grant submissions")
        try {
            val submissions = grantSubmissionDAO.find()
            LOGGER.info("Exported grant submissions count - " + submissions.size)

            val rows = mutableListOf(
                listOf(
                    "ID",
                    "Grant Name",
                    "Requested Amount",
                    "Awarded Amount",
                    "Grant Type",
                    "Tags",
                    "Duration Start",
                    "Duration End"
                )
            )

            submissions.forEach { submission ->
                rows.add(
                    listOf(
                        submission.id.toString(),
                        submission.grantName,
                        submission.requestedAmount.toString(),
                        submission.awardedAmount.toString(),
                        submission.grantType.toString(),
                        submission.tags,
                        submission.duration.grantStart.toString(),
                        submission.duration.grantEnd.toString()
                    )
                )
            }

            val csvString = csvWriter().writeAllAsString(rows)

            return Response.ok(csvString)
                .type("text/csv")
                .header("Content-Disposition", "attachment; filename=\"grant_submissions.csv\"")
                .build()
        } catch (e: Exception) {
            LOGGER.error("Error while exporting nonprofits", e)
            return Response.serverError().build()
        }
    }

    @GET
    @Path("/{nonprofitId}/submissions")
    @Operation(
        summary = "Get all grant submissions by nonprofit ID",
        description = "Fetches all grant submissions for a given nonprofit"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved grant submissions",
                content = [Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = GrantSubmission::class))
                )]
            ),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun getSubmissions(
        @Parameter(
            description = "ID of the nonprofit to fetch submissions for",
            required = true
        ) @PathParam("nonprofitId") nonprofitId: Int
    ): Response {
        LOGGER.info("Fetching submission by nonprofit id {}", nonprofitId)
        try {
            val submissions = grantSubmissionDAO.findByNonprofitId(nonprofitId)
            return Response.ok(submissions).build()
        } catch (e: Exception) {
            LOGGER.error("Error while fetching submission", e)
            return Response.serverError().build()
        }
    }

    @GET
    @Path("/{nonprofitId}/submissions/{id}")
    @Operation(
        summary = "Get a specific grant submission",
        description = "Fetches a specific grant submission by its ID for a given nonprofit"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved the grant submission",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = GrantSubmission::class)
                )]
            ),
            ApiResponse(responseCode = "404", description = "Grant submission not found"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun getSubmission(
        @Parameter(description = "ID of the nonprofit", required = true) @PathParam("nonprofitId") nonprofitId: Int,
        @Parameter(description = "ID of the grant submission to fetch", required = true) @PathParam("id") id: Int
    ): Response {
        LOGGER.info("Fetching submission id {}", id)
        try {
            val submission = grantSubmissionDAO.findById(id)
            return submission?.let {
                Response.ok(it).build()
            } ?: Response.status(Response.Status.NOT_FOUND).entity("Submission not found").build()
        } catch (e: Exception) {
            LOGGER.error("Error while fetching submission", e)
            return Response.serverError().build()
        }
    }

    @POST
    @Path("/{nonprofitId}/submissions")
    @Operation(
        summary = "Add a new grant submission",
        description = "Creates a new grant submission for a given nonprofit"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully added the grant submission",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = GrantSubmission::class)
                )]
            ),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun addSubmission(
        @Parameter(
            description = "ID of the nonprofit for which the grant submission is added",
            required = true
        ) @PathParam("nonprofitId") nonprofitId: Int,
        @RequestBody(
            description = "Grant submission details",
            required = true,
            content = [Content(schema = Schema(implementation = GrantSubmissionRequest::class))]
        ) grantSubmission: GrantSubmissionRequest
    ): Response {
        LOGGER.info("Adding submission {}", grantSubmission)
        try {
            val id = grantSubmissionDAO.insert(
                GrantSubmission(
                    0, nonprofitId,
                    grantSubmission.grantName,
                    grantSubmission.requestedAmount,
                    grantSubmission.awardedAmount,
                    grantSubmission.grantType,
                    grantSubmission.tags,
                    grantSubmission.duration
                )
            )

            val createdGrantSubmission = grantSubmissionDAO.findById(id)

            return Response.ok(createdGrantSubmission).build()
        } catch (e: Exception) {
            LOGGER.error("Error while adding submission", e)
            return Response.serverError().build()
        }
    }

    @PUT
    @Path("/{nonprofitId}/submissions/{id}")
    @Operation(
        summary = "Update a grant submission",
        description = "Updates a grant submission for a given nonprofit by its ID"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully updated the grant submission",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = GrantSubmission::class)
                )]
            ),
            ApiResponse(responseCode = "404", description = "Grant submission not found"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun updateSubmission(
        @Parameter(description = "ID of the grant submission to be updated", required = true) @PathParam("id") id: Int,
        @Parameter(description = "ID of the nonprofit", required = true) @PathParam("nonprofitId") nonprofitId: Int,
        @RequestBody(
            description = "Updated details of the grant submission",
            required = true,
            content = [Content(schema = Schema(implementation = GrantSubmissionRequest::class))]
        ) grantSubmission: GrantSubmissionRequest
    ): Response {
        LOGGER.info("Updating submission {}", grantSubmission)
        try {
            val existing = grantSubmissionDAO.findById(id)
            if (existing === null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Submission not found").build()
            }

            grantSubmissionDAO.update(
                GrantSubmission(
                    id, nonprofitId,
                    grantSubmission.grantName,
                    grantSubmission.requestedAmount,
                    grantSubmission.awardedAmount,
                    grantSubmission.grantType,
                    grantSubmission.tags,
                    grantSubmission.duration
                )
            )

            val updatedGrantSubmission = grantSubmissionDAO.findById(id)

            return Response.ok(updatedGrantSubmission).build()
        } catch (e: Exception) {
            LOGGER.error("Error while updating submission", e)
            return Response.serverError().build()
        }
    }
}