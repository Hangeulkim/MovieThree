package com.threemovie.threemovieapi.Service

import org.json.JSONObject

interface MoviePreviewService {
	fun save_MoviePreview(One_movie_Info: JSONObject, url_Daum_Main: String)
	fun turncate_MoviePreview()

}