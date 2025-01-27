package dev.tian.journalbackend.models;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "UserRelationship")
public class UserRelationship
{
    @Id
    private String id;

    private String username;

    private List<SimpleUser> following;

    private List<SimpleUser> followers;

    private List<SimpleUser> blocked;

    private List<SimpleUser> friends;
}
