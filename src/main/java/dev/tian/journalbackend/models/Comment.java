package dev.tian.journalbackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment
{

    @Id
    private String id;

    private SimpleUser simpleUser;

    private String content;

    private List<Comment> replies;

    @CreatedDate
    private LocalDateTime createdDatetime;

    public Comment(SimpleUser simpleUser, String content)
    {
        this.id = new ObjectId().toString();
        this.simpleUser = simpleUser;
        this.content = content;
        this.replies = new ArrayList<>();
        this.createdDatetime = LocalDateTime.now();
    }
}
