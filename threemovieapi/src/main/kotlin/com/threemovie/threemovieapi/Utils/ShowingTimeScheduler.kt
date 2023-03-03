package com.threemovie.threemovieapi.Utils

import com.threemovie.threemovieapi.Repository.ShowTimeRepository
import com.threemovie.threemovieapi.Repository.Support.ShowTimeRepositorySupport
import com.threemovie.threemovieapi.Repository.Support.UpdateTimeRepositorySupport
import com.threemovie.threemovieapi.Repository.UpdateTimeRepository
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.util.regex.Matcher
import java.util.regex.Pattern

@Component
class ShowingTimeScheduler(
	val updateTimeRepository: UpdateTimeRepository,
	val updateTimeRepositorySupport: UpdateTimeRepositorySupport,
	val showTimeRepository: ShowTimeRepository,
	val showTimeRepositorySupport: ShowTimeRepositorySupport
) {
	val userAgent: String =
		"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36"
	val CGVurl = "http://www.cgv.co.kr"
	val LCurl = "http://www.lottecinema.co.kr"

//	@Async
//	@Scheduled(cron = "0/60 * * * * ?")
//	fun ChkMovieShowingTime() {
//		if (ChkNeedUpdate.chkMovieShowingTime(updateTimeRepositorySupport.getMovieShowingTime().toLong())) {
//			updateCGVShowingTime()
//			updateMegaBOXShowingTime()
//			updateLotteCinemaShowingTime()
//		}
//	}

	fun getLotteCinemaTheaterData(): ArrayList<HashMap<String, Any>> {
		val theaterlist = ArrayList<HashMap<String, Any>>()
		val url: String =
			LCurl + "/LCWS/Cinema/CinemaData.aspx"

		var paramlist = HashMap<String, Any>()
		paramlist.put("MethodName", "GetCinemaItems")
		paramlist.put("channelType", "HO")
		paramlist.put("osType", "W")
		paramlist.put("osVersion", userAgent)
		val conn = Jsoup.connect(url)
			.userAgent(userAgent)
			.data("ParamList", JSONObject(paramlist).toString())
		val doc = conn.post().body().text()
		val data = JSONObject(doc)

		val cinemas = data.getJSONObject("Cinemas").getJSONArray("Items")

		for (i in 0 until cinemas.length()) {
			val cinema = cinemas.getJSONObject(i)
			val cinemaNameKR = "롯데시네마 " + cinema.getString("CinemaName") + "점"
			val cinemaNameEN = "LotteCinema " + cinema.getString("CinemaNameUS")
			val divisionCode = cinema.get("DivisionCode")
			val detailDivisionCode = cinema.get("DetailDivisionCode")
			val cinemaID = cinema.get("CinemaID")

			val theatermap = HashMap<String, Any>()
			theatermap.put("cinemaNameEN", cinemaNameEN)
			theatermap.put("cinemaNameKR", cinemaNameKR)
			theatermap.put("divisionCode", divisionCode)
			theatermap.put("detailDivisionCode", detailDivisionCode)
			theatermap.put("cinemaID", cinemaID)

			theaterlist.add(theatermap)
		}

		return theaterlist
	}

	fun getLotteCinemaDateList(theatercode: String): ArrayList<String> {
		var datelist = ArrayList<String>()
		val url: String =
			LCurl + "/LCWS/Ticketing/TicketingData.aspx"

		var paramlist = HashMap<String, Any>()
		paramlist.put("MethodName", "GetInvisibleMoviePlayInfo")
		paramlist.put("channelType", "HO")
		paramlist.put("osType", "W")
		paramlist.put("osVersion", userAgent)
		paramlist.put("cinemaList", theatercode)
		paramlist.put("movieCd", "")
		paramlist.put("playDt", "2023-03-03")
		val conn = Jsoup.connect(url)
			.userAgent(userAgent)
			.data("ParamList", JSONObject(paramlist).toString())
		val doc = conn.post().body().text()
		val data = JSONObject(doc).getJSONObject("PlayDates").getJSONArray("Items")

		for (i in 0 until data.length()) {
			val playdate = data.getJSONObject(i).getString("PlayDate").split(" ")

			datelist.add(playdate[0])
		}

		return datelist
	}

	fun getLotteCinemaShowingTime(theaterlist: ArrayList<HashMap<String, Any>>): Unit {
		for (theater in theaterlist) {
			val cinemaCode: String =
				"${theater.get("divisionCode")}|${theater.get("detailDivisionCode")}|${theater.get("cinemaID")}"
			val datelist = getLotteCinemaDateList(cinemaCode)
			for (date in datelist) {
				val url: String =
					LCurl + "/LCWS/Ticketing/TicketingData.aspx"
				var paramlist = HashMap<String, String>()
				paramlist.put("MethodName", "GetPlaySequence")
				paramlist.put("channelType", "HO")
				paramlist.put("osType", "W")
				paramlist.put("osVersion", userAgent)
				paramlist.put("playDate", date)
				paramlist.put("cinemaID", cinemaCode)
				paramlist.put("representationMovieCode", "")
				val conn = Jsoup.connect(url)
					.userAgent(userAgent)
					.data("ParamList", JSONObject(paramlist).toString())
				val doc = conn.post().body().text()
				val data = JSONObject(doc)
				val playSeqs = data.getJSONObject("PlaySeqs").getJSONArray("Items")

				println("${theater.get("cinemaNameKR")} ${theater.get("cinemaNameEN")} ${date}")
				for (i in 0 until playSeqs.length()) {
					val playdata = playSeqs.getJSONObject(i)

					val movieNameKR = playdata.get("MovieNameKR")
					val movieNameUS = playdata.get("MovieNameUS")
					val screenNameKR = playdata.get("ScreenNameKR")
					val screenNameUS = playdata.get("ScreenNameUS")
					val startTime = playdata.get("StartTime")
					val endTime = playdata.get("EndTime")
					val totalSeatCount = playdata.get("TotalSeatCount")
					val playSequence = playdata.get("PlaySequence")
					val bookingSeatCount = playdata.get("BookingSeatCount")

					println("${movieNameKR} ${movieNameUS}\n${screenNameKR} ${screenNameUS}\n${startTime} ${endTime}\n${totalSeatCount} ${bookingSeatCount} ${playSequence}")
				}
			}

		}

	}

	fun getCGVDateList(theatercode: String): ArrayList<String> {
		val url: String =
			CGVurl + "/common/showtimes/iframeTheater.aspx?theatercode=${theatercode}"
		val conn = Jsoup.connect(url)
			.userAgent(userAgent)
			.referrer(
				CGVurl
			)
			.header(
				"Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
			)
			.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
			.header(
				"Cookie",
				"ASP.NET_SessionId=test;"
			)
		val doc = conn.get()

		val days = doc.getElementsByClass("day")
		var datelist = ArrayList<String>()

		for (day in days) {
			val href = day.getElementsByTag("a")[0].attr("href")
			val pattern: Pattern = Pattern.compile(".*date=([^&]+).*")
			val matcher: Matcher = pattern.matcher(href)
			if (matcher.find()) {
				datelist.add(matcher.group(1))
			}
		}

		return datelist
	}

	fun getCGVTheaterData(): ArrayList<HashMap<String, Any>> {
		val url: String =
			CGVurl + "/theaters/"
		val conn = Jsoup.connect(url)
			.userAgent(userAgent)
		val doc = conn.get()
		val scripts = doc.select("script")

		var theatersData: String = ""

		for (script in scripts) {
			if (script.data().contains("var theaterJsonData")) {
				val pattern: Pattern = Pattern.compile(".*var theaterJsonData = ([^;]*);")
				val matcher: Matcher = pattern.matcher(script.data())
				if (matcher.find()) {
					theatersData = matcher.group(1)
					break
				}
			}
		}

		val jsonArray = JSONArray(theatersData)
		val theaterlist = ArrayList<HashMap<String, Any>>()

		for (i in 0 until jsonArray.length()) {
			val theaters = jsonArray.getJSONObject(i)
			val regionName = theaters.getString("RegionName")
			val areaTheaterList = theaters.optJSONArray("AreaTheaterDetailList")

			for (j in 0 until areaTheaterList.length()) {
				val theater = areaTheaterList.getJSONObject(j)
				val theaterCode = theater.getString("TheaterCode")
				val theaterName = theater.getString("TheaterName")

				val theatermap = HashMap<String, Any>()
				theatermap.put("theaterCode", theaterCode)
				theatermap.put("theaterName", theaterName)

				theaterlist.add(theatermap)
			}
		}

		return theaterlist
	}

	fun getCGVShowingTime(theaterlist: ArrayList<Triple<String, String, String>>): Unit {
		for (theater in theaterlist) {
			val datelist = getCGVDateList(theater.second)
			for (date in datelist) {
				println("${theater.first} ${theater.third} ${date}")
				val url: String =
					CGVurl + "/common/showtimes/iframeTheater.aspx?theatercode=${theater.second}&date=${date}"
				val conn = Jsoup.connect(url)
					.userAgent(userAgent)
					.referrer(
						CGVurl
					)
					.header(
						"Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
					)
					.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
					.header(
						"Cookie",
						"ASP.NET_SessionId=test;"
					)
				val doc = conn.get()
				val showtimes = doc.getElementsByClass("col-times")

				for (showtime in showtimes) {
					val infoMovie = showtime.getElementsByClass("info-movie")[0]
					val movieName = infoMovie.getElementsByTag("a")[0].text()
					val age = infoMovie.getElementsByTag("i")[0].text()
					val categories = infoMovie.getElementsByTag("i")[1].text()
					val runningTime = infoMovie.getElementsByTag("i")[2].text()
					val comeOut = infoMovie.getElementsByTag("i")[3].text().replace("[^0-9]+".toRegex(), "")


					val typeHalls = showtime.getElementsByClass("type-hall")
					for (typeHall in typeHalls) {
						val infoHall = typeHall.getElementsByClass("info-hall")[0]
						val dimension = infoHall.getElementsByTag("li")[0].text()
						val whereTheater = infoHall.getElementsByTag("li")[1].text()
						val allSeats = infoHall.getElementsByTag("li")[2].text().replace("[^0-9]+".toRegex(), "")


						val infoTimeTable = typeHall.getElementsByClass("info-timetable")[0]
						val timeinfoes = infoTimeTable.getElementsByTag("li")

						for (timeinfo in timeinfoes) {
							val datas = timeinfo.getElementsByTag("a")
							var starttime = ""
							var href = ""
							var seatsLeft = ""
							if (datas.isEmpty()) {
								starttime = timeinfo.getElementsByTag("em")[0].text().replace(":", "")
								seatsLeft = "마감"
							} else {
								starttime = datas[0].attr("data-playstarttime")
								href = CGVurl + datas[0].attr("href")
								seatsLeft = datas[0].attr("data-seatremaincnt")
							}


							println("${movieName} ${age}세 ${categories} ${runningTime} ${comeOut} ${dimension} ${whereTheater} ${allSeats} ${starttime} ${href} ${seatsLeft}좌석 남음")
						}
					}
				}
			}
		}

	}
}