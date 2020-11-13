package TO;

import lombok.Data;

@Data
public class MemberTo {
    private String id;
    private String name;
    private String userName;
    private String phone;
    private String level;
    private String password;
    private String access_token;
    private String expires_in;
    private String uid;
    private Integer gender;
    private Integer points;
}
