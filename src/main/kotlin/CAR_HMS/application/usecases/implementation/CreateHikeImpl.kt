package CAR_HMS.application.usecases.implementation

import CAR_HMS.application.dtos.queries.CreateHikeQuery
import CAR_HMS.application.services.TimeService
import CAR_HMS.application.usecases.CreateHike
import CAR_HMS.domain.hikes.HikesFacade
import CAR_HMS.domain.hikes.NewHike
import CAR_HMS.domain.hikes.OrganizerQualification
import CAR_HMS.domain.members.MembersFacade
import CAR_HMS.domain.trails.TrailId
import CAR_HMS.domain.trails.TrailsFacade
import org.springframework.stereotype.Service

@Service
class CreateHikeImpl(
    private val access: AccessControl,
    private val members: MembersFacade,
    private val trails: TrailsFacade,
    private val hikes: HikesFacade,
    private val time: TimeService
) : CreateHike {

    override fun createHike(query: CreateHikeQuery): String {
        access.requireSelf(query.organizerEmail)
        val organizer = members.requireCertifiedOrganizer(query.organizerEmail)
        val certification = organizer.certification!!
        val trailId = TrailId(query.trailId)
        val trail = trails.requireHikeableTrail(trailId, query.startDateTime.toLocalDate())

        return hikes.createHike(
            NewHike(
                organizer.id, query.title, query.description, trailId, query.startDateTime,
                query.expectedDuration, query.registrationDeadline, query.capacity
            ),
            OrganizerQualification(
                certification.period.issueDate, certification.period.expiryDate, certification.organizerLevel
            ),
            trail.difficulty,
            query.organizerEmail,
            time.now()
        ).value
    }
}
