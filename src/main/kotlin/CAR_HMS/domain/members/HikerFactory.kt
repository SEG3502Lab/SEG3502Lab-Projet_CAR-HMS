package CAR_HMS.domain.members

interface HikerFactory {
    fun createHiker(registration: HikerRegistration): Hiker
}
