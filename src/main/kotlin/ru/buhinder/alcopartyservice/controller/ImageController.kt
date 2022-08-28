package ru.buhinder.alcopartyservice.controller

import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.service.MinioService
import java.util.UUID

@RestController
@RequestMapping("/api/alcoparty/image")
class ImageController(
    private val minioService: MinioService,
) {

    @GetMapping("/{imageId}", produces = [IMAGE_JPEG_VALUE])
    fun getImage(@PathVariable imageId: UUID): Mono<ByteArray> {
        return minioService.getImage(imageId)
    }

}
