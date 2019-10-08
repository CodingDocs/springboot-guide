package github.snailclimb.jpademo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserDTO {
    private String name;
    private int age;
    private String companyName;
    private String schoolName;
}
