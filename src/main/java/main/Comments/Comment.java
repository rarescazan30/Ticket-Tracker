package main.Comments;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

// we tell Jackson how the output should be
@JsonPropertyOrder({
        "author", "content", "createdAt"
})
public class Comment {
    private String author;
    private String content;
    private String createdAt;

    public Comment() {
    }

    public Comment(String content, String author, String createdAt) {
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}