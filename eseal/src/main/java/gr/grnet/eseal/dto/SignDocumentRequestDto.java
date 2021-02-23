package gr.grnet.eseal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.grnet.eseal.utils.NonNullRequestFieldsCheckGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * SignDocumentRequestDto represents an incoming signing request.
 */
@Setter
@NoArgsConstructor
public class SignDocumentRequestDto {

    @NotNull(groups = NonNullRequestFieldsCheckGroup.class, message = "Field username cannot be empty")
    private String username;

    @NotNull(groups = NonNullRequestFieldsCheckGroup.class, message = "Field password cannot be empty")
    private String password;

    @NotNull(groups = NonNullRequestFieldsCheckGroup.class, message = "Field key cannot be empty")
    private String key;

    @NotNull(groups = NonNullRequestFieldsCheckGroup.class, message = "Field toSignDocument cannot be empty")
    @JsonProperty("toSignDocument")
    @Valid
    private ToSignDocument toSignDocument;

    public SignDocumentRequestDto(String username, String password, String key, ToSignDocument toSignDocument) {
        this.username = username;
        this.password = password;
        this.key = key;
        this.toSignDocument = toSignDocument;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getKey() {
        return key;
    }

    public ToSignDocument getToSignDocument() {
        return toSignDocument;
    }

    public String getToSignDocumentB64String() {
        return this.toSignDocument.bytes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
     class ToSignDocument{

        @NotNull(groups = NonNullRequestFieldsCheckGroup.class, message = "Field toSignDocument.bytes cannot be empty")
        String bytes;

        @NotNull(groups = NonNullRequestFieldsCheckGroup.class, message = "Field toSignDocument.name cannot be empty")
        String name;
    }
}
