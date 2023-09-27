package me.elgregos.reakteves.cli.generator.domain.event

import assertk.assertThat
import assertk.assertions.isEqualTo
import me.elgregos.reakteves.cli.generator.GeneratorTest
import kotlin.test.Test

internal class DomainEventGeneratorTest: GeneratorTest() {

    @Test
    fun `should generate domain event`() {
        testTemplateEngine.render(
            "domain/event/DomainEvent.jte",
            mapOf(domainPackageEntry, domainEntry, domainPrefixEntry),
            output
        )
        assertThat(output.toString()).isEqualTo("""
            package com.elgregos.escape.camp.game.domain.event

            import com.fasterxml.jackson.databind.JsonNode
            import me.elgregos.reakteves.domain.event.Event
            import me.elgregos.reakteves.libs.genericObjectMapper
            import me.elgregos.reakteves.libs.nowUTC
            import java.time.LocalDateTime
            import java.util.*

            sealed class GameEvent(
                id: UUID,
                sequenceNum: Long?,
                version: Int,
                createdAt: LocalDateTime,
                createdBy: UUID,
                aggregateId: UUID,
                eventType: String,
                event: JsonNode
            ) : Event<UUID, UUID>(
                id, sequenceNum, version, createdAt, createdBy, eventType, aggregateId, event
            ) {

                data class GameCreated(
                    override val id: UUID = UUID.randomUUID(),
                    override val sequenceNum: Long? = null,
                    override val version: Int = 1,
                    override val createdAt: LocalDateTime = nowUTC(),
                    override val createdBy: UUID,
                    val gameId: UUID,
                    override val event: JsonNode
                ) : GameEvent(
                    id,
                    sequenceNum,
                    version,
                    createdAt,
                    createdBy,
                    gameId,
                    GameCreated::class.simpleName!!,
                    event
                ) {
            
                    constructor(gameId: UUID, createdBy: UUID, createdAt: LocalDateTime) : this(
                        gameId = gameId,
                        createdAt = createdAt,
                        createdBy = createdBy,
                        event = genericObjectMapper.createObjectNode()
                            .put("id", "${'$'}gameId")
                            .put("createdAt", "${'$'}createdAt")
                            .put("createdBy", "${'$'}createdBy"))
                }
            }
        """.trimIndent())
    }
}