package ru.buhinder.alcopartyservice.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import ru.buhinder.alcopartyservice.dto.InvitationLinkResponse
import ru.buhinder.alcopartyservice.entity.InvitationLinkEntity

@Component
class InvitationLinkEntityToInvitationLink : Converter<InvitationLinkEntity, InvitationLinkResponse> {

    override fun convert(source: InvitationLinkEntity): InvitationLinkResponse {

        return InvitationLinkResponse(
            id = source.id!!,
            usageAmount = source.usageAmount,
            expiresAt = source.expiresAt
        )
    }
}