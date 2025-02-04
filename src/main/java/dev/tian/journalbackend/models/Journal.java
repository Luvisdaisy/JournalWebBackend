package dev.tian.journalbackend.models;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Journal")
public class Journal
{
    @Id
    private String id;

    @NotBlank
    @Field(name = "title")
    private String title;

    @NotBlank
    @Field(name = "content")
    private String content;

    @Field(name = "username")
    private String username;

    @Field(name="userAvatar")
    private String userAvatar;

    @Field(name = "likes")
    private List<String> likes;

    @Field(name = "comments")
    private List<Comment> comments;

    @CreatedDate
    @Field(name = "created_datetime")
    private LocalDateTime createdDatetime;

    @LastModifiedDate
    @Field(name = "updated_datetime")
    private LocalDateTime updatedDatetime;

    @Field(name = "is_deleted")
    private Boolean isDeleted;
}
