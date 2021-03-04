package gr.grnet.eseal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.grnet.eseal.utils.NotEmptyValidateDocumentRequestFieldsCheckGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * ValidateDocumentRequestDto represents an incoming document validation request
 */
@Setter
@Getter
@NoArgsConstructor
public class ValidateDocumentRequestDto {

    @NotNull(groups = NotEmptyValidateDocumentRequestFieldsCheckGroup.class, message = "Field signedDocument cannot be empty")
    @JsonProperty("signedDocument")
    @Valid
    private SignedDocument signedDocument;

    @Getter
    @Setter
    @NoArgsConstructor
    public class SignedDocument {

        @NotEmpty(groups = NotEmptyValidateDocumentRequestFieldsCheckGroup.class, message = "Field signedDocument.bytes cannot be empty")
        String bytes;

        @NotEmpty(groups = NotEmptyValidateDocumentRequestFieldsCheckGroup.class, message = "Field signedDocument.name cannot be empty")
        String name;
    }
}