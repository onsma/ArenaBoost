export interface User {
  id?: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  profileImageUrl?: string;
}

export enum UserRole {
  ADMIN = 'ADMIN',
  INVESTOR = 'INVESTOR',
  ATHLETE = 'ATHLETE',
  CLUB_MANAGER = 'CLUB_MANAGER'
}
