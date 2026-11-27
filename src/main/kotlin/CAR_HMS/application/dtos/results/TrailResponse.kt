package CAR_HMS.application.dtos.results

data class TrailResponse(

    val id: String,
    val name: String,
    val difficulty: String,
    val trailhead: String,

    val openingSeasonStart: String,
    val openingSeasonEnd: String
)