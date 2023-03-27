package com.threemovie.threemovieapi.service.impl

import com.threemovie.threemovieapi.Entity.MoviePreview
import com.threemovie.threemovieapi.Repository.MoviePreviewRepository
import com.threemovie.threemovieapi.Utils.GET_DATA_USE_DAUM_API.Companion.GET_DATA_USE_DAUM_API
import com.threemovie.threemovieapi.service.MoviePreviewService
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class MoviePreviewServiceimpl(
    val MoviePreviewRepository: MoviePreviewRepository
): MoviePreviewService {
    override fun save_MoviePreview(One_movie_Info : JSONObject, url_Daum_Main: String){
        val api_steelcut_screening = "api/movie/" + One_movie_Info.get("id").toString() + "/photoList"

        var tmp_data_steelcut = GET_DATA_USE_DAUM_API(url_Daum_Main + api_steelcut_screening)
        val steelcut_Array = JSONObject(tmp_data_steelcut).getJSONArray("contents")
        val movie_releaseDate = JSONObject(One_movie_Info.get("countryMovieInformation").toString()).get("releaseDate").toString()

        val imageUrl_list: ArrayList<String> = arrayListOf<String>()
        var index: Int = 0
        for(One_steelcut in steelcut_Array){
            val imageUrl = JSONObject(One_steelcut.toString()).get("imageUrl")
            imageUrl_list.add(imageUrl.toString())
            if(index > 5) break

            index += 1
        }

        val api_preview_screening = "api/video/list/movie/" + One_movie_Info.get("id").toString() + "?page=1&size=20"

        var tmp_data_preview = GET_DATA_USE_DAUM_API(url_Daum_Main + api_preview_screening)
        val preview_Array = JSONObject(tmp_data_preview).getJSONArray("contents")

        val videoUrl_list: ArrayList<String> = arrayListOf<String>()
        for(One_preview in preview_Array){
            val One_preview_json = JSONObject(One_preview.toString())
            if(One_preview_json.get("title").toString().contains("예고편")){
                val videoUrl = One_preview_json.get("videoUrl")
                videoUrl_list.add(videoUrl.toString())
            }
        }
        val member_MoviePreview = MoviePreview(
            One_movie_Info.get("titleKorean").toString()+"_"+movie_releaseDate,
            imageUrl_list.toString(),
            videoUrl_list.toString()
        )

        val res = MoviePreviewRepository.save(member_MoviePreview)
    }

    override fun turncate_MoviePreview() {
        MoviePreviewRepository.truncate()
    }

}