package CAR_HMS.contracts.support

import CAR_HMS.domain.hikes.HikeStatus
import CAR_HMS.domain.hikes.SignUpStatus
import CAR_HMS.domain.members.MembershipStatus
import io.cucumber.datatable.DataTable

fun hikeStatus(label: String): HikeStatus = HikeStatus.entries.first { it.label == label }

fun signUpStatus(label: String): SignUpStatus = SignUpStatus.entries.first { it.label == label }

fun membershipStatus(label: String): MembershipStatus = MembershipStatus.entries.first { it.label == label }

/** Lit un tableau à deux colonnes ; une cellule vide donne une valeur absente. */
fun DataTable.asFields(): Map<String, String?> = cells().associate { it[0] to it[1] }
