package com.threemovie.threemovieapi.controller

import com.threemovie.threemovieapi.Entity.ShowTime
import com.threemovie.threemovieapi.service.impl.ShowTimeServiceImpl
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/showtimes")
class ShowTimeController(val service: ShowTimeServiceImpl) {
	@GetMapping
	fun getShowTimeAll(): List<ShowTime> = service.getShowTimeAll()

	@GetMapping("/{MovieTheater}")
	fun getShowTime(@PathVariable MovieTheater: String): List<ShowTime> = service.getShowTime(MovieTheater)
}
