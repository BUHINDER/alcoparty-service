package ru.buhinder.alcopartyservice.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import ru.buhinder.alcopartyservice.entity.InvitationLinkEntity
import java.util.UUID

interface InvitationLinkRepository : ReactiveCrudRepository<InvitationLinkEntity, UUID>
