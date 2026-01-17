package main.Comments;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a comment left on a ticket
 */
// we tell Jackson how the output should be
@JsonPropertyOrder({
        "author", "content", "createdAt"
})
public final class Comment {
    private String author;
    private String content;
    private String createdAt;

    /**
     * Default constructor for Jackson deserialization
     */
    public Comment() {
    }

    /**
     * Creates a new comment with content, author and creation date
     */
    public Comment(final String content, final String author, final String createdAt) {
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    /**
     * Returns the author of the comment
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the content of the comment
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns the date when the comment was created
     */
    public String getCreatedAt() {
        return createdAt;
    }
}
