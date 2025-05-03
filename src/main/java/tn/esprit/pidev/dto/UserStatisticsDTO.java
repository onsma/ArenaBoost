package tn.esprit.pidev.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDTO {
    private long totalUsers;
    private long activeUsers;
    private long newUsers;
    private long premiumUsers;
}
