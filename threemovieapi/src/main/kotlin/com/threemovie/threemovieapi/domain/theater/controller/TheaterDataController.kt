package com.threemovie.threemovieapi.domain.theater.controller

import com.threemovie.threemovieapi.domain.theater.entity.domain.TheaterData
import com.threemovie.threemovieapi.domain.theater.entity.dto.TheaterCityDTO
import com.threemovie.threemovieapi.domain.theater.service.TheaterDataService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "theater", description = "영화관 관련 api")
@RestController
@RequestMapping("/api/theater")
class TheaterDataController(val service: TheaterDataService) {
	@GetMapping("/{MovieTheater}")
	fun getTheaterData(@PathVariable MovieTheater: String): List<TheaterData> = service.getTheaterData(MovieTheater)

	@GetMapping("/main")
	fun getTheaterDataAll(): ResponseEntity<List<TheaterCityDTO>> = ResponseEntity.ok(service.getTheaterDataAll())
}
