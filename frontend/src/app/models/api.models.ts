export interface CsrfToken {
  headerName: string;
  parameterName: string;
  token: string;
}

export interface AuthenticatedUser {
  email: string;
  roles: string[];
  organizer: boolean;
}

export interface HikeSummary {
  hikeId: string;
  title: string;
  trailName: string;
  difficulty: string;
  startDateTime: string;
  status: string;
  remainingSpots: number;
  organizerName: string;
}

export interface RegisterHikerRequest {
  invitationCode: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  streetAddress: string;
  city: string;
  province: string;
  postalCode: string;
  dateOfBirth: string;
  emergencyContactName: string;
  emergencyContactPhone: string;
  skillLevel: string;
  waiverAccepted: boolean;
  guardianName: string | null;
}

export interface HikerProfile {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  streetAddress: string;
  city: string;
  province: string;
  postalCode: string;
  dateOfBirth: string;
  emergencyContactName: string;
  emergencyContactPhone: string;
  skillLevel: string;
  membershipStatus: string;
  organizer: boolean;
  certificateNumber: string | null;
  certificateIssueDate: string | null;
  certificateExpiryDate: string | null;
  organizerLevel: string | null;
}

export interface UpdateHikerRequest {
  hikerEmail: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  streetAddress: string;
  city: string;
  province: string;
  postalCode: string;
  emergencyContactName: string;
  emergencyContactPhone: string;
  skillLevel: string;
}

export interface CreateHikeRequest {
  organizerEmail: string;
  title: string;
  description: string;
  trailId: string;
  startDateTime: string;
  expectedDuration: string;
  registrationDeadline: string;
  capacity: number;
}

export interface Trail {
  id: string;
  name: string;
  difficulty: string;
  trailhead: string;
  openingSeasonStart: string;
  openingSeasonEnd: string;
}

export interface UpdateMembershipRequest {
  adminEmail: string;
  hikerEmail: string;
  newStatus: string;
}

export interface RecordCertificationRequest {
  adminEmail: string;
  hikerEmail: string;
  certificateNumber: string;
  issueDate: string;
  expiryDate: string;
  organizerLevel: string;
}

export interface ApiError {
  message: string;
}