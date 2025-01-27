package dev.tian.journalbackend.models;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "User")
public class User
{
    @Id
    private String id;

    @NotBlank
    @Field(name = "username")
    private String username;

    @Email
    @Field(name = "email")
    private String email;

    @NotBlank
    @Field(name = "password")
    private String password;

    @Field(name = "avatar")
    private String avatar;

    @Field(name = "display_name")
    private String displayName;

    @Field(name = "gender")
    private String gender;

    @CreatedDate
    @Field(name = "created_datetime")
    private LocalDateTime createdDatetime;

    @LastModifiedDate
    @Field(name = "updated_datetime")
    private LocalDateTime updatedDatetime;

    @Field(name = "is_activated")
    private Boolean isActivated;

    @Field(name = "is_deleted")
    private Boolean isDeleted;

}