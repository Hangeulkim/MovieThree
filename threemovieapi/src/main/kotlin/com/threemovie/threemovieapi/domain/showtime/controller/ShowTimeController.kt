package com.threemovie.threemovieapi.domain.showtime.controller

import com.threemovie.threemovieapi.domain.showtime.controller.request.FilterRequest
import com.threemovie.threemovieapi.domain.showtime.controller.response.ShowTheaterResponse
import com.threemovie.threemovieapi.domain.showtime.entity.dto.ShowDateDTO
import com.threemovie.threemovieapi.domain.showtime.entity.dto.ShowMovieDTO
import com.threemovie.threemovieapi.domain.showtime.entity.dto.ShowTimeItemDTO
import com.threemovie.threemovieapi.domain.showtime.service.ShowTimeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "showTime", description = "showTime 관련 컨트롤러")
@RestController
class ShowTimeController(val showTimeService: ShowTimeService) {
	@QueryMapping
	fun getMovieList(): List<ShowMovieDTO> = showTimeService.getMovieList()
	
	@QueryMapping
	fun getTheaterList(@Argument filter: FilterRequest): List<ShowTheaterResponse> =
		showTimeService.getTheaterList(filter)
	
	@QueryMapping
	fun getDateList(@Argument filter: FilterRequest): List<ShowDateDTO> = showTimeService.getDateList(filter)
	
	@QueryMapping
	fun getShowTimeList(@Argument filter: FilterRequest): List<ShowTimeItemDTO> {
		return showTimeService.getShowTimeList(filter)
	}
	
}
