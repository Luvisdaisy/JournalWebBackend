package dev.tian.journalbackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUser
{
    private String username;
    private String displayName;
    private String avatar;
}

