package ru.buhinder.alcopartyservice.service.validation

import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import ru.buhinder.alcopartyservice.controller.advice.exception.EntityCannotBeCreatedException

@Service
class ImageValidationService {

    fun validateImageFormat(images: List<FilePart>): Mono<Boolean> {
        return images.toFlux()
            .any {
                val contentType = it.headers().contentType
                contentType == null || contentType != MediaType.IMAGE_JPEG
            }
            .map { isNotJpeg ->
                if (isNotJpeg) {
                    throw EntityCannotBeCreatedException(
                        message = "Invalid media type. Only .jpeg files are allowed",
                        payload = emptyMap()
                    )
                }
                isNotJpeg
            }
    }

}
