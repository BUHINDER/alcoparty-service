package ru.buhinder.alcopartyservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterRegistry

@Configuration
class ConvertersConfig {

    @Bean
    fun configureConverters(
        converterRegistry: ConverterRegistry,
        converters: List<Converter<*, *>>,
    ): ConverterRegistry {
        converters.forEach { converterRegistry.addConverter(it) }
        return converterRegistry
    }

}
