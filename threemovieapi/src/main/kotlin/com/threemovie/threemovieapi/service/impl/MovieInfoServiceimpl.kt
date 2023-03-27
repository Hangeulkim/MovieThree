package com.threemovie.threemovieapi.service.impl

import com.threemovie.threemovieapi.Entity.DTO.MovieDetailDTO
import com.threemovie.threemovieapi.Entity.DTO.MovieListDTO
import com.threemovie.threemovieapi.Entity.MovieInfo
import com.threemovie.threemovieapi.Repository.MovieInfoRepository
import com.threemovie.threemovieapi.Repository.Support.MovieInfoRepositorySupport
import com.threemovie.threemovieapi.Utils.GET_DATA_USE_DAUM_API.Companion.GET_DATA_USE_DAUM_API
import com.threemovie.threemovieapi.service.MovieInfoService
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.util.DoubleSummaryStatistics

@Service
class MovieInfoServiceimpl(
	val movieDataRepository: MovieInfoRepository,
	val movieInfoRepositorySupport: MovieInfoRepositorySupport,
) : MovieInfoService {
	override fun getMovieDetail(movieId: String): MovieDetailDTO {
		return movieInfoRepositorySupport.getMovieDetail(movieId)
	}

	override fun getMovieList(): List<MovieListDTO> {
		return movieInfoRepositorySupport.getMovieList()
	}

	override fun save_MovieData(One_movie_Info: JSONObject, url_Daum_Main: String) {
		val api_movie_data_screening = "api/movie/" + One_movie_Info.get("id").toString() + "/main"

		var tmp_data = GET_DATA_USE_DAUM_API(url_Daum_Main + api_movie_data_screening)
		val movie_data_json = JSONObject(JSONObject(tmp_data).get("movieCommon").toString())
		val movie_releaseDate =
			JSONObject(One_movie_Info.get("countryMovieInformation").toString()).get("releaseDate").toString()

		var netizenAvgRate:Double
		var reservationRate:Double
		if(!One_movie_Info.get("netizenAvgRate").equals(null)){
			netizenAvgRate = One_movie_Info.get("netizenAvgRate").toString().toDouble()
		} else {
			netizenAvgRate = 0.0
		}
		try {
			reservationRate = JSONObject(One_movie_Info.get("reservation").toString()).get("reservationRate").toString().toDouble()
		} catch (e: Exception){
			reservationRate = 0.0
		}


		var Poster: String?
		try {
			Poster = JSONObject(movie_data_json.get("mainPhoto").toString()).get("imageUrl").toString()
		} catch (e: Exception) {
			Poster = null
		}

		var NameKR: String?
		if(movie_data_json.get("titleKorean").equals(null)){
			NameKR = null
		} else {
			NameKR = movie_data_json.get("titleKorean").toString()
		}

		var NameEN: String?
		if(movie_data_json.get("titleEnglish").equals(null)){
			NameEN = null
		} else {
			NameEN = movie_data_json.get("titleEnglish").toString()
		}


		val member_MovieData = MovieInfo(
			One_movie_Info.get("titleKorean").toString() + "_" + movie_releaseDate,
			netizenAvgRate,
			reservationRate,
			movie_data_json.get("plot").toString(),
			NameKR,
			NameEN,
			movie_releaseDate,
			Poster,
			movie_data_json.get("genres").toString()
		)
		val res = movieDataRepository.save(member_MovieData)
	}

	override fun turncate_MovieData() {
		movieDataRepository.truncate()
	}
}
