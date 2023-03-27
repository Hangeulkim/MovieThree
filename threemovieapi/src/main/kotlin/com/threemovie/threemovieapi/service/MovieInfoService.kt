package com.threemovie.threemovieapi.service

import com.threemovie.threemovieapi.Entity.DTO.MovieDetailDTO
import com.threemovie.threemovieapi.Entity.DTO.MovieListDTO
import org.json.JSONObject

interface MovieInfoService {
	fun getMovieList(): List<MovieListDTO>
	
	fun getMovieDetail(movieId: String): MovieDetailDTO

	fun save_MovieData(One_movie_Info: JSONObject, url_Daum_Main: String)

	fun turncate_MovieData()
}
