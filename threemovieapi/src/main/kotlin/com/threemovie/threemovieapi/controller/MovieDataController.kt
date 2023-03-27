package com.threemovie.threemovieapi.controller

import com.threemovie.threemovieapi.Entity.DTO.MovieDetailDTO
import com.threemovie.threemovieapi.Entity.DTO.MovieListDTO
import com.threemovie.threemovieapi.service.impl.MovieDataControlServiceimpl
import com.threemovie.threemovieapi.service.impl.MovieInfoServiceimpl
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/movieinfo")
class MovieDataController(
	val movieInfoService: MovieInfoServiceimpl,
	val movieDataControlService: MovieDataControlServiceimpl,
) {

	@GetMapping()
	fun getMovieDataFromDaum() {
		movieDataControlService.truncateAllMovieData()
		movieDataControlService.GET_MOVIE_INFO_DAUM()
		movieDataControlService.GET_MOVIE_INFO_DAUM_for_upcoming()
	}

	@GetMapping("/movielist")
	fun getMovieList(): List<MovieListDTO> {
		return movieInfoService.getMovieList()
	}

	@GetMapping("/detail/{movieId}")
	fun getMovieDetail(@PathVariable movieId: String): MovieDetailDTO {
		return movieInfoService.getMovieDetail(movieId)
	}
}
